package com.payline.payment.ideal.service.impl;

import com.payline.payment.ideal.Utils;
import com.payline.payment.ideal.bean.Directory;
import com.payline.payment.ideal.bean.PartnerAcquirer;
import com.payline.payment.ideal.bean.response.IdealDirectoryResponse;
import com.payline.payment.ideal.exception.PluginException;
import com.payline.payment.ideal.utils.JSONUtils;
import com.payline.payment.ideal.utils.XMLUtils;
import com.payline.payment.ideal.utils.constant.ContractConfigurationKeys;
import com.payline.payment.ideal.utils.constant.PartnerConfigurationKeys;
import com.payline.payment.ideal.utils.http.IdealHttpClient;
import com.payline.payment.ideal.utils.properties.ReleaseProperties;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.configuration.request.ContractParametersRequest;
import com.payline.pmapi.bean.configuration.request.RetrievePluginConfigurationRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

class ConfigurationServiceImplTest {

    /* I18nService is not mocked here, on purpose, to validate the existence of all
    the messages related to this class, at least in the default locale */
    @Mock
    private IdealHttpClient httpClient;
    @Mock
    private ReleaseProperties releaseProperties;

    @InjectMocks
    private ConfigurationServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void getParameters() {
        final Map<String, String> configurationMap = new HashMap<>();
        configurationMap.put(PartnerConfigurationKeys.ACQUIRERS, "[{\"NAME\":\"BNPP\",\"URL\":\"http://www.urlbnp.com\",\"PUBLIC_KEY\":\"publicKey\",\"IDEAL_PUBLIC\":\"idealPublic\",\"PUBLIC_KEY_ID\":\"publicKeyId\",\"MERCHANT_ID\":\"017001580\",\"SUBMERCHANT_ID\":\"0\"},{\"NAME\":\"ABNAMRO\",\"URL\":\"http://www.abnamro.com\",\"PUBLIC_KEY\":\"publicKey\",\"IDEAL_PUBLIC\":\"abnaMROKey\",\"PUBLIC_KEY_ID\":\"publicKeyId\",\"MERCHANT_ID\":\"01234567\",\"SUBMERCHANT_ID\":\"1\"}]");
        configurationMap.put(PartnerConfigurationKeys.ACQUIRERS_SENSITIVE_DATA, "[{\"NAME\":\"BNPP\",\"PRIVATE_KEY\":\"privateKeyBNPP\"},{\"NAME\":\"ABNAMRO\",\"PRIVATE_KEY\":\"privateKeyAbnamro\"}]");

        final PartnerConfiguration partnerConfiguration = new PartnerConfiguration(configurationMap, new HashMap<>());
        final ContractParametersRequest contractParametersRequest = ContractParametersRequest.builder()
                .locale(Locale.FRANCE)
                .partnerConfiguration(partnerConfiguration).build();
        List<AbstractParameter> parameters = service.getParameters(contractParametersRequest);
        Assertions.assertEquals(3, parameters.size());

        AbstractParameter param1 = parameters.stream()
                .filter(parameter -> "merchantId".equals(parameter.getKey()))
                .findAny()
                .orElse(null);

        AbstractParameter param2 = parameters.stream()
                .filter(parameter -> "merchantSubId".equals(parameter.getKey()))
                .findAny()
                .orElse(null);

            Assertions.assertNotNull(param1);
            Assertions.assertEquals("Id du commerçant", param1.getLabel());
            Assertions.assertNotNull(param2);
            Assertions.assertEquals("Sous id du commerçant", param2.getLabel());
    }

    @Test
    void check() {
        // create mock
        IdealDirectoryResponse directoryResponse = XMLUtils.getInstance().fromXML(Utils.directoryResponseOK, IdealDirectoryResponse.class);
        doReturn(directoryResponse).when(httpClient).directoryRequest(any(ContractParametersCheckRequest.class));

        // call method
        Map<String, String> errors = service.check(Utils.createContractParametersCheckRequest());

        // assertions
        Assertions.assertTrue(errors.isEmpty());
    }

    @Test
    void checkKO() {
        // create mock
        IdealDirectoryResponse directoryResponse = XMLUtils.getInstance().fromXML(Utils.errorResponse, IdealDirectoryResponse.class);
        doReturn(directoryResponse).when(httpClient).directoryRequest(any(ContractParametersCheckRequest.class));

        // call method
        Map<String, String> errors = service.check(Utils.createContractParametersCheckRequest());

        // assertions
        Assertions.assertEquals(1, errors.size());
    }

    @Test
    void checkException() {
        // create mock
        doThrow(new PluginException("foo")).when(httpClient).directoryRequest(any(ContractParametersCheckRequest.class));

        // call method
        Map<String, String> errors = service.check(Utils.createContractParametersCheckRequest());

        // assertions
        Assertions.assertEquals(1, errors.size());
        Assertions.assertTrue(errors.containsKey(ContractParametersCheckRequest.GENERIC_ERROR));
        Assertions.assertEquals("foo", errors.get(ContractParametersCheckRequest.GENERIC_ERROR));
    }

    @Test
    void retrievePluginConfiguration() {

        final Map<String, String> configurationMap = new HashMap<>();
        configurationMap.put(PartnerConfigurationKeys.ACQUIRERS, "[{\"NAME\":\"BNPP\",\"URL\":\"http://www.urlbnp.com\",\"PUBLIC_KEY\":\"publicKey\",\"IDEAL_PUBLIC\":\"idealPublic\",\"PUBLIC_KEY_ID\":\"publicKeyId\",\"MERCHANT_ID\":\"017001580\",\"SUBMERCHANT_ID\":\"0\"},{\"NAME\":\"ABNAMRO\",\"URL\":\"http://www.abnamro.com\",\"PUBLIC_KEY\":\"publicKey\",\"IDEAL_PUBLIC\":\"abnaMROKey\",\"PUBLIC_KEY_ID\":\"publicKeyId\",\"MERCHANT_ID\":\"01234567\",\"SUBMERCHANT_ID\":\"1\"}]");
        configurationMap.put(PartnerConfigurationKeys.ACQUIRERS_SENSITIVE_DATA, "[{\"NAME\":\"BNPP\",\"PRIVATE_KEY\":\"privateKeyBNPP\"},{\"NAME\":\"ABNAMRO\",\"PRIVATE_KEY\":\"privateKeyAbnamro\"}]");

        final PartnerConfiguration partnerConfiguration = new PartnerConfiguration(configurationMap, new HashMap<>());
        final RetrievePluginConfigurationRequest retrievePluginConfigurationRequest = RetrievePluginConfigurationRequest.
                RetrieveConfigurationRequestBuilder
                .aRetrieveConfigurationRequest()
                .withPartnerConfiguration(partnerConfiguration).build();

        final ArgumentCaptor<RetrievePluginConfigurationRequest> captor = ArgumentCaptor.forClass(RetrievePluginConfigurationRequest.class);
        // create mock
        final IdealDirectoryResponse directoryResponse = XMLUtils.getInstance().fromXML(Utils.directoryResponseOK, IdealDirectoryResponse.class);
        doReturn(directoryResponse).when(httpClient).directoryRequest(captor.capture(), any(PartnerAcquirer.class));

        // call method
        final String pluginConfiguration = service.retrievePluginConfiguration(retrievePluginConfigurationRequest);

        // assertions
        Assertions.assertNotNull(pluginConfiguration);

        final Map<String, String> directoryMap = JSONUtils.getInstance().fromJSON(pluginConfiguration);
        assertEquals(2, directoryMap.size());

        final Directory directory = XMLUtils.getInstance().fromXML(directoryMap.get("BNPP"), Directory.class);

        assertNotNull(captor.getValue());
        assertNotNull(captor.getValue().getContractConfiguration());
        assertNotNull(captor.getValue().getContractConfiguration().getProperty(ContractConfigurationKeys.MERCHANT_ID_KEY));
        assertEquals("01234567", captor.getValue().getContractConfiguration().getProperty(ContractConfigurationKeys.MERCHANT_ID_KEY).getValue());
        assertEquals("1", captor.getValue().getContractConfiguration().getProperty(ContractConfigurationKeys.MERCHANT_SUBID_KEY).getValue());

        assertEquals(2, directory.getCountries().size());
        assertEquals(3, directory.getCountries().get(0).getIssuers().size());
    }

    @Test
    void retrievePluginConfigurationKO() {
        // create mock
        RetrievePluginConfigurationRequest pluginConfigurationRequest = RetrievePluginConfigurationRequest.RetrieveConfigurationRequestBuilder
                .aRetrieveConfigurationRequest()
                .withPluginConfiguration("foo")
                .build();
        IdealDirectoryResponse directoryResponse = XMLUtils.getInstance().fromXML(Utils.errorResponse, IdealDirectoryResponse.class);
        doReturn(directoryResponse).when(httpClient).directoryRequest(any(RetrievePluginConfigurationRequest.class), any(PartnerAcquirer.class));

        // call method
        String pluginConfiguration = service.retrievePluginConfiguration(pluginConfigurationRequest);

        // assertions
        Assertions.assertEquals(pluginConfigurationRequest.getPluginConfiguration(), pluginConfiguration);
    }

    @Test
    void retrievePluginConfigurationException() {
        // create mock
        RetrievePluginConfigurationRequest pluginConfigurationRequest = RetrievePluginConfigurationRequest.RetrieveConfigurationRequestBuilder
                .aRetrieveConfigurationRequest()
                .withPluginConfiguration("foo")
                .build();
        doThrow(new PluginException("bar")).when(httpClient).directoryRequest(any(RetrievePluginConfigurationRequest.class), any(PartnerAcquirer.class));

        // call method
        String pluginConfiguration = service.retrievePluginConfiguration(pluginConfigurationRequest);

        // assertions
        Assertions.assertEquals(pluginConfigurationRequest.getPluginConfiguration(), pluginConfiguration);
    }

    @Test
    void getReleaseInformation() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String version = "M.m.p";

        // given: the release properties are OK
        doReturn(version).when(releaseProperties).get("release.version");
        Calendar cal = new GregorianCalendar();
        cal.set(2019, Calendar.AUGUST, 19);
        doReturn(formatter.format(cal.getTime())).when(releaseProperties).get("release.date");

        // when: calling the method getReleaseInformation
        ReleaseInformation releaseInformation = service.getReleaseInformation();

        // then: releaseInformation contains the right values
        assertEquals(version, releaseInformation.getVersion());
        assertEquals(2019, releaseInformation.getDate().getYear());
        assertEquals(Month.AUGUST, releaseInformation.getDate().getMonth());
        assertEquals(19, releaseInformation.getDate().getDayOfMonth());
    }

    @Test
    void getName() {
        String name = service.getName(Locale.getDefault());
        assertNotNull(name);
    }

}