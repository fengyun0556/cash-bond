package com.cpb.tradelink.domain;

import com.cpb.tradelink.domain.model.OmsSubmissionResult;
import com.cpb.tradelink.domain.model.Order;
import com.cpb.tradelink.domain.repository.OrderRepository;
import com.cpb.tradelink.domain.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order createMockOrder(Long orderId) {
        Order mockOrder = mock(Order.class);
        when(mockOrder.getOrderId()).thenReturn(orderId);
        return mockOrder;
    }

    @Test
    void saveOmsSubmissionResult_SuccessCase_ShouldUpdateOrderSuccess() {
        // Arrange
        Long orderId = 1L;
        Order mockOrder = createMockOrder(orderId);
        OmsSubmissionResult omsResult = new OmsSubmissionResult();
        omsResult.setSuccess(true);
        omsResult.setTps2Id(100L);

        // Act
        orderService.saveOmsSubmissionResult(mockOrder, omsResult);

        // Assert
        verify(mockOrder).omsSubmissionSuccess(100L);
        verify(orderRepository).save(mockOrder);
        verify(mockOrder, never()).omsSubmissionFailed();
    }

    @Test
    void saveOmsSubmissionResult_FailureCase_ShouldUpdateOrderFailed() {
        // Arrange
        Long orderId = 1L;
        Order mockOrder = createMockOrder(orderId);
        OmsSubmissionResult omsResult = new OmsSubmissionResult();
        omsResult.setSuccess(false);
        omsResult.setTps2Id(100L);

        // Act
        orderService.saveOmsSubmissionResult(mockOrder, omsResult);

        // Assert
        verify(mockOrder).omsSubmissionFailed();
        verify(orderRepository).save(mockOrder);
        verify(mockOrder, never()).omsSubmissionSuccess(any(Long.class));
    }

    @Test
    void saveOmsSubmissionResult_Exception_ShouldThrow() {
        // Arrange
        Long orderId = 1L;
        Order mockOrder = createMockOrder(orderId);
        OmsSubmissionResult omsResult = new OmsSubmissionResult();
        omsResult.setSuccess(true);
        omsResult.setTps2Id(100L);

        doThrow(new RuntimeException("Database error")).when(orderRepository).save(mockOrder);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            orderService.saveOmsSubmissionResult(mockOrder, omsResult);
        });

        verify(mockOrder).omsSubmissionSuccess(100L);
        verify(orderRepository).save(mockOrder);
    }

    @Test
    void bbgAck_SuccessCase_ShouldUpdateOrderSuccess() {
        // Arrange
        Long tradeLinkId = 2L;
        Order mockOrder = createMockOrder(1L);
        when(orderRepository.findById(tradeLinkId, false, false)).thenReturn(mockOrder);

        // Act
        orderService.bbgAck(true, tradeLinkId);

        // Assert
        verify(orderRepository).findById(tradeLinkId, false, false);
        verify(mockOrder).bbgAckSuccess();
        verify(orderRepository).save(mockOrder);
        verify(mockOrder, never()).bbgAckFailed();
    }

    @Test
    void bbgAck_FailureCase_ShouldUpdateOrderFailed() {
        // Arrange
        Long tradeLinkId = 2L;
        Order mockOrder = createMockOrder(1L);
        when(orderRepository.findById(tradeLinkId, false, false)).thenReturn(mockOrder);

        // Act
        orderService.bbgAck(false, tradeLinkId);

        // Assert
        verify(orderRepository).findById(tradeLinkId, false, false);
        verify(mockOrder).bbgAckFailed();
        verify(orderRepository).save(mockOrder);
        verify(mockOrder, never()).bbgAckSuccess();
    }

    @Test
    void bbgAck_OrderNotFound_ShouldThrowException() {
        // Arrange
        Long tradeLinkId = 2L;
        when(orderRepository.findById(tradeLinkId, false, false)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.bbgAck(true, tradeLinkId);
        });

        assertEquals("Order not found for tradeLinkId: " + tradeLinkId, exception.getMessage());
        verify(orderRepository).findById(tradeLinkId, false, false);
        // 这里不需要验证 mockOrder 的方法，因为订单为 null，不会使用到 mockOrder
    }

    @Test
    void bbgAck_ExceptionDuringProcessing_ShouldThrow() {
        // Arrange
        Long tradeLinkId = 2L;
        Order mockOrder = createMockOrder(1L);
        when(orderRepository.findById(tradeLinkId, false, false)).thenReturn(mockOrder);
        doThrow(new RuntimeException("Database error")).when(orderRepository).save(mockOrder);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            orderService.bbgAck(true, tradeLinkId);
        });

        verify(orderRepository).findById(tradeLinkId, false, false);
        verify(mockOrder).bbgAckSuccess();
        verify(orderRepository).save(mockOrder);
    }

    @Test
    void settlementFailed_ShouldUpdateOrderToSettlementFailed() {
        // Arrange
        Long orderId = 1L;
        Order mockOrder = createMockOrder(orderId);
        when(orderRepository.findById(orderId, false, false)).thenReturn(mockOrder);

        // Act
        orderService.settlementFailed(orderId);

        // Assert
        verify(orderRepository).findById(orderId, false, false);
        verify(mockOrder).settlementFailed();
        verify(orderRepository).save(mockOrder);
    }

    @Test
    void settlementFailed_OrderNotFound_ShouldThrowException() {
        // Arrange
        Long orderId = 1L;
        when(orderRepository.findById(orderId, false, false)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.settlementFailed(orderId);
        });

        assertEquals("Order not found for orderId: " + orderId, exception.getMessage());
        verify(orderRepository).findById(orderId, false, false);
        // 这里不需要验证 mockOrder 的方法，因为订单为 null，不会使用到 mockOrder
    }

    @Test
    void settlementFailed_ExceptionDuringProcessing_ShouldThrow() {
        // Arrange
        Long orderId = 1L;
        Order mockOrder = createMockOrder(orderId);
        when(orderRepository.findById(orderId, false, false)).thenReturn(mockOrder);
        doThrow(new RuntimeException("Database error")).when(orderRepository).save(mockOrder);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            orderService.settlementFailed(orderId);
        });

        verify(orderRepository).findById(orderId, false, false);
        verify(mockOrder).settlementFailed();
        verify(orderRepository).save(mockOrder);
    }
}