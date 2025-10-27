// backend/controller/ItemStockController.java
package com.springboot.wooden.controller;

import com.springboot.wooden.dto.ItemStockRequestDto;
import com.springboot.wooden.dto.ItemStockResponseDto;
import com.springboot.wooden.service.ItemStockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 재고(완제품) 조회/조정 컨트롤러
 * - 목록/단건 조회는 GET
 * - 재고 증감(입고/출고/손익조정 등)은 PATCH /adjust 로 처리
 * - 실제 계산/검증/트랜잭션은 Service가 담당
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stock/item")
public class ItemStockController {
    // 비즈니스 로직은 전부 Service로 위임
    private final ItemStockService itemStockService;

    // ItemStock 목록 조회
    @GetMapping
    public List<ItemStockResponseDto> list() {
        return itemStockService.getItemStocks();
    }
    // Item 단건 조회
    @GetMapping("/{itemNo}")
    public ItemStockResponseDto getOne(@PathVariable Long itemNo) {
        return itemStockService.getOne(itemNo);
    }
    // * - 재고 조정(입고/출고/수정)을 단일 엔드포인트로 처리
    //     * - body 예) { "itemNo":1, "delta": 10, "reason":"입고" }
    @PatchMapping("/adjust")
    public ItemStockResponseDto adjust(@RequestBody @Valid ItemStockRequestDto req) {
        return itemStockService.adjust(req);
    }
}

// 품목 재고의 목록/단건 조회와 수량 증감(조정) PATCH를 노출