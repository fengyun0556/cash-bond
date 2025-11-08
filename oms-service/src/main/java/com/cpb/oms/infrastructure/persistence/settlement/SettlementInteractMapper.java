package com.cpb.oms.infrastructure.persistence.settlement;

import com.cpb.oms.domain.model.settlement.SettlementInteract;
import org.springframework.stereotype.Component;

@Component
public class SettlementInteractMapper {

    public SettlementInteractDetail buildSettlementInteractDetail(SettlementInteract settlementInteract) {
        SettlementInteractDetail settlementInteractDetail = new SettlementInteractDetail();
        settlementInteractDetail.setTps2ExecutionId(settlementInteract.getTps2ExecutionId());
        settlementInteractDetail.setCashAccount(settlementInteract.getCashAccount());
        settlementInteractDetail.setIsin(settlementInteract.getIsin());
        settlementInteractDetail.setExecutedQuantity(settlementInteract.getExecutedQuantity());
        settlementInteractDetail.setExecutedPrice(settlementInteract.getExecutedPrice());
        settlementInteractDetail.setSettlementId(settlementInteract.getSettlementId());
        settlementInteractDetail.setSettlementState(settlementInteract.getSettlementState());
        settlementInteractDetail.setCreateTime(settlementInteract.getCreateTime());
        settlementInteractDetail.setUpdateTime(settlementInteract.getUpdateTime());
        return settlementInteractDetail;
    }

    public SettlementInteract buildSettlementInteract(SettlementInteractDetail settlementInteractDetail) {
        SettlementInteract settlementInteract = new SettlementInteract();
        settlementInteract.setId(settlementInteractDetail.getId());
        settlementInteract.setTps2ExecutionId(settlementInteractDetail.getTps2ExecutionId());
        settlementInteract.setCashAccount(settlementInteractDetail.getCashAccount());
        settlementInteract.setIsin(settlementInteractDetail.getIsin());
        settlementInteract.setExecutedQuantity(settlementInteractDetail.getExecutedQuantity());
        settlementInteract.setExecutedPrice(settlementInteractDetail.getExecutedPrice());
        settlementInteract.setSettlementId(settlementInteractDetail.getSettlementId());
        settlementInteract.setSettlementState(settlementInteractDetail.getSettlementState());
        settlementInteract.setFailedReason(settlementInteractDetail.getFailedReason());
        settlementInteract.setCreateTime(settlementInteractDetail.getCreateTime());
        settlementInteract.setUpdateTime(settlementInteractDetail.getUpdateTime());
        return settlementInteract;
    }
}
