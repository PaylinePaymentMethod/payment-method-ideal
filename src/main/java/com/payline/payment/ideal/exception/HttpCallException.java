package com.payline.payment.ideal.exception;

import com.payline.pmapi.bean.common.FailureCause;

public class HttpCallException extends PluginException {

    public HttpCallException(String message) {
        super(message, FailureCause.COMMUNICATION_ERROR);
    }

    public HttpCallException(String message, Exception cause) {
        super(message, FailureCause.COMMUNICATION_ERROR, cause);
    }

}
