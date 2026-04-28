/*
 * Copyright(C) [2026] [Luvina Software Company]
 * [EmployeeServiceImpl.java], [Apr ,2026] [ntlong]
 */
package com.luvina.la.service.impl;

import com.luvina.la.config.MessageConstants;
import com.luvina.la.dto.EmployeeListDTO;
import com.luvina.la.entity.Certification;
import com.luvina.la.entity.Department;
import com.luvina.la.entity.Employee;
import com.luvina.la.entity.EmployeeCertification;
import com.luvina.la.exception.AppException;
import com.luvina.la.mapper.EmployeeMapper;
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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Triển khai EmployeeService - xử lý business logic cho chức năng Employee.
 *
 *   - Lấy danh sách nhân viên (search, sort, paging) từ database
 *   - Lấy chi tiết nhân viên (ADM003)
 *   - Thêm mới nhân viên kèm danh sách chứng chỉ
 *   - Xóa nhân viên (chặn xóa admin)
 *
 * Validate đầu vào đã được thực hiện ở Controller / EmployeeValidation
 * trước khi gọi Service. Mapping entity ↔ DTO ↔ Request được tách sang
 * EmployeeMapper để Service tập trung vào business logic.
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
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Lấy danh sách nhân viên với tìm kiếm, sắp xếp, phân trang.
     *
     *   Chuẩn hoá (normalize) các giá trị đầu vào (offset, limit, sort, filter)
     *   Đếm tổng số bản ghi thoả điều kiện
     *   Nếu totalRecords = 0 -> trả về danh sách rỗng (code 200)
     *   Lấy danh sách nhân viên từ DB với phân trang
     *   Chuyển đổi kết quả Object[] -> EmployeeListDTO qua EmployeeMapper
     *   Trả về response thành công (code 200)
     *
     * Thứ tự ưu tiên sort cố định: employeeName -> certificationName -> endDate -> employeeId.
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

            // Bước 6: Chuyển đổi kết quả native query (Object[]) sang DTO qua mapper
            List<EmployeeListDTO> employees = employeeMapper.toListDTOs(results);

            return EmployeeListResponse.success(totalRecords, employees);

        } catch (Exception e) {
            log.error("Error getting employees", e);
            return EmployeeListResponse.error(MessageConstants.ER015);
        }
    }

    /**
     * Khẳng định department và certification tồn tại trong DB.
     * Không tồn tại phòng ban sẽ throw AppException(ER004).
     *
     * @param departmentId    ID phòng ban (có thể null nếu chỉ check certification)
     * @param certificationId ID chứng chỉ (có thể null nếu chỉ check department)
     */
    @Override
    public void assertDepartmentAndCertificationExist(Long departmentId, Long certificationId) {
        employeeValidation.assertDepartmentAndCertificationExist(departmentId, certificationId);
    }

    /**
     * Thêm mới 1 nhân viên cùng danh sách chứng chỉ (transactional).
     *
     * Luồng:
     *   - Validate request qua EmployeeValidation
     *   - Lấy reference Department (đã validate tồn tại)
     *   - Map request -> Employee entity qua EmployeeMapper
     *   - Hash password và save Employee
     *   - Insert từng chứng chỉ kèm theo
     *
     * Lỗi business -> throw AppException, @Transactional rollback toàn bộ.
     *
     * @param request Dữ liệu gửi lên từ ADM005
     * @return employee_id vừa tạo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addEmployee(EmployeeRequest request) {
        employeeValidation.validate(request, false);

        Department dept = departmentRepository.getReferenceById(request.getDepartmentId());

        Employee employee = new Employee();
        employeeMapper.applyRequestToEntity(employee, request, dept);
        employee.setEmployeeLoginPassword(passwordEncoder.encode(request.getEmployeeLoginPassword()));

        Employee saveEmployee = employeeRepository.save(employee);
        saveCertifications(saveEmployee, request.getCertifications());

        return saveEmployee.getEmployeeId();
    }

    /**
     * Kiểm tra tồn tại nhân viên theo loginId.
     *
     * @param employeeLoginId loginId cần kiểm tra
     * @return true nếu đã tồn tại
     */
    @Override
    public boolean existsByEmployeeLoginId(String employeeLoginId) {
        return employeeRepository.existsByEmployeeLoginId(employeeLoginId);
    }

    /**
     * Xóa nhân viên theo ID.
     *
     *   - Không tồn tại -> ER014.
     *   - Là admin (loginId = "admin") -> ER020.
     *   - Xóa certifications trước (ràng buộc FK), sau đó xóa employee.
     *
     * @param employeeId ID nhân viên cần xóa
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(MessageConstants.ER014));

        if ("admin".equals(employee.getEmployeeLoginId())) {
            throw new AppException(MessageConstants.ER020);
        }

        employeeCertificationRepository.deleteByEmployeeId(employeeId);
        employeeRepository.delete(employee);
    }

    /**
     * Lấy chi tiết nhân viên cho ADM003.
     *
     * Luồng:
     *   - Tìm employee theo id, không có -> throw AppException(ER013).
     *   - Map entity -> EmployeeDetailDTO qua EmployeeMapper (không trả password).
     *   - Certifications được sort theo certification_level DESC ngay trong mapper.
     *
     * readOnly = true: chỉ đọc DB, cho phép Hibernate tối ưu + truy cập lazy collection.
     * Lỗi hệ thống (SQL, cast,...) sẽ rơi về GlobalExceptionHandler (500 ER015).
     *
     * @param employeeId ID nhân viên cần lấy chi tiết
     * @return EmployeeDetailResponse chứa thông tin đầy đủ của nhân viên
     */
    @Override
    @Transactional(readOnly = true)
    public EmployeeDetailResponse getEmployeeDetail(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(MessageConstants.ER013));

        return EmployeeDetailResponse.success(employeeMapper.toDetailDTO(employee));
    }

    /**
     * Insert toàn bộ danh sách chứng chỉ mới cho nhân viên.
     * Gọi sau khi đã save employee mới (trong luồng add).
     *
     * @param employee Employee đã được save (đã có employeeId)
     * @param certs    Danh sách chứng chỉ từ request (có thể null/empty)
     */
    private void saveCertifications(Employee employee, List<EmployeeCertificationRequest> certs) {
        if (certs == null || certs.isEmpty()) return;
        for (EmployeeCertificationRequest c : certs) {
            EmployeeCertification ec = new EmployeeCertification();
            ec.setEmployee(employee);
            Certification cert = certificationRepository.getReferenceById(c.getCertificationId());
            ec.setCertification(cert);
            ec.setStartDate(employeeMapper.parseDate(c.getStartDate()));
            ec.setEndDate(employeeMapper.parseDate(c.getEndDate()));
            String scoreStr = c.getScore();
            if (scoreStr != null && !scoreStr.isBlank()) {
                ec.setScore(new BigDecimal(scoreStr));
            }
            employeeCertificationRepository.save(ec);
        }
    }
}
