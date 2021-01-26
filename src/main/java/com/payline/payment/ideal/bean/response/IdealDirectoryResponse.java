package com.payline.payment.ideal.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.payline.payment.ideal.bean.Acquirer;
import com.payline.payment.ideal.bean.Directory;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JacksonXmlRootElement(localName = "DirectoryRes", namespace = "http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
public class IdealDirectoryResponse extends IdealResponse {

    @JacksonXmlProperty(localName = "Acquirer")
    private Acquirer acquirer;

    @JacksonXmlProperty(localName = "Directory")
    private Directory directory;
}
