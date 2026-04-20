package com.luvina.la.validation;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [ValidateUtil.java], [Apr ,2026] [ntlong]
 */

import com.luvina.la.config.Constants;
import com.luvina.la.dto.EmployeeListDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp tiện ích chứa các method validate và normalize dữ liệu cho chức năng Employee.
 *
 * - Validate: kiểm tra tính hợp lệ của các parameter đầu vào (sort order, offset, limit)
 * - Normalize: chuẩn hoá giá trị mặc định khi parameter null/empty
 * - Mapping: chuyển đổi kết quả native query (Object[]) sang DTO
 *
 * - EmployeeController: gọi validate trước khi xử lý
 * - EmployeeServiceImpl: gọi normalize và mapping
 *
 * @author [ntlong]
 */
@Component
public class ValidateUtil {

    /** Định dạng ngày tháng sử dụng về dạng: yyyy/MM/dd */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");


    /**
     * Validate giá trị sắp xếp phải là "ASC" hoặc "DESC".
     * Null hoặc empty được chấp nhận (sẽ dùng giá trị mặc định sau).
     *
     * Dùng cho: ord_employee_name, ord_certification_name, ord_end_date
     * Lỗi tương ứng: ER021 - "ソートは（ASC, DESC）でなければなりません。"
     *
     * @param order Giá trị sort cần validate
     * @return true nếu hợp lệ (ASC/DESC/null/empty), false nếu không hợp lệ
     */
    public boolean isValidOrder(String order) {
        return order == null || order.isEmpty()
                || "ASC".equals(order) || "DESC".equals(order);
    }

    /**
     * Validate giá trị phải là số nguyên >= 0.
     * Null hoặc empty được chấp nhận (sẽ dùng giá trị mặc định sau).
     *
     * Dùng cho: offset, limit
     * Lỗi tương ứng: ER018 - "「{0}」は半角で入力してください。"
     *
     * @param value Giá trị cần validate (dạng String từ request param)
     * @return true nếu hợp lệ (số nguyên >= 0 / null / empty), false nếu không hợp lệ
     */
    public boolean isNonNegativeInteger(String value) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        try {
            return Integer.parseInt(value) >= 0;
        } catch (NumberFormatException e) {
            // Không parse được thành số nguyên -> không hợp lệ
            return false;
        }
    }


    /**
     * Chuẩn hoá giá trị sort: null/empty -> "ASC" (mặc định sắp xếp tăng dần).
     *
     * @param order Giá trị sort từ request
     * @return "ASC" nếu null/empty, giữ nguyên nếu đã có giá trị
     */
    public String normalizeOrder(String order) {
        if (order == null || order.isEmpty()) return "ASC";
        return order;
    }

    /**
     * Chuẩn hoá offset: null hoặc giá trị âm -> 0.
     * Offset = 0 nghĩa là lấy từ bản ghi đầu tiên.
     *
     * @param offset Giá trị offset từ request
     * @return 0 nếu null/âm, giữ nguyên nếu hợp lệ
     */
    public int normalizeOffset(Integer offset) {
        return (offset == null || offset < 0) ? Constants.DEFAULT_OFFSET : offset;
    }

    /**
     * Chuẩn hoá limit: null hoặc giá trị <= 0 -> 20 (mặc định 20 bản ghi/trang).
     *
     * @param limit Giá trị limit từ request
     * @return 20 nếu null/<=0, giữ nguyên nếu hợp lệ
     */
    public int normalizeLimit(Integer limit) {
        return (limit == null || limit <= 0) ? Constants.DEFAULT_LIMIT : limit;
    }

    /**
     * Chuẩn hoá tên nhân viên cho điều kiện LIKE trong SQL.
     *
     * Empty string -> null (để bỏ qua điều kiện WHERE trong SQL).
     * Escape các ký tự đặc biệt của LIKE ('!', '%', '_') để khi user gõ
     *    chỉ tìm chuỗi chứa đúng ký tự "%", không bị coi là wildcard.
     *
     * @param employeeName Tên nhân viên từ request
     * @return null nếu empty, ngược lại trả về chuỗi đã escape các ký tự LIKE
     */
    public String normalizeEmployeeName(String employeeName) {
        if (employeeName == null || employeeName.isEmpty()) {
            return null;
        }
        return escapeLikePattern(employeeName);
    }

    /**
     * Escape các ký tự đặc biệt của SQL LIKE để tránh bị hiểu nhầm thành wildcard.
     *
     * Dùng '!' làm ký tự escape
     * Các ký tự cần escape:
     * - '!' : ký tự escape (phải thay thế đầu tiên để không escape lồng)
     * - '%' : match 0 hoặc nhiều ký tự bất kỳ
     * - '_' : match đúng 1 ký tự bất kỳ
     *
     * Dùng '!' làm ký tự escape thay '\' vì native query của Hibernate parse chuỗi '\'' trong "ESCAPE '\\'" sẽ gây lệch parameter index
     * (bug Hibernate khi gặp backslash trong SQL literal).
     *
     * VD: user gõ "50%_test!" -> sau escape: "50!%!_test!!"
     *
     * @param input Chuỗi nguyên bản từ người dùng
     * @return Chuỗi đã escape, an toàn để dùng trong vế LIKE ... ESCAPE '!'
     */
    public String escapeLikePattern(String input) {
        if (input == null) return null;
        return input
                .replace("!", "!!")   // escape dấu '!' trước tiên (tránh double-escape)
                .replace("%", "!%")   // escape dấu phần trăm
                .replace("_", "!_");  // escape dấu gạch dưới
    }

    /**
     * Chuyển đổi danh sách Object[] từ native query sang danh sách EmployeeListDTO.
     *
     * @param results Danh sách kết quả từ native query
     * @return Danh sách EmployeeListDTO đã được mapping
     */
    public List<EmployeeListDTO> mapResultsToDtos(List<Object[]> results) {
        List<EmployeeListDTO> employees = new ArrayList<>();
        for (Object[] row : results) {
            employees.add(new EmployeeListDTO(
                    ((Number) row[0]).longValue(),    // employee_id
                    (String) row[1],                  // employee_name
                    formatDate(row[2]),               // employee_birth_date -> yyyy/MM/dd
                    (String) row[3],                  // department_name
                    (String) row[4],                  // employee_email
                    (String) row[5],                  // employee_telephone
                    (String) row[6],                  // certification_name (nullable)
                    formatDate(row[7]),               // end_date -> yyyy/MM/dd (nullable)
                    row[8] != null ? new BigDecimal(row[8].toString()) : null  // score (nullable)
            ));
        }
        return employees;
    }

    /**
     * Format ngày từ Object sang chuỗi yyyy/MM/dd.
     * Dùng để hiển thị ngày sinh và ngày hết hạn chứng chỉ.
     *
     * @param dateObj Object chứa giá trị ngày từ database
     * @return Chuỗi ngày định dạng "yyyy/MM/dd", hoặc null nếu dateObj = null
     */
    private String formatDate(Object dateObj) {
        if (dateObj == null) return null;
        return LocalDate.parse(dateObj.toString()).format(DATE_FORMAT);
    }

}
