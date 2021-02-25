package com.payline.payment.ideal.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.payline.payment.ideal.utils.PluginUtils;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class IdealBean {
    private static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    @JacksonXmlProperty(isAttribute = true)
    private String version = "3.3.1";


    @JacksonXmlProperty(localName = "createDateTimestamp", namespace ="http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1")
    private String createDateTimeStamp;

    public IdealBean() {
        this.createDateTimeStamp = PluginUtils.dateToString(new Date(), FORMAT);
    }

}
