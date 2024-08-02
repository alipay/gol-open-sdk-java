package com.alipay.global.api.model.ams;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterestFree {

    private String provider;

    private String expireTime;

    private List<Integer> installmentFreeNums;

    private Amount minPaymentAmount;

    private Amount maxPaymentAmount;

    private Integer freePercentage;

}
