package com.springboot.wooden.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * CUSTOMER_TBL
 * - PK: cus_no (AUTO_INCREMENT)
 * - 판매 거래처 기본 마스터
 * - 세터 대신 changeXxx 메서드로 의도된 변경만 허용
 */

@Entity
@Table(name="CUSTOMER_TBL")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cus_no")
    private Long cusNo;        // PK AUTO_INCREMENT

    @Column(name = "cus_comp", nullable = false, length = 20)
    private String cusComp;    // 거래처 명

    @Column(name = "cus_name", nullable = false, length = 10)
    private String cusName;    // 담당자 명

    @Column(name = "cus_email", nullable = false, length = 40)
    private String cusEmail;   // 담당자 이메일

    @Column(name = "cus_phone", nullable = false, length = 11)
    private String cusPhone;   // 판매처 번호

    @Column(name = "cus_addr", nullable = false, length = 50)
    private String cusAddr;    // 판매처 주소

    // 도메인 변경 메서드(의도된 필드만 수정 가능)
    public void changeCusComp(String cusComp)   { this.cusComp = cusComp; }
    public void changeCusName(String cusName)   { this.cusName = cusName; }
    public void changeCusEmail(String cusEmail) { this.cusEmail = cusEmail; }
    public void changeCusPhone(String cusPhone) { this.cusPhone = cusPhone; }
    public void changeCusAddr(String cusAddr)   { this.cusAddr = cusAddr; }
}

// 판매거래처(Customer) 엔티티. 거래처 기본정보(판매처 명/담당자/번호/주소)를 저장하고 세터 대신 변경 메서드로만 수정