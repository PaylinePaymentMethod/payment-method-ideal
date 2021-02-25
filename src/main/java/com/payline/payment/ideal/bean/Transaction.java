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
    @JacksonXmlProperty(localName = "transactionCreateDateTimestamp", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
    private String transactionCreateDateTimestamp;

    @JacksonXmlProperty(localName = "transactionID", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
    private String transactionId;
    @JacksonXmlProperty(localName = "purchaseID", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
    private String purchaseId;
    @JacksonXmlProperty(localName = "amount", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
    private String amount;
    @JacksonXmlProperty(localName = "currency", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
    private String currency;
    @JacksonXmlProperty(localName = "expirationPeriod", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
    private String expirationPeriod;
    @JacksonXmlProperty(localName = "language", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
    private String language;
    @JacksonXmlProperty(localName = "description", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
    private String description;
    @JacksonXmlProperty(localName = "entranceCode", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
    private String entranceCode;

    @JacksonXmlProperty(localName = "status", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
    private Status status;

    @JacksonXmlProperty(localName = "statusDateTimestamp", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
    private String statusDateTimestamp;

    @JacksonXmlProperty(localName = "consumerName", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
    private String consumerName;

    @JacksonXmlProperty(localName = "consumerIBAN", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
    private String consumerIBAN;

    @JacksonXmlProperty(localName = "consumerBIC", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
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
