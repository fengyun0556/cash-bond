package com.cpb.tradelink.infrastructure.persistence.mapper;

import com.cpb.tradelink.domain.model.*;
import com.cpb.tradelink.infrastructure.persistence.entity.OrderDetail;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OrderMapper {

    public OrderDetail toOrderDetail(Order order) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(order.getOrderId());

        if (order.getAccountData() != null) {
            orderDetail.setAccountKey(order.getAccountData().getAccountKey());
            orderDetail.setAccountName(order.getAccountData().getAccountName());
        }

        if (order.getMemberData() != null) {
            orderDetail.setMemberKey(order.getMemberData().getMemberKey());
            orderDetail.setMemberName(order.getMemberData().getMemberName());
        }

        if (order.getCashAccountData() != null) {
            orderDetail.setCashAccount(order.getCashAccountData().getCashAccount());
            orderDetail.setCashAccountCurrency(order.getCashAccountData().getCurrency());
        }

        if (order.getIsinData() != null) {
            orderDetail.setIsin(order.getIsinData().getIsin());
            orderDetail.setIsinName(order.getIsinData().getIsinName());
            orderDetail.setIsinType(order.getIsinData().getIsinType());
        }

        orderDetail.setExchangeCode(order.getExchangeCode());
        orderDetail.setOrderType(order.getOrderType());

        if (order.getQuantityData() != null) {
            orderDetail.setQuantity(order.getQuantityData().getQuantity());
            orderDetail.setTotalExecutedQuantity(order.getQuantityData().getTotalExecutedQuantity());
        }

        orderDetail.setPrice(order.getPrice());

        if (order.getCommissionData() != null) {
            orderDetail.setCommissionRate(order.getCommissionData().getCommissionRate());
            orderDetail.setCommissionType(order.getCommissionData().getCommissionType());
            orderDetail.setCommissionPrice(order.getCommissionData().getCommissionPrice());
        }

        orderDetail.setOrderRequestMode(order.getOrderRequestMode());
        orderDetail.setTps2Id(order.getTps2Id());
        orderDetail.setOrderState(order.getOrderState());
        orderDetail.setExecutionState(order.getExecutionState());
        orderDetail.setIsEnriched(order.getIsEnriched());
        orderDetail.setIsAmended(order.getIsAmended());
        orderDetail.setCreateTime(order.getCreateTime());
        orderDetail.setUpdateTime(order.getUpdateTime());
        return orderDetail;
    }

    public Order toOrder(OrderDetail orderDetail) {
        Order order = new Order();
        order.setOrderId(orderDetail.getOrderId());

        AccountData accountData = new AccountData();
        accountData.setAccountKey(orderDetail.getAccountKey());
        accountData.setAccountName(orderDetail.getAccountName());
        order.setAccountData(accountData);

        MemberData memberData = new MemberData();
        memberData.setMemberKey(orderDetail.getMemberKey());
        memberData.setMemberName(orderDetail.getMemberName());
        order.setMemberData(memberData);

        ISINData isinData = new ISINData();
        isinData.setIsin(orderDetail.getIsin());
        isinData.setIsinType(orderDetail.getIsinType());
        isinData.setIsinName(orderDetail.getIsinName());
        order.setIsinData(isinData);

        order.setExchangeCode(orderDetail.getExchangeCode());

        order.setOrderType(orderDetail.getOrderType());

        QuantityData quantityData = new QuantityData();
        quantityData.setQuantity(orderDetail.getQuantity());
        quantityData.setTotalExecutedQuantity(orderDetail.getTotalExecutedQuantity());
        order.setQuantityData(quantityData);

        order.setPrice(orderDetail.getPrice());

        CommissionData commissionData = new CommissionData();
        commissionData.setCommissionPrice(orderDetail.getCommissionPrice());
        commissionData.setCommissionType(orderDetail.getCommissionType());
        commissionData.setCommissionRate(orderDetail.getCommissionRate());
        order.setCommissionData(commissionData);

        order.setOrderRequestMode(orderDetail.getOrderRequestMode());

        order.setTps2Id(orderDetail.getTps2Id());

        order.setOrderState(orderDetail.getOrderState());

        order.setExecutionState(orderDetail.getExecutionState());

        order.setIsAmended(orderDetail.getIsAmended());

        order.setIsEnriched(orderDetail.getIsEnriched());

        order.setCreateTime(orderDetail.getCreateTime());

        order.setUpdateTime(orderDetail.getUpdateTime());

        return order;
    }
}
