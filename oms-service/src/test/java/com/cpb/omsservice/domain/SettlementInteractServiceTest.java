package com.cpb.omsservice.domain;

import com.cpb.oms.domain.model.executed.BBGExecution;
import com.cpb.oms.domain.model.settlement.SettlementInteract;
import com.cpb.oms.domain.model.trading.TradingInstruction;
import com.cpb.oms.domain.repository.TradingInstructionRepository;
import com.cpb.oms.domain.service.SettlementInteractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettlementInteractServiceTest {

    @Mock
    private TradingInstructionRepository tradingInstructionRepository;

    @InjectMocks
    private SettlementInteractService settlementInteractService;

    @Test
    void buildSettlementInteract_Success_ShouldBuildCorrectly() {
        // Arrange
        Long tradeLinkId = 1001L;
        Long tps2ExecutionId = 2001L;
        
        BBGExecution bbgExecution = new BBGExecution();
        bbgExecution.setTradeLinkId(tradeLinkId);
        bbgExecution.setTps2ExecutionId(tps2ExecutionId);
        bbgExecution.setISIN("US0378331005");
        bbgExecution.setExecutedQuantity(100);
        bbgExecution.setExecutedPrice(BigDecimal.valueOf(150.50));

        TradingInstruction tradingInstruction = new TradingInstruction();
        tradingInstruction.setCashAccount("CASH001");

        when(tradingInstructionRepository.getTradingInstructionByTradeLinkId(tradeLinkId))
                .thenReturn(tradingInstruction);

        // Act
        SettlementInteract result = settlementInteractService.buildSettlementInteract(bbgExecution);

        // Assert
        assertNotNull(result);
        assertEquals(tradeLinkId, result.getTradeLinkId());
        assertEquals(tps2ExecutionId, result.getTps2ExecutionId());
        assertEquals("CASH001", result.getCashAccount());
        assertEquals("US0378331005", result.getIsin());
        assertEquals(100, result.getExecutedQuantity());
        assertEquals(0, BigDecimal.valueOf(150.50).compareTo(result.getExecutedPrice()));
        
        verify(tradingInstructionRepository).getTradingInstructionByTradeLinkId(tradeLinkId);
    }

    @Test
    void buildSettlementInteract_WhenTradingInstructionNotFound_ShouldThrowException() {
        // Arrange
        Long tradeLinkId = 1001L;
        
        BBGExecution bbgExecution = new BBGExecution();
        bbgExecution.setTradeLinkId(tradeLinkId);
        bbgExecution.setTps2ExecutionId(2001L);
        bbgExecution.setISIN("US0378331005");
        bbgExecution.setExecutedQuantity(100);
        bbgExecution.setExecutedPrice(BigDecimal.valueOf(150.50));

        when(tradingInstructionRepository.getTradingInstructionByTradeLinkId(tradeLinkId))
                .thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            settlementInteractService.buildSettlementInteract(bbgExecution);
        });

        verify(tradingInstructionRepository).getTradingInstructionByTradeLinkId(tradeLinkId);
    }

    @Test
    void buildSettlementInteract_WithNullBBGExecution_ShouldThrowNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            settlementInteractService.buildSettlementInteract(null);
        });
    }

    @Test
    void buildSettlementInteract_WithNullTradeLinkId_ShouldHandleGracefully() {
        // Arrange
        BBGExecution bbgExecution = new BBGExecution();
        bbgExecution.setTradeLinkId(null);
        bbgExecution.setTps2ExecutionId(2001L);
        bbgExecution.setISIN("US0378331005");
        bbgExecution.setExecutedQuantity(100);
        bbgExecution.setExecutedPrice(BigDecimal.valueOf(150.50));

        when(tradingInstructionRepository.getTradingInstructionByTradeLinkId(null))
                .thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            settlementInteractService.buildSettlementInteract(bbgExecution);
        });

        verify(tradingInstructionRepository).getTradingInstructionByTradeLinkId(null);
    }

    @Test
    void buildSettlementInteract_WithNullValuesInBBGExecution_ShouldBuildWithNullValues() {
        // Arrange
        Long tradeLinkId = 1001L;
        
        BBGExecution bbgExecution = new BBGExecution();
        bbgExecution.setTradeLinkId(tradeLinkId);
        bbgExecution.setTps2ExecutionId(null);
        bbgExecution.setISIN(null);
        bbgExecution.setExecutedQuantity(null);
        bbgExecution.setExecutedPrice(null);

        TradingInstruction tradingInstruction = new TradingInstruction();
        tradingInstruction.setCashAccount("CASH001");

        when(tradingInstructionRepository.getTradingInstructionByTradeLinkId(tradeLinkId))
                .thenReturn(tradingInstruction);

        // Act
        SettlementInteract result = settlementInteractService.buildSettlementInteract(bbgExecution);

        // Assert
        assertNotNull(result);
        assertEquals(tradeLinkId, result.getTradeLinkId());
        assertNull(result.getTps2ExecutionId());
        assertEquals("CASH001", result.getCashAccount());
        assertNull(result.getIsin());
        assertNull(result.getExecutedQuantity());
        assertNull(result.getExecutedPrice());
        
        verify(tradingInstructionRepository).getTradingInstructionByTradeLinkId(tradeLinkId);
    }

    @Test
    void buildSettlementInteract_WithTradingInstructionHavingNullCashAccount_ShouldSetNullCashAccount() {
        // Arrange
        Long tradeLinkId = 1001L;
        
        BBGExecution bbgExecution = new BBGExecution();
        bbgExecution.setTradeLinkId(tradeLinkId);
        bbgExecution.setTps2ExecutionId(2001L);
        bbgExecution.setISIN("US0378331005");
        bbgExecution.setExecutedQuantity(100);
        bbgExecution.setExecutedPrice(BigDecimal.valueOf(150.50));

        TradingInstruction tradingInstruction = new TradingInstruction();
        tradingInstruction.setCashAccount(null);

        when(tradingInstructionRepository.getTradingInstructionByTradeLinkId(tradeLinkId))
                .thenReturn(tradingInstruction);

        // Act
        SettlementInteract result = settlementInteractService.buildSettlementInteract(bbgExecution);

        // Assert
        assertNotNull(result);
        assertEquals(tradeLinkId, result.getTradeLinkId());
        assertEquals(2001L, result.getTps2ExecutionId());
        assertNull(result.getCashAccount());
        assertEquals("US0378331005", result.getIsin());
        assertEquals(100, result.getExecutedQuantity());
        assertEquals(0, BigDecimal.valueOf(150.50).compareTo(result.getExecutedPrice()));
        
        verify(tradingInstructionRepository).getTradingInstructionByTradeLinkId(tradeLinkId);
    }

    @Test
    void buildSettlementInteract_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Arrange
        Long tradeLinkId = 1001L;
        
        BBGExecution bbgExecution = new BBGExecution();
        bbgExecution.setTradeLinkId(tradeLinkId);
        bbgExecution.setTps2ExecutionId(2001L);
        bbgExecution.setISIN("US0378331005");
        bbgExecution.setExecutedQuantity(100);
        bbgExecution.setExecutedPrice(BigDecimal.valueOf(150.50));

        when(tradingInstructionRepository.getTradingInstructionByTradeLinkId(tradeLinkId))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            settlementInteractService.buildSettlementInteract(bbgExecution);
        });

        verify(tradingInstructionRepository).getTradingInstructionByTradeLinkId(tradeLinkId);
    }

    @Test
    void buildSettlementInteract_WithZeroValues_ShouldBuildCorrectly() {
        // Arrange
        Long tradeLinkId = 1001L;
        
        BBGExecution bbgExecution = new BBGExecution();
        bbgExecution.setTradeLinkId(tradeLinkId);
        bbgExecution.setTps2ExecutionId(2001L);
        bbgExecution.setISIN("US0378331005");
        bbgExecution.setExecutedQuantity(0);
        bbgExecution.setExecutedPrice(BigDecimal.ZERO);

        TradingInstruction tradingInstruction = new TradingInstruction();
        tradingInstruction.setCashAccount("CASH001");

        when(tradingInstructionRepository.getTradingInstructionByTradeLinkId(tradeLinkId))
                .thenReturn(tradingInstruction);

        // Act
        SettlementInteract result = settlementInteractService.buildSettlementInteract(bbgExecution);

        // Assert
        assertNotNull(result);
        assertEquals(tradeLinkId, result.getTradeLinkId());
        assertEquals(2001L, result.getTps2ExecutionId());
        assertEquals("CASH001", result.getCashAccount());
        assertEquals("US0378331005", result.getIsin());
        assertEquals(0, result.getExecutedQuantity());
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getExecutedPrice()));
        
        verify(tradingInstructionRepository).getTradingInstructionByTradeLinkId(tradeLinkId);
    }

    @Test
    void buildSettlementInteract_WithLargeValues_ShouldBuildCorrectly() {
        // Arrange
        Long tradeLinkId = 1001L;
        
        BBGExecution bbgExecution = new BBGExecution();
        bbgExecution.setTradeLinkId(tradeLinkId);
        bbgExecution.setTps2ExecutionId(999999999L);
        bbgExecution.setISIN("US0378331005");
        bbgExecution.setExecutedQuantity(1000000);
        bbgExecution.setExecutedPrice(BigDecimal.valueOf(999999.99));

        TradingInstruction tradingInstruction = new TradingInstruction();
        tradingInstruction.setCashAccount("CASH001");

        when(tradingInstructionRepository.getTradingInstructionByTradeLinkId(tradeLinkId))
                .thenReturn(tradingInstruction);

        // Act
        SettlementInteract result = settlementInteractService.buildSettlementInteract(bbgExecution);

        // Assert
        assertNotNull(result);
        assertEquals(tradeLinkId, result.getTradeLinkId());
        assertEquals(999999999L, result.getTps2ExecutionId());
        assertEquals("CASH001", result.getCashAccount());
        assertEquals("US0378331005", result.getIsin());
        assertEquals(1000000, result.getExecutedQuantity());
        assertEquals(0, BigDecimal.valueOf(999999.99).compareTo(result.getExecutedPrice()));
        
        verify(tradingInstructionRepository).getTradingInstructionByTradeLinkId(tradeLinkId);
    }
}