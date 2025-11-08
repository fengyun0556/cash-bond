package com.cpb.oms.infrastructure.settlement;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettlementRequest {
    private Long tradeLinkId;
    private Long tps2Id;
    private String accountKey;
    private String memberKey;
    private String cashAccount;
    private Integer executedQuantity;
    private BigDecimal executedPrice;
}
