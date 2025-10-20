package com.springboot.wooden.service;

import com.springboot.wooden.domain.Customer;
import com.springboot.wooden.domain.Item;
import com.springboot.wooden.domain.Order;
import com.springboot.wooden.dto.OrderRequestDto;
import com.springboot.wooden.dto.OrderResponseDto;
import com.springboot.wooden.dto.OrderStatusUpdateDto;
import com.springboot.wooden.repository.CustomerRepository;
import com.springboot.wooden.repository.ItemRepository;
import com.springboot.wooden.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ItemRepository itemRepository;
    private final ItemStockService itemStockService;

    // 안전 헬퍼
    private String safeItemName(Order o) {
        return (o.getItem() != null)
                ? o.getItem().getItemName()
                : o.getItemNameSnapshot();
    }

    private String safeCusComp(Order o) {
        return (o.getCustomer() != null)
                ? o.getCustomer().getCusComp()
                : o.getCusCompSnapshot();
    }

    private OrderResponseDto toDto(Order o) {
        int qty   = Math.max(0, o.getOrderQty());
        int price = Math.max(0, o.getOrderPrice());
        long total = 1L * qty * price; // int 오버플로우 방지

        return OrderResponseDto.builder()
                .orderNo(o.getOrderNo())
//                .cusComp(o.getCustomer().getCusComp())
//                .itemName(o.getItem().getItemName())
                .cusComp(safeCusComp(o))      // 스냅샷 백업 경로사용
                .itemName(safeItemName(o))    // 스냅샷 백업 경로사용
                .orderQty(qty)
                .orderPrice(price)
                .totalPrice(total)                    // 항상 계산해서 내려줌
                .orderState(o.getOrderState())
                .orderDeliState(o.getOrderDeliState())
                .deliveryDate(o.getDeliveryDate())
                .orderDate(o.getOrderDate())
                .cusAddr(o.getCusAddr())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllByCompany(String company) {
        return orderRepository.findAllByCustomer_CusComp(company)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public OrderResponseDto register(OrderRequestDto dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("고객 없음: " + dto.getCustomerId()));
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("상품 없음: " + dto.getItemId()));

        LocalDate s = dto.getOrderDate();
        LocalDate e = dto.getDeliveryDate();
        if (s == null || e == null) {
            throw new IllegalArgumentException("주문일자/납품일자 필수입니다.");
        }
        if (s.isAfter(e)) {
            throw new IllegalArgumentException("주문일자는 납품일자보다 빠를 수 없습니다.");
        }
        if ("승인완료" .equals(dto.getOrderState()) || "납품완료" .equals(dto.getOrderDeliState())) {
            throw new IllegalStateException("등록은 '승인대기', '납품대기' 상태로만 가능합니다");
        }

        Order saved = orderRepository.save(Order.builder()
                .customer(customer)
                .item(item)
                .orderQty(dto.getOrderQty())
                .orderPrice(dto.getOrderPrice())
                .orderState(dto.getOrderState())
                .orderDeliState(dto.getOrderDeliState())
                .orderDate(dto.getOrderDate())
                .deliveryDate(dto.getDeliveryDate())
                .cusAddr(dto.getCusAddr())
                .build());

        return toDto(saved);
    }

    @Override
    @Transactional
    public OrderResponseDto update(Long id, OrderRequestDto dto) {
        Order o = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("주문번호 없음: " + id));

        if ("납품완료".equals(o.getOrderDeliState())) {
            // 이미 완료된 주문이면 qty 또는 item 교체는 금지
            // dto.getItemId() 조회 전에 현재 아이템이 null일 가능성 고려(정책상 보통 null 아님)
            Long currentItemNo = (o.getItem() != null) ? o.getItem().getItemNo() : null;
            if (dto.getOrderQty() != o.getOrderQty() || !Objects.equals(dto.getItemId(), currentItemNo)) {
                throw new IllegalStateException("납품완료된 주문은 수량/상품을 수정할 수 없습니다.");
            }
            // 나머지 비핵심 필드(주소, 날짜 등)는 아래에서 업데이트 허용
        }

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("고객 없음: " + dto.getCustomerId()));
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("상품 없음: " + dto.getItemId()));

        LocalDate s = dto.getOrderDate();
        LocalDate e = dto.getDeliveryDate();
        if (s == null || e == null) {
            throw new IllegalArgumentException("주문일자/납품일자 필수입니다.");
        }
        if (s.isAfter(e)) {
            throw new IllegalArgumentException("주문일자는 납품일자보다 빠를 수 없습니다.");
        }

        o.changeCustomer(customer);
        o.changeItem(item);
        o.changeOrderQty(dto.getOrderQty());
        o.changeOrderPrice(dto.getOrderPrice());
        o.changeOrderState(dto.getOrderState());
        o.changeOrderDeliState(dto.getOrderDeliState());
        o.changeOrderDate(dto.getOrderDate());
        o.changeDeliveryDate(dto.getDeliveryDate());
        o.changeCusAddr(dto.getCusAddr());

        return toDto(o);
    }

    // 상태 값 변경 Impl
    @Override
    @Transactional
    public OrderResponseDto updateStatus(Long id, OrderStatusUpdateDto dto) {
        Order o = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("주문번호 없음: " + id));

        // 주문상태 변경 (재고 영향 없음)
        if (dto.getOrderState() != null && !dto.getOrderState().equals(o.getOrderState())) {
            o.changeOrderState(dto.getOrderState());
        }

        // 납품상태 변경 (재고 영향 있음)
        if (dto.getOrderDeliState() != null && !dto.getOrderDeliState().equals(o.getOrderDeliState())) {
            boolean beforeDone = "납품완료".equals(o.getOrderDeliState());
            boolean afterDone  = "납품완료".equals(dto.getOrderDeliState());
            if (afterDone && !"승인완료".equals(o.getOrderState())) {
                throw new IllegalStateException("주문상태가 '승인완료'가 아닌 주문은 납품완료로 변경할 수 없습니다");
            }

            // 완료 → 비완료로 되돌리기 금지
            if (beforeDone && !afterDone) {
                throw new IllegalStateException("이미 납품완료된 주문은 되돌릴 수 없습니다.");
            }

            // 최초로 완료가 되는 경우에만 출고
            // 여기: 최초로 완료가 되는 순간만 출고, 이미 완료 상태면 무시
            if (!beforeDone && afterDone) {
                int qty = o.getOrderQty();

                if (!o.isStockApplied()) {
                    try {
                        itemStockService.sell(o.getItem().getItemNo(), o.getOrderQty());
                        o.markStockApplied();
                    } catch (IllegalStateException e) {
                        String ItemName = (o.getItem() != null) ? o.getItem().getItemName() : o.getItemNameSnapshot();
                        throw new IllegalStateException(
                                "재고 부족 : [" + ItemName + "] 현재 재고가 부족합니다. 필요 수량 : " + qty + "개. 먼저 생산완료 또는 재고조정이 필요합니다."
                        );
                    }
                }
            } else if (beforeDone && afterDone) {
                // 이미 완료 상태에서 또 PATCH: 아무 것도 하지 않음
            }

            o.changeOrderDeliState(dto.getOrderDeliState());
        }
        // 강제 플러쉬
        orderRepository.flush();
        // 최신 엔티티 재조회
        Order updated = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("주문번호 없음 (없데이트 후) : " + id));

        return toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getCompletedOrders() {
        return orderRepository
                .findAllByOrderStateAndOrderDeliState("승인완료", "납품완료")
                .stream().map(this::toDto).toList();
    }


    @Override
    @Transactional
    public void delete(Long orderNo) {
        orderRepository.deleteById(orderNo);
    }
}
