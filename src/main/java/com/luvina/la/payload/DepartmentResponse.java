package com.luvina.la.payload;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [DepartmentResponse.java], [Apr ,2026] [ntlong]
 */
import com.fasterxml.jackson.annotation.JsonInclude;
import com.luvina.la.dto.DepartmentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Response chứa danh sách phòng ban.
 * author: [ntlong]
 */
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentResponse {
    private String code;
    private List<DepartmentDTO> departments;
    private MessageResponse message;
}
