package com.payline.payment.ideal.utils.constant;

public class RequestContextKeys {
    public static final String CHECKOUT_SESSION_ID = "checkoutSessionId";
    public static final String STEP = "step";
    public static final String EMAIL = "email";

    public static final String STEP_COMPLETE = "stepComplete";

    /* Static utility class : no need to instantiate it (Sonar bug fix) */
    private RequestContextKeys() {
    }
}
