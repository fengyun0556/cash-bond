package com.cpb.tradelink.interfaces.rest.response;

import lombok.Data;

@Data
public class OrderCreationResponse {
    private boolean success;
    private String orderId;
    private String errorMessage;
}
