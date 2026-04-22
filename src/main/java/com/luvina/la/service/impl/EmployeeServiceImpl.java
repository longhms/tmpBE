package com.luvina.la.service.impl;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [EmployeeServiceImpl.java], [Apr ,2026] [ntlong]
 */

import com.luvina.la.config.MessageConstants;
import com.luvina.la.dto.EmployeeListDTO;
import com.luvina.la.entity.Certification;
import com.luvina.la.entity.Department;
import com.luvina.la.entity.Employee;
import com.luvina.la.entity.EmployeeCertification;
import com.luvina.la.payload.EmployeeCertificationRequest;
import com.luvina.la.payload.EmployeeListResponse;
import com.luvina.la.payload.EmployeeRegisterResponse;
import com.luvina.la.payload.EmployeeRequest;
import com.luvina.la.payload.MessageResponse;
import com.luvina.la.repository.CertificationRepository;
import com.luvina.la.repository.DepartmentRepository;
import com.luvina.la.repository.EmployeeCertificationRepository;
import com.luvina.la.repository.EmployeeRepository;
import com.luvina.la.service.EmployeeService;
import com.luvina.la.validation.EmployeeValidator;
import com.luvina.la.validation.ValidateUtil;
import com.luvina.la.validation.ValidationError;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Triển khai EmployeeService - xử lý business logic cho chức năng Employee.
 * - Lấy danh sách nhân viên (search, sort, paging) từ database
 *
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
    private final EmployeeCertificationRepository employeeCertificationRepository;
    private final DepartmentRepository departmentRepository;
    private final CertificationRepository certificationRepository;
    private final ValidateUtil validateUtil;
    private final EmployeeValidator employeeValidator;
    private final PasswordEncoder passwordEncoder;

    /**
     * Lấy danh sách nhân viên với tìm kiếm, sắp xếp, phân trang.
     *
     *   Chuẩn hoá (normalize) các giá trị đầu vào (offset, limit, sort, filter)
     *   Đếm tổng số bản ghi thoả điều kiện
     *   Nếu totalRecords = 0 -> trả về danh sách rỗng (code 200)
     *   Lấy danh sách nhân viên từ DB với phân trang
     *   Chuyển đổi kết quả Object[] -> EmployeeListDTO
     *   Trả về response thành công (code 200)
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

            // Nếu không có bản ghi nào -> trả về danh sách rỗng với totalRecords = 0 (code 200)
            if (totalRecords == 0) {
                return EmployeeListResponse.success(0L, Collections.emptyList());
            }

            // Bước 5: Lấy danh sách nhân viên từ DB với phân trang (LIMIT/OFFSET)
            List<Object[]> results = employeeRepository.getEmployees(
                    empName, departmentId,
                    ordEmpName, ordCertName, ordEnd,
                    offset, limit);

            // Bước 6: Chuyển đổi kết quả native query (Object[]) sang DTO
            List<EmployeeListDTO> employees = validateUtil.mapResultsToDtos(results);

            return EmployeeListResponse.success(totalRecords, employees);

        } catch (Exception e) {

            log.error("Error getting employees", e);
            return EmployeeListResponse.error(MessageConstants.ER015);
        }
    }
    /**
     * Kiểm tra tồn tại department và certification trong DB.
     *
     * @return MessageResponse chứa ER004 nếu không tồn tại, null nếu hợp lệ
     */
    @Override
    public MessageResponse validateRefs(Long departmentId, Long certificationId) {
        if (departmentId != null && !departmentRepository.existsById(departmentId)) {
            return new MessageResponse(MessageConstants.ER004, List.of("グループ"));
        }
        if (certificationId != null && !certificationRepository.existsById(certificationId)) {
            return new MessageResponse(MessageConstants.ER004, List.of("資格"));
        }
        return null;
    }

    /**
     * Thêm mới 1 nhân viên cùng danh sách chứng chỉ (transactional).
     *
     * Luồng: validate → hash password → build Employee entity → save Employee →
     * insert từng chứng chỉ. Trả về MSG001 + employee_id mới.
     *
     * @param request Dữ liệu gửi lên từ ADM005
     * @return EmployeeRegisterResponse (200 + MSG001, hoặc 500 + mã lỗi)
     */
    @Override
    @Transactional
    public EmployeeRegisterResponse addEmployee(EmployeeRequest request) {
        try {
            // 1. Validate
            ValidationError err = employeeValidator.validate(request, false);
            if (err != null) return EmployeeRegisterResponse.badRequest(err.getCode(), err.getParams());

            // 2. Build + save employee
            Employee employee = new Employee();
            applyRequestToEmployee(employee, request);
            employee.setEmployeeLoginPassword(passwordEncoder.encode(request.getEmployeeLoginPassword()));
            Employee saved = employeeRepository.save(employee);

            // 3. Insert certifications (nếu có)
            saveCertifications(saved, request.getCertifications());

            // 4. Response thành công
            return EmployeeRegisterResponse.success(saved.getEmployeeId(), MessageConstants.MSG001);
        } catch (Exception e) {
            log.error("Error adding employee", e);
            return EmployeeRegisterResponse.error(MessageConstants.ER015);
        }
    }

    @Override
    public boolean existsByEmployeeLoginId(String employeeLoginId) {
        return employeeRepository.existsByEmployeeLoginId(employeeLoginId);
    }


    /**
     * Password được xử lý riêng (hash ở caller).
     */
    private void applyRequestToEmployee(Employee employee, EmployeeRequest req) {
        employee.setEmployeeLoginId(req.getEmployeeLoginId());
        employee.setEmployeeName(req.getEmployeeName());
        employee.setEmployeeNameKana(req.getEmployeeNameKana());
        employee.setEmployeeBirthDate(employeeValidator.parseDate(req.getEmployeeBirthDate()));
        employee.setEmployeeEmail(req.getEmployeeEmail());
        employee.setEmployeeTelephone(req.getEmployeeTelephone());

        // Department (đã validate tồn tại)
        Department dept = departmentRepository.getReferenceById(req.getDepartmentId());
        employee.setDepartment(dept);
    }

    /**
     * Insert toàn bộ danh sách chứng chỉ mới cho nhân viên.
     * Gọi sau khi save employee mới (với add).
     */
    private void saveCertifications(Employee employee, List<EmployeeCertificationRequest> certs) {
        if (certs == null || certs.isEmpty()) return;
        for (EmployeeCertificationRequest c : certs) {
            EmployeeCertification ec = new EmployeeCertification();
            ec.setEmployee(employee);
            Certification cert = certificationRepository.getReferenceById(c.getCertificationId());
            ec.setCertification(cert);
            ec.setStartDate(employeeValidator.parseDate(c.getStartDate()));
            ec.setEndDate(employeeValidator.parseDate(c.getEndDate()));
            String scoreStr = c.getScore();
            if (scoreStr != null && !scoreStr.isBlank()) {
                ec.setScore(new BigDecimal(scoreStr));
            }
            employeeCertificationRepository.save(ec);
        }
    }


}
