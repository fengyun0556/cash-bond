package com.cpb.omsservice.service.impl;

import com.cpb.omsservice.dto.BBGExecutionMessage;
import com.cpb.omsservice.entity.BBGExecutionDetail;
import com.cpb.omsservice.repository.BBGExecutionDetailRepository;
import com.cpb.omsservice.service.BBGExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class BBGExecutionServiceImpl implements BBGExecutionService {

    @Autowired
    private BBGExecutionDetailRepository bbgExecutionDetailRepository;

    @Override
    public BBGExecutionDetail saveBBGExecutionDetail(BBGExecutionMessage bbgExecutionMessage) {
        BBGExecutionDetail bbgExecutionDetail = new BBGExecutionDetail();
        bbgExecutionDetail.setBbgMessageId(bbgExecutionMessage.getBbgMessageId());
        bbgExecutionDetail.setTradeLinkId(bbgExecutionMessage.getUniqueId());
        bbgExecutionDetail.setAccountKey(bbgExecutionMessage.getAccountKey());
        bbgExecutionDetail.setExecutedQuantity(bbgExecutionMessage.getExecutedQuantity());
        bbgExecutionDetail.setExecutedPrice(bbgExecutionMessage.getExecutedPrice());
        bbgExecutionDetail.setCreateTime(LocalDateTime.now());
        bbgExecutionDetail = bbgExecutionDetailRepository.save(bbgExecutionDetail);
        log.info("BBGExecutionDetail save success, BbgExecutionId: {}", bbgExecutionDetail.getBbgExecutionId());
        return bbgExecutionDetail;
    }
}
