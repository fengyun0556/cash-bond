package com.cpb.omsservice.controller;

import com.alibaba.fastjson2.JSONObject;
import com.cpb.omsservice.dto.OMSOrderCreationRequest;
import com.cpb.omsservice.dto.OMSOrderCreationResponse;
import com.cpb.omsservice.service.TPS2OrderService;
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
public class OrderController {
    @Autowired
    private TPS2OrderService TPS2OrderService;

    @PostMapping
    public ResponseEntity<OMSOrderCreationResponse> createOrder
            (@RequestBody OMSOrderCreationRequest omsOrderCreationRequest) {
        log.info("receive order create request: {}", JSONObject.toJSONString(omsOrderCreationRequest));
        OMSOrderCreationResponse omsOrderCreationResponse = TPS2OrderService.createOrder(omsOrderCreationRequest);
        log.info("order create response: {}", JSONObject.toJSONString(omsOrderCreationRequest));
        return ResponseEntity.ok(omsOrderCreationResponse);
    }

}
