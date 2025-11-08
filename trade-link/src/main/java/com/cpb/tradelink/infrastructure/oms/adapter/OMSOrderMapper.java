package com.cpb.tradelink.infrastructure.oms.adapter;

import com.cpb.tradelink.domain.model.Order;
import com.cpb.tradelink.infrastructure.oms.dto.OMSOrderCreationRequest;
import org.springframework.stereotype.Component;

@Component
public class OMSOrderMapper {


    public OMSOrderCreationRequest toOMSOrderCreationRequest(Order order) {
        return null;
    }
}
