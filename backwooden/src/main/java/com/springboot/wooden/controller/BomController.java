package com.springboot.wooden.controller;

import com.springboot.wooden.dto.BomRequestDto;
import com.springboot.wooden.dto.BomResponseDto;
import com.springboot.wooden.service.BomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plan/bom")   // BOM 리소스의 컬렉션 엔드포인트
@RequiredArgsConstructor             // 생성자 주입(서비스 final 필드 주입)
public class BomController {
    // 비즈니스 로직은 전부 Service로 위임
    private final BomService service;

    // BOM 목록 조회
    @GetMapping
    public List<BomResponseDto> list() {
        return service.getAll();
    }
    // 개별 BOM 단건 조회
    @GetMapping("/{bomId}")
    public BomResponseDto get(@PathVariable Long bomId) {
        return service.getOne(bomId);
    }
    // BOM 등록
    @PostMapping
    public ResponseEntity<BomResponseDto> create(@RequestBody @Valid BomRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }
    // BOM 수정
    @PutMapping("/{bomId}")
    public ResponseEntity<BomResponseDto> update(@PathVariable Long bomId,
                                                 @RequestBody @Valid BomRequestDto dto) {
        return ResponseEntity.ok(service.update(bomId, dto));
    }
    // BOM 삭제
    @DeleteMapping("/{bomId}")
    public ResponseEntity<Void> delete(@PathVariable Long bomId) {
        service.delete(bomId);
        return ResponseEntity.noContent().build();
    }
}

// BOM 에 대해 조회/등록/수정/삭제를 담당하는 REST 컨트롤러 서비스에 일을 시키는 포워더 역할만 수행