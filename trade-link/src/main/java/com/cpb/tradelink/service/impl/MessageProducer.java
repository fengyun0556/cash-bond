package com.cpb.tradelink.service.impl;

import com.cpb.tradelink.dto.EmailRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageProducer {
    
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    
    public void sendMessage(EmailRequest emailRequest) {
        rocketMQTemplate.convertAndSend("NOTIFICATION_TOPIC", emailRequest);
        log.info("send message: {}", emailRequest);
    }
    
}