package com.cpb.oms.infrastructure.persistence.settlement;

import com.cpb.oms.domain.enums.SettlementState;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "SETTLEMENT_INTERACT_DETAIL")
@Data
public class SettlementInteractDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "TPS2_EXECUTION_ID")
    private Long tps2ExecutionId;
    @Column(name = "ACCOUNT_KEY", length = 50)
    private String accountKey;
    @Column(name = "ACCOUNT_NAME", length = 100)
    private String accountName;
    @Column(name = "MEMBER_KEY", length = 50)
    private String memberKey;
    @Column(name = "MEMBER_NAME", length = 100)
    private String memberName;
    @Column(name = "CASH_ACCOUNT", length = 50)
    private String cashAccount;
    @Column(name = "ISIN", length = 12)
    private String isin;
    @Column(name = "EXECUTED_QUANTITY")
    private Integer executedQuantity;
    @Column(name = "EXECUTED_PRICE")
    private BigDecimal executedPrice;
    @Column(name = "SETTLEMENT_ID")
    private Long settlementId;
    @Enumerated(EnumType.STRING)
    @Column(name = "SETTLEMENT_STATE", length = 20)
    private SettlementState settlementState;
    @Column(name = "FAILED_REASON")
    private String failedReason;
    @Column(name = "CREATE_TIME", updatable = false)
    private LocalDateTime createTime;
    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;
}
