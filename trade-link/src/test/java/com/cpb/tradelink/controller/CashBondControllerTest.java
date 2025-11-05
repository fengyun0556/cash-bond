package com.cpb.tradelink.controller;

import com.alibaba.fastjson2.JSONObject;
import com.cpb.tradelink.dto.OrderCreationRequest;
import com.cpb.tradelink.dto.OrderCreationResponse;
import com.cpb.tradelink.dto.RuleCheck;
import com.cpb.tradelink.enums.OrderRequestMode;
import com.cpb.tradelink.enums.OrderType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

@SpringBootTest
@Slf4j
public class CashBondControllerTest {
    @Autowired
    private CashBondController cashBondController;

    @Test
    void createOrder() throws Exception {
        OrderCreationRequest request = new OrderCreationRequest();
        request.setAccountKey("ACC123456");
        request.setAccountName("测试账户");
        request.setMemberKey("MEM789");
        request.setMemberName("测试会员");
        request.setCashAccount("CASH001");
        request.setIsin("US0378331005");
        request.setIsinName("苹果公司股票");
        request.setExchangeCode("NASDAQ");
        request.setOrderType(OrderType.LIMIT);
        request.setQuantity(100);
        request.setPrice(new BigDecimal("150.50"));
        request.setOrderRequestMode(OrderRequestMode.LIVE);

        request.setRuleCheckList(Arrays.asList(
                RuleCheck.builder().ruleId("r_1").ruleCheckResult("success").ruleDescribe("合规描述1").build(),
                RuleCheck.builder().ruleId("r_2").ruleCheckResult("success").ruleDescribe("合规描述2").build(),
                RuleCheck.builder().ruleId("r_3").ruleCheckResult("success").ruleDescribe("合规描述3").build()
        ));

        ResponseEntity<OrderCreationResponse> responseEntity = cashBondController.createOrder(request);
        log.info("response body: {}", JSONObject.toJSONString(responseEntity.getBody()));
    }

}
    