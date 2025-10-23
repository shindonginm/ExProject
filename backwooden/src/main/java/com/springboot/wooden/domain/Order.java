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

    // 판매처 FK (N:1). nullable 허용: 과거데이터 보존, 스냅샷으로 표시 가능
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cus_no", nullable = true)
    private Customer customer;

    // 상품 FK (N:1). nullable 허용
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_no", nullable = true)
    private Item item;

    @Column(name = "order_qty", nullable = false)
    private int orderQty;       // 주문 수량

    @Column(name = "order_price", nullable = false)
    private int orderPrice;     // 주문 금액

    @Column(name = "order_state", length = 10, nullable = false)
    private String orderState;  // 승인 상태(예: 승인대기/승인완료)

    @Column(name = "order_deli_state", length = 10, nullable = false)
    private String orderDeliState; // 납품 상태(예: 납품대기/납품완료)

    @Column(name = "delivery_date", nullable = false)
    private LocalDate deliveryDate; // 납품 일

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;    // 주문 일

    @Column(name = "cus_addr", length = 50, nullable = false)
    private String cusAddr;    // 배송 주소(주문 시점 주소 스냅)

    // 재고 출고 반영 여부(중복 처리 방지 플래그)
    @Column(name = "stock_applied", nullable = false)
    private boolean stockApplied; // 출고 반영 여부

    // 스냅샷 필드들 추가 (연관 엔티티가 바뀌거나 삭제돼도 당시 화면 표시값 유지)
    @Column(name = "cus_comp_snapshot", length = 200)
    private String cusCompSnapshot;

    @Column(name = "item_name_snapshot", length = 200)
    private String itemNameSnapshot;

    @Column(name = "item_code_snapshot", length = 100)
    private String itemCodeSnapshot;

    @Column(name = "item_spec_snapshot", length = 300)
    private String itemSpecSnapshot;

    // 도메인 변경(의도된 필드만 노출)
    public void changeCustomer(Customer customer) { this.customer = customer; }
    public void changeItem(Item item) { this.item = item; }
    public void changeOrderQty(int orderQty) { this.orderQty = orderQty; }
    public void changeOrderPrice(int orderPrice) { this.orderPrice = orderPrice; }
    public void changeOrderState(String orderState) { this.orderState = orderState; }
    public void changeOrderDeliState(String orderDeliState) { this.orderDeliState = orderDeliState; }
    public void changeDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }
    public void changeOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
    public void changeCusAddr(String cusAddr) { this.cusAddr = cusAddr; }
    // 출고 반영 완료 체크/마킹
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

// 주문 엔티티. 거래처/상품과 연관, 상태·납품상태·일자·주소·출고반영 여부를 담고,
// 추후 FK가 끊겨도 이력 보존되도록 거래처명/상품 스냅샷 필드를 갖는다
