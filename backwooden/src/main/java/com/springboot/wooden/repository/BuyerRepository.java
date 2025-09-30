package com.springboot.wooden.repository;

import com.springboot.wooden.domain.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BuyerRepository extends JpaRepository<Buyer, Long> {
}
