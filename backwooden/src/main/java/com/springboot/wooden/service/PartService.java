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

    // 조회
    PartResponseDto getOne(Long partNo);
    List<PartResponseDto> getAll();

    // (선택) Buyer 기준 조회: 1:1이면 유용
    PartResponseDto getByBuyerNo(Long buyerNo);

}
