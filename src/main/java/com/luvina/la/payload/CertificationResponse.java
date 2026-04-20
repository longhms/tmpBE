package com.luvina.la.payload;/**
 * Copyright(C) [2026] [Luvina Software Company]
 * <p>
 * [.java], [Apr ,2026] [ntlong]
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.luvina.la.dto.CertificationDTO;
import com.luvina.la.dto.DepartmentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * des
 * author : [ntlong]
 */
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CertificationResponse {
 private int code;
 private List<CertificationDTO> certifications;
 private MessageResponse message;
}
