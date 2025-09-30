package com.springboot.wooden.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartOrderResponseDto {
    private Long poNo;
    private String buyerComp;   // Buyer 이름
    private String partName;    // Part 이름
    private int poQty;
    private int poPrice;
    private String poState;
    private LocalDate poDate;
    private String buyerAddr;
}
