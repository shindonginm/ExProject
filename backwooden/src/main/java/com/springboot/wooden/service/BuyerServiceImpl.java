package com.springboot.wooden.service;

import com.springboot.wooden.domain.Buyer;
import com.springboot.wooden.dto.BuyerRequestDto;
import com.springboot.wooden.dto.BuyerResponseDto;
import com.springboot.wooden.repository.BuyerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BuyerServiceImpl implements BuyerService {

    private final BuyerRepository repo;

    @Override
    @Transactional
    public BuyerResponseDto save(BuyerRequestDto dto) {
        Buyer saved = repo.save(Buyer.builder()
                .buyerComp(dto.getBuyerComp())
                .buyerName(dto.getBuyerName())
                .buyerEmail(dto.getBuyerEmail())
                .buyerPhone(dto.getBuyerPhone())
                .buyerAddr(dto.getBuyerAddr())
                .build());

        return BuyerResponseDto.builder()
                .buyerNo(saved.getBuyerNo())
                .buyerComp(saved.getBuyerComp())
                .buyerName(saved.getBuyerName())
                .buyerEmail(saved.getBuyerEmail())
                .buyerPhone(saved.getBuyerPhone())
                .buyerAddr(saved.getBuyerAddr())
                .build();
    }

    @Override
    public List<BuyerResponseDto> findAll() {
        return repo.findAll().stream()
                .map(b -> BuyerResponseDto.builder()
                        .buyerNo(b.getBuyerNo())
                        .buyerComp(b.getBuyerComp())
                        .buyerName(b.getBuyerName())
                        .buyerEmail(b.getBuyerEmail())
                        .buyerPhone(b.getBuyerPhone())
                        .buyerAddr(b.getBuyerAddr())
                        .build())
                .toList();
    }

    @Override
    public BuyerResponseDto getOne(Long id) {
        Buyer b = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found: " + id));

        return BuyerResponseDto.builder()
                .buyerNo(b.getBuyerNo())
                .buyerComp(b.getBuyerComp())
                .buyerName(b.getBuyerName())
                .buyerEmail(b.getBuyerEmail())
                .buyerPhone(b.getBuyerPhone())
                .buyerAddr(b.getBuyerAddr())
                .build();
    }

    @Override
    @Transactional
    public BuyerResponseDto update(Long id, BuyerRequestDto dto) {
        Buyer b = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found: " + id));

        b.changeBuyerComp(dto.getBuyerComp());
        b.changeBuyerName(dto.getBuyerName());
        b.changeBuyerEmail(dto.getBuyerEmail());
        b.changeBuyerPhone(dto.getBuyerPhone());
        b.changeBuyerAddr(dto.getBuyerAddr());

        return BuyerResponseDto.builder()
                .buyerNo(b.getBuyerNo())
                .buyerComp(b.getBuyerComp())
                .buyerName(b.getBuyerName())
                .buyerEmail(b.getBuyerEmail())
                .buyerPhone(b.getBuyerPhone())
                .buyerAddr(b.getBuyerAddr())
                .build();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
