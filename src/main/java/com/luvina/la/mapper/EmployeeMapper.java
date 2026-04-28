/*
 * Copyright(C) [2026] [Luvina Software Company]
 * [EmployeeMapper.java], [Apr ,2026] [ntlong]
 */
package com.luvina.la.mapper;

import com.luvina.la.config.Constants;
import com.luvina.la.dto.CertificationDetailDTO;
import com.luvina.la.dto.EmployeeDetailDTO;
import com.luvina.la.dto.EmployeeListDTO;
import com.luvina.la.entity.Certification;
import com.luvina.la.entity.Department;
import com.luvina.la.entity.Employee;
import com.luvina.la.entity.EmployeeCertification;
import com.luvina.la.payload.EmployeeRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp mapper tập trung toàn bộ logic chuyển đổi giữa Entity / Object[] / DTO / Request
 * cho module Employee.
 *
 * Tách khỏi Service để:
 *   - Service chỉ tập trung business logic.
 *   - Validate / Repository không lẫn logic mapping.
 *   - Dễ test riêng từng method mapping.
 *
 * @author [ntlong]
 */
@Component
public class EmployeeMapper {

    /**
     * Chuyển 1 Object[] (kết quả native query getEmployees) sang EmployeeListDTO.
     *
     * @param row 1 dòng kết quả native query (9 cột)
     * @return EmployeeListDTO tương ứng
     */
    public EmployeeListDTO toListDTO(Object[] row) {
        return new EmployeeListDTO(
                ((Number) row[0]).longValue(),
                (String) row[1],
                formatDateFromObject(row[2]),
                (String) row[3],
                (String) row[4],
                (String) row[5],
                (String) row[6],
                formatDateFromObject(row[7]),
                row[8] != null ? new BigDecimal(row[8].toString()) : null
        );
    }

    /**
     * Chuyển danh sách Object[] sang danh sách EmployeeListDTO.
     *
     * @param results Danh sách kết quả native query
     * @return Danh sách EmployeeListDTO đã map
     */
    public List<EmployeeListDTO> toListDTOs(List<Object[]> results) {
        return results.stream().map(this::toListDTO).collect(Collectors.toList());
    }

    /**
     * Chuyển Employee entity sang EmployeeDetailDTO (ADM003).
     * Sort chứng chỉ theo certification_level DESC (cấp cao đứng trước).
     * KHÔNG trả password.
     *
     * @param employee Entity Employee đã load (kèm department + certifications)
     * @return EmployeeDetailDTO sẵn sàng trả về client
     */
    public EmployeeDetailDTO toDetailDTO(Employee employee) {
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
        List<CertificationDetailDTO> certDtos = (certs == null
                ? Collections.<EmployeeCertification>emptyList()
                : certs).stream()
                .filter(ec -> ec.getCertification() != null)
                .sorted(Comparator.comparing(
                        (EmployeeCertification ec) -> ec.getCertification().getCertificationLevel(),
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toCertDTO)
                .collect(Collectors.toList());
        dto.setCertifications(certDtos);

        return dto;
    }

    /**
     * Chuyển 1 EmployeeCertification entity sang CertificationDetailDTO.
     *
     * @param ec EmployeeCertification entity
     * @return CertificationDetailDTO tương ứng
     */
    public CertificationDetailDTO toCertDTO(EmployeeCertification ec) {
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

    /**
     * Áp dữ liệu từ EmployeeRequest sang Employee entity (cho add).
     * Password được xử lý riêng ở Service.
     *
     * @param target Entity Employee đích (đã new hoặc đã load để update)
     * @param req    EmployeeRequest từ client
     * @param dept   Department reference (đã lấy bằng getReferenceById)
     */
    public void applyRequestToEntity(Employee target, EmployeeRequest req, Department dept) {
        target.setEmployeeLoginId(req.getEmployeeLoginId());
        target.setEmployeeName(req.getEmployeeName());
        target.setEmployeeNameKana(req.getEmployeeNameKana());
        target.setEmployeeBirthDate(parseDate(req.getEmployeeBirthDate()));
        target.setEmployeeEmail(req.getEmployeeEmail());
        target.setEmployeeTelephone(req.getEmployeeTelephone());
        target.setDepartment(dept);
    }

    /**
     * Format LocalDate sang chuỗi yyyy/MM/dd, null-safe.
     *
     * @param date Giá trị LocalDate (có thể null)
     * @return Chuỗi yyyy/MM/dd hoặc null nếu input null
     */
    public String formatDate(LocalDate date) {
        return date == null ? null : date.format(Constants.DATE_FORMAT);
    }

    /**
     * Format Object (từ native query, thường là java.sql.Date) sang yyyy/MM/dd.
     *
     * @param dateObj Object chứa giá trị ngày từ DB
     * @return Chuỗi yyyy/MM/dd hoặc null nếu input null
     */
    public String formatDateFromObject(Object dateObj) {
        if (dateObj == null) return null;
        return LocalDate.parse(dateObj.toString()).format(Constants.DATE_FORMAT);
    }

    /**
     * Parse chuỗi yyyy/MM/dd (STRICT) sang LocalDate. Sai format -> null.
     *
     * @param value Chuỗi cần parse
     * @return LocalDate hoặc null nếu không hợp lệ
     */
    public LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, Constants.DATE_FORMAT);
        } catch (Exception e) {
            return null;
        }
    }
}
