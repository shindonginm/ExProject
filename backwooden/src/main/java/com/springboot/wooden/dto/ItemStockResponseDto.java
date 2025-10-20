package com.springboot.wooden.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemStockResponseDto {
    private Long isNo;       // == itemNo (공유 PK)
    private String itemName; // 상품명
    private int isQty;       // 현재 재고수량
    private int totalIn;     // 누적 입고
    private int totalOut;    // 누적 출고
}