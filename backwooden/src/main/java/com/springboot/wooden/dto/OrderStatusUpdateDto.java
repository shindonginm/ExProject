package com.springboot.wooden.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateDto {
    private String orderState;          // null 이면 변경 안 함
    private String orderDeliState;      // null 이면 변경 안 함
}
