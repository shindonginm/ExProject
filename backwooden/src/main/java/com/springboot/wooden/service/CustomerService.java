package com.springboot.wooden.service;

import com.springboot.wooden.dto.CustomerRequestDto;
import com.springboot.wooden.dto.CustomerResponseDto;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<CustomerResponseDto> getAll();
    CustomerResponseDto getOne(Long id);                    // ★ 추가
    Optional<CustomerResponseDto> getByCompany(String company);
    CustomerResponseDto register(CustomerRequestDto dto);
    CustomerResponseDto update(Long id, CustomerRequestDto dto);
    void delete(Long id);
}
