package com.springboot.wooden.controller;

import com.springboot.wooden.dto.PlanRequestDTO;
import com.springboot.wooden.dto.PlanResponseDTO;
import com.springboot.wooden.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping ("/api/plan/planlist")
@RequiredArgsConstructor
public class PlanController {

    private  final PlanService planService;

    @GetMapping
    public List<PlanResponseDTO> list() {
        return planService.getAll();
    }

    @GetMapping("/completed")
    public List<PlanResponseDTO> completed() {
        return planService.getCompletedList();
    }

    // 등록
    @PostMapping
    public ResponseEntity<PlanResponseDTO> createPlan(@RequestBody @Valid PlanRequestDTO dto) {
        PlanResponseDTO saved = planService.save(dto);
        return ResponseEntity.ok(saved); // 생성 후 조회해서 DTO로 반환
    }

    @PutMapping("/{planNo}")
    public ResponseEntity<PlanResponseDTO> update(@PathVariable Long planNo,
                                                  @RequestBody @Valid PlanRequestDTO dto) {
        PlanResponseDTO updated = planService.update(planNo, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{planNo}/status")
    public ResponseEntity<Void> patchStatus(@PathVariable Long planNo, @RequestBody Map<String, String> body) {
        String next = body.get("planState");
        planService.patchStatus(planNo, next);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{planNo}")
    public ResponseEntity<Void> delete(@PathVariable Long planNo) {
        planService.delete(planNo);
        return ResponseEntity.noContent().build();
    }
}
