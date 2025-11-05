package com.cpb.settlementservice.controller;

import com.alibaba.fastjson2.JSONObject;
import com.cpb.settlementservice.dto.SettlementRequest;
import com.cpb.settlementservice.dto.SettlementResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@Slf4j
@RequestMapping("settlement")
public class SettlementController {

    @PostMapping("order")
    public ResponseEntity<SettlementResponse> orderSettlement(@RequestBody SettlementRequest settlementRequest) {
        log.info("settlement request: {}", JSONObject.toJSONString(settlementRequest));
        SettlementResponse settlementResponse = new SettlementResponse();
        settlementResponse.setSuccess(true);

        long min = 1;
        long max = 9999999999L;
        Random random = new Random();
        long range = max - min + 1;
        long randomNumber;

        randomNumber = min + (random.nextLong() % range);
        if (randomNumber < min) {
            randomNumber += range;
        }
        settlementResponse.setSettlementId(randomNumber);

        log.info("response: {}", JSONObject.toJSONString(settlementResponse));
        return ResponseEntity.ok(settlementResponse);
    }
}
