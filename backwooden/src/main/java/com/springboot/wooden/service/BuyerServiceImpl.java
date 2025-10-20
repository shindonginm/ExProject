package com.springboot.wooden.service;

import com.springboot.wooden.domain.Buyer;
import com.springboot.wooden.dto.BuyerRequestDto;
import com.springboot.wooden.dto.BuyerResponseDto;
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
public class BuyerServiceImpl implements BuyerService {

    private final BuyerRepository buyerRepository;
    private final PartRepository partRepository;
    private final PartOrderRepository partOrderRepository;

    private BuyerResponseDto toDto(Buyer b) {
        return BuyerResponseDto.builder()
                .buyerNo(b.getBuyerNo())
                .buyerComp(b.getBuyerComp())
                .buyerName(b.getBuyerName())
                .buyerEmail(b.getBuyerEmail())
                .buyerPhone(b.getBuyerPhone())
                .buyerAddr(b.getBuyerAddr())
                .build();
    }

    // ===== 생성 =====
    @Override
    @Transactional
    public BuyerResponseDto save(BuyerRequestDto dto) {
        Buyer saved = buyerRepository.save(Buyer.builder()
                .buyerComp(dto.getBuyerComp())
                .buyerName(dto.getBuyerName())
                .buyerEmail(dto.getBuyerEmail())
                .buyerPhone(dto.getBuyerPhone())
                .buyerAddr(dto.getBuyerAddr())
                .build());
        return toDto(saved);
    }

    // ===== 목록 =====
    @Override
    public List<BuyerResponseDto> findAll() {
        return buyerRepository.findAll().stream().map(this::toDto).toList();
    }

    // ===== 수정 =====
    @Override
    @Transactional
    public BuyerResponseDto update(Long id, BuyerRequestDto dto) {
        Buyer b = buyerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("구매거래처를 찾을 수 없습니다: " + id));

        b.changeBuyerComp(dto.getBuyerComp());
        b.changeBuyerName(dto.getBuyerName());
        b.changeBuyerEmail(dto.getBuyerEmail());
        b.changeBuyerPhone(dto.getBuyerPhone());
        b.changeBuyerAddr(dto.getBuyerAddr());

        return toDto(b);
    }

    // 삭제
    @Override
    @Transactional
    public void delete(Long id) {
        // 1) 미완료 발주 있으면 막기 (입고완료가 아닌 게 하나라도 있으면 true)
        boolean hasPendingOrders =
                partOrderRepository.existsByBuyer_BuyerNoAndPoStateNot(id, "입고완료");
        if (hasPendingOrders) {
            throw new IllegalStateException("이 거래처의 미완료 발주가 있어 삭제할 수 없습니다.");
        }

        // 2) 참조 끊기: 발주 → buyer null
        partOrderRepository.detachBuyerFromOrders(id);

        // 3) 참조 끊기: 부품 → buyer null
        partRepository.detachBuyerFromParts(id);

        // 4) 실제 삭제
        buyerRepository.deleteById(id);
    }
}
