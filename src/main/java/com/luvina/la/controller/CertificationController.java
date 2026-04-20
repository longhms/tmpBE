package com.luvina.la.controller;/**
 * Copyright(C) [2026] [Luvina Software Company]
 * <p>
 * [.java], [Apr ,2026] [ntlong]
 */

import com.luvina.la.config.MessageConstants;
import com.luvina.la.dto.CertificationDTO;
import com.luvina.la.dto.DepartmentDTO;
import com.luvina.la.payload.CertificationResponse;
import com.luvina.la.payload.DepartmentResponse;
import com.luvina.la.payload.MessageResponse;
import com.luvina.la.service.CertificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * des
 * author : [ntlong]
 */
@RestController
@RequestMapping("/certification")
@RequiredArgsConstructor
public class CertificationController {
 private final CertificationService certificationService;

 /**
  * API lấy danh sách tất cả chung chi.
  *
  * @return CertificationResponse
  */
 @GetMapping
 public CertificationResponse getCertifications() {
  try {
   List<CertificationDTO> certificationDTOS = certificationService.getAllCertifications();
   return new CertificationResponse(HttpStatus.OK.value(), certificationDTOS, new MessageResponse(MessageConstants.MSG001, Collections.emptyList()));
  } catch (Exception e) {
   return new CertificationResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), null,
           new MessageResponse(MessageConstants.ER023, Collections.emptyList()));
  }
 }
}
