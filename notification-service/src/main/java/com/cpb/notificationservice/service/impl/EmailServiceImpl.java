package com.cpb.notificationservice.service.impl;

import com.cpb.notificationservice.dto.EmailRequest;
import com.cpb.notificationservice.service.EmailService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    @SneakyThrows
    @Override
    public void send(EmailRequest emailRequest) {
        int min = 200;
        int max = 1000;
        Random random = new Random();
        // 生成 [200, 1000] 之间的随机数
        int time = random.nextInt(max - min + 1) + min;
        Thread.sleep(time);

        log.info("邮件发送成功，emailId: {}", emailRequest.getId());
    }
}
