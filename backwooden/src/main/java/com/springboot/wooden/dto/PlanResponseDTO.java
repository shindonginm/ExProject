package com.springboot.wooden.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanResponseDTO {
    private Long planNo;    // PK (조회 시만)
    private String itemName;
    private int planQty;
    private String planState;
    private LocalDate planStartDate;
    private LocalDate planEndDate;
}
