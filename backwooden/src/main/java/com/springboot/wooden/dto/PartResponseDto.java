package com.springboot.wooden.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartResponseDto {

    private Long partNo;       // PK (조회 시만)
    private String partCode;
    private String partName;
    private String partSpec;
    private int partPrice;

    private String buyerComp;  // 구매처명 (응답 전용)
}
