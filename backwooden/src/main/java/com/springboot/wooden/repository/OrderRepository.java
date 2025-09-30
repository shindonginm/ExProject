package com.springboot.wooden.repository;

import com.springboot.wooden.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByCustomer_CusComp(String cusComp);
}
