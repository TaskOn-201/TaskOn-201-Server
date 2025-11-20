package com.twohundredone.taskonserver.common;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
public class GlobalExceptionHandler {
    // 비즈니스 로직 예외 (BusinessException)
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(CustomException e) {
        log.warn("[BusinessException] {}", e.getMessage());

        ResponseStatusError status = e.getStatusError();

        return ResponseEntity
                .status(status.getStatusCode())
                .body(ApiResponse.fail(status, null));
    }

    // @Valid 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldError().getDefaultMessage();
        log.warn("[ValidationError] {}", errorMessage);

        return ResponseEntity
                .status(ResponseStatusError.VALIDATION_ERROR.getStatusCode())
                .body(ApiResponse.fail(ResponseStatusError.VALIDATION_ERROR, errorMessage));
    }

    // @PathVariable, @RequestParam 검증 실패
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException e) {
        log.warn("[ConstraintViolation] {}", e.getMessage());

        return ResponseEntity
                .status(ResponseStatusError.VALIDATION_ERROR.getStatusCode())
                .body(ApiResponse.fail(ResponseStatusError.VALIDATION_ERROR, e.getMessage()));
    }

    // 인증(Authentication) 실패
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(AuthenticationException e) {
        log.warn("[AuthenticationException] {}", e.getMessage());

        return ResponseEntity
                .status(ResponseStatusError.UNAUTHORIZED.getStatusCode())
                .body(ApiResponse.fail(ResponseStatusError.UNAUTHORIZED, null));
    }

    // 인가(AccessDenied) 실패
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("[AccessDeniedException] {}", e.getMessage());

        return ResponseEntity
                .status(ResponseStatusError.FORBIDDEN.getStatusCode())
                .body(ApiResponse.fail(ResponseStatusError.FORBIDDEN, null));
    }

    // 그 외 전체 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        log.error("[Exception] {}", e.getMessage(), e);

        return ResponseEntity
                .status(ResponseStatusError.SERVER_ERROR.getStatusCode())
                .body(ApiResponse.fail(ResponseStatusError.SERVER_ERROR, null));
    }
}
