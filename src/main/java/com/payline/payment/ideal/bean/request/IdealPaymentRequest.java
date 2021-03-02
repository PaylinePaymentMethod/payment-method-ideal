package com.payline.payment.ideal.bean.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.payline.payment.ideal.bean.IdealBean;
import com.payline.payment.ideal.bean.Issuer;
import com.payline.payment.ideal.bean.Merchant;
import com.payline.payment.ideal.bean.Transaction;
import com.payline.payment.ideal.utils.constant.IdealConstant;

@JacksonXmlRootElement(localName = "AcquirerTrxReq", namespace = IdealConstant.IDEAL_NAMESPACE)
public class IdealPaymentRequest extends IdealBean {
    @JacksonXmlProperty(localName = "Issuer", namespace = IdealConstant.IDEAL_NAMESPACE)
    private Issuer issuer;

    @JacksonXmlProperty(localName = "Merchant", namespace = IdealConstant.IDEAL_NAMESPACE)
    private Merchant merchant;

    @JacksonXmlProperty(localName = "Transaction", namespace = IdealConstant.IDEAL_NAMESPACE)
    private Transaction transaction;

    public IdealPaymentRequest(Issuer issuer, Merchant merchant, Transaction transaction) {
        this.issuer = issuer;
        this.merchant = merchant;
        this.transaction = transaction;
    }
}
