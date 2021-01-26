package com.payline.payment.ideal.bean.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.payline.payment.ideal.bean.IdealBean;
import com.payline.payment.ideal.bean.Issuer;
import com.payline.payment.ideal.bean.Merchant;
import com.payline.payment.ideal.bean.Transaction;

@JacksonXmlRootElement(localName = "AcquirerTrxReq", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
public class IdealPaymentRequest extends IdealBean {
    @JacksonXmlProperty(localName = "Issuer")
    private Issuer issuer;

    @JacksonXmlProperty(localName = "Merchant")
    private Merchant merchant;

    @JacksonXmlProperty(localName = "Transaction")
    private Transaction transaction;

    public IdealPaymentRequest(Issuer issuer, Merchant merchant, Transaction transaction) {
        this.issuer = issuer;
        this.merchant = merchant;
        this.transaction = transaction;
    }
}
