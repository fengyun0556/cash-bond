package com.cpb.tradelink.interfaces.rest.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class OrderApproveRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 276504120255239455L;
    private Long orderId;
}
