package com.payline.payment.ideal.bean.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.payline.payment.ideal.bean.IdealBean;
import com.payline.payment.ideal.bean.Merchant;
import com.payline.payment.ideal.utils.PluginUtils;
import com.payline.payment.ideal.utils.constant.ContractConfigurationKeys;
import com.payline.payment.ideal.utils.constant.IdealConstant;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractConfiguration;

@JacksonXmlRootElement(localName = "DirectoryReq", namespace = IdealConstant.IDEAL_NAMESPACE)
public class IdealDirectoryRequest extends IdealBean {

    @JacksonXmlProperty(localName = "Merchant", namespace=IdealConstant.IDEAL_NAMESPACE)
    private Merchant merchant;

    public IdealDirectoryRequest() {
    }

    public IdealDirectoryRequest(ContractParametersCheckRequest request) {
        super();

        String subId = request.getAccountInfo().get(ContractConfigurationKeys.MERCHANT_SUBID_KEY);
        if (PluginUtils.isEmpty(subId)) subId = "0";

        this.merchant = new Merchant(request.getAccountInfo().get(ContractConfigurationKeys.MERCHANT_ID_KEY)
                , subId);

    }

    public IdealDirectoryRequest(ContractConfiguration configuration) {
        super();


        String subId = configuration.getProperty(ContractConfigurationKeys.MERCHANT_SUBID_KEY).getValue();
        if (PluginUtils.isEmpty(subId)) subId = "0";

        this.merchant = new Merchant(configuration.getProperty(ContractConfigurationKeys.MERCHANT_ID_KEY).getValue()
                , subId);
    }

    public Merchant getMerchant() {
        return merchant;
    }
}
