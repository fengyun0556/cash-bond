package com.cpb.tradelink.domain.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ExecutionRecord {

    private Long executionId;
    private Long tps2ExecutionId;
    private Long orderId;
    private Integer executedQuantity;
    private BigDecimal executedPrice;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
