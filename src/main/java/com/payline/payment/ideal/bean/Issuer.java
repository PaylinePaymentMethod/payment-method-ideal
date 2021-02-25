package com.payline.payment.ideal.bean;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Issuer {

    @JacksonXmlProperty(localName = "issuerID", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
    private String issuerId;

    @JacksonXmlProperty(localName = "issuerName", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
    private String issuerName;

    @JacksonXmlProperty(localName = "issuerAuthenticationURL", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
    private String issuerAuthenticationURL;
}
