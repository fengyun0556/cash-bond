package com.cpb.oms.infrastructure.persistence.settlement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementInteractDetailJpaRepository extends JpaRepository<SettlementInteractDetail, Long> {
    SettlementInteractDetail getSettlementInteractDetailByTps2ExecutionId(Long tps2ExecutionId);
}
