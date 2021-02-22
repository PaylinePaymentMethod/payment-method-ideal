package com.payline.payment.ideal.service;

import com.payline.payment.ideal.bean.Issuer;
import com.payline.payment.ideal.bean.Merchant;
import com.payline.payment.ideal.bean.Transaction;
import com.payline.payment.ideal.bean.request.IdealPaymentRequest;
import com.payline.payment.ideal.utils.PluginUtils;
import com.payline.payment.ideal.utils.constant.ContractConfigurationKeys;
import com.payline.payment.ideal.utils.constant.FormConfigurationKeys;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.request.PaymentRequest;

public class IdealPaymentRequestService {

    private static final String EXPIRATION_PERIOD = "PT15M";

    // --- Singleton Holder pattern + initialization BEGIN
    private IdealPaymentRequestService() {

    }

    private static class Holder {
        private static final IdealPaymentRequestService INSTANCE = new IdealPaymentRequestService();
    }

    public static IdealPaymentRequestService getInstance() {
        return IdealPaymentRequestService.Holder.INSTANCE;
    }
    // --- Singleton Holder pattern + initialization END

    public IdealPaymentRequest buildIdealPaymentRequest(PaymentRequest request) {

        // prepare needed data
        Amount amount = request.getAmount();
        ContractConfiguration configuration = request.getContractConfiguration();

        Issuer issuer = Issuer.builder()
                .issuerId(request.getPaymentFormContext().getPaymentFormParameter().get(FormConfigurationKeys.BANK))
                .build();

        String subId = configuration.getProperty(ContractConfigurationKeys.MERCHANT_SUBID_KEY).getValue();
        if (PluginUtils.isEmpty(subId)) subId = "0";

        Merchant merchant = new Merchant(configuration.getProperty(ContractConfigurationKeys.MERCHANT_ID_KEY).getValue()
                , subId
                , request.getEnvironment().getRedirectionReturnURL());

        Transaction transaction = Transaction.builder()
                .purchaseId(request.getOrder().getReference())
                .amount(PluginUtils.createStringAmount(amount.getAmountInSmallestUnit(), amount.getCurrency()))
                .currency(amount.getCurrency().getCurrencyCode())
                .expirationPeriod(EXPIRATION_PERIOD)
                .language(request.getLocale().getLanguage())
                .description("description1")
                .entranceCode("D67tyx6rw9IhY71")
                .build();
        return new IdealPaymentRequest(issuer, merchant, transaction);
    }

}
