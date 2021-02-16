package com.payline.payment.ideal.bean.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.payline.payment.ideal.bean.IdealBean;
import com.payline.payment.ideal.bean.Merchant;
import com.payline.payment.ideal.bean.Transaction;

@JacksonXmlRootElement(localName = "AcquirerStatusReq", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
public class IdealStatusRequest extends IdealBean {

    @JacksonXmlProperty(localName = "Merchant", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
    private Merchant merchant;

    @JacksonXmlProperty(localName = "Transaction", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
    private Transaction transaction;

    public IdealStatusRequest(Merchant merchant,Transaction transaction) {
        this.merchant = merchant;
        this.transaction = transaction;
    }
}
