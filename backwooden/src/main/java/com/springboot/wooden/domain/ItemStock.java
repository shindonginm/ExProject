package com.springboot.wooden.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

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

    @OneToOne(fetch = FetchType.LAZY, optional = false)
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
    public void produce(int qty) {              // 입고(생산완료)
        if (qty <= 0) throw new IllegalArgumentException("qty must be > 0");
        this.isQty += qty;
        this.totalIn += qty;
    }

    public void sell(int qty) {                 // 출고(주문완료)
        if (qty <= 0) throw new IllegalArgumentException("qty must be > 0");
        int next = this.isQty - qty;
        if (next < 0) throw new IllegalStateException("재고가 부족합니다");
        this.isQty = next;
        this.totalOut += qty;
    }

    // 둘 다 처리하는 통합 메서드
    public void applyDelta(int delta) {
        if (delta == 0) throw new IllegalArgumentException("delta must not be 0");
        if (delta > 0) produce(delta);
        else sell(-delta);
    }
}












