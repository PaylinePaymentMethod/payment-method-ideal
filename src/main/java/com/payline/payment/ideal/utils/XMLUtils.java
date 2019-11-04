package com.payline.payment.ideal.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.payline.payment.ideal.exception.PluginException;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.logger.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


public class XMLUtils {
    private static final Logger LOGGER = LogManager.getLogger(XMLUtils.class);

    private XmlMapper mapper;

    private XMLUtils() {
        mapper = new XmlMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    /**
     * Singleton Holder
     */
    private static class SingletonHolder {
        private static final XMLUtils INSTANCE = new XMLUtils();
    }

    /**
     * @return the singleton instance
     */
    public static XMLUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }


    public String toXml(Object bean) {
        try {
            return mapper.writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            LOGGER.error("Unable to write XML", e);
            throw new PluginException(e.getMessage(), FailureCause.INVALID_DATA);
        }
    }

    public <T> T fromXML(String xml, Class<T> clazz) {
        try {
            return mapper.readValue(xml, clazz);
        } catch (IOException e) {
            LOGGER.error("Unable to read XML", e);
            throw new PluginException(e.getMessage(), FailureCause.INVALID_DATA);
        }

    }
}
