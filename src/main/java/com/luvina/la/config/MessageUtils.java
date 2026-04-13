package com.luvina.la.config;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [MessageUtils.java], [Apr ,2026] [ntlong]
 */

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Utility đọc message từ messages.properties.
 * author: [ntlong]
 */
@Component
@RequiredArgsConstructor
public class MessageUtils {

    private final MessageSource messageSource;

    /**
     * Lấy message theo code và params.
     *
     * @param code   mã message (vd: "ER001", "field.sort")
     * @param params danh sách tham số thay thế vào {0}, {1}...
     * @return nội dung message, trả về code nếu không tìm thấy
     */
    public String getMessage(String code, Object... params) {
        try {
            return messageSource.getMessage(code, params, Locale.JAPAN);
        } catch (Exception e) {
            return code;
        }
    }
}
