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
    // 실제 로직은 서비스로 위임
    private  final PlanService planService;

    // Plan 목록 조회 : [응답 DTO에 목록(진행/완료 섞여있으면 서비스에서 정렬/필터)]
    @GetMapping
    public List<PlanResponseDTO> list() {
        return planService.getAll();
    }
    // Plan 완료 목록 조회
    @GetMapping("/completed")
    public List<PlanResponseDTO> completed() {
        return planService.getCompletedList();
    }
    // Plan 등록
    @PostMapping
    public ResponseEntity<PlanResponseDTO> createPlan(@RequestBody @Valid PlanRequestDTO dto) {
        PlanResponseDTO saved = planService.save(dto);
        return ResponseEntity.ok(saved); // 생성 후 조회해서 DTO로 반환
    }
    // Plan 수정
    @PutMapping("/{planNo}")
    public ResponseEntity<PlanResponseDTO> update(@PathVariable Long planNo,
                                                  @RequestBody @Valid PlanRequestDTO dto) {
        PlanResponseDTO updated = planService.update(planNo, dto);
        return ResponseEntity.ok(updated);
    }
    // Plan 상태 값 변경
    @PatchMapping("/{planNo}/status")
    public ResponseEntity<Void> patchStatus(@PathVariable Long planNo, @RequestBody Map<String, String> body) {
        String next = body.get("planState");
        planService.patchStatus(planNo, next);
        return ResponseEntity.noContent().build();
    }
    // Plan 삭제
    @DeleteMapping("/{planNo}")
    public ResponseEntity<Void> delete(@PathVariable Long planNo) {
        planService.delete(planNo);
        return ResponseEntity.noContent().build();
    }
}

// Plan 의 목록/완료목록/등록/수정/상태변경/삭제를 담당하는 REST 컨트롤러