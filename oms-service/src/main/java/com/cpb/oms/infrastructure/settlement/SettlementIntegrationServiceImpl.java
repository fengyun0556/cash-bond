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
        log.info("开始调用结算服务，settlementInteractId={}, tps2ExecutionId={}",
                settlementInteract.getId(), settlementInteract.getTps2ExecutionId());

        try {
            SettlementRequest settlementRequest = settlementMapper.buildSettlementInteract(settlementInteract);
            log.debug("结算请求构建完成，settlementInteractId={}", settlementInteract.getId());

            String url = SETTLEMENT_HOST + "/settlement/order";
            log.info("发送结算请求，url={}, settlementInteractId={}", url, settlementInteract.getId());
            log.debug("结算请求详情: {}", JSONObject.toJSONString(settlementRequest));

            ResponseEntity<SettlementResponse> response = restTemplate.postForEntity(url, settlementRequest, SettlementResponse.class);
            log.info("收到结算响应，settlementInteractId={}, httpStatus={}",
                    settlementInteract.getId(), response.getStatusCode());
            log.debug("结算响应详情: {}", JSONObject.toJSONString(response.getBody()));

            if (HttpStatus.OK.equals(response.getStatusCode()) && response.getBody().getSuccess()) {
                log.info("结算服务调用成功，settlementInteractId={}, tps2ExecutionId={}",
                        settlementInteract.getId(), settlementInteract.getTps2ExecutionId());
                SettlementResult settlementResult = settlementMapper.buildSettlementResult(response.getBody());
                log.debug("结算结果映射完成，settlementInteractId={}", settlementInteract.getId());
                return settlementResult;
            } else {
                response.getBody();
                response.getBody();
                log.error("结算服务返回错误，settlementInteractId={}, httpStatus={}, success={}, errorMessage={}",
                        settlementInteract.getId(),
                        response.getStatusCode(),
                        response.getBody().getSuccess(),
                        response.getBody().getFailedReason());

                SettlementResult settlementResult = new SettlementResult();
                settlementResult.setSuccess(false);
                response.getBody();
                settlementResult.setFailedReason("结算服务返回错误: " +
                        response.getBody().getFailedReason());
                return settlementResult;
            }

        } catch (Exception e) {
            log.error("调用结算服务异常，settlementInteractId={}, url={}, 错误信息: {}",
                    settlementInteract.getId(), SETTLEMENT_HOST + "/settlement/order", e.getMessage(), e);

            SettlementResult settlementResult = new SettlementResult();
            settlementResult.setSuccess(false);
            settlementResult.setFailedReason("调用结算服务异常: " + e.getMessage());
            return settlementResult;
        }
    }
}