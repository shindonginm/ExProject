package com.springboot.wooden.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 예측 엔진(파이썬 FastAPI)의 설정 값을 yml에서 읽어 들이는 바인딩 클래스.
 * ex :
 * 1) application.yml 에 값을 적는다.
 *    forecast:
 *      engine:
 *        base-url: http://localhost:5001
 *        connect-timeout-ms: 1500
 *        read-timeout-ms: 2500
 *        horizon: 12
 *
 * 2) 이 클래스를 빈으로 활성화.
 *    - @EnableConfigurationProperties(ForecastEngineProps.class)
 *      또는 @ConfigurationPropertiesScan 사용 (메인 설정에 추가)
 *
 * 3) 필요한 곳에서 생성자 주입으로 받아 사용.
 *    public MyService(ForecastEngineProps props) { ... }
 */

@Data
@ConfigurationProperties(prefix = "forecast.engine")
public class ForecastEngineProps {
    // 예측 엔진 베이스 URL
    private String baseUrl = "http://localhost:5001";
    // 외부 엔진 호출 시 연결 타임아웃(ms)
    private int connectTimeoutMs = 1500;
    // 응답 읽기 타임아웃(ms)
    private int readTimeoutMs = 2500;
    // 기본 예측 길이(주 단위 등), API 호출 시 기본값으로 사용 커스터 마이징 가능
    private int horizon = 12;
}

// application.yml의 forecast.engine.* 값을 자동으로 바인딩해 파이썬 예측 엔진의 기본 주소, 타임아웃,
// 기본 예측 길이(horizon)를 관리하는 설정 DTO. 즉 “하드코딩 금지, 설정은 yml로”를 지키게 관리.