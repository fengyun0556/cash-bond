package com.cpb.oms.application.service;

import com.cpb.oms.application.builder.TradingBuilder;
import com.cpb.oms.domain.model.trading.TradingInstruction;
import com.cpb.oms.domain.repository.TradingInstructionRepository;
import com.cpb.oms.domain.event.TradeSubmissionEvent;
import com.cpb.oms.domain.model.trading.BBGAckMessage;
import com.cpb.oms.interfaces.trading.OrderCreationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TradingApplicationService {

    @Autowired
    private TradingBuilder tradingBuilder;
    @Autowired
    private TradingInstructionRepository tradingInstructionRepository;
    @Autowired
    private MessageSender messageSender;


    public TradingInstruction submitOrder(OrderCreationRequest orderCreationRequest) {
        //build TradingInstruction
        TradingInstruction tradingInstruction = tradingBuilder.buildTradingInstruction(orderCreationRequest);
        tradingInstruction.init();

        //publish domain event to fix engine
        TradeSubmissionEvent tradeSubmissionEvent = tradingInstruction.createTradeSubmissionEvent();
        messageSender.send(tradeSubmissionEvent);

        //save TradingInstruction
        tradingInstructionRepository.save(tradingInstruction);

        return tradingInstruction;
    }

    public void ack(BBGAckMessage bbgAckMessage) {
        TradingInstruction tradingInstruction = tradingInstructionRepository
                .getTradingInstructionByTradeLinkId(bbgAckMessage.getTradeLinkId());
        tradingInstruction.bbgAck();
        tradingInstructionRepository.save(tradingInstruction);
    }
}
