package com.cpb.oms.infrastructure.mq;

import com.cpb.oms.application.service.TradingApplicationService;
import com.cpb.oms.domain.model.trading.BBGAckMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RocketMQMessageListener(topic = "BBG_ACK_TOPIC", consumerGroup = "oms-ack-consumer-group")
public class BBGAckListener implements RocketMQListener<BBGAckMessage> {

    @Autowired
    private TradingApplicationService tradingApplicationService;

    @Override
    public void onMessage(BBGAckMessage bbgAckMessage) {
        log.info("bbg ack 收到消息: {}", bbgAckMessage);
        tradingApplicationService.ack(bbgAckMessage);
        log.info("bbg ack end, tradeLinkId: {}", bbgAckMessage.getTradeLinkId());
    }
}