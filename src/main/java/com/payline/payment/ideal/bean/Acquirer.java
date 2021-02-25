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
public class Acquirer {

    @JacksonXmlProperty(localName = "acquirerID")
    private String acquirerId;

}
