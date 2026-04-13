package com.luvina.la.service;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [EmployeeService.java], [Apr ,2026] [ntlong]
 */

import com.luvina.la.payload.EmployeeListResponse;

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
}
