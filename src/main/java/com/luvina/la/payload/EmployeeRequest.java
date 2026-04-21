package com.luvina.la.payload;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [EmployeeRequest.java], [Apr ,2026] [ntlong]
 */

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request body cho API thêm mới / cập nhật nhân viên (ADM004 -> ADM005).
 *
 * - Thêm mới (POST): đầy đủ các trường, KHÔNG có employeeId.
 * - Cập nhật (PUT): có employeeId, accountName (loginId) vẫn gửi lên nhưng BE
 *   sẽ không đổi login_id (FE không cho sửa), password có thể null/empty -> BE skip update.
 *
 * @author [ntlong]
 */
@Data
@NoArgsConstructor
public class EmployeeRequest {

    /** ID nhân viên - null khi add, có giá trị khi update */
    private Long employeeId;

    /** Login ID (account name) */
    private String employeeLoginId;

    /** Mật khẩu (plain-text, BE hash trước khi lưu). Null/empty khi update -> skip */
    private String employeeLoginPassword;

    /** ID phòng ban */
    private Long departmentId;

    /** Họ tên nhân viên */
    private String employeeName;

    /** Họ tên katakana */
    private String employeeNameKana;

    /** Ngày sinh (yyyy/MM/dd) */
    private String employeeBirthDate;

    /** Email */
    private String employeeEmail;

    /** Số điện thoại */
    private String employeeTelephone;

    /** Danh sách chứng chỉ */
    private List<EmployeeCertificationRequest> certifications;
}
