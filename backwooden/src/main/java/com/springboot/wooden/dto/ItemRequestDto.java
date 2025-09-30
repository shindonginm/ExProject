package com.springboot.wooden.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private Long itemNo;
    private String itemCode;
    private String itemName;
    private String itemSpec;
    private int itemPrice;
}
