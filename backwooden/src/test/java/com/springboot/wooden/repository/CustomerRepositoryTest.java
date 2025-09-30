package com.springboot.wooden.repository;

import com.springboot.wooden.domain.Customer;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
@Log4j2
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void testInsert() {
        for (int i = 0; i < 10; i++) {
            Customer customer = Customer.builder()
                    .cusComp("판매거래처" + i)     // ← 엔티티 필드명에 맞춰 수정
                    .cusName("담당자" + i)
                    .cusEmail("cust" + i + "@naver.com")
                    .cusPhone("01055557744")
                    .cusAddr("경기도 " + i)
                    .build();
            customerRepository.save(customer);
        }
    }

//    @Test
//    public void testRead() {
//        Long id = 3L;
//        Optional<Customer> result = customerRepository.findById(id);
//        Customer customer = result.orElseThrow();
//        log.info("READ => {}", customer);
//    }
//
//    @Test
//    public void testModify() {
//        Long id = 3L;
//        Customer customer = customerRepository.findById(id).orElseThrow();
//
//        // 엔티티에 change* 메서드가 있으면 그걸 사용
//        customer.changeCusComp("판매거래처(수정)");
//        customer.changeCusName("담당자(수정)");
//        customer.changeCusEmail("modified@naver.com");
//        customer.changeCusPhone("010-1234-5678");
//        customer.changeCusAddr("서울특별시 수정구");
//
//        customerRepository.save(customer);
//        log.info("MODIFY => {}", customer);
//    }
//
//    @Test
//    public void testDelete() {
//        Long id = 3L; // 존재하는 PK로 변경
//        customerRepository.deleteById(id);
//        log.info("DELETE => id={}", id);
//    }
}
