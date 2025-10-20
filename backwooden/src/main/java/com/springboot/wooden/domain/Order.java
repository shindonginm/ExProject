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
    @JoinColumn(name = "item_no", nullable = true)
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

    // 출고
    @Column(name = "stock_applied", nullable = false)
    private boolean stockApplied; // 출고 반영 여부

    // 스냅샷 필드들 추가
    @Column(name = "cus_comp_snapshot", length = 200)
    private String cusCompSnapshot;

    @Column(name = "item_name_snapshot", length = 200)
    private String itemNameSnapshot;

    @Column(name = "item_code_snapshot", length = 100)
    private String itemCodeSnapshot;

    @Column(name = "item_spec_snapshot", length = 300)
    private String itemSpecSnapshot;


    public void changeCustomer(Customer customer) { this.customer = customer; }
    public void changeItem(Item item) { this.item = item; }
    public void changeOrderQty(int orderQty) { this.orderQty = orderQty; }
    public void changeOrderPrice(int orderPrice) { this.orderPrice = orderPrice; }
    public void changeOrderState(String orderState) { this.orderState = orderState; }
    public void changeOrderDeliState(String orderDeliState) { this.orderDeliState = orderDeliState; }
    public void changeDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }
    public void changeOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
    public void changeCusAddr(String cusAddr) { this.cusAddr = cusAddr; }
    public boolean isStockApplied() { return stockApplied; }
    public void markStockApplied() { this.stockApplied = true; }
    // 스냅샷 세터(변경 메서드)
    public void changeCusCompSnapshot(String value) { this.cusCompSnapshot = value; }
    public void changeItemNameSnapshot(String v)  { if (this.itemNameSnapshot == null) this.itemNameSnapshot = v; }
    public void changeItemCodeSnapshot(String v)  { if (this.itemCodeSnapshot == null) this.itemCodeSnapshot = v; }
    public void changeItemSpecSnapshot(String v)  { if (this.itemSpecSnapshot == null) this.itemSpecSnapshot = v; }

    // FK 끊기 전 안전 스냅샷 헬퍼
    public void snapshotItemBeforeUnlink() {
        if (this.item != null) {
            changeItemNameSnapshot(this.item.getItemName());
            // Item에 코드/스펙 필드가 실제로 있을 때만 사용
            changeItemCodeSnapshot(this.item.getItemCode());
            changeItemSpecSnapshot(this.item.getItemSpec());
        }
    }

}

