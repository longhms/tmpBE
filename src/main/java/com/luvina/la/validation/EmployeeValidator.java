package com.luvina.la.validation;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [EmployeeValidator.java], [Apr ,2026] [ntlong]
 */

import com.luvina.la.config.MessageConstants;
import com.luvina.la.payload.EmployeeCertificationRequest;
import com.luvina.la.payload.EmployeeRequest;
import com.luvina.la.repository.CertificationRepository;
import com.luvina.la.repository.DepartmentRepository;
import com.luvina.la.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validator cho chức năng thêm mới / cập nhật nhân viên (ADM004).
 *
 * Mỗi method validate* trả về:
 *   - null             -> hợp lệ
 *   - ValidationError  -> mã lỗi đầu tiên gặp phải (stop-on-first-error theo từng field)
 *
 * Thứ tự validate tổng thể: từ trên xuống dưới theo layout màn hình ADM004
 * (loginId -> password -> department -> name -> kana -> birthDate -> email -> phone -> certifications).
 *
 * @author [ntlong]
 */
@Component
@RequiredArgsConstructor
public class EmployeeValidator {

    // ── Các giới hạn độ dài ──
    private static final int MAX_LEN_LOGIN_ID = 50;
    private static final int MAX_LEN_NAME = 125;
    private static final int MAX_LEN_EMAIL = 125;
    private static final int MAX_LEN_PHONE = 50;
    private static final int MIN_LEN_PASSWORD = 8;
    private static final int MAX_LEN_PASSWORD = 50;
    private static final int MAX_LEN_SCORE = 3;

    // ── Các tên field tiếng Nhật (dùng đổ vào {0} trong message) ──
    private static final String FIELD_LOGIN_ID = "アカウント名";
    private static final String FIELD_PASSWORD = "パスワード";
    private static final String FIELD_DEPARTMENT = "グループ";
    private static final String FIELD_NAME = "氏名";
    private static final String FIELD_NAME_KANA = "カタカナ氏名";
    private static final String FIELD_BIRTH_DATE = "生年月日";
    private static final String FIELD_EMAIL = "メールアドレス";
    private static final String FIELD_PHONE = "電話番号";
    private static final String FIELD_CERTIFICATION = "資格";
    private static final String FIELD_START_DATE = "資格交付日";
    private static final String FIELD_END_DATE = "失効日";
    private static final String FIELD_SCORE = "点数";

    // ── Các pattern regex ──
    /** login_id: bắt đầu = chữ/underscore, các ký tự còn lại = [a-zA-Z0-9_] */
    private static final Pattern LOGIN_ID_PATTERN = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");
    /** password: chỉ half-size ASCII có thể in ra (loại khoảng trắng) */
    private static final Pattern HALF_SIZE_PATTERN = Pattern.compile("^[\\x21-\\x7E]+$");
    /** katakana (kèm dấu ー và khoảng trắng fullwidth) */
    private static final Pattern KATAKANA_PATTERN = Pattern.compile("^[\\u30A0-\\u30FF\\uFF65-\\uFF9F\\s]+$");
    /** chỉ digit + optional dấu chấm */
    private static final Pattern SCORE_PATTERN = Pattern.compile("^\\d+(\\.\\d+)?$");
    /** date yyyy/MM/dd (validate lexical), ngữ nghĩa validate qua DateTimeFormatter STRICT */
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("uuuu/MM/dd").withResolverStyle(ResolverStyle.STRICT);

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final CertificationRepository certificationRepository;

    /**
     * Validate toàn bộ EmployeeRequest.
     *
     * @param request    Dữ liệu gửi lên
     * @param isUpdate   true nếu là update (bỏ qua password nếu null/empty, skip check duplicate login_id)
     * @return           ValidationError đầu tiên tìm thấy, null nếu hợp lệ
     */
    public ValidationError validate(EmployeeRequest request, boolean isUpdate) {
        ValidationError err;

        // Login ID
        if ((err = validateLoginId(request.getEmployeeLoginId(), isUpdate)) != null) return err;

        // Password: khi update, null/empty => bỏ qua (không đổi mật khẩu)
        boolean skipPwd = isUpdate
                && (request.getEmployeeLoginPassword() == null
                    || request.getEmployeeLoginPassword().isEmpty());
        if (!skipPwd) {
            if ((err = validatePassword(request.getEmployeeLoginPassword())) != null) return err;
        }

        // Department
        if ((err = validateDepartment(request.getDepartmentId())) != null) return err;

        // Name / Kana / Birth
        if ((err = validateRequiredMaxLen(request.getEmployeeName(), MAX_LEN_NAME, FIELD_NAME)) != null) return err;
        if ((err = validateNameKana(request.getEmployeeNameKana())) != null) return err;
        if ((err = validateBirthDate(request.getEmployeeBirthDate())) != null) return err;

        // Email / Phone
        if ((err = validateEmail(request.getEmployeeEmail())) != null) return err;
        if ((err = validatePhone(request.getEmployeeTelephone())) != null) return err;

        // Certifications (optional – nhưng nếu có row thì tất cả field trong row bắt buộc)
        if (request.getCertifications() != null) {
            for (EmployeeCertificationRequest cert : request.getCertifications()) {
                if ((err = validateCertification(cert)) != null) return err;
            }
        }

        return null;
    }

    /**
     * bat buoc nhap + kiem tra maxlength.
     *
     * @param value     Gia tri can kiem tra
     * @param maxLen    Do dai toi da cho phep
     * @param fieldName Ten field (de do vao {0} cua message)
     */
    private ValidationError validateRequiredMaxLen(String value, int maxLen, String fieldName) {
        if (isEmpty(value)) return error(MessageConstants.ER001, fieldName);
        if (value.length() > maxLen) {
            return error(MessageConstants.ER006, String.valueOf(maxLen), fieldName);
        }
        return null;
    }

    // ── login_id ──
    private ValidationError validateLoginId(String loginId, boolean isUpdate) {
        if (isEmpty(loginId)) return error(MessageConstants.ER001, FIELD_LOGIN_ID);
        if (loginId.length() > MAX_LEN_LOGIN_ID) {
            return error(MessageConstants.ER006, String.valueOf(MAX_LEN_LOGIN_ID), FIELD_LOGIN_ID);
        }
        if (!LOGIN_ID_PATTERN.matcher(loginId).matches()) {
            return error(MessageConstants.ER019, Collections.emptyList());
        }
        // Check duplicate chỉ khi add (update không cho sửa login_id)
        if (!isUpdate && employeeRepository.existsByEmployeeLoginId(loginId)) {
            return error(MessageConstants.ER003, FIELD_LOGIN_ID);
        }
        return null;
    }

    // ── password ──
    private ValidationError validatePassword(String pwd) {
        if (isEmpty(pwd)) return error(MessageConstants.ER001, FIELD_PASSWORD);
        if (pwd.length() < MIN_LEN_PASSWORD || pwd.length() > MAX_LEN_PASSWORD) {
            return error(MessageConstants.ER007, FIELD_PASSWORD,
                    String.valueOf(MIN_LEN_PASSWORD), String.valueOf(MAX_LEN_PASSWORD));
        }
        if (!HALF_SIZE_PATTERN.matcher(pwd).matches()) {
            return error(MessageConstants.ER008, FIELD_PASSWORD);
        }
        return null;
    }

    // ── department_id ──
    private ValidationError validateDepartment(Long departmentId) {
        if (departmentId == null || departmentId <= 0) {
            return error(MessageConstants.ER002, FIELD_DEPARTMENT);
        }
        if (!departmentRepository.existsById(departmentId)) {
            return error(MessageConstants.ER004, FIELD_DEPARTMENT);
        }
        return null;
    }

    // ── name_kana ──
    private ValidationError validateNameKana(String kana) {
        if (isEmpty(kana)) return error(MessageConstants.ER001, FIELD_NAME_KANA);
        if (kana.length() > MAX_LEN_NAME) {
            return error(MessageConstants.ER006, String.valueOf(MAX_LEN_NAME), FIELD_NAME_KANA);
        }
        if (!KATAKANA_PATTERN.matcher(kana).matches()) {
            return error(MessageConstants.ER009, FIELD_NAME_KANA);
        }
        return null;
    }

    // ── birth_date ──
    private ValidationError validateBirthDate(String birth) {
        if (isEmpty(birth)) return error(MessageConstants.ER001, FIELD_BIRTH_DATE);
        if (parseDate(birth) == null) return error(MessageConstants.ER011, FIELD_BIRTH_DATE);
        return null;
    }

    // ── email ──
    private ValidationError validateEmail(String email) {
        if (isEmpty(email)) return error(MessageConstants.ER001, FIELD_EMAIL);
        if (email.length() > MAX_LEN_EMAIL) {
            return error(MessageConstants.ER006, String.valueOf(MAX_LEN_EMAIL), FIELD_EMAIL);
        }
        return null;
    }

    // ── phone ──
    private ValidationError validatePhone(String phone) {
        if (isEmpty(phone)) return error(MessageConstants.ER001, FIELD_PHONE);
        if (phone.length() > MAX_LEN_PHONE) {
            return error(MessageConstants.ER006, String.valueOf(MAX_LEN_PHONE), FIELD_PHONE);
        }
        if (!HALF_SIZE_PATTERN.matcher(phone).matches()) {
            return error(MessageConstants.ER008, FIELD_PHONE);
        }
        return null;
    }

    // ── certification row ──
    private ValidationError validateCertification(EmployeeCertificationRequest cert) {
        if (cert == null) return null;

        // certification_id
        if (cert.getCertificationId() == null || cert.getCertificationId() <= 0) {
            return error(MessageConstants.ER002, FIELD_CERTIFICATION);
        }
        if (!certificationRepository.existsById(cert.getCertificationId())) {
            return error(MessageConstants.ER004, FIELD_CERTIFICATION);
        }

        // start_date
        if (isEmpty(cert.getStartDate())) return error(MessageConstants.ER001, FIELD_START_DATE);
        LocalDate start = parseDate(cert.getStartDate());
        if (start == null) return error(MessageConstants.ER011, FIELD_START_DATE);

        // end_date
        if (isEmpty(cert.getEndDate())) return error(MessageConstants.ER001, FIELD_END_DATE);
        LocalDate end = parseDate(cert.getEndDate());
        if (end == null) return error(MessageConstants.ER011, FIELD_END_DATE);

        // end_date > start_date
        if (!end.isAfter(start)) return error(MessageConstants.ER012, Collections.emptyList());

        // score
        if (isEmpty(cert.getScore())) return error(MessageConstants.ER001, FIELD_SCORE);
        if (!SCORE_PATTERN.matcher(cert.getScore()).matches()) {
            return error(MessageConstants.ER008, FIELD_SCORE);
        }
        if (cert.getScore().length() > MAX_LEN_SCORE) {
            return error(MessageConstants.ER006, String.valueOf(MAX_LEN_SCORE), FIELD_SCORE);
        }
        return null;
    }

    /**
     * Parse date yyyy/MM/dd theo STRICT resolver (reject 2023/02/30, 2023/13/01…).
     * @return LocalDate nếu hợp lệ, null nếu không
     */
    public LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, DATE_FORMAT);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    private ValidationError error(String code, String... params) {
        return new ValidationError(code, Arrays.asList(params));
    }

    private ValidationError error(String code, List<String> params) {
        return new ValidationError(code, params);
    }
}
