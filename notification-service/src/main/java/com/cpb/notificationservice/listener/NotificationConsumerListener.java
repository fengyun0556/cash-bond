package com.cpb.notificationservice.listener;

import com.cpb.notificationservice.dto.EmailRequest;
import com.cpb.notificationservice.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RocketMQMessageListener(topic = "NOTIFICATION_TOPIC", consumerGroup = "notification-consumer-group")
public class NotificationConsumerListener implements RocketMQListener<EmailRequest> {

    @Autowired
    private EmailService emailService;

    @Override
    public void onMessage(EmailRequest emailRequest) {
        log.info("收到消息: {}", emailRequest);
        emailService.send(emailRequest);
    }
}