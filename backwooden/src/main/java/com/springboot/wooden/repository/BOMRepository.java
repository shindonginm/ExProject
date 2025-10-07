package com.springboot.wooden.repository;

import com.springboot.wooden.domain.BOM;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @EntityGraph(attributePaths = {"item", "part"})
    List<BOM> findAllByItem_ItemNo(Long itemNo);

    // 이 부품을 쓰는 BOM 행이 존재하는가?
    boolean existsByPart_PartNo(Long partNo);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from BOM b where b.part.partNo = :partNo")
    int deleteByPartNo(@Param("partNo") Long partNo);
}
