package com.cpb.tradelink.infrastructure.message.publisher;

import com.cpb.tradelink.application.service.MessageSender;
import com.cpb.tradelink.domain.event.MEMOOrderCreatedEvent;
import com.cpb.tradelink.domain.event.OrderAmendedEvent;
import com.cpb.tradelink.domain.event.OrderEnrichedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RocketMQPublisher implements MessageSender {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void send(MEMOOrderCreatedEvent memoOrderCreatedEvent) {
        rocketMQTemplate.convertAndSend("MEMO_CREATED_TOPIC", memoOrderCreatedEvent);
    }

    @Override
    public void send(OrderEnrichedEvent orderEnrichedEvent) {
        rocketMQTemplate.convertAndSend("ORDER_ENRICH_TOPIC", orderEnrichedEvent);
    }

    @Override
    public void send(OrderAmendedEvent orderAmendedEvent) {
        rocketMQTemplate.convertAndSend("ORDER_AMENDED_TOPIC", orderAmendedEvent);
    }
}
