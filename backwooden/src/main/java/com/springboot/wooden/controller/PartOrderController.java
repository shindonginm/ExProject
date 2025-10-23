package com.springboot.wooden.controller;

import com.springboot.wooden.dto.PartOrderRequestDto;
import com.springboot.wooden.dto.PartOrderResponseDto;
import com.springboot.wooden.service.PartOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buyer/partorder")
@RequiredArgsConstructor
public class PartOrderController {
    // 비즈니스 로직은 전부 Service로 위임
    private final PartOrderService partOrderService;

    // PartOrder 목록 조회
    @GetMapping                 // 미완료 목록 (입고완료 제외)
    public List<PartOrderResponseDto> getAllPartOrders() {
        return partOrderService.getAll();
    }
    // PartOrder 완료 목록 조회 : 서비스에서 완료 목록 상태만 반환.
    @GetMapping("/completed")
    public List<PartOrderResponseDto> getCompletedPartOrders() {
        return partOrderService.getCompletedList();
    }
    // PartOrder 등록
    @PostMapping
    public ResponseEntity<PartOrderResponseDto> createPartOrder(@RequestBody @Valid PartOrderRequestDto dto) {
        PartOrderResponseDto saved = partOrderService.addPartOrder(dto);
        return ResponseEntity.ok(saved);
    }

    // PartOrder 수정
    @PutMapping("/{poNo}")
    public ResponseEntity<PartOrderResponseDto> updatePartOrder(@PathVariable Long poNo,
                                                                @RequestBody @Valid PartOrderRequestDto dto) {
        PartOrderResponseDto updated = partOrderService.updatePartOrder(poNo, dto);
        return ResponseEntity.ok(updated);
    }

    // PartOrder 삭제 / 성공 시 204 No Content
    @DeleteMapping("/{poNo}")
    public ResponseEntity<Void> deletePartOrder(@PathVariable Long poNo) {
        partOrderService.deletePartOrder(poNo);
        return ResponseEntity.noContent().build();
    }
}

// PartOrder(등록/수정/삭제)와 목록 조회(미완료·완료)를 PartOrderService에 위임하는 REST 컨트롤러 서비스에 일을 시키는 포워더 역할만 수행