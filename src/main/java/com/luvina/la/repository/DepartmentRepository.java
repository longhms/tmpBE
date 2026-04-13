package com.luvina.la.repository;/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [DepartmentRepository.java], [Apr ,2026] [ntlong]
 */

import com.luvina.la.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Department
 * author : [ntlong]
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

}
