package com.cpb.oms.domain.model.settlement;

import lombok.Data;

@Data
public class SettlementResult {
    private Boolean success;
    private Long settlementId;
    private String failedReason;
}
