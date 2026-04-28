/*
 * Copyright(C) [2026] [Luvina Software Company]
 * [GlobalExceptionHandler.java], [Apr ,2026] [ntlong]
 */
package com.luvina.la.exception;

import com.luvina.la.config.MessageConstants;
import com.luvina.la.payload.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Bắt toàn bộ exception từ Controller/Service và chuyển sang ApiErrorResponse.
 *
 *   - AppException -> 400 kèm code + params (ER001, ER003, ER004, ER013,...).
 *   - Các Exception khác -> 500 kèm ER015.
 *
 * Shape lỗi dùng chung cho mọi API (list, detail, add, update, delete).
 *
 * @author [ntlong]
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Bắt lỗi nghiệp vụ / validate, trả về HTTP 400 kèm code + params.
     *
     * @param exception AppException được throw từ Controller/Service/Validation
     * @return ResponseEntity 400 với ApiErrorResponse chứa code + params
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiErrorResponse> handleAppException(AppException exception) {
        log.warn("AppException: code={}, params={}", exception.getCode(), exception.getParams());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.badRequest(exception.getCode(), exception.getParams()));
    }

    /**
     * Bắt mọi exception còn lại (lỗi hệ thống / không xác định), trả về HTTP 500 ER015.
     *
     * @param exception Exception bất kỳ chưa được handler nào xử lý
     * @return ResponseEntity 500 với ApiErrorResponse ER015
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnknown(Exception exception) {
        log.error("Unexpected error", exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.error(MessageConstants.ER015));
    }
}
