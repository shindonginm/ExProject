package com.springboot.wooden.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PART_STOCK_TBL")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartStock {

    // 부품 재고 일련번호 (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ps_no")
    private Long psNo;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "part_no", nullable = false, unique = true)
    private Part part;

    // 수량
    @Column(name = "ps_qty", nullable = false)
    private int psQty;

    // 도메인 메서드
    public void increase(int delta) { this.psQty += delta; }
    public void decrease(int delta) { this.psQty -= delta; }
}
