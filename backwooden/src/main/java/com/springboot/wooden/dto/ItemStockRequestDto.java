package com.springboot.wooden.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemStockRequestDto {

    @NotNull
    private Long itemNo;   // 대상 상품번호 (공유 PK = isNo)

    @NotNull
    private Integer delta; // 증감량 (+N / -N), 0은 허용 X
}
