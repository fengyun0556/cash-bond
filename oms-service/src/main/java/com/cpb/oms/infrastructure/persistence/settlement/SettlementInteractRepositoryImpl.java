package com.cpb.oms.infrastructure.persistence.settlement;

import com.cpb.oms.domain.model.settlement.SettlementInteract;
import com.cpb.oms.domain.repository.SettlementInteractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SettlementInteractRepositoryImpl implements SettlementInteractRepository {
    @Autowired
    private SettlementInteractDetailJpaRepository settlementInteractDetailJpaRepository;
    @Autowired
    private SettlementInteractMapper settlementInteractMapper;

    @Override
    public void save(SettlementInteract settlementInteract) {
        SettlementInteractDetail settlementInteractDetail = settlementInteractMapper.buildSettlementInteractDetail(settlementInteract);
        settlementInteractDetail = settlementInteractDetailJpaRepository.save(settlementInteractDetail);
        settlementInteract.setId(settlementInteractDetail.getId());
    }

    @Override
    public SettlementInteract getSettlementInteract(Long settlementInteractId) {
        Optional<SettlementInteractDetail> optional = settlementInteractDetailJpaRepository.findById(settlementInteractId);
        if (optional.isEmpty()) return null;
        SettlementInteractDetail settlementInteractDetail = optional.get();
        return settlementInteractMapper.buildSettlementInteract(settlementInteractDetail);
    }

    @Override
    public SettlementInteract getSettlementInteractByTPS2ExecutionId(Long tps2ExecutionId) {
        SettlementInteractDetail settlementInteractDetail = settlementInteractDetailJpaRepository.getSettlementInteractDetailByTps2ExecutionId(tps2ExecutionId);
        return settlementInteractMapper.buildSettlementInteract(settlementInteractDetail);
    }
}
