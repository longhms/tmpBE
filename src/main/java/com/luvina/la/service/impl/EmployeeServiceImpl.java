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
 * Triển khai EmployeeService - xử lý business logic cho chức năng Employee.
 *
 * Chức năng chính:
 * - Lấy danh sách nhân viên (search, sort, paging) từ database
 *
 * Lưu ý:
 * - Validate đầu vào đã được thực hiện tại Controller trước khi gọi Service
 * - Service chỉ thực hiện: normalize dữ liệu -> truy vấn DB -> trả về kết quả
 * - Sử dụng native SQL query thông qua EmployeeRepository
 *
 * @author [ntlong]
 */
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    private final MessageUtils messageUtils;
    private final ValidateUtil validateUtil;

    /**
     * Lấy danh sách nhân viên với tìm kiếm, sắp xếp, phân trang.
     *
     * Flow xử lý:
     *   Bước 1: Chuẩn hoá (normalize) các giá trị đầu vào (offset, limit, sort, filter)
     *   Bước 2: Đếm tổng số bản ghi thoả điều kiện
     *   Bước 3: Nếu totalRecords = 0 -> trả về danh sách rỗng
     *   Bước 4: Lấy danh sách nhân viên từ DB với phân trang
     *   Bước 5: Chuyển đổi kết quả Object[] -> EmployeeListDTO
     *   Bước 6: Trả về response thành công (code 200)
     *
     * Thứ tự ưu tiên sort cố định: employeeName -> certificationName -> endDate -> employeeId
     *
     * @param employeeName         Tên nhân viên để tìm kiếm (LIKE %name%)
     * @param departmentId         ID phòng ban để lọc (exact match)
     * @param ordEmployeeName      Thứ tự sort theo tên (ASC/DESC)
     * @param ordCertificationName Thứ tự sort theo chứng chỉ (ASC/DESC)
     * @param ordEndDate           Thứ tự sort theo ngày hết hạn (ASC/DESC)
     * @param offset               Vị trí bắt đầu lấy (mặc định: 0)
     * @param limit                Số bản ghi tối đa (mặc định: 20)
     * @return EmployeeListResponse chứa danh sách nhân viên hoặc thông báo lỗi
     */
    @Override
    public EmployeeListResponse getEmployees(
            String employeeName, Long departmentId,
            String ordEmployeeName, String ordCertificationName, String ordEndDate,
            Integer offset, Integer limit) {
        try {
            // Bước 1: Chuẩn hoá giá trị mặc định cho offset và limit
            offset = validateUtil.normalizeOffset(offset);
            limit = validateUtil.normalizeLimit(limit);

            // Bước 2: Chuẩn hoá filter - chuyển empty string thành null để bỏ qua điều kiện WHERE
            String empName = validateUtil.normalizeEmployeeName(employeeName);

            // Bước 3: Chuẩn hoá sort - nếu null/empty thì mặc định "ASC"
            String ordEmpName = validateUtil.normalizeOrder(ordEmployeeName);
            String ordCertName = validateUtil.normalizeOrder(ordCertificationName);
            String ordEnd = validateUtil.normalizeOrder(ordEndDate);

            // Bước 4: Đếm tổng số bản ghi thoả điều kiện (loại trừ admin)
            Long totalRecords = employeeRepository.countEmployees(empName, departmentId);

            // Nếu không có bản ghi nào -> trả về danh sách rỗng với totalRecords = 0
            if (totalRecords == 0) {
                return new EmployeeListResponse(0L, Collections.emptyList());
            }

            // Bước 5: Lấy danh sách nhân viên từ DB với phân trang (LIMIT/OFFSET)
            List<Object[]> results = employeeRepository.getEmployees(
                    empName, departmentId,
                    ordEmpName, ordCertName, ordEnd,
                    offset, limit);

            // Bước 6: Chuyển đổi kết quả native query (Object[]) sang DTO
            List<EmployeeListDTO> employees = validateUtil.mapResultsToDtos(results);

            return new EmployeeListResponse(totalRecords, employees);

        } catch (Exception e) {
            // Bắt mọi exception không mong muốn -> trả về lỗi hệ thống ER015
            log.error("Error getting employees", e);
            return buildErrorResponse(MessageConstants.ER015);
        }
    }

    /**
     * Tạo response lỗi với mã lỗi và danh sách tham số từ messages.properties.
     *
     * @param errorCode Mã lỗi (VD: ER015, ER021...)
     * @param params    Danh sách tham số thay thế vào message template
     * @return EmployeeListResponse với code 500 và thông báo lỗi
     */
    private EmployeeListResponse buildErrorResponse(String errorCode, String... params) {
        List<String> paramList = params.length > 0
                ? Arrays.asList(params) : Collections.emptyList();
        return new EmployeeListResponse(errorCode, paramList);
    }
}
