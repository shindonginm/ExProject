package com.springboot.wooden.dto;

import java.time.LocalDate;

public record OrderListRow(
        Long orderNo,
        String buyerName,
        String itemName,
        String itemCode,
        String itemSpec,
        int orderQty,
        int orderPrice,
        String orderState,
        String orderDeliState,
        LocalDate deliveryDate,
        LocalDate orderDate
){}
