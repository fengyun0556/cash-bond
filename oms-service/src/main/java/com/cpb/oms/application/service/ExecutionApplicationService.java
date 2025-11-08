package com.cpb.oms.application.service;

import com.cpb.oms.application.builder.BBGExecutionBuilder;
import com.cpb.oms.domain.event.BBGExecutionMessage;
import com.cpb.oms.domain.event.TradeExecutedEvent;
import com.cpb.oms.domain.model.executed.BBGExecution;
import com.cpb.oms.domain.model.settlement.SettlementInteract;
import com.cpb.oms.domain.model.settlement.SettlementResult;
import com.cpb.oms.domain.repository.ExecutedRepository;
import com.cpb.oms.domain.repository.SettlementInteractRepository;
import com.cpb.oms.domain.service.SettlementIntegrationService;
import com.cpb.oms.domain.service.SettlementInteractService;
import com.cpb.oms.interfaces.executed.ExecutionConfirmedRequest;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExecutionApplicationService {

    @Autowired
    private MessageSender messageSender;
    @Autowired
    private BBGExecutionBuilder bbgExecutionBuilder;
    @Autowired
    private ExecutedRepository executedRepository;
    @Autowired
    private SettlementInteractRepository settlementInteractRepository;
    @Autowired
    private SettlementInteractService settlementInteractService;
    @Autowired
    private SettlementIntegrationService settlementIntegrationService;

    public void executed(BBGExecutionMessage bbgExecutionMessage) {
        BBGExecution bbgExecution = bbgExecutionBuilder.buildBBGExecution(bbgExecutionMessage);
        bbgExecution.init();

        //save DB
        executedRepository.save(bbgExecution);
    }

    public void confirmed(ExecutionConfirmedRequest executionConfirmedRequest) {
        BBGExecution bbgExecution = executedRepository.get(executionConfirmedRequest.getTps2ExecutionId());

        bbgExecution.confirmed();
        executedRepository.save(bbgExecution);

        //send to TL
        TradeExecutedEvent tradeExecutedEvent = bbgExecution.createTradeExecutedEvent();
        messageSender.send(tradeExecutedEvent);

        if (StringUtils.isNotEmpty(bbgExecution.getCashAccount())) {

            //build SettlementInteract
            SettlementInteract settlementInteract = settlementInteractService.buildSettlementInteract(bbgExecution);
            settlementInteract.init();

            //save DB
            settlementInteractRepository.save(settlementInteract);

            //call settlement
            SettlementResult settlementResult = settlementIntegrationService.settlement(settlementInteract);

            //save settlement result
            settlementInteract.saveSettlementResult(settlementResult);

            //update DB
            settlementInteractRepository.save(settlementInteract);
        } else {
            log.info("phone order");
        }
    }

    public BBGExecution get(Long tps2ExecutionId) {
        return executedRepository.get(tps2ExecutionId);
    }
}
