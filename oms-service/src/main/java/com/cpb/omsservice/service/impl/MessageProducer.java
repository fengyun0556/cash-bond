package com.cpb.omsservice.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.cpb.omsservice.dto.BBGOrderCreationMessage;
import com.cpb.omsservice.dto.ExecutionMessage;
import com.cpb.omsservice.dto.PTBMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageProducer {
    
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    
    public void sendBBGOrderCreationMessage(BBGOrderCreationMessage bbgOrderCreationMessage) {
        log.info("send BBG Order Creation Message: {}", JSONObject.toJSONString(bbgOrderCreationMessage));
        rocketMQTemplate.convertAndSend("BBG_ORDER_CREATION_TOPIC", bbgOrderCreationMessage);
    }

    public void sendExecutionMessage(ExecutionMessage executionMessage) {
        log.info("send execution message: {}", JSONObject.toJSONString(executionMessage));
        rocketMQTemplate.convertAndSend("EXECUTION_TOPIC", executionMessage);
    }

    public void sendPTBMessage(PTBMessage ptbMessage) {
        log.info("send PTB message: {}", JSONObject.toJSONString(ptbMessage));
        rocketMQTemplate.convertAndSend("PTB_TOPIC", ptbMessage);
    }
}