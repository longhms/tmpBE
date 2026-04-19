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
    private String code;

    /** Tong so ban ghi thoa dieu kien tim kiem */
    private Long totalRecords;

    /** Danh sach nhan vien tra ve cho client */
    private List<EmployeeListDTO> employees;

    /** Thong tin loi (code + params) */
    private MessageResponse message;
}
