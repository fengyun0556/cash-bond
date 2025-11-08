package com.cpb.tradelink.domain.service;

import com.cpb.tradelink.domain.model.OmsSubmissionResult;
import com.cpb.tradelink.domain.model.Order;
import com.cpb.tradelink.domain.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public void saveOmsSubmissionResult(Order order, OmsSubmissionResult omsSubmissionResult) {
        if (omsSubmissionResult.getSuccess()) {
            order.omsSubmissionSuccess(omsSubmissionResult.getTps2Id());
        } else {
            order.omsSubmissionFailed();
        }
        orderRepository.save(order);
    }

    public void bbgAck(Boolean ackSuccess, Long tradeLinkId) {
        Order order = orderRepository.findById(tradeLinkId, false, false);
        if (ackSuccess) order.bbgAckSuccess();
        else order.bbgAckFailed();
        orderRepository.save(order);
    }

    public void settlementFailed(Long orderId) {
        Order order = orderRepository.findById(orderId, false, false);
        order.settlementFailed();
        orderRepository.save(order);
    }
}
