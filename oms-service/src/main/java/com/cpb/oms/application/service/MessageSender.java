package com.cpb.oms.application.service;

import com.cpb.oms.domain.event.SettlementFailedEvent;
import com.cpb.oms.domain.event.TradeExecutedEvent;
import com.cpb.oms.domain.event.TradeSubmissionEvent;

public interface MessageSender {
    void send(TradeSubmissionEvent tradeSubmissionEvent);

    void send(TradeExecutedEvent tradeExecutedEvent);

    void send(SettlementFailedEvent settlementFailedEvent);
}
