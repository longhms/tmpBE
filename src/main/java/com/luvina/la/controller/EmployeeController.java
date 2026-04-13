package com.luvina.la.controller;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [EmployeeController.java], [Apr ,2026] [ntlong]
 */

import com.luvina.la.payload.EmployeeListResponse;
import com.luvina.la.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xử lý các API liên quan đến Employee.
 * author: [ntlong]
 */
@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * API lấy danh sách nhân viên với tìm kiếm, sắp xếp, phân trang.
     */
    @GetMapping
    public EmployeeListResponse getEmployees(
            @RequestParam(value = "employee_name", required = false) String employeeName,
            @RequestParam(value = "department_id", required = false) Long departmentId,
            @RequestParam(value = "ord_employee_name", required = false) String ordEmployeeName,
            @RequestParam(value = "ord_certification_name", required = false) String ordCertificationName,
            @RequestParam(value = "ord_end_date", required = false) String ordEndDate,
            @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit) {

        return employeeService.getEmployees(
                employeeName, departmentId,
                ordEmployeeName, ordCertificationName, ordEndDate,
                offset, limit);
    }
}
