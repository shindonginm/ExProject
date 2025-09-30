package com.springboot.wooden.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "ORDER_TBL")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_no")
    private Long orderNo; // 주문번호 (PK, AUTO_INCREMENT)

    // 판매처 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cus_no", nullable = false)
    private Customer customer;

    // 상품 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_no", nullable = false)
    private Item item;

    @Column(name = "order_qty", nullable = false)
    private int orderQty;

    @Column(name = "order_price", nullable = false)
    private int orderPrice;

    @Column(name = "order_state", length = 10, nullable = false)
    private String orderState;

    @Column(name = "order_deli_state", length = 10, nullable = false)
    private String orderDeliState;

    @Column(name = "delivery_date", nullable = false)
    private LocalDate deliveryDate;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "cus_addr", length = 50, nullable = false)
    private String cusAddr;

    public void changeCustomer(Customer customer) { this.customer = customer; }
    public void changeItem(Item item) { this.item = item; }
    public void changeOrderQty(int orderQty) { this.orderQty = orderQty; }
    public void changeOrderPrice(int orderPrice) { this.orderPrice = orderPrice; }
    public void changeOrderState(String orderState) { this.orderState = orderState; }
    public void changeOrderDeliState(String orderDeliState) { this.orderDeliState = orderDeliState; }
    public void changeDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }
    public void changeOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
    public void changeCusAddr(String cusAddr) { this.cusAddr = cusAddr; }
}

