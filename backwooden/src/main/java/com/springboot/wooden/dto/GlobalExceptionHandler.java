package com.springboot.wooden.dto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 400: 잘못된 입력/비즈니스 검증 실패
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return badRequest("BAD_REQUEST", ex.getMessage());
    }

    // 409: 상태 충돌(비즈니스 규칙 위반 등)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        return conflict("STATE_CONFLICT", ex.getMessage());
    }

    // Bean Validation 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", "VALIDATION_ERROR");
        body.put("message", "입력값을 확인하세요.");
        body.put("errors", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 트랜잭션 커밋/플러시 중 예외(제약 위반 등)
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Map<String, Object>> handleTransaction(TransactionSystemException ex) {
        String msg = extractMostSpecificMessage(ex);
        log.error("TX ERROR", ex);
        return badRequest("TRANSACTION_ERROR", msg);
    }

    // 낙관적 락 충돌 (@Version)
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, Object>> handleOptimistic(ObjectOptimisticLockingFailureException ex) {
        return conflict("OPTIMISTIC_LOCK_CONFLICT", "다른 사용자가 먼저 변경했습니다. 새로고침 후 다시 시도하세요.");
    }

    // DB 제약조건 위반(FK/UK/NOT NULL 등) — 단일 핸들러(모호성 제거)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegrity(DataIntegrityViolationException ex) {
        String raw = extractMostSpecificMessage(ex);

        String msg = "데이터 제약으로 처리할 수 없습니다.";
        if (contains(raw, "bom"))                      msg = "삭제 불가: 연결된 BOM이 존재합니다.";
        else if (contains(raw, "item_stock"))          msg = "삭제 불가: 연결된 재고행이 존재합니다.";
        else if (contains(raw, "order"))               msg = "삭제 불가: 주문 이력이 존재합니다.";
        else if (contains(raw, "plan"))                msg = "삭제 불가: 생산 계획 이력이 존재합니다.";
        else if (contains(raw, "cannot be null") || contains(raw, "null"))     msg = "필수값 누락입니다.";
        else if (contains(raw, "duplicate") || contains(raw, "unique"))        msg = "중복된 데이터입니다.";
        else if (contains(raw, "cannot add or update a child row") || contains(raw, "foreign key"))
            msg = "참조 무결성 위반입니다. 연결된 데이터부터 정리하세요.";

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("code", "DATA_INTEGRITY_VIOLATION", "message", msg));
    }

    // 선택: 기타 런타임 예외를 400으로 통일하고 싶으면 유지
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        log.error("Runtime", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("code","RUNTIME_ERROR","message","요청을 처리할 수 없습니다."));
    }

    // 최종 캐치올
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception ex) {
        log.error("Unhandled", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("code", "INTERNAL_ERROR", "message", "서버 오류"));
    }

    // ===== helpers =====
    private ResponseEntity<Map<String, Object>> badRequest(String code, String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", code, "message", message));
    }
    private ResponseEntity<Map<String, Object>> conflict(String code, String message) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("code", code, "message", message));
    }
    private String extractMostSpecificMessage(Throwable ex) {
        Throwable root = NestedExceptionUtils.getMostSpecificCause(ex);
        String msg = (root != null && root.getMessage() != null) ? root.getMessage() : ex.getMessage();
        return (msg == null) ? "" : msg.toLowerCase();
    }
    private boolean contains(String haystack, String needle) {
        return haystack != null && haystack.contains(needle);
    }
}
