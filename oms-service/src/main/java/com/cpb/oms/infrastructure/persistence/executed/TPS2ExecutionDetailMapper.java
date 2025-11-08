package com.cpb.oms.infrastructure.persistence.executed;

import com.cpb.oms.domain.model.executed.BBGExecution;
import org.springframework.stereotype.Component;

@Component
public class TPS2ExecutionDetailMapper {
    public TPS2ExecutionDetail buildTPS2ExecutionDetail(BBGExecution bbgExecution) {
        TPS2ExecutionDetail tps2ExecutionDetail = new TPS2ExecutionDetail();
        tps2ExecutionDetail.setBbgMessageId(bbgExecution.getBbgMessageId());
        tps2ExecutionDetail.setTradeLinkId(bbgExecution.getTradeLinkId());
        tps2ExecutionDetail.setAccountKey(bbgExecution.getAccountKey());
        tps2ExecutionDetail.setExecutedQuantity(bbgExecution.getExecutedQuantity());
        tps2ExecutionDetail.setExecutedPrice(bbgExecution.getExecutedPrice());
        tps2ExecutionDetail.setCreateTime(bbgExecution.getCreateTime());
        tps2ExecutionDetail.setUpdateTime(bbgExecution.getUpdateTime());
        return tps2ExecutionDetail;
    }

    public BBGExecution buildBBGExecution(TPS2ExecutionDetail tps2ExecutionDetail) {
        BBGExecution bbgExecution = new BBGExecution();
        bbgExecution.setTps2ExecutionId(tps2ExecutionDetail.getTps2ExecutionId());
        bbgExecution.setTradeLinkId(tps2ExecutionDetail.getTradeLinkId());
        bbgExecution.setBbgMessageId(tps2ExecutionDetail.getBbgMessageId());
        bbgExecution.setAccountKey(tps2ExecutionDetail.getAccountKey());
        bbgExecution.setISIN(tps2ExecutionDetail.getAccountKey());
        bbgExecution.setExecutedQuantity(tps2ExecutionDetail.getExecutedQuantity());
        bbgExecution.setExecutedPrice(tps2ExecutionDetail.getExecutedPrice());
        bbgExecution.setConfirmed(tps2ExecutionDetail.getConfirmed());
        bbgExecution.setConfirmedDateTime(tps2ExecutionDetail.getConfirmedDateTime());
        bbgExecution.setCreateTime(tps2ExecutionDetail.getCreateTime());
        bbgExecution.setUpdateTime(tps2ExecutionDetail.getUpdateTime());
        return bbgExecution;
    }
}
