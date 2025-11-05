package com.cpb.tradelink.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RuleCheck {
    private String ruleId;
    private String ruleCheckResult;
    private String ruleDescribe;
}
