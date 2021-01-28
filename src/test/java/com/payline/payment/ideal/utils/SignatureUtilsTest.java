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

    private static final String sPrivateKey = "-----BEGIN PRIVATE KEY-----\n" +
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