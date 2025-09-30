package com.springboot.wooden.service;

import com.springboot.wooden.domain.Buyer;
import com.springboot.wooden.domain.Part;
import com.springboot.wooden.domain.PartOrder;
import com.springboot.wooden.dto.PartOrderRequestDto;
import com.springboot.wooden.dto.PartOrderResponseDto;
import com.springboot.wooden.repository.BuyerRepository;
import com.springboot.wooden.repository.PartOrderRepository;
import com.springboot.wooden.repository.PartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartOrderServiceImpl implements PartOrderService {

    private final PartOrderRepository partOrderRepository;
    private final BuyerRepository buyerRepository;
    private final PartRepository partRepository;

    // 전체 조회
    @Override
    public List<PartOrderResponseDto> getAll() {
        return partOrderRepository.findAll()
                .stream()
                .map(po -> PartOrderResponseDto.builder()
                        .poNo(po.getPoNo())
                        .buyerComp(po.getBuyer().getBuyerComp())
                        .partName(po.getPart().getPartName())
                        .poQty(po.getPoQty())
                        .poPrice(po.getPoPrice())
                        .poState(po.getPoState())
                        .poDate(po.getPoDate())
                        .buyerAddr(po.getBuyerAddr())
                        .build())
                .toList();
    }

    // 단건 조회
    @Override
    public PartOrderResponseDto getOne(Long poNo) {
        PartOrder po = partOrderRepository.findById(poNo)
                .orElseThrow(() -> new IllegalArgumentException("발주 없음: " + poNo));

        return PartOrderResponseDto.builder()
                .poNo(po.getPoNo())
                .buyerComp(po.getBuyer().getBuyerComp())
                .partName(po.getPart().getPartName())
                .poQty(po.getPoQty())
                .poPrice(po.getPoPrice())
                .poState(po.getPoState())
                .poDate(po.getPoDate())
                .buyerAddr(po.getBuyerAddr())
                .build();
    }

    // 등록
    @Override
    @Transactional
    public PartOrderResponseDto addPartOrder(PartOrderRequestDto dto) {
        Buyer buyer = buyerRepository.findById(dto.getBuyerNo())
                .orElseThrow(() -> new IllegalArgumentException("거래처 없음: " + dto.getBuyerNo()));
        Part part = partRepository.findById(dto.getPartNo())
                .orElseThrow(() -> new IllegalArgumentException("부품 없음: " + dto.getPartNo()));

        PartOrder saved = partOrderRepository.save(PartOrder.builder()
                .buyer(buyer)                  // ★ save 전에 연관 세팅
                .part(part)
                .poQty(dto.getPoQty())
                .poPrice(dto.getPoPrice())
                .poState(dto.getPoState())
                .poDate(dto.getPoDate())
                .buyerAddr(dto.getBuyerAddr())
                .build());

        return PartOrderResponseDto.builder()
                .poNo(saved.getPoNo())
                .buyerComp(saved.getBuyer().getBuyerComp())
                .partName(saved.getPart().getPartName())
                .poQty(saved.getPoQty())
                .poPrice(saved.getPoPrice())
                .poState(saved.getPoState())
                .poDate(saved.getPoDate())
                .buyerAddr(saved.getBuyerAddr())
                .build();
    }

    // 수정
    @Override
    @Transactional
    public PartOrderResponseDto updatePartOrder(Long poNo, PartOrderRequestDto dto) {
        PartOrder po = partOrderRepository.findById(poNo)
                .orElseThrow(() -> new IllegalArgumentException("발주 없음: " + poNo));

        Buyer buyer = buyerRepository.findById(dto.getBuyerNo())
                .orElseThrow(() -> new IllegalArgumentException("거래처 없음: " + dto.getBuyerNo()));
        Part part = partRepository.findById(dto.getPartNo())
                .orElseThrow(() -> new IllegalArgumentException("부품 없음: " + dto.getPartNo()));

        po.changeBuyer(buyer);
        po.changePart(part);
        po.changePoQty(dto.getPoQty());
        po.changePoPrice(dto.getPoPrice());
        po.changePoState(dto.getPoState());
        po.changePoDate(dto.getPoDate());
        po.changeBuyerAddr(dto.getBuyerAddr());

        return PartOrderResponseDto.builder()
                .poNo(po.getPoNo())
                .buyerComp(po.getBuyer().getBuyerComp())
                .partName(po.getPart().getPartName())
                .poQty(po.getPoQty())
                .poPrice(po.getPoPrice())
                .poState(po.getPoState())
                .poDate(po.getPoDate())
                .buyerAddr(po.getBuyerAddr())
                .build();
    }

    // 삭제
    @Override
    @Transactional
    public void deletePartOrder(Long poNo) {
        partOrderRepository.deleteById(poNo);
    }
}
