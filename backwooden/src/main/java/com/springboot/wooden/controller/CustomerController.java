package com.springboot.wooden.controller;

import com.springboot.wooden.dto.CustomerRequestDto;
import com.springboot.wooden.dto.CustomerResponseDto;
import com.springboot.wooden.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order/sellercustomer")
@RequiredArgsConstructor
public class CustomerController {
    // 비즈니스 로직은 전부 Service로 위임
    private final CustomerService service;

    // Customer 목록 조회
    @GetMapping
    public List<CustomerResponseDto> getAllCustomers() {
        return service.getAll();
    }
    // Customer 등록
    @PostMapping
    public ResponseEntity<CustomerResponseDto> createCustomer(@RequestBody @Valid CustomerRequestDto dto) {
        return ResponseEntity.ok(service.register(dto));
    }
    // Customer 수정
    @PutMapping("/{cusNo}")
    public ResponseEntity<CustomerResponseDto> update(@PathVariable Long cusNo,
                                                      @RequestBody @Valid CustomerRequestDto dto) {
        return ResponseEntity.ok(service.update(cusNo, dto));
    }
    // Customer 삭제
    @DeleteMapping("/{cusNo}")
    public ResponseEntity<Void> delete(@PathVariable Long cusNo) {
        service.delete(cusNo);
        return ResponseEntity.noContent().build();
    }
}

// Customer 에 대해 조회/등록/수정/삭제를 담당하는 REST 컨트롤러 서비스에 일을 시키는 포워더 역할만 수행