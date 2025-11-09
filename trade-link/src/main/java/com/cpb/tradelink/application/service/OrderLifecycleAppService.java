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
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
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

    public Order createOrder(OrderCreationRequest orderCreationRequest) {
        log.info("开始创建订单, requestMode={}", orderCreationRequest.getOrderRequestMode());

        try {
            //1. get domain model
            log.debug("构建订单领域模型");
            Order order = orderBuilder.buildFromOrderCreationRequest(orderCreationRequest);
            log.debug("订单领域模型构建完成, orderId={}", order.getOrderId());

            //2. calculate commission
            log.debug("计算佣金");
            order.calculateCommission();
            log.debug("佣金计算完成, orderId={}", order.getOrderId());

            //3. save order and ruleCheck
            order.initForNew();
            orderRepository.save(order);
            log.info("订单初始化并保存完成, orderId={}, status={}", order.getOrderId(), order.getOrderState());

            if (OrderRequestMode.LIVE.equals(order.getOrderRequestMode())) {
                log.info("处理LIVE模式订单, 开始提交到OMS, orderId={}", order.getOrderId());
                //4. call OMS
                OmsSubmissionResult omsSubmissionResult = omsIntegrationService.submitToOms(order);
                log.info("OMS提交完成, orderId={}, success={}, tps2Id={}",
                        order.getOrderId(), omsSubmissionResult.getSuccess(), omsSubmissionResult.getTps2Id());

                //5. save OmsSubmissionResult
                orderService.saveOmsSubmissionResult(order, omsSubmissionResult);
                log.info("LIVE模式订单创建完成, orderId={}", order.getOrderId());

            } else if (OrderRequestMode.MEMO.equals(order.getOrderRequestMode())) {
                log.info("处理MEMO模式订单, 发送MEMO订单创建事件, orderId={}", order.getOrderId());
                MEMOOrderCreatedEvent memoOrderCreatedEvent = order.createMEMOOrderCreatedEvent();
                messageSender.send(memoOrderCreatedEvent);
                log.info("MEMO模式订单创建完成, orderId={}", order.getOrderId());
            }

            log.info("订单创建流程完成, orderId={}", order.getOrderId());
            return order;

        } catch (Exception e) {
            log.error("创建订单失败, 错误信息: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void enrichOrder(OrderEnrichRequest orderEnrichRequest) {
        log.info("开始 enrichment 订单, orderId={}", orderEnrichRequest.getOrderId());

        try {
            Order order = orderRepository.findById(orderEnrichRequest.getOrderId(), false, false);
            if (order == null) {
                log.error("未找到要 enrichment 的订单, orderId={}", orderEnrichRequest.getOrderId());
                throw new RuntimeException("Order not found for enrichment: " + orderEnrichRequest.getOrderId());
            }
            log.debug("找到订单, orderId={}, 当前状态={}", order.getOrderId(), order.getOrderState());

            // 更新现金账户数据
            CashAccountData cashAccountData = order.getCashAccountData();
            cashAccountData.setCashAccount(orderEnrichRequest.getCashAccount());
            cashAccountData.setCurrency(orderEnrichRequest.getCashAccountCurrency());
            log.debug("现金账户数据已更新, orderId={}, cashAccount={}",
                    order.getOrderId(), orderEnrichRequest.getCashAccount());

            // 更新ISIN数据
            ISINData isinData = order.getIsinData();
            isinData.setIsinType(orderEnrichRequest.getIsinType());
            isinData.setIsinName(orderEnrichRequest.getIsinName());
            log.debug("ISIN数据已更新, orderId={}, isinType={}",
                    order.getOrderId(), orderEnrichRequest.getIsinType());

            // 更新佣金数据并重新计算
            CommissionData commissionData = order.getCommissionData();
            commissionData.setCommissionRate(orderEnrichRequest.getCommissionRate());
            commissionData.setCommissionType(orderEnrichRequest.getCommissionType());
            order.calculateCommission();
            log.debug("佣金数据已更新并重新计算, orderId={}, commissionRate={}",
                    order.getOrderId(), orderEnrichRequest.getCommissionRate());

            order.enrich();
            log.info("订单 enrichment 状态更新完成, orderId={}", order.getOrderId());

            orderRepository.save(order);
            log.debug("订单数据已保存, orderId={}", order.getOrderId());

            OrderEnrichedEvent orderEnrichedEvent = order.createOrderEnrichedEvent();
            messageSender.send(orderEnrichedEvent);
            log.info("订单 enrichment 事件已发送, orderId={}", order.getOrderId());

        } catch (Exception e) {
            log.error("enrichment 订单失败, orderId={}, 错误信息: {}",
                    orderEnrichRequest.getOrderId(), e.getMessage(), e);
            throw e;
        }
    }

    public void amendOrder(OrderAmendRequest orderAmendRequest) {
        log.info("开始修改订单, orderId={}", orderAmendRequest.getOrderId());

        try {
            Order order = orderRepository.findById(orderAmendRequest.getOrderId(), false, false);
            if (order == null) {
                log.error("未找到要修改的订单, orderId={}", orderAmendRequest.getOrderId());
                throw new RuntimeException("Order not found for amendment: " + orderAmendRequest.getOrderId());
            }
            log.debug("找到订单, orderId={}, 当前状态={}", order.getOrderId(), order.getOrderState());

            // 更新账户数据
            AccountData accountData = order.getAccountData();
            accountData.setAccountKey(orderAmendRequest.getAccountKey());
            accountData.setAccountName(orderAmendRequest.getAccountName());
            log.debug("账户数据已更新, orderId={}, accountKey={}",
                    order.getOrderId(), orderAmendRequest.getAccountKey());

            // 更新成员数据
            MemberData memberData = order.getMemberData();
            memberData.setMemberKey(orderAmendRequest.getMemberKey());
            memberData.setMemberName(orderAmendRequest.getMemberName());
            log.debug("成员数据已更新, orderId={}, memberKey={}",
                    order.getOrderId(), orderAmendRequest.getMemberKey());

            // 更新现金账户数据
            CashAccountData cashAccountData = order.getCashAccountData();
            cashAccountData.setCashAccount(orderAmendRequest.getCashAccount());
            cashAccountData.setCurrency(orderAmendRequest.getCashAccountCurrency());
            log.debug("现金账户数据已更新, orderId={}, cashAccount={}",
                    order.getOrderId(), orderAmendRequest.getCashAccount());

            // 更新ISIN数据
            ISINData isinData = order.getIsinData();
            isinData.setIsinType(orderAmendRequest.getIsinType());
            log.debug("ISIN数据已更新, orderId={}, isinType={}",
                    order.getOrderId(), orderAmendRequest.getIsinType());

            // 更新佣金数据并重新计算
            CommissionData commissionData = order.getCommissionData();
            commissionData.setCommissionRate(orderAmendRequest.getCommissionRate());
            commissionData.setCommissionType(orderAmendRequest.getCommissionType());
            order.calculateCommission();
            log.debug("佣金数据已更新并重新计算, orderId={}, commissionRate={}",
                    order.getOrderId(), orderAmendRequest.getCommissionRate());

            order.amend();
            log.info("订单修改状态更新完成, orderId={}", order.getOrderId());

            orderRepository.save(order);
            log.debug("订单数据已保存, orderId={}", order.getOrderId());

            OrderAmendedEvent orderAmendedEvent = order.createOrderAmendedEvent();
            messageSender.send(orderAmendedEvent);
            log.info("订单修改事件已发送, orderId={}", order.getOrderId());

        } catch (Exception e) {
            log.error("修改订单失败, orderId={}, 错误信息: {}",
                    orderAmendRequest.getOrderId(), e.getMessage(), e);
            throw e;
        }
    }

    public void approveOrder(OrderApproveRequest orderApproveRequest) {
        log.info("开始审批订单, orderId={}", orderApproveRequest.getOrderId());

        try {
            Order order = orderRepository.findById(orderApproveRequest.getOrderId(), false, false);
            if (order == null) {
                log.error("未找到要审批的订单, orderId={}", orderApproveRequest.getOrderId());
                throw new RuntimeException("Order not found for approval: " + orderApproveRequest.getOrderId());
            }
            log.debug("找到订单, orderId={}, 当前状态={}", order.getOrderId(), order.getOrderState());

            order.approve();
            log.info("订单审批状态更新完成, orderId={}", order.getOrderId());

            orderRepository.save(order);
            log.debug("订单数据已保存, orderId={}", order.getOrderId());

            emailService.orderApproved(order);
            log.info("订单审批邮件已发送, orderId={}", order.getOrderId());

        } catch (Exception e) {
            log.error("审批订单失败, orderId={}, 错误信息: {}",
                    orderApproveRequest.getOrderId(), e.getMessage(), e);
            throw e;
        }
    }

    public Order getOrder(Long orderId) {
        log.debug("查询订单详情, orderId={}, withRelations=true", orderId);
        Order order = orderRepository.findById(orderId, true, true);
        if (order == null) {
            log.warn("未找到订单, orderId={}", orderId);
        } else {
            log.debug("订单查询完成, orderId={}, status={}", order.getOrderId(), order.getOrderState());
        }
        return order;
    }

    public void orderExecuted(ExecutionMessage executionMessage) {
        log.info("开始处理订单执行消息, tradeLinkId={}, executionId={}",
                executionMessage.getTradeLinkId(), executionMessage.getTps2ExecutionId());

        try {
            if (executionMessage.getTradeLinkId() != null) {
                log.info("处理LIVE订单执行, tradeLinkId={}", executionMessage.getTradeLinkId());
                //live order
                Order order = orderRepository.findById(executionMessage.getTradeLinkId(), false, true);
                if (order == null) {
                    log.error("未找到对应的LIVE订单, tradeLinkId={}", executionMessage.getTradeLinkId());
                    throw new RuntimeException("Live order not found for tradeLinkId: " + executionMessage.getTradeLinkId());
                }
                log.debug("找到LIVE订单, orderId={}, 当前状态={}", order.getOrderId(), order.getOrderState());

                List<ExecutionRecord> executionRecordList = order.getExecutionRecordList();
                if (CollectionUtils.isEmpty(executionRecordList)) {
                    executionRecordList = new ArrayList<>();
                    order.setExecutionRecordList(executionRecordList);
                    log.debug("初始化执行记录列表, orderId={}", order.getOrderId());
                }
                ExecutionRecord executionRecord = executionMessage.createExecutionRecord();
                executionRecordList.add(executionRecord);
                log.debug("添加执行记录, orderId={}, executionId={}",
                        order.getOrderId(), executionMessage.getTps2ExecutionId());

                order.executed();
                log.info("LIVE订单执行状态更新完成, orderId={}", order.getOrderId());

                orderRepository.save(order);
                log.debug("LIVE订单数据已保存, orderId={}", order.getOrderId());

                emailService.orderExecuted(order);
                log.info("LIVE订单执行邮件已发送, orderId={}", order.getOrderId());

            } else {
                log.info("创建电话订单, executionId={}", executionMessage.getTps2ExecutionId());
                //create phone order
                Order order = executionMessage.createPhoneOrder();
                log.debug("电话订单创建完成, orderId={}", order.getOrderId());

                order.initForPhone();
                log.debug("电话订单初始化完成, orderId={}", order.getOrderId());

                orderRepository.save(order);
                log.debug("电话订单数据已保存, orderId={}", order.getOrderId());

                emailService.orderEnrich(order);
                log.info("电话订单 enrichment 邮件已发送, orderId={}", order.getOrderId());
            }

            log.info("订单执行消息处理完成");

        } catch (Exception e) {
            log.error("处理订单执行消息失败, tradeLinkId={}, executionId={}, 错误信息: {}",
                    executionMessage.getTradeLinkId(), executionMessage.getTps2ExecutionId(), e.getMessage(), e);
            throw e;
        }
    }
}