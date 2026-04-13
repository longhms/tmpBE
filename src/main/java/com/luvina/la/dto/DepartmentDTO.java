package com.luvina.la.dto;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [DepartmentDTO.java], [Apr ,2026] [ntlong]
 */
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO chứa thông tin phòng ban trả về cho client.
 * author: [ntlong]
 */
@Data
@AllArgsConstructor
public class DepartmentDTO {
    private Long departmentId;
    private String departmentName;
}
