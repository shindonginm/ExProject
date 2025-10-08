package com.springboot.wooden.service;

import com.springboot.wooden.domain.ItemStock;
import com.springboot.wooden.dto.ItemStockRequestDto;
import com.springboot.wooden.dto.ItemStockResponseDto;
import com.springboot.wooden.repository.ItemStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemStockServiceImpl implements ItemStockService {

    private final ItemStockRepository itemStockRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ItemStockResponseDto> getItemStocks() {
        return itemStockRepository.findAllWithItem().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemStockResponseDto getOne(Long itemNo) {
        ItemStock s = itemStockRepository.findById(itemNo)
                .orElseThrow(() -> new IllegalArgumentException("완제품 재고 없음: " + itemNo));
        return toDto(s);
    }

    @Override
    @Transactional
    public ItemStockResponseDto adjust(ItemStockRequestDto request) {
        Integer delta = request.getDelta();
        if (delta == null || delta == 0) {
            throw new IllegalArgumentException("delta는 0이 될 수 없습니다.");
        }

        Long itemNo = request.getItemNo(); // 공유 PK(@MapsId) = Item.itemNo
        ItemStock s = itemStockRepository.findById(itemNo)
                .orElseThrow(() -> new IllegalStateException("재고행 없음: itemNo=" + itemNo));

        // 도메인 메서드로 수량 증감 (@Version으로 낙관적 락)
        s.changeQty(delta);

        return toDto(s);
    }

    private ItemStockResponseDto toDto(ItemStock s) {
        int qty = s.getIsQty();
        String name = s.getItem().getItemName();
        Long itemNo = s.getItemNo(); // PK=FK 하나로 통일

        return ItemStockResponseDto.builder()
                .isNo(itemNo)     // 프론트 DTO 필드명이 isNo라면 그대로 채워줌
                .itemName(name)
                .isQty(qty)
                .build();
    }
}
