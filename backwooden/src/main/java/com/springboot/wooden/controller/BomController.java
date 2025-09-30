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
@RequestMapping("/api/plan/bom")
@RequiredArgsConstructor
public class BomController {

    private final BomService service;

    @GetMapping
    public List<BomResponseDto> list() {
        return service.getAll();
    }

    @GetMapping("/{bomId}")
    public BomResponseDto get(@PathVariable Long bomId) {
        return service.getOne(bomId);
    }

    @PostMapping
    public ResponseEntity<BomResponseDto> create(@RequestBody @Valid BomRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{bomId}")
    public ResponseEntity<BomResponseDto> update(@PathVariable Long bomId,
                                                 @RequestBody @Valid BomRequestDto dto) {
        return ResponseEntity.ok(service.update(bomId, dto));
    }

    @DeleteMapping("/{bomId}")
    public ResponseEntity<Void> delete(@PathVariable Long bomId) {
        service.delete(bomId);
        return ResponseEntity.noContent().build();
    }
}
