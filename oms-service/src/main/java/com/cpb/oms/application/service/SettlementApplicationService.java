package com.cpb.oms.application.service;

import com.cpb.oms.domain.event.OrderAmendedEvent;
import com.cpb.oms.domain.event.OrderEnrichedEvent;
import com.cpb.oms.domain.event.SettlementFailedEvent;
import com.cpb.oms.domain.model.settlement.SettlementInteract;
import com.cpb.oms.domain.model.settlement.SettlementResult;
import com.cpb.oms.domain.repository.SettlementInteractRepository;
import com.cpb.oms.domain.service.SettlementIntegrationService;
import com.cpb.oms.interfaces.settlement.SendToBankerRequest;
import com.cpb.oms.interfaces.settlement.SettlementTriggerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettlementApplicationService {

    @Autowired
    private SettlementInteractRepository settlementInteractRepository;
    @Autowired
    private SettlementIntegrationService settlementIntegrationService;
    @Autowired
    private MessageSender messageSender;

    public Boolean trigger(SettlementTriggerRequest settlementTriggerRequest) {
        SettlementInteract settlementInteract = settlementInteractRepository.getSettlementInteract(settlementTriggerRequest.getSettlementInteractId());

        SettlementResult settlementResult = this.callSettlement(settlementInteract);

        return settlementResult.getSuccess();
    }

    public SettlementInteract get(Long settlementInteractId) {
        return settlementInteractRepository.getSettlementInteract(settlementInteractId);
    }

    public void sendToBanker(SendToBankerRequest sendToBankerRequest) {
        SettlementInteract settlementInteract = settlementInteractRepository.getSettlementInteract(sendToBankerRequest.getSettlementInteractId());

        SettlementFailedEvent settlementFailedEvent = settlementInteract.createSettlementFailedEvent();
        messageSender.send(settlementFailedEvent);

        settlementInteract.sendToBanker();

        settlementInteractRepository.save(settlementInteract);
    }

    public void handle(OrderEnrichedEvent orderEnrichedEvent) {
        SettlementInteract settlementInteract = settlementInteractRepository
                .getSettlementInteractByTPS2ExecutionId(orderEnrichedEvent.getTps2ExecutionId());

        boolean canEnrich = settlementInteract.canEnrich();

        if (canEnrich) {
            settlementInteract.enrich(orderEnrichedEvent);
            this.callSettlement(settlementInteract);
        }
    }

    public void handle(OrderAmendedEvent orderAmendedEvent) {
        SettlementInteract settlementInteract = settlementInteractRepository
                .getSettlementInteractByTPS2ExecutionId(orderAmendedEvent.getTps2ExecutionId());

        boolean canAmend = settlementInteract.canAmend();
        if (canAmend) {
            settlementInteract.amend(orderAmendedEvent);
            this.callSettlement(settlementInteract);
        }
    }

    private SettlementResult callSettlement(SettlementInteract settlementInteract) {
        //call settlement
        SettlementResult settlementResult = settlementIntegrationService.settlement(settlementInteract);

        //save settlement result
        settlementInteract.saveSettlementResult(settlementResult);

        //update DB
        settlementInteractRepository.save(settlementInteract);
        return settlementResult;
    }
}
