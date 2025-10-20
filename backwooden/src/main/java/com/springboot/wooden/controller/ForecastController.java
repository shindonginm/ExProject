package com.springboot.wooden.controller;

import com.springboot.wooden.dto.ForecastSeriesDto;
import com.springboot.wooden.service.ForecastService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forecast")
@RequiredArgsConstructor
public class ForecastController {
    // 비즈니스 로직은 전부 Service로 위임
    private final ForecastService forecastService;

    /**
     * 예: GET /api/forecast/series?itemNo=1&h=12
     * itemNo = item번호 h = 예측할 주의 수 (적지 않으면 12주가 디폴트)
     * - history(주간, 빈 주=0) + forecast(mean/p10/p50/p90) 동시 반환
     */
    @GetMapping("/series")
    public ForecastSeriesDto getSeries(@RequestParam Long itemNo,
                                       @RequestParam(name = "h", defaultValue = "12") int horizonWeeks) {
        return forecastService.getForecastSeries(itemNo, horizonWeeks);
    }
}
