package com.cpb.omsservice.application;

import com.cpb.oms.application.builder.BBGExecutionBuilder;
import com.cpb.oms.application.service.ExecutionApplicationService;
import com.cpb.oms.application.service.MessageSender;
import com.cpb.oms.domain.event.BBGExecutionMessage;
import com.cpb.oms.domain.event.TradeExecutedEvent;
import com.cpb.oms.domain.model.executed.BBGExecution;
import com.cpb.oms.domain.model.settlement.SettlementInteract;
import com.cpb.oms.domain.model.settlement.SettlementResult;
import com.cpb.oms.domain.repository.ExecutedRepository;
import com.cpb.oms.domain.repository.SettlementInteractRepository;
import com.cpb.oms.domain.service.SettlementIntegrationService;
import com.cpb.oms.domain.service.SettlementInteractService;
import com.cpb.oms.interfaces.executed.ExecutionConfirmedRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExecutionApplicationServiceTest {

    @Mock
    private MessageSender messageSender;

    @Mock
    private BBGExecutionBuilder bbgExecutionBuilder;

    @Mock
    private ExecutedRepository executedRepository;

    @Mock
    private SettlementInteractRepository settlementInteractRepository;

    @Mock
    private SettlementInteractService settlementInteractService;

    @Mock
    private SettlementIntegrationService settlementIntegrationService;

    @InjectMocks
    private ExecutionApplicationService executionApplicationService;

    @Test
    void executed_Success_ShouldCompleteAllSteps() {
        // Arrange
        BBGExecutionMessage bbgExecutionMessage = new BBGExecutionMessage();
        bbgExecutionMessage.setBbgMessageId("BBG_MSG_001");
        bbgExecutionMessage.setUniqueId(1L);

        BBGExecution mockBBGExecution = mock(BBGExecution.class);
        when(bbgExecutionBuilder.buildBBGExecution(bbgExecutionMessage)).thenReturn(mockBBGExecution);

        // Act
        executionApplicationService.executed(bbgExecutionMessage);

        // Assert
        verify(bbgExecutionBuilder).buildBBGExecution(bbgExecutionMessage);
        verify(mockBBGExecution).init();
        verify(executedRepository).save(mockBBGExecution);
    }

    @Test
    void executed_WhenBuilderThrowsException_ShouldPropagateException() {
        // Arrange
        BBGExecutionMessage bbgExecutionMessage = new BBGExecutionMessage();
        when(bbgExecutionBuilder.buildBBGExecution(bbgExecutionMessage))
                .thenThrow(new RuntimeException("Build error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            executionApplicationService.executed(bbgExecutionMessage);
        });

        verify(bbgExecutionBuilder).buildBBGExecution(bbgExecutionMessage);
        verify(executedRepository, never()).save(any(BBGExecution.class));
    }

    @Test
    void executed_WhenRepositorySaveThrowsException_ShouldPropagateException() {
        // Arrange
        BBGExecutionMessage bbgExecutionMessage = new BBGExecutionMessage();
        BBGExecution mockBBGExecution = mock(BBGExecution.class);
        when(bbgExecutionBuilder.buildBBGExecution(bbgExecutionMessage)).thenReturn(mockBBGExecution);
        doThrow(new RuntimeException("Save error")).when(executedRepository).save(mockBBGExecution);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            executionApplicationService.executed(bbgExecutionMessage);
        });

        verify(bbgExecutionBuilder).buildBBGExecution(bbgExecutionMessage);
        verify(mockBBGExecution).init();
        verify(executedRepository).save(mockBBGExecution);
    }

    @Test
    void confirmed_LiveOrderWithCashAccount_ShouldCompleteFullProcess() {
        // Arrange
        Long tps2ExecutionId = 1001L;
        ExecutionConfirmedRequest request = new ExecutionConfirmedRequest();
        request.setTps2ExecutionId(tps2ExecutionId);

        BBGExecution mockBBGExecution = mock(BBGExecution.class);
        when(mockBBGExecution.getTps2ExecutionId()).thenReturn(tps2ExecutionId);
        when(mockBBGExecution.getCashAccount()).thenReturn("CASH001");
        when(mockBBGExecution.createTradeExecutedEvent()).thenReturn(mock(TradeExecutedEvent.class));

        SettlementInteract mockSettlementInteract = mock(SettlementInteract.class);
        when(mockSettlementInteract.getId()).thenReturn(2001L);
        when(mockSettlementInteract.getTps2ExecutionId()).thenReturn(tps2ExecutionId);

        SettlementResult mockSettlementResult = mock(SettlementResult.class);
        when(mockSettlementResult.getSuccess()).thenReturn(true);
        when(mockSettlementResult.getFailedReason()).thenReturn("");

        when(executedRepository.get(tps2ExecutionId)).thenReturn(mockBBGExecution);
        when(settlementInteractService.buildSettlementInteract(mockBBGExecution)).thenReturn(mockSettlementInteract);
        when(settlementIntegrationService.settlement(mockSettlementInteract)).thenReturn(mockSettlementResult);

        // Act
        executionApplicationService.confirmed(request);

        // Assert
        verify(executedRepository).get(tps2ExecutionId);
        verify(mockBBGExecution).confirmed();
        verify(executedRepository).save(mockBBGExecution);
        verify(mockBBGExecution).createTradeExecutedEvent();
        verify(messageSender).send(any(TradeExecutedEvent.class));
        verify(settlementInteractService).buildSettlementInteract(mockBBGExecution);
        verify(mockSettlementInteract).init();
        verify(settlementInteractRepository, times(2)).save(mockSettlementInteract);
        verify(settlementIntegrationService).settlement(mockSettlementInteract);
        verify(mockSettlementInteract).saveSettlementResult(mockSettlementResult);
    }

    @Test
    void confirmed_PhoneOrderWithoutCashAccount_ShouldSkipSettlement() {
        // Arrange
        Long tps2ExecutionId = 1001L;
        ExecutionConfirmedRequest request = new ExecutionConfirmedRequest();
        request.setTps2ExecutionId(tps2ExecutionId);

        BBGExecution mockBBGExecution = mock(BBGExecution.class);
        when(mockBBGExecution.getTps2ExecutionId()).thenReturn(tps2ExecutionId);
        when(mockBBGExecution.getCashAccount()).thenReturn("");
        when(mockBBGExecution.createTradeExecutedEvent()).thenReturn(mock(TradeExecutedEvent.class));

        when(executedRepository.get(tps2ExecutionId)).thenReturn(mockBBGExecution);

        // Act
        executionApplicationService.confirmed(request);

        // Assert
        verify(executedRepository).get(tps2ExecutionId);
        verify(mockBBGExecution).confirmed();
        verify(executedRepository).save(mockBBGExecution);
        verify(mockBBGExecution).createTradeExecutedEvent();
        verify(messageSender).send(any(TradeExecutedEvent.class));
        verify(settlementInteractService, never()).buildSettlementInteract(any(BBGExecution.class));
        verify(settlementInteractRepository, never()).save(any(SettlementInteract.class));
        verify(settlementIntegrationService, never()).settlement(any(SettlementInteract.class));
    }

    @Test
    void confirmed_WhenBBGExecutionNotFound_ShouldThrowException() {
        // Arrange
        Long tps2ExecutionId = 1001L;
        ExecutionConfirmedRequest request = new ExecutionConfirmedRequest();
        request.setTps2ExecutionId(tps2ExecutionId);

        when(executedRepository.get(tps2ExecutionId)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            executionApplicationService.confirmed(request);
        });

        assertEquals("BBGExecution not found for Tps2ExecutionId: " + tps2ExecutionId, exception.getMessage());
        verify(executedRepository).get(tps2ExecutionId);
        verify(executedRepository, never()).save(any(BBGExecution.class));
    }

    @Test
    void confirmed_WhenRepositoryGetThrowsException_ShouldPropagateException() {
        // Arrange
        Long tps2ExecutionId = 1001L;
        ExecutionConfirmedRequest request = new ExecutionConfirmedRequest();
        request.setTps2ExecutionId(tps2ExecutionId);

        when(executedRepository.get(tps2ExecutionId)).thenThrow(new RuntimeException("Repository error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            executionApplicationService.confirmed(request);
        });

        verify(executedRepository).get(tps2ExecutionId);
        verify(executedRepository, never()).save(any(BBGExecution.class));
    }

    @Test
    void confirmed_WhenSettlementServiceThrowsException_ShouldPropagateException() {
        // Arrange
        Long tps2ExecutionId = 1001L;
        ExecutionConfirmedRequest request = new ExecutionConfirmedRequest();
        request.setTps2ExecutionId(tps2ExecutionId);

        BBGExecution mockBBGExecution = mock(BBGExecution.class);
        when(mockBBGExecution.getTps2ExecutionId()).thenReturn(tps2ExecutionId);
        when(mockBBGExecution.getCashAccount()).thenReturn("CASH001");
        when(mockBBGExecution.createTradeExecutedEvent()).thenReturn(mock(TradeExecutedEvent.class));

        SettlementInteract mockSettlementInteract = mock(SettlementInteract.class);

        when(executedRepository.get(tps2ExecutionId)).thenReturn(mockBBGExecution);
        when(settlementInteractService.buildSettlementInteract(mockBBGExecution)).thenReturn(mockSettlementInteract);
        when(settlementIntegrationService.settlement(mockSettlementInteract))
                .thenThrow(new RuntimeException("Settlement error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            executionApplicationService.confirmed(request);
        });

        verify(executedRepository).get(tps2ExecutionId);
        verify(mockBBGExecution).confirmed();
        verify(executedRepository).save(mockBBGExecution);
        verify(messageSender).send(any(TradeExecutedEvent.class));
        verify(settlementInteractService).buildSettlementInteract(mockBBGExecution);
        verify(mockSettlementInteract).init();
        verify(settlementInteractRepository).save(mockSettlementInteract);
        verify(settlementIntegrationService).settlement(mockSettlementInteract);
        verify(mockSettlementInteract, never()).saveSettlementResult(any(SettlementResult.class));
    }

    @Test
    void get_Success_ShouldReturnBBGExecution() {
        // Arrange
        Long tps2ExecutionId = 1001L;
        BBGExecution mockBBGExecution = mock(BBGExecution.class);
        when(executedRepository.get(tps2ExecutionId)).thenReturn(mockBBGExecution);

        // Act
        BBGExecution result = executionApplicationService.get(tps2ExecutionId);

        // Assert
        assertNotNull(result);
        verify(executedRepository).get(tps2ExecutionId);
    }

    @Test
    void get_WhenNotFound_ShouldReturnNull() {
        // Arrange
        Long tps2ExecutionId = 1001L;
        when(executedRepository.get(tps2ExecutionId)).thenReturn(null);

        // Act
        BBGExecution result = executionApplicationService.get(tps2ExecutionId);

        // Assert
        assertNull(result);
        verify(executedRepository).get(tps2ExecutionId);
    }

    @Test
    void get_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Arrange
        Long tps2ExecutionId = 1001L;
        when(executedRepository.get(tps2ExecutionId)).thenThrow(new RuntimeException("Repository error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            executionApplicationService.get(tps2ExecutionId);
        });

        verify(executedRepository).get(tps2ExecutionId);
    }

    @Test
    void confirmed_WithNullCashAccount_ShouldSkipSettlement() {
        // Arrange
        Long tps2ExecutionId = 1001L;
        ExecutionConfirmedRequest request = new ExecutionConfirmedRequest();
        request.setTps2ExecutionId(tps2ExecutionId);

        BBGExecution mockBBGExecution = mock(BBGExecution.class);
        when(mockBBGExecution.getTps2ExecutionId()).thenReturn(tps2ExecutionId);
        when(mockBBGExecution.getCashAccount()).thenReturn(null);
        when(mockBBGExecution.createTradeExecutedEvent()).thenReturn(mock(TradeExecutedEvent.class));

        when(executedRepository.get(tps2ExecutionId)).thenReturn(mockBBGExecution);

        // Act
        executionApplicationService.confirmed(request);

        // Assert
        verify(executedRepository).get(tps2ExecutionId);
        verify(mockBBGExecution).confirmed();
        verify(executedRepository).save(mockBBGExecution);
        verify(messageSender).send(any(TradeExecutedEvent.class));
        verify(settlementInteractService, never()).buildSettlementInteract(any(BBGExecution.class));
    }

    @Test
    void confirmed_WithFailedSettlement_ShouldContinueProcess() {
        // Arrange
        Long tps2ExecutionId = 1001L;
        ExecutionConfirmedRequest request = new ExecutionConfirmedRequest();
        request.setTps2ExecutionId(tps2ExecutionId);

        BBGExecution mockBBGExecution = mock(BBGExecution.class);
        when(mockBBGExecution.getTps2ExecutionId()).thenReturn(tps2ExecutionId);
        when(mockBBGExecution.getCashAccount()).thenReturn("CASH001");
        when(mockBBGExecution.createTradeExecutedEvent()).thenReturn(mock(TradeExecutedEvent.class));

        SettlementInteract mockSettlementInteract = mock(SettlementInteract.class);
        when(mockSettlementInteract.getId()).thenReturn(2001L);
        when(mockSettlementInteract.getTps2ExecutionId()).thenReturn(tps2ExecutionId);

        SettlementResult mockSettlementResult = mock(SettlementResult.class);
        when(mockSettlementResult.getSuccess()).thenReturn(false);
        when(mockSettlementResult.getFailedReason()).thenReturn("Insufficient funds");

        when(executedRepository.get(tps2ExecutionId)).thenReturn(mockBBGExecution);
        when(settlementInteractService.buildSettlementInteract(mockBBGExecution)).thenReturn(mockSettlementInteract);
        when(settlementIntegrationService.settlement(mockSettlementInteract)).thenReturn(mockSettlementResult);

        // Act
        executionApplicationService.confirmed(request);

        // Assert
        verify(executedRepository).get(tps2ExecutionId);
        verify(mockBBGExecution).confirmed();
        verify(executedRepository).save(mockBBGExecution);
        verify(messageSender).send(any(TradeExecutedEvent.class));
        verify(settlementInteractService).buildSettlementInteract(mockBBGExecution);
        verify(mockSettlementInteract).init();
        verify(settlementInteractRepository, times(2)).save(mockSettlementInteract);
        verify(settlementIntegrationService).settlement(mockSettlementInteract);
        verify(mockSettlementInteract).saveSettlementResult(mockSettlementResult);
    }
}