package com.cpb.oms.infrastructure.mq;

import com.alibaba.fastjson2.JSONObject;
import com.cpb.oms.application.service.ExecutionApplicationService;
import com.cpb.oms.domain.event.BBGExecutionMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RocketMQMessageListener(topic = "BBG_EXECUTION_TOPIC", consumerGroup = "oms-execute-consumer-group")
public class BBGExecutionListener implements RocketMQListener<BBGExecutionMessage> {

    @Autowired
    private ExecutionApplicationService executionApplicationService;

    @Override
    public void onMessage(BBGExecutionMessage bbgExecutionMessage) {
        log.info("bbg execution 收到消息：{}", JSONObject.toJSONString(bbgExecutionMessage));
        executionApplicationService.executed(bbgExecutionMessage);
        log.info("bbg execution end, {}", bbgExecutionMessage.getBbgMessageId());
    }

}
