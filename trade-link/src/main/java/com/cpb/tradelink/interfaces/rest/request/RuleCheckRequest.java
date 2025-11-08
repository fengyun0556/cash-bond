package com.cpb.tradelink.interfaces.rest.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RuleCheckRequest {
    private String ruleCode;
    private String ruleCheckResult;
    private String ruleDescribe;
}
