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

    // 생산리스트에 생산중인게 있는지 확인
    @Query("""
        select (count(p) > 0)
        from Plan p
        where p.item.itemNo = :itemNo
          and p.planState = '생산중'
    """)
    boolean existsActiveByItem(@Param("itemNo") Long itemNo);
}