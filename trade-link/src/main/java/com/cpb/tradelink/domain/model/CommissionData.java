package com.cpb.tradelink.domain.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CommissionData {
    private BigDecimal commissionRate;
    private String commissionType;
    private BigDecimal commissionPrice;
}
