package com.payline.payment.ideal.service.impl;

import com.payline.payment.ideal.Utils;
import com.payline.payment.ideal.exception.PluginException;
import com.payline.pmapi.bean.paymentform.bean.field.PaymentFormInputFieldSelect;
import com.payline.payment.ideal.utils.JSONUtils;
import com.payline.payment.ideal.utils.constant.ContractConfigurationKeys;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.ContractProperty;
import com.payline.pmapi.bean.paymentform.bean.field.SelectOption;
import com.payline.pmapi.bean.paymentform.bean.form.BankTransferForm;
import com.payline.pmapi.bean.paymentform.bean.form.CustomForm;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseFailure;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class PaymentFormConfigurationServiceImplTest {
    private static final String countries = "<Directory>" +
            "      <directoryDateTimestamp>2004-11-10T10:15:12.145Z</directoryDateTimestamp>" +
            "      <Country>" +
            "         <countryNames>Nederland</countryNames>" +
            "         <Issuer>" +
            "            <issuerID>ABNANL2AXXX</issuerID>" +
            "            <issuerName>ABN AMRO Bank</issuerName>" +
            "         </Issuer>" +
            "         <Issuer>" +
            "            <issuerID>FRBKNL2LXXX</issuerID>" +
            "            <issuerName>Friesland Bank</issuerName>" +
            "         </Issuer>" +
            "         <Issuer>" +
            "            <issuerID>INGBNL2AXXX</issuerID>" +
            "            <issuerName>ING</issuerName>" +
            "         </Issuer>" +
            "      </Country>" +
            "      <Country>" +
            "         <countryNames>België/Belgique</countryNames>" +
            "         <Issuer>" +
            "            <issuerID>KREDBE22XXX</issuerID>" +
            "            <issuerName>KBC</issuerName>" +
            "         </Issuer>" +
            "      </Country>" +
            "   </Directory>";

    private JSONUtils jsonUtils = JSONUtils.getInstance();

    @Spy
    PaymentFormConfigurationServiceImpl service = new PaymentFormConfigurationServiceImpl();

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getPaymentFormConfiguration() {

        final Map<String, String> issuerConfiguration = new HashMap<>();
        issuerConfiguration.put("BNPP", countries);

        final Map<String, String> partner = new HashMap<>();
        partner.put("acquirers", "[{\"NAME\":\"BNPP\",\"URL\":\"https://www.google.fr/\",\"PUBLIC_KEY\":\"publicKey\",\"IDEAL_PUBLIC\":\"idealPublicKey\",\"PUBLIC_KEY_ID\":\"publicKeyId\",\"MERCHANT_ID\":\"0123456\",\"SUBMERCHANT_ID\":\"0\"}]");

        final HashMap<String, ContractProperty> contractPropertyList = new HashMap<>();
        contractPropertyList.put(ContractConfigurationKeys.ACQUIRER_ID, new ContractProperty("BNPP"));
        final ContractConfiguration contractConfiguration = new ContractConfiguration("IDEAL_V2", contractPropertyList);
        final PartnerConfiguration partnerConfiguration = new PartnerConfiguration(partner, new HashMap<>());
        // create data
        final PaymentFormConfigurationRequest request = Utils.createDefaultPaymentFormConfigurationRequestBuilder()
                .withPluginConfiguration(jsonUtils.toJson(issuerConfiguration))
                .withPartnerConfiguration(partnerConfiguration)
                .withContractConfiguration(contractConfiguration)
                .build();

        // call method
        final PaymentFormConfigurationResponse formConfiguration = service.getPaymentFormConfiguration(request);

        // assertion
        assertEquals(PaymentFormConfigurationResponseSpecific.class, formConfiguration.getClass());
        PaymentFormConfigurationResponseSpecific responseSpecific = (PaymentFormConfigurationResponseSpecific) formConfiguration;

        assertEquals(CustomForm.class, responseSpecific.getPaymentForm().getClass());
        CustomForm customForm = (CustomForm) responseSpecific.getPaymentForm();

        PaymentFormInputFieldSelect select = (PaymentFormInputFieldSelect) customForm.getCustomFields().get(0);

        SelectOption bank1 = select.getSelectOptions().stream()
                .filter(bank -> "ABNANL2AXXX".equals(bank.getKey()))
                .findAny()
                .orElse(null);
        assertEquals("ABN AMRO Bank", bank1.getValue());

        SelectOption bank2 = select.getSelectOptions().stream()
                .filter(bank -> "FRBKNL2LXXX".equals(bank.getKey()))
                .findAny()
                .orElse(null);
        assertEquals("Friesland Bank", bank2.getValue());

        SelectOption bank3 = select.getSelectOptions().stream()
                .filter(bank -> "INGBNL2AXXX".equals(bank.getKey()))
                .findAny()
                .orElse(null);
        assertEquals("ING", bank3.getValue());

        SelectOption bank4 = select.getSelectOptions().stream()
                .filter(bank -> "KREDBE22XXX".equals(bank.getKey()))
                .findAny()
                .orElse(null);
        assertEquals("KBC", bank4.getValue());
    }

    @Test
    void getPaymentFormConfigurationKO() {
        // create data
        PaymentFormConfigurationRequest request = Utils.createDefaultPaymentFormConfigurationRequestBuilder()
                .withPluginConfiguration("foo")
                .build();

        // call method
        PaymentFormConfigurationResponse formConfiguration = service.getPaymentFormConfiguration(request);

        // assertion
        assertEquals(PaymentFormConfigurationResponseFailure.class, formConfiguration.getClass());
    }


    @Test
    void getOptionFromPluginConfiguration() {
        // call method
        List<SelectOption> options = service.getOptionFromPluginConfiguration(countries);

        // assertion
        Assertions.assertNotNull(options);
        assertEquals(4, options.size());
    }

    @Test
    void getOptionFromPluginConfigurationnull() {
        assertThrows(PluginException.class, () -> service.getOptionFromPluginConfiguration(null));
    }

    @Test
    void getOptionFromPluginConfigurationKO() {
        assertThrows(PluginException.class, () -> service.getOptionFromPluginConfiguration("foo"));
    }



}