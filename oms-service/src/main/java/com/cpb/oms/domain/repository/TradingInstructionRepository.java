package com.cpb.oms.domain.repository;

import com.cpb.oms.domain.model.trading.TradingInstruction;

public interface TradingInstructionRepository {
    void save(TradingInstruction tradingInstruction);

    TradingInstruction getTradingInstructionByTradeLinkId(Long tradeLinkId);
}
