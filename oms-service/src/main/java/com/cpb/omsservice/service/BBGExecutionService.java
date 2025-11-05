package com.cpb.omsservice.service;

import com.cpb.omsservice.dto.BBGExecutionMessage;
import com.cpb.omsservice.entity.BBGExecutionDetail;

public interface BBGExecutionService {
    BBGExecutionDetail saveBBGExecutionDetail(BBGExecutionMessage bbgExecutionMessage);
}
