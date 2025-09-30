package com.springboot.wooden.repository;

import com.springboot.wooden.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    // JpaRepository에 의해 findById, findAll, save 등 기본 메소드 제공

    // 상품명으로 조회
    Optional<Item> findByItemName(String ItemName);

    // 같은 이름이 여러 개일 수 있으니 리스트로 받을 수도 있음
    List<Item> findAllByItemName(String ItemName);
}
