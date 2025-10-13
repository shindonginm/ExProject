package com.springboot.wooden.controller;

import com.springboot.wooden.dto.WeeklyHistoryDto;
import com.springboot.wooden.service.WeeklyHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class WeeklyHistoryController {

    private final WeeklyHistoryService weeklyHistoryService;

    /**
     *  주간 판매 히스토리 조회 API
     *  - GET /api/history/weekly?itemNo=1&weeks=52
     *
     * @param itemNo   조회할 품목 번호
     * @param weeks    조회할 주 수 (기본 52주)
     * @return         WeeklyHistoryDto (빈 주 = 0 포함)
     */
    @GetMapping("/weekly")
    public WeeklyHistoryDto getWeeklyHistory(
            @RequestParam Long itemNo,
            @RequestParam(defaultValue = "52") int weeks
    ) {
        return weeklyHistoryService.getWeeklyHistory(itemNo, weeks);
    }
}
