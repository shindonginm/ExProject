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
    private Long partNo;

    // Buyer와 1:1 매핑 (FK: buyer_no)
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(
            name = "buyer_no",           // PART_TBL에 생성될 FK 컬럼
            referencedColumnName = "buyer_no",
            nullable = true,
            unique = true                // 1:1 보장
    )
    private Buyer buyer;

    @Column(name = "part_code", nullable = false, length = 20)
    private String partCode;

    @Column(name = "part_name", nullable = false, length = 30)
    private String partName;

    @Column(name = "part_spec", nullable = false, length = 40)
    private String partSpec;

    @Column(name = "part_price", nullable = false)
    private int partPrice;

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
