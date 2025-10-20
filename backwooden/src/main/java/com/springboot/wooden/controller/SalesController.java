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

    private final SalesService salesService;

    /** 월별 판매수익(납품완료+승인완료) */
    @GetMapping("/monthly")
    public List<Map<String, Object>> monthlyRevenue() {
        return salesService.getMonthlyRevenueDelivered();
    }

    /** 특정 월 Top10 (납품완료+승인완료) */
    @GetMapping("/item-qty-monthly")
    public List<Map<String, Object>> itemQtyMonthly(@RequestParam String ym) {
        return salesService.getItemQtyTop10DeliveredByMonth(ym);
    }

    /** 모든 월×상품(납품완료+승인완료) */
    @GetMapping("/item-qty-monthly-all")
    public List<Map<String, Object>> itemQtyMonthlyAll() {
        return salesService.getItemQtyMonthlyAllDelivered();
    }
}
