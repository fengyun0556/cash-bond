package com.cpb.tradelink.infrastructure.oms.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class OMSOrderCreationResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -7143301388048187808L;
    private Boolean success;
    private Long tps2Id;
    private String errorMessage;
}
