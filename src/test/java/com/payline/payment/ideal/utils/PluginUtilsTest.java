package com.payline.payment.ideal.utils;

import com.payline.pmapi.bean.common.FailureCause;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.Currency;
import java.util.stream.Stream;

class PluginUtilsTest {


    private static Stream<Arguments> isEmptyExpectation() {
        return Stream.of(
                Arguments.of(null, true),
                Arguments.of("", true),
                Arguments.of("foo", false)
        );
    }

    @ParameterizedTest
    @MethodSource("isEmptyExpectation")
    void isEmpty(String s, boolean b) {
        Assertions.assertEquals(b, PluginUtils.isEmpty(s));
    }

    private static Stream<Arguments> createStingAmountData() {
        return Stream.of(
                Arguments.of(BigInteger.valueOf(0), "0.00"),
                Arguments.of(BigInteger.valueOf(1), "0.01"),
                Arguments.of(BigInteger.valueOf(10), "0.10"),
                Arguments.of(BigInteger.valueOf(100), "1.00")
        );
    }

    @ParameterizedTest
    @MethodSource("createStingAmountData")
    void createStringAmount(BigInteger amount, String expected) {
        Assertions.assertEquals(expected,PluginUtils.createStringAmount(amount, Currency.getInstance("EUR")) );
    }

    private static Stream<Arguments> truncateData() {
        return Stream.of(
                Arguments.of(null, null, 10),
                Arguments.of("", "", 10),
                Arguments.of("a very loooong string in need to be trucated", "a very loo", 10),
                Arguments.of("a string", "a string", 10),
                Arguments.of("a string", "a", 1),
                Arguments.of("a string", "", 0)
        );
    }

    @ParameterizedTest
    @MethodSource("truncateData")
    void truncate(String s, String expected, int length) {
        Assertions.assertEquals(expected, PluginUtils.truncate(s,length));
    }

    private static Stream<Arguments> failureExpectation() {
        return Stream.of(
                Arguments.of("IX1100", FailureCause.INVALID_DATA),
                Arguments.of("IX1200", FailureCause.INVALID_DATA),
                Arguments.of("IX1300", FailureCause.INVALID_DATA),
                Arguments.of("IX1600", FailureCause.INVALID_DATA),
                Arguments.of("IX????", FailureCause.INVALID_DATA),

                Arguments.of("SO1000", FailureCause.PAYMENT_PARTNER_ERROR),
                Arguments.of("SO1100", FailureCause.COMMUNICATION_ERROR),
                Arguments.of("SO1200", FailureCause.COMMUNICATION_ERROR),
                Arguments.of("SO1400", FailureCause.COMMUNICATION_ERROR),
                Arguments.of("SO????", FailureCause.COMMUNICATION_ERROR),

                Arguments.of("SE2000", FailureCause.REFUSED),
                Arguments.of("SE2100", FailureCause.REFUSED),
                Arguments.of("SE????", FailureCause.REFUSED),

                Arguments.of("BR1200", FailureCause.INVALID_DATA),
                Arguments.of("BR1210", FailureCause.INVALID_FIELD_FORMAT),
                Arguments.of("BR1220", FailureCause.INVALID_DATA),
                Arguments.of("BR1230", FailureCause.INVALID_DATA),
                Arguments.of("BR1270", FailureCause.INVALID_DATA),
                Arguments.of("BR1280", FailureCause.INVALID_DATA),
                Arguments.of("BR????", FailureCause.INVALID_DATA),

                Arguments.of("AP1100", FailureCause.INVALID_DATA),
                Arguments.of("AP1200", FailureCause.INVALID_DATA),
                Arguments.of("AP1300", FailureCause.INVALID_DATA),
                Arguments.of("AP1500", FailureCause.INVALID_DATA),
                Arguments.of("AP2600", FailureCause.INVALID_DATA),
                Arguments.of("AP2900", FailureCause.INVALID_DATA),
                Arguments.of("AP2910", FailureCause.INVALID_DATA),
                Arguments.of("AP2915", FailureCause.INVALID_DATA),
                Arguments.of("AP2920", FailureCause.INVALID_DATA),
                Arguments.of("AP????", FailureCause.INVALID_DATA),

                Arguments.of("", FailureCause.PARTNER_UNKNOWN_ERROR),
                Arguments.of("??????", FailureCause.PARTNER_UNKNOWN_ERROR),
                Arguments.of("?????????", FailureCause.PARTNER_UNKNOWN_ERROR),
                Arguments.of(null, FailureCause.PARTNER_UNKNOWN_ERROR)
        );
    }

    @ParameterizedTest
    @MethodSource("failureExpectation")
    void getFailureCauseFromIdealErrorCode(String errorCode, FailureCause cause) {
        Assertions.assertEquals(cause, PluginUtils.getFailureCauseFromIdealErrorCode(errorCode));
    }
}