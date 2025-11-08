package com.cpb.oms.infrastructure.settlement;

import lombok.Data;

@Data
public class SettlementResponse {
    private Boolean success;
    private Long settlementId;
    private String failedReason;
}
