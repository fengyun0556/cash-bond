package com.cpb.oms.infrastructure.persistence.executed;

import com.cpb.oms.domain.model.executed.BBGExecution;
import com.cpb.oms.domain.repository.ExecutedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ExecutedRepositoryImpl implements ExecutedRepository {
    @Autowired
    private TPS2ExecutionDetailRepository tps2ExecutionDetailRepository;
    @Autowired
    private TPS2ExecutionDetailMapper tps2ExecutionDetailMapper;

    @Override
    public void save(BBGExecution bbgExecution) {
        TPS2ExecutionDetail tps2ExecutionDetail = tps2ExecutionDetailMapper.buildTPS2ExecutionDetail(bbgExecution);
        tps2ExecutionDetail = tps2ExecutionDetailRepository.save(tps2ExecutionDetail);
        bbgExecution.setTps2ExecutionId(tps2ExecutionDetail.getTps2ExecutionId());
    }

    @Override
    public BBGExecution get(Long tps2ExecutionId) {
        Optional<TPS2ExecutionDetail> optional = tps2ExecutionDetailRepository.findById(tps2ExecutionId);
        if (optional.isEmpty()) return null;
        TPS2ExecutionDetail tps2ExecutionDetail = optional.get();
        return tps2ExecutionDetailMapper.buildBBGExecution(tps2ExecutionDetail);
    }
}
