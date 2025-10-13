package com.springboot.wooden.dto;

import lombok.*;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class WeeklyHistoryDto {
    private Long itemNo;                 // 조회 대상 아이템
    private String itemName;             // (옵션) 스냅샷/조인해서 넣어도 됨
    private List<Point> history;         // 주간 시계열 (빈 주 = 0)

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Point {
        private String date;             // 주 시작일(월요일, yyyy-MM-dd)
        private double qty;              // 그 주 판매 합계
    }
}
