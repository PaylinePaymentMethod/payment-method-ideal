package com.payline.payment.ideal.utils.http;

import com.payline.payment.ideal.Utils;
import com.payline.payment.ideal.bean.PartnerAcquirer;
import com.payline.payment.ideal.bean.request.IdealDirectoryRequest;
import com.payline.payment.ideal.bean.response.IdealDirectoryResponse;
import com.payline.payment.ideal.bean.response.IdealPaymentResponse;
import com.payline.payment.ideal.bean.response.IdealStatusResponse;
import com.payline.payment.ideal.exception.PluginException;
import com.payline.payment.ideal.service.impl.PartnerConfigurationService;
import com.payline.payment.ideal.utils.XMLUtils;
import com.payline.payment.ideal.utils.constant.ContractConfigurationKeys;
import com.payline.payment.ideal.utils.security.SignatureUtils;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class IdealHttpClientTest {
    private String signedResponse = "foo";

    @Mock
    private PartnerConfigurationService partnerConfigurationService;

    @InjectMocks
    @Spy
    private IdealHttpClient client = IdealHttpClient.getInstance();

    @Mock
    private SignatureUtils signatureUtils;

    @Mock
    private CloseableHttpClient closeableHttpClient;
    // TODO : Vérifier si il y a plusieurs appel, sinon délcarer en local
    private XMLUtils xmlUtils;


    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);

        xmlUtils = XMLUtils.getInstance();

        doReturn(null).when(signatureUtils).getPrivateKeyFromString(anyString());
        doReturn(null).when(signatureUtils).getPublicKeyFromString(anyString());
        doReturn(signedResponse).when(signatureUtils).signXML(any(), any(), any(), any());

    }

    @Test
    void directoryRequest() throws Exception {
        // init mock
        StringResponse stringResponse = mockStringResponse(200, null, Utils.directoryResponseOK, null);
        final PartnerAcquirer partnerAcquirer = new PartnerAcquirer();
        partnerAcquirer.setUrl("http://www.monext.net");


        doReturn(partnerAcquirer).when(partnerConfigurationService).getPartnerAcquirer(any(), anyString());
        doReturn("hello").when(client).createBody(any(), any(PartnerAcquirer.class));
        doReturn(stringResponse).when(client).getStringResponse(anyString(), any());
        doNothing().when(signatureUtils).verifySignatureXML(any(), any());
        doReturn(true).when(stringResponse).isSuccess();


        // call method
        final ContractParametersCheckRequest contractParametersCheckRequest = Utils.createContractParametersCheckRequest();
        contractParametersCheckRequest.getAccountInfo().put(ContractConfigurationKeys.ACQUIRER_ID, "acquirerId");
        final IdealDirectoryResponse response = client.directoryRequest(contractParametersCheckRequest);

        // assertions
        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.getDirectory().getCountries().isEmpty());

        // then: no HTTP call is made
        verify(closeableHttpClient, never()).execute(any(HttpRequestBase.class));
    }

    @Test
    void directoryRequestWithError() throws Exception {
        // init mock
        StringResponse stringResponse = mockStringResponse(200, null, Utils.errorResponse, null);
        final PartnerAcquirer partnerAcquirer = new PartnerAcquirer();
        partnerAcquirer.setUrl("http://www.monext.net");

        doReturn(partnerAcquirer).when(partnerConfigurationService).getPartnerAcquirer(any(), anyString());
        doReturn("hello").when(client).createBody(any(), any(PartnerAcquirer.class));
        doReturn(stringResponse).when(client).getStringResponse(anyString(), any());
        doNothing().when(signatureUtils).verifySignatureXML(any(), any());
        doReturn(true).when(stringResponse).isSuccess();

        // call method
        final ContractParametersCheckRequest contractParametersCheckRequest = Utils.createContractParametersCheckRequest();
        contractParametersCheckRequest.getAccountInfo().put(ContractConfigurationKeys.ACQUIRER_ID, "acquirerId");

        final IdealDirectoryResponse response = client.directoryRequest(contractParametersCheckRequest);

        // assertions
        Assertions.assertNotNull(response);
        Assertions.assertEquals("AP1100", response.getError().getErrorCode());
        Assertions.assertEquals("MerchantID unknown", response.getError().getErrorMessage());

        // then: no HTTP call is made
        verify(closeableHttpClient, never()).execute(any(HttpRequestBase.class));
    }


    @Test
    void transactionRequest() throws Exception {
        // init mock
        StringResponse stringResponse = mockStringResponse(200, null, Utils.TransactionResponse, null);
        IdealPaymentResponse expectedResponse = xmlUtils.fromXML(stringResponse.getContent(), IdealPaymentResponse.class);
        final PartnerAcquirer partnerAcquirer = new PartnerAcquirer();
        partnerAcquirer.setUrl("http://www.google.com");

        doReturn("hello").when(client).createBody(any(), any(PartnerConfiguration.class));
        doReturn(stringResponse).when(client).getStringResponse(eq(partnerAcquirer.getUrl()), any());
        doNothing().when(signatureUtils).verifySignatureXML(any(), any());
        doReturn(partnerAcquirer).when(client).fetchPartnerAcquirer(any(), any());
        doReturn(true).when(stringResponse).isSuccess();

        // call method
        PaymentRequest request = Utils.createCompletePaymentBuilder().build();
        IdealPaymentResponse response = client.transactionRequest(request);

        // assertions
        Assertions.assertNotNull(response);
        Assertions.assertEquals(expectedResponse.getTransaction().getTransactionId(), response.getTransaction().getTransactionId());
        Assertions.assertEquals(expectedResponse.getTransaction().getConsumerIBAN(), response.getTransaction().getConsumerIBAN());
        Assertions.assertEquals(expectedResponse.getIssuer().getIssuerAuthenticationURL(), response.getIssuer().getIssuerAuthenticationURL());

        // then: no HTTP call is made
        verify(closeableHttpClient, never()).execute(any(HttpRequestBase.class));
    }

    @Test
    void statusRequest() throws Exception {
        // init mock
        StringResponse stringResponse = mockStringResponse(200, null, Utils.statusResponseOK, null);
        IdealStatusResponse expectedResponse = xmlUtils.fromXML(stringResponse.getContent(), IdealStatusResponse.class);
        final PartnerAcquirer partnerAcquirer = new PartnerAcquirer();
        partnerAcquirer.setUrl("http://www.google.com");

        doReturn("hello").when(client).createBody(any(), any(PartnerConfiguration.class));
        doReturn(stringResponse).when(client).getStringResponse(eq(partnerAcquirer.getUrl()), any());
        doNothing().when(signatureUtils).verifySignatureXML(any(), any());
        doReturn(partnerAcquirer).when(client).fetchPartnerAcquirer(any(), any());
        doReturn(true).when(stringResponse).isSuccess();

        // call method
        TransactionStatusRequest request = Utils.createTransactionRequestBuilder().build();
        IdealStatusResponse response = client.statusRequest(request);

        // assertions
        Assertions.assertEquals(expectedResponse.getTransaction().getTransactionId(), response.getTransaction().getTransactionId());
        Assertions.assertEquals(expectedResponse.getTransaction().getConsumerIBAN(), response.getTransaction().getConsumerIBAN());
        Assertions.assertEquals(expectedResponse.getTransaction().getStatus(), response.getTransaction().getStatus());

        // then: no HTTP call is made
        verify(closeableHttpClient, never()).execute(any(HttpRequestBase.class));
    }


    @Test
    void checkResponse() {
        // create data
        StringResponse response = mockStringResponse(200, null, "this is an answer", null);

        doReturn(true).when(response).isSuccess();

        // call method
        Assertions.assertDoesNotThrow(() -> client.checkResponse(response));
    }

    @Test
    void checkResponse403() {


        // create data
        StringResponse response = mockStringResponse(403, null, "this is an answer", null);

        // call method
        try {
            client.checkResponse(response);
            Assertions.fail("CheckResponse with code 403 did not throws Exception");
        } catch (PluginException e) {
            // assertions
            Assertions.assertEquals("this is an answer", e.getErrorCode());
            Assertions.assertEquals(FailureCause.INVALID_DATA, e.getFailureCause());
        }
    }

    @Test
    void checkResponse505() {
        // create data
        StringResponse response = mockStringResponse(505, null, "this is an answer", null);

        // assertions
        Assertions.assertThrows(PluginException.class, () -> client.checkResponse(response));
    }

    @Test
    void createBody() {
        IdealDirectoryRequest request = new IdealDirectoryRequest(Utils.createContractConfiguration());

        String body = client.createBody(request, Utils.createDefaultPartnerConfiguration());

        Assertions.assertNotNull(body);
        Assertions.assertEquals(signedResponse, body);
    }


    private static StringResponse mockStringResponse(int statusCode, String statusMessage, String content, Map<String, String> headers) {
        StringResponse response = mock(StringResponse.class);
        doReturn(content).when(response).getContent();
        doReturn(headers).when(response).getHeaders();
        doReturn(statusCode).when(response).getStatusCode();
        doReturn(statusMessage).when(response).getStatusMessage();
        return response;
    }
}