package com.cpb.oms.infrastructure.mq;

import com.cpb.oms.application.service.SettlementApplicationService;
import com.cpb.oms.domain.event.OrderEnrichedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RocketMQMessageListener(topic = "ORDER_ENRICH_TOPIC", consumerGroup = "oms-enriched-consumer-group")
public class OrderEnrichedListener implements RocketMQListener<OrderEnrichedEvent> {

    @Autowired
    private SettlementApplicationService settlementApplicationService;

    @Override
    public void onMessage(OrderEnrichedEvent orderEnrichedEvent) {
        settlementApplicationService.handle(orderEnrichedEvent);
    }
}
