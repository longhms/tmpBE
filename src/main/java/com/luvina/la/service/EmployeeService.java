/*
 * Copyright(C) [2026] [Luvina Software Company]
 * [EmployeeService.java], [Apr ,2026] [ntlong]
 */
package com.luvina.la.service;

import com.luvina.la.payload.EmployeeDetailResponse;
import com.luvina.la.payload.EmployeeListResponse;
import com.luvina.la.payload.EmployeeRequest;

/**
 * Interface Service cho Employee.
 * Định nghĩa toàn bộ business operation cho module nhân viên
 * (list, detail, add, delete, các check phụ trợ).
 *
 * @author [ntlong]
 */
public interface EmployeeService {

    /**
     * Lấy danh sách nhân viên với tìm kiếm, sắp xếp, phân trang.
     * Thứ tự ưu tiên sort cố định: employeeName -> certificationName -> endDate.
     *
     * @param employeeName         Tên nhân viên (LIKE), có thể null/empty
     * @param departmentId         ID phòng ban (exact), có thể null
     * @param ordEmployeeName      Hướng sort tên (ASC/DESC)
     * @param ordCertificationName Hướng sort tên chứng chỉ (ASC/DESC)
     * @param ordEndDate           Hướng sort ngày hết hạn (ASC/DESC)
     * @param offset               Vị trí bắt đầu lấy
     * @param limit                Số bản ghi tối đa
     * @return EmployeeListResponse chứa danh sách + totalRecords
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
     * Lỗi validate / nghiệp vụ sẽ throw AppException, transaction sẽ rollback.
     *
     * @param request Dữ liệu nhân viên gửi từ ADM005
     * @return employee_id vừa tạo
     */
    Long addEmployee(EmployeeRequest request);

    /**
     * Lấy chi tiết 1 nhân viên (ADM003).
     * Không tồn tại sẽ throw AppException(ER013).
     *
     * @param employeeId ID nhân viên
     * @return EmployeeDetailResponse chứa thông tin chi tiết
     */
    EmployeeDetailResponse getEmployeeDetail(Long employeeId);

    /**
     * Check trùng employeeLoginId trong DB.
     *
     * @param employeeLoginId loginId cần kiểm tra
     * @return true nếu đã tồn tại
     */
    boolean existsByEmployeeLoginId(String employeeLoginId);

    /**
     * Khẳng định phòng ban / chứng chỉ tiếng Nhật tồn tại trong DB.
     * Không tồn tại sẽ throw AppException(ER004).
     *
     * @param departmentId    ID phòng ban (null -> bỏ qua)
     * @param certificationId ID chứng chỉ (null -> bỏ qua)
     */
    void assertDepartmentAndCertificationExist(Long departmentId, Long certificationId);

    /**
     * Xóa 1 nhân viên (ADM003 -> ADM006).
     * Không tồn tại -> ER014. Là admin -> ER020.
     *
     * @param employeeId ID nhân viên cần xóa
     */
    void deleteEmployee(Long employeeId);
}
