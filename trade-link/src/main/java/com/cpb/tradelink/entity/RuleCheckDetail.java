package com.cpb.tradelink.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "RULE_CHECK_DETAIL")
@Data
public class RuleCheckDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "RULE_ID", nullable = false, length = 50)
    private String ruleId;

    @Column(name = "ORDER_ID", nullable = false)
    private Long orderId;

    @Column(name = "RULE_CHECK_RESULT", nullable = false, length = 20)
    private String ruleCheckResult;

    @Column(name = "RULE_DESCRIBE", length = 500)
    private String ruleDescribe;

    @Column(name = "CREATE_TIME", updatable = false)
    private LocalDateTime createTime;

    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;

}
