package com.cpb.tradelink.domain.repository;

import com.cpb.tradelink.domain.model.Order;

public interface OrderRepository {
    void save(Order order);

    Order findById(Long orderId, boolean ruleCheck, boolean execution);
}
