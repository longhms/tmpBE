package com.luvina.la.service.impl;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [EmployeeServiceImpl.java], [Apr ,2026] [ntlong]
 */

import com.luvina.la.config.MessageConstants;
import com.luvina.la.config.MessageUtils;
import com.luvina.la.dto.EmployeeListDTO;
import com.luvina.la.payload.EmployeeListResponse;
import com.luvina.la.repository.EmployeeRepository;
import com.luvina.la.service.EmployeeService;
import com.luvina.la.validation.ValidateUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Triển khai EmployeeService.
 * Sử dụng native SQL query để truy vấn danh sách nhân viên.
 * author: [ntlong]
 */
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    private final MessageUtils messageUtils;

    private final ValidateUtil validateUtil;
    @Override
    public EmployeeListResponse getEmployees(
            String employeeName, Long departmentId,
            String ordEmployeeName, String ordCertificationName, String ordEndDate,
            Integer offset, Integer limit) {
        try {
            // 1. Validate thứ tự sắp xếp
            if (!validateUtil.isValidOrder(ordEmployeeName)
                    || !validateUtil.isValidOrder(ordCertificationName)
                    || !validateUtil.isValidOrder(ordEndDate)) {
                return buildErrorResponse(MessageConstants.ER021,
                        messageUtils.getMessage("field.sort"));
            }

            // 2. Giá trị mặc định
            offset = validateUtil.normalizeOffset(offset);
            limit = validateUtil.normalizeLimit(limit);

            // 3. Chuẩn hóa filter
            String empName = validateUtil.normalizeEmployeeName(employeeName);

            // 4. Chuẩn hóa sort (mặc định ASC nếu không truyền)
            String ordEmpName = validateUtil.normalizeOrder(ordEmployeeName);
            String ordCertName = validateUtil.normalizeOrder(ordCertificationName);
            String ordEnd = validateUtil.normalizeOrder(ordEndDate);

            // 5. Đếm tổng số bản ghi
            Long totalRecords = employeeRepository.countEmployees(empName, departmentId);
            if (totalRecords == 0) {
                return new EmployeeListResponse(0L, Collections.emptyList());
            }

            // 6. Lấy danh sách nhân viên
            List<Object[]> results = employeeRepository.getEmployees(
                    empName, departmentId,
                    ordEmpName, ordCertName, ordEnd,
                    offset, limit);

            // 7. Chuyển đổi kết quả → DTO
            List<EmployeeListDTO> employees = validateUtil.mapResultsToDtos(results);

            return new EmployeeListResponse(totalRecords, employees);

        } catch (Exception e) {
            log.error("Error getting employees", e);
            return buildErrorResponse(MessageConstants.ER015);
        }
    }

    /**
     * Tạo response lỗi với mã lỗi và params từ messages.properties.
     */
    private EmployeeListResponse buildErrorResponse(String errorCode, String... params) {
        List<String> paramList = params.length > 0
                ? Arrays.asList(params) : Collections.emptyList();
        return new EmployeeListResponse(errorCode, paramList);
    }
}
