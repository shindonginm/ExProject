package com.springboot.wooden.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * BOM_TBL
 * - (item_no, part_no) 유니크: 같은 완제품-부품 조합을 중복으로 넣지 못하게 잠금
 * - Item, Part는 LAZY 로딩으로 성능·순환참조 리스크 줄임
 * - qtyPerItem: 완제품 1개당 필요한 특정 부품의 수량
 */

@Entity
@Table(name = "BOM_TBL",
        uniqueConstraints = @UniqueConstraint(columnNames = {"item_no","part_no"}))
@Getter
@ToString(exclude = {"item","part"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BOM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bom_id")
    private Long bomId;

    // N:1 관계 - 여러 BOM 행이 하나의 Item을 참조
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_no", nullable = false)
    private Item item;
    // N:1 관계 - 여러 BOM 행이 하나의 Part를 참조
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "part_no", nullable = false)
    private Part part;
    // 완제품 1개당 필요한 해당 부품 수량 (자연수)
    @Column(name = "qty_per_item", nullable = false)
    private int qtyPerItem; // 완제품 1개당 필요한 해당 부품 수량

    // 세터 대신 명시적 변경 메서드: 유효성 검증 포함
    public void changeItem(Item item) {
        if (item == null) throw new IllegalArgumentException("item is null");
        this.item = item;
    }
    public void changePart(Part part) {
        if (part == null) throw new IllegalArgumentException("part is null");
        this.part = part;
    }
    public void changeQtyPerItem(int qtyPerItem) {
        if (qtyPerItem <= 0) throw new IllegalArgumentException("qtyPerItem must be > 0");
        this.qtyPerItem = qtyPerItem;
    }
}

// BOM 엔티티. 완제품(Item)과 부품(Part)을 ManyToOne으로 묶고,
// 1개 완제품에 필요한 해당 부품 수량(qtyPerItem)을 저장함. 같은 아이템·부품 조합은 유니크