/*
 * Copyright(C) [2026] [Luvina Software Company]
 * [EmployeeValidation.java], [Apr ,2026] [ntlong]
 */
package com.luvina.la.validation;

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
 * Mỗi method validate* là void: hợp lệ -> không làm gì, sai -> throw AppException.
 * GlobalExceptionHandler sẽ bắt và trả response 400 cho client.
 *
 * Thứ tự validate tổng thể: từ trên xuống dưới theo layout màn hình ADM004
 * (loginId -> password -> department -> name -> kana -> birthDate -> email
 *  -> phone -> certifications).
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
     * Validate toàn bộ EmployeeRequest theo thứ tự field trên màn hình ADM004.
     * Gặp lỗi đầu tiên sẽ throw AppException (stop-on-first-error).
     *
     * @param request  Dữ liệu gửi lên từ client
     * @param isUpdate true nếu là update (bỏ qua check duplicate loginId; password
     *                 null/empty -> skip)
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
     * Bắt buộc nhập + kiểm tra maxlength cho 1 field text dạng String.
     *
     * @param value     Giá trị cần kiểm tra
     * @param maxLen    Độ dài tối đa cho phép
     * @param fieldName Tên field (dùng để đổ vào {0} của message)
     */
    private void validateRequiredMaxLen(String value, int maxLen, String fieldName) {
        if (isEmpty(value))
            throw new AppException(MessageConstants.ER001, fieldName);
        if (value.length() > maxLen) {
            throw new AppException(MessageConstants.ER006, fieldName, String.valueOf(maxLen));
        }
    }

    /**
     * Validate login_id: bắt buộc + maxlength + đúng pattern + check trùng (chỉ khi add).
     *
     * @param loginId  Giá trị login_id từ request
     * @param isUpdate true -> bỏ qua check trùng trong DB
     */
    private void validateLoginId(String loginId, boolean isUpdate) {
        validateRequiredMaxLen(loginId, MAX_LEN_LOGIN_ID, FIELD_LOGIN_ID);

        if (!LOGIN_ID_PATTERN.matcher(loginId).matches())
            throw new AppException(MessageConstants.ER019);

        if (!isUpdate && employeeRepository.existsByEmployeeLoginId(loginId))
            throw new AppException(MessageConstants.ER003, FIELD_LOGIN_ID);
    }

    /**
     * Validate password: bắt buộc nhập + độ dài [MIN, MAX] + chỉ ký tự half-size.
     *
     * @param pwd Mật khẩu plain-text từ request
     */
    private void validatePassword(String pwd) {
        if (isEmpty(pwd))
            throw new AppException(MessageConstants.ER001, FIELD_PASSWORD);
        if (pwd.length() < MIN_LEN_PASSWORD || pwd.length() > MAX_LEN_PASSWORD) {
            throw new AppException(MessageConstants.ER007, FIELD_PASSWORD,
                    String.valueOf(MIN_LEN_PASSWORD), String.valueOf(MAX_LEN_PASSWORD));
        }
        if (!HALF_SIZE_PATTERN.matcher(pwd).matches()) {
            throw new AppException(MessageConstants.ER008, FIELD_PASSWORD);
        }
    }

    /**
     * Validate department_id: bắt buộc chọn (>0) + tồn tại trong DB.
     *
     * @param departmentId ID phòng ban từ request
     */
    private void validateDepartment(Long departmentId) {
        if (departmentId == null || departmentId <= 0) {
            throw new AppException(MessageConstants.ER002, FIELD_DEPARTMENT);
        }
        if (!departmentRepository.existsById(departmentId)) {
            throw new AppException(MessageConstants.ER004, FIELD_DEPARTMENT);
        }
    }

    /**
     * Validate name_kana: bắt buộc + maxlength + chỉ chứa ký tự katakana.
     *
     * @param kana Tên katakana từ request
     */
    private void validateNameKana(String kana) {
        validateRequiredMaxLen(kana, MAX_LEN_NAME, FIELD_NAME_KANA);

        if (!KATAKANA_PATTERN.matcher(kana).matches()) {
            throw new AppException(MessageConstants.ER009, FIELD_NAME_KANA);
        }
    }

    /**
     * Validate birth_date: bắt buộc nhập + đúng định dạng yyyy/MM/dd.
     *
     * @param birth Ngày sinh dạng String từ request
     */
    private void validateBirthDate(String birth) {
        if (isEmpty(birth))
            throw new AppException(MessageConstants.ER001, FIELD_BIRTH_DATE);
        if (parseDate(birth) == null)
            throw new AppException(MessageConstants.ER011, FIELD_BIRTH_DATE);
    }

    /**
     * Validate email: bắt buộc + maxlength + đúng pattern email.
     *
     * @param email Email từ request
     */
    private void validateEmail(String email) {
        validateRequiredMaxLen(email, MAX_LEN_EMAIL, FIELD_EMAIL);
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new AppException(MessageConstants.ER005, FIELD_EMAIL);
        }
    }

    /**
     * Validate telephone: bắt buộc + maxlength + chỉ digit và dấu '-'.
     *
     * @param phone Số điện thoại từ request
     */
    private void validatePhone(String phone) {
        validateRequiredMaxLen(phone, MAX_LEN_PHONE, FIELD_PHONE);
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new AppException(MessageConstants.ER008, FIELD_PHONE);
        }
    }

    /**
     * Validate 1 chứng chỉ: certificationId tồn tại + startDate/endDate đúng định dạng
     * + endDate sau startDate + score đúng định dạng.
     *
     * @param cert Dữ liệu 1 chứng chỉ từ request (null -> bỏ qua)
     */
    private void validateCertification(EmployeeCertificationRequest cert) {
        if (cert == null) return;

        // certification_id: bắt buộc chọn + tồn tại trong DB
        if (cert.getCertificationId() == null || cert.getCertificationId() <= 0) {
            throw new AppException(MessageConstants.ER002, FIELD_CERTIFICATION);
        }
        if (!certificationRepository.existsById(cert.getCertificationId())) {
            throw new AppException(MessageConstants.ER004, FIELD_CERTIFICATION);
        }

        // start_date: bắt buộc nhập + đúng định dạng (check empty trước khi parse)
        if (isEmpty(cert.getStartDate())) {
            throw new AppException(MessageConstants.ER001, FIELD_START_DATE);
        }
        LocalDate start = parseDate(cert.getStartDate());
        if (start == null) {
            throw new AppException(MessageConstants.ER011, FIELD_START_DATE);
        }

        // end_date: bắt buộc nhập + đúng định dạng
        if (isEmpty(cert.getEndDate())) {
            throw new AppException(MessageConstants.ER001, FIELD_END_DATE);
        }
        LocalDate end = parseDate(cert.getEndDate());
        if (end == null) {
            throw new AppException(MessageConstants.ER011, FIELD_END_DATE);
        }

        // end_date phải sau start_date (ER012)
        if (!end.isAfter(start)) {
            throw new AppException(MessageConstants.ER012);
        }

        // score: bắt buộc nhập + maxlength + đúng định dạng số
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
     * @param departmentId    ID phòng ban (null -> bỏ qua)
     * @param certificationId ID chứng chỉ (null -> bỏ qua)
     */
    public void checkDepartmentAndCertificationExist(Long departmentId, Long certificationId) {
        if (departmentId != null && !departmentRepository.existsById(departmentId)) {
            throw new AppException(MessageConstants.ER004, FIELD_DEPARTMENT);
        }
        if (certificationId != null && !certificationRepository.existsById(certificationId)) {
            throw new AppException(MessageConstants.ER004, FIELD_CERTIFICATION);
        }
    }

    /**
     * Parse date yyyy/MM/dd theo STRICT resolver
     * (reject ngày không hợp lệ như 2023/02/30, 2023/13/01,...).
     *
     * @param value Chuỗi ngày cần parse
     * @return LocalDate nếu hợp lệ, null nếu không
     */
    public LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, DATE_FORMAT);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Tiện ích kiểm tra String null hoặc rỗng.
     *
     * @param s Chuỗi cần kiểm tra
     * @return true nếu null hoặc empty
     */
    private boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
