package com.cpb.omsservice.listener;

import com.alibaba.fastjson2.JSONObject;
import com.cpb.omsservice.dto.BBGExecutionMessage;
import com.cpb.omsservice.dto.ExecutionMessage;
import com.cpb.omsservice.dto.PTBMessage;
import com.cpb.omsservice.entity.BBGExecutionDetail;
import com.cpb.omsservice.entity.TPS2OrderDetail;
import com.cpb.omsservice.service.BBGExecutionService;
import com.cpb.omsservice.service.TPS2OrderService;
import com.cpb.omsservice.service.impl.MessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RocketMQMessageListener(topic = "BBG_EXECUTION_TOPIC", consumerGroup = "oms-execute-consumer-group")
public class BBGExecutionListener implements RocketMQListener<BBGExecutionMessage> {
    @Autowired
    private BBGExecutionService bbgExecutionService;
    @Autowired
    private TPS2OrderService tps2OrderService;
    @Autowired
    private MessageProducer messageProducer;

    @Override
    public void onMessage(BBGExecutionMessage bbgExecutionMessage) {
        log.info("bbg execution 收到消息：{}", JSONObject.toJSONString(bbgExecutionMessage));
        //TODO save es

        //save DB
        BBGExecutionDetail bbgExecutionDetail = bbgExecutionService.saveBBGExecutionDetail(bbgExecutionMessage);

        //send to TL
        ExecutionMessage executionMessage = this.getExecutionMessage(bbgExecutionDetail, bbgExecutionMessage);
        messageProducer.sendExecutionMessage(executionMessage);

        //send to PTB
        if (bbgExecutionMessage.getUniqueId() != null) {
            //live order, 可以直接调用PTB
            PTBMessage ptbMessage = this.getPTBMessage(bbgExecutionDetail);
            messageProducer.sendPTBMessage(ptbMessage);
        }
        log.info("bbg execution end, {}", bbgExecutionDetail.getBbgExecutionId());
    }

    private ExecutionMessage getExecutionMessage(BBGExecutionDetail bbgExecutionDetail,
                                                 BBGExecutionMessage bbgExecutionMessage) {
        ExecutionMessage executionMessage = new ExecutionMessage();
        executionMessage.setBbgExecutionId(bbgExecutionDetail.getBbgMessageId());
        executionMessage.setTradeLinkId(bbgExecutionDetail.getTradeLinkId());
        executionMessage.setAccountKey(bbgExecutionDetail.getAccountKey());
        executionMessage.setISIN(bbgExecutionMessage.getISIN());
        executionMessage.setISINName(bbgExecutionMessage.getISINName());
        executionMessage.setExchangeCode(bbgExecutionMessage.getExchangeCode());
        executionMessage.setExecutedQuantity(bbgExecutionMessage.getExecutedQuantity());
        executionMessage.setExecutedPrice(bbgExecutionMessage.getExecutedPrice());
        return executionMessage;
    }

    private PTBMessage getPTBMessage(BBGExecutionDetail bbgExecutionDetail) {
        PTBMessage ptbMessage = new PTBMessage();
        ptbMessage.setBbgExecutionId(bbgExecutionDetail.getBbgExecutionId());
        ptbMessage.setAccountKey(bbgExecutionDetail.getAccountKey());

        TPS2OrderDetail tps2OrderDetail = tps2OrderService.getOrderByTradeLink(bbgExecutionDetail.getTradeLinkId());
        if (tps2OrderDetail != null) {
            ptbMessage.setAccountName(tps2OrderDetail.getAccountName());
            ptbMessage.setMemberKey(tps2OrderDetail.getMemberKey());
            ptbMessage.setMemberName(tps2OrderDetail.getMemberName());
            ptbMessage.setCashAccount(tps2OrderDetail.getCashAccount());
            ptbMessage.setISIN(tps2OrderDetail.getIsin());
        }
        ptbMessage.setExecutedQuantity(bbgExecutionDetail.getExecutedQuantity());
        ptbMessage.setExecutedPrice(bbgExecutionDetail.getExecutedPrice());
        return ptbMessage;
    }
}
