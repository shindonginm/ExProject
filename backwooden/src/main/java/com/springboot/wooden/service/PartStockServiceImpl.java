package com.springboot.wooden.service;

import com.springboot.wooden.domain.PartStock;
import com.springboot.wooden.dto.PartStockRequestDto;
import com.springboot.wooden.dto.PartStockResponseDto;
import com.springboot.wooden.repository.PartRepository;
import com.springboot.wooden.repository.PartStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartStockServiceImpl implements PartStockService {

    private final PartStockRepository partStockRepository;
    private final PartRepository partRepository;

    @Override
    public List<PartStockResponseDto> getPartStocks() {
        return partRepository.findPartStockView();
    }

    @Transactional
    @Override
    public PartStockResponseDto adjust(PartStockRequestDto req) {
        if (req.getDelta() == null || req.getDelta() == 0) {
            throw new IllegalArgumentException("delta는 0일 수 없습니다.");
        }

        PartStock ps = partStockRepository.findById(req.getPartNo())
                .orElseThrow(() -> new IllegalArgumentException("재고가 없습니다. partNo=" + req.getPartNo()));

        // 엔티티 메서드 호출로 재고 증감
        ps.changeQty(req.getDelta());

        return toDto(ps);
    }


    private PartStockResponseDto toDto(PartStock ps) {
        return PartStockResponseDto.builder()
                .psNo(ps.getPsNo())
                .partName(ps.getPart().getPartName())
                .psQty(ps.getPsQty())
                .build();
    }
}
