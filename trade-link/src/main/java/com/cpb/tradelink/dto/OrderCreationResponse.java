package com.cpb.tradelink.dto;

import lombok.Data;

@Data
public class OrderCreationResponse {
    private boolean success = true;
    private String orderId;
    private String errorMessage;
}
