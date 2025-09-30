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

    private final PartService partService;

    // 목록: GET /api/buyer/partlist
    @GetMapping
    public List<PartResponseDto> getAll() {
        return partService.getAll();
    }

    // 단건: GET /api/buyer/partlist/{partNo}
    @GetMapping("/{partNo}")
    public PartResponseDto getOne(@PathVariable Long partNo) {
        return partService.getOne(partNo);
    }

    // 구매처 기준 단건(1:1): GET /api/buyer/partlist/buyer/{buyerNo}
    @GetMapping("/buyer/{buyerNo}")
    public PartResponseDto getByBuyer(@PathVariable Long buyerNo) {
        return partService.getByBuyerNo(buyerNo);
    }

    // 등록
    @PostMapping
    public ResponseEntity<PartResponseDto> add(@RequestBody @Valid PartRequestDto dto) {
        return ResponseEntity.ok(partService.save(dto));
    }

    // 수정: PUT /api/buyer/partlist/{partNo}
    @PutMapping("/{partNo}")
    public ResponseEntity<PartResponseDto> update(@PathVariable Long partNo,
                                                  @RequestBody @Valid PartRequestDto dto) {
        return ResponseEntity.ok(partService.update(partNo, dto));
    }

    // 삭제: DELETE /api/buyer/partlist/{partNo}
    @DeleteMapping("/{partNo}")
    public ResponseEntity<Void> delete(@PathVariable Long partNo) {
        partService.delete(partNo);
        return ResponseEntity.noContent().build();
    }
}
