package com.luvina.la.payload;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [EmployeeDetailResponse.java], [Apr ,2026] [ntlong]
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.luvina.la.dto.EmployeeCertificationDetailDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response trả về cho API chi tiết nhân viên (ADM003): GET /employee/{id}
 *
 * - code             : Mã HTTP status (200 khi thành công, 500 khi lỗi hệ thống/không tồn tại)
 * - các field employee : Chỉ có khi thành công
 * - message          : Chỉ có khi thất bại (ER013, ER015,…)
 *
 * Không đóng gói thành sub-object "employee" vì requirement yêu cầu flat JSON.
 *
 * @author [ntlong]
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeDetailResponse {

    /** Mã HTTP status (200 / 500) */
    private int code;

    private Long employeeId;
    private String employeeLoginId;
    private Long departmentId;
    private String departmentName;
    private String employeeName;
    private String employeeNameKana;
    private String employeeBirthDate;
    private String employeeEmail;
    private String employeeTelephone;

    /** Danh sách chứng chỉ (sắp xếp theo certification_level DESC) */
    private List<EmployeeCertificationDetailDTO> certifications;

    /** Thông tin lỗi (chỉ có khi thất bại) */
    private MessageResponse message;
}
