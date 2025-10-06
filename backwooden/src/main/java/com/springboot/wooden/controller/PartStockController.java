package com.springboot.wooden.controller;

import com.springboot.wooden.dto.PartStockRequestDto;
import com.springboot.wooden.dto.PartStockResponseDto;
import com.springboot.wooden.service.PartStockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stock/partstocks")
public class PartStockController {

    private final PartStockService partStockService;

    @GetMapping
    public List<PartStockResponseDto> list() {
        return partStockService.getPartStocks();
    }

    @GetMapping("/{psNo}")
    public PartStockResponseDto getOne(@PathVariable Long psNo) {
        return partStockService.getOne(psNo);
    }

    @PatchMapping("/adjust")
    public PartStockResponseDto adjust(@RequestBody @Valid PartStockRequestDto req) {
        return partStockService.adjust(req);
    }
}
