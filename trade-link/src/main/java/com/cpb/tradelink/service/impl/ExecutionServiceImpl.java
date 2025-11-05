package com.cpb.tradelink.service.impl;

import com.cpb.tradelink.dto.ExecutionMessage;
import com.cpb.tradelink.dto.EmailRequest;
import com.cpb.tradelink.entity.OrderDetail;
import com.cpb.tradelink.entity.OrderExecutionDetail;
import com.cpb.tradelink.enums.OrderRequestMode;
import com.cpb.tradelink.enums.OrderState;
import com.cpb.tradelink.repository.OrderDetailRepository;
import com.cpb.tradelink.repository.OrderExecutionDetailRepository;
import com.cpb.tradelink.service.ExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class ExecutionServiceImpl implements ExecutionService {

    @Autowired
    private MessageProducer messageProducer;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private OrderExecutionDetailRepository orderExecutionDetailRepository;

    @Override
    public void execute(ExecutionMessage executionMessage) {
        //判断是live order，还是phone order
        if (executionMessage.getTradeLinkId() == null) {
            //phone order，create之后发邮件
            log.info("this is phone order, trade link id: {}", executionMessage.getTradeLinkId());
            OrderDetail orderDetail = this.saveOrderDetail(executionMessage);
            this.sendEnrichmentEmail(orderDetail);
        } else {
            //live order
            log.info("this is live order, trade link id: {}", executionMessage.getTradeLinkId());
            Optional<OrderDetail> optionalOrderDetail = orderDetailRepository.findById(executionMessage.getTradeLinkId());
            if (optionalOrderDetail.isEmpty()) {
                log.warn("trade link id {} not found", executionMessage.getTradeLinkId());
                return;
            }
            OrderDetail orderDetail = optionalOrderDetail.get();

            //save execution record 判断是不是全部执行
            if (orderDetail.getQuantity() - orderDetail.getTotalExecutedQuantity() > executionMessage.getExecutedQuantity()) {
                //部分执行
                orderDetail.setOrderState(OrderState.PARTIAL_EXECUTED);
                orderDetail.setTotalExecutedQuantity(orderDetail.getTotalExecutedQuantity() + executionMessage.getExecutedQuantity());
            } else {
                //全部执行
                orderDetail.setOrderState(OrderState.FULL_EXECUTED);
                orderDetail.setTotalExecutedQuantity(orderDetail.getQuantity());
            }
            orderDetail.setUpdateTime(LocalDateTime.now());
            orderDetailRepository.save(orderDetail);
            log.info("live order update order detail success, order id: {}", orderDetail.getOrderId());

            this.saveOrderExecutionDetail(executionMessage);
            this.sendConfirmEmail(orderDetail);
            log.info("execution end, tradeLinkId: {}", orderDetail.getOrderId());
        }
    }

    private OrderDetail saveOrderDetail(ExecutionMessage executionMessage) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setAccountKey(executionMessage.getAccountKey());
        orderDetail.setIsin(executionMessage.getISIN());
        orderDetail.setIsinName(executionMessage.getISINName());
        orderDetail.setExchangeCode(executionMessage.getExchangeCode());
        orderDetail.setOrderType(executionMessage.getOrderType());
        orderDetail.setQuantity(executionMessage.getExecutedQuantity());
        orderDetail.setPrice(executionMessage.getExecutedPrice());
        orderDetail.setOrderState(OrderState.ENRICHMENT);
        orderDetail.setOrderRequestMode(OrderRequestMode.PHONE);
        orderDetail.setCreateTime(LocalDateTime.now());
        this.orderDetailRepository.save(orderDetail);
        log.info("order detail save success, order id: {}", orderDetail.getOrderId());
        return orderDetail;
    }

    private void sendEnrichmentEmail(OrderDetail orderDetail) {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setId(orderDetail.getOrderId());
        emailRequest.setEmailTemplateId("enrichment");
        messageProducer.sendMessage(emailRequest);
    }

    private void sendConfirmEmail(OrderDetail orderDetail) {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setId(orderDetail.getOrderId());
        emailRequest.setEmailTemplateId("confirm");
        messageProducer.sendMessage(emailRequest);
    }

    private void saveOrderExecutionDetail(ExecutionMessage executionMessage) {
        OrderExecutionDetail orderExecutionDetail = new OrderExecutionDetail();
        orderExecutionDetail.setBbgExecutionId(executionMessage.getBbgExecutionId());
        orderExecutionDetail.setTradeLinkId(executionMessage.getTradeLinkId());
        orderExecutionDetail.setExecutedPrice(executionMessage.getExecutedPrice());
        orderExecutionDetail.setExecutedQuantity(executionMessage.getExecutedQuantity());
        orderExecutionDetail.setCreateTime(LocalDateTime.now());
        orderExecutionDetailRepository.save(orderExecutionDetail);
        log.info("save order execution detail success, trade link id: {}", executionMessage.getTradeLinkId());
    }
}
