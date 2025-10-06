package com.springboot.wooden.service;

import com.springboot.wooden.domain.ItemStock;
import com.springboot.wooden.dto.ItemStockRequestDto;
import com.springboot.wooden.dto.ItemStockResponseDto;
import com.springboot.wooden.repository.ItemStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemStockServiceImpl implements ItemStockService {

    private final ItemStockRepository itemStockRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ItemStockResponseDto> getItemStocks() {
        return itemStockRepository.findAllWithItem().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemStockResponseDto getOne(Long isNo) {
        ItemStock s = itemStockRepository.findById(isNo)
                .orElseThrow(() -> new IllegalArgumentException("완제품 재고 없음: " + isNo));
        return toDto(s);
    }

    @Override
    @Transactional
    public ItemStockResponseDto adjust(ItemStockRequestDto request) {
        if (request.getDelta() == null || request.getDelta() == 0) {
            throw new IllegalArgumentException("delta는 0이 될 수 없습니다.");
        }
        Long itemNo = request.getItemNo(); // 공유 PK = isNo
        ItemStock s = itemStockRepository.findById(itemNo)
                .orElseThrow(() -> new IllegalStateException("재고행 없음: itemNo=" + itemNo));

        // 도메인 메서드로 수량 증감 (낙관적 락은 엔티티 @Version으로 적용)
        s.changeQty(request.getDelta());

        return toDto(s);
    }

    private ItemStockResponseDto toDto(ItemStock s) {
        int qty = (s.getIsQty() == null) ? 0 : s.getIsQty();
        String name = (s.getItem() != null) ? s.getItem().getItemName() : "(미지정)";
        // 공유 PK(@MapsId) 구조라면 isNo = itemNo
        Long isNo = (s.getIsNo() != null) ? s.getIsNo()
                : (s.getItem() != null ? s.getItem().getItemNo() : null);

        return ItemStockResponseDto.builder()
                .isNo(isNo)
                .itemName(name)
                .isQty(qty)
                .build();
    }
}
