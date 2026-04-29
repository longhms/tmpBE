/*
 * Copyright(C) [2026] [Luvina Software Company]
 * [AppException.java], [Apr ,2026] [ntlong]
 */
package com.luvina.la.exception;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * Exception nghiệp vụ của ứng dụng.
 *
 * Mang theo mã lỗi (code) và các tham số (params) để GlobalExceptionHandler
 * format ra MessageResponse trả về client.
 *
 * @author [ntlong]
 */
@Getter
public class AppException extends RuntimeException {

    /** Mã lỗi (key trong messages.properties / messages.ts) */
    private final String code;

    /** Danh sách tham số đổ vào placeholder {0}, {1},... của message */
    private final List<String> params;

    /**
     * Tạo exception với mã lỗi và danh sách tham số dạng varargs.
     *
     * @param code   Mã lỗi (vd: "ER004")
     * @param params Các tham số đổ vào message (vd: tên field tiếng Nhật)
     */
    public AppException(String code, String... params) {
        super(code);
        this.code = code;
        this.params = Arrays.asList(params);
    }

    /**
     * Tạo exception với mã lỗi và danh sách tham số đã build sẵn.
     *
     * @param code   Mã lỗi (vd: "ER004")
     * @param params List tham số sẽ đổ vào message
     */
    public AppException(String code, List<String> params) {
        super(code);
        this.code = code;
        this.params = params;
    }
}
