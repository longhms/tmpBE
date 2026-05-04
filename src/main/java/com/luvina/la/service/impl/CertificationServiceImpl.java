package com.luvina.la.service.impl;/**
 * Copyright(C) [2026] [Luvina Software Company]
 * <p>
 * [.java], [Apr ,2026] [ntlong]
 */

import com.luvina.la.dto.CertificationDTO;
import com.luvina.la.dto.DepartmentDTO;
import com.luvina.la.entity.Certification;
import com.luvina.la.entity.Department;
import com.luvina.la.repository.CertificationRepository;
import com.luvina.la.service.CertificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * des
 * author : [ntlong]
 */
@Service
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {
 private final CertificationRepository certificationRepository;

 /**
  * Trả về toàn bộ các chứng chỉ
  *
  */
 @Override
 public List<CertificationDTO> getAllCertifications() {
  List<Certification> certifications = certificationRepository.findAll(
          Sort.by(Sort.Direction.ASC, "certificationLevel")
  );
  return certifications.stream()
          .map(d -> new CertificationDTO(d.getCertificationId(), d.getCertificationName(), d.getCertificationLevel()))
          .collect(Collectors.toList());
 }

 /**
  * kiêểm tra chứng chỉ có tồn tại hay không?
  */
 @Override
 public boolean certificationExists(Long id) {
  return id != null && certificationRepository.existsById(id);
 }
}
