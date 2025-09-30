package com.springboot.wooden.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PartRequestDto {
    private Long partNo;
    private String partCode;
    private String partName;
    private String partSpec;
    private int partPrice;

    private Long buyerNo; // 등록/수정 시 FK로 받음
}
