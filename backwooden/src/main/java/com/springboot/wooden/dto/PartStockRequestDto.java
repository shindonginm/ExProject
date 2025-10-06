package com.springboot.wooden.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartStockRequestDto {

    @NotNull
    private Long partNo;   // 대상 부품번호 (공유PK = psNo)

    @NotNull
    private Integer delta; // 증감량 (+N / -N), 0은 허용 X
}
