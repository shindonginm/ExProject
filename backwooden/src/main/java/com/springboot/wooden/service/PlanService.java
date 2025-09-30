package com.springboot.wooden.service;

import com.springboot.wooden.dto.PlanRequestDTO;
import com.springboot.wooden.dto.PlanResponseDTO;
import java.util.List;

public interface PlanService {
    List<PlanResponseDTO> getAll();
    PlanResponseDTO getOne(Long planNo);
    PlanResponseDTO save(PlanRequestDTO dto);
    PlanResponseDTO update(Long planNo, PlanRequestDTO dto);
    void delete(Long planNo);
}
