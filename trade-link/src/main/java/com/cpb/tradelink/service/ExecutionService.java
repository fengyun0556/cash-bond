package com.cpb.tradelink.service;

import com.cpb.tradelink.dto.ExecutionMessage;

public interface ExecutionService {

    void execute(ExecutionMessage executionMessage);
}
