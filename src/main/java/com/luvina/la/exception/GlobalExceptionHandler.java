package com.luvina.la.exception;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [GlobalExceptionHandler.java], [Apr ,2026] [ntlong]
 */

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
 *  - BusinessException -> 400 kèm code + params (ER001, ER003, ER004, ER013,…).
 *  - Các Exception khác -> 500 kèm ER015.
 *
 * Shape lỗi dùng chung cho mọi API (list, detail, add, update, delete).
 *
 * @author [ntlong]
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Lỗi nghiệp vụ / validate -> 400 */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(BusinessException exception) {
        log.warn("BusinessException: code={}, params={}", exception.getCode(), exception.getParams());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.badRequest(exception.getCode(), exception.getParams()));
    }

    /** Lỗi không xác định -> 500 ER015 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnknown(Exception exception) {
        log.error("Unexpected error", exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.error(MessageConstants.ER015));
    }
}
