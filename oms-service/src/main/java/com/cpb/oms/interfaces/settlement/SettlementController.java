package com.cpb.oms.interfaces.settlement;

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
        settlementApplicationService.sendToBanker(sendToBankerRequest);
        return ResponseEntity.ok(true);
    }

    @PostMapping("trigger")
    public ResponseEntity<Boolean> trigger(@RequestBody SettlementTriggerRequest settlementTriggerRequest) {
        Boolean success = settlementApplicationService.trigger(settlementTriggerRequest);
        return ResponseEntity.ok(success);
    }
}
