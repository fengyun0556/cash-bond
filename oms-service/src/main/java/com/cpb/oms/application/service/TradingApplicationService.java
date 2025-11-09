package com.cpb.oms.application.service;

import com.cpb.oms.application.builder.TradingBuilder;
import com.cpb.oms.domain.model.trading.TradingInstruction;
import com.cpb.oms.domain.repository.TradingInstructionRepository;
import com.cpb.oms.domain.event.TradeSubmissionEvent;
import com.cpb.oms.domain.model.trading.BBGAckMessage;
import com.cpb.oms.interfaces.trading.OrderCreationRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Transactional
public class TradingApplicationService {

    @Autowired
    private TradingBuilder tradingBuilder;
    @Autowired
    private TradingInstructionRepository tradingInstructionRepository;
    @Autowired
    private MessageSender messageSender;

    public TradingInstruction submitOrder(OrderCreationRequest orderCreationRequest) {
        log.info("开始提交订单，请求参数: orderId={}, quantity={}",
                orderCreationRequest.getOrderId(),
                orderCreationRequest.getQuantity());

        try {
            //build TradingInstruction
            TradingInstruction tradingInstruction = tradingBuilder.buildTradingInstruction(orderCreationRequest);
            log.debug("交易指令构建完成，tradeLinkId={}", tradingInstruction.getTradeLinkId());

            tradingInstruction.init();
            log.info("交易指令初始化完成，tradeLinkId={}, status={}",
                    tradingInstruction.getTradeLinkId(), tradingInstruction.getTradeState());

            //publish domain event to fix engine
            TradeSubmissionEvent tradeSubmissionEvent = tradingInstruction.createTradeSubmissionEvent();
            messageSender.send(tradeSubmissionEvent);
            log.info("交易提交事件已发送，tradeLinkId={}", tradingInstruction.getTradeLinkId());

            //save TradingInstruction
            tradingInstructionRepository.save(tradingInstruction);
            log.info("订单提交流程完成，tradeLinkId={}, orderId={}",
                    tradingInstruction.getTradeLinkId(), orderCreationRequest.getOrderId());

            return tradingInstruction;

        } catch (Exception e) {
            log.error("提交订单失败，orderId={}, 错误信息: {}",
                    orderCreationRequest.getOrderId(), e.getMessage(), e);
            throw e;
        }
    }

    public void ack(BBGAckMessage bbgAckMessage) {
        log.info("开始处理 Bloomberg ACK 消息，tradeLinkId={}", bbgAckMessage.getTradeLinkId());

        try {
            TradingInstruction tradingInstruction = tradingInstructionRepository
                    .getTradingInstructionByTradeLinkId(bbgAckMessage.getTradeLinkId());

            if (tradingInstruction == null) {
                log.error("未找到对应的交易指令，tradeLinkId={}", bbgAckMessage.getTradeLinkId());
                throw new RuntimeException("TradingInstruction not found for tradeLinkId: " + bbgAckMessage.getTradeLinkId());
            }

            log.debug("找到交易指令: tradeLinkId={}, 当前状态={}",
                    tradingInstruction.getTradeLinkId(), tradingInstruction.getTradeState());

            tradingInstruction.bbgAck();
            log.info("Bloomberg ACK 处理完成，tradeLinkId={}, 新状态={}",
                    bbgAckMessage.getTradeLinkId(), tradingInstruction.getTradeState());

            tradingInstructionRepository.save(tradingInstruction);
            log.info("交易指令状态已更新，tradeLinkId={}", bbgAckMessage.getTradeLinkId());

        } catch (Exception e) {
            log.error("处理 Bloomberg ACK 消息失败，tradeLinkId={}, 错误信息: {}",
                    bbgAckMessage.getTradeLinkId(), e.getMessage(), e);
            throw e;
        }
    }
}