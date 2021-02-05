package com.payline.payment.ideal.bean;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor
@JacksonXmlRootElement(localName = "Merchant", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
public class Merchant {

    @JacksonXmlProperty(localName = "merchantID", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
    @NonNull
    private String merchantId;

    @JacksonXmlProperty(localName = "subID", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
    @NonNull
    private String subId;

    @JacksonXmlProperty(localName = "merchantReturnURL", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
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
