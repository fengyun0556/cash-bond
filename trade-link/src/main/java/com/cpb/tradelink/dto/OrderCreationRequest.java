package com.cpb.tradelink.dto;

import com.cpb.tradelink.enums.OrderRequestMode;
import com.cpb.tradelink.enums.OrderType;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreationRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 8042070067251656135L;
    private String accountKey;
    private String accountName;
    private String memberKey;
    private String memberName;
    private String cashAccount;
    private String isin;//证券唯一编码
    private String isinName;//证券名称
    private String exchangeCode;//交易所编码
    private OrderType orderType;// 订单类型: LIMIT(限价), MARKET(市价)
    private Integer quantity;// 订单数量 (单位: 股)
    private BigDecimal price;// 限价单价格 (市价单可为空)
    private OrderRequestMode orderRequestMode;
    private List<RuleCheck> ruleCheckList;
}
