/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.alipay.global.api.request.ams.risk.tee.encryptstrategy;

import com.alipay.global.api.model.ams.UserName;
import com.alipay.global.api.model.risk.Order;
import com.alipay.global.api.model.risk.PaymentDetail;
import com.alipay.global.api.request.AlipayRequest;
import com.alipay.global.api.request.ams.risk.RiskDecideRequest;
import com.alipay.global.api.request.ams.risk.tee.crypto.AESCrypto;
import com.alipay.global.api.request.ams.risk.tee.enums.EncryptKeyEnum;
import com.alipay.global.api.request.ams.risk.tee.enums.ErrorCodeEnum;
import com.alipay.global.api.request.ams.risk.tee.exception.CryptoException;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.Charset;
import java.util.List;

/**
 * request encrypt strategy for risk decide API
 * risk decide API 的请求加密策略
 */
public class RiskDecideEncryptStrategy implements EncryptStrategy {

    Charset utf8Charset = Charset.forName("UTF-8");

    @Override
    public void encrypt(byte[] data_key, AlipayRequest<?> request, List<EncryptKeyEnum> encryptKeyList) {
        if (request == null || encryptKeyList == null) {
            return;
        }
        if (!(request instanceof RiskDecideRequest)) {
            throw new CryptoException(ErrorCodeEnum.MISMATCH_ENCRYPT_STRATEGY, "Request is not instance of RiskDecideRequest");
        }
        RiskDecideRequest riskDecideRequest = (RiskDecideRequest) request;
        AESCrypto crypto = AESCrypto.getInstance();
        doEncrypt(data_key, riskDecideRequest, encryptKeyList, crypto);
    }

    @Override
    public void encrypt(String dataKeyBase64, AlipayRequest<?> request, List<EncryptKeyEnum> encryptKeyList) {
        encrypt(DatatypeConverter.parseBase64Binary(dataKeyBase64), request, encryptKeyList);
    }

    /**
     * do encrypt by encryptKeyList
     * 根据 encryptKeyList 进行加密
     *
     * @param data_key       symmetric key
     * @param request        plaintext RiskDecideRequest
     * @param encryptKeyList list of encrypt keys
     * @param crypto         AESCrypto instance
     */
    private void doEncrypt(byte[] data_key, RiskDecideRequest request, List<EncryptKeyEnum> encryptKeyList,
                           AESCrypto crypto) {
        List<Order> orders = request.getOrders();
        List<PaymentDetail> paymentDetails = request.getPaymentDetails();
        byte[] encrypt;
        for (EncryptKeyEnum key : encryptKeyList) {
            if (key == null || key.getCode() == null) {
                continue;
            }
            switch (key) {
                case BUYER_EMAIL:
                    String buyerEmail = request.getBuyer().getBuyerEmail();
                    if (buyerEmail == null || buyerEmail.isEmpty()) {
                        continue;
                    }
                    encrypt = crypto.encrypt(data_key, buyerEmail.getBytes(utf8Charset));
                    request.getBuyer().setBuyerEmail(DatatypeConverter.printBase64Binary(encrypt));
                    break;
                case BUYER_PHONE_NO:
                    String buyerPhoneNo = request.getBuyer().getBuyerPhoneNo();
                    if (buyerPhoneNo == null || buyerPhoneNo.isEmpty()) {
                        continue;
                    }
                    encrypt = crypto.encrypt(data_key, buyerPhoneNo.getBytes(utf8Charset));
                    request.getBuyer().setBuyerPhoneNo(DatatypeConverter.printBase64Binary(encrypt));
                    break;
                case BUYER_REGISTRATION_TIME:
                    String buyerRegistrationTime = request.getBuyer().getBuyerRegistrationTime();
                    if (buyerRegistrationTime == null || buyerRegistrationTime.isEmpty()) {
                        continue;
                    }
                    encrypt = crypto.encrypt(data_key, buyerRegistrationTime.getBytes(utf8Charset));
                    request.getBuyer().setBuyerRegistrationTime(DatatypeConverter.printBase64Binary(encrypt));
                    break;
                case CARDHOLDER_NAME:
                    for (PaymentDetail paymentDetail : paymentDetails) {
                        encryptName(data_key, paymentDetail.getPaymentMethod().getPaymentMethodMetaData().getCardHolderName(), crypto);
                    }
                    break;
                case SHIPPING_ADDRESS1:
                    for (Order order : orders) {
                        String address1 = order.getShipping().getShippingAddress().getAddress1();
                        if (address1 == null || address1.isEmpty()) {
                            continue;
                        }
                        encrypt = crypto.encrypt(data_key, address1.getBytes(utf8Charset));
                        order.getShipping().getShippingAddress().setAddress1(DatatypeConverter.printBase64Binary(encrypt));
                    }
                    break;
                case SHIPPING_ADDRESS2:
                    for (Order order : orders) {
                        String address2 = order.getShipping().getShippingAddress().getAddress2();
                        if (address2 == null || address2.isEmpty()) {
                            continue;
                        }
                        encrypt = crypto.encrypt(data_key, address2.getBytes(utf8Charset));
                        order.getShipping().getShippingAddress().setAddress2(DatatypeConverter.printBase64Binary(encrypt));
                    }
                    break;
                case SHIPPING_NAME:
                    for (Order order : orders) {
                        encryptName(data_key, order.getShipping().getShippingName(), crypto);
                    }
                    break;
                case SHIP_TO_EMAIL:
                    for (Order order : orders) {
                        String email = order.getShipping().getShipToEmail();
                        if (email == null || email.isEmpty()) {
                            continue;
                        }
                        encrypt = crypto.encrypt(data_key, email.getBytes(utf8Charset));
                        order.getShipping().setShipToEmail(DatatypeConverter.printBase64Binary(encrypt));
                    }
                    break;
                case SHIPPING_PHONE_NO:
                    for (Order order : orders) {
                        String phoneNo = order.getShipping().getShippingPhoneNo();
                        if (phoneNo == null || phoneNo.isEmpty()) {
                            continue;
                        }
                        encrypt = crypto.encrypt(data_key, phoneNo.getBytes(utf8Charset));
                        order.getShipping().setShippingPhoneNo(DatatypeConverter.printBase64Binary(encrypt));
                    }
                    break;
                default:
                    throw new CryptoException(ErrorCodeEnum.UNKNOWN_ENCRYPT_KEY, "unknown encrypt key");
            }
        }
    }

    /**
     * encrypt username
     * 加密 username
     *
     * @param data_key symmetric key
     * @param userName user name
     * @param crypto   AESCrypto instance
     */
    private void encryptName(byte[] data_key, UserName userName, AESCrypto crypto) {
        if (userName == null) {
            return;
        }
        if (userName.getFirstName() != null && !userName.getFirstName().isEmpty()) {
            userName.setFirstName(DatatypeConverter.printBase64Binary(
                    crypto.encrypt(data_key, userName.getFirstName().getBytes(utf8Charset))));
        }
        if (userName.getMiddleName() != null && !userName.getMiddleName().isEmpty()) {
            userName.setMiddleName(DatatypeConverter.printBase64Binary(
                    crypto.encrypt(data_key, userName.getMiddleName().getBytes(utf8Charset))));
        }
        if (userName.getLastName() != null && !userName.getLastName().isEmpty()) {
            userName.setLastName(DatatypeConverter.printBase64Binary(
                    crypto.encrypt(data_key, userName.getLastName().getBytes(utf8Charset))));
        }
        if (userName.getFullName() != null && !userName.getFullName().isEmpty()) {
            userName.setFullName(DatatypeConverter.printBase64Binary(
                    crypto.encrypt(data_key, userName.getFullName().getBytes(utf8Charset))));
        }
    }
}

