package com.luvina.la.config;

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
