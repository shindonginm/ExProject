package com.springboot.wooden.repository;

import com.springboot.wooden.domain.BOM;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BOMRepository extends JpaRepository<BOM, Long> {

    @Override
    @EntityGraph(attributePaths = {"item", "part"})
    List<BOM> findAll();

    @EntityGraph(attributePaths = {"item", "part"})
    Optional<BOM> findById(Long bomId);

    boolean existsByItem_ItemNoAndPart_PartNo(Long itemNo, Long partNo);
    Optional<BOM> findByItem_ItemNoAndPart_PartNo(Long itemNo, Long partNo);
}
