/*
 * Copyright(C) [2026] [Luvina Software Company]
 * [ApiErrorResponse.java], [Apr ,2026] [ntlong]
 */
package com.luvina.la.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

/**
 * Response lỗi dùng chung cho toàn bộ các API (ADM002/003/004/005/006).
 *
 * Được trả ra từ {@code GlobalExceptionHandler}:
 *   - code = 400 (AppException) -> lỗi validate / nghiệp vụ (ER001, ER003, ER013, ER014,...).
 *   - code = 500 (Exception)    -> lỗi hệ thống (ER015).
 *
 * Payload thành công của từng chức năng có shape riêng
 * (EmployeeListResponse, EmployeeDetailResponse, EmployeeMutationResponse,...),
 * còn lỗi thì luôn cùng format này -> FE chỉ cần parse một shape duy nhất.
 *
 * @author [ntlong]
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    /** Mã HTTP status (400 / 500) */
    private int code;

    /** Thông tin lỗi (code + params) */
    private MessageResponse message;

    /**
     * Tạo response lỗi validate / nghiệp vụ (code = 400).
     *
     * @param errorCode Mã lỗi (vd: "ER004")
     * @param params    Danh sách tham số đổ vào message (có thể null)
     * @return ApiErrorResponse với code = 400 và message tương ứng
     */
    public static ApiErrorResponse badRequest(String errorCode, List<String> params) {
        ApiErrorResponse res = new ApiErrorResponse();
        res.code = HttpStatus.BAD_REQUEST.value();
        res.message = new MessageResponse(errorCode, params == null ? Collections.emptyList() : params);
        return res;
    }

    /**
     * Tạo response lỗi hệ thống (code = 500).
     *
     * @param errorCode Mã lỗi hệ thống (thường là "ER015")
     * @return ApiErrorResponse với code = 500 và message tương ứng
     */
    public static ApiErrorResponse error(String errorCode) {
        ApiErrorResponse res = new ApiErrorResponse();
        res.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        res.message = new MessageResponse(errorCode, Collections.emptyList());
        return res;
    }
}
