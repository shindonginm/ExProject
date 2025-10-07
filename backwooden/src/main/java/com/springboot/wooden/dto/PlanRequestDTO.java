package com.springboot.wooden.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanRequestDTO {

    // plan_no 매핑
    private Long planNo;
    private Long itemNo;
    private int planQty;
    private String planState;
    private LocalDate planStartDate;
    private LocalDate planEndDate;

    // ✅ 종료일이 시작일보다 빠르면 거부
    @AssertTrue(message = "생산종료일은 시작일과 같거나 이후여야 합니다.")
    public boolean isValidDateRange() {
        return planStartDate != null
                && planEndDate != null
                && !planEndDate.isBefore(planStartDate);
    }
}
