package com.cpb.tradelink.interfaces.rest.response;

import com.cpb.tradelink.domain.enums.ExecutionState;
import com.cpb.tradelink.domain.enums.OrderRequestMode;
import com.cpb.tradelink.domain.enums.OrderState;
import com.cpb.tradelink.domain.enums.OrderType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDetailResponse {
    private Long orderId;
    private String accountKey;
    private String accountName;
    private String memberKey;
    private String memberName;
    private String cashAccount;
    private String cashAccountCurrency;
    private String isin;
    private String isinName;
    private String isinType;
    private String exchangeCode;
    private OrderType orderType;
    private Integer quantity;
    private Integer totalExecutedQuantity;
    private BigDecimal price;
    private BigDecimal commissionRate;
    private String commissionType;
    private BigDecimal commissionPrice;
    private OrderRequestMode orderRequestMode;
    private Long tps2Id;
    private OrderState orderState;
    private ExecutionState executionState;
    private List<RuleCheckResponse> ruleCheckResponseList;
    private List<ExecutionResponse> executionResponseList;
}
