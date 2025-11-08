package com.cpb.oms.interfaces.trading;

import lombok.Data;

@Data
public class OrderCreationResponse {
    private Boolean success;
    private Long tps2Id;
}
