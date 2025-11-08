package com.cpb.tradelink.domain.model;

import lombok.Data;

import java.util.Objects;

@Data
public class QuantityData {
    private Integer quantity;
    private Integer totalExecutedQuantity;

    public boolean fullExecution() {
        return Objects.equals(quantity, totalExecutedQuantity);
    }
}
