package com.payline.payment.ideal.exception;

import com.payline.pmapi.bean.common.FailureCause;

public class CryptoException extends PluginException {

    public CryptoException(String message) {
        super(message, FailureCause.INTERNAL_ERROR);
    }

    public CryptoException(String message, Exception cause) {
        super(message, FailureCause.INTERNAL_ERROR, cause);
    }

}
