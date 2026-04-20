package com.luvina.la.dto;/**
 * Copyright(C) [2026] [Luvina Software Company]
 * <p>
 * [.java], [Apr ,2026] [ntlong]
 */

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * des
 * author : [ntlong]
 */
@Data
@AllArgsConstructor
public class CertificationDTO {
 private Long certificationId;
 private String certificationName;
 private Integer certificationLevel;

}
