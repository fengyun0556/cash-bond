package com.cpb.omsservice.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class PTBMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = -3651681105956034435L;
    private Long bbgExecutionId;
    private String accountKey;
    private String accountName;
    private String memberKey;
    private String memberName;
    private String cashAccount;
    private String ISIN;//证券唯一编码
    private Integer executedQuantity;
    private BigDecimal executedPrice;
}
