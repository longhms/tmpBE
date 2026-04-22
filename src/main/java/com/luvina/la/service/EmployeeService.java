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
     * Thêm mới 1 nhân viên (ADM005 -> ADM006). Trả về MSG001 khi thành công.
     */
    EmployeeRegisterResponse addEmployee(EmployeeRequest request);

    /**
    * check trùng employeeLoginId
    * */
    boolean existsByEmployeeLoginId(String employeeLoginId);

    /**
     * check còn tồn tại phòng ban hoặc chứng chỉ tiếng Nhật.
     *
     * */
    MessageResponse validateRefs(Long departmentId, Long certificationId);
}
