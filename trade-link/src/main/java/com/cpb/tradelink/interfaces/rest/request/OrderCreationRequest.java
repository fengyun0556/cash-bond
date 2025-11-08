package com.cpb.tradelink.interfaces.rest.request;

import com.cpb.tradelink.domain.enums.OrderRequestMode;
import com.cpb.tradelink.domain.enums.OrderType;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreationRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 4502491071753661422L;
    private String accountKey;
    private String accountName;
    private String memberKey;
    private String memberName;
    private String cashAccount;
    private String cashAccountCurrency;
    private String isin;//证券唯一编码
    private String isinName;//证券名称
    private String isinType;
    private String exchangeCode;//交易所编码
    private OrderType orderType;// 订单类型: LIMIT(限价), MARKET(市价)
    private Integer quantity;// 订单数量 (单位: 股)
    private BigDecimal price;// 限价单价格 (市价单可为空)
    private BigDecimal commissionRate;
    private String commissionType;
    private OrderRequestMode orderRequestMode;
    private List<RuleCheckRequest> ruleCheckRequestList;
}
