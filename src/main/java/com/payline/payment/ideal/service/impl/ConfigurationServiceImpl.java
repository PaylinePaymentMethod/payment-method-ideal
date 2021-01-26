package com.payline.payment.ideal.service.impl;

import com.payline.payment.ideal.bean.response.IdealDirectoryResponse;
import com.payline.payment.ideal.utils.XMLUtils;
import com.payline.payment.ideal.utils.constant.ContractConfigurationKeys;
import com.payline.payment.ideal.utils.http.IdealHttpClient;
import com.payline.payment.ideal.utils.i18n.I18nService;
import com.payline.payment.ideal.utils.properties.ReleaseProperties;
import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.InputParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.configuration.request.RetrievePluginConfigurationRequest;
import com.payline.pmapi.service.ConfigurationService;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Log4j2
public class ConfigurationServiceImpl implements ConfigurationService {

    private ReleaseProperties releaseProperties = ReleaseProperties.getInstance();
    private I18nService i18n = I18nService.getInstance();
    private IdealHttpClient client = IdealHttpClient.getInstance();
    private XMLUtils xmlUtils =  XMLUtils.getInstance();

    @Override
    public List<AbstractParameter> getParameters(Locale locale) {
        List<AbstractParameter> parameters = new ArrayList<>();

        final InputParameter merchantId = new InputParameter();
        merchantId.setKey(ContractConfigurationKeys.MERCHANT_ID_KEY);
        merchantId.setLabel(this.i18n.getMessage(ContractConfigurationKeys.MERCHANT_ID_LABEL, locale));
        merchantId.setDescription(this.i18n.getMessage(ContractConfigurationKeys.MERCHANT_ID_DESCRIPTION, locale));
        merchantId.setRequired(true);

        parameters.add(merchantId);

        final InputParameter merchantSubId = new InputParameter();
        merchantSubId.setKey(ContractConfigurationKeys.MERCHANT_SUBID_KEY);
        merchantSubId.setLabel(this.i18n.getMessage(ContractConfigurationKeys.MERCHANT_SUBID_LABEL, locale));
        merchantSubId.setDescription(this.i18n.getMessage(ContractConfigurationKeys.MERCHANT_SUBID_DESCRIPTION, locale));
        merchantSubId.setRequired(false);

        parameters.add(merchantSubId);

        return parameters;
    }

    @Override
    public Map<String, String> check(ContractParametersCheckRequest contractParametersCheckRequest) {
        Map<String, String> errors = new HashMap<>();

        try {

            IdealDirectoryResponse response = client.directoryRequest(contractParametersCheckRequest);
            if (response.getError() != null) {
                errors.put(ContractParametersCheckRequest.GENERIC_ERROR, response.getError().getErrorMessage());
            }
        } catch (RuntimeException e) {
            errors.put(ContractParametersCheckRequest.GENERIC_ERROR, e.getMessage());
        }

        return errors;
    }

    @Override
    public String retrievePluginConfiguration(RetrievePluginConfigurationRequest retrievePluginConfigurationRequest) {
        try {
            IdealDirectoryResponse response = client.directoryRequest(retrievePluginConfigurationRequest);
            if (response.getError() != null) {
                log.error("Could not retrieve plugin configuration due to a partner error: {}", response.getError().getErrorCode());
                return retrievePluginConfigurationRequest.getPluginConfiguration();
            } else {
                return xmlUtils.toXml(response.getDirectory());
            }

        } catch (RuntimeException e) {
            log.error("Could not retrieve plugin configuration due to a plugin error", e);
            return retrievePluginConfigurationRequest.getPluginConfiguration();
        }
    }

    @Override
    public ReleaseInformation getReleaseInformation() {
        return ReleaseInformation.ReleaseBuilder.aRelease()
                .withDate(LocalDate.parse(releaseProperties.get("release.date"), DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .withVersion(releaseProperties.get("release.version"))
                .build();
    }

    @Override
    public String getName(Locale locale) {
        return this.i18n.getMessage("paymentMethod.name", locale);
    }

}