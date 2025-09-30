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

    private final BuyerService buyerService;

    // 목록
    @GetMapping
    public List<BuyerResponseDto> list() {
        return buyerService.findAll();
    }

    // 단건
    @GetMapping("/{buyerNo}")
    public BuyerResponseDto getOne(@PathVariable Long buyerNo) {
        return buyerService.getOne(buyerNo);
    }

    // 추가
    @PostMapping
    public ResponseEntity<BuyerResponseDto> createBuyer(@RequestBody @Valid BuyerRequestDto dto) {
        return ResponseEntity.ok(buyerService.save(dto));
    }

    // 수정
    @PutMapping("/{buyerNo}")
    public ResponseEntity<BuyerResponseDto> update(@PathVariable Long buyerNo,
                                                   @RequestBody @Valid BuyerRequestDto dto) {
        return ResponseEntity.ok(buyerService.update(buyerNo, dto));
    }

    // 삭제
    @DeleteMapping("/{buyerNo}")
    public ResponseEntity<Void> delete(@PathVariable Long buyerNo) {
        buyerService.delete(buyerNo);
        return ResponseEntity.noContent().build();
    }
}
