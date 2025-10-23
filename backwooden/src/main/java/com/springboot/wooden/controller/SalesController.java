// src/main/java/com/springboot/wooden/controller/SalesController.java
package com.springboot.wooden.controller;

import com.springboot.wooden.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SalesController {
    // 실제 로직은 서비스로 위임
    private final SalesService salesService;

    // 월별 판매수익 (기준 : 납품완료+승인완료)
    @GetMapping("/monthly")
    public List<Map<String, Object>> monthlyRevenue() {
        return salesService.getMonthlyRevenueDelivered();
    }
    // 특정 월 Top10 (기준 : 납품완료+승인완료)
    @GetMapping("/item-qty-monthly")
    public List<Map<String, Object>> itemQtyMonthly(@RequestParam String ym) {
        return salesService.getItemQtyTop10DeliveredByMonth(ym);
    }
    // 모든 월×상품 (기준 : 납품완료+승인완료)
    @GetMapping("/item-qty-monthly-all")
    public List<Map<String, Object>> itemQtyMonthlyAll() {
        return salesService.getItemQtyMonthlyAllDelivered();
    }
}

// 매출 대시보드용 읽기 전용 API. 월별 매출 합계, 특정 월의 품목별 Top10 수량, 전월·전상품 교차표를 SalesService에 위임
