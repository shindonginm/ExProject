package com.springboot.wooden.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDto {

    private Long customerId;   // FK → Customer.cus_no
    private Long itemId;       // FK → Item.item_no
    private int orderQty;
    private int orderPrice;
    private String orderState;
    private String orderDeliState;
    private LocalDate deliveryDate;
    private LocalDate orderDate;
    private String cusAddr;
}

