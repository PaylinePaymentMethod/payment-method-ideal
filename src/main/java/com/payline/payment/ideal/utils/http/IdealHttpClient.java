package com.payline.payment.ideal.utils.http;

import com.payline.payment.ideal.bean.IdealBean;
import com.payline.payment.ideal.bean.PartnerAcquirer;
import com.payline.payment.ideal.bean.request.IdealDirectoryRequest;
import com.payline.payment.ideal.bean.request.IdealPaymentRequest;
import com.payline.payment.ideal.bean.request.IdealStatusRequest;
import com.payline.payment.ideal.bean.response.IdealDirectoryResponse;
import com.payline.payment.ideal.bean.response.IdealPaymentResponse;
import com.payline.payment.ideal.bean.response.IdealStatusResponse;
import com.payline.payment.ideal.exception.PluginException;
import com.payline.payment.ideal.service.IdealPaymentRequestService;
import com.payline.payment.ideal.service.IdealStatusRequestService;
import com.payline.payment.ideal.utils.XMLUtils;
import com.payline.payment.ideal.utils.constant.PartnerConfigurationKeys;
import com.payline.payment.ideal.utils.security.SignatureUtils;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.configuration.request.RetrievePluginConfigurationRequest;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

import java.nio.charset.Charset;
import java.security.PrivateKey;
import java.security.PublicKey;
@Log4j2
public class IdealHttpClient extends AbstractHttpClient {

    // headers data
    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String CONTENT_TYPE = "application/xml";

    private XMLUtils xmlUtils;
    private SignatureUtils signatureUtils;
    private IdealPaymentRequestService idealPaymentRequestService;
    private IdealStatusRequestService idealStatusRequestService;

    private IdealHttpClient() {
        xmlUtils = XMLUtils.getInstance();
        signatureUtils = SignatureUtils.getInstance();
        idealPaymentRequestService =  IdealPaymentRequestService.getInstance();
        idealStatusRequestService =  IdealStatusRequestService.getInstance();

    }


    /**
     * Singleton Holder
     */
    private static class SingletonHolder {
        private static final IdealHttpClient INSTANCE = new IdealHttpClient();
    }

    /**
     * @return the singleton instance
     */
    public static IdealHttpClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Create header needed by the API
     *
     * @return a list of headers
     */
    private Header[] createHeaders() {
        Header[] headers = new Header[1];
        headers[0] = new BasicHeader(CONTENT_TYPE_KEY, CONTENT_TYPE);
        return headers;
    }

    /**
     * Create the request body needed by the API
     *
     * @param request       the Object to create the body from
     * @param partnerAcquirer contain all data needed to sign the document
     * @return an XML body signed
     */
    String createBody(final IdealBean request, final PartnerAcquirer partnerAcquirer) {
        String xmlBody = xmlUtils.toXml(request);
        PublicKey publicKey = signatureUtils.getPublicKeyFromString(partnerAcquirer.getPublicKey());
        String publicKeyId = partnerAcquirer.getPublicKeyId();
        PrivateKey privateKey = signatureUtils.getPrivateKeyFromString(partnerAcquirer.getPrivateKey());
        return signatureUtils.signXML(xmlBody, publicKey, publicKeyId, privateKey);
    }

    /**
     * Create the request body needed by the API
     *
     * @param request       the Object to create the body from
     * @param configuration contain all data needed to sign the document
     * @return an XML body signed
     */
    String createBody(IdealBean request, PartnerConfiguration configuration) {
        String xmlBody = xmlUtils.toXml(request);
        PublicKey publicKey = signatureUtils.getPublicKeyFromString(configuration.getProperty(PartnerConfigurationKeys.PUBLIC_KEY));
        String publicKeyId = configuration.getProperty(PartnerConfigurationKeys.PUBLIC_KEY_ID);
        PrivateKey privateKey = signatureUtils.getPrivateKeyFromString(configuration.getProperty(PartnerConfigurationKeys.PRIVATE_KEY));
        return signatureUtils.signXML(xmlBody, publicKey, publicKeyId, privateKey);
    }

    /**
     * Extract info in ContractParametersCheckRequest to call directoryRequest
     *
     * @param request
     * @return the response of the directoryRequestHttp call
     */
    public IdealDirectoryResponse directoryRequest(ContractParametersCheckRequest request) {
        IdealDirectoryRequest directoryRequest = new IdealDirectoryRequest(request);
        return this.directoryRequest(directoryRequest, request.getPartnerConfiguration());
    }

    /**
     * Extract info in RetrievePluginConfigurationRequest to call directoryRequest
     *
     * @param request
     * @return the response of the directoryRequestHttp call
     */
    public IdealDirectoryResponse directoryRequest(RetrievePluginConfigurationRequest request, PartnerAcquirer partnerAcquirer) {
        IdealDirectoryRequest directoryRequest = new IdealDirectoryRequest(request.getContractConfiguration());
        return this.directoryRequest(directoryRequest, partnerAcquirer);
    }

    /**
     * Prepare then call the Directory Request API
     *
     * @param directoryRequest
     * @param partnerAcquirer
     * @return
     */
    private IdealDirectoryResponse directoryRequest(final IdealDirectoryRequest directoryRequest, final PartnerAcquirer partnerAcquirer) {
        // get url
        String url = partnerAcquirer.getUrl();

        // create headers
        Header[] headers = createHeaders();

        // create body
        String signedXmlBody = this.createBody(directoryRequest, partnerAcquirer);

        // do the call
        StringResponse response = super.doPost(url, "", headers, new StringEntity(signedXmlBody, Charset.defaultCharset()));

        // check the response status
        checkResponse(response);

        // check the response signature
        PublicKey idealPublicKey = signatureUtils.getPublicKeyFromString(partnerAcquirer.getIdealPublicKey());
        signatureUtils.verifySignatureXML(response.getContent(), idealPublicKey);
        return xmlUtils.fromXML(response.getContent(), IdealDirectoryResponse.class);
    }

    /**
     * Prepare then call the Directory Request API
     *
     * @param directoryRequest
     * @param configuration
     * @return
     */
    private IdealDirectoryResponse directoryRequest(IdealDirectoryRequest directoryRequest, PartnerConfiguration configuration) {
        // get url
        String url = configuration.getProperty(PartnerConfigurationKeys.URL_ABNAMRO);

        // create headers
        Header[] headers = createHeaders();

        // create body
        String signedXmlBody = this.createBody(directoryRequest, configuration);

        // do the call
        StringResponse response = super.doPost(url, "", headers, new StringEntity(signedXmlBody, Charset.defaultCharset()));

        // check the response status
        checkResponse(response);

        // check the response signature
        PublicKey idealPublicKey = signatureUtils.getPublicKeyFromString(configuration.getProperty(PartnerConfigurationKeys.IDEAL_PUBLIC));
        signatureUtils.verifySignatureXML(response.getContent(), idealPublicKey);
        return xmlUtils.fromXML(response.getContent(), IdealDirectoryResponse.class);
    }

    /**
     * Prepare then call the Transaction Request API
     *
     * @param request
     * @return
     */
    public IdealPaymentResponse transactionRequest(PaymentRequest request) {

        // get url
        String url = request.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.URL_ABNAMRO);

        // create headers
        Header[] headers = createHeaders();

        // create body

        IdealPaymentRequest paymentRequest = idealPaymentRequestService.buildIdealPaymentRequest(request);
        String signedXmlBody = this.createBody(paymentRequest, request.getPartnerConfiguration());

        // do the call
        StringResponse response = super.doPost(url, "", headers, new StringEntity(signedXmlBody, Charset.defaultCharset()));

        // check the response status
        checkResponse(response);

        // check the response signature
        PublicKey idealPublicKey = signatureUtils.getPublicKeyFromString(request.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.IDEAL_PUBLIC));
        signatureUtils.verifySignatureXML(response.getContent(), idealPublicKey);

        // return an Ideal response object
        return xmlUtils.fromXML(response.getContent(), IdealPaymentResponse.class);
    }

    /**
     * Extract info in RedirectionPaymentRequest to call statusRequest
     *
     * @param request
     * @return
     */
    public IdealStatusResponse statusRequest(RedirectionPaymentRequest request) {

        IdealStatusRequest statusRequest = idealStatusRequestService.buildIdealStatusRequest(request);
        return this.statusRequest(statusRequest, request.getPartnerConfiguration());
    }

    /**
     * Extract info in TransactionStatusRequest to call statusRequest
     *
     * @param request
     * @return
     */
    public IdealStatusResponse statusRequest(TransactionStatusRequest request) {
        IdealStatusRequest statusRequest = idealStatusRequestService.buildIdealStatusRequest(request);
        return this.statusRequest(statusRequest, request.getPartnerConfiguration());
    }

    /**
     * Prepare then call the Status Request API
     *
     * @param statusRequest
     * @param configuration
     * @return
     */
    private IdealStatusResponse statusRequest(IdealStatusRequest statusRequest, PartnerConfiguration configuration) {
        // get url
        String url = configuration.getProperty(PartnerConfigurationKeys.URL_ABNAMRO);

        // create headers
        Header[] headers = createHeaders();

        // create body
        String signedXmlBody = this.createBody(statusRequest, configuration);

        // do the call
        StringResponse response = super.doPost(url, "", headers, new StringEntity(signedXmlBody, Charset.defaultCharset()));

        // check the response status
        checkResponse(response);

        // check the response signature
        PublicKey idealPublicKey = signatureUtils.getPublicKeyFromString(configuration.getProperty(PartnerConfigurationKeys.IDEAL_PUBLIC));
        signatureUtils.verifySignatureXML(response.getContent(), idealPublicKey);

        // return an Ideal response object
        return xmlUtils.fromXML(response.getContent(), IdealStatusResponse.class);
    }


    /**
     * Check the status response and throws an {@link com.payline.payment.ideal.exception.HttpCallException} if the response is not OK
     *
     * @param response
     */
    void checkResponse(StringResponse response) {
        if (!response.isSuccess()) {
            log.error("Bad response status: {}", response.getStatusCode());

            // Extract error code and message from the error response
            String message = "partner error: " + response.getStatusCode() + " " + response.getStatusMessage();
            int errorCode = response.getStatusCode();
            if (response.getContent() != null) {
                message = response.getContent();
                errorCode = response.getStatusCode();
            }

            // Mapping between partner error codes and Payline failure causes
            FailureCause failureCause = FailureCause.PARTNER_UNKNOWN_ERROR;
            if (errorCode >= 400 && errorCode < 500) {
                failureCause = FailureCause.INVALID_DATA;
            }

            throw new PluginException(message, failureCause);
        }
    }
}