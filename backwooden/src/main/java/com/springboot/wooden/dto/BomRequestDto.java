package com.springboot.wooden.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BomRequestDto {
    private Long bomId;
    private Long itemNo;
    private Long partNo;
    private int qtyPerItem;
}
