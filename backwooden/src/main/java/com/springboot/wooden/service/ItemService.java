package com.springboot.wooden.service;

import com.springboot.wooden.dto.ItemRequestDto;
import com.springboot.wooden.dto.ItemResponseDto;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    List<ItemResponseDto> getAll();
    ItemResponseDto getOne(Long itemNo);                 // ★ 추가
    Optional<ItemResponseDto> getByName(String name);    // (옵션) 이름 조회
    ItemResponseDto save(ItemRequestDto requestDto);     // 등록
    ItemResponseDto update(Long id, ItemRequestDto dto); // 수정
    void delete(Long id);                                // 삭제
}
