/*
 * Copyright(C) [2026] [Luvina Software Company]
 * [EmployeeDetailResponse.java], [Apr ,2026] [ntlong]
 */
package com.luvina.la.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.luvina.la.dto.EmployeeDetailDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Response cho API lấy chi tiết 1 nhân viên (ADM003): GET /employee/{id}.
 *
 * Thành công (code = 200) trả kèm EmployeeDetailDTO.
 * Các trường hợp lỗi (ER013, ER015,...) được trả qua ApiErrorResponse từ
 * GlobalExceptionHandler -> response này chỉ giữ shape success.
 *
 * @author [ntlong]
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeDetailResponse {

    /** Mã HTTP status (200) */
    private int code;

    /** Thông tin chi tiết nhân viên */
    private EmployeeDetailDTO employee;

    /**
     * Tạo response thành công (code = 200) kèm EmployeeDetailDTO.
     *
     * @param employee EmployeeDetailDTO đã map từ entity
     * @return EmployeeDetailResponse code = 200
     */
    public static EmployeeDetailResponse success(EmployeeDetailDTO employee) {
        EmployeeDetailResponse res = new EmployeeDetailResponse();
        res.code = HttpStatus.OK.value();
        res.employee = employee;
        return res;
    }
}
