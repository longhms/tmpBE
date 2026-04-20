package com.luvina.la.controller;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [EmployeeController.java], [Apr ,2026] [ntlong]
 */

import com.luvina.la.config.Constants;
import com.luvina.la.config.MessageConstants;
import com.luvina.la.payload.EmployeeListResponse;
import com.luvina.la.payload.MessageResponse;
import com.luvina.la.service.EmployeeService;
import com.luvina.la.validation.ValidateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Controller xử lý các API liên quan đến Employee.
 *
 * Chức năng chính:
 * - Lấy danh sách nhân viên (search, sort, paging)
 *
 * Validate được thực hiện tại đây (Controller) trước khi gọi Service.
 * Nếu dữ liệu không hợp lệ, trả về response lỗi ngay (không gọi Service).
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
            return buildErrorResponse(MessageConstants.ER021, Collections.emptyList());
        }

        // Validate offset phải là số nguyên >= 0 (ER018)
        // Nếu offset không phải số nguyên hợp lệ -> trả về lỗi ER018 với tham số "オフセット"
        if (!validateUtil.isNonNegativeInteger(offset)) {
            return buildErrorResponse(MessageConstants.ER018, Arrays.asList(Constants.OFFSET));
        }

        // Validate limit phải là số nguyên >= 0 (ER018)
        // Nếu limit không phải số nguyên hợp lệ -> trả về lỗi ER018 với tham số "リミット"
        if (!validateUtil.isNonNegativeInteger(limit)) {
            return buildErrorResponse(MessageConstants.ER018, Arrays.asList(Constants.LIMIT));
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
     * Tạo response lỗi validate tại Controller với HTTP status 500.
     * @param errorCode Mã lỗi
     * @param params    Danh sách tham số thay thế vào message template
     * @return EmployeeListResponse với code = "500" và thông tin lỗi
     */
    private EmployeeListResponse buildErrorResponse(String errorCode, List<String> params) {
        EmployeeListResponse response = new EmployeeListResponse();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(new MessageResponse(errorCode, params));
        return response;
    }
}
