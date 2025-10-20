package com.springboot.wooden.service;

import com.springboot.wooden.dto.PlanRequestDTO;
import com.springboot.wooden.dto.PlanResponseDTO;
import java.util.List;

public interface PlanService {
    List<PlanResponseDTO> getAll();          // 진행중만
    List<PlanResponseDTO> getCompletedList();// 생산완료만
    PlanResponseDTO save(PlanRequestDTO dto);
    PlanResponseDTO update(Long planNo, PlanRequestDTO dto);
    void patchStatus(Long planNo, String next);
    void delete(Long planNo);

}
