package com.bbg.service;

import com.bbg.config.TcpClientConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class BBGAckService {
    @Autowired
    private TcpClientConfig tcpClientConfig;
    @Autowired
    private BBGExecutionService bbgExecutionService;
    private static final Random random = new Random();

    @SneakyThrows
    @Async("ackExecutor")
    public void sendAckMessage(String message) {
        log.info("handle bbg ack, {}", message);
        String[] values = message.split("\\|");
        String uniqueId = null;
        for (String value : values) {
            if (value.startsWith("1=")) {
                String[] strings = value.split("=");
                uniqueId = strings[1];
            }
        }
        String ackMessage = "18=0|1=" + uniqueId + "|22=true";//tag 18: 0-ack; 1-execution
        tcpClientConfig.getChannel().writeAndFlush(ackMessage);

        // 生成300到1000之间的随机毫秒数
        int delay = 300 + random.nextInt(701);
        Thread.sleep(delay);
        bbgExecutionService.sendExecutionMessage(values);
    }
}
