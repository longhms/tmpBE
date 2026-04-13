package com.luvina.la.controller;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [DepartmentController.java], [Apr ,2026] [ntlong]
 */
import com.luvina.la.config.MessageConstants;
import com.luvina.la.dto.DepartmentDTO;
import com.luvina.la.payload.DepartmentResponse;
import com.luvina.la.payload.MessageResponse;
import com.luvina.la.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * Controller xử lý các API liên quan đến Department.
 * author: [ntlong]
 */
@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService service;

    /**
     * API lấy danh sách tất cả phòng ban.
     *
     * @return DepartmentResponse
     */
    @GetMapping
    public DepartmentResponse getDepartments() {
        try {
            List<DepartmentDTO> departments = service.getAllDepartments();
            return new DepartmentResponse("200", departments, null);
        } catch (Exception e) {
            return new DepartmentResponse("500", null,
                    new MessageResponse(MessageConstants.ER015, Collections.emptyList()));
        }
    }
}
