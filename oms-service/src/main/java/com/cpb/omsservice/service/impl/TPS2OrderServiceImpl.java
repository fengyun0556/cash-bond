package com.cpb.omsservice.service.impl;

import com.cpb.omsservice.dto.BBGAckMessage;
import com.cpb.omsservice.dto.BBGOrderCreationMessage;
import com.cpb.omsservice.dto.OMSOrderCreationRequest;
import com.cpb.omsservice.dto.OMSOrderCreationResponse;
import com.cpb.omsservice.entity.TPS2OrderDetail;
import com.cpb.omsservice.enums.OrderState;
import com.cpb.omsservice.repository.TPS2OrderDetailRepository;
import com.cpb.omsservice.service.TPS2OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class TPS2OrderServiceImpl implements TPS2OrderService {
    @Autowired
    private MessageProducer messageProducer;
    @Autowired
    private TPS2OrderDetailRepository tps2OrderDetailRepository;

    @Override
    public OMSOrderCreationResponse createOrder(OMSOrderCreationRequest omsOrderCreationRequest) {
        OMSOrderCreationResponse omsOrderCreationResponse = new OMSOrderCreationResponse();
        //save order record
        TPS2OrderDetail tps2OrderDetail = this.saveTPS2OrderDetail(omsOrderCreationRequest);

        //send RocketMQ to Fix Engine
        BBGOrderCreationMessage bbgOrderCreationMessage = this.getBBGOrderCreationMessage(omsOrderCreationRequest);
        messageProducer.sendBBGOrderCreationMessage(bbgOrderCreationMessage);

        omsOrderCreationResponse.setSuccess(true);
        omsOrderCreationResponse.setTps2Id(tps2OrderDetail.getTps2Id());
        return omsOrderCreationResponse;
    }

    @Override
    public void ack(BBGAckMessage bbgAckMessage) {
        Optional<TPS2OrderDetail> optional = this.tps2OrderDetailRepository.getByTradeLinkId(bbgAckMessage.getTradeLinkId());
        if (optional.isEmpty()) {
            log.warn("trade link id not found: {}", bbgAckMessage.getTradeLinkId());
            return;
        }
        TPS2OrderDetail tps2OrderDetail = optional.get();
        tps2OrderDetail.setOrderState(OrderState.BBG_ACK);
        tps2OrderDetail.setUpdateTime(LocalDateTime.now());
        this.tps2OrderDetailRepository.save(tps2OrderDetail);
        log.info("update tps2 order detail success, tps2 id: {}", tps2OrderDetail.getTps2Id());
    }

    @Override
    public TPS2OrderDetail getOrderByTradeLink(Long tradeLinkId) {
        Optional<TPS2OrderDetail> optional = this.tps2OrderDetailRepository.getByTradeLinkId(tradeLinkId);
        if (optional.isEmpty()) {
            log.warn("trade link id not found: {}", tradeLinkId);
            return null;
        }
        return optional.get();
    }

    private TPS2OrderDetail saveTPS2OrderDetail(OMSOrderCreationRequest omsOrderCreationRequest) {
        TPS2OrderDetail tps2OrderDetail = new TPS2OrderDetail();
        tps2OrderDetail.setTradeLinkId(omsOrderCreationRequest.getOrderId());
        tps2OrderDetail.setAccountKey(omsOrderCreationRequest.getAccountKey());
        tps2OrderDetail.setAccountName(omsOrderCreationRequest.getAccountName());
        tps2OrderDetail.setMemberKey(omsOrderCreationRequest.getMemberKey());
        tps2OrderDetail.setMemberName(omsOrderCreationRequest.getMemberName());
        tps2OrderDetail.setCashAccount(omsOrderCreationRequest.getCashAccount());
        tps2OrderDetail.setIsin(omsOrderCreationRequest.getISIN());
        tps2OrderDetail.setIsinName(omsOrderCreationRequest.getISINName());
        tps2OrderDetail.setExchangeCode(omsOrderCreationRequest.getExchangeCode());
        tps2OrderDetail.setOrderType(omsOrderCreationRequest.getOrderType());
        tps2OrderDetail.setQuantity(omsOrderCreationRequest.getQuantity());
        tps2OrderDetail.setPrice(omsOrderCreationRequest.getPrice());
        tps2OrderDetail.setOrderState(OrderState.SUBMITTED);
        tps2OrderDetail.setCreateTime(LocalDateTime.now());
        tps2OrderDetail = tps2OrderDetailRepository.save(tps2OrderDetail);
        log.info("TPS2 Order Detail save success, tps2 id: {}", tps2OrderDetail.getTps2Id());
        return tps2OrderDetail;
    }

    private BBGOrderCreationMessage getBBGOrderCreationMessage(OMSOrderCreationRequest omsOrderCreationRequest) {
        BBGOrderCreationMessage bbgOrderCreationMessage = new BBGOrderCreationMessage();
        bbgOrderCreationMessage.setUniqueId(omsOrderCreationRequest.getOrderId());
        bbgOrderCreationMessage.setAccountKey(omsOrderCreationRequest.getAccountKey());
        bbgOrderCreationMessage.setISIN(omsOrderCreationRequest.getISIN());
        bbgOrderCreationMessage.setISINName(omsOrderCreationRequest.getISINName());
        bbgOrderCreationMessage.setExchangeCode(omsOrderCreationRequest.getExchangeCode());
        bbgOrderCreationMessage.setOrderType(omsOrderCreationRequest.getOrderType());
        bbgOrderCreationMessage.setQuantity(omsOrderCreationRequest.getQuantity());
        bbgOrderCreationMessage.setPrice(omsOrderCreationRequest.getPrice());
        return bbgOrderCreationMessage;
    }

}
