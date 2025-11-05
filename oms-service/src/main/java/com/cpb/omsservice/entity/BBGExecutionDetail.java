package com.cpb.omsservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "BBG_EXECUTION_DETAIL")
@Data
public class BBGExecutionDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BBG_EXECUTION_ID")
    private Long bbgExecutionId;
    @Column(name = "BBG_MESSAGE_ID")
    private String bbgMessageId;
    @Column(name = "TRADE_LINK_ID")
    private Long tradeLinkId;
    @Column(name = "ACCOUNT_KEY")
    private String accountKey;
    @Column(name = "EXECUTED_QUANTITY")
    private Integer executedQuantity;
    @Column(name = "EXECUTED_PRICE")
    private BigDecimal executedPrice;
    @Column(name = "CREATE_TIME", updatable = false)
    private LocalDateTime createTime;
    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;
}
