package com.springboot.wooden.controller;

import com.springboot.wooden.dto.OrderRequestDto;
import com.springboot.wooden.dto.OrderResponseDto;
import com.springboot.wooden.dto.OrderStatusUpdateDto;
import com.springboot.wooden.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // REST API 컨트롤러 (JSON 반환)
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    // 비즈니스 로직은 전부 Service로 위임
    private final OrderService orderService;

    // Order 목록 조회
    @GetMapping
    public List<OrderResponseDto> getOrders() {
        return orderService.getAllOrders();
    }
    // 판매 거래처명으로 조회
    @GetMapping("/company/{company}")
    public List<OrderResponseDto> getByCompany(@PathVariable String company) {
        return orderService.getAllByCompany(company);
    }
    // Order 등록
    @PostMapping
    public OrderResponseDto addOrder(@RequestBody OrderRequestDto dto) {
        return orderService.register(dto); // saveOrder → register
    }
    // Order 수정
    @PutMapping("/{orderNo}")
    public OrderResponseDto updateOrder(
            @PathVariable Long orderNo,
            @RequestBody OrderRequestDto dto) {
        return orderService.update(orderNo, dto);
    }
    // Order 상태 값 변경
    @PatchMapping("/{id}/status")
    public OrderResponseDto patchStatus(
            @PathVariable Long id,
            @RequestBody OrderStatusUpdateDto dto) {
        return orderService.updateStatus(id, dto);
    }
    // 완료된 Order 목록 조회
    @GetMapping("/completed")
    public List<OrderResponseDto> completed() {
        return orderService.getCompletedOrders();
    }
    // Order 삭제
    @DeleteMapping("/{orderNo}")
    public void deleteOrder(@PathVariable Long orderNo) {
        orderService.delete(orderNo);
    }
}

// Order CRUD + 상태변경 + 조건조회(회사명, 완료건)를 OrderService에 위임해 처리하는 REST 컨트롤러 서비스에 일을 시키는 포워더 역할만 수행