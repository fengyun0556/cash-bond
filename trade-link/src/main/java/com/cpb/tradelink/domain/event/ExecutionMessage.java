package com.cpb.tradelink.domain.event;

import com.cpb.tradelink.domain.model.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExecutionMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 8457843989850121044L;
    private Long tps2ExecutionId;
    private Long tradeLinkId;
    private String accountKey;
    private String ISIN;//证券唯一编码
    private String ISINName;//证券名称
    private String exchangeCode;//交易所编码
    private Integer executedQuantity;
    private BigDecimal executedPrice;

    public ExecutionRecord createExecutionRecord() {
        ExecutionRecord executionRecord = new ExecutionRecord();
        executionRecord.setTps2ExecutionId(tps2ExecutionId);
        executionRecord.setExecutedQuantity(executedQuantity);
        executionRecord.setExecutedPrice(executedPrice);
        executionRecord.setOrderId(tradeLinkId);
        executionRecord.setCreateTime(LocalDateTime.now());
        executionRecord.setUpdateTime(LocalDateTime.now());
        return executionRecord;
    }

    public Order createPhoneOrder() {
        Order order = new Order();
        order.setOrderId(this.tradeLinkId);

        AccountData accountData = new AccountData();
        accountData.setAccountKey(this.accountKey);
        order.setAccountData(accountData);

        ISINData isinData = new ISINData();
        isinData.setIsin(this.ISIN);
        isinData.setIsinName(this.ISINName);
        order.setIsinData(isinData);

        order.setExchangeCode(this.exchangeCode);

        QuantityData quantityData = new QuantityData();
        quantityData.setQuantity(this.executedQuantity);
        quantityData.setTotalExecutedQuantity(this.executedQuantity);
        order.setQuantityData(quantityData);

        ExecutionRecord executionRecord = new ExecutionRecord();
        executionRecord.setExecutedPrice(this.executedPrice);
        executionRecord.setExecutedQuantity(this.executedQuantity);
        List<ExecutionRecord> executionRecordList = List.of(executionRecord);
        order.setExecutionRecordList(executionRecordList);

        return order;
    }
}
