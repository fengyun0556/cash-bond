package com.cpb.oms.infrastructure.mq;

import com.alibaba.fastjson2.JSONObject;
import com.cpb.oms.application.service.SettlementApplicationService;
import com.cpb.oms.domain.event.TradeExecutedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RocketMQMessageListener(topic = "EXECUTION_TOPIC", consumerGroup = "oms-execute-consumer-group")
public class ExecutionConsumerListener implements RocketMQListener<TradeExecutedEvent> {

    @Autowired
    private SettlementApplicationService settlementApplicationService;

    @Override
    public void onMessage(TradeExecutedEvent tradeExecutedEvent) {
        log.info("execution 收到消息: {}", JSONObject.toJSONString(tradeExecutedEvent));
        settlementApplicationService.orderExecuted(tradeExecutedEvent);
        log.info("execution end, trade link id: {}", tradeExecutedEvent.getTradeLinkId());
    }

}