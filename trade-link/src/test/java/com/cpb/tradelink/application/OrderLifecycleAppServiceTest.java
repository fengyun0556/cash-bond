package com.cpb.tradelink.application;

import com.cpb.tradelink.application.builder.OrderBuilder;
import com.cpb.tradelink.application.service.EmailService;
import com.cpb.tradelink.application.service.MessageSender;
import com.cpb.tradelink.application.service.OrderLifecycleAppService;
import com.cpb.tradelink.domain.enums.OrderRequestMode;
import com.cpb.tradelink.domain.enums.OrderState;
import com.cpb.tradelink.domain.event.MEMOOrderCreatedEvent;
import com.cpb.tradelink.domain.event.OrderAmendedEvent;
import com.cpb.tradelink.domain.event.OrderEnrichedEvent;
import com.cpb.tradelink.domain.model.*;
import com.cpb.tradelink.domain.event.ExecutionMessage;
import com.cpb.tradelink.domain.repository.OrderRepository;
import com.cpb.tradelink.domain.service.OmsIntegrationService;
import com.cpb.tradelink.domain.service.OrderService;
import com.cpb.tradelink.interfaces.rest.request.OrderAmendRequest;
import com.cpb.tradelink.interfaces.rest.request.OrderApproveRequest;
import com.cpb.tradelink.interfaces.rest.request.OrderCreationRequest;
import com.cpb.tradelink.interfaces.rest.request.OrderEnrichRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderLifecycleAppServiceTest {

    @Mock
    private OrderBuilder orderBuilder;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private OmsIntegrationService omsIntegrationService;

    @Mock
    private MessageSender messageSender;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OrderLifecycleAppService orderLifecycleAppService;

    private OrderCreationRequest orderCreationRequest;
    private OrderEnrichRequest orderEnrichRequest;
    private OrderAmendRequest orderAmendRequest;
    private OrderApproveRequest orderApproveRequest;
    private Long orderId;

    @BeforeEach
    void setUp() {
        orderId = 1L;

        orderCreationRequest = new OrderCreationRequest();
        orderCreationRequest.setOrderRequestMode(OrderRequestMode.LIVE);

        orderEnrichRequest = new OrderEnrichRequest();
        orderEnrichRequest.setOrderId(orderId);
        orderEnrichRequest.setCashAccount("CASH001");
        orderEnrichRequest.setCashAccountCurrency("USD");
        orderEnrichRequest.setIsinType("EQUITY");
        orderEnrichRequest.setIsinName("Test ISIN");
        orderEnrichRequest.setCommissionRate(BigDecimal.valueOf(0.1));
        orderEnrichRequest.setCommissionType("PERCENTAGE");

        orderAmendRequest = new OrderAmendRequest();
        orderAmendRequest.setOrderId(orderId);
        orderAmendRequest.setAccountKey("ACC001");
        orderAmendRequest.setAccountName("Test Account");
        orderAmendRequest.setMemberKey("MEM001");
        orderAmendRequest.setMemberName("Test Member");
        orderAmendRequest.setCashAccount("CASH002");
        orderAmendRequest.setCashAccountCurrency("EUR");
        orderAmendRequest.setIsinType("BOND");
        orderAmendRequest.setCommissionRate(BigDecimal.valueOf(0.2));
        orderAmendRequest.setCommissionType("FIXED");

        orderApproveRequest = new OrderApproveRequest();
        orderApproveRequest.setOrderId(orderId);

        // 不再在 setUp 中创建 mockOrder，而是在每个测试方法中按需创建
    }

    // 创建基本的 mock Order
    private Order createMockOrder() {
        Order order = mock(Order.class);
        when(order.getOrderId()).thenReturn(orderId);
        return order;
    }

    // 为 enrichOrder 测试专用的辅助方法
    private void setupEnrichOrderDataMocks(Order order) {
        CashAccountData mockCashAccountData = mock(CashAccountData.class);
        ISINData mockIsinData = mock(ISINData.class);
        CommissionData mockCommissionData = mock(CommissionData.class);

        when(order.getCashAccountData()).thenReturn(mockCashAccountData);
        when(order.getIsinData()).thenReturn(mockIsinData);
        when(order.getCommissionData()).thenReturn(mockCommissionData);
    }

    // 为 amendOrder 测试专用的辅助方法
    private void setupAmendOrderDataMocks(Order order) {
        CashAccountData mockCashAccountData = mock(CashAccountData.class);
        ISINData mockIsinData = mock(ISINData.class);
        CommissionData mockCommissionData = mock(CommissionData.class);
        AccountData mockAccountData = mock(AccountData.class);
        MemberData mockMemberData = mock(MemberData.class);

        when(order.getCashAccountData()).thenReturn(mockCashAccountData);
        when(order.getIsinData()).thenReturn(mockIsinData);
        when(order.getCommissionData()).thenReturn(mockCommissionData);
        when(order.getAccountData()).thenReturn(mockAccountData);
        when(order.getMemberData()).thenReturn(mockMemberData);
    }

    @Test
    void createOrder_LiveMode_Success() {
        // Arrange
        Order mockOrder = createMockOrder();
        when(mockOrder.getOrderRequestMode()).thenReturn(OrderRequestMode.LIVE);
        when(orderBuilder.buildFromOrderCreationRequest(orderCreationRequest)).thenReturn(mockOrder);

        OmsSubmissionResult omsResult = new OmsSubmissionResult();
        omsResult.setSuccess(true);
        omsResult.setTps2Id(1L);
        when(omsIntegrationService.submitToOms(mockOrder)).thenReturn(omsResult);

        // Act
        Order result = orderLifecycleAppService.createOrder(orderCreationRequest);

        // Assert
        assertNotNull(result);
        verify(orderBuilder).buildFromOrderCreationRequest(orderCreationRequest);
        verify(mockOrder).calculateCommission();
        verify(mockOrder).initForNew();
        verify(orderRepository).save(mockOrder);
        verify(omsIntegrationService).submitToOms(mockOrder);
        verify(orderService).saveOmsSubmissionResult(mockOrder, omsResult);
        verify(messageSender, never()).send(any(MEMOOrderCreatedEvent.class));
    }

    @Test
    void createOrder_MemoMode_Success() {
        // Arrange
        Order mockOrder = createMockOrder();
        when(mockOrder.getOrderRequestMode()).thenReturn(OrderRequestMode.MEMO);
        when(orderBuilder.buildFromOrderCreationRequest(orderCreationRequest)).thenReturn(mockOrder);
        when(mockOrder.createMEMOOrderCreatedEvent()).thenReturn(mock(MEMOOrderCreatedEvent.class));

        // Act
        Order result = orderLifecycleAppService.createOrder(orderCreationRequest);

        // Assert
        assertNotNull(result);
        verify(orderBuilder).buildFromOrderCreationRequest(orderCreationRequest);
        verify(mockOrder).calculateCommission();
        verify(mockOrder).initForNew();
        verify(orderRepository).save(mockOrder);
        verify(omsIntegrationService, never()).submitToOms(any(Order.class));
        verify(orderService, never()).saveOmsSubmissionResult(any(Order.class), any(OmsSubmissionResult.class));
        verify(messageSender).send(any(MEMOOrderCreatedEvent.class));
    }

    @Test
    void enrichOrder_Success() {
        // Arrange
        Order mockOrder = createMockOrder();
        setupEnrichOrderDataMocks(mockOrder);
        when(orderRepository.findById(orderId, false, false)).thenReturn(mockOrder);
        when(mockOrder.createOrderEnrichedEvent()).thenReturn(mock(OrderEnrichedEvent.class));

        // Act
        orderLifecycleAppService.enrichOrder(orderEnrichRequest);

        // Assert
        verify(orderRepository).findById(orderId, false, false);
        verify(mockOrder.getCashAccountData()).setCashAccount("CASH001");
        verify(mockOrder.getCashAccountData()).setCurrency("USD");
        verify(mockOrder.getIsinData()).setIsinType("EQUITY");
        verify(mockOrder.getIsinData()).setIsinName("Test ISIN");
        verify(mockOrder.getCommissionData()).setCommissionRate(BigDecimal.valueOf(0.1));
        verify(mockOrder.getCommissionData()).setCommissionType("PERCENTAGE");
        verify(mockOrder).calculateCommission();
        verify(mockOrder).enrich();
        verify(orderRepository).save(mockOrder);
        verify(messageSender).send(any(OrderEnrichedEvent.class));
    }

    @Test
    void enrichOrder_OrderNotFound_ThrowsException() {
        // Arrange
        when(orderRepository.findById(orderId, false, false)).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            orderLifecycleAppService.enrichOrder(orderEnrichRequest);
        });
    }

    @Test
    void amendOrder_Success() {
        // Arrange
        Order mockOrder = createMockOrder();
        setupAmendOrderDataMocks(mockOrder);
        when(orderRepository.findById(orderId, false, false)).thenReturn(mockOrder);
        when(mockOrder.createOrderAmendedEvent()).thenReturn(mock(OrderAmendedEvent.class));

        // Act
        orderLifecycleAppService.amendOrder(orderAmendRequest);

        // Assert
        verify(orderRepository).findById(orderId, false, false);
        verify(mockOrder.getAccountData()).setAccountKey("ACC001");
        verify(mockOrder.getAccountData()).setAccountName("Test Account");
        verify(mockOrder.getMemberData()).setMemberKey("MEM001");
        verify(mockOrder.getMemberData()).setMemberName("Test Member");
        verify(mockOrder.getCashAccountData()).setCashAccount("CASH002");
        verify(mockOrder.getCashAccountData()).setCurrency("EUR");
        verify(mockOrder.getIsinData()).setIsinType("BOND");
        verify(mockOrder.getCommissionData()).setCommissionRate(BigDecimal.valueOf(0.2));
        verify(mockOrder.getCommissionData()).setCommissionType("FIXED");
        verify(mockOrder).calculateCommission();
        verify(mockOrder).amend();
        verify(orderRepository).save(mockOrder);
        verify(messageSender).send(any(OrderAmendedEvent.class));
    }

    @Test
    void amendOrder_OrderNotFound_ThrowsException() {
        // Arrange
        when(orderRepository.findById(orderId, false, false)).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            orderLifecycleAppService.amendOrder(orderAmendRequest);
        });
    }

    @Test
    void approveOrder_Success() {
        // Arrange
        Order mockOrder = createMockOrder();
        when(orderRepository.findById(orderId, false, false)).thenReturn(mockOrder);

        // Act
        orderLifecycleAppService.approveOrder(orderApproveRequest);

        // Assert
        verify(orderRepository).findById(orderId, false, false);
        verify(mockOrder).approve();
        verify(orderRepository).save(mockOrder);
        verify(emailService).orderApproved(mockOrder);
    }

    @Test
    void approveOrder_OrderNotFound_ThrowsException() {
        // Arrange
        when(orderRepository.findById(orderId, false, false)).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            orderLifecycleAppService.approveOrder(orderApproveRequest);
        });
    }

    @Test
    void getOrder_WithRelations_Success() {
        // Arrange
        Order mockOrder = createMockOrder();
        when(orderRepository.findById(orderId, true, true)).thenReturn(mockOrder);

        // Act
        Order result = orderLifecycleAppService.getOrder(orderId);

        // Assert
        assertNotNull(result);
        verify(orderRepository).findById(orderId, true, true);
    }

    @Test
    void getOrder_NotFound_ReturnsNull() {
        // Arrange
        when(orderRepository.findById(orderId, true, true)).thenReturn(null);

        // Act
        Order result = orderLifecycleAppService.getOrder(orderId);

        // Assert
        assertNull(result);
        verify(orderRepository).findById(orderId, true, true);
    }

    @Test
    void orderExecuted_LiveOrder_Success() {
        // Arrange
        Order mockOrder = createMockOrder();
        ExecutionMessage executionMessage = mock(ExecutionMessage.class);
        when(executionMessage.getTradeLinkId()).thenReturn(orderId);
        when(executionMessage.getTps2ExecutionId()).thenReturn(1L);

        when(orderRepository.findById(orderId, false, true)).thenReturn(mockOrder);
        when(mockOrder.getExecutionRecordList()).thenReturn(new ArrayList<>());

        // Act
        orderLifecycleAppService.orderExecuted(executionMessage);

        // Assert
        verify(orderRepository).findById(orderId, false, true);
        verify(mockOrder).executed();
        verify(orderRepository).save(mockOrder);
        verify(emailService).orderExecuted(mockOrder);
    }

    @Test
    void orderExecuted_LiveOrderWithExistingRecords_Success() {
        // Arrange
        Order mockOrder = createMockOrder();
        ExecutionMessage executionMessage = mock(ExecutionMessage.class);
        when(executionMessage.getTradeLinkId()).thenReturn(orderId);
        when(executionMessage.getTps2ExecutionId()).thenReturn(1L);

        List<ExecutionRecord> existingRecords = new ArrayList<>();
        existingRecords.add(mock(ExecutionRecord.class));

        when(orderRepository.findById(orderId, false, true)).thenReturn(mockOrder);
        when(mockOrder.getExecutionRecordList()).thenReturn(existingRecords);

        // Act
        orderLifecycleAppService.orderExecuted(executionMessage);

        // Assert
        verify(orderRepository).findById(orderId, false, true);
        verify(mockOrder).executed();
        verify(orderRepository).save(mockOrder);
        verify(emailService).orderExecuted(mockOrder);
    }

    @Test
    void orderExecuted_PhoneOrder_Success() {
        // Arrange
        ExecutionMessage executionMessage = mock(ExecutionMessage.class);
        when(executionMessage.getTradeLinkId()).thenReturn(null);
        when(executionMessage.getTps2ExecutionId()).thenReturn(1L);

        Order phoneOrder = mock(Order.class);
        when(phoneOrder.getOrderId()).thenReturn(2L);
        when(executionMessage.createPhoneOrder()).thenReturn(phoneOrder);

        // Act
        orderLifecycleAppService.orderExecuted(executionMessage);

        // Assert
        verify(executionMessage).createPhoneOrder();
        verify(phoneOrder).initForPhone();
        verify(orderRepository).save(phoneOrder);
        verify(emailService).orderEnrich(phoneOrder);
    }

    @Test
    void orderExecuted_LiveOrderNotFound_ThrowsException() {
        // Arrange
        ExecutionMessage executionMessage = mock(ExecutionMessage.class);
        when(executionMessage.getTradeLinkId()).thenReturn(orderId);
        when(executionMessage.getTps2ExecutionId()).thenReturn(1L);

        when(orderRepository.findById(orderId, false, true)).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            orderLifecycleAppService.orderExecuted(executionMessage);
        });
    }
}