package com.community.api.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
//공통 예외 처리
public class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicate(DuplicateException e){
        return ResponseEntity.status(409)
                .body(ApiResponse.of(e.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<?>> handleBadRequest(BadRequestException e){
        return ResponseEntity.status(400)
                .body(ApiResponse.of(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        return ResponseEntity.status(500)
                .body(ApiResponse.of("internal_server_error"));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<?>> handleForbidden(ForbiddenException e){
        return ResponseEntity.status(403)
                .body(ApiResponse.of(e.getMessage()));
    }
}
