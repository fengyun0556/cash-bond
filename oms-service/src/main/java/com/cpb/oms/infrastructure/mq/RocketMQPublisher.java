package com.cpb.oms.infrastructure.mq;

import com.alibaba.fastjson2.JSONObject;
import com.cpb.oms.application.service.MessageSender;
import com.cpb.oms.domain.event.SettlementFailedEvent;
import com.cpb.oms.domain.event.TradeExecutedEvent;
import com.cpb.oms.domain.event.TradeSubmissionEvent;
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
    public void send(TradeSubmissionEvent tradeSubmissionEvent) {
        log.info("send BBG Order Creation Message: {}", JSONObject.toJSONString(tradeSubmissionEvent));
        rocketMQTemplate.convertAndSend("BBG_ORDER_CREATION_TOPIC", tradeSubmissionEvent);
    }

    @Override
    public void send(TradeExecutedEvent tradeExecutedEvent) {
        log.info("send BBG Executed Message: {}", JSONObject.toJSONString(tradeExecutedEvent));
        rocketMQTemplate.convertAndSend("BBG_ORDER_EXECUTED_TOPIC", tradeExecutedEvent);
    }

    @Override
    public void send(SettlementFailedEvent settlementFailedEvent) {
        log.info("send settlement failed Message: {}", JSONObject.toJSONString(settlementFailedEvent));
        rocketMQTemplate.convertAndSend("SETTLEMENT_FAILED_TOPIC", settlementFailedEvent);
    }
}
