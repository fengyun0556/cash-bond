package com.cpb.oms.interfaces.settlement;

import com.cpb.oms.domain.model.settlement.SettlementInteract;
import org.springframework.stereotype.Component;

@Component
public class SettlementInteractAssembler {
    public SettlementInteractResponse convertToSettlementInteractResponse(SettlementInteract settlementInteract) {
        SettlementInteractResponse settlementInteractResponse = new SettlementInteractResponse();
        settlementInteractResponse.setId(settlementInteract.getId());
        settlementInteractResponse.setTps2ExecutionId(settlementInteract.getTps2ExecutionId());
        settlementInteractResponse.setCashAccount(settlementInteract.getCashAccount());
        settlementInteractResponse.setIsin(settlementInteract.getIsin());
        settlementInteractResponse.setExecutedQuantity(settlementInteract.getExecutedQuantity());
        settlementInteractResponse.setExecutedPrice(settlementInteract.getExecutedPrice());
        settlementInteractResponse.setSettlementId(settlementInteract.getSettlementId());
        settlementInteractResponse.setSettlementState(settlementInteract.getSettlementState());
        settlementInteractResponse.setFailedReason(settlementInteract.getFailedReason());
        return settlementInteractResponse;
    }
}
