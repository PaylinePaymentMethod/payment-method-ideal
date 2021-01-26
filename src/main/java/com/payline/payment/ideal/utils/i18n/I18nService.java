package com.payline.payment.ideal.utils.i18n;

import lombok.extern.log4j.Log4j2;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Log4j2
public class I18nService {
    private static final String I18N_SERVICE_DEFAULT_LOCALE = "en";
    private static final String RESOURCE_BUNDLE_BASE_NAME = "messages";

    /**
     * Private constructor
     */
    private I18nService() {
        Locale.setDefault(new Locale(I18N_SERVICE_DEFAULT_LOCALE));
    }

    /**
     * Holder
     */
    private static class SingletonHolder {
        /**
         * Unique instance, not preinitializes
         */
        private static final I18nService instance = new I18nService();
    }

    /**
     * Unique access point for the singleton instance
     */
    public static I18nService getInstance() {
        return SingletonHolder.instance;
    }

    public String getMessage(final String key, final Locale locale) {
        ResourceBundle messages = ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME, locale);
        try {
            return messages.getString(key);
        } catch (MissingResourceException e) {
            log.error("Trying to get a message with a key that does not exist: {} (language:  {})", key, locale.getLanguage());
            return "???" + locale + "." + key + "???";
        }
    }

    // If ever needed, implement getMessage( String, Locale, String... ) to insert values into the translation messages
}
