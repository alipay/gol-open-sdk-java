package com.alipay.global.api.tools;


/**
 * Alipay.com Inc. Copyright (c) 2004-2019 All Rights Reserved.
 */

import com.alipay.global.api.base64.Base64Provider;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SignatureTool {

    private static final String RSA             = "RSA";
    private static final String SHA256WITHRSA   = "SHA256withRSA";
    private static final String DEFAULT_CHARSET = "UTF-8";

    public static String sign(String httpMethod, String path, String clientId, String reqTimeStr, String reqBody, String merchantPrivateKey) throws Exception{
        String reqContent = genSignContent(httpMethod, path, clientId, reqTimeStr, reqBody);
        return encode(signWithSHA256RSA(reqContent, merchantPrivateKey), DEFAULT_CHARSET);
    }

    public static boolean verify(String httpMethod, String path, String clientId, String rspTimeStr, String rspBody, String signature, String alipayPublicKey) throws Exception {
        String rspContent = genSignContent(httpMethod, path, clientId, rspTimeStr, rspBody);
        return verifySignatureWithSHA256RSA(rspContent, decode(signature, DEFAULT_CHARSET), alipayPublicKey);
    }

    public static String genSignContent(String httpMethod, String path, String clientId, String timeString, String content){
        String payload = httpMethod + " " + path + "\n" + clientId + "." + timeString
                + "." + content;

        return payload;
    }

    /**
     * Sign the contents of the merchant request
     *
     * @param reqContent = httpMethod + " " + uriWithQueryString + "\n" + clientId + "." + timeString + "." + reqBody;
     * @param merchantPrivateKey the private key
     * @return the string
     * @throws Exception the exception
     */
    public static String sign(String reqContent, String merchantPrivateKey) throws Exception{
        return encode(signWithSHA256RSA(reqContent, merchantPrivateKey), DEFAULT_CHARSET);
    }

    /**
     * Check the response of Alipay
     *
     * @param rspContent = httpMethod + " " + uriWithQueryString + "\n" + clientId + "." + timeString + "." + rspBody;
     * @param signature  the signature
     * @param alipayPublicKey  the public key
     * @return the boolean
     * @throws Exception the exception
     */
    public static boolean verify(String rspContent, String signature, String alipayPublicKey) throws Exception {
        return verifySignatureWithSHA256RSA(rspContent, decode(signature, DEFAULT_CHARSET), alipayPublicKey);
    }

    /**
     * Verify if the received signature is correctly generated with the sender's public key
     *
     * @param rspContent: the original content signed by the sender and to be verified by the receiver.
     * @param signature:  the signature generated by the sender
     * @param strPk:      the public key string-base64 encoded
     * @return
     * @throws Exception
     */
    private static boolean verifySignatureWithSHA256RSA(String rspContent, String signature, String strPk) throws Exception {
        PublicKey publicKey = getPublicKeyFromBase64String(strPk);

        Signature publicSignature = Signature.getInstance(SHA256WITHRSA);
        publicSignature.initVerify(publicKey);
        publicSignature.update(rspContent.getBytes(DEFAULT_CHARSET));

        byte[] signatureBytes = Base64Provider.getBase64Encryptor().decode(signature);
        return publicSignature.verify(signatureBytes);

    }


    /**
     * Generate base64 encoded signature using the sender's private key
     *
     * @param reqContent:    the original content to be signed by the sender
     * @param strPrivateKey: the private key which should be base64 encoded
     * @return
     * @throws Exception
     */
    private static String signWithSHA256RSA(String reqContent, String strPrivateKey) throws Exception {
        Signature privateSignature = Signature.getInstance(SHA256WITHRSA);
        privateSignature.initSign(getPrivateKeyFromBase64String(strPrivateKey));
        privateSignature.update(reqContent.getBytes(DEFAULT_CHARSET));
        byte[] s = privateSignature.sign();

        return Base64Provider.getBase64Encryptor().encodeToString(s);
    }

    /**
     *
     * @param publicKeyString
     * @return
     */
    private static PublicKey getPublicKeyFromBase64String(String publicKeyString) throws Exception{
        byte[] b1 = Base64Provider.getBase64Encryptor().decode(publicKeyString);
        X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(b1);
        KeyFactory kf = KeyFactory.getInstance(RSA);
        return kf.generatePublic(X509publicKey);
    }

    /**
     *
     * @param privateKeyString
     * @return
     * @throws Exception
     */
    private static PrivateKey getPrivateKeyFromBase64String(String privateKeyString) throws Exception{
        byte[] b1 = Base64Provider.getBase64Encryptor().decode(privateKeyString);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b1);
        KeyFactory kf = KeyFactory.getInstance(RSA);
        return kf.generatePrivate(spec);
    }

    /**
     * URL  encode
     * @param originalStr
     * @param characterEncoding
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String encode(String originalStr,
                                String characterEncoding) throws UnsupportedEncodingException {
        return URLEncoder.encode(originalStr, characterEncoding);
    }

    /**
     * URL decode
     * @param originalStr
     * @param characterEncoding
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String decode(String originalStr,
                                String characterEncoding) throws UnsupportedEncodingException {
        return URLDecoder.decode(originalStr, characterEncoding);
    }

}
