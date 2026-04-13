package com.luvina.la.payload;/**
 * Copyright(C) [2026] [Luvina Software Company]
 * <p>
 * [EmployeeListResponse.java], [Apr ,2026] [ntlong]
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.luvina.la.config.Constants;
import com.luvina.la.dto.EmployeeListDTO;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Response danh sach employee
 * author : [ntlong]
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeListResponse {
 private String code;
 private Long totalRecords;
 private List<EmployeeListDTO> employees;
 private MessageResponse message;

 public EmployeeListResponse(Long totalRecords, List<EmployeeListDTO> employees) {
  this.code = String.valueOf(Constants.HTTP_CODE_200);
  this.totalRecords = totalRecords;
  this.employees = employees;
 }

 public EmployeeListResponse(String errorCode, List<String> params) {
  this.code = String.valueOf(Constants.HTTP_CODE_500);
  this.message = new MessageResponse(errorCode, params);
 }
}
