package com.springboot.wooden.repository;

import com.springboot.wooden.domain.Customer;
import com.springboot.wooden.domain.Item;
import com.springboot.wooden.domain.Order;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
@Log4j2
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ItemRepository itemRepository;

    // INSERT
    @Test
    @Transactional
    @Commit
    public void testInsert() {
        // 이미 존재하는 고객과 상품 id 사용
        Long customerId = 2L;
        Long itemId = 1L;

        Customer customer = customerRepository.findById(customerId).orElseThrow();
        Item item = itemRepository.findById(itemId).orElseThrow();


        Order order = Order.builder()
                .customer(customer)
                .item(item)
                .orderQty(3)
                .orderPrice(item.getItemPrice())
                .orderState("ORDERED")
                .orderDeliState("READY")
                .orderDate(LocalDate.now())
                .cusAddr(customer.getCusAddr())   // ★ 고객 PK로 찾아온 주소 사용
                .build();

        orderRepository.save(order);
        log.info("INSERT => {}", order);
    }

//     READ
    @Test
    @Transactional
    @Commit
    public void testRead() {
        Long orderId = 1L;
        Optional<Order> result = orderRepository.findById(orderId);
        Order order = result.orElseThrow();

        log.info("READ => {}", order);
        log.info("READ.customer => {}", order.getCustomer().getCusComp());
        log.info("READ.item => {}", order.getItem().getItemName());
    }

//    // UPDATE
//    @Test
//    @Transactional
//    @Commit
//    public void testModify() {
//        Long orderId = 1L;
//        Order order = orderRepository.findById(orderId).orElseThrow();
//
//        order.changeOrderQty(5);
//        order.changeOrderState("COMPLETED");
//        order.changeOrderDeliState("DELIVERED");
//
//        orderRepository.save(order);
//        log.info("MODIFY => {}", order);
//    }

    // DELETE
//    @Test
//    @Transactional
//    @Commit
//    public void testDelete() {
//        Long orderId = 1L;
//        orderRepository.deleteById(orderId);
//        log.info("DELETE => id={}", orderId);
//    }
}
