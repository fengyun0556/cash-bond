package com.cpb.tradelink.domain;

import com.cpb.tradelink.domain.enums.ExecutionState;
import com.cpb.tradelink.domain.enums.OrderRequestMode;
import com.cpb.tradelink.domain.enums.OrderState;
import com.cpb.tradelink.domain.enums.OrderType;
import com.cpb.tradelink.domain.event.MEMOOrderCreatedEvent;
import com.cpb.tradelink.domain.event.OrderAmendedEvent;
import com.cpb.tradelink.domain.event.OrderEnrichedEvent;
import com.cpb.tradelink.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Order order;
    private AccountData accountData;
    private MemberData memberData;
    private CashAccountData cashAccountData;
    private ISINData isinData;
    private QuantityData quantityData;
    private CommissionData commissionData;
    private List<RuleCheck> ruleCheckList;
    private List<ExecutionRecord> executionRecordList;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        accountData = new AccountData();
        accountData.setAccountKey("ACC001");
        accountData.setAccountName("Test Account");

        memberData = new MemberData();
        memberData.setMemberKey("MEM001");
        memberData.setMemberName("Test Member");

        cashAccountData = new CashAccountData();
        cashAccountData.setCashAccount("CASH001");
        cashAccountData.setCurrency("USD");

        isinData = new ISINData();
        isinData.setIsin("US0378331005");
        isinData.setIsinName("Apple Inc");
        isinData.setIsinType("EQUITY");

        quantityData = new QuantityData();
        quantityData.setQuantity(100);

        commissionData = new CommissionData();
        commissionData.setCommissionRate(BigDecimal.valueOf(0.01));
        commissionData.setCommissionType("PERCENTAGE");

        ruleCheckList = new ArrayList<>();
        RuleCheck ruleCheck = new RuleCheck();
        ruleCheck.setRuleCode("RULE001");
        ruleCheck.setRuleCheckResult("PASS");
        ruleCheck.setRuleDescribe("Test Rule");
        ruleCheckList.add(ruleCheck);

        executionRecordList = new ArrayList<>();

        // 创建 Order 对象
        order = new Order();
        order.setOrderId(1L);
        order.setAccountData(accountData);
        order.setMemberData(memberData);
        order.setCashAccountData(cashAccountData);
        order.setIsinData(isinData);
        order.setExchangeCode("NYSE");
        order.setOrderType(OrderType.LIMIT);
        order.setQuantityData(quantityData);
        order.setPrice(BigDecimal.valueOf(150.0));
        order.setCommissionData(commissionData);
        order.setRuleCheckList(ruleCheckList);
        order.setExecutionRecordList(executionRecordList);
    }

    @Test
    void initForNew_LiveMode_ShouldSetNewState() {
        // Arrange
        order.setOrderRequestMode(OrderRequestMode.LIVE);

        // Act
        order.initForNew();

        // Assert
        assertEquals(OrderState.NEW, order.getOrderState());
        assertNotNull(order.getCreateTime());
        assertNotNull(order.getUpdateTime());
        assertTrue(order.getRuleCheckList().get(0).getCreateTime().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(order.getRuleCheckList().get(0).getUpdateTime().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void initForNew_MemoMode_ShouldSetExecutedStateAndCreateExecutionRecord() {
        // Arrange
        order.setOrderRequestMode(OrderRequestMode.MEMO);
        order.setExecutionRecordList(new ArrayList<>());

        // Act
        order.initForNew();

        // Assert
        assertEquals(OrderState.EXECUTED, order.getOrderState());
        assertEquals(100, order.getQuantityData().getTotalExecutedQuantity());
        assertNotNull(order.getExecutionRecordList());
        assertEquals(1, order.getExecutionRecordList().size());
        
        ExecutionRecord executionRecord = order.getExecutionRecordList().get(0);
        assertEquals(BigDecimal.valueOf(150.0), executionRecord.getExecutedPrice());
        assertEquals(100, executionRecord.getExecutedQuantity());
        assertNotNull(executionRecord.getCreateTime());
        assertNotNull(executionRecord.getUpdateTime());
    }

    @Test
    void initForPhone_ShouldSetPhoneModeAndExecutedState() {
        // Arrange
        ExecutionRecord executionRecord = new ExecutionRecord();
        executionRecord.setExecutedPrice(BigDecimal.valueOf(150.0));
        executionRecord.setExecutedQuantity(100);
        List<ExecutionRecord> records = new ArrayList<>();
        records.add(executionRecord);
        order.setExecutionRecordList(records);

        // Act
        order.initForPhone();

        // Assert
        assertEquals(OrderState.EXECUTED, order.getOrderState());
        assertEquals(ExecutionState.FULL_EXECUTION, order.getExecutionState());
        assertEquals(OrderRequestMode.PHONE, order.getOrderRequestMode());
        assertFalse(order.getIsEnriched());
        assertEquals(1L, executionRecord.getOrderId());
        assertNotNull(executionRecord.getCreateTime());
        assertNotNull(executionRecord.getUpdateTime());
        assertNotNull(order.getCreateTime());
        assertNotNull(order.getUpdateTime());
    }

    @Test
    void calculateCommission_ShouldCalculateCorrectCommission() {
        // Arrange
        // quantity = 100, price = 150.0, commissionRate = 0.01
        // expected commission = 100 * 150.0 * 0.01 = 150.0

        // Act
        order.calculateCommission();

        // Assert
        // 使用 compareTo 方法比较 BigDecimal
        assertEquals(0, BigDecimal.valueOf(150.0).compareTo(order.getCommissionData().getCommissionPrice()));

        assertEquals(BigDecimal.valueOf(150.0).stripTrailingZeros(),
                order.getCommissionData().getCommissionPrice().stripTrailingZeros());
    }

    @Test
    void omsSubmissionSuccess_ShouldUpdateStateAndTps2Id() {
        // Arrange
        Long tps2Id = 12345L;

        // Act
        order.omsSubmissionSuccess(tps2Id);

        // Assert
        assertEquals(OrderState.SUBMITTED, order.getOrderState());
        assertEquals(tps2Id, order.getTps2Id());
        assertNotNull(order.getUpdateTime());
    }

    @Test
    void omsSubmissionFailed_ShouldUpdateStateToFailed() {
        // Act
        order.omsSubmissionFailed();

        // Assert
        assertEquals(OrderState.FAILED, order.getOrderState());
        assertNotNull(order.getUpdateTime());
    }

    @Test
    void enrich_ShouldUpdateStateToEnrichment() {
        // Act
        order.enrich();

        // Assert
        assertEquals(OrderState.ENRICHMENT, order.getOrderState());
        assertNotNull(order.getUpdateTime());
    }

    @Test
    void amend_ShouldUpdateStateToAmended() {
        // Act
        order.amend();

        // Assert
        assertEquals(OrderState.AMENDED, order.getOrderState());
        assertNotNull(order.getUpdateTime());
    }

    @Test
    void approve_ShouldUpdateStateToApproved() {
        // Act
        order.approve();

        // Assert
        assertEquals(OrderState.APPROVED, order.getOrderState());
        assertNotNull(order.getUpdateTime());
    }

    @Test
    void createMEMOOrderCreatedEvent_ShouldCreateEventWithCorrectData() {
        // Arrange
        order.setOrderRequestMode(OrderRequestMode.MEMO);
        quantityData.setTotalExecutedQuantity(100);
        order.setQuantityData(quantityData);

        // Act
        MEMOOrderCreatedEvent event = order.createMEMOOrderCreatedEvent();

        // Assert
        assertNotNull(event);
        assertEquals(1L, event.getOrderId());
        assertEquals("ACC001", event.getAccountKey());
        assertEquals("Test Account", event.getAccountName());
        assertEquals("MEM001", event.getMemberKey());
        assertEquals("Test Member", event.getMemberName());
        assertEquals("CASH001", event.getCashAccount());
        assertEquals("USD", event.getCashAccountCurrency());
        assertEquals("US0378331005", event.getIsin());
        assertEquals("EQUITY", event.getIsinType());
        assertEquals("NYSE", event.getExchangeCode());
        assertEquals(100, event.getTotalExecutedQuantity());
        assertEquals(BigDecimal.valueOf(150.0), event.getPrice());
        assertEquals(BigDecimal.valueOf(0.01), event.getCommissionRate());
    }

    @Test
    void createOrderEnrichedEvent_ShouldCreateEventWithCorrectData() {
        // Arrange
        ExecutionRecord executionRecord = new ExecutionRecord();
        executionRecord.setTps2ExecutionId(999L);
        executionRecordList.add(executionRecord);
        order.setExecutionRecordList(executionRecordList);

        // Act
        OrderEnrichedEvent event = order.createOrderEnrichedEvent();

        // Assert
        assertNotNull(event);
        assertEquals(1L, event.getOrderId());
        assertEquals(999L, event.getTps2ExecutionId());
        assertEquals("CASH001", event.getCashAccount());
        assertEquals("USD", event.getCashAccountCurrency());
        assertEquals("US0378331005", event.getIsin());
        assertEquals("EQUITY", event.getIsinType());
        assertEquals(BigDecimal.valueOf(150.0), event.getPrice());
        assertEquals(BigDecimal.valueOf(0.01), event.getCommissionRate());
    }

    @Test
    void createOrderAmendedEvent_ShouldCreateEventWithCorrectData() {
        // Act
        OrderAmendedEvent event = order.createOrderAmendedEvent();

        // Assert
        assertNotNull(event);
        assertEquals(1L, event.getOrderId());
        assertEquals("ACC001", event.getAccountKey());
        assertEquals("Test Account", event.getAccountName());
        assertEquals("MEM001", event.getMemberKey());
        assertEquals("Test Member", event.getMemberName());
        assertEquals("CASH001", event.getCashAccount());
        assertEquals("USD", event.getCashAccountCurrency());
        assertEquals("US0378331005", event.getIsin());
        assertEquals("EQUITY", event.getIsinType());
        assertEquals(BigDecimal.valueOf(0.01), event.getCommissionRate());
    }

    @Test
    void bbgAckSuccess_ShouldUpdateStateToBbgAck() {
        // Act
        order.bbgAckSuccess();

        // Assert
        assertEquals(OrderState.BBG_ACK, order.getOrderState());
        assertNotNull(order.getUpdateTime());
    }

    @Test
    void bbgAckFailed_ShouldUpdateStateToFailed() {
        // Act
        order.bbgAckFailed();

        // Assert
        assertEquals(OrderState.FAILED, order.getOrderState());
        assertNotNull(order.getUpdateTime());
    }

    @Test
    void executed_FullExecution_ShouldUpdateStateAndExecutionState() {
        // Arrange
        ExecutionRecord record1 = new ExecutionRecord();
        record1.setExecutedQuantity(100);
        executionRecordList.add(record1);
        order.setExecutionRecordList(executionRecordList);

        // Act
        order.executed();

        // Assert
        assertEquals(OrderState.EXECUTED, order.getOrderState());
        assertEquals(ExecutionState.FULL_EXECUTION, order.getExecutionState());
        assertEquals(100, order.getQuantityData().getTotalExecutedQuantity());
        assertNotNull(order.getUpdateTime());
    }

    @Test
    void executed_PartialExecution_ShouldUpdateStateAndExecutionState() {
        // Arrange
        ExecutionRecord record1 = new ExecutionRecord();
        record1.setExecutedQuantity(50);
        executionRecordList.add(record1);
        order.setExecutionRecordList(executionRecordList);

        // Act
        order.executed();

        // Assert
        assertEquals(OrderState.EXECUTED, order.getOrderState());
        assertEquals(ExecutionState.PARTIAL_EXECUTION, order.getExecutionState());
        assertEquals(50, order.getQuantityData().getTotalExecutedQuantity());
        assertNotNull(order.getUpdateTime());
    }

    @Test
    void executed_MultipleRecords_ShouldSumQuantitiesCorrectly() {
        // Arrange
        ExecutionRecord record1 = new ExecutionRecord();
        record1.setExecutedQuantity(30);
        ExecutionRecord record2 = new ExecutionRecord();
        record2.setExecutedQuantity(40);
        executionRecordList.add(record1);
        executionRecordList.add(record2);
        order.setExecutionRecordList(executionRecordList);

        // Act
        order.executed();

        // Assert
        assertEquals(70, order.getQuantityData().getTotalExecutedQuantity());
        assertEquals(ExecutionState.PARTIAL_EXECUTION, order.getExecutionState());
    }

    @Test
    void settlementFailed_ShouldUpdateAmendedFlagAndTimestamp() {
        // Arrange
        order.setIsAmended(true);

        // Act
        order.settlementFailed();

        // Assert
        assertFalse(order.getIsAmended());
        assertNotNull(order.getUpdateTime());
    }
}