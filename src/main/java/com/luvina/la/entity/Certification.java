/**
 * Copyright(C) 2026 Luvina Software
 * Certification.java, 09/04/2026 nguyenanhngocminh
 */
package com.luvina.la.entity;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [Certification.java], [Apr ,2026] [ntlong]
 */
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

import lombok.Data;

/**
 * Entity Certification đại diện cho bảng certifications lưu thông tin chứng chỉ tiếng Nhật.
 * 
 * @author [ntlong]
 */
@Entity
@Table(name = "certifications")
@Data
public class Certification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certification_id", unique = true, nullable = false)
    private Long certificationId;

    @Column(name = "certification_name", nullable = false)
    private String certificationName;

    @Column(name = "certification_level", nullable = false)
    private Integer certificationLevel;

    @OneToMany(mappedBy = "certification")
    private List<EmployeeCertification> employees;
}
