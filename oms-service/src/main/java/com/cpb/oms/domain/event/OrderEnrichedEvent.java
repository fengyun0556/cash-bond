package com.cpb.oms.domain.event;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderEnrichedEvent {
    private Long orderId;
    private Long tps2ExecutionId;
    private String cashAccount;
    private String cashAccountCurrency;
    private String isin;
    private String isinType;
    private BigDecimal price;
    private BigDecimal commissionRate;
    private BigDecimal commissionPrice;
}
