package com.springboot.wooden.service;

import com.springboot.wooden.domain.Item;
import com.springboot.wooden.domain.Plan;
import com.springboot.wooden.dto.PlanRequestDTO;
import com.springboot.wooden.dto.PlanResponseDTO;
import com.springboot.wooden.repository.ItemRepository;
import com.springboot.wooden.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
    private final ItemRepository itemRepository;

    // 목록 조회 (정렬 X)
    @Override
    @Transactional(readOnly = true)
    public List<PlanResponseDTO> getAll() {
        return planRepository.findAll()
                .stream()
                .map(p -> PlanResponseDTO.builder()
                        .planNo(p.getPlanNo())
                        .itemName(p.getItem().getItemName())
                        .planState(p.getPlanState())
                        .planStartDate(p.getPlanStartDate())
                        .planEndDate(p.getPlanEndDate())
                        .planQty(p.getPlanQty())
                        .build())
                .toList();
    }

    // 단건
    @Override
    @Transactional(readOnly = true)
    public PlanResponseDTO getOne(Long planNo) {
        Plan p = planRepository.findById(planNo)
                .orElseThrow(() -> new IllegalArgumentException("생산계획 없음: " + planNo));

        return PlanResponseDTO.builder()
                .planNo(p.getPlanNo())
                .itemName(p.getItem().getItemName())
                .planState(p.getPlanState())
                .planStartDate(p.getPlanStartDate())
                .planEndDate(p.getPlanEndDate())
                .planQty(p.getPlanQty())
                .build();
    }

    // 등록
    @Override
    @Transactional
    public PlanResponseDTO save(PlanRequestDTO dto) {
        Item item = itemRepository.findById(dto.getItemNo())
                .orElseThrow(() -> new IllegalArgumentException("상품 없음: " + dto.getItemNo()));

        Plan saved = planRepository.save(Plan.builder()
                .item(item) // save 전에 FK 세팅
                .planState(dto.getPlanState())
                .planStartDate(dto.getPlanStartDate())
                .planEndDate(dto.getPlanEndDate())
                .planQty(dto.getPlanQty())
                .build());

        return PlanResponseDTO.builder()
                .planNo(saved.getPlanNo())
                .itemName(saved.getItem().getItemName())
                .planState(saved.getPlanState())
                .planStartDate(saved.getPlanStartDate())
                .planEndDate(saved.getPlanEndDate())
                .planQty(saved.getPlanQty())
                .build();
    }

    // 수정
    @Override
    @Transactional
    public PlanResponseDTO update(Long planNo, PlanRequestDTO dto) {
        Plan p = planRepository.findById(planNo)
                .orElseThrow(() -> new IllegalArgumentException("생산계획 없음: " + planNo));

        Item item = itemRepository.findById(dto.getItemNo())
                .orElseThrow(() -> new IllegalArgumentException("상품 없음: " + dto.getItemNo()));

        p.changeItem(item);
        p.changePlanState(dto.getPlanState());
        p.changePlanStartDate(dto.getPlanStartDate());
        p.changePlanEndDate(dto.getPlanEndDate());
        p.changePlanQty(dto.getPlanQty());

        return PlanResponseDTO.builder()
                .planNo(p.getPlanNo())
                .itemName(p.getItem().getItemName())
                .planState(p.getPlanState())
                .planStartDate(p.getPlanStartDate())
                .planEndDate(p.getPlanEndDate())
                .planQty(p.getPlanQty())
                .build();
    }

    // 삭제
    @Override
    @Transactional
    public void delete(Long planNo) {
        planRepository.deleteById(planNo);
    }
}
