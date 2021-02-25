package com.payline.payment.ideal.bean.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.payline.payment.ideal.bean.IdealBean;
import com.payline.payment.ideal.bean.IdealError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class IdealResponse extends IdealBean {
    @JacksonXmlProperty(localName = "Error")
    private IdealError error;
}
