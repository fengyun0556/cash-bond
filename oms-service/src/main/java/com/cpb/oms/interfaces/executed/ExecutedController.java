package com.cpb.oms.interfaces.executed;

import com.alibaba.fastjson2.JSONObject;
import com.cpb.oms.application.service.ExecutionApplicationService;
import com.cpb.oms.domain.model.executed.BBGExecution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("executed")
@Slf4j
public class ExecutedController {

    @Autowired
    private ExecutionApplicationService executionApplicationService;
    @Autowired
    private ExecutionAssembler executionAssembler;

    @GetMapping("{tps2ExecutionId}")
    public ResponseEntity<ExecutionResponse> getExecution(@PathVariable Long tps2ExecutionId) {
        log.info("receive execution get request: {}", tps2ExecutionId);
        BBGExecution bbgExecution = executionApplicationService.get(tps2ExecutionId);
        ExecutionResponse executionResponse = executionAssembler.convertToExecutionResponse(bbgExecution);
        log.info("execution get");
        return ResponseEntity.ok(executionResponse);
    }

    @PostMapping("confirmed")
    public ResponseEntity<Boolean> confirmed
            (@RequestBody ExecutionConfirmedRequest executionConfirmedRequest) {
        log.info("receive execution confirmed request: {}", JSONObject.toJSONString(executionConfirmedRequest));
        executionApplicationService.confirmed(executionConfirmedRequest);
        log.info("execution confirmed");
        return ResponseEntity.ok(true);
    }

}
