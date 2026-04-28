package com.luvina.la.validation;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [EmployeeValidator.java], [Apr ,2026] [ntlong]
 */

import com.luvina.la.config.MessageConstants;
import com.luvina.la.exception.AppException;
import com.luvina.la.payload.EmployeeCertificationRequest;
import com.luvina.la.payload.EmployeeRequest;
import com.luvina.la.repository.CertificationRepository;
import com.luvina.la.repository.DepartmentRepository;
import com.luvina.la.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static com.luvina.la.config.Constants.*;

/**
 * Validator cho chức năng thêm mới / cập nhật nhân viên (ADM004).
 *
 * Mỗi method validate* là void: hợp lệ → không làm gì, sai → throw AppException.
 * GlobalExceptionHandler sẽ bắt và trả response 400 cho client.
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
     * Gặp lỗi đầu tiên → throw AppException (stop-on-first-error).
     *
     * @param request    Dữ liệu gửi lên
     * @param isUpdate   true nếu là update (bỏ qua password nếu null/empty, skip check duplicate login_id)
     */
    public void validate(EmployeeRequest request, boolean isUpdate) {

        validateLoginId(request.getEmployeeLoginId(), isUpdate);

        boolean skipPassword = isUpdate && isEmpty(request.getEmployeeLoginPassword());

        if (!skipPassword) validatePassword(request.getEmployeeLoginPassword());
        validateDepartment(request.getDepartmentId());
        validateRequiredMaxLen(request.getEmployeeName(), MAX_LEN_NAME, FIELD_NAME);
        validateNameKana(request.getEmployeeNameKana());
        validateBirthDate(request.getEmployeeBirthDate());
        validateEmail(request.getEmployeeEmail());
        validatePhone(request.getEmployeeTelephone());

        if (request.getCertifications() != null) {
            for (EmployeeCertificationRequest c : request.getCertifications()) {
                validateCertification(c);
            }
        }
    }

    /**
     * bat buoc nhap + kiem tra maxlength.
     *
     * @param value     Gia tri can kiem tra
     * @param maxLen    Do dai toi da cho phep
     * @param fieldName Ten field (de do vao {0} cua message)
     */
    private void validateRequiredMaxLen(String value, int maxLen, String fieldName) {
        if (isEmpty(value))
            throw new AppException(MessageConstants.ER001, fieldName);
        if (value.length() > maxLen) {
            throw new AppException(MessageConstants.ER006, fieldName, String.valueOf(maxLen));
        }
    }

    //login_id: bat buoc nhap + kiem tra maxlength + check trung
    private void validateLoginId(String loginId, boolean isUpdate) {
        validateRequiredMaxLen(loginId, MAX_LEN_LOGIN_ID, FIELD_LOGIN_ID);

        if (!LOGIN_ID_PATTERN.matcher(loginId).matches())
            throw new AppException(MessageConstants.ER019);

        if (!isUpdate && employeeRepository.existsByEmployeeLoginId(loginId))
            throw new AppException(MessageConstants.ER003, FIELD_LOGIN_ID);
    }

    //password: bat buoc nhap + kiem tra maxlength + check định dạng
    private void validatePassword(String pwd) {
        if (isEmpty(pwd))
            throw new AppException(MessageConstants.ER001, FIELD_PASSWORD);
        if (pwd.length() < MIN_LEN_PASSWORD || pwd.length() > MAX_LEN_PASSWORD) {
            throw new AppException(MessageConstants.ER007, FIELD_PASSWORD, String.valueOf(MIN_LEN_PASSWORD), String.valueOf(MAX_LEN_PASSWORD));
        }
        if (!HALF_SIZE_PATTERN.matcher(pwd).matches()) {
            throw new AppException(MessageConstants.ER008, FIELD_PASSWORD);
        }
    }

    // department_id: bat buoc nhap + kiem tra ton tai
    private void validateDepartment(Long departmentId) {
        if (departmentId == null || departmentId <= 0) {
            throw new AppException(MessageConstants.ER002, FIELD_DEPARTMENT);
        }
        if (!departmentRepository.existsById(departmentId)) {
            throw new AppException(MessageConstants.ER004, FIELD_DEPARTMENT);
        }
    }

    // name_kana: bat buoc nhap + kiem tra maxlength + check định dạng
    private void validateNameKana(String kana) {
        validateRequiredMaxLen(kana, MAX_LEN_NAME, FIELD_NAME_KANA);

        if (!KATAKANA_PATTERN.matcher(kana).matches()) {
            throw new AppException(MessageConstants.ER009, FIELD_NAME_KANA);
        }
    }

    // birth_date: bat buoc nhap + kiem tra định dạng
    private void validateBirthDate(String birth) {
        if (isEmpty(birth))
            throw new AppException(MessageConstants.ER001, FIELD_BIRTH_DATE);
        if (parseDate(birth) == null)
            throw new AppException(MessageConstants.ER011, FIELD_BIRTH_DATE);
    }

    // email: bat buoc nhap + kiem tra maxlength + check định dạng
    private void validateEmail(String email) {
        validateRequiredMaxLen(email, MAX_LEN_EMAIL, FIELD_EMAIL);
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new AppException(MessageConstants.ER005, FIELD_EMAIL);
        }
    }

    // phone: bat buoc nhap + kiem tra maxlength + check định dạng (chỉ digit và dấu '-')
    private void validatePhone(String phone) {
        validateRequiredMaxLen(phone, MAX_LEN_PHONE, FIELD_PHONE);
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new AppException(MessageConstants.ER008, FIELD_PHONE);
        }
    }

    // certification: bat buoc nhap + kiem tra ton tai + check định dạng ngày + score
    private void validateCertification(EmployeeCertificationRequest cert) {
        if (cert == null) return;

        // certification_id: bat buoc chon + kiem tra ton tai
        if (cert.getCertificationId() == null || cert.getCertificationId() <= 0) {
            throw new AppException(MessageConstants.ER002, FIELD_CERTIFICATION);
        }
        if (!certificationRepository.existsById(cert.getCertificationId())) {
            throw new AppException(MessageConstants.ER004, FIELD_CERTIFICATION);
        }

        // start_date: bat buoc nhap + kiem tra định dạng (check empty trước khi parse)
        if (isEmpty(cert.getStartDate())) {
            throw new AppException(MessageConstants.ER001, FIELD_START_DATE);
        }
        LocalDate start = parseDate(cert.getStartDate());
        if (start == null) {
            throw new AppException(MessageConstants.ER011, FIELD_START_DATE);
        }

        // end_date: bat buoc nhap + kiem tra định dạng
        if (isEmpty(cert.getEndDate())) {
            throw new AppException(MessageConstants.ER001, FIELD_END_DATE);
        }
        LocalDate end = parseDate(cert.getEndDate());
        if (end == null) {
            throw new AppException(MessageConstants.ER011, FIELD_END_DATE);
        }

        // end_date > start_date (ER012)
        if (!end.isAfter(start)) {
            throw new AppException(MessageConstants.ER012);
        }

        // score: bat buoc nhap + kiem tra maxlength + check định dạng
        validateRequiredMaxLen(cert.getScore(), MAX_LEN_SCORE, FIELD_SCORE);
        if (!SCORE_PATTERN.matcher(cert.getScore()).matches()) {
            throw new AppException(MessageConstants.ER008, FIELD_SCORE);
        }
    }

    /**
     * Khẳng định department và certification tồn tại trong DB.
     * Dùng cho endpoint /check-refs-exist (FE check trước khi submit).
     * Không tồn tại sẽ throw AppException(ER004).
     *
     * @param departmentId    ID phòng ban (null -> bỏ qua check phòng ban)
     * @param certificationId ID chứng chỉ (null -> bỏ qua check chứng chỉ)
     */
    public void assertDepartmentAndCertificationExist(Long departmentId, Long certificationId) {
        if (departmentId != null && !departmentRepository.existsById(departmentId)) {
            throw new AppException(MessageConstants.ER004, FIELD_DEPARTMENT);
        }
        if (certificationId != null && !certificationRepository.existsById(certificationId)) {
            throw new AppException(MessageConstants.ER004, FIELD_CERTIFICATION);
        }
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
}
