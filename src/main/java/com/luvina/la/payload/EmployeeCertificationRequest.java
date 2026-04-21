package com.luvina.la.payload;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [EmployeeCertificationRequest.java], [Apr ,2026] [ntlong]
 */

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Dữ liệu 1 chứng chỉ trong request add/update nhân viên (ADM004).
 *
 * Ngày được truyền lên dạng String yyyy/MM/dd để validator kiểm tra định dạng
 * trước khi parse sang LocalDate.
 *
 * @author [ntlong]
 */
@Data
@NoArgsConstructor
public class EmployeeCertificationRequest {

    /** ID chứng chỉ (FK tới bảng certifications) */
    private Long certificationId;

    /** Ngày cấp (yyyy/MM/dd) */
    private String startDate;

    /** Ngày hết hạn (yyyy/MM/dd) */
    private String endDate;

    /** Điểm thi (nhập dưới dạng chuỗi để validate half-size digit) */
    private String score;
}
