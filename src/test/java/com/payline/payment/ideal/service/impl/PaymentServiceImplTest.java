package com.payline.payment.ideal.service.impl;

import com.payline.payment.ideal.Utils;
import com.payline.payment.ideal.bean.IdealError;
import com.payline.payment.ideal.bean.Issuer;
import com.payline.payment.ideal.bean.Transaction;
import com.payline.payment.ideal.bean.response.IdealPaymentResponse;
import com.payline.payment.ideal.exception.PluginException;
import com.payline.payment.ideal.utils.http.IdealHttpClient;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

class PaymentServiceImplTest {
    @Mock
    private IdealHttpClient client;

    @InjectMocks
    private PaymentServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void paymentRequest() {
        // create Mock
        String id = "anId";
        Transaction transaction = Transaction.builder()
                .transactionId(id)
                .build();

        Issuer issuer = Issuer.builder()
                .issuerId("issuerId")
                .issuerName("issuerName")
                .issuerAuthenticationURL(Utils.SUCCESS_URL)
                .build();

        IdealPaymentResponse idealPaymentResponse = new IdealPaymentResponse(null, null, issuer, transaction);
        doReturn(idealPaymentResponse).when(client).transactionRequest(any());

        // call method
        PaymentResponse response = service.paymentRequest(any());

        // assertions
        Assertions.assertEquals(PaymentResponseRedirect.class, response.getClass());
        PaymentResponseRedirect responseRedirect = (PaymentResponseRedirect) response;
        Assertions.assertEquals(id, responseRedirect.getPartnerTransactionId());
        Assertions.assertEquals(Utils.SUCCESS_URL, responseRedirect.getRedirectionRequest().getUrl().toString());

    }

    @Test
    void paymentRequestwithError() {
        // create Mock
        String errorCode = "AP110";
        IdealError error = new IdealError(errorCode
                , "message"
                , "details"
                , "action"
                , "consumerMessage");
        IdealPaymentResponse idealPaymentResponse = new IdealPaymentResponse(error, null, null, null);

        doReturn(idealPaymentResponse).when(client).transactionRequest(any());

        // call method
        PaymentResponse response = service.paymentRequest(any());

        // assertions
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assertions.assertEquals(errorCode, responseFailure.getErrorCode());
    }

    @Test
    void paymentRequestWithException() {
        // create Mock
        doThrow(new PluginException("foo")).when(client).transactionRequest(any());

        // call method
        PaymentResponse response = service.paymentRequest(any());

        // assertions
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assertions.assertEquals("foo", responseFailure.getErrorCode());
    }

    @Test
    void paymentRequestwithMalformedUrlException() {
        // create Mock
        String id = "anId";
        Transaction transaction = Transaction.builder()
                .transactionId(id)
                .build();

        Issuer issuer = Issuer.builder()
                .issuerId("issuerId")
                .issuerName("issuerName")
                .issuerAuthenticationURL("a malformedUrl")
                .build();

        IdealPaymentResponse idealPaymentResponse = new IdealPaymentResponse(null, null, issuer, transaction);
        doReturn(idealPaymentResponse).when(client).transactionRequest(any());

        // call method
        PaymentResponse response = service.paymentRequest(any());
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

}