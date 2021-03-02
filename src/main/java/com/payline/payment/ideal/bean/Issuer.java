package com.payline.payment.ideal.bean;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.payline.payment.ideal.utils.constant.IdealConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Issuer {

    @JacksonXmlProperty(localName = "issuerID",  namespace = IdealConstant.IDEAL_NAMESPACE)
    private String issuerId;

    @JacksonXmlProperty(localName = "issuerName",  namespace = IdealConstant.IDEAL_NAMESPACE)
    private String issuerName;

    @JacksonXmlProperty(localName = "issuerAuthenticationURL",  namespace = IdealConstant.IDEAL_NAMESPACE)
    private String issuerAuthenticationURL;
}
