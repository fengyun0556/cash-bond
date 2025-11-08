package com.cpb.oms.application.builder;

import com.cpb.oms.domain.model.trading.TradingInstruction;
import com.cpb.oms.interfaces.trading.OrderCreationRequest;
import org.springframework.stereotype.Component;

@Component
public class TradingBuilder {

    public TradingInstruction buildTradingInstruction(OrderCreationRequest orderCreationRequest) {
        TradingInstruction tradingInstruction = new TradingInstruction();
        tradingInstruction.setTradeLinkId(orderCreationRequest.getOrderId());
        tradingInstruction.setAccountKey(orderCreationRequest.getAccountKey());
        tradingInstruction.setAccountName(orderCreationRequest.getAccountName());
        tradingInstruction.setMemberKey(orderCreationRequest.getMemberKey());
        tradingInstruction.setMemberName(orderCreationRequest.getMemberName());
        tradingInstruction.setCashAccount(orderCreationRequest.getCashAccount());
        tradingInstruction.setISIN(orderCreationRequest.getISIN());
        tradingInstruction.setISINName(orderCreationRequest.getISINName());
        tradingInstruction.setExchangeCode(orderCreationRequest.getExchangeCode());
        tradingInstruction.setQuantity(orderCreationRequest.getQuantity());
        tradingInstruction.setPrice(orderCreationRequest.getPrice());
        return tradingInstruction;
    }
}
