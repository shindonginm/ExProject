package com.springboot.wooden.repository;

import com.springboot.wooden.domain.Part;
import com.springboot.wooden.dto.PartStockResponseDto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PartRepository extends JpaRepository<Part, Long> {

    @Override
    @EntityGraph(attributePaths = {"buyer"})
    List<Part> findAll();

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
