/**
 * Copyright(C) 2026 Luvina Software
 * Employee.java, 09/04/2026 nguyenanhngocminh
 */
package com.luvina.la.entity;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [Employee.java], [Apr ,2026] [ntlong]
 */
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * Entity Employee đại diện cho bảng employees lưu thông tin nhân viên.
 * 
 * @author [ntlong]
 */
@Entity
@Table(name = "employees")
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 5771173953267484096L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id", unique = true, nullable = false)
    private Long employeeId;

    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    @Column(name = "employee_name_kana")
    private String employeeNameKana;

    @Column(name = "employee_birth_date")
    private LocalDate employeeBirthDate;

    @Column(name = "employee_email", nullable = false)
    private String employeeEmail;

    @Column(name = "employee_telephone")
    private String employeeTelephone;

    @Column(name = "employee_login_id", nullable = false)
    private String employeeLoginId;

    @Column(name = "employee_login_password", nullable = false)
    private String employeeLoginPassword;

    @OneToMany(mappedBy = "employee")
    @JsonIgnore
    private List<EmployeeCertification> certifications;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

}
