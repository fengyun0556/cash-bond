package com.cpb.tradelink.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ORDER_EXECUTION_DETAIL")
@Data
public class OrderExecutionDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EXECUTION_ID")
    private Long executionId;
    @Column(name = "BBG_EXECUTION_ID")
    private String bbgExecutionId;
    @Column(name = "TRADE_LINK_ID")
    private Long tradeLinkId;
    @Column(name = "EXECUTED_QUANTITY")
    private Integer executedQuantity;
    @Column(name = "EXECUTED_PRICE")
    private BigDecimal executedPrice;
    @Column(name = "CREATE_TIME", updatable = false)
    private LocalDateTime createTime;
    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;
}
