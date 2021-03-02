package com.payline.payment.ideal.utils.constant;

public class ContractConfigurationKeys {
    public static final String MERCHANT_ID_KEY = "merchantId";
    public static final String MERCHANT_ID_LABEL = "merchantId.label";
    public static final String MERCHANT_ID_DESCRIPTION = "merchantId.description";

    public static final String MERCHANT_SUBID_KEY = "merchantSubId";
    public static final String MERCHANT_SUBID_LABEL = "merchantSubId.label";
    public static final String MERCHANT_SUBID_DESCRIPTION = "merchantSubId.description";

    public static final String ACQUIRER_ID = "acquirerId";
    public static final String ACQUIRER_ID_LABEL = "acquirer.label";
    public static final String ACQUIRER_ID_DESCRIPTION = "aquirer.description";




    /* Static utility class : no need to instantiate it (Sonar bug fix) */
    private ContractConfigurationKeys() {
    }
}
