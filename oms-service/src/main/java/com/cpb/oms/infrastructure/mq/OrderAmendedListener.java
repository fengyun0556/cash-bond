package com.cpb.oms.infrastructure.mq;

import com.cpb.oms.application.service.SettlementApplicationService;
import com.cpb.oms.domain.event.OrderAmendedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RocketMQMessageListener(topic = "ORDER_AMENDED_TOPIC", consumerGroup = "oms-amend-consumer-group")
public class OrderAmendedListener implements RocketMQListener<OrderAmendedEvent> {

    @Autowired
    private SettlementApplicationService settlementApplicationService;

    @Override
    public void onMessage(OrderAmendedEvent orderAmendedEvent) {
        settlementApplicationService.handle(orderAmendedEvent);
    }
}
