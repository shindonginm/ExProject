package com.springboot.wooden.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private String buyerComp;

    @Column(name = "buyer_name", nullable = false, length = 10)
    private String buyerName;

    @Column(name = "buyer_email", nullable = false, length = 40)
    private String buyerEmail;

    @Column(name = "buyer_phone", nullable = false, length = 11)
    private String buyerPhone;

    @Column(name = "buyer_addr", nullable = false, length = 50)
    private String buyerAddr;

    public void changeBuyerComp(String buyerComp)   { this.buyerComp = buyerComp; }
    public void changeBuyerName(String buyerName)   { this.buyerName = buyerName; }
    public void changeBuyerEmail(String buyerEmail) { this.buyerEmail = buyerEmail; }
    public void changeBuyerPhone(String buyerPhone) { this.buyerPhone = buyerPhone; }
    public void changeBuyerAddr(String buyerAddr)   { this.buyerAddr = buyerAddr; }
}
