package com.cpb.oms.interfaces.trading;

import com.cpb.oms.domain.model.trading.TradingInstruction;
import org.springframework.stereotype.Component;

@Component
public class TradingAssembler {
    public OrderCreationResponse buildOrderCreationResponse(TradingInstruction tradingInstruction) {
        OrderCreationResponse orderCreationResponse = new OrderCreationResponse();
        if (tradingInstruction.getTps2Id() != null) {
            orderCreationResponse.setSuccess(true);
            orderCreationResponse.setTps2Id(tradingInstruction.getTps2Id());
        } else orderCreationResponse.setSuccess(false);

        return orderCreationResponse;
    }
}
