package com.springboot.wooden.service;

import com.springboot.wooden.dto.BomRequestDto;
import com.springboot.wooden.dto.BomResponseDto;

import java.util.List;

public interface BomService {
    List<BomResponseDto> getAll();
    BomResponseDto getOne(Long bomId);
    BomResponseDto create(BomRequestDto dto);
    BomResponseDto update(Long bomId, BomRequestDto dto);
    void delete(Long bomId);
}
