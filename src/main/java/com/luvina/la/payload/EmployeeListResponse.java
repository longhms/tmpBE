/*
 * Copyright(C) [2026] [Luvina Software Company]
 * [EmployeeListResponse.java], [Apr ,2026] [ntlong]
 */
package com.luvina.la.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.luvina.la.dto.EmployeeListDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

/**
 * Response trả về cho API lấy danh sách nhân viên (ADM002).
 *
 *   - code         : Mã HTTP status (200 khi thành công, 400 lỗi validate, 500 lỗi hệ thống).
 *   - totalRecords : Tổng số bản ghi tìm thấy (chỉ có khi thành công).
 *   - employees    : Danh sách nhân viên (chỉ có khi thành công).
 *   - message      : Thông tin lỗi (chỉ có khi thất bại).
 *
 * @author [ntlong]
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeListResponse {

    /** Mã HTTP status (200 / 400 / 500) */
    private int code;

    /** Tổng số bản ghi thoả điều kiện tìm kiếm */
    private Long totalRecords;

    /** Danh sách nhân viên trả về cho client */
    private List<EmployeeListDTO> employees;

    /** Thông tin lỗi (code + params) */
    private MessageResponse message;

    /**
     * Tạo response thành công (code = 200) kèm danh sách nhân viên.
     *
     * @param totalRecords Tổng số bản ghi thoả điều kiện
     * @param employees    Danh sách nhân viên đã map sang DTO
     * @return EmployeeListResponse code = 200
     */
    public static EmployeeListResponse success(Long totalRecords, List<EmployeeListDTO> employees) {
        EmployeeListResponse res = new EmployeeListResponse();
        res.code = HttpStatus.OK.value();
        res.totalRecords = totalRecords;
        res.employees = employees;
        return res;
    }

    /**
     * Tạo response lỗi hệ thống (code = 500) kèm code + params.
     *
     * @param errorCode Mã lỗi
     * @param params    Tham số đổ vào message
     * @return EmployeeListResponse code = 500
     */
    public static EmployeeListResponse error(String errorCode, List<String> params) {
        EmployeeListResponse res = new EmployeeListResponse();
        res.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        res.message = new MessageResponse(errorCode, params);
        return res;
    }

    /**
     * Overload tiện dụng cho lỗi hệ thống không kèm params.
     *
     * @param errorCode Mã lỗi (thường là ER015)
     * @return EmployeeListResponse code = 500 với params rỗng
     */
    public static EmployeeListResponse error(String errorCode) {
        return error(errorCode, Collections.emptyList());
    }

    /**
     * Tạo response lỗi validate input (code = 400) - dùng tại Controller.
     *
     * @param errorCode Mã lỗi (ER018, ER021,...)
     * @param params    Tham số đổ vào message
     * @return EmployeeListResponse code = 400
     */
    public static EmployeeListResponse badRequest(String errorCode, List<String> params) {
        EmployeeListResponse res = new EmployeeListResponse();
        res.code = HttpStatus.BAD_REQUEST.value();
        res.message = new MessageResponse(errorCode, params);
        return res;
    }
}
