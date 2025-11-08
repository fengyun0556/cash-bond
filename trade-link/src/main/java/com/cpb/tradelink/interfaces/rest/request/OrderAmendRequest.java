package com.cpb.tradelink.interfaces.rest.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderAmendRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 7161868817338094968L;
    private Long orderId;
    private String accountKey;
    private String accountName;
    private String memberKey;
    private String memberName;
    private String cashAccount;
    private String cashAccountCurrency;
    private String isinType;
    private BigDecimal commissionRate;
    private String commissionType;
}
