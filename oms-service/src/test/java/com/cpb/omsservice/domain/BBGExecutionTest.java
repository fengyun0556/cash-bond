package com.cpb.omsservice.domain;

import com.cpb.oms.domain.event.TradeExecutedEvent;
import com.cpb.oms.domain.model.executed.BBGExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BBGExecutionTest {

    private BBGExecution bbgExecution;

    @BeforeEach
    void setUp() {
        bbgExecution = new BBGExecution();
        bbgExecution.setTps2ExecutionId(1001L);
        bbgExecution.setTradeLinkId(2001L);
        bbgExecution.setBbgMessageId("BBG_MSG_001");
        bbgExecution.setAccountKey("ACC001");
        bbgExecution.setCashAccount("CASH001");
        bbgExecution.setISIN("US0378331005");
        bbgExecution.setExecutedQuantity(100);
        bbgExecution.setExecutedPrice(BigDecimal.valueOf(150.50));
        bbgExecution.setConfirmedDateTime(LocalDateTime.of(2023, 1, 1, 10, 0));
    }

    @Test
    void init_ShouldSetTimestampsAndConfirmedFalse() {
        // Arrange
        LocalDateTime beforeTest = LocalDateTime.now().minusSeconds(1);

        // Act
        bbgExecution.init();

        // Assert
        assertNotNull(bbgExecution.getCreateTime());
        assertNotNull(bbgExecution.getUpdateTime());
        assertTrue(bbgExecution.getCreateTime().isAfter(beforeTest));
        assertTrue(bbgExecution.getUpdateTime().isAfter(beforeTest));
        assertFalse(bbgExecution.getConfirmed());
    }

    @Test
    void init_WhenCalledMultipleTimes_ShouldNotResetCreateTime() {
        // Arrange
        bbgExecution.init();
        LocalDateTime firstCreateTime = bbgExecution.getCreateTime();
        LocalDateTime firstUpdateTime = bbgExecution.getUpdateTime();

        // Act
        bbgExecution.init();

        // Assert
        assertTrue(bbgExecution.getUpdateTime().isAfter(firstUpdateTime));
        assertFalse(bbgExecution.getConfirmed());
    }

    @Test
    void createTradeExecutedEvent_ShouldCreateEventWithCorrectData() {
        // Arrange & Act
        TradeExecutedEvent event = bbgExecution.createTradeExecutedEvent();

        // Assert
        assertNotNull(event);
        assertEquals(1001L, event.getTps2ExecutionId());
        assertEquals(2001L, event.getTradeLinkId());
        assertEquals("ACC001", event.getAccountKey());
        assertEquals("US0378331005", event.getISIN());
        assertEquals(100, event.getExecutedQuantity());
        assertEquals(0, BigDecimal.valueOf(150.50).compareTo(event.getExecutedPrice()));
    }

    @Test
    void createTradeExecutedEvent_WithNullValues_ShouldHandleGracefully() {
        // Arrange
        bbgExecution.setTps2ExecutionId(null);
        bbgExecution.setTradeLinkId(null);
        bbgExecution.setAccountKey(null);
        bbgExecution.setISIN(null);
        bbgExecution.setExecutedQuantity(null);
        bbgExecution.setExecutedPrice(null);

        // Act
        TradeExecutedEvent event = bbgExecution.createTradeExecutedEvent();

        // Assert
        assertNotNull(event);
        assertNull(event.getTps2ExecutionId());
        assertNull(event.getTradeLinkId());
        assertNull(event.getAccountKey());
        assertNull(event.getISIN());
        assertNull(event.getExecutedQuantity());
        assertNull(event.getExecutedPrice());
    }

    @Test
    void confirmed_ShouldSetConfirmedTrueAndUpdateTimestamp() {
        // Arrange
        bbgExecution.init();
        LocalDateTime initialUpdateTime = bbgExecution.getUpdateTime();
        
        // 等待一小段时间确保时间戳不同
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act
        bbgExecution.confirmed();

        // Assert
        assertTrue(bbgExecution.getConfirmed());
        assertNotNull(bbgExecution.getUpdateTime());
        assertTrue(bbgExecution.getUpdateTime().isAfter(initialUpdateTime));
    }

    @Test
    void confirmed_WhenCalledMultipleTimes_ShouldRemainConfirmedAndUpdateTimestamp() {
        // Arrange
        bbgExecution.init();
        bbgExecution.confirmed();
        LocalDateTime firstConfirmTime = bbgExecution.getUpdateTime();

        // 等待一小段时间确保时间戳不同
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act
        bbgExecution.confirmed();

        // Assert
        assertTrue(bbgExecution.getConfirmed());
        assertTrue(bbgExecution.getUpdateTime().isAfter(firstConfirmTime));
    }

    @Test
    void confirmed_WithoutInit_ShouldStillWork() {
        // Arrange
        // 不调用 init() 方法

        // Act
        bbgExecution.confirmed();

        // Assert
        assertTrue(bbgExecution.getConfirmed());
        assertNotNull(bbgExecution.getUpdateTime());
    }

    @Test
    void confirmed_ShouldUpdateConfirmedDateTime() {
        // Arrange
        bbgExecution.init();
        LocalDateTime beforeConfirm = LocalDateTime.now().minusSeconds(1);

        // Act
        bbgExecution.confirmed();

        // Assert
        assertTrue(bbgExecution.getConfirmed());
        assertNotNull(bbgExecution.getConfirmedDateTime());
        assertFalse(bbgExecution.getConfirmedDateTime().isAfter(beforeConfirm));
    }

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Arrange & Act
        bbgExecution.setTps2ExecutionId(999L);
        bbgExecution.setTradeLinkId(888L);
        bbgExecution.setBbgMessageId("NEW_BBG_MSG");
        bbgExecution.setAccountKey("NEW_ACC");
        bbgExecution.setCashAccount("NEW_CASH");
        bbgExecution.setISIN("NEW_ISIN");
        bbgExecution.setExecutedQuantity(500);
        bbgExecution.setExecutedPrice(BigDecimal.valueOf(200.75));
        bbgExecution.setConfirmed(true);

        LocalDateTime testTime = LocalDateTime.now();
        bbgExecution.setConfirmedDateTime(testTime);
        bbgExecution.setCreateTime(testTime);
        bbgExecution.setUpdateTime(testTime);

        // Assert
        assertEquals(999L, bbgExecution.getTps2ExecutionId());
        assertEquals(888L, bbgExecution.getTradeLinkId());
        assertEquals("NEW_BBG_MSG", bbgExecution.getBbgMessageId());
        assertEquals("NEW_ACC", bbgExecution.getAccountKey());
        assertEquals("NEW_CASH", bbgExecution.getCashAccount());
        assertEquals("NEW_ISIN", bbgExecution.getISIN());
        assertEquals(500, bbgExecution.getExecutedQuantity());
        assertEquals(0, BigDecimal.valueOf(200.75).compareTo(bbgExecution.getExecutedPrice()));
        assertTrue(bbgExecution.getConfirmed());
        assertEquals(testTime, bbgExecution.getConfirmedDateTime());
        assertEquals(testTime, bbgExecution.getCreateTime());
        assertEquals(testTime, bbgExecution.getUpdateTime());
    }

    @Test
    void objectState_AfterInitialization() {
        // Act
        bbgExecution.init();

        // Assert
        assertFalse(bbgExecution.getConfirmed());
        assertNotNull(bbgExecution.getCreateTime());
        assertNotNull(bbgExecution.getUpdateTime());
    }

    @Test
    void objectState_AfterConfirmed() {
        // Arrange
        bbgExecution.init();

        // Act
        bbgExecution.confirmed();

        // Assert
        assertTrue(bbgExecution.getConfirmed());
        assertTrue(bbgExecution.getUpdateTime().isAfter(bbgExecution.getCreateTime()));
        assertNotNull(bbgExecution.getConfirmedDateTime());
    }

    @Test
    void createTradeExecutedEvent_WithZeroQuantity_ShouldIncludeZero() {
        // Arrange
        bbgExecution.setExecutedQuantity(0);
        bbgExecution.setExecutedPrice(BigDecimal.ZERO);

        // Act
        TradeExecutedEvent event = bbgExecution.createTradeExecutedEvent();

        // Assert
        assertNotNull(event);
        assertEquals(0, event.getExecutedQuantity());
        assertEquals(0, BigDecimal.ZERO.compareTo(event.getExecutedPrice()));
    }

    @Test
    void createTradeExecutedEvent_WithLargeValues_ShouldHandleCorrectly() {
        // Arrange
        bbgExecution.setExecutedQuantity(1000000);
        bbgExecution.setExecutedPrice(BigDecimal.valueOf(999999.99));

        // Act
        TradeExecutedEvent event = bbgExecution.createTradeExecutedEvent();

        // Assert
        assertNotNull(event);
        assertEquals(1000000, event.getExecutedQuantity());
        assertEquals(0, BigDecimal.valueOf(999999.99).compareTo(event.getExecutedPrice()));
    }
}