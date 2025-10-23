package com.springboot.wooden.controller;

import com.springboot.wooden.dto.WeeklyHistoryDto;
import com.springboot.wooden.service.WeeklyHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class WeeklyHistoryController {
    // 실질 로직은 서비스로 위임
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
        // 파라미터 검증/전달만 하고 서비스에 맡김
        return weeklyHistoryService.getWeeklyHistory(itemNo, weeks);
    }
}

// 주간 판매 히스토리를 품목별로 조회하는 읽기 전용 REST 컨트롤러. 
// 빈 주차는 0으로 채운 DTO를 WeeklyHistoryService에서 만들어서 그대로 내보냄.