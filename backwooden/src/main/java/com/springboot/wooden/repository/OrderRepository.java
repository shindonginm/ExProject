package com.springboot.wooden.repository;

import com.springboot.wooden.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByCustomer_CusComp(String cusComp);

    List<Order> findAllByOrderStateAndOrderDeliState(String orderState, String orderDeliState);
}
