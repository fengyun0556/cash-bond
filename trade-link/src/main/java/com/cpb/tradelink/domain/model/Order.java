package com.cpb.tradelink.domain.model;

import com.cpb.tradelink.domain.enums.ExecutionState;
import com.cpb.tradelink.domain.enums.OrderRequestMode;
import com.cpb.tradelink.domain.enums.OrderState;
import com.cpb.tradelink.domain.enums.OrderType;
import com.cpb.tradelink.domain.event.MEMOOrderCreatedEvent;
import com.cpb.tradelink.domain.event.OrderAmendedEvent;
import com.cpb.tradelink.domain.event.OrderEnrichedEvent;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Order {

    private Long orderId;

    private AccountData accountData;

    private MemberData memberData;

    private CashAccountData cashAccountData;

    private ISINData isinData;

    private String exchangeCode;

    private OrderType orderType;

    private QuantityData quantityData;

    private BigDecimal price;

    private CommissionData commissionData;

    private OrderRequestMode orderRequestMode;

    private Long tps2Id;

    private OrderState orderState;

    private ExecutionState executionState;

    private Boolean isEnriched;

    private Boolean isAmended;

    private List<RuleCheck> ruleCheckList;

    private List<ExecutionRecord> executionRecordList;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public void initForNew() {
        if (OrderRequestMode.LIVE.equals(this.orderRequestMode))
            this.orderState = OrderState.NEW;
        else if (OrderRequestMode.MEMO.equals(this.orderRequestMode)) {
            this.orderState = OrderState.EXECUTED;
            QuantityData quantityData = this.quantityData;
            quantityData.setTotalExecutedQuantity(quantityData.getQuantity());

            ExecutionRecord executionRecord = new ExecutionRecord();
            executionRecord.setExecutedPrice(this.price);
            executionRecord.setExecutedQuantity(quantityData.getQuantity());
            executionRecord.setCreateTime(LocalDateTime.now());
            executionRecord.setUpdateTime(LocalDateTime.now());
            this.executionRecordList = List.of(executionRecord);
        }
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this.ruleCheckList.forEach(ruleCheck -> {
            ruleCheck.setCreateTime(LocalDateTime.now());
            ruleCheck.setUpdateTime(LocalDateTime.now());
        });
    }

    public void initForPhone() {
        this.orderState = OrderState.EXECUTED;
        this.executionState = ExecutionState.FULL_EXECUTION;
        this.orderRequestMode = OrderRequestMode.PHONE;
        this.isEnriched = false;
        ExecutionRecord executionRecord = this.executionRecordList.get(0);
        executionRecord.setOrderId(this.orderId);
        executionRecord.setCreateTime(LocalDateTime.now());
        executionRecord.setUpdateTime(LocalDateTime.now());

        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    public void calculateCommission() {
        CommissionData commissionData = this.commissionData;
        commissionData.setCommissionPrice(
                commissionData.getCommissionRate()
                        .multiply(BigDecimal.valueOf(this.quantityData.getQuantity()))
                        .multiply(this.price)
        );
    }

    public void omsSubmissionSuccess(Long tps2Id) {
        this.tps2Id = tps2Id;
        this.orderState = OrderState.SUBMITTED;
        this.updateTime = LocalDateTime.now();
    }

    public void omsSubmissionFailed() {
        this.orderState = OrderState.FAILED;
        this.updateTime = LocalDateTime.now();
    }

    public void enrich() {
        this.orderState = OrderState.ENRICHMENT;
        this.updateTime = LocalDateTime.now();
    }

    public void amend() {
        this.orderState = OrderState.AMENDED;
        this.updateTime = LocalDateTime.now();
    }

    public void approve() {
        this.orderState = OrderState.APPROVED;
        this.updateTime = LocalDateTime.now();
    }

    public MEMOOrderCreatedEvent createMEMOOrderCreatedEvent() {
        MEMOOrderCreatedEvent memoOrderCreatedEvent = new MEMOOrderCreatedEvent();
        memoOrderCreatedEvent.setOrderId(this.orderId);
        memoOrderCreatedEvent.setAccountKey(this.getAccountData().getAccountKey());
        memoOrderCreatedEvent.setAccountName(this.getAccountData().getAccountName());
        memoOrderCreatedEvent.setMemberKey(this.getMemberData().getMemberKey());
        memoOrderCreatedEvent.setMemberName(this.getMemberData().getMemberName());
        memoOrderCreatedEvent.setCashAccount(this.getCashAccountData().getCashAccount());
        memoOrderCreatedEvent.setCashAccountCurrency(this.getCashAccountData().getCurrency());
        memoOrderCreatedEvent.setIsin(this.getIsinData().getIsin());
        memoOrderCreatedEvent.setIsinType(this.getIsinData().getIsinType());
        memoOrderCreatedEvent.setExchangeCode(this.exchangeCode);
        memoOrderCreatedEvent.setTotalExecutedQuantity(this.getQuantityData().getTotalExecutedQuantity());
        memoOrderCreatedEvent.setPrice(this.price);
        memoOrderCreatedEvent.setCommissionRate(this.getCommissionData().getCommissionRate());
        memoOrderCreatedEvent.setCommissionPrice(this.getCommissionData().getCommissionPrice());
        return memoOrderCreatedEvent;
    }

    public OrderEnrichedEvent createOrderEnrichedEvent() {
        OrderEnrichedEvent orderEnrichedEvent = new OrderEnrichedEvent();
        orderEnrichedEvent.setOrderId(this.orderId);
        orderEnrichedEvent.setTps2ExecutionId(this.executionRecordList.get(0).getTps2ExecutionId());
        orderEnrichedEvent.setCashAccount(this.getCashAccountData().getCashAccount());
        orderEnrichedEvent.setCashAccountCurrency(this.getCashAccountData().getCurrency());
        orderEnrichedEvent.setIsin(this.getIsinData().getIsin());
        orderEnrichedEvent.setIsinType(this.getIsinData().getIsinType());
        orderEnrichedEvent.setPrice(this.price);
        orderEnrichedEvent.setCommissionRate(this.getCommissionData().getCommissionRate());
        orderEnrichedEvent.setCommissionPrice(this.getCommissionData().getCommissionPrice());
        return orderEnrichedEvent;
    }

    public OrderAmendedEvent createOrderAmendedEvent() {
        OrderAmendedEvent orderAmendedEvent = new OrderAmendedEvent();
        orderAmendedEvent.setOrderId(this.orderId);
        orderAmendedEvent.setAccountKey(this.getAccountData().getAccountKey());
        orderAmendedEvent.setAccountName(this.getAccountData().getAccountName());
        orderAmendedEvent.setMemberKey(this.getMemberData().getMemberKey());
        orderAmendedEvent.setMemberName(this.getMemberData().getMemberName());
        orderAmendedEvent.setCashAccount(this.getCashAccountData().getCashAccount());
        orderAmendedEvent.setCashAccountCurrency(this.getCashAccountData().getCurrency());
        orderAmendedEvent.setIsin(this.getIsinData().getIsin());
        orderAmendedEvent.setIsinType(this.getIsinData().getIsinType());
        orderAmendedEvent.setCommissionRate(this.getCommissionData().getCommissionRate());
        orderAmendedEvent.setCommissionPrice(this.getCommissionData().getCommissionPrice());
        return orderAmendedEvent;
    }

    public void bbgAckSuccess() {
        this.orderState = OrderState.BBG_ACK;
        this.updateTime = LocalDateTime.now();
    }

    public void bbgAckFailed() {
        this.orderState = OrderState.FAILED;
        this.updateTime = LocalDateTime.now();
    }

    public void executed() {
        this.orderState = OrderState.EXECUTED;
        Integer totalExecutedQuantity = this.executionRecordList.stream().mapToInt(ExecutionRecord::getExecutedQuantity).sum();
        QuantityData quantityData = this.quantityData;
        quantityData.setTotalExecutedQuantity(totalExecutedQuantity);

        if (quantityData.fullExecution()) {
            this.executionState = ExecutionState.FULL_EXECUTION;
        } else {
            this.executionState = ExecutionState.PARTIAL_EXECUTION;
        }

        this.updateTime = LocalDateTime.now();
    }

    public void settlementFailed() {
        this.isAmended = false;
        this.updateTime = LocalDateTime.now();
    }
}
