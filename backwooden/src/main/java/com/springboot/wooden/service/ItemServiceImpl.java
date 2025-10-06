// service/ItemServiceImpl.java
package com.springboot.wooden.service;

import com.springboot.wooden.domain.Item;
import com.springboot.wooden.domain.ItemStock;
import com.springboot.wooden.dto.ItemRequestDto;
import com.springboot.wooden.dto.ItemResponseDto;
import com.springboot.wooden.repository.ItemRepository;
import com.springboot.wooden.repository.ItemStockRepository;
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

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> getAll() {
        return itemRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResponseDto getOne(Long itemNo) {
        Item it = itemRepository.findById(itemNo)
                .orElseThrow(() -> new EntityNotFoundException("상품 없음: " + itemNo));
        return toDto(it);
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

    @Override
    @Transactional
    public void delete(Long itemNo) {
        // 1) 연결된 재고행이 있으면 삭제 금지
        if (itemStockRepository.existsById(itemNo)) {
            throw new IllegalStateException("해당 품목의 재고가 남아 있어 삭제할 수 없습니다. 먼저 재고를 삭제하세요.");
        }
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
