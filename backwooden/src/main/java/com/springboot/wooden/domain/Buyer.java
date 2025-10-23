package com.springboot.wooden.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * BUYER_TBL
 * - PK: buyer_no (AUTO_INCREMENT)
 * - 회사/담당자/연락처/주소 등 구매처 기본 마스터
 * - 세터 대신 changeXxx 메서드로 유효한 변경만 노출
 */

@Entity
@Table(name = "BUYER_TBL")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Buyer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "buyer_no")
    private Long buyerNo; // PK: AUTO_INCREMENT

    @Column(name = "buyer_comp", nullable = false, length = 20)
    private String buyerComp;   // 구매처 명

    @Column(name = "buyer_name", nullable = false, length = 10)
    private String buyerName;   // 담당자 명

    @Column(name = "buyer_email", nullable = false, length = 40)
    private String buyerEmail;  // 담당자 이메일

    @Column(name = "buyer_phone", nullable = false, length = 11)
    private String buyerPhone;  // 담당자 번호

    @Column(name = "buyer_addr", nullable = false, length = 50)
    private String buyerAddr;   // 구매처 주소

    // 도메인 변경 메서드(의도된 필드만 수정 가능)
    public void changeBuyerComp(String buyerComp)   { this.buyerComp = buyerComp; }
    public void changeBuyerName(String buyerName)   { this.buyerName = buyerName; }
    public void changeBuyerEmail(String buyerEmail) { this.buyerEmail = buyerEmail; }
    public void changeBuyerPhone(String buyerPhone) { this.buyerPhone = buyerPhone; }
    public void changeBuyerAddr(String buyerAddr)   { this.buyerAddr = buyerAddr; }
}

// 구매거래처 엔티티. 구매처 기본정보(회사명/담당자/이메일/전화/주소)를 저장하고 변경 메서드로 필드 수정만 허용