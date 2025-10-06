// backend/repository/PartStockRepository.java
package com.springboot.wooden.repository;

import com.springboot.wooden.domain.PartStock;
import org.springframework.data.jpa.repository.*;
import java.util.List;
import java.util.Optional;

public interface PartStockRepository extends JpaRepository<PartStock, Long> {

    @EntityGraph(attributePaths = "part")
    @Query("select ps from PartStock ps")
    List<PartStock> findAllWithPart();

//    공유 PK가 같음
    Optional<PartStock> findByPart_PartNo(Long partNo);
}
