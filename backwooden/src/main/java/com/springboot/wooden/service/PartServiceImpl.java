package com.springboot.wooden.service;

import com.springboot.wooden.domain.Buyer;
import com.springboot.wooden.domain.Part;
import com.springboot.wooden.domain.PartStock;
import com.springboot.wooden.dto.PartRequestDto;
import com.springboot.wooden.dto.PartResponseDto;
import com.springboot.wooden.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartServiceImpl implements PartService {

    private final PartRepository partRepository;
    private final BuyerRepository buyerRepository;
    private final PartStockRepository partStockRepository;
    private final PartOrderRepository partOrderRepository;
    private final BOMRepository bomRepository;


    // 목록
    @Override
    @Transactional(readOnly = true)
    public List<PartResponseDto> getAll() {
        return partRepository.findAll()
                .stream()
                .map(p -> PartResponseDto.builder()
                        .partNo(p.getPartNo())
                        .partCode(p.getPartCode())
                        .partName(p.getPartName())
                        .partSpec(p.getPartSpec())
                        .partPrice(p.getPartPrice())
                        .buyerComp(p.getBuyer() != null ? p.getBuyer().getBuyerComp() : null)
                        .build())
                .toList();
    }

    // 구매처 기준 단건(1:1)
    @Override
    @Transactional(readOnly = true)
    public PartResponseDto getByBuyerNo(Long buyerNo) {
        Part p = partRepository.findByBuyer_BuyerNo(buyerNo)
                .orElseThrow(() -> new EntityNotFoundException("Part not found for buyerNo: " + buyerNo));
        return PartResponseDto.builder()
                .partNo(p.getPartNo())
                .partCode(p.getPartCode())
                .partName(p.getPartName())
                .partSpec(p.getPartSpec())
                .partPrice(p.getPartPrice())
                .buyerComp(p.getBuyer() != null ? p.getBuyer().getBuyerComp() : null)
                .build();
    }

    // 등록
    @Override
    @Transactional
    public PartResponseDto save(PartRequestDto dto) {
        Buyer buyer = buyerRepository.findById(dto.getBuyerNo())
                .orElseThrow(() -> new EntityNotFoundException("Buyer not found: " + dto.getBuyerNo()));

        // 1:1 보장 (이미 해당 buyerNo를 쓰는 Part가 있으면 불가)
        if (partRepository.existsByBuyer_BuyerNo(dto.getBuyerNo())) {
            throw new IllegalStateException("이미 이 구매처의 부품이 존재합니다. buyerNo=" + dto.getBuyerNo());
        }

        Part saved = partRepository.save(Part.builder()
                .buyer(buyer)
                .partCode(dto.getPartCode())
                .partName(dto.getPartName())
                .partSpec(dto.getPartSpec())
                .partPrice(dto.getPartPrice())
                .build());

        partStockRepository.save(PartStock.builder()
                .part(saved)
                .psQty(0)
                .build());

        return PartResponseDto.builder()
                .partNo(saved.getPartNo())
                .partCode(saved.getPartCode())
                .partName(saved.getPartName())
                .partSpec(saved.getPartSpec())
                .partPrice(saved.getPartPrice())
                .buyerComp(saved.getBuyer() != null ? saved.getBuyer().getBuyerComp() : null)
                .build();
    }

    // 수정
    @Override
    @Transactional
    public PartResponseDto update(Long partNo, PartRequestDto dto) {
        Part part = partRepository.findById(partNo)
                .orElseThrow(() -> new EntityNotFoundException("Part not found: " + partNo));

        // 구매처 변경 요청시 1:1 제약 확인
        if (dto.getBuyerNo() != null) {
            Buyer buyer = buyerRepository.findById(dto.getBuyerNo())
                    .orElseThrow(() -> new EntityNotFoundException("Buyer not found: " + dto.getBuyerNo()));
            partRepository.findByBuyer_BuyerNo(dto.getBuyerNo()).ifPresent(existing -> {
                if (!existing.getPartNo().equals(partNo)) {
                    throw new IllegalStateException("이미 해당 구매처에 연결된 부품이 존재합니다. buyerNo=" + dto.getBuyerNo());
                }
            });
            part.changeBuyer(buyer);
        }

        part.changePartCode(dto.getPartCode());
        part.changePartName(dto.getPartName());
        part.changePartSpec(dto.getPartSpec());
        part.changePartPrice(dto.getPartPrice());

        return PartResponseDto.builder()
                .partNo(part.getPartNo())
                .partCode(part.getPartCode())
                .partName(part.getPartName())
                .partSpec(part.getPartSpec())
                .partPrice(part.getPartPrice())
                .buyerComp(part.getBuyer() != null ? part.getBuyer().getBuyerComp() : null)
                .build();
    }

    @Override
    @Transactional
    public void delete(Long partNo) {
        // 1) 미완료 발주가 남아 있으면 금지
        boolean hasPending = partOrderRepository
                .existsByPart_PartNoAndPoStateNot(partNo, "입고완료");
        if (hasPending) {
            throw new IllegalStateException("이 부품의 '입고대기/진행' 발주가 있어 삭제할 수 없습니다.");
        }

        // 2) 발주에서 part FK 끊기
        partOrderRepository.detachPartFromOrders(partNo);

        // 3) BOM에서 이 부품을 참조하는 행 삭제
        bomRepository.deleteByPartNo(partNo);

        // 4) 재고행 삭제 (공유 PK = partNo)
        partStockRepository.deleteById(partNo);

        // 5) 부품 삭제
        partRepository.deleteById(partNo);
    }
}
