package com.springboot.wooden.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS 설정.
 * - 대상 경로: /api/** 로 들어오는 모든 요청
 * - 허용 출처: "http://localhost:3000" (프론트 개발 서버)
 * - 허용 메서드: GET, POST, PUT, DELETE, PATCH, OPTIONS
 * - 허용/노출 헤더: 모두(*)
 * 결과: 브라우저의 사전 요청(Preflight)과 실제 요청이 차단되지 않음.
 */

@Configuration
public class CorsConfig {

    // WebMvcConfigurer 빈을 등록해 스프링 MVC CORS 정책을 커스터마이즈
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        // 익명 구현체로 필요한 메서드만 오버라이드
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        // 이 경로 패턴에만 CORS 적용
                        .allowedOrigins("http://localhost:3000")
                        // 브라우저가 사용할 수 있는 HTTP 메서드
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        // 요청에 포함될 수 있는 헤더(Authorization 등). 전부 허용
                        .allowedHeaders("*")
                        // 응답에서 브라우저가 접근할 수 있도록 노출할 헤더
                        .exposedHeaders("*");
            }
        };
    }
}

// 프론트(localhost:3000)에서 오는 요청에 대해 /api/** 경로만 CORS를 열어 주고,
// 모든 헤더와 주요 메서드(GET/POST/PUT/DELETE/PATCH/OPTIONS)를 허용하도록 스프링 MVC에 등록.
// 즉, 브라우저가 “교차 출처라서 못 보냄”이라고 만드는 관리자