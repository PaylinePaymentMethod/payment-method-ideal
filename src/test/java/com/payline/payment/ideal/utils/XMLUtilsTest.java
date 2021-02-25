package com.payline.payment.ideal.utils;

import com.payline.payment.ideal.Utils;
import com.payline.payment.ideal.bean.Country;
import com.payline.payment.ideal.bean.request.IdealDirectoryRequest;
import com.payline.payment.ideal.bean.request.IdealPaymentRequest;
import com.payline.payment.ideal.bean.request.IdealStatusRequest;
import com.payline.payment.ideal.bean.response.IdealDirectoryResponse;
import com.payline.payment.ideal.bean.response.IdealPaymentResponse;
import com.payline.payment.ideal.bean.response.IdealStatusResponse;
import com.payline.payment.ideal.service.IdealPaymentRequestService;
import com.payline.payment.ideal.service.IdealStatusRequestService;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import jdk.internal.org.jline.utils.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class XMLUtilsTest {

    @InjectMocks
    XMLUtils xmlUtils;

    private IdealPaymentRequestService idealPaymentRequestService = IdealPaymentRequestService.getInstance();
    private IdealStatusRequestService idealStatusRequestService = IdealStatusRequestService.getInstance();

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void toXmlDirectoryRequest() {
        ContractConfiguration configuration = Utils.createContractConfiguration();
        IdealDirectoryRequest request = new IdealDirectoryRequest(configuration);

        String s = xmlUtils.toXml(request);

        Assertions.assertNotNull(s);
        Assertions.assertTrue(s.contains("<Merchant"));
        Assertions.assertTrue(s.contains("<merchantID"));


    }

    @Test
    void toXmlPaymentRequest() {
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().build();
        IdealPaymentRequest request = idealPaymentRequestService.buildIdealPaymentRequest(paymentRequest);

        String s = xmlUtils.toXml(request);

        System.out.println(s);
        Assertions.assertNotNull(s);
        Assertions.assertTrue(s.contains("<Issuer"));
        Assertions.assertTrue(s.contains("<issuerID"));
        Assertions.assertTrue(s.contains("<Merchant"));
        Assertions.assertTrue(s.contains("<merchantID"));
        Assertions.assertTrue(s.contains("<merchantReturnURL"));
        Assertions.assertTrue(s.contains("<Transaction"));
        Assertions.assertTrue(s.contains("<purchaseID"));
        Assertions.assertTrue(s.contains("<amount"));
        Assertions.assertTrue(s.contains("<currency"));
        Assertions.assertTrue(s.contains("<expirationPeriod"));
    }

    @Test
    void toXmlStatusRequest() {

        RedirectionPaymentRequest redirectionPaymentRequest = Utils.createCompleteRedirectionPayment("123123");
        IdealStatusRequest request = idealStatusRequestService.buildIdealStatusRequest(redirectionPaymentRequest);
        String s = xmlUtils.toXml(request);

        Assertions.assertNotNull(s);
        Assertions.assertTrue(s.contains("<Merchant"));
        Assertions.assertTrue(s.contains("<merchantID"));
        Assertions.assertTrue(s.contains("<Transaction"));
        Assertions.assertTrue(s.contains("<transactionID"));
    }

    @Test
    void fromXMLDirectoryResponse() {
        IdealDirectoryResponse response =  xmlUtils.fromXML(Utils.directoryResponseOK, IdealDirectoryResponse.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("0001", response.getAcquirer().getAcquirerId());

        Country res = response.getDirectory().getCountries().stream()
                .filter(country -> "Nederland".equals(country.getCountryNames()))
                .findAny()
                .orElse(null);

        Assertions.assertTrue(response.getDirectory().getCountries().contains(res));

    }


    @Test
    void fromXMLPaymentResponse() {
        IdealPaymentResponse response = xmlUtils.fromXML(Utils.TransactionResponse, IdealPaymentResponse.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("0001000000000001", response.getTransaction().getTransactionId());
        Assertions.assertEquals("0001", response.getAcquirer().getAcquirerId());
        Assertions.assertEquals("https://www.issuingbank.eu/ideal", response.getIssuer().getIssuerAuthenticationURL());
    }


    @Test
    void fromXMLStatusResponse() {
        IdealStatusResponse response = xmlUtils.fromXML(Utils.statusResponseOK, IdealStatusResponse.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("0001000000000001", response.getTransaction().getTransactionId());
        Assertions.assertEquals("0001", response.getAcquirer().getAcquirerId());
        Assertions.assertEquals("Success", response.getTransaction().getStatus().getStatusCode());
        Assertions.assertEquals("NL44RABO0123456789", response.getTransaction().getConsumerIBAN());
    }
}