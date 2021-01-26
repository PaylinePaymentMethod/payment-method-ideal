package com.payline.payment.ideal.utils.security;

import com.payline.payment.ideal.exception.CryptoException;
import com.payline.payment.ideal.exception.InvalidDataException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class SignatureUtils {
    private static final String DOM = "DOM";
    private static final String KEY_ALGORITHM = "RSA";

    private static final String EMPTY = "";
    private static final String BEGIN_PRIVATE_PATTERN = "-----BEGIN PRIVATE KEY-----\n";
    private static final String END_PRIVATE_PATTERN = "-----END PRIVATE KEY-----";
    private static final String BEGIN_PUBLIC_PATTERN = "-----BEGIN PUBLIC KEY-----\n";
    private static final String END_PUBLIC_PATTERN = "-----END PUBLIC KEY-----";

    private static final String SIGNATURE_TAG = "Signature";
    private static final String S_SIGNATURE_METHOD = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";


    private SignatureUtils() {
    }

    /**
     * Singleton Holder
     */
    private static class SingletonHolder {
        private static final SignatureUtils INSTANCE = new SignatureUtils();
    }

    /**
     * @return the singleton instance
     */
    public static SignatureUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }


    /**
     * Create a PrivateKey object from a String
     *
     * @param key
     * @return
     */
    public PrivateKey getPrivateKeyFromString(String key) {
        if (key == null) {
            throw new InvalidDataException("private Key shall not be null");
        }
        try {
            String privateKeyPEM = key
                    .replace(BEGIN_PRIVATE_PATTERN, EMPTY)
                    .replace(END_PRIVATE_PATTERN, EMPTY);

            byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
            KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            return kf.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new InvalidDataException(e.getMessage());
        }
    }

    /**
     * Create a publicKey object from a String
     *
     * @param key
     * @return
     */
    public PublicKey getPublicKeyFromString(String key) {
        if (key == null) {
            throw new InvalidDataException("public Key shall not be null");
        }
        try {
            String publicKeyPEM = key
                    .replace(BEGIN_PUBLIC_PATTERN, EMPTY)
                    .replace(END_PUBLIC_PATTERN, EMPTY);

            byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
            KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM);
            return kf.generatePublic(new X509EncodedKeySpec(encoded));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new InvalidDataException(e.getMessage());
        }
    }

    /**
     * create a signed XML from an unsigned XML
     *
     * @param message    the XML message to sign
     * @param publicKey  the public key to give to verify the signature
     * @param privateKey the privateKey used to sign the document
     * @return the signed XML
     */
    public String signXML(String message, PublicKey publicKey, String publicKeyId, PrivateKey privateKey) {
        if (message == null) {
            throw new InvalidDataException("message to sign shall not be null");
        }
        if (publicKey == null || privateKey == null) {
            throw new InvalidDataException("key shall not be null");
        }
        try {

            XMLSignatureFactory fac = XMLSignatureFactory.getInstance(DOM);

            // Signed Info
            // Canonicalization Method
            CanonicalizationMethod cm = fac.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null);

            // Signature Method
            SignatureMethod sm = fac.newSignatureMethod(S_SIGNATURE_METHOD, null);

            // Reference
            //     transforms
            List<Transform> transformList = Collections.singletonList(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));

            //     Digest Method
            DigestMethod digestMethod = fac.newDigestMethod(DigestMethod.SHA256, null);

            Reference ref = fac.newReference("", digestMethod, transformList, null, null);

            // create Signed Info
            List<Reference> refList = new ArrayList<>();
            refList.add(ref);
            SignedInfo signedInfo = fac.newSignedInfo(cm, sm, refList);

            // Key Info
            KeyInfoFactory keyInfoFactory = fac.getKeyInfoFactory();
            KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(keyInfoFactory.newKeyName(publicKeyId)));

            // convert String to XML Document
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputSource inputSource = new InputSource();
            inputSource.setCharacterStream(new StringReader(message));
            Document document = documentBuilder.parse(inputSource);
            document.setXmlStandalone(true);

            // sign Document
            DOMSignContext domSignContext = new DOMSignContext(privateKey, document.getDocumentElement());
            XMLSignature signature = fac.newXMLSignature(signedInfo, keyInfo);
            signature.sign(domSignContext);

            // add signature to document
            DOMSource domSource = new DOMSource(document);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer trans = tf.newTransformer();
            trans.transform(domSource, new StreamResult(outputStream));

            // get the final signed XML
            return outputStream.toString();

        } catch (InvalidAlgorithmParameterException | IOException | XMLSignatureException | MarshalException | ParserConfigurationException | SAXException | NoSuchAlgorithmException | TransformerException e) {
            throw new CryptoException("Unable to sign document", e);
        }
    }


    /**
     * check the signature of a signed XML
     *
     * @param message   the signed message to verify
     * @param publicKey the key used to verify the signature
     */
    public void verifySignatureXML(String message, PublicKey publicKey) {
        if (message == null) {
            throw new InvalidDataException("message to sign shall not be null");
        }
        if (publicKey == null) {
            throw new InvalidDataException("public Key shall not be null");
        }
        try {
            // convert String to XML Document
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);
            documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); // Compliant
            documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, ""); // compliant
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputSource inputSource = new InputSource();
            inputSource.setCharacterStream(new StringReader(message));
            Document document = documentBuilder.parse(inputSource);

            // search Signature Element
            NodeList nodeList = document.getElementsByTagNameNS(XMLSignature.XMLNS, SIGNATURE_TAG);
            if (nodeList.getLength() == 0) {
                // no signature found
                throw new CryptoException("no Signature element found");
            }

            XMLSignatureFactory fac = XMLSignatureFactory.getInstance(DOM);
            DOMValidateContext validateContext = new DOMValidateContext(publicKey, nodeList.item(0));

            XMLSignature signature = fac.unmarshalXMLSignature(validateContext);


            if (!signature.validate(validateContext)) {
                throw new CryptoException("Invalid signature");
            }

        } catch (IOException | XMLSignatureException | SAXException | ParserConfigurationException | MarshalException e) {
            throw new CryptoException("Unable to verify signature", e);
        }
    }
}
