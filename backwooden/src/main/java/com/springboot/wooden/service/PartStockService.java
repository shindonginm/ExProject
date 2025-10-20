package com.springboot.wooden.service;

import com.springboot.wooden.dto.PartStockRequestDto;
import com.springboot.wooden.dto.PartStockResponseDto;
import java.util.List;

public interface PartStockService {
    List<PartStockResponseDto> getPartStocks();      // 목록 조회 (psNo/partName/psQty)
    PartStockResponseDto adjust(PartStockRequestDto req); // 증감 (낙관적 락)
}
