package com.luvina.la.dto;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [CertificationDetailDTO.java], [Apr ,2026] [ntlong]
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO đại diện 1 dòng chứng chỉ tiếng Nhật của nhân viên (dùng trên ADM003/ADM004).
 *
 * @author [ntlong]
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificationDetailDTO {

    /** ID chứng chỉ (certifications.certification_id) */
    private Long certificationId;

    /** Cấp độ chứng chỉ (dùng để FE xác định thứ tự / hiển thị) */
    private Integer certificationLevel;

    /** Tên chứng chỉ (certifications.certification_name) */
    private String certificationName;

    /** Ngày cấp (yyyy/MM/dd) */
    private String startDate;

    /** Ngày hết hạn (yyyy/MM/dd) */
    private String endDate;

    /** Điểm số */
    private BigDecimal score;
}
