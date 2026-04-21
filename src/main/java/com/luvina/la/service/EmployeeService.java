package com.luvina.la.service;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [EmployeeService.java], [Apr ,2026] [ntlong]
 */

import com.luvina.la.payload.EmployeeDetailResponse;
import com.luvina.la.payload.EmployeeListResponse;
import com.luvina.la.payload.EmployeeRegisterResponse;
import com.luvina.la.payload.EmployeeRequest;

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
     * Lấy thông tin chi tiết của 1 nhân viên (cho màn hình ADM003/ADM004).
     *
     * @param employeeId ID nhân viên cần lấy
     * @return EmployeeDetailResponse - thành công (200) hoặc lỗi (500 + ER013/ER015)
     */
    EmployeeDetailResponse getEmployeeDetail(Long employeeId);

    /**
     * Thêm mới 1 nhân viên (ADM005 -> ADM006). Trả về MSG001 khi thành công.
     */
    EmployeeRegisterResponse addEmployee(EmployeeRequest request);

    /**
     * Cập nhật 1 nhân viên. Trả về MSG002 khi thành công.
     * login_id không được đổi (FE disable). password rỗng -> giữ nguyên.
     */
    EmployeeRegisterResponse updateEmployee(EmployeeRequest request);

    /**
     * Xoá 1 nhân viên theo id. Trả về MSG003 khi thành công.
     * Không cho xoá admin (ER020), không tồn tại (ER014).
     */
    EmployeeRegisterResponse deleteEmployee(Long employeeId);
}
