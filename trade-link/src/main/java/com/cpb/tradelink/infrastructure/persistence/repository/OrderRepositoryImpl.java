package com.cpb.tradelink.infrastructure.persistence.repository;

import com.cpb.tradelink.domain.model.ExecutionRecord;
import com.cpb.tradelink.domain.model.Order;
import com.cpb.tradelink.domain.model.RuleCheck;
import com.cpb.tradelink.domain.repository.OrderRepository;
import com.cpb.tradelink.infrastructure.persistence.entity.OrderDetail;
import com.cpb.tradelink.infrastructure.persistence.entity.OrderExecutionDetail;
import com.cpb.tradelink.infrastructure.persistence.entity.RuleCheckDetail;
import com.cpb.tradelink.infrastructure.persistence.jpa.OrderExecutionDetailRepository;
import com.cpb.tradelink.infrastructure.persistence.mapper.ExecutionMapper;
import com.cpb.tradelink.infrastructure.persistence.mapper.OrderMapper;
import com.cpb.tradelink.infrastructure.persistence.jpa.OrderDetailRepository;
import com.cpb.tradelink.infrastructure.persistence.jpa.RuleCheckDetailRepository;
import com.cpb.tradelink.infrastructure.persistence.mapper.RuleCheckMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class OrderRepositoryImpl implements OrderRepository {

    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private RuleCheckDetailRepository ruleCheckDetailRepository;
    @Autowired
    private OrderExecutionDetailRepository orderExecutionDetailRepository;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RuleCheckMapper ruleCheckMapper;
    @Autowired
    private ExecutionMapper executionMapper;

    @Override
    public void save(Order order) {
        OrderDetail orderDetail = orderMapper.toOrderDetail(order);
        orderDetail = orderDetailRepository.save(orderDetail);
        order.setOrderId(orderDetail.getOrderId());

        if (!CollectionUtils.isEmpty(order.getRuleCheckList())) {
            List<RuleCheckDetail> ruleCheckDetailList = ruleCheckMapper
                    .toRuleCheckDetailList(orderDetail.getOrderId(), order.getRuleCheckList());
            ruleCheckDetailList.forEach(ruleCheckDetail -> {
                if (ruleCheckDetail.getId() == null) {
                    ruleCheckDetailRepository.save(ruleCheckDetail);
                }
            });
        }

        if (!CollectionUtils.isEmpty(order.getExecutionRecordList())) {
            List<OrderExecutionDetail> orderExecutionDetailList = executionMapper
                    .toOrderExecutionDetailList(orderDetail.getOrderId(), order.getExecutionRecordList());
            orderExecutionDetailList.forEach(orderExecutionDetail -> {
                if (orderExecutionDetail.getExecutionId() == null) {
                    orderExecutionDetailRepository.save(orderExecutionDetail);
                }
            });
        }
    }

    @Override
    public Order findById(Long orderId, boolean ruleCheck, boolean execution) {
        Optional<OrderDetail> optional = orderDetailRepository.findById(orderId);
        if (optional.isEmpty()) {
            log.error("not found orderId: {}", orderId);
            return null;
        }
        OrderDetail orderDetail = optional.get();

        Order order = orderMapper.toOrder(orderDetail);

        if (ruleCheck) {
            List<RuleCheckDetail> ruleCheckDetailList = ruleCheckDetailRepository.findAllByOrderIdOrderById(orderId);
            List<RuleCheck> ruleCheckList = ruleCheckMapper.toRuleCheckList(ruleCheckDetailList);
            order.setRuleCheckList(ruleCheckList);
        }

        if (execution) {
            List<OrderExecutionDetail> orderExecutionDetailList = orderExecutionDetailRepository.findByOrderIdOrderByExecutionId(orderId);
            List<ExecutionRecord> executionRecordList = executionMapper.toExecutionRecordList(orderExecutionDetailList);
            order.setExecutionRecordList(executionRecordList);
        }

        return order;
    }


}
