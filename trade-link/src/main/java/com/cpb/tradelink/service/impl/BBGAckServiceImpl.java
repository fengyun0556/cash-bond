package com.cpb.tradelink.service.impl;

import com.cpb.tradelink.dto.BBGAckMessage;
import com.cpb.tradelink.entity.OrderDetail;
import com.cpb.tradelink.enums.OrderState;
import com.cpb.tradelink.repository.OrderDetailRepository;
import com.cpb.tradelink.service.BBGAckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class BBGAckServiceImpl implements BBGAckService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    public void ack(BBGAckMessage bbgAckMessage) {
        Optional<OrderDetail> optionalOrderDetail = orderDetailRepository.findById(bbgAckMessage.getTradeLinkId());
        if (optionalOrderDetail.isEmpty()) {
            log.warn("trade link id {} not found", bbgAckMessage.getTradeLinkId());
            return;
        }
        OrderDetail orderDetail = optionalOrderDetail.get();
        if (Boolean.TRUE.equals(bbgAckMessage.getAckSuccess())) orderDetail.setOrderState(OrderState.BBG_ACK);
        else orderDetail.setOrderState(OrderState.FAILED);

        orderDetailRepository.save(orderDetail);
        log.info("handle bbg ack end, save DB success, {}", orderDetail.getOrderId());
    }
}
