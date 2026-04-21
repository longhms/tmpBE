package com.luvina.la.dto;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [EmployeeCertificationDetailDTO.java], [Apr ,2026] [ntlong]
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO đại diện cho một chứng chỉ tiếng Nhật của nhân viên trong màn hình ADM003.
 *
 * Dùng cho response của API GET /employee/{id} (chi tiết nhân viên).
 * Danh sách các chứng chỉ được sắp xếp theo certification_level giảm dần.
 *
 * @author [ntlong]
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeCertificationDetailDTO {

    /** ID chứng chỉ (certification_id) */
    private Long certificationId;

    /** Ngày cấp chứng chỉ - format yyyy/MM/dd */
    private String startDate;

    /** Ngày hết hạn chứng chỉ - format yyyy/MM/dd */
    private String endDate;

    /** Điểm thi */
    private BigDecimal score;
}
