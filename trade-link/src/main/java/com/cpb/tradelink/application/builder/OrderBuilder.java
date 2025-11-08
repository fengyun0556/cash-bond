package com.cpb.tradelink.application.builder;

import com.cpb.tradelink.domain.model.*;
import com.cpb.tradelink.interfaces.rest.request.OrderCreationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderBuilder {

    public Order buildFromOrderCreationRequest(OrderCreationRequest orderCreationRequest) {
        Order order = new Order();

        AccountData accountData = this.createAccountData(orderCreationRequest);
        order.setAccountData(accountData);

        MemberData memberData = this.createMemberData(orderCreationRequest);
        order.setMemberData(memberData);

        ISINData isinData = this.getISINData(orderCreationRequest);
        order.setIsinData(isinData);

        order.setExchangeCode(orderCreationRequest.getExchangeCode());

        order.setOrderType(orderCreationRequest.getOrderType());

        QuantityData quantityData = this.createQuantityData(orderCreationRequest);
        order.setQuantityData(quantityData);

        order.setPrice(orderCreationRequest.getPrice());

        CommissionData commissionData = this.getCommissionData(orderCreationRequest);
        order.setCommissionData(commissionData);

        order.setOrderRequestMode(orderCreationRequest.getOrderRequestMode());

        List<RuleCheck> ruleCheckList = this.getRuleCheckList(orderCreationRequest);
        order.setRuleCheckList(ruleCheckList);

        return order;
    }

    private AccountData createAccountData(OrderCreationRequest orderCreationRequest) {
        AccountData accountData = new AccountData();
        accountData.setAccountKey(orderCreationRequest.getAccountKey());
        accountData.setAccountName(orderCreationRequest.getAccountName());
        return accountData;
    }

    private MemberData createMemberData(OrderCreationRequest orderCreationRequest) {
        MemberData memberData = new MemberData();
        memberData.setMemberKey(orderCreationRequest.getMemberKey());
        memberData.setMemberName(orderCreationRequest.getMemberName());
        return memberData;
    }

    private ISINData getISINData(OrderCreationRequest orderCreationRequest) {
        ISINData isinData = new ISINData();
        isinData.setIsin(orderCreationRequest.getIsin());
        isinData.setIsinName(orderCreationRequest.getIsinName());
        isinData.setIsinType(orderCreationRequest.getIsinType());
        return isinData;
    }

    private QuantityData createQuantityData(OrderCreationRequest orderCreationRequest) {
        QuantityData quantityData = new QuantityData();
        quantityData.setQuantity(orderCreationRequest.getQuantity());
        return quantityData;
    }

    private CommissionData getCommissionData(OrderCreationRequest orderCreationRequest) {
        CommissionData commissionData = new CommissionData();
        commissionData.setCommissionRate(orderCreationRequest.getCommissionRate());
        commissionData.setCommissionType(orderCreationRequest.getCommissionType());
        return commissionData;
    }

    private List<RuleCheck> getRuleCheckList(OrderCreationRequest orderCreationRequest) {
        if (!CollectionUtils.isEmpty(orderCreationRequest.getRuleCheckRequestList())) {
            return orderCreationRequest.getRuleCheckRequestList().stream().map(ruleCheckRequest -> {
                RuleCheck ruleCheck = new RuleCheck();
                ruleCheck.setRuleCode(ruleCheckRequest.getRuleCode());
                ruleCheck.setRuleCheckResult(ruleCheckRequest.getRuleCheckResult());
                ruleCheck.setRuleDescribe(ruleCheckRequest.getRuleDescribe());
                return ruleCheck;
            }).collect(Collectors.toList());
        } else return null;
    }
}
