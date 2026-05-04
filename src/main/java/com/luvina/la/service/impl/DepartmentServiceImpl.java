package com.luvina.la.service.impl;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [DepartmentServiceImpl.java], [Apr ,2026] [ntlong]
 */

import com.luvina.la.dto.DepartmentDTO;
import com.luvina.la.entity.Department;
import com.luvina.la.repository.DepartmentRepository;
import com.luvina.la.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Triển khai DepartmentService.
 * author: [ntlong]
 */
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public List<DepartmentDTO> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll(
                Sort.by(Sort.Direction.ASC, "departmentName")
        );
        return departments.stream()
                .map(d -> new DepartmentDTO(d.getDepartmentId(), d.getDepartmentName()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean departmentExists(Long id) {
        return id != null && departmentRepository.existsById(id);
    }
}
