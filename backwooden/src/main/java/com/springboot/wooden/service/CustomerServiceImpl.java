package com.springboot.wooden.service;

import com.springboot.wooden.domain.Customer;
import com.springboot.wooden.dto.CustomerRequestDto;
import com.springboot.wooden.dto.CustomerResponseDto;
import com.springboot.wooden.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;

    @Override
    public List<CustomerResponseDto> getAll() {
        return repository.findAll().stream()
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
    public CustomerResponseDto getOne(Long id) {
        Customer c = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));

        return CustomerResponseDto.builder()
                .cusNo(c.getCusNo())
                .cusComp(c.getCusComp())
                .cusName(c.getCusName())
                .cusEmail(c.getCusEmail())
                .cusPhone(c.getCusPhone())
                .cusAddr(c.getCusAddr())
                .build();
    }

    @Override
    public Optional<CustomerResponseDto> getByCompany(String company) {
        return repository.findByCusComp(company)
                .map(c -> CustomerResponseDto.builder()
                        .cusNo(c.getCusNo())
                        .cusComp(c.getCusComp())
                        .cusName(c.getCusName())
                        .cusEmail(c.getCusEmail())
                        .cusPhone(c.getCusPhone())
                        .cusAddr(c.getCusAddr())
                        .build());
    }

    @Override
    @Transactional
    public CustomerResponseDto register(CustomerRequestDto dto) {
        Customer saved = repository.save(Customer.builder()
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
        Customer c = repository.findById(id)
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

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
