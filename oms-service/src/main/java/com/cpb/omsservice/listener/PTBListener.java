package com.cpb.omsservice.listener;

import com.alibaba.fastjson2.JSONObject;
import com.cpb.omsservice.dto.PTBMessage;
import com.cpb.omsservice.entity.PTBDetail;
import com.cpb.omsservice.service.PTBService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RocketMQMessageListener(topic = "PTB_TOPIC", consumerGroup = "oms-ptb-consumer-group")
public class PTBListener implements RocketMQListener<PTBMessage> {
    @Autowired
    private PTBService ptbService;

    @Override
    public void onMessage(PTBMessage ptbMessage) {
        log.info("receive ptb message: {}", JSONObject.toJSONString(ptbMessage));
        PTBDetail ptbDetail = this.ptbService.savePTBDetail(ptbMessage);
        this.ptbService.sendToSettlementService(ptbDetail, ptbMessage);
        log.info("ptb message end, Bbg Execution Id: {}", ptbMessage.getBbgExecutionId());
    }
}
