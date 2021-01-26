package com.payline.payment.ideal.bean;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * All possible transaction status
 */
public enum Categories {
    INVALID_XML("IX"),
    SYSTEM_MAINTENANCE("SO"),
    SECURITY("SE"),
    FIELD_ERRORS("BR"),
    APPLICATION_ERRORS("AP"),
    UNKNOWN_ERROR("UE");

    private final String errorCode;

    Categories(String errorCode) {
        this.errorCode = errorCode;
    }

    @JsonValue
    public String getErrorCode() {
        return this.errorCode;
    }
}
