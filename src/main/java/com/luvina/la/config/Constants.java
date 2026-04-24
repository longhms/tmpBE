package com.luvina.la.config;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

public class Constants {

    private Constants() {
    }

    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";
    public static final boolean IS_CROSS_ALLOW = true;

    public static final String JWT_SECRET = "Luvina-Academe";
    public static final long JWT_EXPIRATION = 160 * 60 * 60; // 7 day

    public static final String OFFSET = "オフセット";
    public static final String LIMIT = "リミット";

    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_LIMIT = 5;

    // ── Các giới hạn độ dài ──
    public static final int MAX_LEN_LOGIN_ID = 50;
    public static final int MAX_LEN_NAME = 125;
    public static final int MAX_LEN_EMAIL = 125;
    public static final int MAX_LEN_PHONE = 50;
    public static final int MIN_LEN_PASSWORD = 8;
    public static final int MAX_LEN_PASSWORD = 50;
    public static final int MAX_LEN_SCORE = 3;

    // ── Các tên field tiếng Nhật (dùng đổ vào {0} trong message) ──
    public static final String FIELD_LOGIN_ID = "アカウント名";
    public static final String FIELD_PASSWORD = "パスワード";
    public static final String FIELD_DEPARTMENT = "グループ";
    public static final String FIELD_NAME = "氏名";
    public static final String FIELD_NAME_KANA = "カタカナ氏名";
    public static final String FIELD_BIRTH_DATE = "生年月日";
    public static final String FIELD_EMAIL = "メールアドレス";
    public static final String FIELD_PHONE = "電話番号";
    public static final String FIELD_CERTIFICATION = "資格";
    public static final String FIELD_START_DATE = "資格交付日";
    public static final String FIELD_END_DATE = "失効日";
    public static final String FIELD_SCORE = "点数";

    // ── Các pattern regex ──
    /** login_id: bắt đầu = chữ/underscore, các ký tự còn lại = [a-zA-Z0-9_] */
    public static final Pattern LOGIN_ID_PATTERN = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");
    /** half-size trong ASCII (loại khoảng trắng) */
    public static final Pattern HALF_SIZE_PATTERN = Pattern.compile("^[\\x21-\\x7E]+$");
    /** katakana (kèm dấu ー và khoảng trắng fullwidth) */
    public static final Pattern KATAKANA_PATTERN = Pattern.compile("^[\\u30A0-\\u30FF\\uFF65-\\uFF9F\\s]+$");
    /** chỉ digit + optional dấu chấm */
    public static final Pattern SCORE_PATTERN = Pattern.compile("^\\d+(\\.\\d+)?$");
    /** pattern email */
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    /** phone: chỉ digit và dấu gạch ngang */
    public static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9\\-]+$");
    /** date yyyy/MM/dd (validate lexical), ngữ nghĩa validate qua DateTimeFormatter STRICT */
    public static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("uuuu/MM/dd").withResolverStyle(ResolverStyle.STRICT);

    // config endpoints public
    public static final String[] ENDPOINTS_PUBLIC = new String[] {
            "/",
            "/login/**",
            "/error/**"
    };

    // config endpoints for USER role
    public static final String[] ENDPOINTS_WITH_ROLE = new String[] {
            "/user/**"
    };

    // user attributies put to token
    public static final String[] ATTRIBUTIES_TO_TOKEN = new String[] {
            "employeeId",
            "departmentId",
            "employeeName",
            "employeeNameKana",
            "employeeBirthDate",
            "employeeEmail",
            "employeeTelephone",
            "employeeLoginId",
            "employeeEmail"
    };
}
