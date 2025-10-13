package com.springboot.wooden.service;

import com.springboot.wooden.dto.ForecastSeriesDto;
import com.springboot.wooden.dto.WeeklyHistoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ForecastServiceImpl implements ForecastService {

    private final WeeklyHistoryService weeklyHistoryService;
    private final ForecastEngineClient engine;

    @Override
    public ForecastSeriesDto getForecastSeries(Long itemNo, int horizonWeeks) {
        // 1) 과거 히스토리(빈 주=0) 확보
        WeeklyHistoryDto hist = weeklyHistoryService.getWeeklyHistory(itemNo, /*weeksBack*/ 52);

        // 2) 엔진에 맞는 HistPoint 변환
        List<ForecastSeriesDto.HistPoint> histPoints = hist.getHistory().stream()
                .map(p -> ForecastSeriesDto.HistPoint.builder()
                        .date(p.getDate())
                        .qty(p.getQty())
                        .build())
                .toList();

        // 3) ARIMA 엔진 호출 → 예측 결과
        List<ForecastSeriesDto.FcPoint> forecast = engine.forecastWeekly(histPoints, horizonWeeks);

        // 4) 응답 DTO 구성(히스토리+예측)
        return ForecastSeriesDto.builder()
                .itemNo(hist.getItemNo())
                .itemName(hist.getItemName())
                .history(histPoints)
                .forecast(forecast)
                .build();
    }
}
