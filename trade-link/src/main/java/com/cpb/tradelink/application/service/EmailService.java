package com.cpb.tradelink.application.service;

import com.cpb.tradelink.domain.model.Order;

public interface EmailService {
    void orderApproved(Order order);

    void orderExecuted(Order order);

    void orderEnrich(Order order);
}
