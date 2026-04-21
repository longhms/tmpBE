package com.luvina.la.repository;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [EmployeeCertificationRepository.java], [Apr ,2026] [ntlong]
 */

import com.luvina.la.entity.EmployeeCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository cho bảng employees_certifications.
 * Dùng để xoá toàn bộ chứng chỉ cũ của 1 nhân viên khi update
 * (pattern delete-all-then-insert).
 *
 * @author [ntlong]
 */
@Repository
public interface EmployeeCertificationRepository extends JpaRepository<EmployeeCertification, Long> {

    /**
     * Xoá tất cả chứng chỉ của 1 nhân viên theo employee_id.
     * @param employeeId ID nhân viên
     */
    @Modifying
    @Query("DELETE FROM EmployeeCertification ec WHERE ec.employee.employeeId = :employeeId")
    void deleteByEmployeeId(@Param("employeeId") Long employeeId);
}
