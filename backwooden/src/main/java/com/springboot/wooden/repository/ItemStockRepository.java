package com.springboot.wooden.repository;

import com.springboot.wooden.domain.ItemStock;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

public interface ItemStockRepository extends JpaRepository<ItemStock, Long> {

    @EntityGraph(attributePaths = "item")
    @Query("select s from ItemStock s")   // ← alias를 s로 (is는 예약어 혼동 방지)
    List<ItemStock> findAllWithItem();

    Optional<ItemStock> findByItem_ItemNo(Long itemNo);
}
