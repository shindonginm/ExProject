package com.springboot.wooden.service;

import com.springboot.wooden.dto.OrderRequestDto;
import com.springboot.wooden.dto.OrderResponseDto;
import com.springboot.wooden.dto.OrderStatusUpdateDto;

import java.util.List;

public interface OrderService {
    List<OrderResponseDto> getAllOrders();                    // 전체 조회
    List<OrderResponseDto> getAllByCompany(String company);   // 판매처명으로 목록 조회
    OrderResponseDto register(OrderRequestDto dto);           // 등록
    OrderResponseDto update(Long id, OrderRequestDto dto);    // 수정
    OrderResponseDto updateStatus(Long id, OrderStatusUpdateDto dto);
    List<OrderResponseDto> getCompletedOrders();              // 상태 값 변경 후 이동
    void delete(Long id);                                     // 삭제
}
