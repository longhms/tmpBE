package com.luvina.la.payload;/**
 * Copyright(C) [2026] [Luvina Software Company]
 * <p>
 * [MessageResponse.java], [Apr ,2026] [ntlong]
 */

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Response message
 * author : [ntlong]
 */
@Data
@AllArgsConstructor
public class MessageResponse {
 private String code;
 private List<String> params;
}
