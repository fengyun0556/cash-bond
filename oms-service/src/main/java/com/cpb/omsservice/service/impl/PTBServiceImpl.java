package com.cpb.omsservice.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.cpb.omsservice.dto.PTBMessage;
import com.cpb.omsservice.dto.SettlementRequest;
import com.cpb.omsservice.dto.SettlementResponse;
import com.cpb.omsservice.entity.PTBDetail;
import com.cpb.omsservice.repository.PTBDetailRepository;
import com.cpb.omsservice.service.PTBService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@Slf4j
public class PTBServiceImpl implements PTBService {
    @Value("${settlement.host}")
    private String SETTLEMENT_HOST;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PTBDetailRepository ptbDetailRepository;

    @Override
    public PTBDetail savePTBDetail(PTBMessage ptbMessage) {
        PTBDetail ptbDetail = new PTBDetail();
        ptbDetail.setBbgExecutionId(ptbMessage.getBbgExecutionId());
        ptbDetail.setAccountKey(ptbMessage.getAccountKey());
        ptbDetail.setAccountName(ptbMessage.getAccountName());
        ptbDetail.setMemberKey(ptbMessage.getMemberKey());
        ptbDetail.setMemberName(ptbMessage.getMemberName());
        ptbDetail.setCashAccount(ptbMessage.getCashAccount());
        ptbDetail.setIsin(ptbMessage.getISIN());
        ptbDetail.setExecutedQuantity(ptbMessage.getExecutedQuantity());
        ptbDetail.setExecutedPrice(ptbMessage.getExecutedPrice());
        ptbDetail.setCreateTime(LocalDateTime.now());
        ptbDetail = ptbDetailRepository.save(ptbDetail);
        log.info("save ptb detail, bbg Execution Id: {}", ptbDetail.getBbgExecutionId());
        return ptbDetail;
    }

    @Override
    public void sendToSettlementService(PTBDetail ptbDetail, PTBMessage ptbMessage) {
        //send to settlement service
        SettlementRequest settlementRequest = this.getSettlementRequest(ptbDetail, ptbMessage);
        String url = SETTLEMENT_HOST + "/settlement/order";
        log.info("settlement request: {}", JSONObject.toJSONString(settlementRequest));
        ResponseEntity<SettlementResponse> response = restTemplate.postForEntity
                (url, settlementRequest, SettlementResponse.class);
        log.info("settlement response: {}", JSONObject.toJSONString(response));
        if (HttpStatus.OK.equals(response.getStatusCode()) && response.getBody().getSuccess()) {
            Long settlementId = response.getBody().getSettlementId();
            ptbDetail.setSettlementId(settlementId);
            ptbDetail.setUpdateTime(LocalDateTime.now());
            ptbDetailRepository.save(ptbDetail);
            log.info("ptb detail update success, bbg Execution Id: {}", ptbDetail.getBbgExecutionId());
        } else {
            log.error("ptb id: {}, bad response, status: {}, response body: {}",
                    ptbDetail.getPtbId(), response.getStatusCode(), JSONObject.toJSONString(response.getBody()));
        }
    }

    private SettlementRequest getSettlementRequest(PTBDetail ptbDetail, PTBMessage ptbMessage) {
        return new SettlementRequest();
    }
}
