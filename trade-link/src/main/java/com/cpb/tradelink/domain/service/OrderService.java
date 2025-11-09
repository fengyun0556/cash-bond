package com.cpb.tradelink.domain.service;

import com.cpb.tradelink.domain.model.OmsSubmissionResult;
import com.cpb.tradelink.domain.model.Order;
import com.cpb.tradelink.domain.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public void saveOmsSubmissionResult(Order order, OmsSubmissionResult omsSubmissionResult) {
        log.info("保存OMS提交结果, orderId={}, success={}, tps2Id={}",
                order.getOrderId(), omsSubmissionResult.getSuccess(), omsSubmissionResult.getTps2Id());

        try {
            if (omsSubmissionResult.getSuccess()) {
                log.debug("OMS提交成功, 更新订单状态为成功, orderId={}", order.getOrderId());
                order.omsSubmissionSuccess(omsSubmissionResult.getTps2Id());
                log.info("OMS提交成功处理完成, orderId={}, tps2Id={}", order.getOrderId(), omsSubmissionResult.getTps2Id());
            } else {
                log.warn("OMS提交失败, 更新订单状态为失败, orderId={}", order.getOrderId());
                order.omsSubmissionFailed();
                log.info("OMS提交失败处理完成, orderId={}", order.getOrderId());
            }

            orderRepository.save(order);
            log.debug("订单状态已保存到数据库, orderId={}", order.getOrderId());

        } catch (Exception e) {
            log.error("保存OMS提交结果失败, orderId={}, 错误信息: {}", order.getOrderId(), e.getMessage(), e);
            throw e;
        }
    }

    public void bbgAck(Boolean ackSuccess, Long tradeLinkId) {
        log.info("处理Bloomberg ACK结果, tradeLinkId={}, ackSuccess={}", tradeLinkId, ackSuccess);

        try {
            Order order = orderRepository.findById(tradeLinkId, false, false);
            if (order == null) {
                log.error("未找到对应的订单, tradeLinkId={}", tradeLinkId);
                throw new RuntimeException("Order not found for tradeLinkId: " + tradeLinkId);
            }
            log.debug("找到订单, orderId={}, 当前状态={}", order.getOrderId(), order.getOrderState());

            if (ackSuccess) {
                log.debug("Bloomberg ACK成功, 更新订单状态, orderId={}", order.getOrderId());
                order.bbgAckSuccess();
                log.info("Bloomberg ACK成功处理完成, orderId={}, tradeLinkId={}", order.getOrderId(), tradeLinkId);
            } else {
                log.warn("Bloomberg ACK失败, 更新订单状态, orderId={}", order.getOrderId());
                order.bbgAckFailed();
                log.info("Bloomberg ACK失败处理完成, orderId={}, tradeLinkId={}", order.getOrderId(), tradeLinkId);
            }

            orderRepository.save(order);
            log.debug("订单状态已保存到数据库, orderId={}", order.getOrderId());

        } catch (Exception e) {
            log.error("处理Bloomberg ACK结果失败, tradeLinkId={}, 错误信息: {}", tradeLinkId, e.getMessage(), e);
            throw e;
        }
    }

    public void settlementFailed(Long orderId) {
        log.info("处理结算失败, orderId={}", orderId);

        try {
            Order order = orderRepository.findById(orderId, false, false);
            if (order == null) {
                log.error("未找到对应的订单, orderId={}", orderId);
                throw new RuntimeException("Order not found for orderId: " + orderId);
            }
            log.debug("找到订单, orderId={}, 当前状态={}", order.getOrderId(), order.getOrderState());

            log.warn("更新订单状态为结算失败, orderId={}", order.getOrderId());
            order.settlementFailed();
            log.info("结算失败处理完成, orderId={}", order.getOrderId());

            orderRepository.save(order);
            log.debug("订单状态已保存到数据库, orderId={}", order.getOrderId());

        } catch (Exception e) {
            log.error("处理结算失败失败, orderId={}, 错误信息: {}", orderId, e.getMessage(), e);
            throw e;
        }
    }
}