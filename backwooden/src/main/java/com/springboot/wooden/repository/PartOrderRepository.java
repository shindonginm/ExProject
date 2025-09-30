package com.springboot.wooden.repository;

import com.springboot.wooden.domain.PartOrder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartOrderRepository extends JpaRepository<PartOrder, Long> {

    // 발주 상태별 조회하면서 Buyer, Part 정보도 같이 fetch
    @EntityGraph(attributePaths = {"buyer", "part"})
    List<PartOrder> findByPoState(String poState);

    // 특정 Buyer 기준 조회 (Buyer와 Part 정보 fetch)
    @EntityGraph(attributePaths = {"buyer", "part"})
    List<PartOrder> findByBuyerBuyerNo(Long buyerNo);

    // 특정 Part 기준 조회 (Buyer와 Part 정보 fetch)
    @EntityGraph(attributePaths = {"buyer", "part"})
    List<PartOrder> findByPartPartNo(Long partNo);

    // 모든 발주 조회 (Buyer와 Part 정보 fetch)
    @EntityGraph(attributePaths = {"buyer", "part"})
    List<PartOrder> findAll();
}
