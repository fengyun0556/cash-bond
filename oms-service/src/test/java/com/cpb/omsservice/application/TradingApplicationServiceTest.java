package com.cpb.omsservice.application;

import com.cpb.oms.application.builder.TradingBuilder;
import com.cpb.oms.application.service.MessageSender;
import com.cpb.oms.application.service.TradingApplicationService;
import com.cpb.oms.domain.model.trading.TradingInstruction;
import com.cpb.oms.domain.repository.TradingInstructionRepository;
import com.cpb.oms.domain.event.TradeSubmissionEvent;
import com.cpb.oms.domain.model.trading.BBGAckMessage;
import com.cpb.oms.interfaces.trading.OrderCreationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradingApplicationServiceTest {

    @Mock
    private TradingBuilder tradingBuilder;

    @Mock
    private TradingInstructionRepository tradingInstructionRepository;

    @Mock
    private MessageSender messageSender;

    @InjectMocks
    private TradingApplicationService tradingApplicationService;

    private OrderCreationRequest orderCreationRequest;
    private BBGAckMessage bbgAckMessage;
    private Long tradeLinkId;
    private Long orderId;

    @BeforeEach
    void setUp() {
        tradeLinkId = 1001L;
        orderId = 2001L;

        orderCreationRequest = new OrderCreationRequest();
        orderCreationRequest.setOrderId(orderId);
        orderCreationRequest.setQuantity(100);

        bbgAckMessage = new BBGAckMessage();
        bbgAckMessage.setTradeLinkId(tradeLinkId);
    }

    // 辅助方法，创建 mock TradingInstruction
    private TradingInstruction createMockTradingInstruction(Long tradeLinkId) {
        TradingInstruction mockInstruction = mock(TradingInstruction.class);
        when(mockInstruction.getTradeLinkId()).thenReturn(tradeLinkId);
        return mockInstruction;
    }

    @Test
    void submitOrder_Success_ShouldCompleteAllSteps() {
        // Arrange
        TradingInstruction mockTradingInstruction = createMockTradingInstruction(tradeLinkId);
        when(tradingBuilder.buildTradingInstruction(orderCreationRequest)).thenReturn(mockTradingInstruction);
        when(mockTradingInstruction.createTradeSubmissionEvent()).thenReturn(mock(TradeSubmissionEvent.class));

        // Act
        TradingInstruction result = tradingApplicationService.submitOrder(orderCreationRequest);

        // Assert
        assertNotNull(result);
        verify(tradingBuilder).buildTradingInstruction(orderCreationRequest);
        verify(mockTradingInstruction).init();
        verify(mockTradingInstruction).createTradeSubmissionEvent();
        verify(messageSender).send(any(TradeSubmissionEvent.class));
        verify(tradingInstructionRepository).save(mockTradingInstruction);
    }

    @Test
    void submitOrder_WhenBuilderThrowsException_ShouldPropagateException() {
        // Arrange
        when(tradingBuilder.buildTradingInstruction(orderCreationRequest))
                .thenThrow(new RuntimeException("Build error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            tradingApplicationService.submitOrder(orderCreationRequest);
        });

        verify(tradingBuilder).buildTradingInstruction(orderCreationRequest);
        // 这里不需要验证 mockTradingInstruction 的方法，因为构建失败不会创建对象
    }

    @Test
    void submitOrder_WhenMessageSenderThrowsException_ShouldPropagateException() {
        // Arrange
        TradingInstruction mockTradingInstruction = createMockTradingInstruction(tradeLinkId);
        when(tradingBuilder.buildTradingInstruction(orderCreationRequest)).thenReturn(mockTradingInstruction);
        when(mockTradingInstruction.createTradeSubmissionEvent()).thenReturn(mock(TradeSubmissionEvent.class));
        doThrow(new RuntimeException("Message send error")).when(messageSender).send(any(TradeSubmissionEvent.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            tradingApplicationService.submitOrder(orderCreationRequest);
        });

        verify(tradingBuilder).buildTradingInstruction(orderCreationRequest);
        verify(mockTradingInstruction).init();
        verify(mockTradingInstruction).createTradeSubmissionEvent();
        verify(messageSender).send(any(TradeSubmissionEvent.class));
        verify(tradingInstructionRepository, never()).save(any(TradingInstruction.class));
    }

    @Test
    void submitOrder_WhenRepositorySaveThrowsException_ShouldPropagateException() {
        // Arrange
        TradingInstruction mockTradingInstruction = createMockTradingInstruction(tradeLinkId);
        when(tradingBuilder.buildTradingInstruction(orderCreationRequest)).thenReturn(mockTradingInstruction);
        when(mockTradingInstruction.createTradeSubmissionEvent()).thenReturn(mock(TradeSubmissionEvent.class));
        doThrow(new RuntimeException("Save error")).when(tradingInstructionRepository).save(mockTradingInstruction);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            tradingApplicationService.submitOrder(orderCreationRequest);
        });

        verify(tradingBuilder).buildTradingInstruction(orderCreationRequest);
        verify(mockTradingInstruction).init();
        verify(mockTradingInstruction).createTradeSubmissionEvent();
        verify(messageSender).send(any(TradeSubmissionEvent.class));
        verify(tradingInstructionRepository).save(mockTradingInstruction);
    }

    @Test
    void ack_Success_ShouldUpdateTradingInstruction() {
        // Arrange
        TradingInstruction mockTradingInstruction = createMockTradingInstruction(tradeLinkId);
        when(tradingInstructionRepository.getTradingInstructionByTradeLinkId(tradeLinkId))
                .thenReturn(mockTradingInstruction);

        // Act
        tradingApplicationService.ack(bbgAckMessage);

        // Assert
        verify(tradingInstructionRepository).getTradingInstructionByTradeLinkId(tradeLinkId);
        verify(mockTradingInstruction).bbgAck();
        verify(tradingInstructionRepository).save(mockTradingInstruction);
    }

    @Test
    void ack_WhenTradingInstructionNotFound_ShouldThrowException() {
        // Arrange
        when(tradingInstructionRepository.getTradingInstructionByTradeLinkId(tradeLinkId))
                .thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tradingApplicationService.ack(bbgAckMessage);
        });

        assertEquals("TradingInstruction not found for tradeLinkId: " + tradeLinkId, exception.getMessage());
        verify(tradingInstructionRepository).getTradingInstructionByTradeLinkId(tradeLinkId);
        // 这里不需要验证 mockTradingInstruction 的方法，因为找不到对象
    }

    @Test
    void ack_WhenRepositoryGetThrowsException_ShouldPropagateException() {
        // Arrange
        when(tradingInstructionRepository.getTradingInstructionByTradeLinkId(tradeLinkId))
                .thenThrow(new RuntimeException("Repository error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            tradingApplicationService.ack(bbgAckMessage);
        });

        verify(tradingInstructionRepository).getTradingInstructionByTradeLinkId(tradeLinkId);
        // 这里不需要验证 mockTradingInstruction 的方法，因为查询失败
    }

    @Test
    void ack_WhenBbgAckThrowsException_ShouldPropagateException() {
        // Arrange
        TradingInstruction mockTradingInstruction = createMockTradingInstruction(tradeLinkId);
        when(tradingInstructionRepository.getTradingInstructionByTradeLinkId(tradeLinkId))
                .thenReturn(mockTradingInstruction);
        doThrow(new RuntimeException("BBG Ack error")).when(mockTradingInstruction).bbgAck();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            tradingApplicationService.ack(bbgAckMessage);
        });

        verify(tradingInstructionRepository).getTradingInstructionByTradeLinkId(tradeLinkId);
        verify(mockTradingInstruction).bbgAck();
        verify(tradingInstructionRepository, never()).save(any(TradingInstruction.class));
    }

    @Test
    void ack_WhenRepositorySaveThrowsException_ShouldPropagateException() {
        // Arrange
        TradingInstruction mockTradingInstruction = createMockTradingInstruction(tradeLinkId);
        when(tradingInstructionRepository.getTradingInstructionByTradeLinkId(tradeLinkId))
                .thenReturn(mockTradingInstruction);
        doThrow(new RuntimeException("Save error")).when(tradingInstructionRepository).save(mockTradingInstruction);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            tradingApplicationService.ack(bbgAckMessage);
        });

        verify(tradingInstructionRepository).getTradingInstructionByTradeLinkId(tradeLinkId);
        verify(mockTradingInstruction).bbgAck();
        verify(tradingInstructionRepository).save(mockTradingInstruction);
    }

    @Test
    void ack_WithNullBbgAckMessage_ShouldThrowNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            tradingApplicationService.ack(null);
        });
    }

    @Test
    void ack_WithNullTradeLinkId_ShouldHandleGracefully() {
        // Arrange
        bbgAckMessage.setTradeLinkId(null);
        when(tradingInstructionRepository.getTradingInstructionByTradeLinkId(null))
                .thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tradingApplicationService.ack(bbgAckMessage);
        });

        assertEquals("TradingInstruction not found for tradeLinkId: null", exception.getMessage());
        verify(tradingInstructionRepository).getTradingInstructionByTradeLinkId(null);
    }

    @Test
    void submitOrder_WithNullOrderCreationRequest_ShouldThrowNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            tradingApplicationService.submitOrder(null);
        });
    }
}