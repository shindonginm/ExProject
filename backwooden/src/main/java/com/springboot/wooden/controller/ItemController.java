package com.springboot.wooden.controller;

import com.springboot.wooden.domain.Item;
import com.springboot.wooden.dto.ItemRequestDto;
import com.springboot.wooden.dto.ItemResponseDto;
import com.springboot.wooden.repository.ItemRepository;
import com.springboot.wooden.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plan/itemlist")
@RequiredArgsConstructor
public class ItemController {
    // 비즈니스 로직은 전부 Service로 위임
    private final ItemService service;
    // 드롭다운 조회용
    private final ItemRepository itemRepository;

    // Item 목록 조회
    @GetMapping
    public List<ItemResponseDto> getAll() {
        return service.getAll();
    }
    // Item 전체 아이템 목록 조회 (드롭다운용)
    @GetMapping("/main")
    public List<Item> getAllItems() {
        return itemRepository.findAllLight();
    }
    // Item 등록
    @PostMapping
    public ResponseEntity<ItemResponseDto> create(@RequestBody @Valid ItemRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }
    // Item 수정
    @PutMapping("/{itemNo}")
    public ResponseEntity<ItemResponseDto> update(@PathVariable Long itemNo,
                                                  @RequestBody @Valid ItemRequestDto dto) {
        return ResponseEntity.ok(service.update(itemNo, dto));
    }
    // Item 삭제 (성공 시 204 No Content)
    @DeleteMapping("/{itemNo}")
    public ResponseEntity<Void> delete(@PathVariable Long itemNo) {
        service.delete(itemNo);
        return ResponseEntity.noContent().build();
    }
}

// Item 에 대해 조회/등록/수정/삭제를 담당하는 REST 컨트롤러 서비스에 일을 시키는 포워더 역할만 수행
