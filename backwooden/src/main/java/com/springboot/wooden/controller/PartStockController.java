package com.springboot.wooden.controller;

import com.springboot.wooden.dto.PartStockRequestDto;
import com.springboot.wooden.dto.PartStockResponseDto;
import com.springboot.wooden.service.PartStockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 재고(입고/사용량) 조회/조정 컨트롤러
 * - 목록/단건 조회는 GET
 * - 재고 증감(입고/사용 등)은 PATCH /adjust 로 처리
 * - 수량 계산/검증/트랜잭션은 Service 레이어가 담당
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stock/partstocks")
public class PartStockController {
    // 비즈니스 로직은 전부 Service로 위임
    private final PartStockService partStockService;
    
    // PartStock 목록 조회
    @GetMapping
    public List<PartStockResponseDto> list() {
        return partStockService.getPartStocks();
    }
    // Part 재고 수량 조정
    // 요청 바디(PartStockRequestDto)에 부품 ID, 증/감 수량을 담아 전송
    // 서비스에서 트랜잭션으로 현재고 갱신 + 입출고 이력 기록, 검증(음수 방지) 수행
    @PatchMapping("/adjust")
    public PartStockResponseDto adjust(@RequestBody @Valid PartStockRequestDto req) {
        return partStockService.adjust(req);
    }
}
