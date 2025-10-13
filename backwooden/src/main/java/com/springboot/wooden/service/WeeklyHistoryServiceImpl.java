
package com.springboot.wooden.service;

import com.springboot.wooden.domain.Order;
import com.springboot.wooden.dto.WeeklyHistoryDto;
import com.springboot.wooden.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeeklyHistoryServiceImpl implements WeeklyHistoryService {

    private final OrderRepository orderRepository;

    @Override
    public WeeklyHistoryDto getWeeklyHistory(Long itemNo, int weeksBack) {
        // 기준: 가장 가까운 월요일을 end로
        LocalDate endMonday = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate startMonday = endMonday.minusWeeks(weeksBack);

        // 완료건만 DB에서 로드 (납품완료/승인완료 조건은 Repository @Query에서 처리)
        List<Order> orders = orderRepository.findCompletedByItemAndDeliveryDateBetween(
                itemNo, startMonday, endMonday
        );

        // 주차 버킷 초기화 (빈 주=0 보장)
        Map<LocalDate, Double> weeklyQty = new LinkedHashMap<>();
        for (LocalDate d = startMonday; !d.isAfter(endMonday); d = d.plusWeeks(1)) {
            weeklyQty.put(d, 0.0);
        }

        // 각 주문을 해당 주(월요일 시작)에 합산
        for (Order o : orders) {
            LocalDate dd = o.getDeliveryDate();
            if (dd == null) continue;
            LocalDate weekStart = dd.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            weeklyQty.computeIfPresent(weekStart, (k, v) -> v + o.getOrderQty());
        }

        // DTO 변환
        var history = weeklyQty.entrySet().stream()
                .map(e -> WeeklyHistoryDto.Point.builder()
                        .date(e.getKey().toString())
                        .qty(e.getValue())
                        .build())
                .collect(Collectors.toList());

        return WeeklyHistoryDto.builder()
                .itemNo(itemNo)
                .itemName(
                        orders.isEmpty() ? null :
                                Optional.ofNullable(orders.get(0).getItem())
                                        .map(i -> i.getItemName())
                                        .orElse(null)
                )
                .history(history)
                .build();
    }
}
