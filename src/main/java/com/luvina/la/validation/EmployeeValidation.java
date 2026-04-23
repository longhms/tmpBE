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
import com.luvina.la.payload.ValidationErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.luvina.la.config.Constants.*;

/**
 * Validator cho chức năng thêm mới / cập nhật nhân viên (ADM004).
 *
 * Mỗi method validate* trả về:
 *   - null             -> hợp lệ
 *   - ValidationErrorResponse  -> mã lỗi đầu tiên gặp phải (stop-on-first-error theo từng field)
 *
 * Thứ tự validate tổng thể: từ trên xuống dưới theo layout màn hình ADM004
 * (loginId -> password -> department -> name -> kana -> birthDate -> email -> phone -> certifications).
 *
 * @author [ntlong]
 */
@Component
@RequiredArgsConstructor
public class EmployeeValidation {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final CertificationRepository certificationRepository;

    /**
     * Validate toàn bộ EmployeeRequest.
     *
     * @param request    Dữ liệu gửi lên
     * @param isUpdate   true nếu là update (bỏ qua password nếu null/empty, skip check duplicate login_id)
     * @return           ValidationErrorResponse đầu tiên tìm thấy, null nếu hợp lệ
     */
    public ValidationErrorResponse validate(EmployeeRequest request, boolean isUpdate) {
        ValidationErrorResponse err;

        // Validate loginId:bat buoc nhap + kiem tra maxlength + check trung
        if ((err = validateLoginId(request.getEmployeeLoginId(), isUpdate)) != null) return err;

        // Validate password:bat buoc nhap + kiem tra maxlength + check định dạng
        boolean skipPwd = isUpdate
                && (request.getEmployeeLoginPassword() == null
                    || request.getEmployeeLoginPassword().isEmpty());
        if (!skipPwd) {
            if ((err = validatePassword(request.getEmployeeLoginPassword())) != null) return err;
        }

        // Validate department:bat buoc nhap + kiem tra ton tai
        if ((err = validateDepartment(request.getDepartmentId())) != null) return err;

        // Validate name:bat buoc nhap + kiem tra maxlength
        if ((err = validateRequiredMaxLen(request.getEmployeeName(), MAX_LEN_NAME, FIELD_NAME)) != null) return err;
        // Validate kana:bat buoc nhap + kiem tra maxlength + check định dạng
        if ((err = validateNameKana(request.getEmployeeNameKana())) != null) return err;
        // Validate birthDate:bat buoc nhap + kiem tra định dạng
        if ((err = validateBirthDate(request.getEmployeeBirthDate())) != null) return err;

        // Validate email:bat buoc nhap + kiem tra maxlength + check định dạng
        if ((err = validateEmail(request.getEmployeeEmail())) != null) return err;
        // Validate phone:bat buoc nhap + kiem tra maxlength + check định dạng
        if ((err = validatePhone(request.getEmployeeTelephone())) != null) return err;

        // Validate certifications:bat buoc nhap + kiem tra maxlength + check định dạng
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
    private ValidationErrorResponse validateRequiredMaxLen(String value, int maxLen, String fieldName) {
        if (isEmpty(value)) return error(MessageConstants.ER001, fieldName);
        if (value.length() > maxLen) {
            return error(MessageConstants.ER006, String.valueOf(maxLen), fieldName);
        }
        return null;
    }

    //login_id: bat buoc nhap + kiem tra maxlength + check trung
    private ValidationErrorResponse validateLoginId(String loginId, boolean isUpdate) {
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

    //password: bat buoc nhap + kiem tra maxlength + check định dạng
    private ValidationErrorResponse validatePassword(String pwd) {
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

    // department_id: bat buoc nhap + kiem tra ton tai
    private ValidationErrorResponse validateDepartment(Long departmentId) {
        if (departmentId == null || departmentId <= 0) {
            return error(MessageConstants.ER002, FIELD_DEPARTMENT);
        }
        if (!departmentRepository.existsById(departmentId)) {
            return error(MessageConstants.ER004, FIELD_DEPARTMENT);
        }
        return null;
    }

    // name_kana: bat buoc nhap + kiem tra maxlength + check định dạng
    private ValidationErrorResponse validateNameKana(String kana) {
        if (isEmpty(kana)) return error(MessageConstants.ER001, FIELD_NAME_KANA);
        if (kana.length() > MAX_LEN_NAME) {
            return error(MessageConstants.ER006, String.valueOf(MAX_LEN_NAME), FIELD_NAME_KANA);
        }
        if (!KATAKANA_PATTERN.matcher(kana).matches()) {
            return error(MessageConstants.ER009, FIELD_NAME_KANA);
        }
        return null;
    }

    // birth_date: bat buoc nhap + kiem tra định dạng
    private ValidationErrorResponse validateBirthDate(String birth) {
        if (isEmpty(birth)) return error(MessageConstants.ER001, FIELD_BIRTH_DATE);
        if (parseDate(birth) == null) return error(MessageConstants.ER011, FIELD_BIRTH_DATE);
        return null;
    }

    // email: bat buoc nhap + kiem tra maxlength + check định dạng
    private ValidationErrorResponse validateEmail(String email) {
        if (isEmpty(email)) return error(MessageConstants.ER001, FIELD_EMAIL);
        if (email.length() > MAX_LEN_EMAIL) {
            return error(MessageConstants.ER006, String.valueOf(MAX_LEN_EMAIL), FIELD_EMAIL);
        }
        return null;
    }

    // phone: bat buoc nhap + kiem tra maxlength + check định dạng
    private ValidationErrorResponse validatePhone(String phone) {
        if (isEmpty(phone)) return error(MessageConstants.ER001, FIELD_PHONE);
        if (phone.length() > MAX_LEN_PHONE) {
            return error(MessageConstants.ER006, String.valueOf(MAX_LEN_PHONE), FIELD_PHONE);
        }
        if (!HALF_SIZE_PATTERN.matcher(phone).matches()) {
            return error(MessageConstants.ER008, FIELD_PHONE);
        }
        return null;
    }

    // certification:bat buoc nhap + kiem tra maxlength + check định dạng
    private ValidationErrorResponse validateCertification(EmployeeCertificationRequest cert) {
        if (cert == null) return null;

        // certification_id: bat buoc nhap + kiem tra ton tai
        if (cert.getCertificationId() == null || cert.getCertificationId() <= 0) {
            return error(MessageConstants.ER002, FIELD_CERTIFICATION);
        }
        if (!certificationRepository.existsById(cert.getCertificationId())) {
            return error(MessageConstants.ER004, FIELD_CERTIFICATION);
        }

        // start_date:bat buoc nhap + kiem tra định dạng
        if (isEmpty(cert.getStartDate())) return error(MessageConstants.ER001, FIELD_START_DATE);
        LocalDate start = parseDate(cert.getStartDate());
        if (start == null) return error(MessageConstants.ER011, FIELD_START_DATE);

        // end_date: bat buoc nhap + kiem tra định dạng
        if (isEmpty(cert.getEndDate())) return error(MessageConstants.ER001, FIELD_END_DATE);
        LocalDate end = parseDate(cert.getEndDate());
        if (end == null) return error(MessageConstants.ER011, FIELD_END_DATE);

        // end_date > start_date: kiem tra ngay ket thuc lon hon ngay bat dau
        if (!end.isAfter(start)) return error(MessageConstants.ER012, Collections.emptyList());

        // score: bat buoc nhap + kiem tra maxlength + check định dạng
        if (isEmpty(cert.getScore())) return error(MessageConstants.ER001, FIELD_SCORE);
        if (cert.getScore().length() > MAX_LEN_SCORE) {
            return error(MessageConstants.ER006, String.valueOf(MAX_LEN_SCORE), FIELD_SCORE);
        }
        if (!SCORE_PATTERN.matcher(cert.getScore()).matches()) {
            return error(MessageConstants.ER008, FIELD_SCORE);
        }
        return null;
    }

    /**
     * Check tồn tại department / certification (dùng cho endpoint /validate-refs).
     * Tái sử dụng logic existsById đã có sẵn để không duplicate.
     *
     * @return ValidationErrorResponse (ER004) nếu không tồn tại, null nếu hợp lệ
     */
    public ValidationErrorResponse validateRefs(Long departmentId, Long certificationId) {
        if (departmentId != null && !departmentRepository.existsById(departmentId)) {
            return error(MessageConstants.ER004, FIELD_DEPARTMENT);
        }
        if (certificationId != null && !certificationRepository.existsById(certificationId)) {
            return error(MessageConstants.ER004, FIELD_CERTIFICATION);
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
    
    // ham kiem tra string null hoac empty
    private boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    // hàm tạo error không tham số
    private ValidationErrorResponse error(String code, String... params) {
        return new ValidationErrorResponse(code, Arrays.asList(params));
    }

    //hàm tạo error không có tham số
    private ValidationErrorResponse error(String code, List<String> params) {
        return new ValidationErrorResponse(code, params);
    }
}
