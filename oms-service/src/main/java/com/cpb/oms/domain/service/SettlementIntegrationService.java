package com.cpb.oms.domain.service;

import com.cpb.oms.domain.model.settlement.SettlementInteract;
import com.cpb.oms.domain.model.settlement.SettlementResult;

public interface SettlementIntegrationService {
    SettlementResult settlement(SettlementInteract settlementInteract);
}
