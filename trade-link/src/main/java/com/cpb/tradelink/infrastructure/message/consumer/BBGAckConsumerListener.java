package com.cpb.tradelink.infrastructure.message.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.cpb.tradelink.domain.event.BBGAckMessage;
import com.cpb.tradelink.domain.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RocketMQMessageListener(topic = "BBG_ACK_TOPIC", consumerGroup = "tl-ack-consumer-group")
public class BBGAckConsumerListener implements RocketMQListener<BBGAckMessage> {

    @Autowired
    private OrderService orderService;

    @Override
    public void onMessage(BBGAckMessage bbgAckMessage) {
        log.info("bbg ack 收到消息: {}", JSONObject.toJSONString(bbgAckMessage));
        orderService.bbgAck(bbgAckMessage.getAckSuccess(), bbgAckMessage.getTradeLinkId());
        log.info("bbg ack end, tradeLinkId: {}", bbgAckMessage.getTradeLinkId());
    }
}