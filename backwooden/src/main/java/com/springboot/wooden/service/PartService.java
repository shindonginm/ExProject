package com.springboot.wooden.service;

import com.springboot.wooden.domain.Part;
import com.springboot.wooden.dto.PartRequestDto;
import com.springboot.wooden.dto.PartResponseDto;

import java.util.List;
import java.util.Optional;

public interface PartService {

    // 등록
    PartResponseDto save(PartRequestDto dto);

    // 수정
    PartResponseDto update(Long partNo, PartRequestDto dto);

    // 삭제
    void delete(Long partNo);

    List<PartResponseDto> getAll();

    PartResponseDto getByBuyerNo(Long buyerNo);

}
