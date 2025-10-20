package com.springboot.wooden.service;

import com.springboot.wooden.domain.Item;
import com.springboot.wooden.domain.ItemStock;
import com.springboot.wooden.dto.ItemStockRequestDto;
import com.springboot.wooden.dto.ItemStockResponseDto;
import com.springboot.wooden.repository.ItemRepository;
import com.springboot.wooden.repository.ItemStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemStockServiceImpl implements ItemStockService {

    private final ItemStockRepository itemStockRepository;
    private final ItemRepository itemRepository;

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
        ItemStock s = getOrCreateLocked(itemNo); // 잠금 사용

        // 도메인 메서드로 수량 증감 (@Version으로 낙관적 락)
        s.applyDelta(delta);        // 증가 감소 둘다 처리

        return toDto(s);
    }

    private ItemStockResponseDto toDto(ItemStock s) {
        Long itemNo = s.getItemNo();
        Item item = s.getItem(); // LEFT FETCH + NOT_FOUND IGNORE → null 가능
        String itemName = (item != null ? item.getItemName() : "(미연결)");
        int qty = s.getIsQty();

        return ItemStockResponseDto.builder()
                .isNo(itemNo)     // 프론트 DTO 필드명이 isNo라면 그대로 채워줌
                .itemName(itemName)
                .isQty(s.getIsQty())
                .totalIn(s.getTotalIn())
                .totalOut(s.getTotalOut())
                .build();
    }


    private ItemStock getOrCreateLocked(Long itemNo) {
        return itemStockRepository.findByItemNoForUpdate(itemNo)
                .orElseGet(() -> {
                    var item = itemRepository.findById(itemNo)
                            .orElseThrow(() -> new IllegalArgumentException("상품 없음: " + itemNo));
                    return itemStockRepository.save(ItemStock.builder()
                            .item(item)
                            .isQty(0)
                            .totalIn(0)
                            .totalOut(0)
                            .build());
                });
    }

    @Override
    public void sell(Long itemNo, int qty) {
        if (qty <= 0) return;
        ItemStock s = getOrCreateLocked(itemNo);
        if (s.getIsQty() < qty) {
            throw new IllegalStateException("재고 부족: 현재 " + s.getIsQty() + "개, 출고요청 " + qty + "개");
        }
        s.sell(qty); // 엔티티 메서드에서 isQty 감소, totalOut 증가
    }
}
