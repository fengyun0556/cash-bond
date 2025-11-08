package com.cpb.oms.interfaces.executed;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExecutionResponse {
    private Long tps2ExecutionId;
    private Long tradeLinkId;
    private String bbgMessageId;
    private String accountKey;
    private String ISIN;//证券唯一编码
    private Integer executedQuantity;
    private BigDecimal executedPrice;
    private Boolean confirmed;
}
