package com.cpb.notificationservice.service;

import com.cpb.notificationservice.dto.EmailRequest;

public interface EmailService {

    void send(EmailRequest emailRequest);
}
