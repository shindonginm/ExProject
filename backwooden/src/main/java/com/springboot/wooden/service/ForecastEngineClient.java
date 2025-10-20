package com.springboot.wooden.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.wooden.config.ForecastEngineProps;
import com.springboot.wooden.dto.ForecastSeriesDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ForecastEngineClient {

    private final RestTemplate forecastRestTemplate; // RestClientConfig에서 만든 빈
    private final ForecastEngineProps props;         // 분리한 설정 빈
    private final ObjectMapper om = new ObjectMapper();

    public List<ForecastSeriesDto.FcPoint> forecastWeekly(
            List<ForecastSeriesDto.HistPoint> history, int horizon) {

        String url = props.getBaseUrl() + "/forecast/weekly";
        Map<String, Object> body = Map.of(
                "history", history,
                "horizon", horizon > 0 ? horizon : props.getHorizon()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> res = forecastRestTemplate.postForEntity(
                url, new HttpEntity<>(body, headers), String.class);

        try {
            List<Map<String, Object>> raw = om.readValue(
                    res.getBody(), new TypeReference<>(){});
            List<ForecastSeriesDto.FcPoint> out = new ArrayList<>();
            for (Map<String, Object> m : raw) {
                out.add(ForecastSeriesDto.FcPoint.builder()
                        .date((String) m.get("date"))
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
