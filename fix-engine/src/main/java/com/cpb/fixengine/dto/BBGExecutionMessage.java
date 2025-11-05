package com.cpb.fixengine.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BBGExecutionMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = -6216358546383076523L;
    private Long uniqueId;
    private String bbgMessageId;
    private String accountKey;
    private String ISIN;//证券唯一编码
    private String ISINName;//证券名称
    private String exchangeCode;//交易所编码
    private Integer executedQuantity;
    private BigDecimal executedPrice;
}