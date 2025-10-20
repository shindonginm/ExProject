package com.springboot.wooden.service;

import com.springboot.wooden.domain.Customer;
import com.springboot.wooden.domain.Order;
import com.springboot.wooden.dto.CustomerRequestDto;
import com.springboot.wooden.dto.CustomerResponseDto;
import com.springboot.wooden.repository.CustomerRepository;
import com.springboot.wooden.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<CustomerResponseDto> getAll() {
        return customerRepository.findAll().stream()
                .map(c -> CustomerResponseDto.builder()
                        .cusNo(c.getCusNo())
                        .cusComp(c.getCusComp())
                        .cusName(c.getCusName())
                        .cusEmail(c.getCusEmail())
                        .cusPhone(c.getCusPhone())
                        .cusAddr(c.getCusAddr())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public CustomerResponseDto register(CustomerRequestDto dto) {
        Customer saved = customerRepository.save(Customer.builder()
                .cusComp(dto.getCusComp())
                .cusName(dto.getCusName())
                .cusEmail(dto.getCusEmail())
                .cusPhone(dto.getCusPhone())
                .cusAddr(dto.getCusAddr())
                .build());

        return CustomerResponseDto.builder()
                .cusNo(saved.getCusNo())
                .cusComp(saved.getCusComp())
                .cusName(saved.getCusName())
                .cusEmail(saved.getCusEmail())
                .cusPhone(saved.getCusPhone())
                .cusAddr(saved.getCusAddr())
                .build();
    }

    @Override
    @Transactional
    public CustomerResponseDto update(Long id, CustomerRequestDto dto) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));

        c.changeCusComp(dto.getCusComp());
        c.changeCusName(dto.getCusName());
        c.changeCusEmail(dto.getCusEmail());
        c.changeCusPhone(dto.getCusPhone());
        c.changeCusAddr(dto.getCusAddr());

        return CustomerResponseDto.builder()
                .cusNo(c.getCusNo())
                .cusComp(c.getCusComp())
                .cusName(c.getCusName())
                .cusEmail(c.getCusEmail())
                .cusPhone(c.getCusPhone())
                .cusAddr(c.getCusAddr())
                .build();
    }

    @Transactional
    @Override
    public void delete(Long cusNo) {
        boolean hasUncompleted =
                orderRepository.existsByCustomer_CusNoAndOrderDeliStateNot(cusNo, "납품완료");

        if (hasUncompleted) {
            throw new IllegalStateException("삭제 불가: 납품이 완료되지 않은 주문이 존재합니다.");
        }

        List<Order> orders = orderRepository.findAllByCustomer_CusNo(cusNo);
        for (Order order : orders) {
            // 완료된 주문만 스냅샷 보존 후 FK 해제
            if ("승인완료".equals(order.getOrderState())
                    && "납품완료".equals(order.getOrderDeliState())) {

                // FK 끊기 전에 스냅샷 채우기
                order.snapshotItemBeforeUnlink();

                // FK 해제
                order.changeCustomer(null);
            }
        }

        customerRepository.deleteById(cusNo);
    }
}