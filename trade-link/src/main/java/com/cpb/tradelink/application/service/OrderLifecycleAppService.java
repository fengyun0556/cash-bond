package com.cpb.tradelink.application.service;

import com.cpb.tradelink.application.builder.OrderBuilder;
import com.cpb.tradelink.domain.enums.OrderRequestMode;
import com.cpb.tradelink.domain.event.MEMOOrderCreatedEvent;
import com.cpb.tradelink.domain.event.OrderAmendedEvent;
import com.cpb.tradelink.domain.event.OrderEnrichedEvent;
import com.cpb.tradelink.domain.model.*;
import com.cpb.tradelink.domain.event.ExecutionMessage;
import com.cpb.tradelink.domain.repository.OrderRepository;
import com.cpb.tradelink.domain.service.OmsIntegrationService;
import com.cpb.tradelink.domain.service.OrderService;
import com.cpb.tradelink.interfaces.rest.request.OrderAmendRequest;
import com.cpb.tradelink.interfaces.rest.request.OrderApproveRequest;
import com.cpb.tradelink.interfaces.rest.request.OrderCreationRequest;
import com.cpb.tradelink.interfaces.rest.request.OrderEnrichRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderLifecycleAppService {

    @Autowired
    private OrderBuilder orderBuilder;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OmsIntegrationService omsIntegrationService;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private EmailService emailService;

    public Order createOrder(OrderCreationRequest orderCreationRequest/*应该使用Command，简单场景直接用接口层的request了*/) {
        //1. get domain model
        Order order = orderBuilder.buildFromOrderCreationRequest(orderCreationRequest);

        //2. calculate commission
        order.calculateCommission();

        //3. save order and ruleCheck
        order.initForNew();
        orderRepository.save(order);

        if (OrderRequestMode.LIVE.equals(order.getOrderRequestMode())) {
            //4. call OMS
            OmsSubmissionResult omsSubmissionResult = omsIntegrationService.submitToOms(order);

            //5. save OmsSubmissionResult
            orderService.saveOmsSubmissionResult(order, omsSubmissionResult);
        } else if (OrderRequestMode.MEMO.equals(order.getOrderRequestMode())) {
            MEMOOrderCreatedEvent memoOrderCreatedEvent = order.createMEMOOrderCreatedEvent();
            messageSender.send(memoOrderCreatedEvent);
        }

        return order;
    }

    public void enrichOrder(OrderEnrichRequest orderEnrichRequest) {
        Order order = orderRepository.findById(orderEnrichRequest.getOrderId(), false, false);

        CashAccountData cashAccountData = order.getCashAccountData();
        cashAccountData.setCashAccount(orderEnrichRequest.getCashAccount());
        cashAccountData.setCurrency(orderEnrichRequest.getCashAccountCurrency());

        ISINData isinData = order.getIsinData();
        isinData.setIsinType(orderEnrichRequest.getIsinType());
        isinData.setIsinName(orderEnrichRequest.getIsinName());

        CommissionData commissionData = order.getCommissionData();
        commissionData.setCommissionRate(orderEnrichRequest.getCommissionRate());
        commissionData.setCommissionType(orderEnrichRequest.getCommissionType());
        order.calculateCommission();

        order.enrich();

        orderRepository.save(order);

        OrderEnrichedEvent orderEnrichedEvent = order.createOrderEnrichedEvent();
        messageSender.send(orderEnrichedEvent);
    }

    public void amendOrder(OrderAmendRequest orderAmendRequest) {
        Order order = orderRepository.findById(orderAmendRequest.getOrderId(), false, false);

        AccountData accountData = order.getAccountData();
        accountData.setAccountKey(orderAmendRequest.getAccountKey());
        accountData.setAccountName(orderAmendRequest.getAccountName());

        MemberData memberData = order.getMemberData();
        memberData.setMemberKey(orderAmendRequest.getMemberKey());
        memberData.setMemberName(orderAmendRequest.getMemberName());

        CashAccountData cashAccountData = order.getCashAccountData();
        cashAccountData.setCashAccount(orderAmendRequest.getCashAccount());
        cashAccountData.setCurrency(orderAmendRequest.getCashAccountCurrency());

        ISINData isinData = order.getIsinData();
        isinData.setIsinType(orderAmendRequest.getIsinType());

        CommissionData commissionData = order.getCommissionData();
        commissionData.setCommissionRate(orderAmendRequest.getCommissionRate());
        commissionData.setCommissionType(orderAmendRequest.getCommissionType());
        order.calculateCommission();

        order.amend();

        orderRepository.save(order);

        OrderAmendedEvent orderAmendedEvent = order.createOrderAmendedEvent();
        messageSender.send(orderAmendedEvent);
    }

    public void approveOrder(OrderApproveRequest orderApproveRequest) {
        Order order = orderRepository.findById(orderApproveRequest.getOrderId(), false, false);

        order.approve();

        orderRepository.save(order);

        emailService.orderApproved(order);
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId, true, true);
    }

    public void orderExecuted(ExecutionMessage executionMessage) {
        if (executionMessage.getTradeLinkId() != null) {
            //live order
            Order order = orderRepository.findById(executionMessage.getTradeLinkId(), false, true);

            List<ExecutionRecord> executionRecordList = order.getExecutionRecordList();
            if (CollectionUtils.isEmpty(executionRecordList)) {
                executionRecordList = new ArrayList<>();
                order.setExecutionRecordList(executionRecordList);
            }
            ExecutionRecord executionRecord = executionMessage.createExecutionRecord();
            executionRecordList.add(executionRecord);

            order.executed();

            orderRepository.save(order);

            emailService.orderExecuted(order);
        } else {
            //create phone order
            Order order = executionMessage.createPhoneOrder();

            order.initForPhone();

            orderRepository.save(order);

            emailService.orderEnrich(order);
        }

    }
}
