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
 * DTO chi tiết 1 nhân viên (ADM003).
 * Không bao gồm password.
 *
 * @author [ntlong]
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDetailDTO {

    private Long employeeId;
    private String employeeLoginId;
    private String employeeName;
    private String employeeNameKana;
    /** yyyy/MM/dd */
    private String employeeBirthDate;
    private String employeeEmail;
    private String employeeTelephone;

    private Long departmentId;
    private String departmentName;

    /** Danh sách chứng chỉ tiếng Nhật - sort theo certification_level DESC */
    private List<CertificationDetailDTO> certifications;
}
