package com.cpb.tradelink.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.cpb.tradelink.dto.OMSOrderCreationRequest;
import com.cpb.tradelink.dto.OMSOrderCreationResponse;
import com.cpb.tradelink.dto.OrderCreationRequest;
import com.cpb.tradelink.dto.RuleCheck;
import com.cpb.tradelink.entity.OrderDetail;
import com.cpb.tradelink.entity.RuleCheckDetail;
import com.cpb.tradelink.enums.OrderState;
import com.cpb.tradelink.repository.OrderDetailRepository;
import com.cpb.tradelink.repository.RuleCheckDetailRepository;
import com.cpb.tradelink.service.CashBondService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CashBondServiceImpl implements CashBondService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private RuleCheckDetailRepository ruleCheckDetailRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${oms.host}")
    private String OMS_HOST;

    @Override
    public String createOrder(OrderCreationRequest orderCreationRequest) {
        OrderDetail orderDetail = this.getOrderDetail(orderCreationRequest);
        orderDetail = orderDetailRepository.save(orderDetail);
        log.info("order detail save DB success, order id: {}", orderDetail.getOrderId());

        List<RuleCheckDetail> ruleCheckDetailList = this.getRuleCheckDetailList
                (orderCreationRequest.getRuleCheckList(), orderDetail.getOrderId());
        ruleCheckDetailRepository.saveAll(ruleCheckDetailList);
        log.info("rule check detail save DB success, order id: {}", orderDetail.getOrderId());

        Long tps2Id = this.sendToOMS(orderCreationRequest, orderDetail.getOrderId());
        if (tps2Id != null) {
            orderDetail.setTps2Id(tps2Id);
            orderDetail.setOrderState(OrderState.SUBMITTED);
            orderDetailRepository.save(orderDetail);
            log.info("update tps2 id: {}, order id: {}", tps2Id, orderDetail.getOrderId());
        }

        return orderDetail.getOrderId().toString();
    }

    private Long sendToOMS(OrderCreationRequest orderCreationRequest, Long orderId) {
        OMSOrderCreationRequest omsOrderCreationRequest = this.getOMSOrderCreationRequest
                (orderCreationRequest, orderId);
        String url = OMS_HOST + "/oms-service/order";
        log.info("send to OMS: {}", JSONObject.toJSONString(orderCreationRequest));
        ResponseEntity<OMSOrderCreationResponse> response = restTemplate.postForEntity
                (url, omsOrderCreationRequest, OMSOrderCreationResponse.class);
        log.info("OMS response status: {}", JSONObject.toJSONString(response.getStatusCode()));
        if (HttpStatus.OK.equals(response.getStatusCode()) && response.getBody().getSuccess()) {
            log.info("OMS response: {}", JSONObject.toJSONString(response.getBody()));
            return response.getBody().getTps2Id();
        } else return null;
    }

    private OMSOrderCreationRequest getOMSOrderCreationRequest(OrderCreationRequest orderCreationRequest, Long orderId) {
        OMSOrderCreationRequest omsOrderCreationRequest = new OMSOrderCreationRequest();
        omsOrderCreationRequest.setOrderId(orderId);
        omsOrderCreationRequest.setAccountKey(orderCreationRequest.getAccountKey());
        omsOrderCreationRequest.setAccountName(orderCreationRequest.getAccountName());
        omsOrderCreationRequest.setMemberKey(orderCreationRequest.getMemberKey());
        omsOrderCreationRequest.setMemberName(orderCreationRequest.getMemberName());
        omsOrderCreationRequest.setCashAccount(orderCreationRequest.getCashAccount());
        omsOrderCreationRequest.setISIN(orderCreationRequest.getIsin());
        omsOrderCreationRequest.setISINName(orderCreationRequest.getIsinName());
        omsOrderCreationRequest.setExchangeCode(orderCreationRequest.getExchangeCode());
        omsOrderCreationRequest.setOrderType(orderCreationRequest.getOrderType());
        omsOrderCreationRequest.setQuantity(orderCreationRequest.getQuantity());
        omsOrderCreationRequest.setPrice(orderCreationRequest.getPrice());
        return omsOrderCreationRequest;
    }

    private OrderDetail getOrderDetail(OrderCreationRequest request) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setAccountKey(request.getAccountKey());
        orderDetail.setAccountName(request.getAccountName());
        orderDetail.setMemberKey(request.getMemberKey());
        orderDetail.setMemberName(request.getMemberName());
        orderDetail.setCashAccount(request.getCashAccount());
        orderDetail.setIsin(request.getIsin());
        orderDetail.setIsinName(request.getIsinName());
        orderDetail.setExchangeCode(request.getExchangeCode());
        orderDetail.setOrderType(request.getOrderType());
        orderDetail.setQuantity(request.getQuantity());
        orderDetail.setTotalExecutedQuantity(0);
        orderDetail.setOrderState(OrderState.NEW);
        orderDetail.setPrice(request.getPrice());
        orderDetail.setOrderRequestMode(request.getOrderRequestMode());
        orderDetail.setCreateTime(LocalDateTime.now());
        return orderDetail;
    }

    private List<RuleCheckDetail> getRuleCheckDetailList(List<RuleCheck> ruleCheckList, Long orderId) {
        return ruleCheckList.stream()
                .map(ruleCheck -> createRuleCheckDetail(ruleCheck, orderId))
                .collect(Collectors.toList());
    }

    private RuleCheckDetail createRuleCheckDetail(RuleCheck ruleCheck, Long orderId) {
        RuleCheckDetail ruleCheckDetail = new RuleCheckDetail();
        ruleCheckDetail.setOrderId(orderId);
        ruleCheckDetail.setRuleId(ruleCheck.getRuleId());
        ruleCheckDetail.setRuleCheckResult(ruleCheck.getRuleCheckResult());
        ruleCheckDetail.setRuleDescribe(ruleCheck.getRuleDescribe());
        ruleCheckDetail.setCreateTime(LocalDateTime.now());
        return ruleCheckDetail;
    }
}
