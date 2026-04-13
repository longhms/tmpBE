package com.luvina.la.service;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [DepartmentService.java], [Apr ,2026] [ntlong]
 */

import com.luvina.la.dto.DepartmentDTO;

import java.util.List;

/**
 * Interface Service cho Department.
 * author: [ntlong]
 */
public interface DepartmentService {

    /**
     * Lấy tất cả phòng ban, sắp xếp theo tên tăng dần.
     */
    List<DepartmentDTO> getAllDepartments();
}
