package com.luvina.la.service.impl;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [EmployeeServiceImpl.java], [Apr ,2026] [ntlong]
 */

import com.luvina.la.config.Constants;
import com.luvina.la.config.MessageConstants;
import com.luvina.la.dto.CertificationDetailDTO;
import com.luvina.la.dto.EmployeeDetailDTO;
import com.luvina.la.dto.EmployeeListDTO;
import com.luvina.la.entity.Certification;
import com.luvina.la.entity.Department;
import com.luvina.la.entity.Employee;
import com.luvina.la.entity.EmployeeCertification;
import com.luvina.la.exception.BusinessException;
import com.luvina.la.payload.EmployeeCertificationRequest;
import com.luvina.la.payload.EmployeeDetailResponse;
import com.luvina.la.payload.EmployeeListResponse;
import com.luvina.la.payload.EmployeeRequest;
import com.luvina.la.repository.CertificationRepository;
import com.luvina.la.repository.DepartmentRepository;
import com.luvina.la.repository.EmployeeCertificationRepository;
import com.luvina.la.repository.EmployeeRepository;
import com.luvina.la.service.EmployeeService;
import com.luvina.la.validation.EmployeeValidation;
import com.luvina.la.validation.ValidateUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    private final EmployeeValidation employeeValidation;
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
     * Delegate sang EmployeeValidation. Không tồn tại → throw BusinessException(ER004).
     */
    @Override
    public void validateRefs(Long departmentId, Long certificationId) {
        employeeValidation.validateRefs(departmentId, certificationId);
    }

    /**
     * Thêm mới 1 nhân viên cùng danh sách chứng chỉ (transactional).
     *
     * Luồng: validate → hash password → build Employee entity → save Employee →
     * insert từng chứng chỉ. Lỗi → throw BusinessException, @Transactional rollback.
     *
     * @param request Dữ liệu gửi lên từ ADM005
     * @return employee_id vừa tạo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addEmployee(EmployeeRequest request) {
        employeeValidation.validate(request, false);

        Employee employee = new Employee();
        applyRequestToEmployee(employee, request);

        employee.setEmployeeLoginPassword(passwordEncoder.encode(request.getEmployeeLoginPassword()));
        Employee saveEmployee = employeeRepository.save(employee);

        saveCertifications(saveEmployee, request.getCertifications());

        return saveEmployee.getEmployeeId();
    }

    @Override
    public boolean existsByEmployeeLoginId(String employeeLoginId) {
        return employeeRepository.existsByEmployeeLoginId(employeeLoginId);
    }

    /**
     * Xóa nhân viên theo ID.
     * - Không tồn tại → ER014.
     * - Là admin (loginId = "admin") → ER020.
     * - Xóa certifications trước (FK constraint), sau đó xóa employee.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException(MessageConstants.ER014));

        if ("admin".equals(employee.getEmployeeLoginId())) {
            throw new BusinessException(MessageConstants.ER020);
        }

        employeeCertificationRepository.deleteByEmployeeId(employeeId);
        employeeRepository.delete(employee);
    }

    /**
     * Lấy chi tiết nhân viên cho ADM003.
     *
     * Luồng:
     *   - Tìm employee theo id -> không có → throw BusinessException(ER013).
     *   - Map entity -> EmployeeDetailDTO (không trả password).
     *   - Certifications: sort theo certification_level DESC (cấp cao đứng trước).
     *
     * readOnly = true: chỉ đọc DB, cho phép Hibernate tối ưu + truy cập lazy collection.
     * Lỗi hệ thống (SQL, cast,…) sẽ rơi về GlobalExceptionHandler (500 ER015).
     */
    @Override
    @Transactional(readOnly = true)
    public EmployeeDetailResponse getEmployeeDetail(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException(MessageConstants.ER013));

        EmployeeDetailDTO dto = toDetailDTO(employee);
        return EmployeeDetailResponse.success(dto);
    }

    /**
     * Map Employee entity -> EmployeeDetailDTO.
     * Sort certifications theo certification_level DESC.
     */
    private EmployeeDetailDTO toDetailDTO(Employee employee) {
        EmployeeDetailDTO dto = new EmployeeDetailDTO();
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setEmployeeLoginId(employee.getEmployeeLoginId());
        dto.setEmployeeName(employee.getEmployeeName());
        dto.setEmployeeNameKana(employee.getEmployeeNameKana());
        dto.setEmployeeBirthDate(formatDate(employee.getEmployeeBirthDate()));
        dto.setEmployeeEmail(employee.getEmployeeEmail());
        dto.setEmployeeTelephone(employee.getEmployeeTelephone());

        Department dept = employee.getDepartment();
        if (dept != null) {
            dto.setDepartmentId(dept.getDepartmentId());
            dto.setDepartmentName(dept.getDepartmentName());
        }

        List<EmployeeCertification> certs = employee.getCertifications();
        List<CertificationDetailDTO> certDtos = (certs == null ? Collections.<EmployeeCertification>emptyList() : certs)
                .stream()
                .filter(ec -> ec.getCertification() != null)
                .sorted(Comparator.comparing(
                        (EmployeeCertification ec) -> ec.getCertification().getCertificationLevel(),
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toCertDTO)
                .collect(Collectors.toList());
        dto.setCertifications(certDtos);

        return dto;
    }

    /** Map 1 EmployeeCertification -> CertificationDetailDTO */
    private CertificationDetailDTO toCertDTO(EmployeeCertification ec) {
        Certification c = ec.getCertification();
        return new CertificationDetailDTO(
                c.getCertificationId(),
                c.getCertificationLevel(),
                c.getCertificationName(),
                formatDate(ec.getStartDate()),
                formatDate(ec.getEndDate()),
                ec.getScore()
        );
    }

    /** Format LocalDate -> yyyy/MM/dd, null-safe */
    private String formatDate(LocalDate date) {
        return date == null ? null : date.format(Constants.DATE_FORMAT);
    }


    /**
     * lấy dữ liệu từ EmployeeRequest
     * Password được xử lý riêng ở addEmployee.
     */
    private void applyRequestToEmployee(Employee employee, EmployeeRequest req) {
        employee.setEmployeeLoginId(req.getEmployeeLoginId());
        employee.setEmployeeName(req.getEmployeeName());
        employee.setEmployeeNameKana(req.getEmployeeNameKana());
        employee.setEmployeeBirthDate(employeeValidation.parseDate(req.getEmployeeBirthDate()));
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
            ec.setStartDate(employeeValidation.parseDate(c.getStartDate()));
            ec.setEndDate(employeeValidation.parseDate(c.getEndDate()));
            String scoreStr = c.getScore();
            if (scoreStr != null && !scoreStr.isBlank()) {
                ec.setScore(new BigDecimal(scoreStr));
            }
            employeeCertificationRepository.save(ec);
        }
    }


}
