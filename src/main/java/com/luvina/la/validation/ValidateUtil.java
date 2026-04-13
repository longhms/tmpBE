package com.luvina.la.validation;

import com.luvina.la.dto.EmployeeListDTO;
import com.luvina.la.payload.EmployeeListResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [ValidateUtil.java], [Apr ,2026] [ntlong]
 */
@Component
public class ValidateUtil {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /**
     * Validate giá trị sắp xếp phải là ASC hoặc DESC (hoặc null/empty).
     */
    public boolean isValidOrder(String order) {
        return order == null || order.isEmpty()
                || "ASC".equals(order) || "DESC".equals(order);
    }

    /**
     * Chuẩn hóa giá trị sort: null/empty → "ASC".
     */
    public String normalizeOrder(String order) {
        if (order == null || order.isEmpty()) return "ASC";
        return order;
    }


    public int normalizeOffset(Integer offset) {
        return (offset == null || offset < 0) ? 0 : offset;
    }

    public int normalizeLimit(Integer limit) {
        return (limit == null || limit <= 0) ? 5 : limit;
    }

    public String normalizeEmployeeName(String employeeName) {
        return (employeeName != null && !employeeName.isEmpty())
                ? employeeName : null;
    }

    /**
     * Chuyển đổi danh sách Object[] từ native query sang EmployeeListDTO.
     */
    public List<EmployeeListDTO> mapResultsToDtos(List<Object[]> results) {
        List<EmployeeListDTO> employees = new ArrayList<>();
        for (Object[] row : results) {
            employees.add(new EmployeeListDTO(
                    ((Number) row[0]).longValue(),    // employee_id
                    (String) row[1],                  // employee_name
                    formatDate(row[2]),               // employee_birth_date
                    (String) row[3],                  // department_name
                    (String) row[4],                  // employee_email
                    (String) row[5],                  // employee_telephone
                    (String) row[6],                  // certification_name
                    formatDate(row[7]),               // end_date
                    row[8] != null ? new BigDecimal(row[8].toString()) : null  // score
            ));
        }
        return employees;
    }

    /**
     * Format ngày từ Object sang chuỗi yyyy/MM/dd.
     */
    private String formatDate(Object dateObj) {
        if (dateObj == null) return null;
        return LocalDate.parse(dateObj.toString()).format(DATE_FORMAT);
    }

}
