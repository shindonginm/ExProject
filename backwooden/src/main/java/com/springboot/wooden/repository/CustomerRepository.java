package com.springboot.wooden.repository;

import com.springboot.wooden.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // 기본 CRUD 는 JpaRepository 가 제공
    // ps : findByCompany 쿼리 메소드 추가도 가능

    Optional<Customer> findByCusComp(String company);
}
