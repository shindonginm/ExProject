package com.springboot.wooden.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartOrderRequestDto {
    private Long poNo;
    private Long buyerNo;   // FK로 연결될 Buyer
    private Long partNo;    // FK로 연결될 Part
    private int poQty;
    private int poPrice;
    private String poState;
    private LocalDate poDate;
    private String buyerAddr;
}
