package com.cpb.fixengine.service;

import com.cpb.fixengine.dto.BBGAckMessage;
import com.cpb.fixengine.dto.BBGExecutionMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class BBGService {
    @Autowired
    private MessageProducer messageProducer;

    public void handleBBGAck(String[] values) {
        BBGAckMessage bbgAckMessage = new BBGAckMessage();
        for (String value : values) {
            if (value.startsWith("1=")) {//1=tradeLinkId
                String[] strings = value.split("=");
                bbgAckMessage.setTradeLinkId(Long.valueOf(strings[1]));
            } else if (value.startsWith("22=")) {//22=ackSuccess
                String[] strings = value.split("=");
                bbgAckMessage.setAckSuccess(Boolean.valueOf(strings[1]));
            }
        }
        messageProducer.sendBBGAckMessage(bbgAckMessage);
    }

    public void handleBBGExecution(String[] values) {
        BBGExecutionMessage bbgExecutionMessage = new BBGExecutionMessage();
        for (String value : values) {
            if (value.startsWith("1=")) {//1=tradeLinkId
                String[] strings = value.split("=");
                bbgExecutionMessage.setUniqueId(Long.valueOf(strings[1]));
            } else if (value.startsWith("19=")) {//19=bbgMessageId
                String[] strings = value.split("=");
                bbgExecutionMessage.setBbgMessageId(strings[1]);
            } else if (value.startsWith("22=")) {//22=accountKey
                String[] strings = value.split("=");
                bbgExecutionMessage.setAccountKey(strings[1]);
            } else if (value.startsWith("2=")) {//2=ISIN
                String[] strings = value.split("=");
                bbgExecutionMessage.setISIN(strings[1]);
            } else if (value.startsWith("3=")) {//3=ISINName
                String[] strings = value.split("=");
                bbgExecutionMessage.setISINName(strings[1]);
            } else if (value.startsWith("4=")) {//4=exchangeCode
                String[] strings = value.split("=");
                bbgExecutionMessage.setExchangeCode(strings[1]);
            } else if (value.startsWith("26=")) {//26=executedPrice
                String[] strings = value.split("=");
                bbgExecutionMessage.setExecutedPrice(new BigDecimal(strings[1]));
            } else if (value.startsWith("27=")) {//27=executedQuantity
                String[] strings = value.split("=");
                bbgExecutionMessage.setExecutedQuantity(Integer.valueOf(strings[1]));
            }
        }

        messageProducer.sendBBGExecutionMessage(bbgExecutionMessage);
    }
}
