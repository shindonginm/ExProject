package com.springboot.wooden.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BomResponseDto {
    private Long bomId;
    private String itemName;
    private String partName;
    private int qtyPerItem;
}
