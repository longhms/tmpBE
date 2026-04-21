package com.luvina.la.service.impl;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [EmployeeServiceImpl.java], [Apr ,2026] [ntlong]
 */

import com.luvina.la.config.MessageConstants;
import com.luvina.la.dto.EmployeeCertificationDetailDTO;
import com.luvina.la.dto.EmployeeListDTO;
import com.luvina.la.entity.Certification;
import com.luvina.la.entity.Department;
import com.luvina.la.entity.Employee;
import com.luvina.la.entity.EmployeeCertification;
import com.luvina.la.payload.EmployeeCertificationRequest;
import com.luvina.la.payload.EmployeeDetailResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    /** Định dạng ngày hiển thị về client: yyyy/MM/dd */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

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
                return buildSuccessResponse(0L, Collections.emptyList());
            }

            // Bước 5: Lấy danh sách nhân viên từ DB với phân trang (LIMIT/OFFSET)
            List<Object[]> results = employeeRepository.getEmployees(
                    empName, departmentId,
                    ordEmpName, ordCertName, ordEnd,
                    offset, limit);

            // Bước 6: Chuyển đổi kết quả native query (Object[]) sang DTO
            List<EmployeeListDTO> employees = validateUtil.mapResultsToDtos(results);

            return buildSuccessResponse(totalRecords, employees);

        } catch (Exception e) {

            log.error("Error getting employees", e);
            return buildErrorResponse(MessageConstants.ER015);
        }
    }

    /**
     * Lấy thông tin chi tiết của 1 nhân viên theo id.
     *
     * Luồng xử lý:
     *  1. Query DB tìm nhân viên (loại trừ admin)
     *  2. Nếu không tồn tại -> trả về lỗi ER013 (code 500)
     *  3. Query danh sách chứng chỉ của nhân viên (ORDER BY certification_level DESC)
     *  4. Map kết quả sang EmployeeDetailResponse (code 200)
     *
     * @param employeeId ID nhân viên
     * @return EmployeeDetailResponse chứa thông tin chi tiết hoặc lỗi
     */
    @Override
    public EmployeeDetailResponse getEmployeeDetail(Long employeeId) {
        try {
            // Bước 1: Query thông tin cơ bản của nhân viên
            List<Object[]> rows = employeeRepository.findEmployeeDetailById(employeeId);

            // Bước 2: Không tìm thấy -> ER013 (user không tồn tại khi biên tập)
            if (rows == null || rows.isEmpty()) {
                return buildDetailErrorResponse(MessageConstants.ER013);
            }

            Object[] row = rows.get(0);

            // Bước 3: Query danh sách chứng chỉ (có thể rỗng)
            List<Object[]> certRows = employeeRepository.findCertificationsByEmployeeId(employeeId);
            List<EmployeeCertificationDetailDTO> certifications = new ArrayList<>();
            for (Object[] cr : certRows) {
                certifications.add(new EmployeeCertificationDetailDTO(
                        ((Number) cr[0]).longValue(),                         // certification_id
                        formatDate(cr[1]),                                    // start_date -> yyyy/MM/dd
                        formatDate(cr[2]),                                    // end_date   -> yyyy/MM/dd
                        cr[3] != null ? new BigDecimal(cr[3].toString()) : null // score
                ));
            }

            // Bước 4: Build response thành công
            EmployeeDetailResponse response = new EmployeeDetailResponse();
            response.setCode(HttpStatus.OK.value());
            response.setEmployeeId(((Number) row[0]).longValue());
            response.setEmployeeLoginId((String) row[1]);
            response.setDepartmentId(row[2] != null ? ((Number) row[2]).longValue() : null);
            response.setDepartmentName((String) row[3]);
            response.setEmployeeName((String) row[4]);
            response.setEmployeeNameKana((String) row[5]);
            response.setEmployeeBirthDate(formatDate(row[6]));
            response.setEmployeeEmail((String) row[7]);
            response.setEmployeeTelephone((String) row[8]);
            response.setCertifications(certifications);
            return response;

        } catch (Exception e) {
            log.error("Error getting employee detail for id={}", employeeId, e);
            return buildDetailErrorResponse(MessageConstants.ER015);
        }
    }

    /**
     * Format ngày từ Object (LocalDate/Date/String) sang chuỗi yyyy/MM/dd.
     *
     * @param dateObj Object chứa giá trị ngày từ database
     * @return Chuỗi ngày định dạng yyyy/MM/dd, null nếu dateObj = null
     */
    private String formatDate(Object dateObj) {
        if (dateObj == null) return null;
        return LocalDate.parse(dateObj.toString()).format(DATE_FORMAT);
    }

    /**
     * Tạo EmployeeDetailResponse lỗi với HTTP status 500.
     *
     * @param errorCode Mã lỗi (ER013 / ER015)
     * @return EmployeeDetailResponse với code = 500 và message
     */
    private EmployeeDetailResponse buildDetailErrorResponse(String errorCode) {
        EmployeeDetailResponse response = new EmployeeDetailResponse();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(new MessageResponse(errorCode, Collections.emptyList()));
        return response;
    }

    // ══════════════════════════════════════════════════════════════════
    //  ADD / UPDATE / DELETE
    // ══════════════════════════════════════════════════════════════════

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
            if (err != null) return buildRegisterErrorResponse(err.getCode(), err.getParams());

            // 2. Build + save employee
            Employee employee = new Employee();
            applyRequestToEmployee(employee, request, false);
            employee.setEmployeeLoginPassword(passwordEncoder.encode(request.getEmployeeLoginPassword()));
            Employee saved = employeeRepository.save(employee);

            // 3. Insert certifications (nếu có)
            saveCertifications(saved, request.getCertifications());

            // 4. Response thành công
            return buildRegisterSuccessResponse(saved.getEmployeeId(), MessageConstants.MSG001);
        } catch (Exception e) {
            log.error("Error adding employee", e);
            return buildRegisterErrorResponse(MessageConstants.ER015, Collections.emptyList());
        }
    }

    /**
     * Cập nhật 1 nhân viên. login_id không đổi, password rỗng → giữ nguyên.
     * Dùng pattern delete-all-then-insert cho danh sách chứng chỉ để tránh merge phức tạp.
     *
     * @param request Dữ liệu gửi lên từ ADM005 (có employee_id)
     * @return EmployeeRegisterResponse (200 + MSG002, hoặc 500 + mã lỗi)
     */
    @Override
    @Transactional
    public EmployeeRegisterResponse updateEmployee(EmployeeRequest request) {
        try {
            // 0. Yêu cầu phải có employee_id
            if (request.getEmployeeId() == null) {
                return buildRegisterErrorResponse(MessageConstants.ER013, Collections.emptyList());
            }

            // 1. Load entity hiện tại – không tồn tại hoặc admin → ER013
            Employee employee = employeeRepository.findByEmployeeId(request.getEmployeeId()).orElse(null);
            if (employee == null || "admin".equals(employee.getEmployeeLoginId())) {
                return buildRegisterErrorResponse(MessageConstants.ER013, Collections.emptyList());
            }

            // 2. Ép request.loginId = loginId cũ (FE disable, BE đảm bảo không đổi)
            request.setEmployeeLoginId(employee.getEmployeeLoginId());

            // 3. Validate (isUpdate = true → bỏ password nếu rỗng, không check duplicate login_id)
            ValidationError err = employeeValidator.validate(request, true);
            if (err != null) return buildRegisterErrorResponse(err.getCode(), err.getParams());

            // 4. Update các field (skip null-password)
            applyRequestToEmployee(employee, request, true);
            if (request.getEmployeeLoginPassword() != null && !request.getEmployeeLoginPassword().isEmpty()) {
                employee.setEmployeeLoginPassword(passwordEncoder.encode(request.getEmployeeLoginPassword()));
            }
            employeeRepository.save(employee);

            // 5. Xoá hết chứng chỉ cũ → insert lại (đơn giản, tránh phân tích diff)
            employeeCertificationRepository.deleteByEmployeeId(employee.getEmployeeId());
            employeeCertificationRepository.flush();
            saveCertifications(employee, request.getCertifications());

            return buildRegisterSuccessResponse(employee.getEmployeeId(), MessageConstants.MSG002);
        } catch (Exception e) {
            log.error("Error updating employee id={}", request.getEmployeeId(), e);
            return buildRegisterErrorResponse(MessageConstants.ER015, Collections.emptyList());
        }
    }

    /**
     * Xoá 1 nhân viên. Ràng buộc:
     *  - Không tồn tại -> ER014
     *  - Là admin      -> ER020
     *  - Lỗi hệ thống  -> ER015
     *
     * @param employeeId ID nhân viên cần xoá
     * @return EmployeeRegisterResponse (200 + MSG003, hoặc 500 + mã lỗi)
     */
    @Override
    @Transactional
    public EmployeeRegisterResponse deleteEmployee(Long employeeId) {
        try {
            Employee employee = employeeRepository.findByEmployeeId(employeeId).orElse(null);
            if (employee == null) {
                return buildRegisterErrorResponse(MessageConstants.ER014, Collections.emptyList());
            }
            if ("admin".equals(employee.getEmployeeLoginId())) {
                return buildRegisterErrorResponse(MessageConstants.ER020, Collections.emptyList());
            }

            // Xoá chứng chỉ trước (FK) rồi xoá employee
            employeeCertificationRepository.deleteByEmployeeId(employeeId);
            employeeCertificationRepository.flush();
            employeeRepository.delete(employee);

            return buildRegisterSuccessResponse(employeeId, MessageConstants.MSG003);
        } catch (Exception e) {
            log.error("Error deleting employee id={}", employeeId, e);
            return buildRegisterErrorResponse(MessageConstants.ER015, Collections.emptyList());
        }
    }

    /**
     * Copy các field "ghi được" từ request sang entity.
     * Không đụng tới loginId khi update (đã set lại ở updateEmployee).
     * Password được xử lý riêng (hash ở caller).
     */
    private void applyRequestToEmployee(Employee employee, EmployeeRequest req, boolean isUpdate) {
        if (!isUpdate) {
            employee.setEmployeeLoginId(req.getEmployeeLoginId());
        }
        employee.setEmployeeName(req.getEmployeeName());
        employee.setEmployeeNameKana(req.getEmployeeNameKana());
        employee.setEmployeeBirthDate(employeeValidator.parseDate(req.getEmployeeBirthDate()));
        employee.setEmployeeEmail(req.getEmployeeEmail());
        employee.setEmployeeTelephone(req.getEmployeeTelephone());

        // Department (đã validate tồn tại)
        Department dept = departmentRepository.getOne(req.getDepartmentId());
        employee.setDepartment(dept);
    }

    /**
     * Insert toàn bộ danh sách chứng chỉ mới cho nhân viên.
     * Gọi sau khi đã xoá hết cert cũ (với update) hoặc sau khi save employee mới (với add).
     */
    private void saveCertifications(Employee employee, List<EmployeeCertificationRequest> certs) {
        if (certs == null || certs.isEmpty()) return;
        for (EmployeeCertificationRequest c : certs) {
            EmployeeCertification ec = new EmployeeCertification();
            ec.setEmployee(employee);
            Certification cert = certificationRepository.getOne(c.getCertificationId());
            ec.setCertification(cert);
            ec.setStartDate(employeeValidator.parseDate(c.getStartDate()));
            ec.setEndDate(employeeValidator.parseDate(c.getEndDate()));
            ec.setScore(new java.math.BigDecimal(c.getScore()));
            employeeCertificationRepository.save(ec);
        }
    }

    /**
     * Response thành công (200) cho add/update/delete.
     * @param employeeId ID nhân viên sau thao tác
     * @param msgCode   MSG001 / MSG002 / MSG003
     */
    private EmployeeRegisterResponse buildRegisterSuccessResponse(Long employeeId, String msgCode) {
        EmployeeRegisterResponse res = new EmployeeRegisterResponse();
        res.setCode(HttpStatus.OK.value());
        res.setEmployeeId(employeeId);
        res.setMessage(new MessageResponse(msgCode, Collections.emptyList()));
        return res;
    }

    /**
     * Response lỗi (500) cho add/update/delete.
     */
    private EmployeeRegisterResponse buildRegisterErrorResponse(String errorCode, List<String> params) {
        EmployeeRegisterResponse res = new EmployeeRegisterResponse();
        res.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        res.setMessage(new MessageResponse(errorCode, params == null ? Collections.emptyList() : params));
        return res;
    }

    /**
     * Tạo response thành công với HTTP status 200.
     *
     * set code khi trả về thành công
     *
     * @param totalRecords Tổng số bản ghi tìm thấy
     * @param employees    Danh sách nhân viên
     * @return EmployeeListResponse với code = "200"
     */
    private EmployeeListResponse buildSuccessResponse(Long totalRecords, List<EmployeeListDTO> employees) {
        EmployeeListResponse response = new EmployeeListResponse();
        response.setCode(HttpStatus.OK.value());
        response.setTotalRecords(totalRecords);
        response.setEmployees(employees);
        return response;
    }

    /**
     * Tạo response lỗi hệ thống với HTTP status 500.
     *
     * @param errorCode Mã lỗi (VD: ER015)
     * @return EmployeeListResponse với code = "500" và thông tin lỗi
     */
    private EmployeeListResponse buildErrorResponse(String errorCode) {
        EmployeeListResponse response = new EmployeeListResponse();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(new MessageResponse(errorCode, Collections.emptyList()));
        return response;
    }
}
