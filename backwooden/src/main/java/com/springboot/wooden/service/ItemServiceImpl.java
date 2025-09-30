package com.springboot.wooden.service;

import com.springboot.wooden.domain.Item;
import com.springboot.wooden.dto.ItemRequestDto;
import com.springboot.wooden.dto.ItemResponseDto;
import com.springboot.wooden.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;

    @Override
    public List<ItemResponseDto> getAll() {
        return repository.findAll().stream()
                .map(i -> ItemResponseDto.builder()
                        .itemNo(i.getItemNo())
                        .itemCode(i.getItemCode())
                        .itemName(i.getItemName())
                        .itemSpec(i.getItemSpec())
                        .itemPrice(i.getItemPrice())
                        .build())
                .toList();
    }

    @Override
    public ItemResponseDto getOne(Long itemNo) {
        Item i = repository.findById(itemNo)
                .orElseThrow(() -> new IllegalArgumentException("상품 없음: " + itemNo));

        return ItemResponseDto.builder()
                .itemNo(i.getItemNo())
                .itemCode(i.getItemCode())
                .itemName(i.getItemName())
                .itemSpec(i.getItemSpec())
                .itemPrice(i.getItemPrice())
                .build();
    }

    @Override
    public Optional<ItemResponseDto> getByName(String name) {
        return repository.findByItemName(name)
                .map(i -> ItemResponseDto.builder()
                        .itemNo(i.getItemNo())
                        .itemCode(i.getItemCode())
                        .itemName(i.getItemName())
                        .itemSpec(i.getItemSpec())
                        .itemPrice(i.getItemPrice())
                        .build());
    }

    @Override
    @Transactional
    public ItemResponseDto save(ItemRequestDto dto) {
        Item saved = repository.save(Item.builder()
                .itemCode(dto.getItemCode())
                .itemName(dto.getItemName())
                .itemSpec(dto.getItemSpec())
                .itemPrice(dto.getItemPrice())
                .build());

        return ItemResponseDto.builder()
                .itemNo(saved.getItemNo())
                .itemCode(saved.getItemCode())
                .itemName(saved.getItemName())
                .itemSpec(saved.getItemSpec())
                .itemPrice(saved.getItemPrice())
                .build();
    }

    @Override
    @Transactional
    public ItemResponseDto update(Long id, ItemRequestDto dto) {
        Item i = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품 없음: " + id));

        i.changeItemCode(dto.getItemCode());
        i.changeItemName(dto.getItemName());
        i.changeItemSpec(dto.getItemSpec());
        i.changeItemPrice(dto.getItemPrice());

        return ItemResponseDto.builder()
                .itemNo(i.getItemNo())
                .itemCode(i.getItemCode())
                .itemName(i.getItemName())
                .itemSpec(i.getItemSpec())
                .itemPrice(i.getItemPrice())
                .build();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
