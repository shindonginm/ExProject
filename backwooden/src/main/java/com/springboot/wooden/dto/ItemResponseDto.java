package com.springboot.wooden.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponseDto {
    private Long itemNo;     // PK
    private String itemCode;
    private String itemName;
    private String itemSpec;
    private int itemPrice;
}
