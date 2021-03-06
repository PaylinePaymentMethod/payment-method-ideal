package com.payline.payment.ideal.service.impl;

import com.payline.payment.ideal.bean.Country;
import com.payline.payment.ideal.bean.Directory;
import com.payline.payment.ideal.bean.Issuer;
import com.payline.payment.ideal.exception.InvalidDataException;
import com.payline.payment.ideal.exception.PluginException;
import com.payline.payment.ideal.service.LogoPaymentFormConfigurationService;
import com.payline.payment.ideal.utils.PluginUtils;
import com.payline.payment.ideal.utils.XMLUtils;
import com.payline.payment.ideal.utils.constant.FormConfigurationKeys;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.paymentform.bean.field.PaymentFormField;
import com.payline.pmapi.bean.paymentform.bean.field.PaymentFormInputFieldSelect;
import com.payline.pmapi.bean.paymentform.bean.field.SelectOption;
import com.payline.pmapi.bean.paymentform.bean.form.BankTransferForm;
import com.payline.pmapi.bean.paymentform.bean.form.CustomForm;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseFailure;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class PaymentFormConfigurationServiceImpl extends LogoPaymentFormConfigurationService {
    private XMLUtils xmlUtils = XMLUtils.getInstance();

    @Override
    public PaymentFormConfigurationResponse getPaymentFormConfiguration(PaymentFormConfigurationRequest request) {
        try {

            // Champ de selection de banque
            final PaymentFormInputFieldSelect selectField = PaymentFormInputFieldSelect.PaymentFormFieldSelectBuilder.aPaymentFormInputFieldSelect()
                    .withSelectOptions(getOptionFromPluginConfiguration(request.getPluginConfiguration()))
                    .withIsFilterable(true)
                    .withKey(BankTransferForm.BANK_KEY)
                    .withLabel(i18n.getMessage(FormConfigurationKeys.FORM_BUTTON_IDEAL_LABEL, request.getLocale()))
                    .withPlaceholder(i18n.getMessage(FormConfigurationKeys.FORM_BUTTON_IDEAL_PLACEHOLDER, request.getLocale()))
                    .withRequiredErrorMessage(i18n.getMessage(FormConfigurationKeys.FORM_BUTTON_IDEAL_ERROR_MSG, request.getLocale()))
                    .withValidationErrorMessage(i18n.getMessage(FormConfigurationKeys.FORM_BUTTON_IDEAL_ERROR_MSG, request.getLocale()))
                    .withRequired(true)
                    .build();

            final List<PaymentFormField> paymentFormFields = new ArrayList<>();
            paymentFormFields.add(selectField);

            final CustomForm customForm = CustomForm.builder()
                    .withDisplayButton(true)
                    .withDescription("")
                    .withButtonText(i18n.getMessage(FormConfigurationKeys.FORM_BUTTON_IDEAL_TEXT, request.getLocale()))
                    .withCustomFields(paymentFormFields)
                    .build();

            return PaymentFormConfigurationResponseSpecific.PaymentFormConfigurationResponseSpecificBuilder
                    .aPaymentFormConfigurationResponseSpecific()
                    .withPaymentForm(customForm)
                    .build();
        } catch (PluginException e) {
            return e.toPaymentFormConfigurationResponseFailureBuilder()
                    .build();

        } catch (RuntimeException e) {
            log.error("Unexpected plugin error", e);
            return PaymentFormConfigurationResponseFailure.PaymentFormConfigurationResponseFailureBuilder
                    .aPaymentFormConfigurationResponseFailure()
                    .withErrorCode(PluginException.runtimeErrorCode(e))
                    .withFailureCause(FailureCause.INTERNAL_ERROR)
                    .build();
        }
    }

    List<SelectOption> getOptionFromPluginConfiguration(String configuration) {
        if (configuration == null || PluginUtils.isEmpty(configuration)) {
            throw new InvalidDataException("plugin configuration can't be empty");

        }

        List<SelectOption> options = new ArrayList<>();

        Directory directory = xmlUtils.fromXML(configuration, Directory.class);
        if (directory == null || directory.getCountries() == null) {
            throw new InvalidDataException("Unable to parse plugin configuration");

        }

        for (Country country : directory.getCountries()) {

            if (country != null && country.getIssuers() != null) {
                for (Issuer issuer : country.getIssuers()) {
                    SelectOption option = SelectOption.SelectOptionBuilder
                            .aSelectOption()
                            .withKey(issuer.getIssuerId())
                            .withValue(issuer.getIssuerName())
                            .build();

                    options.add(option);
                }
            }
        }

        if (options.isEmpty()) {
            throw new InvalidDataException("Issuer list can't be empty");
        }

        return options;
    }
}