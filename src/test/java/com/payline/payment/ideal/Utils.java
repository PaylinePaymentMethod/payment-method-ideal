package com.payline.payment.ideal;

import com.payline.payment.ideal.utils.constant.ContractConfigurationKeys;
import com.payline.payment.ideal.utils.constant.FormConfigurationKeys;
import com.payline.payment.ideal.utils.constant.PartnerConfigurationKeys;
import com.payline.payment.ideal.utils.constant.RequestContextKeys;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.*;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;

import java.math.BigInteger;
import java.util.*;


public class Utils {
    private static final Locale FRENCH = Locale.FRENCH;
    private static final String EUR = "EUR";
    public static final String SUCCESS_URL = "https://succesurl.com/";
    public static final String FAILURE_URL = "http://cancelurl.com/";
    public static final String NOTIFICATION_URL = "http://notificationurl.com/";


    private static final String URL = "https://abnamro-test.ideal-payment.de/ideal/iDealv3";
    private static final String MERCHANT_ID = "123";

    public static final String PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n" +
            "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDxpk8erMd4jNY+" +
            "ULRPpM+x4R81MFt4rPWILrR4PFVNHHZ9X0iGck5CirUq/v0gdXS3rvU3nVfr/Ii4" +
            "oYnv3+VISsxpWF1HjtY5wwh9zr/kDS6G6mbaVDxthDTh7AS1tnkHHskvAytgfFtS" +
            "9MxiIarZUPY74vSKfwy2w+HMKMpIMhqQdJPeIy2ftY8sEEMD5wdrZ0LSQVcXNTWq" +
            "EMLG3RjozmJbUvAMFl7/NV0NWJZzJCjXoWpfAA3y8Ee29AnWYq3eLDyHV8YF5ZZQ" +
            "NiXzzAI68wqFbPe+o45z8+tKAReBR0VtmZa2sX3IX3ec/5sJKZ9mB1T5oQDMasP4" +
            "ocRoN88rAgMBAAECggEBAI4lbU83BZNFDVXUtWxQH5icO3ZXPsdqvnfgOWqs2uSk" +
            "RJMVNJ1Zqe18mHt1SUMJtKHEOhz/4lM/1tD8vR4sjzwKO9oQD7bjL+MEdf9DWh91" +
            "HZRhWCU//dfSOCVZZ5/YebMVifSsoZx9Kl9O/tcOxOhWtrGdnInHmJv999nXeL4k" +
            "DlAgr4FTFUsPD+NFGAZVoBHW70aPu84XaH2UM0ipCeEseqqcIPsB2yvYVXtLbHBX" +
            "2zIJUBGs8kZ+wdzSqBzK4hGZlkd5HVeczH8AKjbhGwIG1OBLkDcc1s/m39T9zN5D" +
            "S9NWRnXrhkIIIi1em2PSGu2s5wYahsIqMzTWTi31r7ECgYEA/UV0sbeq5rl4ARmQ" +
            "ntct6DCZShNGuuN9iyu9ToGT7nCBorKmYcdAY3PMxRD2218F0yLGWzt0LtNXsijB" +
            "6yE8eM+wqVvaPbrYP2t9uacYw7dBPOJqkafJlTP8aOAYCeHiE2UjvhwB5YxDFj61" +
            "vwrNqMtIUl835ozv2s5XS7vukikCgYEA9EDMtrbfFPfV5yV9nuEcBhJ7rKzX8ax7" +
            "LF6XxDeap7qyQdpd3i4vn0vEzxQQBvSB62sg/yscH+mRefd8JjcQqNnx51oO1ejn" +
            "XjZw+dRWh49FJP/BTEysO+cf/EFnygPF12shtHpz6H7YiExtRmfXVUspA+yyV5UJ" +
            "jCTh0O5LSTMCgYBgmi5hXsHD0TgxizO7Mi3jYy4EsAeJXx3SiHNjT09CYg1AJk4J" +
            "+3rWtCOFguv1TnAlUR1BTRMKjTfkz2DvszSii+1BG7TJWMwEnJZOyqpKdEpg06d8" +
            "OPhNfY+n3NmuY0bcyPXyHDKpAG/SO0cNQCyjVi4WpRToThdqaMupKlxbyQKBgGTY" +
            "BC1D32LW2Dew4Oah5mITa4BldFrRbaFqBJr8ohuyFzrdH3hF9V99dupQTDWy6Zj7" +
            "CAqSD/CVDH0g0t8sSPKN2TQ9mHZ0zGG3dHmRU5BwdInMFlCcL1gkGq6ZinJ7kEla" +
            "b/YFwKkzBc9wToWNBfivKWX3acKDRAfaimkqmWbFAoGADjT6nX/3Sf72Or4VBkLS" +
            "UCcix8t+F1uzNlJeGpwLu8cft/a7Kwoy0HwjKWxfJGPiIX00GYnwGbGjVX9E/T5k" +
            "/NudozvST8Ufx7Y/gLZTnLm85p+C5XXp7/PyM8ejvYIrz6kTI5iw+Isl4zLRAGCl" +
            "dLteIAeOuyOwgxdmuSFyGjY=" +
            "-----END PRIVATE KEY-----";

    public static final String PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA8aZPHqzHeIzWPlC0T6TP" +
            "seEfNTBbeKz1iC60eDxVTRx2fV9IhnJOQoq1Kv79IHV0t671N51X6/yIuKGJ79/l" +
            "SErMaVhdR47WOcMIfc6/5A0uhupm2lQ8bYQ04ewEtbZ5Bx7JLwMrYHxbUvTMYiGq" +
            "2VD2O+L0in8MtsPhzCjKSDIakHST3iMtn7WPLBBDA+cHa2dC0kFXFzU1qhDCxt0Y" +
            "6M5iW1LwDBZe/zVdDViWcyQo16FqXwAN8vBHtvQJ1mKt3iw8h1fGBeWWUDYl88wC" +
            "OvMKhWz3vqOOc/PrSgEXgUdFbZmWtrF9yF93nP+bCSmfZgdU+aEAzGrD+KHEaDfP" +
            "KwIDAQAB" +
            "-----END PUBLIC KEY-----";

    public static final String IDEAL_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp7VvkaJ/R/TZJVVUk1N5" +
            "RcIdr5O1aOGV0dxfEQ1/UatF3iUatPuEBtYg19kSFc3T/kzQgrAHohgEG9Ro5JJg" +
            "JpHOu7A4DFmJvfPYeQxrUUBfux6O8Va1tGin/tMvZQUJgTJm+tc9HRXHzg3dz514" +
            "rgcXD/iA5d70cQLHZY3dOYIqHlE3kwbzpAFBAHfrpfXGWJPjiXf8Han6m8+eSv8s" +
            "iUOMesN4gXBK3r+yqHsh/VoXfwc09iHP89CEojhH3EiI9WmgInXF6E7cFYuXV6gy" +
            "s9tZNaLK8uifM39ygjbjaRWKiBdtLfDy61Z1WMEtm84gOg+8oU68EiUYMNeusjLO" +
            "+wIDAQAB" +
            "-----END PUBLIC KEY-----";

    public static final String directoryResponseOK = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<DirectoryRes xmlns=\"http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1\" version=\"3.3.1\">" +
            "   <createDateTimestamp>2008-11-14T09:30:47.0Z</createDateTimestamp>" +
            "   <Acquirer>" +
            "      <acquirerID>0001</acquirerID>" +
            "   </Acquirer>" +
            "   <Directory>" +
            "      <directoryDateTimestamp>2004-11-10T10:15:12.145Z</directoryDateTimestamp>" +
            "      <Country>" +
            "         <countryNames>Nederland</countryNames>" +
            "         <Issuer>" +
            "            <issuerID>ABNANL2AXXX</issuerID>" +
            "            <issuerName>ABN AMRO Bank</issuerName>" +
            "         </Issuer>" +
            "         <Issuer>" +
            "            <issuerID>FRBKNL2LXXX</issuerID>" +
            "            <issuerName>Friesland Bank</issuerName>" +
            "         </Issuer>" +
            "         <Issuer>" +
            "            <issuerID>INGBNL2AXXX</issuerID>" +
            "            <issuerName>ING</issuerName>" +
            "         </Issuer>" +
            "      </Country>" +
            "      <Country>" +
            "         <countryNames>België/Belgique</countryNames>" +
            "         <Issuer>" +
            "            <issuerID>KREDBE22XXX</issuerID>" +
            "            <issuerName>KBC</issuerName>" +
            "         </Issuer>" +
            "      </Country>" +
            "   </Directory>" +
            "   <Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">" +
            "      <SignedInfo>" +
            "         <CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" />" +
            "         <SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\" />" +
            "         <Reference URI=\"\">" +
            "            <Transforms>" +
            "               <Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\" />" +
            "            </Transforms>" +
            "            <DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\" />" +
            "            <DigestValue>0R00+jQyZlSLrTA+1gSmsH9vtbzbNbIIoWzh9SDJXGc=</DigestValue>" +
            "         </Reference>" +
            "      </SignedInfo>" +
            "      <SignatureValue>H5FBG+1ceWVgaQ[...]/mTUv5xEedkVhTZGuQ2aiUeAec=</SignatureValue>" +
            "      <KeyInfo>" +
            "         <KeyName>7D665C81A[...]04D276F07BF2</KeyName>" +
            "      </KeyInfo>" +
            "   </Signature>" +
            "</DirectoryRes>";

    public static final String errorResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<AcquirerErrorRes xmlns=\"http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1\" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\" version=\"3.3.1\">" +
            "   <createDateTimestamp>2019-07-22T12:44:23.674Z</createDateTimestamp>" +
            "   <Error>" +
            "      <errorCode>AP1100</errorCode>" +
            "      <errorMessage>MerchantID unknown</errorMessage>" +
            "      <errorDetail>Field generating error: merchantID</errorDetail>" +
            "      <suggestedAction>Please try again later or pay using another payment method.</suggestedAction>" +
            "      <consumerMessage>Betalen met iDEAL is nu niet mogelijk. Probeer het later nogmaals of betaal op een andere manier.</consumerMessage>" +
            "   </Error>" +
            "   <Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">" +
            "      <SignedInfo>" +
            "         <CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" />" +
            "         <SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\" />" +
            "         <Reference URI=\"\">" +
            "            <Transforms>" +
            "               <Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\" />" +
            "            </Transforms>" +
            "            <DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\" />" +
            "            <DigestValue>qxfkhFB1u1a1Kk6iwXKT5zVIWsH5lGbC73wZay0zw1U=</DigestValue>" +
            "         </Reference>" +
            "      </SignedInfo>" +
            "      <SignatureValue>dS5kpS44Ru1rh[...]xaKCSe7Q/ZHADq4sbfQ==</SignatureValue>" +
            "      <KeyInfo>" +
            "         <KeyName>E202419EEB02DDBD458176AE486B31A88C8891DB</KeyName>" +
            "      </KeyInfo>" +
            "   </Signature>" +
            "</AcquirerErrorRes>";

    public static final String TransactionResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<AcquirerTrxRes xmlns=\"http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1\" version=\"3.3.1\">\n" +
            "   <createDateTimestamp>2008-11-14T09:30:47.0Z</createDateTimestamp>\n" +
            "   <Acquirer>\n" +
            "      <acquirerID>0001</acquirerID>\n" +
            "   </Acquirer>\n" +
            "   <Issuer>\n" +
            "      <issuerAuthenticationURL>https://www.issuingbank.eu/ideal</issuerAuthenticationURL>\n" +
            "   </Issuer>\n" +
            "   <Transaction>\n" +
            "      <transactionID>0001000000000001</transactionID>\n" +
            "      <transactionCreateDateTimestamp>2008-11- 14T09:30:50.125Z</transactionCreateDateTimestamp>\n" +
            "      <purchaseID>iDEAL21</purchaseID>\n" +
            "   </Transaction>\n" +
            "   <Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">\n" +
            "      <SignedInfo>\n" +
            "         <CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" />\n" +
            "         <SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\" />\n" +
            "         <Reference URI=\"\">\n" +
            "            <Transforms>\n" +
            "               <Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\" />\n" +
            "            </Transforms>\n" +
            "            <DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\" />\n" +
            "            <DigestValue>fU+UQd8JBswjQruM4MOLau8fmFrcfXaZ/kwufu45JUw=</DigestValue>\n" +
            "         </Reference>\n" +
            "      </SignedInfo>\n" +
            "      <SignatureValue>U/gCgvwtFOrE[...]qUI6g2XCeczC5xo+Gg4eA+L0=</SignatureValue>\n" +
            "      <KeyInfo>\n" +
            "         <KeyName>7D665C81ABBE1A7D0E525BFC171F04D276F07BF2</KeyName>\n" +
            "      </KeyInfo>\n" +
            "   </Signature>\n" +
            "</AcquirerTrxRes>\n";

    public static final String statusResponseOK = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<AcquirerStatusRes xmlns=\"http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1\" version=\"3.3.1\">\n"+
            "   <createDateTimestamp>2008-11-14T09:30:47.0Z</createDateTimestamp>\n"+
            "   <Acquirer>\n"+
            "      <acquirerID>0001</acquirerID>\n"+
            "   </Acquirer>\n"+
            "   <Transaction>\n"+
            "      <transactionID>0001000000000001</transactionID>\n"+
            "      <status>Success</status>\n"+
            "      <statusDateTimestamp>2008-11-14T09:32:47.0Z</statusDateTimestamp>\n"+
            "      <consumerName>Onderheuvel</consumerName>\n"+
            "      <consumerIBAN>NL44RABO0123456789</consumerIBAN>\n"+
            "      <consumerBIC>RABONL2U</consumerBIC>\n"+
            "      <amount>59.99</amount>\n"+
            "      <currency>EUR</currency>\n"+
            "   </Transaction>\n"+
            "   <Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">\n"+
            "      <SignedInfo>\n"+
            "         <CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" />\n"+
            "         <SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\" />\n"+
            "         <Reference URI=\"\">\n"+
            "            <Transforms>\n"+
            "               <Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\" />\n"+
            "            </Transforms>\n"+
            "            <DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\" />\n"+
            "            <DigestValue>P9rQXN4+XSNXe2XTKTlA5mOxAC5CzaFxpPD0VMxlO2s=</DigestValue>\n"+
            "         </Reference>\n"+
            "      </SignedInfo>\n"+
            "      <SignatureValue>Tdtj0eUdP[...]ln1+ZYL4HvhD7helRokM=</SignatureValue>\n"+
            "      <KeyInfo>\n"+
            "         <KeyName>7D665C81ABBE1A7D0E525BFC171F04D276F07BF2</KeyName>\n"+
            "      </KeyInfo>\n"+
            "   </Signature>\n"+
            "</AcquirerStatusRes>\n";
    public static ContractParametersCheckRequest createContractParametersCheckRequest() {
        Map<String, String> accountInfo = new HashMap<>();
        accountInfo.put(ContractConfigurationKeys.MERCHANT_ID_KEY, MERCHANT_ID);
        accountInfo.put(ContractConfigurationKeys.MERCHANT_SUBID_KEY, null);

        ContractConfiguration configuration = createContractConfiguration();

        return ContractParametersCheckRequest.CheckRequestBuilder.aCheckRequest()
                .withAccountInfo(accountInfo)
                .withLocale(FRENCH)
                .withContractConfiguration(configuration)
                .withEnvironment(createDefaultPaylineEnvironment())
                .withPartnerConfiguration(createDefaultPartnerConfiguration())
                .build();

    }

    public static PaymentRequest.Builder createCompletePaymentBuilder() {
        final Amount amount = createAmount(EUR);
        final ContractConfiguration contractConfiguration = createContractConfiguration();
        final Environment paylineEnvironment = new Environment(NOTIFICATION_URL, SUCCESS_URL, FAILURE_URL, true);
        final String transactionID = createTransactionId();
        final Order order = createOrder(transactionID);
        final String softDescriptor = "softDescriptor";
        final Locale locale = Locale.FRANCE;
        final Buyer buyer = createDefaultBuyer();

        return PaymentRequest.builder()
                .withPaymentFormContext(createPaymentFormContext())
                .withAmount(amount)
                .withBrowser(new Browser("", Locale.FRANCE))
                .withContractConfiguration(contractConfiguration)
                .withEnvironment(paylineEnvironment)
                .withOrder(order)
                .withLocale(locale)
                .withTransactionId(transactionID)
                .withSoftDescriptor(softDescriptor)
                .withBuyer(buyer)
                .withPartnerConfiguration(createDefaultPartnerConfiguration());
    }

    private static String createTransactionId() {
        return "transactionID" + Calendar.getInstance().getTimeInMillis();
    }

    private static Map<Buyer.AddressType, Buyer.Address> createAddresses(Buyer.Address address) {
        Map<Buyer.AddressType, Buyer.Address> addresses = new HashMap<>();
        addresses.put(Buyer.AddressType.DELIVERY, address);
        addresses.put(Buyer.AddressType.BILLING, address);

        return addresses;
    }

    private static Map<Buyer.AddressType, Buyer.Address> createDefaultAddresses() {
        Buyer.Address address = createDefaultAddress();
        return createAddresses(address);
    }

    private static Amount createAmount(String currency) {
        return new Amount(BigInteger.TEN, Currency.getInstance(currency));
    }

    private static Order createOrder(String transactionID) {
        return Order.OrderBuilder.anOrder().withReference(transactionID).build();
    }

    private static Buyer.FullName createFullName() {
        return new Buyer.FullName("foo", "bar", "UNKNOWN");
    }

    private static Map<Buyer.PhoneNumberType, String> createDefaultPhoneNumbers() {
        Map<Buyer.PhoneNumberType, String> phoneNumbers = new HashMap<>();
        phoneNumbers.put(Buyer.PhoneNumberType.BILLING, "0606060606");

        return phoneNumbers;
    }

    public static ContractConfiguration createContractConfiguration() {
        Map<String, ContractProperty> map = new HashMap<>();
        map.put(ContractConfigurationKeys.MERCHANT_ID_KEY, new ContractProperty(MERCHANT_ID));
        map.put(ContractConfigurationKeys.MERCHANT_SUBID_KEY, new ContractProperty(null));

        return new ContractConfiguration("iDEAL", map);
    }

    public static PaymentFormContext createPaymentFormContext(){
        Map<String, String> map = new HashMap<>();
        map.put(FormConfigurationKeys.BANK, "RABONL2UXXX");

        return PaymentFormContext.PaymentFormContextBuilder.aPaymentFormContext()
                .withPaymentFormParameter(map)
                .build();
    }

    private static Buyer.Address createAddress(String street, String city, String zip) {
        return Buyer.Address.AddressBuilder.anAddress()
                .withStreet1(street)
                .withCity(city)
                .withZipCode(zip)
                .withCountry("country")
                .build();
    }

    private static Buyer.Address createDefaultAddress() {
        return createAddress("a street", "a city", "a zip");
    }

    private static Buyer createBuyer(Map<Buyer.PhoneNumberType, String> phoneNumbers, Map<Buyer.AddressType, Buyer.Address> addresses, Buyer.FullName fullName) {
        return Buyer.BuyerBuilder.aBuyer()
                .withCustomerIdentifier("customerId")
                .withEmail("foo@bar.baz")
                .withPhoneNumbers(phoneNumbers)
                .withAddresses(addresses)
                .withFullName(fullName)
                .build();
    }

    private static Buyer createDefaultBuyer() {
        return createBuyer(createDefaultPhoneNumbers(), createDefaultAddresses(), createFullName());
    }

    public static Environment createDefaultPaylineEnvironment() {
        return new Environment("http://notificationURL.com", "http://redirectionURL.com", "http://redirectionCancelURL.com", true);
    }

    public static PartnerConfiguration createDefaultPartnerConfiguration() {
        Map<String, String> map = new HashMap<>();
        map.put(PartnerConfigurationKeys.URL_ABNAMRO, URL);
        map.put(PartnerConfigurationKeys.PUBLIC_KEY_ID, "686643AF86B9BC2F442992919092A7B3835990D4");
        Map<String, String> sensitiveMap = new HashMap<>();
        sensitiveMap.put(PartnerConfigurationKeys.PRIVATE_KEY, PRIVATE_KEY);
        sensitiveMap.put(PartnerConfigurationKeys.PUBLIC_KEY, PUBLIC_KEY);
        sensitiveMap.put(PartnerConfigurationKeys.IDEAL_PUBLIC, IDEAL_PUBLIC_KEY);
        return new PartnerConfiguration(map, sensitiveMap);
    }

    public static PaymentFormConfigurationRequest.PaymentFormConfigurationRequestBuilder createDefaultPaymentFormConfigurationRequestBuilder() {
        return PaymentFormConfigurationRequest.PaymentFormConfigurationRequestBuilder.aPaymentFormConfigurationRequest()
                .withLocale(FRENCH)
                .withBuyer(createDefaultBuyer())
                .withAmount(new Amount(BigInteger.valueOf(100), Currency.getInstance(EUR)))
                .withContractConfiguration(createContractConfiguration())
                .withOrder(createOrder("007"))
                .withEnvironment(createDefaultPaylineEnvironment())
                .withPartnerConfiguration(createDefaultPartnerConfiguration());
    }

    //Cree une redirection payment par defaut
    public static RedirectionPaymentRequest createCompleteRedirectionPayment(String transactionId) {
        final Map<String, String> requestData = new HashMap<>();
        requestData.put(RequestContextKeys.PARTNER_TRANSACTION_ID, transactionId);
        final RequestContext requestContext = RequestContext.RequestContextBuilder
                .aRequestContext()
                .withRequestData(requestData)
                .build();
        return RedirectionPaymentRequest.builder()
                .withCaptureNow(true)
                .withAmount(createAmount(EUR))
                .withBrowser(new Browser("", Locale.FRANCE))
                .withContractConfiguration(createContractConfiguration())
                .withEnvironment(createDefaultPaylineEnvironment())
                .withOrder(createOrder(transactionId))
                .withLocale(Locale.FRANCE)
                .withTransactionId(transactionId)
                .withSoftDescriptor("a descriptor")
                .withPartnerConfiguration(createDefaultPartnerConfiguration())
                .withBuyer(createDefaultBuyer())
                .withRequestContext(requestContext)
                .build();
    }

    public static TransactionStatusRequest.TransactionStatusRequestBuilder createTransactionRequestBuilder() {
        final Amount amount = createAmount(EUR);
        final ContractConfiguration contractConfiguration = createContractConfiguration();
        final Environment paylineEnvironment = new Environment(NOTIFICATION_URL, SUCCESS_URL, FAILURE_URL, true);
        final String transactionID = createTransactionId();
        final Order order = createOrder(transactionID);
        final Buyer buyer = createDefaultBuyer();

        return TransactionStatusRequest.TransactionStatusRequestBuilder
                .aNotificationRequest()
                .withAmount(amount)
                .withContractConfiguration(contractConfiguration)
                .withEnvironment(paylineEnvironment)
                .withOrder(order)
                .withTransactionId(transactionID)
                .withBuyer(buyer)
                .withPartnerConfiguration(createDefaultPartnerConfiguration());
    }


    public static Buyer createBuyer() {
        final Buyer.FullName fullName = new Buyer.FullName("FirstName", "LastName", "Mr");
        return Buyer.BuyerBuilder.aBuyer().withFullName(fullName).build();
    }
}
