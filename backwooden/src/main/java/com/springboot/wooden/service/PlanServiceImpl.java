package com.springboot.wooden.service;

import com.springboot.wooden.domain.BOM;
import com.springboot.wooden.domain.Item;
import com.springboot.wooden.domain.ItemStock;
import com.springboot.wooden.domain.PartStock;
import com.springboot.wooden.domain.Plan;
import com.springboot.wooden.dto.PlanRequestDTO;
import com.springboot.wooden.dto.PlanResponseDTO;
import com.springboot.wooden.repository.BOMRepository;
import com.springboot.wooden.repository.ItemRepository;
import com.springboot.wooden.repository.ItemStockRepository;
import com.springboot.wooden.repository.PartStockRepository;
import com.springboot.wooden.repository.PlanRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
    private final ItemRepository itemRepository;

    private final BOMRepository bomRepository;
    private final PartStockRepository partStockRepository;
    private final ItemStockRepository itemStockRepository;

    // ===== 공통 유효성 =====
    private void validateDatesAndQty(PlanRequestDTO dto) {
        LocalDate s = dto.getPlanStartDate();
        LocalDate e = dto.getPlanEndDate();
        if (s == null || e == null) {
            throw new IllegalArgumentException("생산시작일/종료일은 필수입니다.");
        }
        if (s.isAfter(e)) {
            throw new IllegalArgumentException("생산종료일은 생산시작일보다 빠를 수 없습니다.");
        }
        if (dto.getPlanQty() <= 0) {
            throw new IllegalArgumentException("생산수량은 1 이상이어야 합니다.");
        }
    }

    private void ensurePartsEnoughOrThrow(Item item, int qty) {
        var bomList = bomRepository.findAllByItem_ItemNo(item.getItemNo());
        if (bomList.isEmpty()) throw new IllegalStateException("BOM이 없어 생산 불가: " + item.getItemName());

        for (BOM b : bomList) {
            int need = b.getQtyPerItem() * qty;
            PartStock ps = partStockRepository.findById(b.getPart().getPartNo())
                    .orElseThrow(() -> new IllegalStateException("부품 재고 없음: " + b.getPart().getPartName()));
            if (ps.getPsQty() < need) {
                throw new IllegalStateException(
                        "원자재 부족: %s 필요 %d, 보유 %d".formatted(b.getPart().getPartName(), need, ps.getPsQty()));
            }
        }
    }

    private void consumePartsByBOM(Item item, int qty) {
        for (BOM b : bomRepository.findAllByItem_ItemNo(item.getItemNo())) {
            int need = b.getQtyPerItem() * qty;
            PartStock ps = partStockRepository.findById(b.getPart().getPartNo())
                    .orElseThrow(() -> new IllegalStateException("부품 재고 없음: " + b.getPart().getPartName()));
            ps.changeQty(-need);
        }
    }

    private void releasePartsByBOM(Item item, int qty) {
        for (BOM b : bomRepository.findAllByItem_ItemNo(item.getItemNo())) {
            int giveback = b.getQtyPerItem() * qty;
            PartStock ps = partStockRepository.findById(b.getPart().getPartNo())
                    .orElseThrow(() -> new IllegalStateException("부품 재고 없음: " + b.getPart().getPartName()));
            ps.changeQty(+giveback);
        }
    }

    private void increaseItemStock(Item item, int qty) {
        ItemStock is = itemStockRepository.findById(item.getItemNo())
                .orElseGet(() -> itemStockRepository.save(
                        ItemStock.builder()
                                .item(item)
                                .isQty(0)
                                .totalIn(0)
                                .totalOut(0)
                                .build()
                ));
        is.produce(qty);
    }

    // ===== 서비스 메서드 =====

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponseDTO> getAll() {
        return planRepository.findByPlanStateNot("생산완료").stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponseDTO> getCompletedList() {
        return planRepository.findByPlanState("생산완료").stream().map(this::toDto).toList();
    }

    // 등록: 생산중만 허용 + 등록 시 원자재 즉시 차감 + 날짜/수량 검증
    @Override
    @Transactional
    public PlanResponseDTO save(PlanRequestDTO dto) {
        // 서비스 레벨 검증
        validateDatesAndQty(dto);

        if (!"생산중".equals(dto.getPlanState())) {
            throw new IllegalArgumentException("등록은 '생산중' 상태로만 가능합니다.");
        }

        var item = itemRepository.findById(dto.getItemNo())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("상품 없음: " + dto.getItemNo()));

        ensurePartsEnoughOrThrow(item, dto.getPlanQty());
        consumePartsByBOM(item, dto.getPlanQty());

        var saved = planRepository.save(Plan.builder()
                .item(item)
                .planQty(dto.getPlanQty())
                .planState("생산중")
                .planStartDate(dto.getPlanStartDate())
                .planEndDate(dto.getPlanEndDate())
                .build());

        return toDto(saved);
    }

    /** 수정: 날짜/수량 검증 + 진행중 변경 보정 + 완료 시 완제품 증가 */
    @Override
    @Transactional
    public PlanResponseDTO update(Long planNo, PlanRequestDTO dto) {
        // ✅ 서비스 레벨 검증
        validateDatesAndQty(dto);

        var p = planRepository.findById(planNo)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("생산계획 없음: " + planNo));

        var newItem = itemRepository.findById(dto.getItemNo())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("상품 없음: " + dto.getItemNo()));

        boolean wasDone = "생산완료".equals(p.getPlanState());
        boolean willBeDone = "생산완료".equals(dto.getPlanState());

        if (wasDone && !willBeDone) {
            throw new IllegalStateException("완료된 계획은 되돌릴 수 없습니다.");
        }

        if (!wasDone) {
            var oldItem = p.getItem();
            int oldQty = p.getPlanQty();
            int newQty = dto.getPlanQty();

            boolean itemChanged = !oldItem.getItemNo().equals(newItem.getItemNo());
            if (itemChanged) {
                releasePartsByBOM(oldItem, oldQty);
                ensurePartsEnoughOrThrow(newItem, newQty);
                consumePartsByBOM(newItem, newQty);
            } else {
                int delta = newQty - oldQty;
                if (delta > 0) {
                    ensurePartsEnoughOrThrow(newItem, delta);
                    consumePartsByBOM(newItem, delta);
                } else if (delta < 0) {
                    releasePartsByBOM(newItem, -delta);
                }
            }
        }

        p.changeItem(newItem);
        p.changePlanQty(dto.getPlanQty());
        p.changePlanStartDate(dto.getPlanStartDate());
        p.changePlanEndDate(dto.getPlanEndDate());
        p.changePlanState(dto.getPlanState());

        if (!wasDone && willBeDone) {
            increaseItemStock(newItem, dto.getPlanQty());
        }

        return toDto(p);
    }

    @Override
    @Transactional
    public void patchStatus(Long planNo, String next) {
        Plan p = planRepository.findById(planNo)
                .orElseThrow(() -> new EntityNotFoundException("생산계획 없음 : " + planNo));

        String before = p.getPlanState();
        if(!"생산완료".equals(before) && "생산완료".equals(next)) {
            // 최초 완료 전환될 때만 완제품 입고
            Item item = p.getItem();
            ItemStock itemStock = itemStockRepository.findById(item.getItemNo())
                    .orElseGet(() -> itemStockRepository.save(
                            ItemStock.builder()
                                    .item(item)
                                    .isQty(0)
                                    .totalIn(0)
                                    .totalOut(0)
                                    .build()
                    ));
            itemStock.produce(p.getPlanQty());
        }
        p.changePlanState(next);
    }

    @Override
    @Transactional
    public void delete(Long planNo) {
        var p = planRepository.findById(planNo)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("생산계획 없음: " + planNo));

        if (!"생산완료".equals(p.getPlanState())) {
            releasePartsByBOM(p.getItem(), p.getPlanQty());
        }
        planRepository.delete(p);
    }

    private PlanResponseDTO toDto(Plan p) {
        return PlanResponseDTO.builder()
                .planNo(p.getPlanNo())
                .itemName(p.getItem().getItemName())
                .planState(p.getPlanState())
                .planStartDate(p.getPlanStartDate())
                .planEndDate(p.getPlanEndDate())
                .planQty(p.getPlanQty())
                .build();
    }
}
