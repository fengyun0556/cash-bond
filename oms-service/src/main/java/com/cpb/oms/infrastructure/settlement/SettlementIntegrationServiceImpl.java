package com.cpb.oms.infrastructure.settlement;

import com.alibaba.fastjson2.JSONObject;
import com.cpb.oms.domain.model.settlement.SettlementInteract;
import com.cpb.oms.domain.model.settlement.SettlementResult;
import com.cpb.oms.domain.service.SettlementIntegrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class SettlementIntegrationServiceImpl implements SettlementIntegrationService {
    @Value("${settlement.host}")
    private String SETTLEMENT_HOST;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private SettlementMapper settlementMapper;

    @Override
    public SettlementResult settlement(SettlementInteract settlementInteract) {
        SettlementRequest settlementRequest = settlementMapper.buildSettlementInteract(settlementInteract);

        String url = SETTLEMENT_HOST + "/settlement/order";
        log.info("settlement request: {}", JSONObject.toJSONString(settlementRequest));
        ResponseEntity<SettlementResponse> response = restTemplate.postForEntity
                (url, settlementRequest, SettlementResponse.class);
        log.info("settlement response: {}", JSONObject.toJSONString(response));
        if (HttpStatus.OK.equals(response.getStatusCode()) && response.getBody().getSuccess()) {
            log.info("ptb detail update success, bbg Execution Id: {}", settlementInteract.getId());
            return settlementMapper.buildSettlementResult(response.getBody());
        } else {
            log.error("ptb id: {}, bad response, status: {}, response body: {}",
                    settlementInteract.getId(), response.getStatusCode(), JSONObject.toJSONString(response.getBody()));
            SettlementResult settlementResult = new SettlementResult();
            settlementResult.setSuccess(false);
            return settlementResult;
        }
    }
}
