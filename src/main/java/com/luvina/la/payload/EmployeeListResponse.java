package com.luvina.la.payload;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [EmployeeListResponse.java], [Apr ,2026] [ntlong]
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.luvina.la.dto.EmployeeListDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

/**
 * Response tra ve cho API lay danh sach nhan vien (ADM002).
 * - code        : Ma HTTP status (VD: "200" khi thanh cong, "500" khi loi he thong).
 * - totalRecords: Tong so ban ghi tim thay (chi co khi thanh cong).
 * - employees   : Danh sach nhan vien (chi co khi thanh cong).
 * - message     : Thong tin loi (chi co khi that bai).
 *
 * @author [ntlong]
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeListResponse {

    /** Ma HTTP status (dang String): "200" neu thanh cong, "500" neu loi he thong. */
    private int code;

    /** Tong so ban ghi thoa dieu kien tim kiem */
    private Long totalRecords;

    /** Danh sach nhan vien tra ve cho client */
    private List<EmployeeListDTO> employees;

    /** Thong tin loi (code + params) */
    private MessageResponse message;

    /** Response thành công (code = 200) */
    public static EmployeeListResponse success(Long totalRecords, List<EmployeeListDTO> employees) {
        EmployeeListResponse res = new EmployeeListResponse();
        res.code = HttpStatus.OK.value();
        res.totalRecords = totalRecords;
        res.employees = employees;
        return res;
    }

    /** Response lỗi hệ thống (code = 500) */
    public static EmployeeListResponse error(String errorCode, List<String> params) {
        EmployeeListResponse res = new EmployeeListResponse();
        res.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        res.message = new MessageResponse(errorCode, params);
        return res;
    }

    /** Overload cho trường hợp lỗi không có params */
    public static EmployeeListResponse error(String errorCode) {
        return error(errorCode, Collections.emptyList());
    }

    /** Response lỗi validate input (code = 400) — dùng ở Controller */
    public static EmployeeListResponse badRequest(String errorCode, List<String> params) {
        EmployeeListResponse res = new EmployeeListResponse();
        res.code = HttpStatus.BAD_REQUEST.value();
        res.message = new MessageResponse(errorCode, params);
        return res;
    }
}
