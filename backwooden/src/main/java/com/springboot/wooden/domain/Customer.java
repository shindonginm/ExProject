package com.springboot.wooden.domain;

import jakarta.persistence.*;
import lombok.*;

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
    private Long cusNo;        // PK 자동증가

    @Column(name = "cus_comp", nullable = false, length = 20)
    private String cusComp;    // 거래처 명

    @Column(name = "cus_name", nullable = false, length = 10)
    private String cusName;    // 담당자 명

    @Column(name = "cus_email", nullable = false, length = 40)
    private String cusEmail;   // 담당자 이메일

    @Column(name = "cus_phone", nullable = false, length = 11)
    private String cusPhone;   // 회사 번호 (정규식 체크 예정)

    @Column(name = "cus_addr", nullable = false, length = 50)
    private String cusAddr;    // 회사 주소

    public void changeCusComp(String cusComp)   { this.cusComp = cusComp; }
    public void changeCusName(String cusName)   { this.cusName = cusName; }
    public void changeCusEmail(String cusEmail) { this.cusEmail = cusEmail; }
    public void changeCusPhone(String cusPhone) { this.cusPhone = cusPhone; }
    public void changeCusAddr(String cusAddr)   { this.cusAddr = cusAddr; }
}
