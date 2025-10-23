package com.springboot.wooden.controller;

import com.springboot.wooden.dto.BuyerRequestDto;
import com.springboot.wooden.dto.BuyerResponseDto;
import com.springboot.wooden.service.BuyerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buyer/buyercustomer")
@RequiredArgsConstructor
public class BuyerController {
    // 비즈니스 로직은 전부 Service로 위임
    private final BuyerService buyerService;

    // Buyer 목록 조회
    @GetMapping
    public List<BuyerResponseDto> list() {
        return buyerService.findAll();
    }
    // Buyer 등록
    @PostMapping
    public ResponseEntity<BuyerResponseDto> createBuyer(@RequestBody @Valid BuyerRequestDto dto) {
        return ResponseEntity.ok(buyerService.save(dto));
    }
    // Buyer 수정
    @PutMapping("/{buyerNo}")
    public ResponseEntity<BuyerResponseDto> update(@PathVariable Long buyerNo,
                                                   @RequestBody @Valid BuyerRequestDto dto) {
        return ResponseEntity.ok(buyerService.update(buyerNo, dto));
    }
    // Buyer 삭제
    @DeleteMapping("/{buyerNo}")
    public ResponseEntity<Void> delete(@PathVariable Long buyerNo) {
        buyerService.delete(buyerNo);
        return ResponseEntity.noContent().build();
    }
}

// Buyer 에 대해 조회/등록/수정/삭제를 담당하는 REST 컨트롤러 서비스에 일을 시키는 포워더 역할만 수행