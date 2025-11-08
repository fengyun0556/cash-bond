package com.cpb.tradelink.infrastructure.persistence.mapper;

import com.cpb.tradelink.domain.model.RuleCheck;
import com.cpb.tradelink.infrastructure.persistence.entity.RuleCheckDetail;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RuleCheckMapper {
    public List<RuleCheckDetail> toRuleCheckDetailList(Long orderId, List<RuleCheck> ruleCheckList) {
        return ruleCheckList.stream().map(ruleCheck -> {
            RuleCheckDetail ruleCheckDetail = new RuleCheckDetail();
            ruleCheckDetail.setRuleCode(ruleCheck.getRuleCode());
            ruleCheckDetail.setOrderId(orderId);
            ruleCheckDetail.setRuleCheckResult(ruleCheck.getRuleCheckResult());
            ruleCheckDetail.setRuleDescribe(ruleCheck.getRuleDescribe());
            ruleCheckDetail.setCreateTime(ruleCheck.getCreateTime());
            ruleCheckDetail.setUpdateTime(ruleCheck.getUpdateTime());
            return ruleCheckDetail;
        }).collect(Collectors.toList());
    }

    public List<RuleCheck> toRuleCheckList(List<RuleCheckDetail> ruleCheckDetailList) {
        return ruleCheckDetailList.stream().map(ruleCheckDetail -> {
            RuleCheck ruleCheck = new RuleCheck();
            ruleCheck.setRuleCode(ruleCheckDetail.getRuleCode());
            ruleCheck.setRuleCheckResult(ruleCheckDetail.getRuleCheckResult());
            ruleCheck.setRuleDescribe(ruleCheckDetail.getRuleDescribe());
            ruleCheck.setCreateTime(ruleCheckDetail.getCreateTime());
            ruleCheck.setUpdateTime(ruleCheckDetail.getUpdateTime());
            return ruleCheck;
        }).collect(Collectors.toList());
    }
}
