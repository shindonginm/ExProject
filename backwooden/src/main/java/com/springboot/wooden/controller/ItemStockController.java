// backend/controller/ItemStockController.java
package com.springboot.wooden.controller;

import com.springboot.wooden.dto.ItemStockRequestDto;
import com.springboot.wooden.dto.ItemStockResponseDto;
import com.springboot.wooden.service.ItemStockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stock/itemstocks")
public class ItemStockController {

    private final ItemStockService itemStockService;

    // 목록
    @GetMapping
    public List<ItemStockResponseDto> list() {
        return itemStockService.getItemStocks();
    }

    @GetMapping("/{isNo}")
    public ItemStockResponseDto getOne(@PathVariable Long isNo) {
        return itemStockService.getOne(isNo);
    }

    @PatchMapping("/adjust")
    public ItemStockResponseDto adjust(@RequestBody @Valid ItemStockRequestDto req) {
        return itemStockService.adjust(req);
    }
}
