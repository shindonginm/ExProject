package com.springboot.wooden.repository;

import com.springboot.wooden.domain.PartStock;
import org.springframework.data.jpa.repository.*;

public interface PartStockRepository extends JpaRepository<PartStock, Long> {

}
