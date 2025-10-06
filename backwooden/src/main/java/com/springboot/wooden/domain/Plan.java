package com.springboot.wooden.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "PLAN_TBL")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_no")
    private Long planNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_no", referencedColumnName = "item_no", nullable = false)
    private Item item;

    @Column(name = "plan_qty", nullable = false)
    private int planQty;
    @Column(name = "plan_state", nullable = false)
    private String planState;
    @Column(name = "plan_start_date", nullable = false)
    private LocalDate planStartDate;
    @Column(name = "plan_end_date", nullable = false)
    private LocalDate planEndDate;

    public void changeItem(Item item) { this.item = item; }
    public void changePlanQty(int planQty) { this.planQty = planQty; }
    public void changePlanState(String planState) {
        this.planState = planState;
    }
    public void changePlanStartDate(LocalDate planStartDate) {
        this.planStartDate = planStartDate;
    }
    public void changePlanEndDate(LocalDate planEndDate) {
        this.planEndDate = planEndDate;
    }
}
