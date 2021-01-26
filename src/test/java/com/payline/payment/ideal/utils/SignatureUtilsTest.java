package com.payline.payment.ideal.utils;

import com.payline.payment.ideal.exception.PluginException;
import com.payline.payment.ideal.utils.security.SignatureUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;

class SignatureUtilsTest {
    private SignatureUtils signatureUtils = SignatureUtils.getInstance();

    private static final String sPublicKey = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA8aZPHqzHeIzWPlC0T6TP" +
            "seEfNTBbeKz1iC60eDxVTRx2fV9IhnJOQoq1Kv79IHV0t671N51X6/yIuKGJ79/l" +
            "SErMaVhdR47WOcMIfc6/5A0uhupm2lQ8bYQ04ewEtbZ5Bx7JLwMrYHxbUvTMYiGq" +
            "2VD2O+L0in8MtsPhzCjKSDIakHST3iMtn7WPLBBDA+cHa2dC0kFXFzU1qhDCxt0Y" +
            "6M5iW1LwDBZe/zVdDViWcyQo16FqXwAN8vBHtvQJ1mKt3iw8h1fGBeWWUDYl88wC" +
            "OvMKhWz3vqOOc/PrSgEXgUdFbZmWtrF9yF93nP+bCSmfZgdU+aEAzGrD+KHEaDfP" +
            "KwIDAQAB" +
            "-----END PUBLIC KEY-----";

    private static final String signedReference = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><DirectoryReq xmlns=\"http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1\" version=\"3.3.1\"><createDateTimestamp>2019-07-30T08:16:03.574Z</createDateTimestamp><Merchant><merchantID>003087616</merchantID><subID>0</subID></Merchant><Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\"><SignedInfo><CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\"/><Reference URI=\"\"><Transforms><Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/></Transforms><DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"/><DigestValue>c3VWIOE23ovKAUUt3V7b2Zrwdafg0AF9xpsxjwRF+LA=</DigestValue></Reference></SignedInfo><SignatureValue>kLAZMsidTHR+dRYr+jS294GqSflrW6/+mlu3kxB8FyvJp8rnG9kYvy4NNGp9jr/Nt3hlx1UaDsOa\n" +
            "NnwcxIIM3XJnKBU1FfyuJ0sgvqOX8ucfZBwCjQVy1EVNMZix+9T9J+6h2e+f196/SMKtlGArnyVL\n" +
            "/GbtWHW/Nr0l4oryKCCvI18jPkzD6KJhcxGJoaZOoqEi5WDIqOKdpspRfNrvUDoHJE3hDxC9ypHl\n" +
            "9GHYmNc+xh/4qhF49zyH0RdffgF7gUSsHn1g4CAC2HeyxTbQlgjWHzuiJf/PWwzGWjtRYhm0aKCH\n" +
            "PUK7aVmQK5ite4Q3bHKL4FrLUQWAjcxgJ8bayA==</SignatureValue><KeyInfo><KeyName>foo</KeyName></KeyInfo></Signature></DirectoryReq>";


    @Test
    void getPrivateKeyFromString() throws IOException {
        String fileName = System.getProperty("project.privateKey");
        Path path = Paths.get(fileName);
        String key = new String(Files.readAllBytes(path));
        PrivateKey privateKey = signatureUtils.getPrivateKeyFromString(key);

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
    void signXML() throws IOException {
        String message = "<DirectoryReq xmlns='http://www.idealdesk.com/ideal/messages/mer-acq/3.3.1' version='3.3.1'><createDateTimestamp>2019-07-30T08:16:03.574Z</createDateTimestamp><Merchant><merchantID>003087616</merchantID><subID>0</subID></Merchant></DirectoryReq>";

        // get key pair
        String sPrivateKey = new String(Files.readAllBytes(Paths.get(System.getProperty("project.privateKey"))));
        PrivateKey privateKey = signatureUtils.getPrivateKeyFromString(sPrivateKey);
        PublicKey publicKey = signatureUtils.getPublicKeyFromString(sPublicKey);

        String body = signatureUtils.signXML(message, publicKey, "foo", privateKey);
        Assertions.assertEquals(signedReference, body.replace("&#13;", ""));
    }

    @Test
    void signXMLwithNullMessage() throws IOException {
        // get key pair
        String sPrivateKey = new String(Files.readAllBytes(Paths.get(System.getProperty("project.privateKey"))));
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
        String sPrivateKey = new String(Files.readAllBytes(Paths.get(System.getProperty("project.privateKey"))));
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