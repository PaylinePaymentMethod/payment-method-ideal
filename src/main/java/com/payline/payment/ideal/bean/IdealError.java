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
public class IdealError {

    @JacksonXmlProperty(localName = "errorCode")
    private String errorCode;

    @JacksonXmlProperty(localName = "errorMessage")
    private String errorMessage;

    @JacksonXmlProperty(localName = "errorDetail")
    private String errorDetail;

    @JacksonXmlProperty(localName = "suggestedAction")
    private String suggestedAction;

    @JacksonXmlProperty(localName = "consumerMessage")
    private String consumerMessage;
}