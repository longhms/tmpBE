package com.luvina.la.payload;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [EmployeeRegisterResponse.java], [Apr ,2026] [ntlong]
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response cho các API thay đổi dữ liệu Employee:
 *   POST   /employee          (add)     -> MSG001 khi thành công
 *   PUT    /employee          (update)  -> MSG002
 *   DELETE /employee/{id}     (delete)  -> MSG003
 *
 * Khi lỗi -> code = 500 + message (ER001, ER003, ER004, ER013, ER014, ER015,…).
 *
 * @author [ntlong]
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeRegisterResponse {

    /** Mã HTTP status (200 / 500) */
    private int code;

    /** ID nhân viên sau khi thao tác (add: ID mới, update/delete: ID cũ) */
    private Long employeeId;

    /** Thông tin message (code + params) - cả success lẫn error đều set */
    private MessageResponse message;
}
