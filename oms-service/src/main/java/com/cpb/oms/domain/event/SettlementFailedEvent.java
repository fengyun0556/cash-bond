package com.cpb.oms.domain.event;

import lombok.Data;

@Data
public class SettlementFailedEvent {
    private Long orderId;
}
