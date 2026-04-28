/*
 * Copyright(C) [2026] [Luvina Software Company]
 * [EmployeeController.java], [Apr ,2026] [ntlong]
 */
package com.luvina.la.controller;

import com.luvina.la.config.Constants;
import com.luvina.la.config.MessageConstants;
import com.luvina.la.exception.AppException;
import com.luvina.la.payload.EmployeeDetailResponse;
import com.luvina.la.payload.EmployeeListResponse;
import com.luvina.la.payload.EmployeeMutationResponse;
import com.luvina.la.payload.EmployeeRequest;
import com.luvina.la.service.EmployeeService;
import com.luvina.la.validation.ValidateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * Controller xử lý các API liên quan đến Employee.
 *
 * Chức năng chính:
 * - Lấy danh sách nhân viên (search, sort, paging)
 * - Kiểm tra trùng login ID
 * - Kiểm tra tồn tại department / certification
 * - Thêm mới nhân viên
 *
 * Validate query param (sort, offset, limit) thực hiện tại Controller.
 * Validate nghiệp vụ (addEmployee) thực hiện trong Service qua EmployeeValidator.
 *
 * @author [ntlong]
 */
@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final ValidateUtil validateUtil;

    /**
     * API lấy danh sách nhân viên với tìm kiếm, sắp xếp, phân trang.
     *
     *   Validate các parameter đầu vào tại Controller
     *   Nếu hợp lệ -> gọi Service để lấy dữ liệu
     *   Nếu không hợp lệ -> trả về response lỗi
     *
     * @param employeeName         Tên nhân viên (LIKE %name%) - có thể null/empty
     * @param departmentId         ID phòng ban (exact match) - có thể null
     * @param ordEmployeeName      Thứ tự sắp xếp theo tên (ASC/DESC) - có thể null/empty
     * @param ordCertificationName Thứ tự sắp xếp theo chứng chỉ (ASC/DESC) - có thể null/empty
     * @param ordEndDate           Thứ tự sắp xếp theo ngày hết hạn (ASC/DESC) - có thể null/empty
     * @param offset               Vị trí bắt đầu lấy dữ liệu (int >= 0) - có thể null/empty
     * @param limit                Số bản ghi tối đa mỗi trang (int >= 0) - có thể null/empty
     * @return EmployeeListResponse chứa danh sách nhân viên hoặc thông báo lỗi
     */
    @GetMapping
    public EmployeeListResponse getEmployees(
            @RequestParam(value = "employee_name", required = false) String employeeName,
            @RequestParam(value = "department_id", required = false) Long departmentId,
            @RequestParam(value = "ord_employee_name", required = false) String ordEmployeeName,
            @RequestParam(value = "ord_certification_name", required = false) String ordCertificationName,
            @RequestParam(value = "ord_end_date", required = false) String ordEndDate,
            @RequestParam(value = "offset", required = false) String offset,
            @RequestParam(value = "limit", required = false) String limit) {

        // Validate thứ tự sắp xếp (ER021)
        // Giá trị ord chỉ được phép là "ASC", "DESC", null hoặc empty
        // Nếu sai -> trả về lỗi ER021
        if (!validateUtil.isValidOrder(ordEmployeeName)
                || !validateUtil.isValidOrder(ordCertificationName)
                || !validateUtil.isValidOrder(ordEndDate)) {
            return EmployeeListResponse.badRequest(MessageConstants.ER021, Collections.emptyList());
        }

        // Validate offset phải là số nguyên >= 0 (ER018)
        // Nếu offset không phải số nguyên hợp lệ -> trả về lỗi ER018 với tham số "オフセット"
        if (!validateUtil.isNonNegativeInteger(offset)) {
            return EmployeeListResponse.badRequest(MessageConstants.ER018, List.of(Constants.OFFSET));
        }

        // Validate limit phải là số nguyên >= 0 (ER018)
        // Nếu limit không phải số nguyên hợp lệ -> trả về lỗi ER018 với tham số "リミット"
        if (!validateUtil.isNonNegativeInteger(limit)) {
            return EmployeeListResponse.badRequest(MessageConstants.ER018, List.of(Constants.LIMIT));
        }

        // Parse offset/limit từ String sang Integer
        // Nếu null hoặc empty thì truyền null -> Service sẽ dùng giá trị mặc định
        Integer offsetValue = (offset == null || offset.isEmpty()) ? null : Integer.parseInt(offset);
        Integer limitValue = (limit == null || limit.isEmpty()) ? null : Integer.parseInt(limit);

        // Gọi Service để lấy dữ liệu (đã qua validate)
        return employeeService.getEmployees(
                employeeName, departmentId,
                ordEmployeeName, ordCertificationName, ordEndDate,
                offsetValue, limitValue);
    }

    /**
     * API check trùng employee-login-id.
     * Trùng -> throw AppException(ER003) -> GlobalExceptionHandler trả 400.
     *
     * @param employeeLoginId Login ID cần kiểm tra
     * @return EmployeeMutationResponse.ok() nếu chưa trùng
     */
    @GetMapping("/check-employee-login-id")
    public EmployeeMutationResponse checkEmployeeLoginId(@RequestParam("loginId") String employeeLoginId) {
        if (employeeService.existsByEmployeeLoginId(employeeLoginId)){
            throw new AppException(MessageConstants.ER003, Constants.FIELD_LOGIN_ID);
        }
        return EmployeeMutationResponse.ok();
    }

    /**
     * API khẳng định department và certification tồn tại trong DB.
     * Dùng cho FE check trước khi submit form (ADM004 / ADM005).
     * Không tồn tại sẽ throw AppException(ER004) -> GlobalExceptionHandler trả 400.
     *
     * @param departmentId    ID phòng ban cần kiểm tra (optional)
     * @param certificationId ID chứng chỉ cần kiểm tra (optional)
     * @return EmployeeMutationResponse.ok() nếu tồn tại
     */
    @GetMapping("/check-refs-exist")
    public EmployeeMutationResponse checkRefsExist(
            @RequestParam(value = "departmentId", required = false) Long departmentId,
            @RequestParam(value = "certificationId", required = false) Long certificationId
    ) {
        employeeService.assertDepartmentAndCertificationExist(departmentId, certificationId);
        return EmployeeMutationResponse.ok();
    }


    /**
     * API lấy chi tiết nhân viên (ADM003).
     * Không tồn tại → AppException(ER013) → GlobalExceptionHandler trả 400.
     *
     * @param employeeId ID nhân viên lấy từ path
     * @return EmployeeDetailResponse chứa thông tin chi tiết nhân viên
     */
    @GetMapping("/{employeeId}")
    public EmployeeDetailResponse getEmployeeDetail(@PathVariable("employeeId") Long employeeId) {
        return employeeService.getEmployeeDetail(employeeId);
    }

    /**
     * API thêm mới nhân viên (ADM005 -> ADM006).
     * Validate và lưu DB được thực hiện trong Service.
     * Lỗi validate/nghiệp vụ -> AppException -> GlobalExceptionHandler trả 400.
     * Lỗi hệ thống -> Exception -> GlobalExceptionHandler trả 500 ER015.
     *
     * @param request EmployeeRequest gửi từ ADM005
     * @return EmployeeMutationResponse kèm employeeId mới và MSG001
     */
    @PostMapping
    public EmployeeMutationResponse addEmployee(@RequestBody EmployeeRequest request) {
        Long id = employeeService.addEmployee(request);
        return EmployeeMutationResponse.success(id, MessageConstants.MSG001);
    }

    /**
     * API xóa nhân viên (ADM003 -> ADM006).
     * Không tồn tại → ER014 (400). Là admin → ER020 (400). Lỗi hệ thống → ER015 (500).
     *
     * @param employeeId ID nhân viên lấy từ path
     * @return EmployeeMutationResponse kèm MSG003 khi thành công
     */
    @DeleteMapping("/{employeeId}")
    public EmployeeMutationResponse deleteEmployee(@PathVariable("employeeId") Long employeeId) {
        employeeService.deleteEmployee(employeeId);
        return EmployeeMutationResponse.success(employeeId, MessageConstants.MSG003);
    }

}
