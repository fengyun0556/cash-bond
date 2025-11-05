package com.cpb.fixengine.listener;

import com.alibaba.fastjson2.JSONObject;
import com.cpb.fixengine.dto.BBGOrderCreationMessage;
import com.cpb.fixengine.handler.TcpServerHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RocketMQMessageListener(topic = "BBG_ORDER_CREATION_TOPIC", consumerGroup = "fix-engine-consumer-group")
public class CashBondOrderCreationListener implements RocketMQListener<BBGOrderCreationMessage> {
    @Autowired
    private TcpServerHandler tcpServerHandler;

    @Override
    public void onMessage(BBGOrderCreationMessage bbgOrderCreationMessage) {
        log.info("receive message: {}", JSONObject.toJSONString(bbgOrderCreationMessage));
        StringBuilder bbgOrderCreationStringBuilder = new StringBuilder();
        bbgOrderCreationStringBuilder.append("1=").append(bbgOrderCreationMessage.getUniqueId()).append("|");
        bbgOrderCreationStringBuilder.append("2=").append(bbgOrderCreationMessage.getISIN()).append("|");
        bbgOrderCreationStringBuilder.append("3=").append(bbgOrderCreationMessage.getISINName()).append("|");
        bbgOrderCreationStringBuilder.append("4=").append(bbgOrderCreationMessage.getExchangeCode()).append("|");
        bbgOrderCreationStringBuilder.append("5=").append(bbgOrderCreationMessage.getOrderType()).append("|");
        bbgOrderCreationStringBuilder.append("6=").append(bbgOrderCreationMessage.getQuantity()).append("|");
        bbgOrderCreationStringBuilder.append("7=").append(bbgOrderCreationMessage.getPrice());

        log.info("bbgOrderCreationStringBuilder: {}", bbgOrderCreationStringBuilder);
        try {
            tcpServerHandler.sendMessage(bbgOrderCreationStringBuilder.toString());
        } catch (Exception e) {
            log.error("call BG error: {}", e.getMessage(), e);
        }
    }
}
