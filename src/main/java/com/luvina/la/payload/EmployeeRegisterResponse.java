package com.luvina.la.payload;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [EmployeeRegisterResponse.java], [Apr ,2026] [ntlong]
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

/**
 * Response cho các API thay đổi dữ liệu Employee:
 *   POST   /employee          (add)     -> MSG001 khi thành công
 *   PUT    /employee          (update)  -> MSG002
 *   DELETE /employee/{id}     (delete)  -> MSG003
 *
 * Lỗi validate/nghiệp vụ -> code = 400 (ER001, ER003, ER004, ER013, ER014,…).
 * Lỗi hệ thống            -> code = 500 (ER015).
 *
 * @author [ntlong]
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeRegisterResponse {

    /** Mã HTTP status (200 / 400 / 500) */
    private int code;

    /** ID nhân viên sau khi thao tác (add: ID mới, update/delete: ID cũ) */
    private Long employeeId;

    /** Thông tin message (code + params) - cả success lẫn error đều set */
    private MessageResponse message;

    /** Response thành công cho add (code = 200, kèm MSG001/002/003) */
    public static EmployeeRegisterResponse success(Long employeeId, String messageCode) {
        EmployeeRegisterResponse res = ok();
        res.employeeId = employeeId;
        res.message = new MessageResponse(messageCode, Collections.emptyList());
        return res;
    }

    /** Response OK đơn giản (check trùng login, validate refs) */
    public static EmployeeRegisterResponse ok() {
        EmployeeRegisterResponse res = new EmployeeRegisterResponse();
        res.code = HttpStatus.OK.value();
        return res;
    }

    /** Response lỗi validate input (code = 400) */
    public static EmployeeRegisterResponse badRequest(String errorCode, List<String> params) {
        EmployeeRegisterResponse res = new EmployeeRegisterResponse();
        res.code = HttpStatus.BAD_REQUEST.value();
        res.message = new MessageResponse(errorCode, params);
        return res;
    }

    /** Response lỗi hệ thống (code = 500) */
    public static EmployeeRegisterResponse error(String errorCode) {
        EmployeeRegisterResponse res = new EmployeeRegisterResponse();
        res.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        res.message = new MessageResponse(errorCode, Collections.emptyList());
        return res;
    }
}
