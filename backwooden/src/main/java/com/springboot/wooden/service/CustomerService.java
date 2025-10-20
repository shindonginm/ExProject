package com.springboot.wooden.service;

import com.springboot.wooden.dto.CustomerRequestDto;
import com.springboot.wooden.dto.CustomerResponseDto;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<CustomerResponseDto> getAll();
    CustomerResponseDto register(CustomerRequestDto dto);
    CustomerResponseDto update(Long id, CustomerRequestDto dto);
    void delete(Long id);
}
