// service/ItemServiceImpl.java
package com.springboot.wooden.service;

import com.springboot.wooden.domain.Item;
import com.springboot.wooden.domain.ItemStock;
import com.springboot.wooden.domain.Order;
import com.springboot.wooden.dto.ItemRequestDto;
import com.springboot.wooden.dto.ItemResponseDto;
import com.springboot.wooden.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemStockRepository itemStockRepository;
    private final OrderRepository orderRepository;
    private final PlanRepository planRepository;
    private final BOMRepository bomRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> getAll() {
        return itemRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ItemResponseDto create(ItemRequestDto dto) {
        // 1) Item 저장
        Item saved = itemRepository.save(Item.builder()
                .itemCode(dto.getItemCode())
                .itemName(dto.getItemName())
                .itemSpec(dto.getItemSpec())
                .itemPrice(dto.getItemPrice())
                .build());

        itemStockRepository.findById(saved.getItemNo())
                .orElseGet(() -> itemStockRepository.save(
                        ItemStock.builder()
                                .item(saved)
                                .isQty(0)
                                .build()
                ));

        return toDto(saved);
    }

    @Override
    @Transactional
    public ItemResponseDto update(Long itemNo, ItemRequestDto dto) {
        Item it = itemRepository.findById(itemNo)
                .orElseThrow(() -> new EntityNotFoundException("상품 없음: " + itemNo));

        it.changeItemCode(dto.getItemCode());
        it.changeItemName(dto.getItemName());
        it.changeItemSpec(dto.getItemSpec());
        it.changeItemPrice(dto.getItemPrice());

        return toDto(it);
    }

    @Transactional
    public void delete(Long itemNo) {
        if (orderRepository.existsByItem_ItemNoAndOrderDeliStateNot(itemNo, "납품완료")) {
            throw new IllegalStateException("삭제 불가: 납품이 완료되지 않은 주문이 존재합니다.");
        }

        // 완료 주문 스냅샷 → FK 해제
        for (Order o : orderRepository.findAllByItem_ItemNo(itemNo)) {
            if ("승인완료".equals(o.getOrderState()) && "납품완료".equals(o.getOrderDeliState())) {
                if (o.getItem() != null) {
                    if (o.getItemNameSnapshot() == null) o.changeItemNameSnapshot(o.getItem().getItemName());
                    if (o.getItemCodeSnapshot() == null) o.changeItemCodeSnapshot(o.getItem().getItemCode());
                    if (o.getItemSpecSnapshot() == null) o.changeItemSpecSnapshot(o.getItem().getItemSpec());
                }
                o.changeItem(null);
            }
        }
        // 재고행 선삭제
        itemStockRepository.deleteById(itemNo);

        // BOM/Plan 등 정리
        bomRepository.deleteByItemNo(itemNo);
        // planRepository도 FK 있으면 정리 필요. (없으면 패스)

        // 마지막으로 아이템 삭제
        itemRepository.deleteById(itemNo);
    }

    private ItemResponseDto toDto(Item it) {
        return ItemResponseDto.builder()
                .itemNo(it.getItemNo())
                .itemCode(it.getItemCode())
                .itemName(it.getItemName())
                .itemSpec(it.getItemSpec())
                .itemPrice(it.getItemPrice())
                .build();
    }
}
