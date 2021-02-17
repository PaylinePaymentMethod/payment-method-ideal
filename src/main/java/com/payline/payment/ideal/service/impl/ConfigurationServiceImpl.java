package com.payline.payment.ideal.service.impl;

import com.payline.payment.ideal.bean.PartnerAcquirer;
import com.payline.payment.ideal.bean.response.IdealDirectoryResponse;
import com.payline.payment.ideal.exception.PluginException;
import com.payline.payment.ideal.service.PartnerConfigurationService;
import com.payline.payment.ideal.utils.JSONUtils;
import com.payline.payment.ideal.utils.PluginUtils;
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
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.ContractProperty;
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
    private PartnerConfigurationService partnerConfigurationService = new PartnerConfigurationServiceImpl();
    private JSONUtils jsonUtils = JSONUtils.getInstance();
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

            Map <String, String> directoryConfigurationMap = new HashMap<>();
            if (!PluginUtils.isEmpty(retrievePluginConfigurationRequest.getPluginConfiguration())) {
                directoryConfigurationMap = jsonUtils.fromJSON(retrievePluginConfigurationRequest.getPluginConfiguration());
            }

            //On récupère la liste des acquéreurs.
            final Map<String, PartnerAcquirer> partnerAcquirers = partnerConfigurationService.fetchAcquirerList(retrievePluginConfigurationRequest.getPartnerConfiguration());

            //on interroge chaque acquéreur pour récupérer sa configuration
            for (PartnerAcquirer partnerAcquirer : partnerAcquirers.values()) {
                getDirectoryIssuer(directoryConfigurationMap, partnerAcquirer, retrievePluginConfigurationRequest);
            }
            return jsonUtils.toJson(directoryConfigurationMap);

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

    /**
     * Permet d'appeler le partenaire pour récupérer la liste des issuers possibles pour celui-ci.
     * @param directoryConfigurationMap
     *          La Map a mettre à jour si l'appel à réussi.
     * @param partnerAcquirer
     *          La configuration du partenaire qu'il faut appeler.
     * @param request
     *          Les paramètres de la requête faite par le core.
     */
    protected void getDirectoryIssuer(final Map<String, String> directoryConfigurationMap, final PartnerAcquirer partnerAcquirer,
                                      final RetrievePluginConfigurationRequest request) {
        try {
            final ContractConfiguration contractConfiguration = buildContractConfiguration(partnerAcquirer);
            final RetrievePluginConfigurationRequest newRequest = RetrievePluginConfigurationRequest.
                    RetrieveConfigurationRequestBuilder.aRetrieveConfigurationRequest()
                    .withContractConfiguration(contractConfiguration)
                    .withEnvironment(request.getEnvironment())
                    .withPartnerConfiguration(request.getPartnerConfiguration())
                    .withPluginConfiguration(request.getPluginConfiguration()).build();

            final IdealDirectoryResponse response = client.directoryRequest(newRequest, partnerAcquirer);
            //En cas d'erreur sur une interrogation d'un plugin on garde l'ancienne configuration.
            if (response.getError() != null) {
                log.error("Could not retrieve plugin configuration due to a partner error: {}", response.getError().getErrorCode());
            } else {
                directoryConfigurationMap.put(partnerAcquirer.getName(), xmlUtils.toXml(response.getDirectory()));
            }
            //PluginException rassemble les HTTP Communication error
            //on ne change pas les anciennes configurations si elles existent.
        } catch (PluginException e) {
            log.error("Exception au niveau de l'exécution du plugin", e);
        }
    }

    protected ContractConfiguration buildContractConfiguration(final PartnerAcquirer partnerAcquirer) {
        final Map<String, ContractProperty> properties = new HashMap<>();
        properties.put(ContractConfigurationKeys.MERCHANT_ID_KEY, new ContractProperty(partnerAcquirer.getMerchantId()));
        properties.put(ContractConfigurationKeys.MERCHANT_SUBID_KEY, new ContractProperty(partnerAcquirer.getSubMerchantId()));
        return new ContractConfiguration("IDEAL_V2", properties);
    }

}