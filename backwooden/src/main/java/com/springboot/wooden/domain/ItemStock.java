package com.springboot.wooden.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ITEM_STOCK_TBL")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemStock {

    @Id
    @Column(name = "is_no")
    private Long isNo;  // 공유 PK (== Item.itemNo)

    @OneToOne
    @MapsId
    @JoinColumn(name = "is_no")
    private Item item;

    @Column(name = "is_qty", nullable = false)
    private Integer isQty;

    @Version
    private Long version;

    public void changeQty(int delta) {
        int next = this.isQty + delta;
        if (next < 0) {
            throw new IllegalStateException("재고가 음수가 될 수 없습니다. 현재=" + isQty + ", 요청=" + delta);
        }
        this.isQty = next;
    }
}
