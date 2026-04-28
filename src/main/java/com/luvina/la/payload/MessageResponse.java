/*
 * Copyright(C) [2026] [Luvina Software Company]
 * [MessageResponse.java], [Apr ,2026] [ntlong]
 */
package com.luvina.la.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Cấu trúc thông tin message dùng chung cho mọi response.
 *
 *   - code  : Mã message (ER001, ER003, MSG001,...).
 *   - params: Danh sách tham số đổ vào placeholder {0}, {1},... của message.
 *
 * Được nhúng vào EmployeeMutationResponse / EmployeeListResponse / ApiErrorResponse
 * để FE chỉ cần parse đúng 1 cấu trúc message duy nhất.
 *
 * @author [ntlong]
 */
@Data
@AllArgsConstructor
public class MessageResponse {

    /** Mã message (ERxxx / MSGxxx) */
    private String code;

    /** Danh sách tham số đổ vào placeholder của message */
    private List<String> params;
}
