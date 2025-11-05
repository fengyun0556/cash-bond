package com.cpb.fixengine.dto;

import com.cpb.fixengine.enums.OrderType;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BBGOrderCreationMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = -4413621676113305640L;
    private Long uniqueId;
    private String accountKey;
    private String ISIN;//证券唯一编码
    private String ISINName;//证券名称
    private String exchangeCode;//交易所编码
    private OrderType orderType;// 订单类型: LIMIT(限价), MARKET(市价)
    private Integer quantity;// 订单数量 (单位: 股)
    private BigDecimal price;// 限价单价格 (市价单可为空)
}