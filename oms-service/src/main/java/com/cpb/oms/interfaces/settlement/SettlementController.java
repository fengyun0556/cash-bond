package com.cpb.oms.interfaces.settlement;

import com.alibaba.fastjson2.JSONObject;
import com.cpb.oms.application.service.SettlementApplicationService;
import com.cpb.oms.domain.model.settlement.SettlementInteract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("settlement")
@Slf4j
public class SettlementController {

    @Autowired
    private SettlementApplicationService settlementApplicationService;
    @Autowired
    private SettlementInteractAssembler settlementInteractAssembler;

    @GetMapping("{settlementInteractId}")
    public ResponseEntity<SettlementInteractResponse> getExecution(@PathVariable Long settlementInteractId) {
        log.info("receive execution get request: {}", settlementInteractId);
        SettlementInteract settlementInteract = settlementApplicationService.get(settlementInteractId);
        SettlementInteractResponse settlementInteractResponse =
                settlementInteractAssembler.convertToSettlementInteractResponse(settlementInteract);
        log.info("execution get");
        return ResponseEntity.ok(settlementInteractResponse);
    }

    @PostMapping("sendToBanker")
    public ResponseEntity<Boolean> sendToBanker(@RequestBody SendToBankerRequest sendToBankerRequest) {
        log.info("send To Banker, {}", JSONObject.toJSONString(sendToBankerRequest));
        settlementApplicationService.sendToBanker(sendToBankerRequest);
        log.info("send To Banker 结束");
        return ResponseEntity.ok(true);
    }

    @PostMapping("trigger")
    public ResponseEntity<Boolean> trigger(@RequestBody SettlementTriggerRequest settlementTriggerRequest) {
        log.info("trigger, {}", JSONObject.toJSONString(settlementTriggerRequest));
        Boolean success = settlementApplicationService.trigger(settlementTriggerRequest);
        log.info("trigger 结束, {}", success);
        return ResponseEntity.ok(success);
    }
}
