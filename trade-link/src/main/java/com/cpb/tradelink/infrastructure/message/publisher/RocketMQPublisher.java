package com.cpb.tradelink.infrastructure.message.publisher;

import com.cpb.tradelink.application.service.MessageSender;
import com.cpb.tradelink.domain.event.MEMOOrderCreatedEvent;
import com.cpb.tradelink.domain.event.OrderAmendedEvent;
import com.cpb.tradelink.domain.event.OrderEnrichedEvent;
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
    public void send(MEMOOrderCreatedEvent memoOrderCreatedEvent) {
        log.info("发送MEMO订单创建事件到RocketMQ, topic=MEMO_CREATED_TOPIC, orderId={}",
                memoOrderCreatedEvent.getOrderId());
        try {
            rocketMQTemplate.convertAndSend("MEMO_CREATED_TOPIC", memoOrderCreatedEvent);
            log.info("MEMO订单创建事件发送成功, topic=MEMO_CREATED_TOPIC, orderId={}",
                    memoOrderCreatedEvent.getOrderId());
        } catch (Exception e) {
            log.error("MEMO订单创建事件发送失败, topic=MEMO_CREATED_TOPIC, orderId={}, 错误信息: {}",
                    memoOrderCreatedEvent.getOrderId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void send(OrderEnrichedEvent orderEnrichedEvent) {
        log.info("发送订单 enrichment 事件到RocketMQ, topic=ORDER_ENRICH_TOPIC, orderId={}, tps2ExecutionId={}",
                orderEnrichedEvent.getOrderId(), orderEnrichedEvent.getTps2ExecutionId());
        try {
            rocketMQTemplate.convertAndSend("ORDER_ENRICH_TOPIC", orderEnrichedEvent);
            log.info("订单 enrichment 事件发送成功, topic=ORDER_ENRICH_TOPIC, orderId={}",
                    orderEnrichedEvent.getOrderId());
        } catch (Exception e) {
            log.error("订单 enrichment 事件发送失败, topic=ORDER_ENRICH_TOPIC, orderId={}, 错误信息: {}",
                    orderEnrichedEvent.getOrderId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void send(OrderAmendedEvent orderAmendedEvent) {
        log.info("发送订单修改事件到RocketMQ, topic=ORDER_AMENDED_TOPIC, orderId={}",
                orderAmendedEvent.getOrderId());
        try {
            rocketMQTemplate.convertAndSend("ORDER_AMENDED_TOPIC", orderAmendedEvent);
            log.info("订单修改事件发送成功, topic=ORDER_AMENDED_TOPIC, orderId={}",
                    orderAmendedEvent.getOrderId());
        } catch (Exception e) {
            log.error("订单修改事件发送失败, topic=ORDER_AMENDED_TOPIC, orderId={}, 错误信息: {}",
                    orderAmendedEvent.getOrderId(), e.getMessage(), e);
            throw e;
        }
    }
}
