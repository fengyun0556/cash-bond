package com.cpb.omsservice.dto;

import com.cpb.omsservice.enums.OrderType;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ExecutionMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 8457843989850121044L;
    private String bbgExecutionId;
    private Long tradeLinkId;
    private String accountKey;
    private String ISIN;//证券唯一编码
    private String ISINName;//证券名称
    private String exchangeCode;//交易所编码
    private OrderType orderType;// 订单类型: LIMIT(限价), MARKET(市价)
    private Integer executedQuantity;
    private BigDecimal executedPrice;
}
