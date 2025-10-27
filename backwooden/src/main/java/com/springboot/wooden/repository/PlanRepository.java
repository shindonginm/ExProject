package com.springboot.wooden.repository;

import com.springboot.wooden.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findByPlanStateNot(String planState);
    List<Plan> findByPlanState(String planState);
}