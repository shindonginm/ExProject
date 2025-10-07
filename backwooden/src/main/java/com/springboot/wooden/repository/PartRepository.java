package com.springboot.wooden.repository;

import com.springboot.wooden.domain.Part;
import com.springboot.wooden.dto.PartStockResponseDto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PartRepository extends JpaRepository<Part, Long> {

    @Override
    @EntityGraph(attributePaths = {"buyer"})
    List<Part> findAll();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Part p set p.buyer = null where p.buyer.buyerNo = :buyerNo")
    int detachBuyerFromParts(@Param("buyerNo") Long buyerNo);

    @EntityGraph(attributePaths = "buyer")
    Optional<Part> findByBuyer_BuyerNo(Long buyerNo);

    boolean existsByBuyer_BuyerNo(Long buyerNo);

    @Query("""
        select new com.springboot.wooden.dto.PartStockResponseDto(
            p.partNo,
            p.partName,
            coalesce(ps.psQty, 0)
        )
        from Part p
        left join PartStock ps on ps.psNo = p.partNo
        order by p.partNo
    """)
    List<PartStockResponseDto> findPartStockView();
}
