package com.cpb.oms.domain.event;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeExecutedEvent {
    private Long tps2ExecutionId;
    private Long tradeLinkId;
    private String accountKey;
    private String ISIN;//证券唯一编码
    private String ISINName;//证券名称
    private String exchangeCode;//交易所编码
    private Integer executedQuantity;
    private BigDecimal executedPrice;
}
