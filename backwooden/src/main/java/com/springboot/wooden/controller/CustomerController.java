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

    private final CustomerService service;

    // 전체
    @GetMapping
    public List<CustomerResponseDto> getAllCustomers() {
        return service.getAll();
    }

    // 단건(ID)
    @GetMapping("/{cusNo}")
    public CustomerResponseDto getOne(@PathVariable Long cusNo) {
        return service.getOne(cusNo);
    }

    // 회사명 기준 단건(Unique 가정) → 없으면 404
    @GetMapping("/company/{company}")
    public ResponseEntity<CustomerResponseDto> getByCompany(@PathVariable String company) {
        return service.getByCompany(company)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 등록
    @PostMapping
    public ResponseEntity<CustomerResponseDto> createCustomer(@RequestBody @Valid CustomerRequestDto dto) {
        return ResponseEntity.ok(service.register(dto));
    }

    // 수정
    @PutMapping("/{cusNo}")
    public ResponseEntity<CustomerResponseDto> update(@PathVariable Long cusNo,
                                                      @RequestBody @Valid CustomerRequestDto dto) {
        return ResponseEntity.ok(service.update(cusNo, dto));
    }

    // 삭제
    @DeleteMapping("/{cusNo}")
    public ResponseEntity<Void> delete(@PathVariable Long cusNo) {
        service.delete(cusNo);
        return ResponseEntity.noContent().build();
    }
}
