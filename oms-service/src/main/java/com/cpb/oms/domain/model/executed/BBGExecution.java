package com.cpb.oms.domain.model.executed;

import com.cpb.oms.domain.event.TradeExecutedEvent;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BBGExecution {
    private Long tps2ExecutionId;
    private Long tradeLinkId;
    private String bbgMessageId;
    private String accountKey;
    private String cashAccount;
    private String ISIN;//证券唯一编码
    private Integer executedQuantity;
    private BigDecimal executedPrice;
    private Boolean confirmed;
    private LocalDateTime confirmedDateTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public void init() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this.confirmed = false;
    }

    public TradeExecutedEvent createTradeExecutedEvent() {
        TradeExecutedEvent tradeExecutedEvent = new TradeExecutedEvent();
        tradeExecutedEvent.setTps2ExecutionId(this.tps2ExecutionId);
        tradeExecutedEvent.setTradeLinkId(this.tradeLinkId);
        tradeExecutedEvent.setAccountKey(this.accountKey);
        tradeExecutedEvent.setISIN(this.ISIN);
        tradeExecutedEvent.setExecutedQuantity(this.executedQuantity);
        tradeExecutedEvent.setExecutedPrice(this.executedPrice);
        return tradeExecutedEvent;
    }

    public void confirmed() {
        this.confirmed = true;
        this.updateTime = LocalDateTime.now();
    }
}
