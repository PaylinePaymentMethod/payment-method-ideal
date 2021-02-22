package com.payline.payment.ideal.service.impl;

import com.payline.payment.ideal.Utils;
import com.payline.payment.ideal.bean.IdealError;
import com.payline.payment.ideal.bean.Transaction;
import com.payline.payment.ideal.bean.response.IdealStatusResponse;
import com.payline.payment.ideal.exception.PluginException;
import com.payline.payment.ideal.utils.http.IdealHttpClient;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.BankAccount;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.BankTransfer;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.EmptyTransactionDetails;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentWithRedirectionServiceImplTest {
    @Mock
    private IdealHttpClient client;

    @InjectMocks
    @Spy
    private PaymentWithRedirectionServiceImpl underTest;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void finalizeRedirectionPayment() {
        // create mock
        String id = "anId";
        doReturn(new IdealStatusResponse()).when(client).statusRequest(any(RedirectionPaymentRequest.class));

        PaymentResponseSuccess success = PaymentResponseSuccess.PaymentResponseSuccessBuilder
                .aPaymentResponseSuccess()
                .withTransactionDetails(new EmptyTransactionDetails())
                .withPartnerTransactionId(id)
                .build();
        doReturn(success).when(underTest).handleResponse(any(), any(), any());

        // call method
        RedirectionPaymentRequest request = Utils.createCompleteRedirectionPayment(id);
        PaymentResponse response = underTest.finalizeRedirectionPayment(request);

        // assertions
        Assertions.assertEquals(PaymentResponseSuccess.class, response.getClass());
        PaymentResponseSuccess responseSuccess = (PaymentResponseSuccess) response;
        Assertions.assertEquals(id, responseSuccess.getPartnerTransactionId());
    }

    @Test
    void finalizeRedirectionPaymentWithPluginException() {
        // create mock
        RedirectionPaymentRequest request = Utils.createCompleteRedirectionPayment("anId");
        doThrow(new PluginException("an error")).when(underTest).handleResponse(any(), any(), any());

        // call method
        PaymentResponse response = underTest.finalizeRedirectionPayment(request);

        // assertions
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

    @Test
    void handleSessionExpired() {
        // create mock
        String id = "anId";

        doReturn(mock(IdealStatusResponse.class)).when(client).statusRequest(any(TransactionStatusRequest.class));

        PaymentResponseSuccess success = PaymentResponseSuccess.PaymentResponseSuccessBuilder
                .aPaymentResponseSuccess()
                .withTransactionDetails(new EmptyTransactionDetails())
                .withPartnerTransactionId(id)
                .build();
        doReturn(success).when(underTest).handleResponse(any(), any(), any());

        // call method
        TransactionStatusRequest request = Utils.createTransactionRequestBuilder().build();
        PaymentResponse response = underTest.handleSessionExpired(request);

        // assertions
        Assertions.assertEquals(PaymentResponseSuccess.class, response.getClass());
        PaymentResponseSuccess responseSuccess = (PaymentResponseSuccess) response;
        Assertions.assertEquals(id, responseSuccess.getPartnerTransactionId());
    }

    @Test
    void handleSessionExpiredWithPluginException() {
        // create mock
        TransactionStatusRequest request = Utils.createTransactionRequestBuilder().build();
        doThrow(new PluginException("an error")).when(underTest).handleResponse(any(), any(), any());

        // call method
        PaymentResponse response = underTest.handleSessionExpired(request);

        // assertions
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

    @Test
    void handleResponseSUCCESS() {
        // create mock
        String id = "anId";
        Transaction transaction = Transaction.builder()
                .transactionId(id)
                .status(Transaction.Status.SUCCESS)
                .consumerBIC("BIC")
                .consumerIBAN("IBAN")
                .build();


        final Buyer buyer = Utils.createBuyer();

        IdealStatusResponse idealStatusResponse = new IdealStatusResponse(null, transaction);

        // call method
        PaymentResponse response = underTest.handleResponse(id, idealStatusResponse, buyer);

        // assertions
        Assertions.assertEquals(PaymentResponseSuccess.class, response.getClass());
        PaymentResponseSuccess responseSuccess = (PaymentResponseSuccess) response;
        Assertions.assertEquals(id, responseSuccess.getPartnerTransactionId());
        Assertions.assertEquals(Transaction.Status.SUCCESS.name(), responseSuccess.getStatusCode());
    }

    @Test
    void handleResponseOTHER() {
        // create mock
        String id = "anId";
        Transaction transaction = Transaction.builder()
                .transactionId(id)
                .status(Transaction.Status.CANCELLED)
                .build();
        IdealStatusResponse idealStatusResponse = new IdealStatusResponse(null, transaction);
        final Buyer buyer = Utils.createBuyer();

        // call method
        PaymentResponse response = underTest.handleResponse(id, idealStatusResponse, buyer);

        // assertions
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assertions.assertEquals(id, responseFailure.getPartnerTransactionId());
        Assertions.assertEquals(Transaction.Status.CANCELLED.name(), responseFailure.getErrorCode());
    }




    private static Stream<Arguments> parse_nonExistingTransaction_set() {
        return Stream.of(
                Arguments.of(Transaction.Status.CANCELLED, FailureCause.CANCEL),
                Arguments.of(Transaction.Status.FAILURE, FailureCause.REFUSED),
                Arguments.of(Transaction.Status.EXPIRED, FailureCause.SESSION_EXPIRED)
        );
    }

    @ParameterizedTest
    @MethodSource("parse_nonExistingTransaction_set")
    void handleResponseOTHER(Transaction.Status status, FailureCause expectedCause) {
        // create mock
        String id = "anId";
        Transaction transaction = Transaction.builder()
                .transactionId(id)
                .status(status)
                .build();
        IdealStatusResponse idealStatusResponse = new IdealStatusResponse(null, transaction);
        final Buyer buyer = Utils.createBuyer();

        // call method
        PaymentResponse response = underTest.handleResponse(id, idealStatusResponse, buyer);

        // assertions
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assertions.assertEquals(id, responseFailure.getPartnerTransactionId());
        Assertions.assertEquals(status.name(), responseFailure.getErrorCode());
        Assertions.assertEquals(expectedCause, responseFailure.getFailureCause());
    }

    @Test
    void handleResponseWithError() {
        // create mock
        String id = "anId";
        String errorCode = "AP110";
        final Buyer buyer = Utils.createBuyer();
        IdealError error = new IdealError(errorCode
                , "message"
                , "details"
                , "action"
                , "consumerMessage");
        IdealStatusResponse idealStatusResponse = new IdealStatusResponse(error, null, null);

        // call method
        PaymentResponse response = underTest.handleResponse(id, idealStatusResponse, buyer);

        // assertions
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assertions.assertEquals(id, responseFailure.getPartnerTransactionId());
        Assertions.assertEquals(errorCode, responseFailure.getErrorCode());
    }

    @Nested
    class testBuildBankTransfer {

        @ParameterizedTest
        @CsvSource({ "LastName, FirstName, LastName FirstName", "LastName, null, LastName", "null, FirstName, FirstName", "null, null, ''" })
        void withHolder(String lastName, String firstName, String expectedResult) {

            if (firstName.equals("null")) {
                firstName = null;
            }
            if (lastName.equals("null")) {
                lastName = null;
            }

            final Buyer.FullName fullName = new Buyer.FullName(firstName, lastName, "M");

            final Transaction transaction = Transaction.builder()
                    .consumerIBAN("FR7630001007941234567890185")
                    .consumerBIC("BNPAFRPPTAS").build();

            final IdealStatusResponse idealStatusResponse = new IdealStatusResponse(null, null, transaction);

            final BankTransfer expectedBankTransfert = underTest.buildBankTransfer(idealStatusResponse,
                    Buyer.BuyerBuilder.aBuyer().withFullName(fullName).build());

            assertNotNull(expectedBankTransfert);
            assertNotNull(expectedBankTransfert.getOwner());
            BankAccount owner = expectedBankTransfert.getOwner();
            assertEquals("FR7630001007941234567890185", owner.getIban());
            assertEquals("BNPAFRPPTAS", owner.getBic());
            assertEquals(expectedResult, owner.getHolder() );
            assertEquals("", owner.getAccountNumber());
            assertEquals("", owner.getBankCode());
            assertEquals("", owner.getBankName());
            assertEquals("", owner.getCountryCode());
        }

    }

}