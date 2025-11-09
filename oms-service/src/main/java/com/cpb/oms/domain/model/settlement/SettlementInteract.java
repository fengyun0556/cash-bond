package com.cpb.oms.domain.model.settlement;

import com.cpb.oms.domain.enums.SettlementState;
import com.cpb.oms.domain.event.OrderAmendedEvent;
import com.cpb.oms.domain.event.OrderEnrichedEvent;
import com.cpb.oms.domain.event.SettlementFailedEvent;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SettlementInteract {
    private Long id;
    private Long tps2ExecutionId;
    private Long tradeLinkId;
    private String cashAccount;
    private String isin;
    private Integer executedQuantity;
    private BigDecimal executedPrice;
    private Long settlementId;
    private SettlementState settlementState;
    private String failedReason;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public void init() {
        if (StringUtils.isEmpty(this.cashAccount)) {
            this.settlementState = SettlementState.WAITING_FOR_ENRICHMENT;
        } else {
            this.settlementState = SettlementState.WAITING_FOR_SETTLEMENT;
        }
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    public void saveSettlementResult(SettlementResult settlementResult) {
        if (settlementResult.getSuccess()) {
            this.settlementId = settlementResult.getSettlementId();
            this.settlementState = SettlementState.SETTLEMENT_SUCCESS;
            this.failedReason = "";
        } else {
            this.settlementState = SettlementState.SETTLEMENT_FAILED;
            this.failedReason = settlementResult.getFailedReason();
        }
        this.updateTime = LocalDateTime.now();
    }

    public SettlementFailedEvent createSettlementFailedEvent() {
        SettlementFailedEvent settlementFailedEvent = new SettlementFailedEvent();
        settlementFailedEvent.setOrderId(this.tradeLinkId);
        return settlementFailedEvent;
    }

    public void sendToBanker() {
        this.settlementState = SettlementState.WAITING_FOR_AMEND;
        this.updateTime = LocalDateTime.now();
    }

    public boolean canEnrich() {
        return SettlementState.WAITING_FOR_ENRICHMENT.equals(this.settlementState);
    }

    public boolean canAmend() {
        return SettlementState.WAITING_FOR_AMEND.equals(this.settlementState);
    }

    public void enrich(OrderEnrichedEvent orderEnrichedEvent) {
        if (orderEnrichedEvent == null) return;

        this.tradeLinkId = orderEnrichedEvent.getOrderId();
        this.cashAccount = orderEnrichedEvent.getCashAccount();
        this.isin = orderEnrichedEvent.getIsin();
    }

    public void amend(OrderAmendedEvent orderAmendedEvent) {
        if (orderAmendedEvent == null) return;

        this.tradeLinkId = orderAmendedEvent.getOrderId();
        this.cashAccount = orderAmendedEvent.getCashAccount();
        this.isin = orderAmendedEvent.getIsin();
    }
}
