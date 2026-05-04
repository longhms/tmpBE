package com.luvina.la.service;/**
 * Copyright(C) [2026] [Luvina Software Company]
 * <p>
 * [.java], [Apr ,2026] [ntlong]
 */

import com.luvina.la.dto.CertificationDTO;

import java.util.List;

/**
 * des
 * author : [ntlong]
 */
public interface CertificationService {
 /**
  * lâấy toaàn bộ danh sách certification.
  */
 List<CertificationDTO> getAllCertifications();

 /**
  * kiểm tra tồn tại certification
  * @return true nếu tồn tại
  */
 boolean certificationExists(Long id);
}
