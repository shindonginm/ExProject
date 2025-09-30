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

    // 목록 (Order와 같은 패턴: 트랜잭션 안에서 DTO 변환 완료)
    @Override
    @Transactional(readOnly = true)
    public List<BomResponseDto> getAll() {
        return bomRepository.findAll()
                .stream()
                .map(b -> BomResponseDto.builder()
                        .bomId(b.getBomId())                               // UI엔 안 보여줘도 조작용으로 필요
                        .itemName(b.getItem().getItemName())               // 프론트 표시용 name
                        .partName(b.getPart().getPartName())               // 프론트 표시용 name
                        .qtyPerItem(b.getQtyPerItem())
                        .build())
                .toList();
    }

    // 단건
    @Override
    @Transactional(readOnly = true)
    public BomResponseDto getOne(Long bomId) {
        BOM b = bomRepository.findById(bomId)
                .orElseThrow(() -> new EntityNotFoundException("BOM not found: " + bomId));

        return BomResponseDto.builder()
                .bomId(b.getBomId())
                .itemName(b.getItem().getItemName())
                .partName(b.getPart().getPartName())
                .qtyPerItem(b.getQtyPerItem())
                .build();
    }

    // 등록
    @Override
    @Transactional
    public BomResponseDto create(BomRequestDto dto) {
        if (dto.getQtyPerItem() == null || dto.getQtyPerItem() <= 0) {
            throw new IllegalArgumentException("qtyPerItem must be > 0");
        }
        if (bomRepository.existsByItem_ItemNoAndPart_PartNo(dto.getItemNo(), dto.getPartNo())) {
            throw new IllegalStateException("이미 존재하는 BOM입니다. (itemNo=%d, partNo=%d)".formatted(dto.getItemNo(), dto.getPartNo()));
        }

        Item item = itemRepository.findById(dto.getItemNo())
                .orElseThrow(() -> new EntityNotFoundException("Item not found: " + dto.getItemNo()));
        Part part = partRepository.findById(dto.getPartNo())
                .orElseThrow(() -> new EntityNotFoundException("Part not found: " + dto.getPartNo()));

        BOM saved = bomRepository.save(BOM.builder()
                .item(item)
                .part(part)
                .qtyPerItem(dto.getQtyPerItem())
                .build());

        return BomResponseDto.builder()
                .bomId(saved.getBomId())
                .itemName(saved.getItem().getItemName())
                .partName(saved.getPart().getPartName())
                .qtyPerItem(saved.getQtyPerItem())
                .build();
    }

    // 수정
    @Override
    @Transactional
    public BomResponseDto update(Long bomId, BomRequestDto dto) {
        BOM b = bomRepository.findById(bomId)
                .orElseThrow(() -> new EntityNotFoundException("BOM not found: " + bomId));

        Item item = itemRepository.findById(dto.getItemNo())
                .orElseThrow(() -> new EntityNotFoundException("Item not found: " + dto.getItemNo()));
        Part part = partRepository.findById(dto.getPartNo())
                .orElseThrow(() -> new EntityNotFoundException("Part not found: " + dto.getPartNo()));

        // 다른 레코드와의 (item, part) 유니크 충돌 방지
        bomRepository.findByItem_ItemNoAndPart_PartNo(dto.getItemNo(), dto.getPartNo())
                .ifPresent(dup -> {
                    if (!dup.getBomId().equals(bomId)) {
                        throw new IllegalStateException("다른 BOM과 중복됩니다. (itemNo=%d, partNo=%d)".formatted(dto.getItemNo(), dto.getPartNo()));
                    }
                });

        // 도메인 메서드로 변경
        b.changeItem(item);
        b.changePart(part);
        b.changeQtyPerItem(dto.getQtyPerItem());

        return BomResponseDto.builder()
                .bomId(b.getBomId())
                .itemName(b.getItem().getItemName())
                .partName(b.getPart().getPartName())
                .qtyPerItem(b.getQtyPerItem())
                .build();
    }

    // 삭제
    @Override
    @Transactional
    public void delete(Long bomId) {
        if (!bomRepository.existsById(bomId)) {
            throw new EntityNotFoundException("BOM not found: " + bomId);
        }
        bomRepository.deleteById(bomId);
    }
}
