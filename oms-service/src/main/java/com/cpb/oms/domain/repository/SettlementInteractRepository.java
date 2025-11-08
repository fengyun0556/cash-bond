package com.cpb.oms.domain.repository;

import com.cpb.oms.domain.model.settlement.SettlementInteract;

public interface SettlementInteractRepository {
    void save(SettlementInteract settlementInteract);

    SettlementInteract getSettlementInteract(Long settlementInteractId);

    SettlementInteract getSettlementInteractByTPS2ExecutionId(Long tps2ExecutionId);
}
