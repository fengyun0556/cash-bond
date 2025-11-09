package com.cpb.omsservice.domain;

import com.cpb.oms.domain.enums.OrderType;
import com.cpb.oms.domain.enums.TradeState;
import com.cpb.oms.domain.event.TradeSubmissionEvent;
import com.cpb.oms.domain.model.trading.TradingInstruction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TradingInstructionTest {

    private TradingInstruction tradingInstruction;

    @BeforeEach
    void setUp() {
        tradingInstruction = new TradingInstruction();
        tradingInstruction.setTps2Id(1001L);
        tradingInstruction.setTradeLinkId(2001L);
        tradingInstruction.setAccountKey("ACC001");
        tradingInstruction.setAccountName("Test Account");
        tradingInstruction.setMemberKey("MEM001");
        tradingInstruction.setMemberName("Test Member");
        tradingInstruction.setCashAccount("CASH001");
        tradingInstruction.setISIN("US0378331005");
        tradingInstruction.setISINName("Apple Inc");
        tradingInstruction.setExchangeCode("NYSE");
        tradingInstruction.setOrderType(OrderType.LIMIT);
        tradingInstruction.setQuantity(100);
        tradingInstruction.setPrice(BigDecimal.valueOf(150.50));
    }

    @Test
    void init_ShouldSetSubmittedStateAndTimestamps() {
        // Arrange
        LocalDateTime beforeTest = LocalDateTime.now().minusSeconds(1);

        // Act
        tradingInstruction.init();

        // Assert
        assertEquals(TradeState.SUBMITTED, tradingInstruction.getTradeState());
        assertNotNull(tradingInstruction.getCreateDateTime());
        assertNotNull(tradingInstruction.getUpdateDateTime());
        assertTrue(tradingInstruction.getCreateDateTime().isAfter(beforeTest));
        assertTrue(tradingInstruction.getUpdateDateTime().isAfter(beforeTest));
    }

    @Test
    void init_WhenCalledMultipleTimes_ShouldNotChangeState() {
        // Arrange
        tradingInstruction.init();
        TradeState firstState = tradingInstruction.getTradeState();
        LocalDateTime firstCreateTime = tradingInstruction.getCreateDateTime();
        LocalDateTime firstUpdateTime = tradingInstruction.getUpdateDateTime();

        // Act
        tradingInstruction.init();

        // Assert
        assertEquals(firstState, tradingInstruction.getTradeState());
        assertTrue(tradingInstruction.getUpdateDateTime().isAfter(firstUpdateTime));
    }

    @Test
    void createTradeSubmissionEvent_ShouldCreateEventWithCorrectData() {
        // Arrange & Act
        TradeSubmissionEvent event = tradingInstruction.createTradeSubmissionEvent();

        // Assert
        assertNotNull(event);
        assertEquals(2001L, event.getUniqueId());
        assertEquals("ACC001", event.getAccountKey());
        assertEquals("US0378331005", event.getISIN());
        assertEquals("Apple Inc", event.getISINName());
        assertEquals("NYSE", event.getExchangeCode());
        assertEquals(OrderType.LIMIT, event.getOrderType());
        assertEquals(100, event.getQuantity());
        assertEquals(0, BigDecimal.valueOf(150.50).compareTo(event.getPrice()));
    }

    @Test
    void createTradeSubmissionEvent_WithNullValues_ShouldHandleGracefully() {
        // Arrange
        tradingInstruction.setTradeLinkId(null);
        tradingInstruction.setAccountKey(null);
        tradingInstruction.setISIN(null);
        tradingInstruction.setISINName(null);
        tradingInstruction.setExchangeCode(null);
        tradingInstruction.setOrderType(null);
        tradingInstruction.setQuantity(null);
        tradingInstruction.setPrice(null);

        // Act
        TradeSubmissionEvent event = tradingInstruction.createTradeSubmissionEvent();

        // Assert
        assertNotNull(event);
        assertNull(event.getUniqueId());
        assertNull(event.getAccountKey());
        assertNull(event.getISIN());
        assertNull(event.getISINName());
        assertNull(event.getExchangeCode());
        assertNull(event.getOrderType());
        assertNull(event.getQuantity());
        assertNull(event.getPrice());
    }

    @Test
    void createTradeSubmissionEvent_WithMarketOrder_ShouldIncludeNullPrice() {
        // Arrange
        tradingInstruction.setOrderType(OrderType.MARKET);
        tradingInstruction.setPrice(null);

        // Act
        TradeSubmissionEvent event = tradingInstruction.createTradeSubmissionEvent();

        // Assert
        assertNotNull(event);
        assertEquals(OrderType.MARKET, event.getOrderType());
        assertNull(event.getPrice());
    }

    @Test
    void bbgAck_ShouldUpdateStateToBbgAckAndTimestamp() {
        // Arrange
        tradingInstruction.init();
        LocalDateTime initialUpdateTime = tradingInstruction.getUpdateDateTime();
        
        // 等待一小段时间确保时间戳不同
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act
        tradingInstruction.bbgAck();

        // Assert
        assertEquals(TradeState.BBG_ACK, tradingInstruction.getTradeState());
        assertNotNull(tradingInstruction.getUpdateDateTime());
        assertTrue(tradingInstruction.getUpdateDateTime().isAfter(initialUpdateTime));
    }

    @Test
    void bbgAck_WhenCalledMultipleTimes_ShouldUpdateTimestamp() {
        // Arrange
        tradingInstruction.init();
        tradingInstruction.bbgAck();
        LocalDateTime firstAckTime = tradingInstruction.getUpdateDateTime();

        // 等待一小段时间确保时间戳不同
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act
        tradingInstruction.bbgAck();

        // Assert
        assertEquals(TradeState.BBG_ACK, tradingInstruction.getTradeState());
        assertTrue(tradingInstruction.getUpdateDateTime().isAfter(firstAckTime));
    }

    @Test
    void bbgAck_WithoutInit_ShouldStillWork() {
        // Arrange
        // 不调用 init() 方法

        // Act
        tradingInstruction.bbgAck();

        // Assert
        assertEquals(TradeState.BBG_ACK, tradingInstruction.getTradeState());
        assertNotNull(tradingInstruction.getUpdateDateTime());
    }

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Arrange & Act
        tradingInstruction.setTps2Id(999L);
        tradingInstruction.setTradeLinkId(888L);
        tradingInstruction.setAccountKey("NEW_ACC");
        tradingInstruction.setAccountName("New Account");
        tradingInstruction.setMemberKey("NEW_MEM");
        tradingInstruction.setMemberName("New Member");
        tradingInstruction.setCashAccount("NEW_CASH");
        tradingInstruction.setISIN("NEW_ISIN");
        tradingInstruction.setISINName("New ISIN Name");
        tradingInstruction.setTradeState(TradeState.BBG_ACK);
        tradingInstruction.setExchangeCode("NASDAQ");
        tradingInstruction.setOrderType(OrderType.MARKET);
        tradingInstruction.setQuantity(500);
        tradingInstruction.setPrice(BigDecimal.valueOf(200.75));

        LocalDateTime testTime = LocalDateTime.now();
        tradingInstruction.setCreateDateTime(testTime);
        tradingInstruction.setUpdateDateTime(testTime);

        // Assert
        assertEquals(999L, tradingInstruction.getTps2Id());
        assertEquals(888L, tradingInstruction.getTradeLinkId());
        assertEquals("NEW_ACC", tradingInstruction.getAccountKey());
        assertEquals("New Account", tradingInstruction.getAccountName());
        assertEquals("NEW_MEM", tradingInstruction.getMemberKey());
        assertEquals("New Member", tradingInstruction.getMemberName());
        assertEquals("NEW_CASH", tradingInstruction.getCashAccount());
        assertEquals("NEW_ISIN", tradingInstruction.getISIN());
        assertEquals("New ISIN Name", tradingInstruction.getISINName());
        assertEquals(TradeState.BBG_ACK, tradingInstruction.getTradeState());
        assertEquals("NASDAQ", tradingInstruction.getExchangeCode());
        assertEquals(OrderType.MARKET, tradingInstruction.getOrderType());
        assertEquals(500, tradingInstruction.getQuantity());
        assertEquals(0, BigDecimal.valueOf(200.75).compareTo(tradingInstruction.getPrice()));
        assertEquals(testTime, tradingInstruction.getCreateDateTime());
        assertEquals(testTime, tradingInstruction.getUpdateDateTime());
    }

    @Test
    void objectState_AfterInitialization() {
        // Act
        tradingInstruction.init();

        // Assert
        assertEquals(TradeState.SUBMITTED, tradingInstruction.getTradeState());
        assertNotNull(tradingInstruction.getCreateDateTime());
        assertNotNull(tradingInstruction.getUpdateDateTime());
    }

    @Test
    void objectState_AfterBbgAck() {
        // Arrange
        tradingInstruction.init();

        // Act
        tradingInstruction.bbgAck();

        // Assert
        assertEquals(TradeState.BBG_ACK, tradingInstruction.getTradeState());
        assertTrue(tradingInstruction.getUpdateDateTime().isAfter(tradingInstruction.getCreateDateTime()));
    }
}