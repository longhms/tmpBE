package com.luvina.la.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Copyright(C) [2026] [Luvina Software Company]
 * <p>
 * [.java], [Apr ,2026] [ntlong]
 */
@Data
@AllArgsConstructor
public class ValidationErrorResponse {
    /** Mã lỗi */
    private String code;
    /** Danh sách tham số */
    private List<String> params;
}
