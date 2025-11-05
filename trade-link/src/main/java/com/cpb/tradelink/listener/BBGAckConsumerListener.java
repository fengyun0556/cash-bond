package com.cpb.tradelink.listener;

import com.alibaba.fastjson2.JSONObject;
import com.cpb.tradelink.dto.BBGAckMessage;
import com.cpb.tradelink.service.BBGAckService;
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
    private BBGAckService bbgAckService;

    @Override
    public void onMessage(BBGAckMessage bbgAckMessage) {
        log.info("bbg ack 收到消息: {}", JSONObject.toJSONString(bbgAckMessage));
        bbgAckService.ack(bbgAckMessage);
        log.info("bbg ack end, tradeLinkId: {}", bbgAckMessage.getTradeLinkId());
    }
}