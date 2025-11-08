package com.cpb.oms.infrastructure.settlement;

import com.cpb.oms.domain.model.settlement.SettlementInteract;
import com.cpb.oms.domain.model.settlement.SettlementResult;
import org.springframework.stereotype.Component;

@Component
public class SettlementMapper {
    public SettlementRequest buildSettlementInteract(SettlementInteract settlementInteract) {
        SettlementRequest settlementRequest = new SettlementRequest();
        settlementRequest.setAccountKey(settlementInteract.getCashAccount());
        settlementRequest.setCashAccount(settlementInteract.getCashAccount());
        settlementRequest.setExecutedQuantity(settlementInteract.getExecutedQuantity());
        settlementRequest.setExecutedPrice(settlementInteract.getExecutedPrice());
        return settlementRequest;
    }

    public SettlementResult buildSettlementResult(SettlementResponse settlementResponse) {
        SettlementResult settlementResult = new SettlementResult();
        settlementResult.setSuccess(settlementResponse.getSuccess());
        settlementResult.setSettlementId(settlementResponse.getSettlementId());
        settlementResult.setFailedReason(settlementResponse.getFailedReason());
        return settlementResult;
    }
}
