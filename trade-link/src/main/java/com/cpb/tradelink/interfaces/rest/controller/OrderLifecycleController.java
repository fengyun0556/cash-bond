package com.cpb.tradelink.interfaces.rest.controller;

import com.alibaba.fastjson2.JSONObject;
import com.cpb.tradelink.application.service.OrderLifecycleAppService;
import com.cpb.tradelink.domain.model.Order;
import com.cpb.tradelink.interfaces.rest.assembler.OrderAssembler;
import com.cpb.tradelink.interfaces.rest.request.OrderAmendRequest;
import com.cpb.tradelink.interfaces.rest.request.OrderApproveRequest;
import com.cpb.tradelink.interfaces.rest.request.OrderCreationRequest;
import com.cpb.tradelink.interfaces.rest.request.OrderEnrichRequest;
import com.cpb.tradelink.interfaces.rest.response.OrderCreationResponse;
import com.cpb.tradelink.interfaces.rest.response.OrderDetailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cashBond/order")
@Slf4j
public class OrderLifecycleController {

    @Autowired
    private OrderLifecycleAppService orderLifecycleAppService;
    @Autowired
    private OrderAssembler orderAssembler;

    @GetMapping("{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrder(@PathVariable Long orderId) {
        log.info("get order detail, orderId: {}", orderId);
        Order order = orderLifecycleAppService.getOrder(orderId);
        OrderDetailResponse orderDetailResponse = orderAssembler.toOrderDetailResponse(order);
        log.info("get order detail, response: {}", JSONObject.toJSONString(orderDetailResponse));
        return ResponseEntity.ok(orderDetailResponse);
    }

    @PostMapping()
    public ResponseEntity<OrderCreationResponse> createOrder(@RequestBody OrderCreationRequest orderCreationRequest) {
        log.info("create order start, request body: {}", JSONObject.toJSONString(orderCreationRequest));
        Order order = orderLifecycleAppService.createOrder(orderCreationRequest);
        OrderCreationResponse orderCreationResponse = orderAssembler.toOrderCreationResponse(order);
        log.info("create order end, response: {}", JSONObject.toJSONString(orderCreationResponse));
        return ResponseEntity.ok(orderCreationResponse);
    }

    @PostMapping("enrich")
    public ResponseEntity<Boolean> enrichOrder(@RequestBody OrderEnrichRequest orderEnrichRequest) {
        log.info("enrich order start, request body: {}", JSONObject.toJSONString(orderEnrichRequest));
        orderLifecycleAppService.enrichOrder(orderEnrichRequest);
        log.info("enrich order end");
        return ResponseEntity.ok(true);
    }

    @PostMapping("amend")
    public ResponseEntity<Boolean> amendOrder(@RequestBody OrderAmendRequest orderAmendRequest) {
        log.info("amend order start, request body: {}", JSONObject.toJSONString(orderAmendRequest));
        orderLifecycleAppService.amendOrder(orderAmendRequest);
        log.info("amend order end");
        return ResponseEntity.ok(true);
    }

    @PostMapping("approve")
    public ResponseEntity<Boolean> approveOrder(@RequestBody OrderApproveRequest orderApproveRequest) {
        log.info("approve order start, request body: {}", JSONObject.toJSONString(orderApproveRequest));
        orderLifecycleAppService.approveOrder(orderApproveRequest);
        log.info("approve order end");
        return ResponseEntity.ok(true);
    }

}
