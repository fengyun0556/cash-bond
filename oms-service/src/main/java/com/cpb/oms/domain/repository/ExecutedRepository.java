package com.cpb.oms.domain.repository;

import com.cpb.oms.domain.model.executed.BBGExecution;

public interface ExecutedRepository {
    void save(BBGExecution bbgExecution);

    BBGExecution get(Long tps2ExecutionId);
}
