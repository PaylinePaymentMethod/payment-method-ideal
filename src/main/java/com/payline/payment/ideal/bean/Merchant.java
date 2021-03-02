package com.payline.payment.ideal.bean;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.payline.payment.ideal.utils.constant.IdealConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor
@JacksonXmlRootElement(localName = "Merchant",  namespace = IdealConstant.IDEAL_NAMESPACE)
public class Merchant {

    @JacksonXmlProperty(localName = "merchantID",  namespace = IdealConstant.IDEAL_NAMESPACE)
    @NonNull
    private String merchantId;

    @JacksonXmlProperty(localName = "subID",  namespace = IdealConstant.IDEAL_NAMESPACE)
    @NonNull
    private String subId;

    @JacksonXmlProperty(localName = "merchantReturnURL",  namespace = IdealConstant.IDEAL_NAMESPACE)
    private String merchantReturnURL;


    public Merchant(String merchantId, String subId) {
        this.merchantId = merchantId;
        this.subId = subId;
    }

    public Merchant(String merchantId, String subId, String redirectionURL) {
        this.merchantId = merchantId;
        this.subId = subId;
        this.merchantReturnURL = redirectionURL;
    }
}
