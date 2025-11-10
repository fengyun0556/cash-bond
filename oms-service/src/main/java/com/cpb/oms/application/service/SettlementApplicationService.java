package com.cpb.oms.application.service;

import com.cpb.oms.domain.event.OrderAmendedEvent;
import com.cpb.oms.domain.event.OrderEnrichedEvent;
import com.cpb.oms.domain.event.SettlementFailedEvent;
import com.cpb.oms.domain.event.TradeExecutedEvent;
import com.cpb.oms.domain.model.settlement.SettlementInteract;
import com.cpb.oms.domain.model.settlement.SettlementResult;
import com.cpb.oms.domain.repository.SettlementInteractRepository;
import com.cpb.oms.domain.service.SettlementIntegrationService;
import com.cpb.oms.domain.service.SettlementInteractService;
import com.cpb.oms.interfaces.settlement.SendToBankerRequest;
import com.cpb.oms.interfaces.settlement.SettlementTriggerRequest;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Transactional
public class SettlementApplicationService {

    @Autowired
    private SettlementInteractRepository settlementInteractRepository;
    @Autowired
    private SettlementIntegrationService settlementIntegrationService;
    @Autowired
    private SettlementInteractService settlementInteractService;
    @Autowired
    private MessageSender messageSender;

    public Boolean trigger(SettlementTriggerRequest settlementTriggerRequest) {
        log.info("开始触发结算流程，请求参数: settlementInteractId={}", settlementTriggerRequest.getSettlementInteractId());

        try {
            SettlementInteract settlementInteract = settlementInteractRepository.getSettlementInteract(settlementTriggerRequest.getSettlementInteractId());
            log.debug("获取到结算交互记录: id={}, status={}", settlementInteract.getId(), settlementInteract.getSettlementState());

            SettlementResult settlementResult = this.callSettlement(settlementInteract);
            boolean success = settlementResult.getSuccess();

            log.info("结算流程触发完成，结果: success={}, settlementInteractId={}", success, settlementTriggerRequest.getSettlementInteractId());
            return success;

        } catch (Exception e) {
            log.error("触发结算流程失败，settlementInteractId={}, 错误信息: {}",
                    settlementTriggerRequest.getSettlementInteractId(), e.getMessage(), e);
            throw e;
        }
    }

    public SettlementInteract get(Long settlementInteractId) {
        log.info("查询结算交互记录，settlementInteractId={}", settlementInteractId);
        try {
            SettlementInteract settlementInteract = settlementInteractRepository.getSettlementInteract(settlementInteractId);
            log.debug("结算交互记录查询完成，settlementInteractId={}, status={}",
                    settlementInteractId, settlementInteract.getSettlementState());
            return settlementInteract;
        } catch (Exception e) {
            log.error("查询结算交互记录失败，settlementInteractId={}, 错误信息: {}", settlementInteractId, e.getMessage(), e);
            throw e;
        }
    }

    public void sendToBanker(SendToBankerRequest sendToBankerRequest) {
        log.info("开始发送给银行家，请求参数: settlementInteractId={}", sendToBankerRequest.getSettlementInteractId());

        try {
            SettlementInteract settlementInteract = settlementInteractRepository.getSettlementInteract(sendToBankerRequest.getSettlementInteractId());
            log.debug("获取到结算交互记录: id={}, status={}", settlementInteract.getId(), settlementInteract.getSettlementState());

            SettlementFailedEvent settlementFailedEvent = settlementInteract.createSettlementFailedEvent();
            messageSender.send(settlementFailedEvent);
            log.info("已发送结算失败事件，settlementInteractId={}, OrderId={}",
                    sendToBankerRequest.getSettlementInteractId(), settlementFailedEvent.getOrderId());

            settlementInteract.sendToBanker();
            log.debug("结算记录状态已更新为发送给银行家，settlementInteractId={}", settlementInteract.getId());

            settlementInteractRepository.save(settlementInteract);
            log.info("发送给银行家流程完成，settlementInteractId={}", sendToBankerRequest.getSettlementInteractId());

        } catch (Exception e) {
            log.error("发送给银行家失败，settlementInteractId={}, 错误信息: {}",
                    sendToBankerRequest.getSettlementInteractId(), e.getMessage(), e);
            throw e;
        }
    }

    public void handle(OrderEnrichedEvent orderEnrichedEvent) {
        log.info("开始处理订单 enrichment 事件，tps2ExecutionId={}", orderEnrichedEvent.getTps2ExecutionId());

        try {
            SettlementInteract settlementInteract = settlementInteractRepository
                    .getSettlementInteractByTPS2ExecutionId(orderEnrichedEvent.getTps2ExecutionId());
            log.debug("获取到结算交互记录: id={}, status={}",
                    settlementInteract.getId(), settlementInteract.getSettlementState());

            boolean canEnrich = settlementInteract.canEnrich();
            log.debug("检查是否可以 enrichment: canEnrich={}", canEnrich);

            if (canEnrich) {
                log.info("开始 enrichment 结算记录，settlementInteractId={}", settlementInteract.getId());
                settlementInteract.enrich(orderEnrichedEvent);
                this.callSettlement(settlementInteract);
                log.info("订单 enrichment 处理完成，settlementInteractId={}", settlementInteract.getId());
            } else {
                log.warn("当前状态不允许 enrichment，settlementInteractId={}, status={}",
                        settlementInteract.getId(), settlementInteract.getSettlementState());
            }

        } catch (Exception e) {
            log.error("处理订单 enrichment 事件失败，tps2ExecutionId={}, 错误信息: {}",
                    orderEnrichedEvent.getTps2ExecutionId(), e.getMessage(), e);
            throw e;
        }
    }

    public void handle(OrderAmendedEvent orderAmendedEvent) {
        log.info("开始处理订单修改事件，tps2ExecutionId={}", orderAmendedEvent.getTps2ExecutionId());

        try {
            SettlementInteract settlementInteract = settlementInteractRepository
                    .getSettlementInteractByTPS2ExecutionId(orderAmendedEvent.getTps2ExecutionId());
            log.debug("获取到结算交互记录: id={}, status={}",
                    settlementInteract.getId(), settlementInteract.getSettlementState());

            boolean canAmend = settlementInteract.canAmend();
            log.debug("检查是否可以修改: canAmend={}", canAmend);

            if (canAmend) {
                log.info("开始修改结算记录，settlementInteractId={}", settlementInteract.getId());
                settlementInteract.amend(orderAmendedEvent);
                this.callSettlement(settlementInteract);
                log.info("订单修改处理完成，settlementInteractId={}", settlementInteract.getId());
            } else {
                log.warn("当前状态不允许修改，settlementInteractId={}, status={}",
                        settlementInteract.getId(), settlementInteract.getSettlementState());
            }

        } catch (Exception e) {
            log.error("处理订单修改事件失败，tps2ExecutionId={}, 错误信息: {}",
                    orderAmendedEvent.getTps2ExecutionId(), e.getMessage(), e);
            throw e;
        }
    }

    private SettlementResult callSettlement(SettlementInteract settlementInteract) {
        log.info("开始调用结算服务，settlementInteractId={}, tps2ExecutionId={}",
                settlementInteract.getId(), settlementInteract.getTps2ExecutionId());

        try {
            //call settlement
            SettlementResult settlementResult = settlementIntegrationService.settlement(settlementInteract);
            log.info("结算服务调用完成，settlementInteractId={}, success={}, message={}",
                    settlementInteract.getId(), settlementResult.getSuccess(), settlementResult.getFailedReason());

            //save settlement result
            settlementInteract.saveSettlementResult(settlementResult);
            log.debug("结算结果已保存到交互记录，settlementInteractId={}", settlementInteract.getId());

            //update DB
            settlementInteractRepository.save(settlementInteract);
            log.debug("结算交互记录已更新到数据库，settlementInteractId={}", settlementInteract.getId());

            return settlementResult;

        } catch (Exception e) {
            log.error("调用结算服务失败，settlementInteractId={}, 错误信息: {}",
                    settlementInteract.getId(), e.getMessage(), e);
            throw e;
        }
    }

    public void orderExecuted(TradeExecutedEvent tradeExecutedEvent) {
        //构建结算交互记录
        SettlementInteract settlementInteract = settlementInteractService.buildSettlementInteract(tradeExecutedEvent);
        settlementInteract.init();
        log.info("结算交互记录初始化完成, SettlementInteractId: {}", settlementInteract.getId());

        //保存到数据库
        settlementInteractRepository.save(settlementInteract);
        log.info("结算交互记录保存完成, SettlementInteractId: {}, Tps2ExecutionId: {}",
                settlementInteract.getId(), settlementInteract.getTps2ExecutionId());

        if (StringUtils.isNotEmpty(settlementInteract.getCashAccount())) {
            log.info("处理LIVE订单结算, Tps2ExecutionId: {}, CashAccount: {}",
                    tradeExecutedEvent.getTps2ExecutionId(), settlementInteract.getCashAccount());

            //调用结算服务
            log.info("开始调用结算服务, SettlementInteractId: {}", settlementInteract.getId());
            SettlementResult settlementResult = settlementIntegrationService.settlement(settlementInteract);
            log.info("结算服务调用完成, SettlementInteractId: {}, Success: {}, Message: {}",
                    settlementInteract.getId(), settlementResult.getSuccess(), settlementResult.getFailedReason());

            //保存结算结果
            settlementInteract.saveSettlementResult(settlementResult);
            log.info("结算结果已保存到交互记录, SettlementInteractId: {}", settlementInteract.getId());

            //更新数据库
            settlementInteractRepository.save(settlementInteract);
            log.info("结算交互记录更新完成, SettlementInteractId: {}", settlementInteract.getId());

        } else {
            log.info("处理PHONE订单, 跳过结算流程, Tps2ExecutionId: {}", tradeExecutedEvent.getTps2ExecutionId());
        }
    }
}