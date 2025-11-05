package com.bbg.service;

import com.bbg.config.TcpClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
@Slf4j
public class BBGExecutionService {
    @Autowired
    private TcpClientConfig tcpClientConfig;
    private static final Random random = new Random();


    @Async("executionExecutor")
    public void sendExecutionMessage(String[] values) {
        log.info("handle execution, {}", values);
        StringBuilder executionMessage = new StringBuilder("18=1|");//tag 18: 0-ack; 1-execution
        String uniqueId = null, ISIN = null, ISINName = null, exchangeCode = null;
        Integer quantity = null;
        BigDecimal price = null;

        for (String value : values) {
            if (value.startsWith("1=")) {//1=UniqueId
                String[] strings = value.split("=");
                uniqueId = strings[1];
            } else if (value.startsWith("2=")) {//2=ISIN
                String[] strings = value.split("=");
                ISIN = strings[1];
            } else if (value.startsWith("3=")) {//3=ISINName
                String[] strings = value.split("=");
                ISINName = strings[1];
            } else if (value.startsWith("6=")) {//6=Quantity
                String[] strings = value.split("=");
                quantity = Integer.valueOf(strings[1]);
            } else if (value.startsWith("7=")) {//7=Price
                String[] strings = value.split("=");
                price = new BigDecimal(strings[1]);
            }
        }
        executionMessage.append("1=").append(uniqueId).append("|");
        executionMessage.append("19=")
                .append("BBG_")
                .append(System.currentTimeMillis())
                .append(random.nextInt(1000)).append("|");//bbgMessageId
        executionMessage.append("2=").append(ISIN).append("|");
        executionMessage.append("3=").append(ISINName).append("|");
        executionMessage.append("26=").append(price).append("|");
        executionMessage.append("27=").append(quantity);

        log.info("executionMessage: {}", executionMessage);
        tcpClientConfig.getChannel().writeAndFlush(executionMessage.toString());
    }
}
