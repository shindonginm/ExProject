package com.springboot.wooden.repository;

import com.springboot.wooden.domain.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 고객 회사명으로 조회 (N+1 방지)
    @EntityGraph(attributePaths = {"customer", "item"})
    List<Order> findAllByCustomer_CusComp(String cusComp);

    // 고객 PK로 조회 (N+1 방지)
    @EntityGraph(attributePaths = {"customer", "item"})
    List<Order> findAllByCustomer_CusNo(Long cusNo);

    @EntityGraph(attributePaths = {"customer", "item"})
    List<Order> findAll();


    // "해당 고객의 '납품완료'가 아닌 주문이 하나라도 있으면 삭제 X " (삭제 차단용)
    boolean existsByCustomer_CusNoAndOrderDeliStateNot(Long cusNo, String orderDeliState);

    // 상태별 목록
    List<Order> findAllByOrderStateAndOrderDeliState(String orderState, String orderDeliState);

    // 아이템별 미완료 주문 존재 여부
    boolean existsByItem_ItemNoAndOrderDeliStateNot(Long itemNo, String orderDeliState);
    // 아이템별 주문 전체 조회
    List<Order> findAllByItem_ItemNo(Long itemNo);

    /**
     * 예측용 메서드
     * 주간 히스토리(빈 주=0) 집계를 위한 "원본 행" 조회용:
     * - itemNo 기준
     * - deliveryDate 기간 내
     * - 납품완료(or 승인완료)만
     */
    @Query("""
        select o
        from OrderEntity o
        where o.item.itemNo = :itemNo
          and o.deliveryDate is not null
          and o.deliveryDate between :start and :end
          and (
                o.orderDeliState = '납품완료'
             or o.orderState     = '승인완료'
          )
        order by o.deliveryDate asc, o.orderNo asc
    """)
    List<Order> findCompletedByItemAndDeliveryDateBetween(
            @Param("itemNo") Long itemNo,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}