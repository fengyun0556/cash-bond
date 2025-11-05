package com.cpb.omsservice.service;

import com.cpb.omsservice.dto.BBGAckMessage;
import com.cpb.omsservice.dto.OMSOrderCreationRequest;
import com.cpb.omsservice.dto.OMSOrderCreationResponse;
import com.cpb.omsservice.entity.TPS2OrderDetail;

public interface TPS2OrderService {
    OMSOrderCreationResponse createOrder(OMSOrderCreationRequest omsOrderCreationRequest);

    void ack(BBGAckMessage bbgAckMessage);

    TPS2OrderDetail getOrderByTradeLink(Long tradeLinkId);
}
