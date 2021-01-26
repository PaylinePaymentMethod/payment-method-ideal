package com.payline.payment.ideal.service;

import com.payline.payment.ideal.bean.Merchant;
import com.payline.payment.ideal.bean.Transaction;
import com.payline.payment.ideal.bean.request.IdealStatusRequest;
import com.payline.payment.ideal.utils.PluginUtils;
import com.payline.payment.ideal.utils.constant.ContractConfigurationKeys;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;

public class IdealStatusRequestService {

    // --- Singleton Holder pattern + initialization BEGIN
    private IdealStatusRequestService() {

    }

    private static class Holder {
        private static final IdealStatusRequestService INSTANCE = new IdealStatusRequestService();
    }

    public static IdealStatusRequestService getInstance() {
        return IdealStatusRequestService.Holder.INSTANCE;
    }
    // --- Singleton Holder pattern + initialization END

    public IdealStatusRequest buildIdealStatusRequest(RedirectionPaymentRequest request) {
        String subId = request.getContractConfiguration().getProperty(ContractConfigurationKeys.MERCHANT_SUBID_KEY).getValue();
        if (PluginUtils.isEmpty(subId)) subId = "0";

        Merchant merchant = new Merchant(
                request.getContractConfiguration().getProperty(ContractConfigurationKeys.MERCHANT_ID_KEY).getValue()
                , subId);

        // in RedirectionPaymentRequests, field 'transactionId' is the partner transactionId
        Transaction transaction = Transaction.builder()
                .transactionId(request.getTransactionId())
                .build();

        return new IdealStatusRequest(merchant,transaction);
    }

    public IdealStatusRequest buildIdealStatusRequest(TransactionStatusRequest request) {

        String subId = request.getContractConfiguration().getProperty(ContractConfigurationKeys.MERCHANT_SUBID_KEY).getValue();
        if (PluginUtils.isEmpty(subId)) subId = "0";

        Merchant merchant = new Merchant(
                request.getContractConfiguration().getProperty(ContractConfigurationKeys.MERCHANT_ID_KEY).getValue()
                , subId);

        // in TransactionStatusRequest, field 'transactionId' is the partner transactionId
        Transaction transaction = Transaction.builder()
                .transactionId(request.getTransactionId())
                .build();

        return new IdealStatusRequest(merchant,transaction);
    }
}
