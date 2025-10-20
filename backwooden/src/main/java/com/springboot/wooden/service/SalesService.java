// src/main/java/com/springboot/wooden/service/SalesService.java
package com.springboot.wooden.service;

import com.springboot.wooden.repository.SalesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final SalesRepository repo;

    public List<Map<String, Object>> getMonthlyRevenueDelivered() {
        var rows = repo.findMonthlyRevenueDelivered();
        return rows.stream().map(r -> {
            Map<String, Object> m = new HashMap<>();
            m.put("ym",    r.get("ym"));
            m.put("total", r.get("total"));
            return m;
        }).toList();
    }

    public List<Map<String, Object>> getItemQtyTop10DeliveredByMonth(String ym) {
        // "2025-1" → "2025-01" 보정
        String fixed = normalizeYYYYMM(ym);
        var rows = repo.findItemQtyTop10DeliveredByMonth(fixed);
        return rows.stream().map(r -> {
            Map<String, Object> m = new HashMap<>();
            m.put("itemNo",   r.get("itemNo"));
            m.put("itemName", r.get("itemName"));
            m.put("qty",      r.get("qty"));
            return m;
        }).toList();
    }

    public List<Map<String, Object>> getItemQtyMonthlyAllDelivered() {
        var rows = repo.findItemQtyMonthlyAllDelivered();
        return rows.stream().map(r -> {
            Map<String, Object> m = new HashMap<>();
            m.put("ym",       r.get("ym"));
            m.put("itemNo",   r.get("itemNo"));
            m.put("itemName", r.get("itemName"));
            m.put("totalQty", r.get("totalQty"));
            return m;
        }).toList();
    }

    private String normalizeYYYYMM(String s) {
        if (s == null) return null;
        String x = s.trim().replace('.', '-').replace('/', '-').replace('_', '-').replaceAll("-+","-");
        if (x.matches("^\\d{6}$")) return x.substring(0,4) + "-" + x.substring(4,6);
        if (x.matches("^\\d{4}-\\d{1}$")) return x.substring(0,5) + "0" + x.substring(5);
        return x;
    }
}
