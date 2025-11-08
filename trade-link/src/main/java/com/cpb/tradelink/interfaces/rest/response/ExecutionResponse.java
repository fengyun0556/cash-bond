package com.cpb.tradelink.interfaces.rest.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExecutionResponse {
    private Integer executedQuantity;
    private BigDecimal executedPrice;
}
