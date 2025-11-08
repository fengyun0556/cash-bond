package com.cpb.tradelink.infrastructure.persistence.mapper;

import com.cpb.tradelink.domain.model.ExecutionRecord;
import com.cpb.tradelink.infrastructure.persistence.entity.OrderExecutionDetail;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExecutionMapper {

    public List<OrderExecutionDetail> toOrderExecutionDetailList(Long orderId, List<ExecutionRecord> executionRecordList) {
        return executionRecordList.stream().map(executionRecord -> {
            OrderExecutionDetail orderExecutionDetail = new OrderExecutionDetail();
            orderExecutionDetail.setExecutionId(executionRecord.getExecutionId());
            orderExecutionDetail.setOrderId(orderId);
            orderExecutionDetail.setTps2ExecutionId(executionRecord.getTps2ExecutionId());
            orderExecutionDetail.setExecutedPrice(executionRecord.getExecutedPrice());
            orderExecutionDetail.setExecutedQuantity(executionRecord.getExecutedQuantity());
            orderExecutionDetail.setCreateTime(executionRecord.getCreateTime());
            orderExecutionDetail.setUpdateTime(executionRecord.getUpdateTime());
            return orderExecutionDetail;
        }).collect(Collectors.toList());
    }

    public List<ExecutionRecord> toExecutionRecordList(List<OrderExecutionDetail> orderExecutionDetailList) {
        return orderExecutionDetailList.stream().map(orderExecutionDetail -> {
            ExecutionRecord executionRecord = new ExecutionRecord();
            executionRecord.setExecutionId(orderExecutionDetail.getExecutionId());
            executionRecord.setTps2ExecutionId(orderExecutionDetail.getTps2ExecutionId());
            executionRecord.setExecutedQuantity(orderExecutionDetail.getExecutedQuantity());
            executionRecord.setExecutedPrice(orderExecutionDetail.getExecutedPrice());
            executionRecord.setOrderId(orderExecutionDetail.getOrderId());
            executionRecord.setCreateTime(LocalDateTime.now());
            executionRecord.setUpdateTime(LocalDateTime.now());
            return executionRecord;
        }).collect(Collectors.toList());
    }
}
