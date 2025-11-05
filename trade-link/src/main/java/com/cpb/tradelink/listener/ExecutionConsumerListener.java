package com.cpb.tradelink.listener;

import com.alibaba.fastjson2.JSONObject;
import com.cpb.tradelink.dto.ExecutionMessage;
import com.cpb.tradelink.service.ExecutionService;
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
    private ExecutionService executionService;

    @Override
    public void onMessage(ExecutionMessage executionMessage) {
        log.info("bbg execution 收到消息: {}", JSONObject.toJSONString(executionMessage));
        executionService.execute(executionMessage);
        log.info("bbg execution end, trade link id: {}", executionMessage.getTradeLinkId());
    }
}