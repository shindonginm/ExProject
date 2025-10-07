package com.springboot.wooden.repository;

import com.springboot.wooden.domain.PartOrder;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

@Repository
public interface PartOrderRepository extends JpaRepository<PartOrder, Long> {

    // Buyer 기준: 특정 상태가 "아닌" 발주가 하나라도 있는가? (예: 입고완료 아닌 것 존재?)
    boolean existsByBuyer_BuyerNoAndPoStateNot(Long buyerNo, String poState);

    // Buyer 기준: 특정 상태인 발주가 존재하는가?
    boolean existsByBuyer_BuyerNoAndPoState(Long buyerNo, String poState);

    // Part 기준: 특정 상태가 "아닌" 발주가 하나라도 있는가?
    boolean existsByPart_PartNoAndPoStateNot(Long partNo, String poState);

    // Part 기준: 특정 상태인 발주가 존재하는가? (예: 입고대기 존재?)
    boolean existsByPart_PartNoAndPoState(Long partNo, String poState);

    // 단순 존재 (상태 무관)
    boolean existsByBuyer_BuyerNo(Long buyerNo);
    boolean existsByPart_PartNo(Long partNo);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update PartOrder po set po.part = null where po.part.partNo = :partNo")
    int detachPartFromOrders(@Param("partNo") Long partNo);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update PartOrder po set po.buyer = null where po.buyer.buyerNo = :buyerNo")
    int detachBuyerFromOrders(@Param("buyerNo") Long buyerNo);

    // 상태별 조회: Buyer/Part eager fetch
    @EntityGraph(attributePaths = {"buyer", "part"})
    List<PartOrder> findByPoState(String poState);

    // 상태 제외 조회: (예: 입고완료 제외 목록)
    @EntityGraph(attributePaths = {"buyer", "part"})
    List<PartOrder> findByPoStateNot(String poState);

    // Buyer 기준 목록
    @EntityGraph(attributePaths = {"buyer", "part"})
    List<PartOrder> findByBuyer_BuyerNo(Long buyerNo);

    // Part 기준 목록
    @EntityGraph(attributePaths = {"buyer", "part"})
    List<PartOrder> findByPart_PartNo(Long partNo);

    // 전체 목록 (eager fetch)
    @Override
    @EntityGraph(attributePaths = {"buyer", "part"})
    List<PartOrder> findAll();
}
