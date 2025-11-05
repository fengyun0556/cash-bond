package com.cpb.omsservice.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SettlementRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -2765788365144211542L;
    private Long tradeLinkId;
    private Long tps2Id;
    private String accountKey;
    private String memberKey;
    private String cashAccount;
    private Integer executedQuantity;
    private BigDecimal executedPrice;
}
