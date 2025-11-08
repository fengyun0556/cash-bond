package com.cpb.oms.domain.service;

import com.cpb.oms.domain.model.executed.BBGExecution;
import com.cpb.oms.domain.model.settlement.SettlementInteract;
import com.cpb.oms.domain.model.trading.TradingInstruction;
import com.cpb.oms.domain.repository.TradingInstructionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SettlementInteractService {

    @Autowired
    private TradingInstructionRepository tradingInstructionRepository;

    public SettlementInteract buildSettlementInteract(BBGExecution bbgExecution) {
        TradingInstruction tradingInstruction = tradingInstructionRepository
                .getTradingInstructionByTradeLinkId(bbgExecution.getTradeLinkId());

        SettlementInteract settlementInteract = new SettlementInteract();
        settlementInteract.setTradeLinkId(bbgExecution.getTradeLinkId());
        settlementInteract.setTps2ExecutionId(bbgExecution.getTps2ExecutionId());
        settlementInteract.setCashAccount(tradingInstruction.getCashAccount());
        settlementInteract.setIsin(bbgExecution.getISIN());
        settlementInteract.setExecutedQuantity(bbgExecution.getExecutedQuantity());
        settlementInteract.setExecutedPrice(bbgExecution.getExecutedPrice());

        return settlementInteract;
    }
}
