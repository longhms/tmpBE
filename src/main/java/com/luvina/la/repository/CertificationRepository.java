package com.luvina.la.repository;/**
 * Copyright(C) [2026] [Luvina Software Company]
 * <p>
 * [.java], [Apr ,2026] [ntlong]
 */

import com.luvina.la.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * des
 * author : [ntlong]
 */
@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {
}
