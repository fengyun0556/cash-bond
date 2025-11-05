package com.cpb.tradelink.service;

import com.cpb.tradelink.dto.OrderCreationRequest;

public interface CashBondService {

    String createOrder(OrderCreationRequest orderCreationRequest);
}
