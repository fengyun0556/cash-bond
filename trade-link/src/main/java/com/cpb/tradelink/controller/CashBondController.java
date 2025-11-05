package com.cpb.tradelink.controller;

import com.alibaba.fastjson2.JSONObject;
import com.cpb.tradelink.dto.OrderCreationRequest;
import com.cpb.tradelink.dto.OrderCreationResponse;
import com.cpb.tradelink.service.CashBondService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cashBond")
@Slf4j
public class CashBondController {

    @Autowired
    private CashBondService cashBondService;

    @PostMapping("order")
    public ResponseEntity<OrderCreationResponse> createOrder(@RequestBody OrderCreationRequest orderCreationRequest) {
        log.info("create order start, request body: {}", JSONObject.toJSONString(orderCreationRequest));
        String orderId = cashBondService.createOrder(orderCreationRequest);
        OrderCreationResponse orderCreationResponse = new OrderCreationResponse();
        orderCreationResponse.setOrderId(orderId);
        log.info("create order end, response: {}", JSONObject.toJSONString(orderCreationResponse));
        return ResponseEntity.ok(orderCreationResponse);
    }
}
