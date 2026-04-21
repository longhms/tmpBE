package com.luvina.la.repository;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [EmployeeRepository.java], [Apr ,2026] [ntlong]
 */

import com.luvina.la.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho Employee.
 * author: [ntlong]
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeLoginId(String employeeLoginId);

    Optional<Employee> findByEmployeeId(Long employeeId);

    /**
     * Kiểm tra login_id đã tồn tại chưa (dùng khi add).
     */
    boolean existsByEmployeeLoginId(String employeeLoginId);

    /**
     * Đếm tổng số nhân viên (loại trừ admin) theo điều kiện tìm kiếm.
     *
     * mệnh đề LIKE có "ESCAPE '!'" để các ký tự '%', '_', '\' đã được
     * escape ở tầng Service (ValidateUtil#escapeLikePattern) được hiểu là ký tự
     * thường, không phải wildcard.
     */
    @Query(value =
        "SELECT COUNT(e.employee_id) " +
        "FROM employees e " +
        "  INNER JOIN departments d ON e.department_id = d.department_id " +
        "WHERE e.employee_login_id != 'admin' " +
        "  AND (:employeeName IS NULL OR e.employee_name LIKE CONCAT('%', :employeeName, '%') ESCAPE '!') " +
        "  AND (:departmentId IS NULL OR e.department_id = :departmentId)",
        nativeQuery = true)
    Long countEmployees(
            @Param("employeeName") String employeeName,
            @Param("departmentId") Long departmentId);

    /**
     * Lấy danh sách nhân viên (loại trừ admin) với tìm kiếm, sắp xếp, phân trang.
     * Thứ tự sắp xếp cố định: employee_name → certification_name → end_date → employee_id.
     *
     * mệnh đề LIKE có "ESCAPE '!'" tương tự countEmployees để đảm bảo
     * ký tự đặc biệt ('%', '_', '\') không còn hiệu ứng wildcard sau khi được
     * escape ở tầng Service.
     */
    @Query(value =
        "SELECT " +
        "  e.employee_id, " +
        "  e.employee_name, " +
        "  e.employee_birth_date, " +
        "  d.department_name, " +
        "  e.employee_email, " +
        "  e.employee_telephone, " +
        "  c.certification_name, " +
        "  ec.end_date, " +
        "  ec.score " +
        "FROM employees e " +
        "  INNER JOIN departments d ON e.department_id = d.department_id " +
        "  LEFT JOIN employees_certifications ec ON e.employee_id = ec.employee_id " +
        "  LEFT JOIN certifications c ON ec.certification_id = c.certification_id " +
        "WHERE e.employee_login_id != 'admin' " +
        "  AND (:employeeName IS NULL OR e.employee_name LIKE CONCAT('%', :employeeName, '%') ESCAPE '!') " +
        "  AND (:departmentId IS NULL OR e.department_id = :departmentId) " +
        "ORDER BY " +
        "  CASE WHEN :ordEmployeeName = 'ASC' THEN e.employee_name END ASC, " +
        "  CASE WHEN :ordEmployeeName = 'DESC' THEN e.employee_name END DESC, " +
        "  CASE WHEN :ordCertificationName = 'ASC' THEN c.certification_level END DESC, " +
        "  CASE WHEN :ordCertificationName = 'DESC' THEN c.certification_level END ASC, " +
        "  CASE WHEN :ordEndDate = 'ASC' THEN ec.end_date END ASC, " +
        "  CASE WHEN :ordEndDate = 'DESC' THEN ec.end_date END DESC, " +
        "  e.employee_id ASC " +
        "LIMIT :limit OFFSET :offset",
        nativeQuery = true)
    List<Object[]> getEmployees(
            @Param("employeeName") String employeeName,
            @Param("departmentId") Long departmentId,
            @Param("ordEmployeeName") String ordEmployeeName,
            @Param("ordCertificationName") String ordCertificationName,
            @Param("ordEndDate") String ordEndDate,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit);

    /**
     * Lấy thông tin chi tiết của 1 nhân viên theo employee_id (loại trừ admin).
     *
     * Join với departments để lấy tên phòng ban.
     * Trả về 1 dòng duy nhất (hoặc rỗng nếu không tìm thấy).
     *
     * Các cột trả về:
     *  [0] employee_id
     *  [1] employee_login_id
     *  [2] department_id
     *  [3] department_name
     *  [4] employee_name
     *  [5] employee_name_kana
     *  [6] employee_birth_date
     *  [7] employee_email
     *  [8] employee_telephone
     */
    @Query(value =
        "SELECT " +
        "  e.employee_id, " +
        "  e.employee_login_id, " +
        "  d.department_id, " +
        "  d.department_name, " +
        "  e.employee_name, " +
        "  e.employee_name_kana, " +
        "  e.employee_birth_date, " +
        "  e.employee_email, " +
        "  e.employee_telephone " +
        "FROM employees e " +
        "  INNER JOIN departments d ON e.department_id = d.department_id " +
        "WHERE e.employee_id = :employeeId " +
        "  AND e.employee_login_id != 'admin'",
        nativeQuery = true)
    List<Object[]> findEmployeeDetailById(@Param("employeeId") Long employeeId);

    /**
     * Lấy danh sách chứng chỉ của 1 nhân viên, sắp xếp theo certification_level DESC.
     *
     * Các cột trả về:
     *  [0] certification_id
     *  [1] start_date
     *  [2] end_date
     *  [3] score
     */
    @Query(value =
        "SELECT " +
        "  c.certification_id, " +
        "  ec.start_date, " +
        "  ec.end_date, " +
        "  ec.score " +
        "FROM employees_certifications ec " +
        "  INNER JOIN certifications c ON ec.certification_id = c.certification_id " +
        "WHERE ec.employee_id = :employeeId " +
        "ORDER BY c.certification_level DESC",
        nativeQuery = true)
    List<Object[]> findCertificationsByEmployeeId(@Param("employeeId") Long employeeId);
}
