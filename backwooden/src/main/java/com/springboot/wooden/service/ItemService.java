package com.springboot.wooden.service;

import com.springboot.wooden.dto.ItemRequestDto;
import com.springboot.wooden.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {
    List<ItemResponseDto> getAll();
    ItemResponseDto create(ItemRequestDto dto);
    ItemResponseDto update(Long itemNo, ItemRequestDto dto);
    void delete(Long itemNo);
}
