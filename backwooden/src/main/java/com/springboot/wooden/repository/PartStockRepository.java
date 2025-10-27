// backend/repository/PartStockRepository.java
package com.springboot.wooden.repository;

import com.springboot.wooden.domain.PartStock;
import org.springframework.data.jpa.repository.*;
import java.util.List;
import java.util.Optional;

public interface PartStockRepository extends JpaRepository<PartStock, Long> {

}
