package com.payline.payment.ideal.utils;

import com.payline.payment.ideal.exception.PluginException;
import com.payline.payment.ideal.utils.security.SignatureUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SignatureUtilsTest {
    private SignatureUtils signatureUtils = SignatureUtils.getInstance();

    private static final String sPrivateKey = "-----BEGIN PRIVATE KEY-----\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC5uZHobzIkUG/AH36sXTMjP32zffNj1ocNHmjp88pjoz2wT0t+hJRxKYkCp0k3KkXZa9douk0KAviNeJ+M3rz51UTz1N/Z4Ln3e+xl24jHDJAazrsK2NZnu7FfBNBafWloXizBXcWc/K01qAHRYPeuIhxzR6Dan8Tz+lIT40I4boJLB+F156DM718t168gdK35Qrg/kR7OsQL297V+/LOxpI5QICeQP1LC1LMyCKRfn7LAk+ZKop37O2kHeIovqQleynCgOCSn1r9+tBV6CBoQ0C8tLBs4JPJdL8WzL/jJm5NAsGw0mRWETxyZO3lAqF/Zc2t+yeATUlOFRj2K5ERlAgMBAAECggEAQXlqNsb8plC1FMYFoj45CPQZHG4Kn+fBNJ7D3bZUY0vpM37A3pleu4YpBwZyiUAd38hk6EbFYgIqHbWoicD8XhcRd+RUBjtzVAwcCsyaK5ICO3El/2zyKR/CJ/1Kdpw/zMtme8TTJcTuSzwvZ+EHcBwWa42U533rsRdOWDSF4953g94I0kpaG3QtguRX1WPu37rfy9Q5jBYCP8RI+dlpseO61fAw1BeRDdapE+xbTXwTVS6j7IO4hAxubb17mzQpVWnu0nrPcQxy4b8yX2grngF9hxAmRLF3UUGfAQbbQu+BvJF8AwoW7PIFOkjDa+pzRENk5JKF+5i4+gXwpVezAQKBgQDoR9JTcMYS6n5JwtJ+FzoUQ4R7yKh4TexHOJMFEtUCLWyPa/bHhtbX7ooGaBOFPwiWKgcOYGPG4TNTJ1fSSXOeNG0z0M2/4VHU046Eflm1FMyEkeSxIBf1u+iADbXpft7TnthWqQXdUZrolQmgMHScad278lbUhxpuVhCCU9TGdwKBgQDMsLXEzC/0Sob3u6ogFgpU4fTYZrs0rrC39Oy/Vcpm7nDwx1G4ocfNYmOYdgqU6bo0gfaU9HF89GRsdI+YbGqNzqvnwMV/PKMC+gQDhmD51e4WTR8zQlYSlU0enUG5YJny6UEeiWFKvC2emxY3gSgSV5PPue3204BasbQx3wjXAwKBgQCDEDuCL2jkNYIqto407YAs/OfdPJyqvTUr/qUeQMEGKIFP79hgPxos3wDsmn8hOS7sJCy1FPhXuKxQKCvqkN13EBfqG9Bsi4Oz9ec9pVmSzniT+Qo0U2qRkoSe9J2HcbnjaIajZ13SwoAI6kdzJsQ1mZquuQ/7fXwRc+h+0TSHAwKBgC3f7N0OZjD6AwXCqX5HcklT2uwlGg/Ulwajfevj7ljjV6ye/1HB8GW3h1rEGdsGBxV9iyYXItg+bcNg3E3s/nSJvT1Tlu/NRzuDjSK9Gz7jE1ksL7HtTB0eGQqjc/d4DwjB/jY50/RL+G1zlKcb6z8xIY+MvCh1fm7VJO2IbxLBAoGBAKcxLQaTLVDjTvbGpnYhBp7HH8nM+jUor9BJjf7o0Ix/pxeop2YMz29dWke9oIBSMM8BDIvBJpYeckJHEkY5tgmwFhd9bOV1Sap6cdAFuWyVXQabY6yWQOcMdoJGw37oxujmDarHfKTyNtfCDwUQZm/kxNY7Xqq3Ys1qBgKwMMqr-----END PRIVATE KEY-----";

    private static final String sPublicKey = "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAubmR6G8yJFBvwB9+rF0zIz99s33zY9aHDR5o6fPKY6M9sE9LfoSUcSmJAqdJNypF2WvXaLpNCgL4jXifjN68+dVE89Tf2eC593vsZduIxwyQGs67CtjWZ7uxXwTQWn1paF4swV3FnPytNagB0WD3riIcc0eg2p/E8/pSE+NCOG6CSwfhdeegzO9fLdevIHSt+UK4P5EezrEC9ve1fvyzsaSOUCAnkD9SwtSzMgikX5+ywJPmSqKd+ztpB3iKL6kJXspwoDgkp9a/frQVeggaENAvLSwbOCTyXS/Fsy/4yZuTQLBsNJkVhE8cmTt5QKhf2XNrfsngE1JThUY9iuREZQIDAQAB-----END PUBLIC KEY-----";

    private static final String signedReference ="<?xml version=\"1.0\" encoding=\"UTF-8\"?><DirectoryReq xmlns=\"http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1\" version=\"3.3.1\"><createDateTimestamp>2019-07-30T08:16:03.574Z</createDateTimestamp><Merchant><merchantID>003087616</merchantID><subID>0</subID></Merchant><Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\"><SignedInfo><CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\"/><Reference URI=\"\"><Transforms><Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/></Transforms><DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"/><DigestValue>c3VWIOE23ovKAUUt3V7b2Zrwdafg0AF9xpsxjwRF+LA=</DigestValue></Reference></SignedInfo><SignatureValue>LT5KJc9PWk8cV9xlsyAV13hWy2tEMjV2QTbFyMirczFNi+p9hlHhC2cIG7g7zsBktv82+0KWbiTi\n" +
"4HZmwYuv7i92HlN8PLrewhe2yz9fnytmXCcPVQLUEkBHEJqRNCNy5qSVlNALXs6LgXWRRVqsNBPn\n" +
    "SL+BtxBfoqv21gOxcJ2deCWIUul8NE4gXEHI6QolZXCMVFe5rZaAduIy0k1iuBN8FwDT8wpR0g7y\n" +
    "rBO+4sfPay71I89z7zHD/JB/4sEzGT8zZremET/dsDfOSB/BZ8PTQhSjFx3gV4ZZ7eTiSpAis/os\n" +
"4Tte+rghHbnyM2DciQuarn8LRHV6UULd/Vuycg==</SignatureValue><KeyInfo><KeyName>foo</KeyName></KeyInfo></Signature></DirectoryReq>";


    @Test
    void getPrivateKeyFromString() {

        PrivateKey privateKey = signatureUtils.getPrivateKeyFromString(sPrivateKey);
        Assertions.assertNotNull(privateKey);
    }

    @Test
    void getPrivateKeyFromStringWithNullString() {
        Assertions.assertThrows(PluginException.class, () -> signatureUtils.getPrivateKeyFromString(null));

    }

    @Test
    void getPrivateKeyFromStringWithWrongString() {
        Assertions.assertThrows(PluginException.class, () -> signatureUtils.getPrivateKeyFromString("thisIsaWrongKey"));

    }

    @Test
    void getPublicKeyFromString() {
        PublicKey publicKey = signatureUtils.getPublicKeyFromString(sPublicKey);
        Assertions.assertNotNull(publicKey);
    }

    @Test
    void getPublicKeyFromStringWithNullString() {
        Assertions.assertThrows(PluginException.class, () -> signatureUtils.getPublicKeyFromString(null));

    }

    @Test
    void getPublicKeyFromStringWithWrongString() {
        Assertions.assertThrows(PluginException.class, () -> signatureUtils.getPublicKeyFromString("thisIsaWrongKey"));

    }

    @Test
    void signXML() {
        String message = "<DirectoryReq xmlns='http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1' version='3.3.1'><createDateTimestamp>2019-07-30T08:16:03.574Z</createDateTimestamp><Merchant><merchantID>003087616</merchantID><subID>0</subID></Merchant></DirectoryReq>";

        // get key pair
        PrivateKey privateKey = signatureUtils.getPrivateKeyFromString(sPrivateKey);
        PublicKey publicKey = signatureUtils.getPublicKeyFromString(sPublicKey);

        String body = signatureUtils.signXML(message, publicKey, "foo", privateKey);
        signatureUtils.verifySignatureXML(body, publicKey);
        assertEquals(signedReference.replaceAll("\\s+", ""),body.replace("&#13;", "").replaceAll("\\s+", ""));
    }

    @Test
    void signXMLwithNullMessage() {
        // get key pair
        PrivateKey privateKey = signatureUtils.getPrivateKeyFromString(sPrivateKey);
        PublicKey publicKey = signatureUtils.getPublicKeyFromString(sPublicKey);
        Assertions.assertThrows(PluginException.class, () -> signatureUtils.signXML(null, publicKey, "foo", privateKey));

    }

    @Test
    void signXMLwithNullKeys() {
        String message = "<DirectoryReq xmlns='http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1' version='3.3.1'><createDateTimestamp>2019-07-30T08:16:03.574Z</createDateTimestamp><Merchant><merchantID>003087616</merchantID><subID>0</subID></Merchant></DirectoryReq>";
        Assertions.assertThrows(PluginException.class, () -> signatureUtils.signXML(message, null, "foo", null));

    }

    @Test
    void signXMLwithWrongXML() throws Exception {
        // get key pair
        PrivateKey privateKey = signatureUtils.getPrivateKeyFromString(sPrivateKey);
        PublicKey publicKey = signatureUtils.getPublicKeyFromString(sPublicKey);

        Assertions.assertThrows(PluginException.class, () -> signatureUtils.signXML("I am not an XML Document", publicKey, "foo", privateKey));
    }

    @Test
    void verifySignatureXML() throws Exception {
        PublicKey publicKey = signatureUtils.getPublicKeyFromString(sPublicKey);
        Assertions.assertDoesNotThrow(() -> signatureUtils.verifySignatureXML(signedReference, publicKey));
    }

    @Test
    void verifySignatureXMLwithNullMessage() throws Exception {
        PublicKey publicKey = signatureUtils.getPublicKeyFromString(sPublicKey);

        Assertions.assertThrows(PluginException.class, () -> signatureUtils.verifySignatureXML(null, publicKey));
    }

    @Test
    void verifySignatureXMLwithNullKey() {
        Assertions.assertThrows(PluginException.class, () -> signatureUtils.verifySignatureXML(signedReference, null));
    }

    @Test
    void verifySignatureXMLwithWrongXml() throws Exception {
        PublicKey publicKey = signatureUtils.getPublicKeyFromString(sPublicKey);
        Assertions.assertThrows(PluginException.class, () -> signatureUtils.verifySignatureXML("I am not an XML Document", publicKey));
    }

    @Test
    void verifySignatureXMLwithoutSignatureTag() throws Exception {
        String notSigned = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><DirectoryReq xmlns=\"http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1\" version=\"3.3.1\"><createDateTimestamp>2019-07-30T08:16:03.574Z</createDateTimestamp><Merchant><merchantID>003087616</merchantID><subID>0</subID></Merchant></DirectoryReq>";

        PublicKey publicKey = signatureUtils.getPublicKeyFromString(sPublicKey);
        Assertions.assertThrows(PluginException.class, () -> signatureUtils.verifySignatureXML(notSigned, publicKey));
    }
}