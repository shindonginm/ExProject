package com.springboot.wooden.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartStockResponseDto {
    private Long psNo;       // == partNo (공유 PK)
    private String partName; // 부품명
    private int psQty;       // 현재 재고수량
}
