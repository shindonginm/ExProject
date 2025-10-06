package com.springboot.wooden.repository;

import com.springboot.wooden.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByItemName(String ItemName);

    List<Item> findAllByItemName(String ItemName);
}
