package com.payline.payment.ideal.service;

import com.payline.payment.ideal.bean.PartnerAcquirer;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;

import java.util.Map;

public interface PartnerConfigurationService {

    /**
     * Retrieve acquirerList from partnerConfiguration.
     * @param partnerConfiguration
     * @return
     */
    Map<String, PartnerAcquirer> fetchAcquirerList(PartnerConfiguration partnerConfiguration);
}
