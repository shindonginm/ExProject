package com.springboot.wooden.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name ="ITEM_STOCK_TBL")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "is_no")
    private Long isNo; // PK는 컬럼명에 맞춰 isNo로

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_no", nullable = false, unique = true)
    private Item item;

    @Column(name = "is_qty", nullable = false)
    private int isQty;

    // 수량 증감 메서드
    public void increase(int delta){ this.isQty += delta; }
    public void decrease(int delta){ this.isQty -= delta; }
}
