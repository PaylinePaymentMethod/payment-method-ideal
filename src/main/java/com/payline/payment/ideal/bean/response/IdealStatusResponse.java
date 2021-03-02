package com.payline.payment.ideal.bean.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.payline.payment.ideal.bean.Acquirer;
import com.payline.payment.ideal.bean.IdealError;
import com.payline.payment.ideal.bean.Transaction;
import com.payline.payment.ideal.utils.constant.IdealConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JacksonXmlRootElement(localName = "AcquirerStatusRes",  namespace = IdealConstant.IDEAL_NAMESPACE)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class IdealStatusResponse extends IdealResponse {

    @JacksonXmlProperty(localName = "Acquirer")
    private Acquirer acquirer;

    @JacksonXmlProperty(localName = "Transaction")
    private Transaction transaction;

    public IdealStatusResponse(IdealError error, Acquirer acquirer, Transaction transaction) {
        super(error);
        this.acquirer = acquirer;
        this.transaction = transaction;
    }
}
