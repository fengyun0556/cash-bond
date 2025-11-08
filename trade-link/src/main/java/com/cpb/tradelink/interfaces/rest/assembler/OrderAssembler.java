package com.cpb.tradelink.interfaces.rest.assembler;

import com.cpb.tradelink.domain.model.*;
import com.cpb.tradelink.interfaces.rest.response.ExecutionResponse;
import com.cpb.tradelink.interfaces.rest.response.OrderCreationResponse;
import com.cpb.tradelink.interfaces.rest.response.OrderDetailResponse;
import com.cpb.tradelink.interfaces.rest.response.RuleCheckResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderAssembler {
    public OrderCreationResponse toOrderCreationResponse(Order order) {
        OrderCreationResponse orderCreationResponse = new OrderCreationResponse();
        if (order.getOrderId() != null) {
            orderCreationResponse.setOrderId(String.valueOf(order.getOrderId()));
            orderCreationResponse.setSuccess(true);
        } else {
            orderCreationResponse.setSuccess(false);
        }
        return orderCreationResponse;
    }

    public OrderDetailResponse toOrderDetailResponse(Order order) {
        OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
        orderDetailResponse.setOrderId(order.getOrderId());

        AccountData accountData = order.getAccountData();
        orderDetailResponse.setAccountKey(accountData.getAccountKey());
        orderDetailResponse.setAccountName(accountData.getAccountName());

        MemberData memberData = order.getMemberData();
        orderDetailResponse.setMemberKey(memberData.getMemberKey());
        orderDetailResponse.setMemberName(memberData.getMemberName());

        CashAccountData cashAccountData = order.getCashAccountData();
        if (cashAccountData != null) {
            orderDetailResponse.setCashAccount(cashAccountData.getCashAccount());
            orderDetailResponse.setCashAccountCurrency(cashAccountData.getCurrency());
        }

        ISINData isinData = order.getIsinData();
        if (isinData != null) {
            orderDetailResponse.setIsin(isinData.getIsin());
            orderDetailResponse.setIsinName(isinData.getIsinName());
            orderDetailResponse.setIsinType(isinData.getIsinType());
        }

        orderDetailResponse.setExchangeCode(order.getExchangeCode());
        orderDetailResponse.setOrderType(order.getOrderType());

        QuantityData quantityData = order.getQuantityData();
        orderDetailResponse.setQuantity(quantityData.getQuantity());
        orderDetailResponse.setTotalExecutedQuantity(quantityData.getTotalExecutedQuantity());

        orderDetailResponse.setPrice(order.getPrice());

        CommissionData commissionData = order.getCommissionData();
        if (commissionData != null) {
            orderDetailResponse.setCommissionRate(commissionData.getCommissionRate());
            orderDetailResponse.setCommissionType(commissionData.getCommissionType());
            orderDetailResponse.setCommissionPrice(commissionData.getCommissionPrice());
        }

        orderDetailResponse.setOrderRequestMode(order.getOrderRequestMode());
        orderDetailResponse.setTps2Id(order.getTps2Id());
        orderDetailResponse.setOrderState(order.getOrderState());
        orderDetailResponse.setExecutionState(order.getExecutionState());

        if (!CollectionUtils.isEmpty(order.getRuleCheckList())) {
            List<RuleCheckResponse> ruleCheckResponseList = order.getRuleCheckList()
                    .stream().map(ruleCheck -> {
                        RuleCheckResponse ruleCheckResponse = new RuleCheckResponse();
                        ruleCheckResponse.setRuleCheckResult(ruleCheck.getRuleCheckResult());
                        ruleCheckResponse.setRuleCode(ruleCheck.getRuleCode());
                        ruleCheckResponse.setRuleDescribe(ruleCheck.getRuleDescribe());
                        return ruleCheckResponse;
                    }).collect(Collectors.toList());
            orderDetailResponse.setRuleCheckResponseList(ruleCheckResponseList);
        }

        if (!CollectionUtils.isEmpty(order.getExecutionRecordList())) {
            List<ExecutionResponse> executionRecordList = order.getExecutionRecordList()
                    .stream().map(executionRecord -> {
                        ExecutionResponse executionResponse = new ExecutionResponse();
                        executionResponse.setExecutedPrice(executionRecord.getExecutedPrice());
                        executionResponse.setExecutedQuantity(executionRecord.getExecutedQuantity());
                        return executionResponse;
                    }).collect(Collectors.toList());
            orderDetailResponse.setExecutionResponseList(executionRecordList);
        }

        return orderDetailResponse;
    }

}
