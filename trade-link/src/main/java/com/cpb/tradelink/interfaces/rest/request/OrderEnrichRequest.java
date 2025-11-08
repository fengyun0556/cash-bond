package com.cpb.tradelink.interfaces.rest.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderEnrichRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -3948518371680119764L;
    private Long orderId;
    private String cashAccount;
    private String cashAccountCurrency;
    private String isinName;//证券名称
    private String isinType;
    private BigDecimal commissionRate;
    private String commissionType;
}
