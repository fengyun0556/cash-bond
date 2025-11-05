package com.cpb.tradelink.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Data
@ToString
public class BBGAckMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = -5152703913758656715L;
    private Long tradeLinkId;
    private Boolean ackSuccess;
}
