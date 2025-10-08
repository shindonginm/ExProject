package com.springboot.wooden.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name ="ITEM_STOCK_TBL", uniqueConstraints = @UniqueConstraint(columnNames = {"item_no"}))
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemStock {

    @Id
    @Column(name = "item_no")               // PK == FK
    private Long itemNo;                    // 공유 PK (== Item.itemNo)

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "item_no", nullable = false)
    private Item item;

    @Column(name = "is_qty", nullable = false)
    private int isQty;      // 현재 재고

    @Column(name = "total_in", nullable = false)
    private int totalIn;    // 누적 재고 (=생산완료)

    @Column(name = "total_out", nullable = false)
    private int totalOut;   // 누적 출고 (=주문완료)

    public void changeItem(Item item) {
        this.item = Objects.requireNonNull(item);
    }

    // 수량 증감 메서드
    public void changeQty(int proQty) {
        if (proQty <= 0) throw new IllegalArgumentException("proQty Must be > 0");
        this.isQty += proQty;
        this.totalIn += proQty;
    }

    public void sell(int sellQty) {
        if (sellQty <= 0) throw new IllegalArgumentException("sellQty Must be > 0");
        int next = this.isQty - sellQty;
        if (next < 0) throw new IllegalStateException("재고가 부족합니다");
        this.isQty = next;
        this.totalOut += sellQty;
    }
}












