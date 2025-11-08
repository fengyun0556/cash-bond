package com.cpb.oms.domain.model.trading;

import com.cpb.oms.domain.enums.OrderType;
import com.cpb.oms.domain.enums.TradeState;
import com.cpb.oms.domain.event.TradeSubmissionEvent;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TradingInstruction {
    private Long tps2Id;
    private Long tradeLinkId;
    private String accountKey;
    private String accountName;
    private String memberKey;
    private String memberName;
    private String cashAccount;
    private String ISIN;//证券唯一编码
    private String ISINName;//证券名称
    private TradeState tradeState;
    private String exchangeCode;//交易所编码
    private OrderType orderType;// 订单类型: LIMIT(限价), MARKET(市价)
    private Integer quantity;// 订单数量 (单位: 股)
    private BigDecimal price;// 限价单价格 (市价单可为空)
    private LocalDateTime createDateTime;
    private LocalDateTime updateDateTime;

    public void init() {
        this.tradeState = TradeState.SUBMITTED;
        this.createDateTime = LocalDateTime.now();
        this.updateDateTime = LocalDateTime.now();
    }

    public TradeSubmissionEvent createTradeSubmissionEvent() {
        TradeSubmissionEvent tradeSubmissionEvent = new TradeSubmissionEvent();
        tradeSubmissionEvent.setUniqueId(this.tradeLinkId);
        tradeSubmissionEvent.setAccountKey(this.accountKey);
        tradeSubmissionEvent.setISIN(this.ISIN);
        tradeSubmissionEvent.setISINName(this.ISINName);
        tradeSubmissionEvent.setExchangeCode(this.exchangeCode);
        tradeSubmissionEvent.setOrderType(this.orderType);
        tradeSubmissionEvent.setQuantity(this.quantity);
        tradeSubmissionEvent.setPrice(this.price);
        return tradeSubmissionEvent;
    }

    public void bbgAck() {
        this.tradeState = TradeState.BBG_ACK;
        this.updateDateTime = LocalDateTime.now();
    }
}
