package com.payline.payment.ideal.bean.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.payline.payment.ideal.bean.Acquirer;
import com.payline.payment.ideal.bean.IdealError;
import com.payline.payment.ideal.bean.Issuer;
import com.payline.payment.ideal.bean.Transaction;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JacksonXmlRootElement(localName = "AcquirerTrxRes", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
@NoArgsConstructor
@Getter
public class IdealPaymentResponse extends IdealResponse {

    @JacksonXmlProperty(localName = "Acquirer")
    private Acquirer acquirer;

    @JacksonXmlProperty(localName = "Issuer")
    private Issuer issuer;

    @JacksonXmlProperty(localName = "Transaction")
    private Transaction transaction;

    public IdealPaymentResponse(IdealError error, Acquirer acquirer, Issuer issuer, Transaction transaction) {
        super(error);
        this.acquirer = acquirer;
        this.issuer = issuer;
        this.transaction = transaction;
    }
}
