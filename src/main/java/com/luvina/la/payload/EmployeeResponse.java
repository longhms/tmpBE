/*
 * Copyright(C) [2026] [Luvina Software Company]
 * [EmployeeMutationResponse.java], [Apr ,2026] [ntlong]
 */
package com.luvina.la.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

/**
 * Response thành công cho các API thay đổi dữ liệu Employee
 * (mutation: add / update / delete) và các API check phụ trợ.
 *
 *   POST   /employee                  (add)     -> MSG001 khi thành công
 *   PUT    /employee                  (update)  -> MSG002
 *   DELETE /employee/{id}             (delete)  -> MSG003
 *   GET    /employee/check-employee-login-id    -> chỉ trả code = 200
 *   GET    /employee/check-refs-exist           -> chỉ trả code = 200
 *
 * Các trường hợp lỗi (validate / nghiệp vụ / hệ thống) được trả qua
 * ApiErrorResponse từ GlobalExceptionHandler -> FE chỉ parse 1 shape lỗi duy nhất.
 *
 * @author [ntlong]
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeResponse {

    /** Mã HTTP status */
    private int code;

    /** ID nhân viên sau khi thao tác */
    private Long employeeId;

    /** Thông tin message (code + params) - cả success lẫn error đều set khi cần */
    private MessageResponse message;

    /**
     * Tạo response thành công kèm employeeId và message code (MSG001/MSG002/MSG003).
     *
     * @param employeeId  ID nhân viên (mới hoặc đang thao tác)
     * @param messageCode Mã message thành công (MSG001/002/003)
     * @return EmployeeMutationResponse code = 200 kèm employeeId + message
     */
    public static EmployeeResponse success(Long employeeId, String messageCode) {
        EmployeeResponse res = ok();
        res.employeeId = employeeId;
        res.message = new MessageResponse(messageCode, Collections.emptyList());
        return res;
    }

    /**
     * Tạo response OK đơn giản (chỉ chứa code = 200), dùng cho các API check.
     *
     * @return EmployeeMutationResponse với code = 200
     */
    public static EmployeeResponse ok() {
        EmployeeResponse res = new EmployeeResponse();
        res.code = HttpStatus.OK.value();
        return res;
    }

    /**
     * Tạo response lỗi validate input (code = 400).
     *
     * @param errorCode Mã lỗi
     * @param params    Tham số đổ vào message
     * @return EmployeeMutationResponse code = 400 kèm message
     */
    public static EmployeeResponse badRequest(String errorCode, List<String> params) {
        EmployeeResponse res = new EmployeeResponse();
        res.code = HttpStatus.BAD_REQUEST.value();
        res.message = new MessageResponse(errorCode, params);
        return res;
    }

    /**
     * Tạo response lỗi hệ thống (code = 500).
     *
     * @param errorCode Mã lỗi hệ thống (thường là ER015)
     * @return EmployeeMutationResponse code = 500 kèm message
     */
    public static EmployeeResponse error(String errorCode) {
        EmployeeResponse res = new EmployeeResponse();
        res.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        res.message = new MessageResponse(errorCode, Collections.emptyList());
        return res;
    }
}
