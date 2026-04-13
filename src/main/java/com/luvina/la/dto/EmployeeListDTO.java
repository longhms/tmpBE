package com.luvina.la.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * Copyright(C) [2026] [Luvina Software Company]
 * <p>
 * [EmployeeListDTO.java], [Apr ,2026] [ntlong]
 */
@Data
@AllArgsConstructor
public class EmployeeListDTO {
    private static final long serialVersionUID = 1L;

    private Long employeeId;
    private String employeeName;
    private String employeeBirthDate;
    private String departmentName;
    private String employeeEmail;
    private String employeeTelephone;
    private String certificationName;
    private String endDate;
    private BigDecimal score;
}

