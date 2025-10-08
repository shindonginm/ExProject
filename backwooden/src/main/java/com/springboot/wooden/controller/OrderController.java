package com.springboot.wooden.controller;

import com.springboot.wooden.dto.OrderListRow;
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

    private final OrderService orderService;

    // 전체 주문 조회
    @GetMapping
    public List<OrderResponseDto> getOrders() {
        return orderService.getAllOrders();
    }

    // 스냅샷 기반 주문리스트
    @GetMapping("/list")
    public List<OrderListRow> getOrderList() {
        return orderService.getOrderList(); // Repository의 findOrderListRows() 호출
    }

    @GetMapping("/{orderNo}")   // ✅ 단건은 orderNo로!
    public OrderResponseDto getOne(@PathVariable Long orderNo) {
        return orderService.getOne(orderNo);
    }

    @GetMapping("/company/{company}") // 회사명으로는 목록 반환
    public List<OrderResponseDto> getByCompany(@PathVariable String company) {
        return orderService.getAllByCompany(company);
    }

    // 주문 등록
    @PostMapping
    public OrderResponseDto addOrder(@RequestBody OrderRequestDto dto) {
        return orderService.register(dto); // saveOrder → register
    }

    // 주문 수정
    @PutMapping("/{orderNo}")
    public OrderResponseDto updateOrder(
            @PathVariable Long orderNo,
            @RequestBody OrderRequestDto dto) {
        return orderService.update(orderNo, dto);
    }

    // 상태 변경
    @PatchMapping("/{id}/status")
    public OrderResponseDto patchStatus(
            @PathVariable Long id,
            @RequestBody OrderStatusUpdateDto dto) {
        return orderService.updateStatus(id, dto);
    }

    // 상태 완료 목록 (경로 중복 제거!)
    @GetMapping("/completed")
    public List<OrderResponseDto> completed() {
        return orderService.getCompletedOrders();
    }

    // 주문 삭제
    @DeleteMapping("/{orderNo}")
    public void deleteOrder(@PathVariable Long orderNo) {
        orderService.delete(orderNo);
    }
}
