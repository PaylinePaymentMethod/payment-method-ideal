package com.payline.payment.ideal.bean;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Transaction {
    @JacksonXmlProperty(localName = "transactionCreateDateTimestamp")
    private String transactionCreateDateTimestamp;

    @JacksonXmlProperty(localName = "transactionID")
    private String transactionId;
    @JacksonXmlProperty(localName = "purchaseID")
    private String purchaseId;
    @JacksonXmlProperty(localName = "amount")
    private String amount;
    @JacksonXmlProperty(localName = "currency")
    private String currency;
    @JacksonXmlProperty(localName = "expirationPeriod")
    private String expirationPeriod;
    @JacksonXmlProperty(localName = "language")
    private String language;
    @JacksonXmlProperty(localName = "description")
    private String description;
    @JacksonXmlProperty(localName = "entranceCode")
    private String entranceCode;

    @JacksonXmlProperty(localName = "status")
    private Status status;

    @JacksonXmlProperty(localName = "statusDateTimestamp")
    private String statusDateTimestamp;

    @JacksonXmlProperty(localName = "consumerName")
    private String consumerName;

    @JacksonXmlProperty(localName = "consumerIBAN")
    private String consumerIBAN;

    @JacksonXmlProperty(localName = "consumerBIC")
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
