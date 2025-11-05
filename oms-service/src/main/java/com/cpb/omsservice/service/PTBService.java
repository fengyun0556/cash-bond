package com.cpb.omsservice.service;

import com.cpb.omsservice.dto.PTBMessage;
import com.cpb.omsservice.entity.PTBDetail;

public interface PTBService {
    PTBDetail savePTBDetail(PTBMessage ptbMessage);

    void sendToSettlementService(PTBDetail ptbDetail, PTBMessage ptbMessage);
}
