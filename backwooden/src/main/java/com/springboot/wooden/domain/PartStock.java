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

    @Id
    @Column(name = "ps_no")
    private Long psNo;  // 공유 PK

    @OneToOne
    @MapsId
    @JoinColumn(name = "ps_no")
    private Part part;  // Part와 1:1 관계

    @Column(name = "ps_qty", nullable = false)
    private Integer psQty;  // 현재 재고 수량

    // 동시에 같은 수정 못하게 막음
    @Version // ← 낙관적 락 버전 칼럼 (INT/BIGINT)
    private Long version; // 낙관적 락 버전

    public void changeQty(int delta) {      // 수량 증감 메서드
        int next = this.psQty + delta;
        if (next < 0) {
            throw new IllegalStateException(
                    "재고가 음수가 될 수 없습니다. 현재=" + psQty + ", 요청=" + delta
            );
        }
        this.psQty = next;
    }
    // 신규 insert 시 version 기본값 보정(널 대신 0부터 시작)
    @PrePersist
    public void initVersion(){
        if(version == null ) version = 0L;
    }
}

// 부품 재고 엔티티. Part와 1:1 공유 PK로 묶이고, 수량(psQty) 증감과 동시수정 충돌을 @Version 낙관적 락으로 막음