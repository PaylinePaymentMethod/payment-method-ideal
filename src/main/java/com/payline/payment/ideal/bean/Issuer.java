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

    @JacksonXmlProperty(localName = "issuerID")
    private String issuerId;

    @JacksonXmlProperty(localName = "issuerName")
    private String issuerName;

    @JacksonXmlProperty(localName = "issuerAuthenticationURL")
    private String issuerAuthenticationURL;
}
