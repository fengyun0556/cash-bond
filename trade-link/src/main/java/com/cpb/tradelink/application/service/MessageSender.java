package com.cpb.tradelink.application.service;

import com.cpb.tradelink.domain.event.MEMOOrderCreatedEvent;
import com.cpb.tradelink.domain.event.OrderAmendedEvent;
import com.cpb.tradelink.domain.event.OrderEnrichedEvent;

public interface MessageSender {
    void send(MEMOOrderCreatedEvent memoOrderCreatedEvent);

    void send(OrderEnrichedEvent orderEnrichedEvent);

    void send(OrderAmendedEvent orderAmendedEvent);
}
