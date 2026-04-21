package com.luvina.la.dto;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [EmployeeDetailDTO.java], [Apr ,2026] [ntlong]
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO chi tiết nhân viên dùng cho màn hình ADM003 (xem chi tiết)
 * và ADM004 (biên tập - lấy dữ liệu khởi tạo form).
 *
 * Tương ứng response của API: GET /employee/{id}
 *
 * @author [ntlong]
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDetailDTO {

    /** ID nhân viên */
    private Long employeeId;

    /** Tên đăng nhập (account name) - ở màn edit sẽ bị disable */
    private String employeeLoginId;

    /** ID phòng ban */
    private Long departmentId;

    /** Tên phòng ban */
    private String departmentName;

    /** Họ tên nhân viên */
    private String employeeName;

    /** Họ tên katakana */
    private String employeeNameKana;

    /** Ngày sinh - format yyyy/MM/dd */
    private String employeeBirthDate;

    /** Email */
    private String employeeEmail;

    /** Số điện thoại */
    private String employeeTelephone;

    /** Danh sách chứng chỉ (sort theo certification_level giảm dần) */
    private List<EmployeeCertificationDetailDTO> certifications;
}
