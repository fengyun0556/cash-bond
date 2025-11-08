package com.cpb.oms.infrastructure.persistence.executed;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TPS2_EXECUTION_DETAIL")
@Data
public class TPS2ExecutionDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TPS2_EXECUTION_ID")
    private Long tps2ExecutionId;
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
    @Column(name = "CONFIRMED")
    private Boolean confirmed;
    @Column(name = "CONFIRMED_DATETIME")
    private LocalDateTime confirmedDateTime;
    @Column(name = "CREATE_TIME", updatable = false)
    private LocalDateTime createTime;
    @Column(name = "UPDATE_TIME", updatable = false)
    private LocalDateTime updateTime;
}
