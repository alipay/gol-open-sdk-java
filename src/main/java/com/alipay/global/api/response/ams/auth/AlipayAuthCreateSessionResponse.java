package com.alipay.global.api.response.ams.auth;

import com.alipay.global.api.response.AlipayResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlipayAuthCreateSessionResponse extends AlipayResponse {

    private String paymentSessionId;
    private String paymentSessionData;
    private String paymentSessionExpiryTime;

}
