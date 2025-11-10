package com.cpb.oms.application.service;

import com.cpb.oms.application.builder.BBGExecutionBuilder;
import com.cpb.oms.domain.event.BBGExecutionMessage;
import com.cpb.oms.domain.event.TradeExecutedEvent;
import com.cpb.oms.domain.model.executed.BBGExecution;
import com.cpb.oms.domain.model.settlement.SettlementInteract;
import com.cpb.oms.domain.model.settlement.SettlementResult;
import com.cpb.oms.domain.repository.ExecutedRepository;
import com.cpb.oms.domain.repository.SettlementInteractRepository;
import com.cpb.oms.domain.service.SettlementIntegrationService;
import com.cpb.oms.domain.service.SettlementInteractService;
import com.cpb.oms.interfaces.executed.ExecutionConfirmedRequest;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Transactional
public class ExecutionApplicationService {

    @Autowired
    private MessageSender messageSender;
    @Autowired
    private BBGExecutionBuilder bbgExecutionBuilder;
    @Autowired
    private ExecutedRepository executedRepository;
    @Autowired
    private SettlementInteractRepository settlementInteractRepository;
    @Autowired
    private SettlementInteractService settlementInteractService;
    @Autowired
    private SettlementIntegrationService settlementIntegrationService;

    public void executed(BBGExecutionMessage bbgExecutionMessage) {
        log.info("开始处理BBG执行消息, BbgMessageId: {}, UniqueId: {}",
                bbgExecutionMessage.getBbgMessageId(), bbgExecutionMessage.getUniqueId());

        try {
            BBGExecution bbgExecution = bbgExecutionBuilder.buildBBGExecution(bbgExecutionMessage);
            log.info("BBGExecution构建完成, BbgMessageId: {}", bbgExecutionMessage.getBbgMessageId());

            bbgExecution.init();
            log.info("BBGExecution初始化完成, BbgMessageId: {}, UniqueId: {}",
                    bbgExecutionMessage.getBbgMessageId(), bbgExecutionMessage.getUniqueId());

            //保存到数据库
            executedRepository.save(bbgExecution);
            log.info("BBGExecution保存完成, BbgMessageId: {}, UniqueId: {}, Tps2ExecutionId: {}",
                    bbgExecutionMessage.getBbgMessageId(), bbgExecutionMessage.getUniqueId(), bbgExecution.getTps2ExecutionId());

        } catch (Exception e) {
            log.error("处理BBG执行消息失败, BbgMessageId: {}, UniqueId: {}, 错误信息: {}",
                    bbgExecutionMessage.getBbgMessageId(), bbgExecutionMessage.getUniqueId(), e.getMessage(), e);
            throw e;
        }
    }

    public void confirmed(ExecutionConfirmedRequest executionConfirmedRequest) {
        log.info("开始确认执行, Tps2ExecutionId: {}", executionConfirmedRequest.getTps2ExecutionId());

        try {
            BBGExecution bbgExecution = executedRepository.get(executionConfirmedRequest.getTps2ExecutionId());
            if (bbgExecution == null) {
                log.error("未找到对应的BBG执行记录, Tps2ExecutionId: {}", executionConfirmedRequest.getTps2ExecutionId());
                throw new RuntimeException("BBGExecution not found for Tps2ExecutionId: " + executionConfirmedRequest.getTps2ExecutionId());
            }
            log.info("找到BBG执行记录, Tps2ExecutionId: {}", bbgExecution.getTps2ExecutionId());

            bbgExecution.confirmed();
            executedRepository.save(bbgExecution);
            log.info("执行确认状态更新并保存完成, Tps2ExecutionId: {}", executionConfirmedRequest.getTps2ExecutionId());

            //发送到TL
            TradeExecutedEvent tradeExecutedEvent = bbgExecution.createTradeExecutedEvent();
            messageSender.send(tradeExecutedEvent);
            log.info("交易执行事件已发送, Tps2ExecutionId: {}", executionConfirmedRequest.getTps2ExecutionId());

            log.info("执行确认流程完成, Tps2ExecutionId: {}", executionConfirmedRequest.getTps2ExecutionId());
        } catch (Exception e) {
            log.error("执行确认流程失败, Tps2ExecutionId: {}, 错误信息: {}",
                    executionConfirmedRequest.getTps2ExecutionId(), e.getMessage(), e);
            throw e;
        }
    }

    public BBGExecution get(Long tps2ExecutionId) {
        log.info("查询BBG执行记录, Tps2ExecutionId: {}", tps2ExecutionId);
        BBGExecution bbgExecution = executedRepository.get(tps2ExecutionId);
        if (bbgExecution == null) {
            log.warn("未找到BBG执行记录, Tps2ExecutionId: {}", tps2ExecutionId);
        } else {
            log.info("BBG执行记录查询完成, Tps2ExecutionId: {}",
                    tps2ExecutionId);
        }
        return bbgExecution;
    }
}
