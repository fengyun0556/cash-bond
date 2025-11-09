package com.cpb.omsservice.domain;

import com.cpb.oms.domain.enums.SettlementState;
import com.cpb.oms.domain.event.OrderAmendedEvent;
import com.cpb.oms.domain.event.OrderEnrichedEvent;
import com.cpb.oms.domain.event.SettlementFailedEvent;
import com.cpb.oms.domain.model.settlement.SettlementInteract;
import com.cpb.oms.domain.model.settlement.SettlementResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SettlementInteractTest {

    private SettlementInteract settlementInteract;

    @BeforeEach
    void setUp() {
        settlementInteract = new SettlementInteract();
        settlementInteract.setId(1L);
        settlementInteract.setTps2ExecutionId(1001L);
        settlementInteract.setTradeLinkId(2001L);
        settlementInteract.setExecutedQuantity(100);
        settlementInteract.setExecutedPrice(BigDecimal.valueOf(150.50));
        settlementInteract.setSettlementId(3001L);
        settlementInteract.setFailedReason("Test failure reason");
    }

    @Test
    void init_WithEmptyCashAccount_ShouldSetWaitingForEnrichment() {
        // Arrange
        settlementInteract.setCashAccount("");

        // Act
        settlementInteract.init();

        // Assert
        assertEquals(SettlementState.WAITING_FOR_ENRICHMENT, settlementInteract.getSettlementState());
        assertNotNull(settlementInteract.getCreateTime());
        assertNotNull(settlementInteract.getUpdateTime());
        assertTrue(settlementInteract.getCreateTime().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(settlementInteract.getUpdateTime().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void init_WithNullCashAccount_ShouldSetWaitingForEnrichment() {
        // Arrange
        settlementInteract.setCashAccount(null);

        // Act
        settlementInteract.init();

        // Assert
        assertEquals(SettlementState.WAITING_FOR_ENRICHMENT, settlementInteract.getSettlementState());
    }

    @Test
    void init_WithCashAccount_ShouldSetWaitingForSettlement() {
        // Arrange
        settlementInteract.setCashAccount("CASH001");

        // Act
        settlementInteract.init();

        // Assert
        assertEquals(SettlementState.WAITING_FOR_SETTLEMENT, settlementInteract.getSettlementState());
        assertNotNull(settlementInteract.getCreateTime());
        assertNotNull(settlementInteract.getUpdateTime());
    }

    @Test
    void saveSettlementResult_SuccessCase_ShouldUpdateSuccessState() {
        // Arrange
        settlementInteract.init();
        SettlementResult settlementResult = new SettlementResult();
        settlementResult.setSuccess(true);
        settlementResult.setSettlementId(4001L);
        settlementResult.setFailedReason("");

        LocalDateTime initialUpdateTime = settlementInteract.getUpdateTime();

        // Act
        settlementInteract.saveSettlementResult(settlementResult);

        // Assert
        assertEquals(SettlementState.SETTLEMENT_SUCCESS, settlementInteract.getSettlementState());
        assertEquals(4001L, settlementInteract.getSettlementId());
        assertEquals("", settlementInteract.getFailedReason());
        assertTrue(settlementInteract.getUpdateTime().isAfter(initialUpdateTime));
    }

    @Test
    void saveSettlementResult_FailureCase_ShouldUpdateFailedState() {
        // Arrange
        settlementInteract.init();
        SettlementResult settlementResult = new SettlementResult();
        settlementResult.setSuccess(false);
        settlementResult.setSettlementId(null);
        settlementResult.setFailedReason("Insufficient funds");

        LocalDateTime initialUpdateTime = settlementInteract.getUpdateTime();

        // Act
        settlementInteract.saveSettlementResult(settlementResult);

        // Assert
        assertEquals(SettlementState.SETTLEMENT_FAILED, settlementInteract.getSettlementState());
        assertEquals(3001, settlementInteract.getSettlementId());
        assertEquals("Insufficient funds", settlementInteract.getFailedReason());
        assertTrue(settlementInteract.getUpdateTime().isAfter(initialUpdateTime));
    }

    @Test
    void createSettlementFailedEvent_ShouldCreateEventWithTradeLinkId() {
        // Arrange
        settlementInteract.setTradeLinkId(5001L);

        // Act
        SettlementFailedEvent event = settlementInteract.createSettlementFailedEvent();

        // Assert
        assertNotNull(event);
        assertEquals(5001L, event.getOrderId());
    }

    @Test
    void createSettlementFailedEvent_WithNullTradeLinkId_ShouldCreateEvent() {
        // Arrange
        settlementInteract.setTradeLinkId(null);

        // Act
        SettlementFailedEvent event = settlementInteract.createSettlementFailedEvent();

        // Assert
        assertNotNull(event);
        assertNull(event.getOrderId());
    }

    @Test
    void sendToBanker_ShouldUpdateStateToWaitingForAmend() {
        // Arrange
        settlementInteract.init();
        LocalDateTime initialUpdateTime = settlementInteract.getUpdateTime();

        // Act
        settlementInteract.sendToBanker();

        // Assert
        assertEquals(SettlementState.WAITING_FOR_AMEND, settlementInteract.getSettlementState());
        assertTrue(settlementInteract.getUpdateTime().isAfter(initialUpdateTime));
    }

    @Test
    void canEnrich_WhenWaitingForEnrichment_ShouldReturnTrue() {
        // Arrange
        settlementInteract.setSettlementState(SettlementState.WAITING_FOR_ENRICHMENT);

        // Act
        boolean result = settlementInteract.canEnrich();

        // Assert
        assertTrue(result);
    }

    @Test
    void canEnrich_WhenNotWaitingForEnrichment_ShouldReturnFalse() {
        // Arrange
        settlementInteract.setSettlementState(SettlementState.WAITING_FOR_SETTLEMENT);

        // Act
        boolean result = settlementInteract.canEnrich();

        // Assert
        assertFalse(result);
    }

    @Test
    void canAmend_WhenWaitingForAmend_ShouldReturnTrue() {
        // Arrange
        settlementInteract.setSettlementState(SettlementState.WAITING_FOR_AMEND);

        // Act
        boolean result = settlementInteract.canAmend();

        // Assert
        assertTrue(result);
    }

    @Test
    void canAmend_WhenNotWaitingForAmend_ShouldReturnFalse() {
        // Arrange
        settlementInteract.setSettlementState(SettlementState.SETTLEMENT_SUCCESS);

        // Act
        boolean result = settlementInteract.canAmend();

        // Assert
        assertFalse(result);
    }

    @Test
    void enrich_ShouldUpdateFieldsFromOrderEnrichedEvent() {
        // Arrange
        OrderEnrichedEvent event = new OrderEnrichedEvent();
        event.setOrderId(6001L);
        event.setCashAccount("ENRICHED_CASH");
        event.setIsin("ENRICHED_ISIN");

        // Act
        settlementInteract.enrich(event);

        // Assert
        assertEquals(6001L, settlementInteract.getTradeLinkId());
        assertEquals("ENRICHED_CASH", settlementInteract.getCashAccount());
        assertEquals("ENRICHED_ISIN", settlementInteract.getIsin());
    }

    @Test
    void enrich_WithNullEvent_ShouldHandleGracefully() {
        // Arrange
        Long originalTradeLinkId = settlementInteract.getTradeLinkId();
        String originalCashAccount = settlementInteract.getCashAccount();
        String originalIsin = settlementInteract.getIsin();

        // Act
        settlementInteract.enrich(null);

        // Assert
        assertEquals(originalTradeLinkId, settlementInteract.getTradeLinkId());
        assertEquals(originalCashAccount, settlementInteract.getCashAccount());
        assertEquals(originalIsin, settlementInteract.getIsin());
    }

    @Test
    void amend_ShouldUpdateFieldsFromOrderAmendedEvent() {
        // Arrange
        OrderAmendedEvent event = new OrderAmendedEvent();
        event.setOrderId(7001L);
        event.setCashAccount("AMENDED_CASH");
        event.setIsin("AMENDED_ISIN");

        // Act
        settlementInteract.amend(event);

        // Assert
        assertEquals(7001L, settlementInteract.getTradeLinkId());
        assertEquals("AMENDED_CASH", settlementInteract.getCashAccount());
        assertEquals("AMENDED_ISIN", settlementInteract.getIsin());
    }

    @Test
    void amend_WithNullEvent_ShouldHandleGracefully() {
        // Arrange
        Long originalTradeLinkId = settlementInteract.getTradeLinkId();
        String originalCashAccount = settlementInteract.getCashAccount();
        String originalIsin = settlementInteract.getIsin();

        // Act
        settlementInteract.amend(null);

        // Assert
        assertEquals(originalTradeLinkId, settlementInteract.getTradeLinkId());
        assertEquals(originalCashAccount, settlementInteract.getCashAccount());
        assertEquals(originalIsin, settlementInteract.getIsin());
    }

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Arrange & Act
        settlementInteract.setId(999L);
        settlementInteract.setTps2ExecutionId(888L);
        settlementInteract.setTradeLinkId(777L);
        settlementInteract.setCashAccount("TEST_CASH");
        settlementInteract.setIsin("TEST_ISIN");
        settlementInteract.setExecutedQuantity(500);
        settlementInteract.setExecutedPrice(BigDecimal.valueOf(200.75));
        settlementInteract.setSettlementId(666L);
        settlementInteract.setSettlementState(SettlementState.SETTLEMENT_FAILED);
        settlementInteract.setFailedReason("Test reason");

        LocalDateTime testTime = LocalDateTime.now();
        settlementInteract.setCreateTime(testTime);
        settlementInteract.setUpdateTime(testTime);

        // Assert
        assertEquals(999L, settlementInteract.getId());
        assertEquals(888L, settlementInteract.getTps2ExecutionId());
        assertEquals(777L, settlementInteract.getTradeLinkId());
        assertEquals("TEST_CASH", settlementInteract.getCashAccount());
        assertEquals("TEST_ISIN", settlementInteract.getIsin());
        assertEquals(500, settlementInteract.getExecutedQuantity());
        assertEquals(0, BigDecimal.valueOf(200.75).compareTo(settlementInteract.getExecutedPrice()));
        assertEquals(666L, settlementInteract.getSettlementId());
        assertEquals(SettlementState.SETTLEMENT_FAILED, settlementInteract.getSettlementState());
        assertEquals("Test reason", settlementInteract.getFailedReason());
        assertEquals(testTime, settlementInteract.getCreateTime());
        assertEquals(testTime, settlementInteract.getUpdateTime());
    }

    @Test
    void init_MultipleTimes_ShouldNotResetCreateTime() {
        // Arrange
        settlementInteract.setCashAccount("CASH001");
        settlementInteract.init();
        LocalDateTime firstCreateTime = settlementInteract.getCreateTime();

        // Act
        settlementInteract.init();

        // Assert
        assertTrue(settlementInteract.getUpdateTime().isAfter(firstCreateTime));
    }

}