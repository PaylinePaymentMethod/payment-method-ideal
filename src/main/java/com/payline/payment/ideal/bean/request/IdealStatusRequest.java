package com.payline.payment.ideal.bean.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.payline.payment.ideal.bean.IdealBean;
import com.payline.payment.ideal.bean.Merchant;
import com.payline.payment.ideal.bean.Transaction;
import com.payline.payment.ideal.utils.constant.IdealConstant;

@JacksonXmlRootElement(localName = "AcquirerStatusReq", namespace = IdealConstant.IDEAL_NAMESPACE)
public class IdealStatusRequest extends IdealBean {

    @JacksonXmlProperty(localName = "Merchant", namespace = IdealConstant.IDEAL_NAMESPACE)
    private Merchant merchant;

    @JacksonXmlProperty(localName = "Transaction", namespace = IdealConstant.IDEAL_NAMESPACE)
    private Transaction transaction;

    public IdealStatusRequest(Merchant merchant,Transaction transaction) {
        this.merchant = merchant;
        this.transaction = transaction;
    }
}
