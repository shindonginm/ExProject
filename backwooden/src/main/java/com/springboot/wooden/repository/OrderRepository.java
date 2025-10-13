package com.springboot.wooden.repository;

import com.springboot.wooden.domain.Order;
import com.springboot.wooden.dto.OrderListRow; // DTO 새로 추가
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // === 기존 로직 ===
    List<Order> findAllByCustomer_CusComp(String cusComp);
    List<Order> findAllByOrderStateAndOrderDeliState(String orderState, String orderDeliState);

    @Query("""
        select (count(o) > 0)
        from OrderEntity o
        where o.customer.cusNo = :cusNo
          and (o.orderState <> '승인완료' or o.orderDeliState <> '납품완료')
    """)
    boolean existsUncompletedByCustomer(@Param("cusNo") Long cusNo);

    List<Order> findAllByCustomer_CusNo(Long cusNo);

    @Query("""
        select (count(o) > 0)
        from OrderEntity o
        where o.item.itemNo = :itemNo
          and (o.orderState <> '승인완료' or o.orderDeliState <> '납품완료')
    """)
    boolean existsUncompletedByItem(@Param("itemNo") Long itemNo);

    List<Order> findAllByItem_ItemNo(Long itemNo);


    // === 🔹 추가: 주문 리스트 (스냅샷 우선 안전 조회) ===
    @Query("""
        select new com.springboot.wooden.dto.OrderListRow(
            o.orderNo,
            coalesce(o.cusCompSnapshot, c.cusComp),
            coalesce(o.itemNameSnapshot, i.itemName),
            coalesce(o.itemCodeSnapshot, i.itemCode),
            coalesce(o.itemSpecSnapshot, i.itemSpec),
            o.orderQty,
            o.orderPrice,
            o.orderState,
            o.orderDeliState,
            o.deliveryDate,
            o.orderDate
        )
        from OrderEntity o
        left join o.customer c
        left join o.item i
        order by o.orderDate desc, o.orderNo desc
    """)
    List<OrderListRow> findOrderListRows();


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
