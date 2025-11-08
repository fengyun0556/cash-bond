package com.cpb.tradelink.domain.event;

import lombok.Data;

@Data
public class SettlementFailedMessage {

    private Long orderId;
}
