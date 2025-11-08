package com.cpb.oms.interfaces.settlement;

import com.cpb.oms.domain.enums.SettlementState;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettlementInteractResponse {
    private Long id;
    private Long tps2ExecutionId;
    private String cashAccount;
    private String isin;
    private Integer executedQuantity;
    private BigDecimal executedPrice;
    private Long settlementId;
    private SettlementState settlementState;
    private String failedReason;
}
