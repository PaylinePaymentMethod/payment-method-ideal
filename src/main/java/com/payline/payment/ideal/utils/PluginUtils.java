package com.payline.payment.ideal.utils;


import com.payline.pmapi.bean.common.FailureCause;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;


public class PluginUtils {

    private PluginUtils() {
        // ras.
    }

    public static boolean isEmpty(String s) {

        return s == null || s.isEmpty();
    }


    /**
     * Return a string which was converted from cents to euro
     *
     * @param amount
     * @return Amount as String
     */
    public static String createStringAmount(BigInteger amount, Currency currency) {
        // get currency digit numbers
        int nbDigits = currency.getDefaultFractionDigits();

        StringBuilder sb = new StringBuilder();
        sb.append(amount);

        for (int i = sb.length(); i < 3; i++) {
            sb.insert(0, "0");
        }

        sb.insert(sb.length() - nbDigits, ".");
        return sb.toString();
    }

    public static String truncate(String value, int length) {
        if (value != null && value.length() > length) {
            value = value.substring(0, length);
        }
        return value;
    }

    public static String dateToString(Date date, String format) {
        DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }


    // Ideal Specific methods
    public static FailureCause getFailureCauseFromIdealErrorCode(String error) {
        if (error == null || error.length() < 6) {
            return FailureCause.PARTNER_UNKNOWN_ERROR;
        }

        String errorCat = error.substring(0, 2);
        String errorCode = error.substring(2, 6);

        switch (errorCat.toUpperCase()) {
            case "IX":
            case "AP":
                return FailureCause.INVALID_DATA;
            case "SO":
                if ("1000".equals(errorCode)) {
                    return FailureCause.PAYMENT_PARTNER_ERROR;
                } else {
                    return FailureCause.COMMUNICATION_ERROR;
                }

            case "SE":
                return FailureCause.REFUSED;

            case "BR":
                if ("1210".equals(errorCode)) {
                    return FailureCause.INVALID_FIELD_FORMAT;
                } else {
                    return FailureCause.INVALID_DATA;
                }

            default:
                return FailureCause.PARTNER_UNKNOWN_ERROR;

        }
    }

}