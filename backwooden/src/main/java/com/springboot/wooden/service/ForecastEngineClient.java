package com.springboot.wooden.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.wooden.dto.ForecastSeriesDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ForecastEngineClient {

    // 필요시 @Bean 주입으로 바꿔도 됨
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper om = new ObjectMapper();

    /**
     * 파이썬 엔진(예: http://localhost:5001/forecast/weekly) 호출.
     * body: { history:[{date,qty},...], horizon:n }
     * resp: [{date, mean, p10, p50, p90}, ...]
     */
    public List<ForecastSeriesDto.FcPoint> forecastWeekly(
            List<ForecastSeriesDto.HistPoint> history, int horizon) {

        String url = "http://localhost:5001/forecast/weekly"; // 필요시 yml로 분리
        Map<String, Object> body = new HashMap<>();
        body.put("history", history);
        body.put("horizon", horizon);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> res = restTemplate.postForEntity(
                url, new HttpEntity<>(body, headers), String.class);

        try {
            List<Map<String, Object>> raw = om.readValue(
                    res.getBody(), new TypeReference<>() {});
            List<ForecastSeriesDto.FcPoint> out = new ArrayList<>();
            for (Map<String, Object> m : raw) {
                out.add(ForecastSeriesDto.FcPoint.builder()
                        .date((String)m.get("date"))
                        .mean(toD(m.get("mean")))
                        .p10(toD(m.get("p10")))
                        .p50(toD(m.get("p50")))
                        .p90(toD(m.get("p90")))
                        .build());
            }
            return out;
        } catch (Exception e) {
            throw new RuntimeException("Forecast engine parse error", e);
        }
    }

    private static Double toD(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.doubleValue();
        return Double.valueOf(String.valueOf(o));
    }
}
