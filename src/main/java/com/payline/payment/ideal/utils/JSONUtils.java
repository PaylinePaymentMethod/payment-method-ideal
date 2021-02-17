package com.payline.payment.ideal.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payline.payment.ideal.exception.PluginException;

import java.util.Map;

public class JSONUtils {

    private ObjectMapper objectMapper;

    private JSONUtils() {
        objectMapper = new ObjectMapper();
    }

    /**
     * Singleton Holder
     */
    private static class SingletonHolder {
        private static final JSONUtils INSTANCE = new JSONUtils();
    }

    /**
     * @return the singleton instance
     */
    public static JSONUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public String toJson(final Map<String, String> elements) {
        try {
            return objectMapper.writeValueAsString(elements);
        } catch (JsonProcessingException e) {
            throw new PluginException("Impossible de convertir en json", e);
        }
    }

    public Map<String, String> fromJSON(final String json) {
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            throw new PluginException("Impossible de lire le json", e);
        }
    }


}
