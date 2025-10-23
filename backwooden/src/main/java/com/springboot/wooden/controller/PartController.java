package com.springboot.wooden.controller;

import com.springboot.wooden.dto.PartRequestDto;
import com.springboot.wooden.dto.PartResponseDto;
import com.springboot.wooden.service.PartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buyer/partlist")
@RequiredArgsConstructor
public class PartController {
    // 비즈니스 로직은 전부 Service로 위임
    private final PartService partService;

    // Part 목록 조회
    @GetMapping
    public List<PartResponseDto> getAll() {
        return partService.getAll();
    }
    // BuyerNo 기준 단건(1:1) 조회
    @GetMapping("/buyer/{buyerNo}")
    public PartResponseDto getByBuyer(@PathVariable Long buyerNo) {
        return partService.getByBuyerNo(buyerNo);
    }
    // Part 등록
    @PostMapping
    public ResponseEntity<PartResponseDto> add(@RequestBody @Valid PartRequestDto dto) {
        return ResponseEntity.ok(partService.save(dto));
    }
    // Part 수정
    @PutMapping("/{partNo}")
    public ResponseEntity<PartResponseDto> update(@PathVariable Long partNo,
                                                  @RequestBody @Valid PartRequestDto dto) {
        return ResponseEntity.ok(partService.update(partNo, dto));
    }
    // Part 삭제 / 성공 시 204 No Content
    @DeleteMapping("/{partNo}")
    public ResponseEntity<Void> delete(@PathVariable Long partNo) {
        partService.delete(partNo);
        return ResponseEntity.noContent().build();
    }
}

// Part 에 대해 조회/등록/수정/삭제를 담당하는 REST 컨트롤러 서비스에 일을 시키는 포워더 역할만 수행