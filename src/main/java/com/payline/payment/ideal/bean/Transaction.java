package com.payline.payment.ideal.bean;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.payline.payment.ideal.utils.constant.IdealConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Transaction {
    @JacksonXmlProperty(localName = "transactionCreateDateTimestamp",  namespace = IdealConstant.IDEAL_NAMESPACE)
    private String transactionCreateDateTimestamp;

    @JacksonXmlProperty(localName = "transactionID",  namespace = IdealConstant.IDEAL_NAMESPACE)
    private String transactionId;
    @JacksonXmlProperty(localName = "purchaseID",  namespace = IdealConstant.IDEAL_NAMESPACE)
    private String purchaseId;
    @JacksonXmlProperty(localName = "amount",  namespace = IdealConstant.IDEAL_NAMESPACE)
    private String amount;
    @JacksonXmlProperty(localName = "currency",  namespace = IdealConstant.IDEAL_NAMESPACE)
    private String currency;
    @JacksonXmlProperty(localName = "expirationPeriod",  namespace = IdealConstant.IDEAL_NAMESPACE)
    private String expirationPeriod;
    @JacksonXmlProperty(localName = "language",  namespace = IdealConstant.IDEAL_NAMESPACE)
    private String language;
    @JacksonXmlProperty(localName = "description",  namespace = IdealConstant.IDEAL_NAMESPACE)
    private String description;
    @JacksonXmlProperty(localName = "entranceCode",  namespace = IdealConstant.IDEAL_NAMESPACE)
    private String entranceCode;

    @JacksonXmlProperty(localName = "status",  namespace = IdealConstant.IDEAL_NAMESPACE)
    private Status status;

    @JacksonXmlProperty(localName = "statusDateTimestamp",  namespace = IdealConstant.IDEAL_NAMESPACE)
    private String statusDateTimestamp;

    @JacksonXmlProperty(localName = "consumerName",  namespace = IdealConstant.IDEAL_NAMESPACE)
    private String consumerName;

    @JacksonXmlProperty(localName = "consumerIBAN",  namespace = IdealConstant.IDEAL_NAMESPACE)
    private String consumerIBAN;

    @JacksonXmlProperty(localName = "consumerBIC",  namespace = IdealConstant.IDEAL_NAMESPACE)
    private String consumerBIC;

    /**
     * All possible transaction status
     */
    public enum Status {
        OPEN("Open"),
        SUCCESS("Success"),
        FAILURE("Failure"),
        CANCELLED("Cancelled"),
        EXPIRED("Expired");

        private final String statusCode;

        Status(String statusCode) {
            this.statusCode = statusCode;
        }

        @JsonValue
        public String getStatusCode() {
            return this.statusCode;
        }
    }
}
