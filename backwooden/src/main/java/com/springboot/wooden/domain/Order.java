package com.springboot.wooden.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity(name = "OrderEntity")
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
    @JoinColumn(name = "cus_no", nullable = true)
    private Customer customer;

    // 상품 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_no", nullable = true) // 삭제 가능성을 대비해 null 허용
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

    @Column(name = "stock_applied", nullable = false)
    private boolean stockApplied = false;

    // 판매처 스냅샷
    @Column(name = "cus_comp_snapshot")
    private String cusCompSnapshot;

    // 상품 스냅샷
    @Column(name = "item_name_snapshot")
    private String itemNameSnapshot;

    @Column(name = "item_code_snapshot")
    private String itemCodeSnapshot;

    @Column(name = "item_spec_snapshot")
    private String itemSpecSnapshot;

    public void changeCusCompSnapshot(String cusCompSnapshot) {
        this.cusCompSnapshot = cusCompSnapshot;
    }

    public void changeItemNameSnapshot(String itemNameSnapshot) {
        this.itemNameSnapshot = itemNameSnapshot;
    }

    public void changeItemCodeSnapshot(String itemCodeSnapshot) {
        this.itemCodeSnapshot = itemCodeSnapshot;
    }

    public void changeItemSpecSnapshot(String itemSpecSnapshot) {
        this.itemSpecSnapshot = itemSpecSnapshot;
    }

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
