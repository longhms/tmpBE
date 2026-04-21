package com.luvina.la.validation;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [ValidationError.java], [Apr ,2026] [ntlong]
 */

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Kết quả lỗi validate do EmployeeValidator trả về.
 * Bao gồm mã lỗi (ER001, ER003,…) và danh sách tham số thay vào message template.
 *
 * @author [ntlong]
 */
@Data
@AllArgsConstructor
public class ValidationError {
    /** Mã lỗi - VD "ER001" */
    private String code;
    /** Danh sách tham số (thường là tên field tiếng Nhật) */
    private List<String> params;
}
