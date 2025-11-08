package com.cpb.tradelink.infrastructure.message.publisher;

import com.cpb.tradelink.application.service.EmailService;
import com.cpb.tradelink.domain.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void orderApproved(Order order) {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setId(order.getOrderId());
        emailRequest.setEmailTemplateId("confirm");
        rocketMQTemplate.convertAndSend("NOTIFICATION_TOPIC", emailRequest);
        log.info("send message: {}", emailRequest);
    }

    @Override
    public void orderExecuted(Order order) {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setId(order.getOrderId());
        emailRequest.setEmailTemplateId("executed");
        rocketMQTemplate.convertAndSend("NOTIFICATION_TOPIC", emailRequest);
        log.info("send message: {}", emailRequest);
    }

    @Override
    public void orderEnrich(Order order) {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setId(order.getOrderId());
        emailRequest.setEmailTemplateId("enrich");
        rocketMQTemplate.convertAndSend("NOTIFICATION_TOPIC", emailRequest);
        log.info("send message: {}", emailRequest);
    }
}
