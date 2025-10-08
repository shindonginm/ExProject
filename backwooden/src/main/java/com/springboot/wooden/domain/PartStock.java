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
    private Long psNo;

    @OneToOne
    @MapsId
    @JoinColumn(name = "ps_no")
    private Part part;

    @Column(name = "ps_qty", nullable = false)
    private Integer psQty;

    // 동시에 같은 수정 못하게 막음
    @Version // ← 낙관적 락 버전 칼럼 (INT/BIGINT)
    private Long version;

    public void changeQty(int delta) {
        int next = this.psQty + delta;
        if (next < 0) {
            throw new IllegalStateException(
                    "재고가 음수가 될 수 없습니다. 현재=" + psQty + ", 요청=" + delta
            );
        }
        this.psQty = next;
    }

    @PrePersist
    public void initVersion(){
        if(version == null ) version = 0L;
    }
}
