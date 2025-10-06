package com.springboot.wooden.service;

import com.springboot.wooden.domain.BOM;
import com.springboot.wooden.domain.Item;
import com.springboot.wooden.domain.Part;
import com.springboot.wooden.dto.BomRequestDto;
import com.springboot.wooden.dto.BomResponseDto;
import com.springboot.wooden.repository.BOMRepository;
import com.springboot.wooden.repository.ItemRepository;
import com.springboot.wooden.repository.PartRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BomServiceImpl implements BomService {

    private final BOMRepository bomRepository;
    private final ItemRepository itemRepository;
    private final PartRepository partRepository;

    @Transactional(readOnly = true)
    @Override
    public List<BomResponseDto> getAll() {
        return bomRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public BomResponseDto getOne(Long bomId) {
        BOM b = bomRepository.findById(bomId)
                .orElseThrow(() -> new EntityNotFoundException("BOM not found: " + bomId));
        return toDto(b);
    }

    @Transactional
    @Override
    public BomResponseDto create(BomRequestDto dto) {
        if (dto.getQtyPerItem() <= 0) throw new IllegalArgumentException("qtyPerItem must be > 0");
        if (bomRepository.existsByItem_ItemNoAndPart_PartNo(dto.getItemNo(), dto.getPartNo()))
            throw new IllegalStateException("이미 존재하는 BOM입니다. (itemNo=%d, partNo=%d)".formatted(dto.getItemNo(), dto.getPartNo()));

        Item item = itemRepository.findById(dto.getItemNo())
                .orElseThrow(() -> new EntityNotFoundException("Item not found: " + dto.getItemNo()));
        Part part = partRepository.findById(dto.getPartNo())
                .orElseThrow(() -> new EntityNotFoundException("Part not found: " + dto.getPartNo()));

        BOM saved = bomRepository.save(BOM.builder()
                .item(item)
                .part(part)
                .qtyPerItem(dto.getQtyPerItem())
                .build());

        return toDto(saved);
    }

    @Transactional
    @Override
    public BomResponseDto update(Long bomId, BomRequestDto dto) {
        if (dto.getQtyPerItem() <= 0) throw new IllegalArgumentException("qtyPerItem must be > 0");

        BOM b = bomRepository.findById(bomId)
                .orElseThrow(() -> new EntityNotFoundException("BOM not found: " + bomId));

        Item item = itemRepository.findById(dto.getItemNo())
                .orElseThrow(() -> new EntityNotFoundException("Item not found: " + dto.getItemNo()));
        Part part = partRepository.findById(dto.getPartNo())
                .orElseThrow(() -> new EntityNotFoundException("Part not found: " + dto.getPartNo()));

        bomRepository.findByItem_ItemNoAndPart_PartNo(dto.getItemNo(), dto.getPartNo())
                .ifPresent(dup -> {
                    if (!dup.getBomId().equals(bomId)) {
                        throw new IllegalStateException("다른 BOM과 중복됩니다. (itemNo=%d, partNo=%d)".formatted(dto.getItemNo(), dto.getPartNo()));
                    }
                });

        b.changeItem(item);
        b.changePart(part);
        b.changeQtyPerItem(dto.getQtyPerItem());

        return toDto(b);
    }

    @Transactional
    @Override
    public void delete(Long bomId) {
        if (!bomRepository.existsById(bomId)) {
            throw new EntityNotFoundException("BOM not found: " + bomId);
        }
        bomRepository.deleteById(bomId);
    }

    private BomResponseDto toDto(BOM b) {
        return BomResponseDto.builder()
                .bomId(b.getBomId())
                .itemName(b.getItem().getItemName())
                .partName(b.getPart().getPartName())
                .qtyPerItem(b.getQtyPerItem())
                .build();
    }
}



