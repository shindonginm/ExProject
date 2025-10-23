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
    private Long poNo; // 발주 번호 PK: AUTO_INCREMENT

    // 발주 대상 부품 / optional = true: 부품 매핑 없이도 일단 저장 허용
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "part_no", nullable = true)
    private Part part;      // 부품 번호

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "buyer_no", nullable = true)
    private Buyer buyer;    // 구매거래처

    @Column(name = "po_qty", nullable = false)
    private int poQty;   // 발주 수량

    @Column(name = "po_price", nullable = false)
    private int poPrice; // 발주 금액(단가×수량 등)

    @Column(name = "po_state", length = 20, nullable = false)
    private String poState; // 발주 상태: "입고대기", "입고완료"

    @Column(name = "po_date", nullable = false)
    private LocalDate poDate; // 발주 일

    @Column(name = "buyer_addr", length = 100, nullable = false)
    private String buyerAddr; // 거래처 주소

    @Column(name = "buyer_comp_snap")
    private String buyerCompSnap;   // 구매처 명 스냅샷: FK 변경/삭제 후에도 과거 표시 보존

    @Column(name = "part_name_snap")
    private String partNameSnap;    // 부품 명 스냅샷: FK 변경/삭제 후에도 과거 표시 보존

    // 도메인 변경 메서드: 의도된 필드만 수정 노출
    public void changeBuyer(Buyer buyer) { this.buyer = buyer; }
    public void changePart(Part part) { this.part = part; }
    public void changePoQty(int qty) { this.poQty = qty; }
    public void changePoPrice(int price) { this.poPrice = price; }
    public void changePoState(String state) { this.poState = state; }
    public void changePoDate(LocalDate date) { this.poDate = date; }
    public void changeBuyerAddr(String addr) { this.buyerAddr = addr; }
    public void changeBuyerCompSnap(String snap) { this.buyerCompSnap = snap; }
    public void changePartNameSnap(String snap) { this.partNameSnap = snap; }
}

// 부품 발주 엔티티. 부품/구매처와 연결되고, 수량·금액·상태·일자와 스냅샷 필드를 가진다. 변경 메서드로만 상태 갱신