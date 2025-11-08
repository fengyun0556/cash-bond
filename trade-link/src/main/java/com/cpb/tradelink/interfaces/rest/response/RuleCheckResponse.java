package com.cpb.tradelink.interfaces.rest.response;

import lombok.Data;

@Data
public class RuleCheckResponse {
    private String ruleCode;
    private String ruleCheckResult;
    private String ruleDescribe;
}
