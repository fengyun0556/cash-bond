package com.cpb.omsservice.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SettlementResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1339079683759862048L;
    private Boolean success;
    private Long settlementId;
}
