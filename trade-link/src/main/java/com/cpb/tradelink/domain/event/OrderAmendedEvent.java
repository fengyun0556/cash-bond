package com.cpb.tradelink.domain.event;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderAmendedEvent {
    private Long orderId;
    private String accountKey;
    private String accountName;
    private String memberKey;
    private String memberName;
    private String cashAccount;
    private String cashAccountCurrency;
    private String isin;
    private String isinType;
    private BigDecimal commissionRate;
    private BigDecimal commissionPrice;
}
