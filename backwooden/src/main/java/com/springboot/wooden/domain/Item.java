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
    private Long itemNo;   // PK AUTO_INCREMENT

    @Column(name = "item_code", nullable = false, length = 20)
    private String itemCode;  // 상품 코드 (외부표기/검색키)

    @Column(name = "item_name", nullable = false, length = 30)
    private String itemName;  // 상품 명

    @Column(name = "item_spec", nullable = false, length = 40)
    private String itemSpec;  // 상품 규격

    @Column(name = "item_price", nullable = false)
    private int itemPrice;    // 상품 단가

    // 도메인 변경 메서드(의도된 필드만 수정 가능)
    public void changeItemCode(String itemCode) { this.itemCode = itemCode; }
    public void changeItemName(String itemName) { this.itemName = itemName; }
    public void changeItemSpec(String itemSpec) { this.itemSpec = itemSpec; }
    public void changeItemPrice(int itemPrice)  { this.itemPrice = itemPrice; }
}

// 완제품(Item) 마스터 엔티티. 코드/이름/규격/단가를 갖고, 세터 대신 전용 변경 메서드로만 값 변경