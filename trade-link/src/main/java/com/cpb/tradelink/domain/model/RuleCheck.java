package com.cpb.tradelink.domain.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RuleCheck {
    private String ruleCode;
    private String ruleCheckResult;
    private String ruleDescribe;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
