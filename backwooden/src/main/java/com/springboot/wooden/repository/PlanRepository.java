package com.springboot.wooden.repository;

import com.springboot.wooden.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

}