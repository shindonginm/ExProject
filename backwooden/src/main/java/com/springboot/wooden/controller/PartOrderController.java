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
@RequestMapping("/api/buyer/partorder") // 그대로 유지
@RequiredArgsConstructor
public class PartOrderController {

    private final PartOrderService partOrderService;

    @GetMapping                 // 미완료 목록 (입고완료 제외)
    public List<PartOrderResponseDto> getAllPartOrders() {
        return partOrderService.getAll();
    }

    @GetMapping("/completed")
    public List<PartOrderResponseDto> getCompletedPartOrders() {
        return partOrderService.getCompletedList();
    }

    // 발주 등록: POST /api/buyer/partorder
    @PostMapping
    public ResponseEntity<PartOrderResponseDto> createPartOrder(@RequestBody @Valid PartOrderRequestDto dto) {
        PartOrderResponseDto saved = partOrderService.addPartOrder(dto);
        return ResponseEntity.ok(saved);
    }

    // 발주 수정: PUT /api/buyer/partorder/{poNo}
    @PutMapping("/{poNo}")
    public ResponseEntity<PartOrderResponseDto> updatePartOrder(@PathVariable Long poNo,
                                                                @RequestBody @Valid PartOrderRequestDto dto) {
        PartOrderResponseDto updated = partOrderService.updatePartOrder(poNo, dto);
        return ResponseEntity.ok(updated);
    }

    // 발주 삭제: DELETE /api/buyer/partorder/{poNo}
    @DeleteMapping("/{poNo}")
    public ResponseEntity<Void> deletePartOrder(@PathVariable Long poNo) {
        partOrderService.deletePartOrder(poNo);
        return ResponseEntity.noContent().build();
    }
}
