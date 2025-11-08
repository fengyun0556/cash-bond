package com.cpb.tradelink.domain.service;

import com.cpb.tradelink.domain.model.OmsSubmissionResult;
import com.cpb.tradelink.domain.model.Order;

public interface OmsIntegrationService {
    OmsSubmissionResult submitToOms(Order order);
}
