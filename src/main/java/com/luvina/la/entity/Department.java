/**
 * Copyright(C) 2026 Luvina Software
 * Department.java, 09/04/2026 nguyenanhngocminh
 */
package com.luvina.la.entity;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [Department.java], [Apr ,2026] [ntlong]
 */
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * Entity Department đại diện cho bảng departments lưu thông tin phòng ban.
 * 
 * @author [ntlong]
 */
@Entity
@Table(name = "departments")
@Data
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id", unique = true, nullable = false)
    private Long departmentId;

    @Column(name = "department_name", nullable = false)
    private String departmentName;

    @OneToMany(mappedBy = "department")
    @JsonIgnore
    private List<Employee> employees;
}
