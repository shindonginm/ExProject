package com.springboot.wooden.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyerResponseDto {
    private Long buyerNo;
    private String buyerComp;
    private String buyerName;
    private String buyerEmail;
    private String buyerPhone;
    private String buyerAddr;
}
