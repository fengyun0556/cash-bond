package com.cpb.oms.interfaces.trading;

import com.alibaba.fastjson2.JSONObject;
import com.cpb.oms.application.service.TradingApplicationService;
import com.cpb.oms.domain.model.trading.TradingInstruction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
@Slf4j
public class TradingController {

    @Autowired
    private TradingApplicationService tradingApplicationService;
    @Autowired
    private TradingAssembler tradingAssembler;

    @PostMapping
    public ResponseEntity<OrderCreationResponse> createOrder
            (@RequestBody OrderCreationRequest orderCreationRequest) {
        log.info("receive order create request: {}", JSONObject.toJSONString(orderCreationRequest));
        TradingInstruction tradingInstruction = tradingApplicationService.submitOrder(orderCreationRequest);
        OrderCreationResponse orderCreationResponse = tradingAssembler.buildOrderCreationResponse(tradingInstruction);
        log.info("order create response: {}", JSONObject.toJSONString(orderCreationResponse));
        return ResponseEntity.ok(orderCreationResponse);
    }
}
