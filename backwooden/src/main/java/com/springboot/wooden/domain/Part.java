package com.springboot.wooden.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PART_TBL")
@Getter
@ToString(exclude = "buyer")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "part_no")
    private Long partNo;        // PK: AUTO_INCREMENT

    // Buyer와 1:1 매핑 (FK: buyer_no)
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(
            name = "buyer_no",           // PART_TBL에 생성될 FK 컬럼
            referencedColumnName = "buyer_no",
            nullable = true,
            unique = true                // 1:1 유일 제약
    )
    private Buyer buyer;

    @Column(name = "part_code", nullable = false, length = 20)
    private String partCode;    // 부품 코드

    @Column(name = "part_name", nullable = false, length = 30)
    private String partName;    // 부품 명

    @Column(name = "part_spec", nullable = false, length = 40)
    private String partSpec;    // 부품 규격

    @Column(name = "part_price", nullable = false)
    private int partPrice;      // 부품 단가

    // 도메인 변경 메서드: 의도된 필드만 수정 노출
    public void changeBuyer(Buyer buyer) {
        this.buyer = buyer;
    }
    public void changePartCode(String partCode) {
        this.partCode = partCode;
    }
    public void changePartName(String partName) {
        this.partName = partName;
    }
    public void changePartSpec(String partSpec) {
        this.partSpec = partSpec;
    }
    public void changePartPrice(int partPrice) {
        this.partPrice = partPrice;
    }
}

// 부품 엔티티. 구매처와 1:1로 연결되고, 코드/이름/규격/단가를 가진다. 변경 메서드로 안전하게 필드 갱신