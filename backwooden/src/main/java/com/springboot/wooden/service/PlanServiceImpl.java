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

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
    private final ItemRepository itemRepository;

    // 재고 반영용
    private final BOMRepository bomRepository;
    private final PartStockRepository partStockRepository;
    private final ItemStockRepository itemStockRepository;

    /** 해당 Item을 qty만큼 생산하려고 할 때 원자재 충분성 검사 */
    private void ensurePartsEnoughOrThrow(Item item, int planQty) {
        var bomList = bomRepository.findAllByItem_ItemNo(item.getItemNo());
        if (bomList.isEmpty()) {
            throw new IllegalStateException("BOM이 없어 생산 불가: item=" + item.getItemName());
        }
        for (BOM b : bomList) {
            int need = b.getQtyPerItem() * planQty;
            PartStock ps = partStockRepository.findById(b.getPart().getPartNo())
                    .orElseThrow(() -> new IllegalStateException("부품 재고 없음: " + b.getPart().getPartName()));
            if (ps.getPsQty() < need) {
                throw new IllegalStateException(
                        "원자재 부족: %s 필요 %d, 보유 %d".formatted(b.getPart().getPartName(), need, ps.getPsQty())
                );
            }
        }
    }

    /** BOM 기준으로 부품 재고 차감 */
    private void consumePartsByBOM(Item item, int produceQty) {
        for (BOM b : bomRepository.findAllByItem_ItemNo(item.getItemNo())) {
            int need = b.getQtyPerItem() * produceQty;
            PartStock ps = partStockRepository.findById(b.getPart().getPartNo())
                    .orElseThrow(() -> new IllegalStateException("부품 재고 없음: " + b.getPart().getPartName()));
            ps.changeQty(-need); // 감소
        }
    }

    /** 완제품 재고 증가(없으면 0으로 생성 후 증가) */
    private void increaseItemStock(Item item, int qty) {
        ItemStock is = itemStockRepository.findById(item.getItemNo())
                .orElseGet(() -> itemStockRepository.save(
                        ItemStock.builder().item(item).isQty(0).build()
                ));
        is.changeQty(qty);
    }

    // =================== 서비스 메서드 ===================

    /** 진행중 목록(생산완료 제외) */
    @Override
    @Transactional(readOnly = true)
    public List<PlanResponseDTO> getAll() {
        return planRepository.findByPlanStateNot("생산완료")
                .stream()
                .map(this::toDto)
                .toList();
    }

    /** 생산완료 목록 */
    @Override
    @Transactional(readOnly = true)
    public List<PlanResponseDTO> getCompletedList() {
        return planRepository.findByPlanState("생산완료")
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PlanResponseDTO getOne(Long planNo) {
        Plan p = planRepository.findById(planNo)
                .orElseThrow(() -> new EntityNotFoundException("생산계획 없음: " + planNo));
        return toDto(p);
    }

    /** 등록: ‘생산완료’로 등록 금지(진행중만), 등록 시에도 생산 가능성 체크 */
    @Override
    @Transactional
    public PlanResponseDTO save(PlanRequestDTO dto) {
        Item item = itemRepository.findById(dto.getItemNo())
                .orElseThrow(() -> new EntityNotFoundException("상품 없음: " + dto.getItemNo()));

        if ("생산완료".equals(dto.getPlanState())) {
            throw new IllegalArgumentException("상태는 진행중으로만 등록 가능합니다");
        }

        ensurePartsEnoughOrThrow(item, dto.getPlanQty());

        Plan saved = planRepository.save(Plan.builder()
                .item(item)
                .planQty(dto.getPlanQty())
                .planState(dto.getPlanState()) // 보통 "진행중"
                .planStartDate(dto.getPlanStartDate())
                .planEndDate(dto.getPlanEndDate())
                .build());

        return toDto(saved);
    }

    /** 수정: 진행중 → 생산완료 변경할 때 재고 반영 */
    @Override
    @Transactional
    public PlanResponseDTO update(Long planNo, PlanRequestDTO dto) {
        Plan p = planRepository.findById(planNo)
                .orElseThrow(() -> new EntityNotFoundException("생산계획 없음: " + planNo));

        Item item = itemRepository.findById(dto.getItemNo())
                .orElseThrow(() -> new EntityNotFoundException("상품 없음: " + dto.getItemNo()));

        boolean wasDone = "생산완료".equals(p.getPlanState());
        boolean willBeDone = "생산완료".equals(dto.getPlanState());

        // 값 반영
        p.changeItem(item);
        p.changePlanQty(dto.getPlanQty());
        p.changePlanStartDate(dto.getPlanStartDate());
        p.changePlanEndDate(dto.getPlanEndDate());
        p.changePlanState(dto.getPlanState());

        if (!wasDone && willBeDone) {
            ensurePartsEnoughOrThrow(item, dto.getPlanQty());
            consumePartsByBOM(item, dto.getPlanQty());
            increaseItemStock(item, dto.getPlanQty());
        }

        return toDto(p);
    }

    @Override
    @Transactional
    public void delete(Long planNo) {
        planRepository.deleteById(planNo);
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
