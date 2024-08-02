package com.alipay.global.api.response.ams.pay;

import com.alipay.global.api.response.AlipayResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlipayPayCancelResponse extends AlipayResponse {

    private String paymentId;
    private String paymentRequestId;
    private String cancelTime;

}
