package com.cpb.oms.application.builder;

import com.cpb.oms.domain.event.BBGExecutionMessage;
import com.cpb.oms.domain.model.executed.BBGExecution;
import org.springframework.stereotype.Component;

@Component
public class BBGExecutionBuilder {

    public BBGExecution buildBBGExecution(BBGExecutionMessage bbgExecutionMessage) {
        BBGExecution bbgExecution = new BBGExecution();
        bbgExecution.setTradeLinkId(bbgExecutionMessage.getUniqueId());
        bbgExecution.setBbgMessageId(bbgExecutionMessage.getBbgMessageId());
        bbgExecution.setAccountKey(bbgExecutionMessage.getAccountKey());
        bbgExecution.setISIN(bbgExecutionMessage.getISIN());
        bbgExecution.setExecutedQuantity(bbgExecutionMessage.getExecutedQuantity());
        bbgExecution.setExecutedPrice(bbgExecutionMessage.getExecutedPrice());
        return bbgExecution;
    }
}
