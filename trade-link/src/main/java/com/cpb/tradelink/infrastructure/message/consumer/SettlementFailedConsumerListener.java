package com.cpb.tradelink.infrastructure.message.consumer;

import com.cpb.tradelink.domain.event.SettlementFailedMessage;
import com.cpb.tradelink.domain.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RocketMQMessageListener(topic = "SETTLEMENT_FAILED_TOPIC", consumerGroup = "tl-execute-consumer-group")
public class SettlementFailedConsumerListener implements RocketMQListener<SettlementFailedMessage> {

    @Autowired
    private OrderService orderService;

    @Override
    public void onMessage(SettlementFailedMessage settlementFailedMessage) {
        orderService.settlementFailed(settlementFailedMessage.getOrderId());
    }
}
