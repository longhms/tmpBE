/*
 * Copyright(C) [2026] [Luvina Software Company]
 * [EmployeeListDTO.java], [Apr ,2026] [ntlong]
 */
package com.luvina.la.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO đại diện 1 dòng nhân viên trong danh sách (ADM002).
 *
 * Mỗi field được map từ 1 cột của native query getEmployees thông qua EmployeeMapper.
 * Các trường ngày tháng đã được format sẵn về dạng yyyy/MM/dd.
 *
 * @author [ntlong]
 */
@Data
@AllArgsConstructor
public class EmployeeListDTO {

    private static final long serialVersionUID = 1L;

    /** ID nhân viên */
    private Long employeeId;

    /** Tên nhân viên */
    private String employeeName;

    /** Ngày sinh (yyyy/MM/dd) */
    private String employeeBirthDate;

    /** Tên phòng ban */
    private String departmentName;

    /** Email */
    private String employeeEmail;

    /** Số điện thoại */
    private String employeeTelephone;

    /** Tên chứng chỉ tiếng Nhật cao cấp nhất (có thể null) */
    private String certificationName;

    /** Ngày hết hạn của chứng chỉ trên (yyyy/MM/dd, có thể null) */
    private String endDate;

    /** Điểm chứng chỉ (có thể null) */
    private BigDecimal score;
}
