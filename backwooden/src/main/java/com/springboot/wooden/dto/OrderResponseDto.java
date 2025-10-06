package com.springboot.wooden.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {

    private Long orderNo;
    private String cusComp;  // Customer.company
    private String itemName;      // Item.name
    private int orderQty;
    private int orderPrice;
    private Long totalPrice;    // (계산 필드) = qty * price
    private String orderState;
    private String orderDeliState;
    private LocalDate deliveryDate;
    private LocalDate orderDate;
    private String cusAddr;
}

