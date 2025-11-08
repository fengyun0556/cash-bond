package com.cpb.oms.interfaces.executed;

import com.cpb.oms.domain.model.executed.BBGExecution;
import org.springframework.stereotype.Component;

@Component
public class ExecutionAssembler {
    public ExecutionResponse convertToExecutionResponse(BBGExecution bbgExecution) {
        ExecutionResponse executionResponse = new ExecutionResponse();
        executionResponse.setTps2ExecutionId(bbgExecution.getTps2ExecutionId());
        executionResponse.setTradeLinkId(bbgExecution.getTradeLinkId());
        executionResponse.setBbgMessageId(bbgExecution.getBbgMessageId());
        executionResponse.setAccountKey(bbgExecution.getAccountKey());
        executionResponse.setISIN(bbgExecution.getISIN());
        executionResponse.setExecutedQuantity(bbgExecution.getExecutedQuantity());
        executionResponse.setExecutedPrice(bbgExecution.getExecutedPrice());
        executionResponse.setConfirmed(bbgExecution.getConfirmed());
        return executionResponse;
    }
}
