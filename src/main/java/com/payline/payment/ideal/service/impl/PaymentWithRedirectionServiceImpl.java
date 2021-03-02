package com.payline.payment.ideal.service.impl;

import com.payline.payment.ideal.bean.Transaction;
import com.payline.payment.ideal.bean.response.IdealStatusResponse;
import com.payline.payment.ideal.exception.PluginException;
import com.payline.payment.ideal.utils.PluginUtils;
import com.payline.payment.ideal.utils.http.IdealHttpClient;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.common.OnHoldCause;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.BankAccount;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.BankTransfer;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.EmptyTransactionDetails;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseOnHold;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.service.PaymentWithRedirectionService;
import lombok.extern.log4j.Log4j2;
@Log4j2
public class PaymentWithRedirectionServiceImpl implements PaymentWithRedirectionService {

    private IdealHttpClient client = IdealHttpClient.getInstance();

    @Override
    public PaymentResponse finalizeRedirectionPayment(RedirectionPaymentRequest redirectionPaymentRequest) {
        String partnerTransactionId = redirectionPaymentRequest.getTransactionId();
        try {
            IdealStatusResponse response = client.statusRequest(redirectionPaymentRequest);
            return this.handleResponse(partnerTransactionId, response, redirectionPaymentRequest.getBuyer());

        } catch (PluginException e) {
            return e.toPaymentResponseFailureBuilder()
                    .withPartnerTransactionId(partnerTransactionId)
                    .build();

        } catch (RuntimeException e) {
            log.error("Unexpected plugin error", e);
            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withErrorCode(PluginException.runtimeErrorCode(e))
                    .withFailureCause(FailureCause.INTERNAL_ERROR)
                    .build();
        }

    }

    @Override
    public PaymentResponse handleSessionExpired(TransactionStatusRequest transactionStatusRequest) {
        String partnerTransactionId = transactionStatusRequest.getTransactionId();
        try {
            IdealStatusResponse response = client.statusRequest(transactionStatusRequest);
            return handleResponse(partnerTransactionId, response, transactionStatusRequest.getBuyer());

        } catch (PluginException e) {
            return e.toPaymentResponseFailureBuilder()
                    .withPartnerTransactionId(partnerTransactionId)
                    .build();

        } catch (RuntimeException e) {
            log.error("Unexpected plugin error", e);
            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withErrorCode(PluginException.runtimeErrorCode(e))
                    .withFailureCause(FailureCause.INTERNAL_ERROR)
                    .build();
        }
    }

    PaymentResponse handleResponse(String partnerTransactionId, final IdealStatusResponse response, final Buyer buyer) {
        if (response.getError() != null) {
            String errorCode = response.getError().getErrorCode();
            log.info("an error occurred: {}",response.getError());

            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withErrorCode(errorCode)
                    .withFailureCause(PluginUtils.getFailureCauseFromIdealErrorCode(errorCode))
                    .build();

        } else {
            partnerTransactionId = response.getTransaction().getTransactionId();

            // check the status response: "SUCCESS", "OPEN", other...
            Transaction.Status status = response.getTransaction().getStatus();
            if (Transaction.Status.SUCCESS.equals(status)) {

                final BankTransfer bankTransfer = buildBankTransfer(response, buyer);

                return PaymentResponseSuccess.PaymentResponseSuccessBuilder
                        .aPaymentResponseSuccess()
                        .withPartnerTransactionId(partnerTransactionId)
                        .withStatusCode(status.name())
                        .withTransactionAdditionalData(response.getTransaction().getConsumerIBAN())
                        .withTransactionDetails(bankTransfer)
                        .build();
            } else if (Transaction.Status.OPEN.equals(status)) {
                return PaymentResponseOnHold.PaymentResponseOnHoldBuilder
                        .aPaymentResponseOnHold()
                        .withOnHoldCause(OnHoldCause.ASYNC_RETRY)
                        .withBuyerPaymentId(new EmptyTransactionDetails())
                        .withPartnerTransactionId(partnerTransactionId)
                        .withStatusCode(status.name())
                        .build();
            } else {
                FailureCause cause = FailureCause.PARTNER_UNKNOWN_ERROR;
                if (Transaction.Status.CANCELLED.equals(status)) {
                    cause = FailureCause.CANCEL;
                } else if (Transaction.Status.FAILURE.equals(status)) {
                    cause = FailureCause.REFUSED;
                } else if (Transaction.Status.EXPIRED.equals(status)) {
                    cause = FailureCause.SESSION_EXPIRED;
                }

                return PaymentResponseFailure.PaymentResponseFailureBuilder
                        .aPaymentResponseFailure()
                        .withPartnerTransactionId(partnerTransactionId)
                        .withErrorCode(status.name())
                        .withFailureCause(cause)
                        .build();
            }
        }

    }

    protected BankTransfer buildBankTransfer(final IdealStatusResponse response, final Buyer buyer) {
        final BankAccount.BankAccountBuilder owner = BankAccount.BankAccountBuilder.aBankAccount();
        if (buyer != null && buyer.getFullName() != null) {
            final Buyer.FullName fullName = buyer.getFullName();
            if (fullName.getLastName() != null && fullName.getFirstName() != null) {
                owner.withHolder(fullName.getLastName() + " " + fullName.getFirstName());
            } else if (fullName.getLastName() != null) {
                owner.withHolder(fullName.getLastName());
            } else if (fullName.getFirstName() != null) {
                owner.withHolder(fullName.getFirstName());
            }else {
                owner.withHolder("");
            }
        }
        owner.withIban(response.getTransaction().getConsumerIBAN());
        owner.withBic(response.getTransaction().getConsumerBIC());
        owner.withAccountNumber("");
        owner.withBankCode("");
        owner.withBankName("");
        owner.withCountryCode("");
        return new BankTransfer(owner.build(), null);
    }
}
