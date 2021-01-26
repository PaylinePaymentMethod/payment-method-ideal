package com.payline.payment.ideal.service.impl;

import com.payline.payment.ideal.Utils;
import com.payline.payment.ideal.bean.Directory;
import com.payline.payment.ideal.bean.response.IdealDirectoryResponse;
import com.payline.payment.ideal.exception.PluginException;
import com.payline.payment.ideal.utils.XMLUtils;
import com.payline.payment.ideal.utils.http.IdealHttpClient;
import com.payline.payment.ideal.utils.properties.ReleaseProperties;
import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.configuration.request.RetrievePluginConfigurationRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
        List<AbstractParameter> parameters = service.getParameters(Locale.FRANCE);
        Assertions.assertEquals(2, parameters.size());

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
        // create mock
        IdealDirectoryResponse directoryResponse = XMLUtils.getInstance().fromXML(Utils.directoryResponseOK, IdealDirectoryResponse.class);
        doReturn(directoryResponse).when(httpClient).directoryRequest(any(RetrievePluginConfigurationRequest.class));

        // call method
        String pluginConfiguration = service.retrievePluginConfiguration(Mockito.mock(RetrievePluginConfigurationRequest.class));

        // assertions
        Assertions.assertNotNull(pluginConfiguration);
        Directory directory = XMLUtils.getInstance().fromXML(pluginConfiguration, Directory.class);

        Assertions.assertEquals(2, directory.getCountries().size());
        Assertions.assertEquals(3, directory.getCountries().get(0).getIssuers().size());
    }

    @Test
    void retrievePluginConfigurationKO() {
        // create mock
        RetrievePluginConfigurationRequest pluginConfigurationRequest = RetrievePluginConfigurationRequest.RetrieveConfigurationRequestBuilder
                .aRetrieveConfigurationRequest()
                .withPluginConfiguration("foo")
                .build();
        IdealDirectoryResponse directoryResponse = XMLUtils.getInstance().fromXML(Utils.errorResponse, IdealDirectoryResponse.class);
        doReturn(directoryResponse).when(httpClient).directoryRequest(any(RetrievePluginConfigurationRequest.class));

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
        doThrow(new PluginException("bar")).when(httpClient).directoryRequest(any(RetrievePluginConfigurationRequest.class));

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