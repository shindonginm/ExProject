package com.springboot.wooden.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
// ForecastEngineProps 클래스를 구성요소로 등록해서
// application.yml 의 forecast.engine.* 값을 바인딩할 수 있게 함
@EnableConfigurationProperties(ForecastEngineProps.class)
public class RestClientConfig {

    /**
     * 예측 엔진 호출용 RestTemplate 빈.
     * - 연결/응답 타임아웃을 ForecastEngineProps 값으로 설정.
     * - 한 번 생성해 애플리케이션 전역에서 주입 받아 재사용.
     */
    @Bean
    public RestTemplate forecastRestTemplate(ForecastEngineProps props) {
        // 가장 간단한 HTTP 요청 팩토리. 타임아웃 세팅 가능
        var f = new SimpleClientHttpRequestFactory();
        // 소켓 연결 타임아웃(ms)
        f.setConnectTimeout(props.getConnectTimeoutMs());
        // 응답 읽기 타임아웃(ms)
        f.setReadTimeout(props.getReadTimeoutMs());
        // 위 팩토리를 사용하도록 설정된 RestTemplate 생성
        return new RestTemplate(f);
    }
}

// RestClientConfig: 그 설정을 써서 실제로 HTTP 클라이언트 빈(RestClient/RestTemplate)을 만드는 파일.
// “한 번 만들어 애플리케이션 전체에서 재사용
