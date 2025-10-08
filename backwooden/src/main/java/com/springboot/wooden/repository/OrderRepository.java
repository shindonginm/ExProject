package com.springboot.wooden.repository;

import com.springboot.wooden.domain.Order;
import com.springboot.wooden.dto.OrderListRow; // DTO 새로 추가
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
