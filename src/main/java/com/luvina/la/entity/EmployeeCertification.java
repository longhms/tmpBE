/**
 * Copyright(C) 2026 Luvina Software
 * EmployeeCertification.java, 09/04/2026 nguyenanhngocminh
 */
package com.luvina.la.entity;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [EmployeeCertification.java], [Apr ,2026] [ntlong]
 */

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import javax.persistence.*;

import lombok.Data;

/**
 * Entity EmployeeCertification đại diện cho bảng employees_certifications
 * lưu thông tin quan hệ giữa nhân viên và chứng chỉ tiếng Nhật.
 * 
 * @author [ntlong]
 */
@Entity
@Table(name = "employees_certifications")
@Data
public class EmployeeCertification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_certification_id", unique = true, nullable = false)
    private Long employeeCertificationId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "certification_id", nullable = false)
    private Certification certification;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "score", nullable = false)
    private BigDecimal score;
}
