package com.luvina.la.exception;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright(C) [2026] [Luvina Software Company]
 * <p>
 * [.java], [Apr ,2026] [ntlong]
 */
@Getter
public class BusinessException extends RuntimeException {
    private final String code;
    private final List<String> params;

    public BusinessException(String code, String... params) {
        super(code);
        this.code = code;
        this.params = Arrays.asList(params);
    }
    public BusinessException(String code, List<String> params) {
      super(code);
      this.code = code;
      this.params = params;
    }
}
