package com.springboot.wooden.domain;

import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_no", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "part_no", nullable = false)
    private Part part;

    @Column(name = "qty_per_item", nullable = false)
    private int qtyPerItem; // 완제품 1개당 필요한 해당 부품 수량

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
