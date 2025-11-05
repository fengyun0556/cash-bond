package com.cpb.tradelink.service;

import com.cpb.tradelink.dto.BBGAckMessage;

public interface BBGAckService {

    void ack(BBGAckMessage bbgAckMessage);
}
