package com.springboot.wooden.repository;

import com.springboot.wooden.domain.Part;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartRepository extends JpaRepository<Part, Long> {

    @EntityGraph(attributePaths = "buyer")                 // 추가
    Optional<Part> findByBuyer_BuyerNo(Long buyerNo);
    boolean existsByBuyer_BuyerNo(Long buyerNo);
//    existsByBuyer_BuyerNo(Long buyerNo)
//    = Part 테이블에서, buyer_no 컬럼 값이 있는지 확인
//    true, false로 반환
}
