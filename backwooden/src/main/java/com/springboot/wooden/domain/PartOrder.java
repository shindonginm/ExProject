package com.springboot.wooden.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "PART_ORDER_TBL")
@Getter
@ToString(exclude = {"buyer", "part"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "po_no")
    private Long poNo; // 발주 번호 (PK)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buyer_no", nullable = false)
    private Buyer buyer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "part_no", nullable = false)
    private Part part;

    @Column(name = "po_qty", nullable = false)
    private int poQty;   // 발주 수량

    @Column(name = "po_price", nullable = false)
    private int poPrice; // 발주 금액

    @Column(name = "po_state", length = 20, nullable = false)
    private String poState; // 발주 상태

    @Column(name = "po_date", nullable = false)
    private LocalDate poDate; // 발주일

    @Column(name = "buyer_addr", length = 100, nullable = false)
    private String buyerAddr; // 거래처 주소

    // --- 변경 메서드 ---
    public void changeBuyer(Buyer buyer) { this.buyer = buyer; }
    public void changePart(Part part) { this.part = part; }
    public void changePoQty(int qty) { this.poQty = qty; }
    public void changePoPrice(int price) { this.poPrice = price; }
    public void changePoState(String state) { this.poState = state; }
    public void changePoDate(LocalDate date) { this.poDate = date; }
    public void changeBuyerAddr(String addr) { this.buyerAddr = addr; }
}
