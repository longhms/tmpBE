package com.luvina.la.service;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [EmployeeService.java], [Apr ,2026] [ntlong]
 */

import com.luvina.la.payload.*;

/**
 * Interface Service cho Employee.
 * author: [ntlong]
 */
public interface EmployeeService {

    /**
     * Lấy danh sách nhân viên với tìm kiếm, sắp xếp, phân trang.
     * Thứ tự ưu tiên sort cố định: employeeName → certificationName → endDate.
     */
    EmployeeListResponse getEmployees(
            String employeeName,
            Long departmentId,
            String ordEmployeeName,
            String ordCertificationName,
            String ordEndDate,
            Integer offset,
            Integer limit
    );

    /**
     * Thêm mới 1 nhân viên (ADM005 -> ADM006).
     * Trả về employee_id vừa tạo. Lỗi validate/nghiệp vụ → throw BusinessException.
     */
    Long addEmployee(EmployeeRequest request);

    /**
     * Lấy chi tiết 1 nhân viên (ADM003). Không tồn tại → throw BusinessException(ER013).
     */
    EmployeeDetailResponse getEmployeeDetail(Long employeeId);

    /**
     * Check trùng employeeLoginId.
     */
    boolean existsByEmployeeLoginId(String employeeLoginId);

    /**
     * Check tồn tại phòng ban / chứng chỉ tiếng Nhật.
     * Không tồn tại → throw BusinessException(ER004).
     */
    void validateRefs(Long departmentId, Long certificationId);
}
