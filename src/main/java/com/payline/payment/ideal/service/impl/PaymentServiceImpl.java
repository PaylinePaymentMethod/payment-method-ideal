package com.payline.payment.ideal.service.impl;

import com.payline.payment.ideal.bean.response.IdealPaymentResponse;
import com.payline.payment.ideal.exception.PluginException;
import com.payline.payment.ideal.utils.PluginUtils;
import com.payline.payment.ideal.utils.constant.IdealConstant;
import com.payline.payment.ideal.utils.constant.RequestContextKeys;
import com.payline.payment.ideal.utils.http.IdealHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.RequestContext;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import com.payline.pmapi.service.PaymentService;
import lombok.extern.log4j.Log4j2;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class PaymentServiceImpl implements PaymentService {

    private IdealHttpClient client = IdealHttpClient.getInstance();

    @Override
    public PaymentResponse paymentRequest(PaymentRequest paymentRequest) {
        String partnerTransactionId = IdealConstant.UNKNOWN;

        try {
            IdealPaymentResponse response = client.transactionRequest(paymentRequest);

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

                PaymentResponseRedirect.RedirectionRequest redirectionRequest = PaymentResponseRedirect.RedirectionRequest.RedirectionRequestBuilder
                        .aRedirectionRequest()
                        .withUrl(new URL(response.getIssuer().getIssuerAuthenticationURL()))
                        .withRequestType(PaymentResponseRedirect.RedirectionRequest.RequestType.GET)
                        .build();

                // request context
                //On ajoute le partnerTransactionId à la requête afin de pouvoir
                //retrouver la transaction après la redirection.
                final Map<String, String> requestData = new HashMap<>();
                requestData.put(RequestContextKeys.PARTNER_TRANSACTION_ID, partnerTransactionId);

                final RequestContext requestContext = RequestContext.RequestContextBuilder.aRequestContext()
                        .withRequestData(requestData)
                        .withSensitiveRequestData(new HashMap<>())
                        .build();

                return PaymentResponseRedirect.PaymentResponseRedirectBuilder
                        .aPaymentResponseRedirect()
                        .withRedirectionRequest(redirectionRequest)
                        .withPartnerTransactionId(partnerTransactionId)
                        .withStatusCode(IdealConstant.STATUS_OK)
                        .withRequestContext(requestContext)
                        .build();

            }
        } catch (MalformedURLException e) {
            log.error("received URL isn't well formed", e);
            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withErrorCode(e.getMessage())
                    .withFailureCause(FailureCause.INVALID_DATA)
                    .build();
        } catch (PluginException e) {
            log.error("Unable to perform the payment request", e);
            return e.toPaymentResponseFailureBuilder()
                    .withPartnerTransactionId(partnerTransactionId)
                    .build();

        } catch (RuntimeException e) {
            log.error("Unexpected runtime error", e);
            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withErrorCode(PluginException.runtimeErrorCode(e))
                    .withFailureCause(FailureCause.INTERNAL_ERROR)
                    .build();
        }
    }


}
