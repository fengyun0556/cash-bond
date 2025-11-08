package com.cpb.oms.infrastructure.persistence.trading;

import com.cpb.oms.domain.model.trading.TradingInstruction;
import com.cpb.oms.domain.repository.TradingInstructionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class TradingInstructionRepositoryImpl implements TradingInstructionRepository {

    @Autowired
    private TPS2OrderDetailJpaRepository tps2OrderDetailJpaRepository;
    @Autowired
    private TradingInstructionMapper tradingInstructionMapper;

    @Override
    public void save(TradingInstruction tradingInstruction) {
        TPS2OrderDetail tps2OrderDetail = tradingInstructionMapper.buildTPS2OrderDetail(tradingInstruction);
        tps2OrderDetail = tps2OrderDetailJpaRepository.save(tps2OrderDetail);
        tradingInstruction.setTps2Id(tps2OrderDetail.getTps2Id());
    }

    @Override
    public TradingInstruction getTradingInstructionByTradeLinkId(Long tradeLinkId) {
        Optional<TPS2OrderDetail> optional = tps2OrderDetailJpaRepository.getByTradeLinkId(tradeLinkId);
        TPS2OrderDetail tps2OrderDetail = optional.get();
        return tradingInstructionMapper.buildTradingInstruction(tps2OrderDetail);
    }


}
