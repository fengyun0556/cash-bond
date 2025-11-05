package com.cpb.omsservice.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class OMSOrderCreationResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 4215227497747067082L;
    private Boolean success;
    private Long tps2Id;
    private String errorMessage;
}
