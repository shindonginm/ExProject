package com.springboot.wooden.service;

import com.springboot.wooden.domain.Customer;
import com.springboot.wooden.domain.Item;
import com.springboot.wooden.domain.Order;
import com.springboot.wooden.dto.OrderRequestDto;
import com.springboot.wooden.dto.OrderResponseDto;
import com.springboot.wooden.repository.CustomerRepository;
import com.springboot.wooden.repository.ItemRepository;
import com.springboot.wooden.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(o -> OrderResponseDto.builder()
                        .orderNo(o.getOrderNo())
                        .cusComp(o.getCustomer().getCusComp())
                        .itemName(o.getItem().getItemName())
                        .orderQty(o.getOrderQty())
                        .orderPrice(o.getOrderPrice())
                        .orderState(o.getOrderState())
                        .orderDeliState(o.getOrderDeliState())
                        .deliveryDate(o.getDeliveryDate())
                        .orderDate(o.getOrderDate())
                        .cusAddr(o.getCusAddr())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOne(Long orderNo) {
        Order o = orderRepository.findById(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("주문번호 없음: " + orderNo));

        return OrderResponseDto.builder()
                .orderNo(o.getOrderNo())
                .cusComp(o.getCustomer().getCusComp())
                .itemName(o.getItem().getItemName())
                .orderQty(o.getOrderQty())
                .orderPrice(o.getOrderPrice())
                .orderState(o.getOrderState())
                .orderDeliState(o.getOrderDeliState())
                .deliveryDate(o.getDeliveryDate())
                .orderDate(o.getOrderDate())
                .cusAddr(o.getCusAddr())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllByCompany(String company) {
        return orderRepository.findAllByCustomer_CusComp(company)
                .stream()
                .map(o -> OrderResponseDto.builder()
                        .orderNo(o.getOrderNo())
                        .cusComp(o.getCustomer().getCusComp())
                        .itemName(o.getItem().getItemName())
                        .orderQty(o.getOrderQty())
                        .orderPrice(o.getOrderPrice())
                        .orderState(o.getOrderState())
                        .orderDeliState(o.getOrderDeliState())
                        .deliveryDate(o.getDeliveryDate())
                        .orderDate(o.getOrderDate())
                        .cusAddr(o.getCusAddr())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public OrderResponseDto register(OrderRequestDto dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("고객 없음: " + dto.getCustomerId()));
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("상품 없음: " + dto.getItemId()));

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

        return OrderResponseDto.builder()
                .orderNo(saved.getOrderNo())
                .cusComp(saved.getCustomer().getCusComp())
                .itemName(saved.getItem().getItemName())
                .orderQty(saved.getOrderQty())
                .orderPrice(saved.getOrderPrice())
                .orderState(saved.getOrderState())
                .orderDeliState(saved.getOrderDeliState())
                .deliveryDate(saved.getDeliveryDate())
                .orderDate(saved.getOrderDate())
                .cusAddr(saved.getCusAddr())
                .build();
    }

    @Override
    @Transactional
    public OrderResponseDto update(Long id, OrderRequestDto dto) {
        Order o = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("주문번호 없음: " + id));

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("고객 없음: " + dto.getCustomerId()));
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("상품 없음: " + dto.getItemId()));

        o.changeCustomer(customer);
        o.changeItem(item);
        o.changeOrderQty(dto.getOrderQty());
        o.changeOrderPrice(dto.getOrderPrice());
        o.changeOrderState(dto.getOrderState());
        o.changeOrderDeliState(dto.getOrderDeliState());
        o.changeOrderDate(dto.getOrderDate());
        o.changeDeliveryDate(dto.getDeliveryDate());
        o.changeCusAddr(dto.getCusAddr());
        // 변경감지로 UPDATE 반영

        return OrderResponseDto.builder()
                .orderNo(o.getOrderNo())
                .cusComp(o.getCustomer().getCusComp())
                .itemName(o.getItem().getItemName())
                .orderQty(o.getOrderQty())
                .orderPrice(o.getOrderPrice())
                .orderState(o.getOrderState())
                .orderDeliState(o.getOrderDeliState())
                .deliveryDate(o.getDeliveryDate())
                .orderDate(o.getOrderDate())
                .cusAddr(o.getCusAddr())
                .build();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        orderRepository.deleteById(id);
    }
}
