package com.springboot.wooden.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ITEM_TBL")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_no")
    private Long itemNo;   // PK 자동증가

    @Column(name = "item_code", nullable = false, length = 20)
    private String itemCode;  // 상품코드

    @Column(name = "item_name", nullable = false, length = 30)
    private String itemName;  // 상품명

    @Column(name = "item_spec", nullable = false, length = 40)
    private String itemSpec;  // 규격

    @Column(name = "item_price", nullable = false)
    private int itemPrice; // 단가

    // 변경 메서드
    public void changeItemCode(String itemCode) { this.itemCode = itemCode; }
    public void changeItemName(String itemName) { this.itemName = itemName; }
    public void changeItemSpec(String itemSpec) { this.itemSpec = itemSpec; }
    public void changeItemPrice(int itemPrice)  { this.itemPrice = itemPrice; }
}
