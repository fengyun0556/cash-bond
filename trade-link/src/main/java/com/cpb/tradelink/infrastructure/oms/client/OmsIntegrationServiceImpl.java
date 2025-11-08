package com.cpb.tradelink.infrastructure.oms.client;

import com.alibaba.fastjson2.JSONObject;
import com.cpb.tradelink.domain.model.OmsSubmissionResult;
import com.cpb.tradelink.domain.model.Order;
import com.cpb.tradelink.domain.service.OmsIntegrationService;
import com.cpb.tradelink.infrastructure.oms.adapter.OMSOrderMapper;
import com.cpb.tradelink.infrastructure.oms.dto.OMSOrderCreationRequest;
import com.cpb.tradelink.infrastructure.oms.dto.OMSOrderCreationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class OmsIntegrationServiceImpl implements OmsIntegrationService {

    @Autowired
    private RestTemplate restTemplate;
    @Value("${oms.host}")
    private String OMS_HOST;
    @Autowired
    private OMSOrderMapper omsOrderMapper;

    @Override
    public OmsSubmissionResult submitToOms(Order order) {
        OMSOrderCreationRequest omsOrderCreationRequest = omsOrderMapper.toOMSOrderCreationRequest(order);

        String url = OMS_HOST + "/oms-service/order";
        log.info("send to OMS: {}", JSONObject.toJSONString(omsOrderCreationRequest));
        ResponseEntity<OMSOrderCreationResponse> response = restTemplate.postForEntity
                (url, omsOrderCreationRequest, OMSOrderCreationResponse.class);
        log.info("OMS response status: {}, OMS response: {}",
                JSONObject.toJSONString(response.getStatusCode()),
                JSONObject.toJSONString(response.getBody()));

        OmsSubmissionResult omsSubmissionResult = new OmsSubmissionResult();
        if (HttpStatus.OK.equals(response.getStatusCode())
                && response.getBody().getSuccess()
                && response.getBody().getTps2Id() != null) {
            omsSubmissionResult.setSuccess(true);
            omsSubmissionResult.setTps2Id(response.getBody().getTps2Id());
        } else {
            omsSubmissionResult.setSuccess(false);
        }
        return omsSubmissionResult;
    }

}
