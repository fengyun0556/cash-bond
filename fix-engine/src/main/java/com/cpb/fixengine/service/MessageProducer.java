package com.cpb.fixengine.service;

import com.alibaba.fastjson2.JSONObject;
import com.cpb.fixengine.dto.BBGAckMessage;
import com.cpb.fixengine.dto.BBGExecutionMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageProducer {
    
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    
    public void sendBBGAckMessage(BBGAckMessage bbgAckMessage) {
        log.info("send BBG ack message: {}", JSONObject.toJSONString(bbgAckMessage));
        rocketMQTemplate.convertAndSend("BBG_ACK_TOPIC", bbgAckMessage);
    }

    public void sendBBGExecutionMessage(BBGExecutionMessage bbgExecutionMessage) {
        log.info("send execution message: {}", JSONObject.toJSONString(bbgExecutionMessage));
        rocketMQTemplate.convertAndSend("BBG_EXECUTION_TOPIC", bbgExecutionMessage);
    }
}