package com.cpb.tradelink.infrastructure.message.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.cpb.tradelink.application.service.OrderLifecycleAppService;
import com.cpb.tradelink.domain.event.ExecutionMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RocketMQMessageListener(topic = "EXECUTION_TOPIC", consumerGroup = "tl-execute-consumer-group")
public class ExecutionConsumerListener implements RocketMQListener<ExecutionMessage> {

    @Autowired
    private OrderLifecycleAppService orderLifecycleAppService;

    @Override
    public void onMessage(ExecutionMessage executionMessage) {
        log.info("bbg execution 收到消息: {}", JSONObject.toJSONString(executionMessage));
        orderLifecycleAppService.orderExecuted(executionMessage);
        log.info("bbg execution end, trade link id: {}", executionMessage.getTradeLinkId());
    }

}