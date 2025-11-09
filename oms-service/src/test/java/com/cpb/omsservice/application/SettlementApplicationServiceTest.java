package com.cpb.omsservice.application;

import com.cpb.oms.application.service.MessageSender;
import com.cpb.oms.application.service.SettlementApplicationService;
import com.cpb.oms.domain.event.OrderAmendedEvent;
import com.cpb.oms.domain.event.OrderEnrichedEvent;
import com.cpb.oms.domain.event.SettlementFailedEvent;
import com.cpb.oms.domain.model.settlement.SettlementInteract;
import com.cpb.oms.domain.model.settlement.SettlementResult;
import com.cpb.oms.domain.repository.SettlementInteractRepository;
import com.cpb.oms.domain.service.SettlementIntegrationService;
import com.cpb.oms.interfaces.settlement.SendToBankerRequest;
import com.cpb.oms.interfaces.settlement.SettlementTriggerRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettlementApplicationServiceTest {

    @Mock
    private SettlementInteractRepository settlementInteractRepository;

    @Mock
    private SettlementIntegrationService settlementIntegrationService;

    @Mock
    private MessageSender messageSender;

    @InjectMocks
    private SettlementApplicationService settlementApplicationService;

    @Test
    void trigger_Success_ShouldReturnTrue() {
        // Arrange
        Long settlementInteractId = 1001L;
        SettlementTriggerRequest request = new SettlementTriggerRequest();
        request.setSettlementInteractId(settlementInteractId);

        SettlementInteract mockSettlementInteract = mock(SettlementInteract.class);
        when(mockSettlementInteract.getId()).thenReturn(settlementInteractId);

        SettlementResult mockSettlementResult = mock(SettlementResult.class);
        when(mockSettlementResult.getSuccess()).thenReturn(true);

        when(settlementInteractRepository.getSettlementInteract(settlementInteractId)).thenReturn(mockSettlementInteract);
        when(settlementIntegrationService.settlement(mockSettlementInteract)).thenReturn(mockSettlementResult);

        // Act
        Boolean result = settlementApplicationService.trigger(request);

        // Assert
        assertTrue(result);
        verify(settlementInteractRepository).getSettlementInteract(settlementInteractId);
        verify(settlementIntegrationService).settlement(mockSettlementInteract);
        verify(mockSettlementInteract).saveSettlementResult(mockSettlementResult);
        verify(settlementInteractRepository).save(mockSettlementInteract);
    }

    @Test
    void trigger_Failure_ShouldReturnFalse() {
        // Arrange
        Long settlementInteractId = 1001L;
        SettlementTriggerRequest request = new SettlementTriggerRequest();
        request.setSettlementInteractId(settlementInteractId);

        SettlementInteract mockSettlementInteract = mock(SettlementInteract.class);
        when(mockSettlementInteract.getId()).thenReturn(settlementInteractId);

        SettlementResult mockSettlementResult = mock(SettlementResult.class);
        when(mockSettlementResult.getSuccess()).thenReturn(false);

        when(settlementInteractRepository.getSettlementInteract(settlementInteractId)).thenReturn(mockSettlementInteract);
        when(settlementIntegrationService.settlement(mockSettlementInteract)).thenReturn(mockSettlementResult);

        // Act
        Boolean result = settlementApplicationService.trigger(request);

        // Assert
        assertFalse(result);
        verify(settlementInteractRepository).getSettlementInteract(settlementInteractId);
        verify(settlementIntegrationService).settlement(mockSettlementInteract);
        verify(mockSettlementInteract).saveSettlementResult(mockSettlementResult);
        verify(settlementInteractRepository).save(mockSettlementInteract);
    }

    @Test
    void trigger_WhenSettlementInteractNotFound_ShouldThrowException() {
        // Arrange
        Long settlementInteractId = 1001L;
        SettlementTriggerRequest request = new SettlementTriggerRequest();
        request.setSettlementInteractId(settlementInteractId);

        when(settlementInteractRepository.getSettlementInteract(settlementInteractId)).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            settlementApplicationService.trigger(request);
        });

        verify(settlementInteractRepository).getSettlementInteract(settlementInteractId);
        verify(settlementIntegrationService, never()).settlement(any(SettlementInteract.class));
    }

    @Test
    void trigger_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Arrange
        Long settlementInteractId = 1001L;
        SettlementTriggerRequest request = new SettlementTriggerRequest();
        request.setSettlementInteractId(settlementInteractId);

        when(settlementInteractRepository.getSettlementInteract(settlementInteractId))
                .thenThrow(new RuntimeException("Repository error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            settlementApplicationService.trigger(request);
        });

        verify(settlementInteractRepository).getSettlementInteract(settlementInteractId);
        verify(settlementIntegrationService, never()).settlement(any(SettlementInteract.class));
    }

    @Test
    void get_Success_ShouldReturnSettlementInteract() {
        // Arrange
        Long settlementInteractId = 1001L;
        SettlementInteract mockSettlementInteract = mock(SettlementInteract.class);
        when(settlementInteractRepository.getSettlementInteract(settlementInteractId)).thenReturn(mockSettlementInteract);

        // Act
        SettlementInteract result = settlementApplicationService.get(settlementInteractId);

        // Assert
        assertNotNull(result);
        verify(settlementInteractRepository).getSettlementInteract(settlementInteractId);
    }

    @Test
    void get_WhenNotFound_ShouldThrowException() {
        // Arrange
        Long settlementInteractId = 1001L;
        when(settlementInteractRepository.getSettlementInteract(settlementInteractId)).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            settlementApplicationService.get(settlementInteractId);
        });

        verify(settlementInteractRepository).getSettlementInteract(settlementInteractId);
    }

    @Test
    void sendToBanker_Success_ShouldCompleteAllSteps() {
        // Arrange
        Long settlementInteractId = 1001L;
        SendToBankerRequest request = new SendToBankerRequest();
        request.setSettlementInteractId(settlementInteractId);

        SettlementInteract mockSettlementInteract = mock(SettlementInteract.class);
        SettlementFailedEvent mockEvent = mock(SettlementFailedEvent.class);
        when(mockEvent.getOrderId()).thenReturn(2001L);

        when(settlementInteractRepository.getSettlementInteract(settlementInteractId)).thenReturn(mockSettlementInteract);
        when(mockSettlementInteract.createSettlementFailedEvent()).thenReturn(mockEvent);

        // Act
        settlementApplicationService.sendToBanker(request);

        // Assert
        verify(settlementInteractRepository).getSettlementInteract(settlementInteractId);
        verify(mockSettlementInteract).createSettlementFailedEvent();
        verify(messageSender).send(mockEvent);
        verify(mockSettlementInteract).sendToBanker();
        verify(settlementInteractRepository).save(mockSettlementInteract);
    }

    @Test
    void sendToBanker_WhenSettlementInteractNotFound_ShouldThrowException() {
        // Arrange
        Long settlementInteractId = 1001L;
        SendToBankerRequest request = new SendToBankerRequest();
        request.setSettlementInteractId(settlementInteractId);

        when(settlementInteractRepository.getSettlementInteract(settlementInteractId)).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            settlementApplicationService.sendToBanker(request);
        });

        verify(settlementInteractRepository).getSettlementInteract(settlementInteractId);
        verify(messageSender, never()).send(any(SettlementFailedEvent.class));
    }

    @Test
    void sendToBanker_WhenMessageSenderThrowsException_ShouldPropagateException() {
        // Arrange
        Long settlementInteractId = 1001L;
        SendToBankerRequest request = new SendToBankerRequest();
        request.setSettlementInteractId(settlementInteractId);

        SettlementInteract mockSettlementInteract = mock(SettlementInteract.class);
        SettlementFailedEvent mockEvent = mock(SettlementFailedEvent.class);

        when(settlementInteractRepository.getSettlementInteract(settlementInteractId)).thenReturn(mockSettlementInteract);
        when(mockSettlementInteract.createSettlementFailedEvent()).thenReturn(mockEvent);
        doThrow(new RuntimeException("Message send error")).when(messageSender).send(mockEvent);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            settlementApplicationService.sendToBanker(request);
        });

        verify(settlementInteractRepository).getSettlementInteract(settlementInteractId);
        verify(mockSettlementInteract).createSettlementFailedEvent();
        verify(messageSender).send(mockEvent);
        verify(mockSettlementInteract, never()).sendToBanker();
        verify(settlementInteractRepository, never()).save(any(SettlementInteract.class));
    }

    @Test
    void handleOrderEnrichedEvent_WhenCanEnrich_ShouldProcessEnrichment() {
        // Arrange
        Long tps2ExecutionId = 3001L;
        OrderEnrichedEvent event = new OrderEnrichedEvent();
        event.setTps2ExecutionId(tps2ExecutionId);

        SettlementInteract mockSettlementInteract = mock(SettlementInteract.class);
        when(mockSettlementInteract.getId()).thenReturn(1001L);

        SettlementResult mockSettlementResult = mock(SettlementResult.class);

        when(settlementInteractRepository.getSettlementInteractByTPS2ExecutionId(tps2ExecutionId)).thenReturn(mockSettlementInteract);
        when(mockSettlementInteract.canEnrich()).thenReturn(true);
        when(settlementIntegrationService.settlement(mockSettlementInteract)).thenReturn(mockSettlementResult);

        // Act
        settlementApplicationService.handle(event);

        // Assert
        verify(settlementInteractRepository).getSettlementInteractByTPS2ExecutionId(tps2ExecutionId);
        verify(mockSettlementInteract).canEnrich();
        verify(mockSettlementInteract).enrich(event);
        verify(settlementIntegrationService).settlement(mockSettlementInteract);
        verify(mockSettlementInteract).saveSettlementResult(mockSettlementResult);
        verify(settlementInteractRepository).save(mockSettlementInteract);
    }

    @Test
    void handleOrderEnrichedEvent_WhenCannotEnrich_ShouldSkipProcessing() {
        // Arrange
        Long tps2ExecutionId = 3001L;
        OrderEnrichedEvent event = new OrderEnrichedEvent();
        event.setTps2ExecutionId(tps2ExecutionId);

        SettlementInteract mockSettlementInteract = mock(SettlementInteract.class);
        when(mockSettlementInteract.getId()).thenReturn(1001L);

        when(settlementInteractRepository.getSettlementInteractByTPS2ExecutionId(tps2ExecutionId)).thenReturn(mockSettlementInteract);
        when(mockSettlementInteract.canEnrich()).thenReturn(false);

        // Act
        settlementApplicationService.handle(event);

        // Assert
        verify(settlementInteractRepository).getSettlementInteractByTPS2ExecutionId(tps2ExecutionId);
        verify(mockSettlementInteract).canEnrich();
        verify(mockSettlementInteract, never()).enrich(any(OrderEnrichedEvent.class));
        verify(settlementIntegrationService, never()).settlement(any(SettlementInteract.class));
    }

    @Test
    void handleOrderAmendedEvent_WhenCanAmend_ShouldProcessAmendment() {
        // Arrange
        Long tps2ExecutionId = 3001L;
        OrderAmendedEvent event = new OrderAmendedEvent();
        event.setTps2ExecutionId(tps2ExecutionId);

        SettlementInteract mockSettlementInteract = mock(SettlementInteract.class);
        when(mockSettlementInteract.getId()).thenReturn(1001L);

        SettlementResult mockSettlementResult = mock(SettlementResult.class);

        when(settlementInteractRepository.getSettlementInteractByTPS2ExecutionId(tps2ExecutionId)).thenReturn(mockSettlementInteract);
        when(mockSettlementInteract.canAmend()).thenReturn(true);
        when(settlementIntegrationService.settlement(mockSettlementInteract)).thenReturn(mockSettlementResult);

        // Act
        settlementApplicationService.handle(event);

        // Assert
        verify(settlementInteractRepository).getSettlementInteractByTPS2ExecutionId(tps2ExecutionId);
        verify(mockSettlementInteract).canAmend();
        verify(mockSettlementInteract).amend(event);
        verify(settlementIntegrationService).settlement(mockSettlementInteract);
        verify(mockSettlementInteract).saveSettlementResult(mockSettlementResult);
        verify(settlementInteractRepository).save(mockSettlementInteract);
    }

    @Test
    void handleOrderAmendedEvent_WhenCannotAmend_ShouldSkipProcessing() {
        // Arrange
        Long tps2ExecutionId = 3001L;
        OrderAmendedEvent event = new OrderAmendedEvent();
        event.setTps2ExecutionId(tps2ExecutionId);

        SettlementInteract mockSettlementInteract = mock(SettlementInteract.class);
        when(mockSettlementInteract.getId()).thenReturn(1001L);

        when(settlementInteractRepository.getSettlementInteractByTPS2ExecutionId(tps2ExecutionId)).thenReturn(mockSettlementInteract);
        when(mockSettlementInteract.canAmend()).thenReturn(false);

        // Act
        settlementApplicationService.handle(event);

        // Assert
        verify(settlementInteractRepository).getSettlementInteractByTPS2ExecutionId(tps2ExecutionId);
        verify(mockSettlementInteract).canAmend();
        verify(mockSettlementInteract, never()).amend(any(OrderAmendedEvent.class));
        verify(settlementIntegrationService, never()).settlement(any(SettlementInteract.class));
    }

    @Test
    void handleOrderEnrichedEvent_WhenSettlementInteractNotFound_ShouldThrowException() {
        // Arrange
        Long tps2ExecutionId = 3001L;
        OrderEnrichedEvent event = new OrderEnrichedEvent();
        event.setTps2ExecutionId(tps2ExecutionId);

        when(settlementInteractRepository.getSettlementInteractByTPS2ExecutionId(tps2ExecutionId)).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            settlementApplicationService.handle(event);
        });

        verify(settlementInteractRepository).getSettlementInteractByTPS2ExecutionId(tps2ExecutionId);
    }

    @Test
    void handleOrderAmendedEvent_WhenSettlementInteractNotFound_ShouldThrowException() {
        // Arrange
        Long tps2ExecutionId = 3001L;
        OrderAmendedEvent event = new OrderAmendedEvent();
        event.setTps2ExecutionId(tps2ExecutionId);

        when(settlementInteractRepository.getSettlementInteractByTPS2ExecutionId(tps2ExecutionId)).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            settlementApplicationService.handle(event);
        });

        verify(settlementInteractRepository).getSettlementInteractByTPS2ExecutionId(tps2ExecutionId);
    }

    @Test
    void handleOrderEnrichedEvent_WhenSettlementServiceThrowsException_ShouldPropagateException() {
        // Arrange
        Long tps2ExecutionId = 3001L;
        OrderEnrichedEvent event = new OrderEnrichedEvent();
        event.setTps2ExecutionId(tps2ExecutionId);

        SettlementInteract mockSettlementInteract = mock(SettlementInteract.class);
        when(mockSettlementInteract.getId()).thenReturn(1001L);

        when(settlementInteractRepository.getSettlementInteractByTPS2ExecutionId(tps2ExecutionId)).thenReturn(mockSettlementInteract);
        when(mockSettlementInteract.canEnrich()).thenReturn(true);
        when(settlementIntegrationService.settlement(mockSettlementInteract))
                .thenThrow(new RuntimeException("Settlement service error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            settlementApplicationService.handle(event);
        });

        verify(settlementInteractRepository).getSettlementInteractByTPS2ExecutionId(tps2ExecutionId);
        verify(mockSettlementInteract).canEnrich();
        verify(mockSettlementInteract).enrich(event);
        verify(settlementIntegrationService).settlement(mockSettlementInteract);
        verify(mockSettlementInteract, never()).saveSettlementResult(any(SettlementResult.class));
        verify(settlementInteractRepository, never()).save(any(SettlementInteract.class));
    }
}