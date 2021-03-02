package com.payline.payment.ideal.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payline.payment.ideal.bean.PartnerAcquirer;
import com.payline.payment.ideal.exception.PluginException;
import com.payline.payment.ideal.utils.constant.PartnerConfigurationKeys;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartnerConfigurationService {

    // --- Singleton Holder pattern + initialization BEGIN
    private PartnerConfigurationService() {

    }

    private static class Holder {
        private static final PartnerConfigurationService INSTANCE = new PartnerConfigurationService();
    }

    public static PartnerConfigurationService getInstance() {
        return PartnerConfigurationService.Holder.INSTANCE;
    }
    // --- Singleton Holder pattern + initialization END


    public Map<String, PartnerAcquirer> fetchAcquirerList(final PartnerConfiguration partnerConfiguration) {
        final ObjectMapper mapper = new ObjectMapper();
        final String acquirers = partnerConfiguration.getProperty(PartnerConfigurationKeys.ACQUIRERS);
        final String acquirersSensitive = partnerConfiguration.getProperty(PartnerConfigurationKeys.ACQUIRERS_SENSITIVE_DATA);

        try {
            final List<PartnerAcquirer> acquirerList = mapper.readValue(acquirers, new TypeReference<List<PartnerAcquirer>>(){});
            final Map<String, PartnerAcquirer> acquirerMap = new HashMap<>();
            acquirerList.forEach(e -> acquirerMap.put(e.getName(), e));
            final List<PartnerAcquirer> acquirerSensitiveList = mapper.readValue(acquirersSensitive, new TypeReference<List<PartnerAcquirer>>(){});
            acquirerSensitiveList.forEach(e -> {
                if (acquirerMap.get(e.getName()) != null) {
                    acquirerMap.get(e.getName()).setPrivateKey(e.getPrivateKey());
                }
            });
            return acquirerMap;
        } catch (final JsonProcessingException e) {
            throw new PluginException("Parametrage invalide pour acquirers", e);
        }
    }

    public PartnerAcquirer getPartnerAcquirer(final PartnerConfiguration partnerConfiguration, final String name) {
        final Map<String, PartnerAcquirer> acquirerMap = fetchAcquirerList(partnerConfiguration);
        return acquirerMap.get(name);
    }
}
