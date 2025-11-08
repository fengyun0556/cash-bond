package com.cpb.oms.infrastructure.persistence.executed;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TPS2ExecutionDetailRepository extends JpaRepository<TPS2ExecutionDetail, Long> {
}
