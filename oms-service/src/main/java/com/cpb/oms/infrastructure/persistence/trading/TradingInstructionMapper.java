package com.cpb.oms.infrastructure.persistence.trading;

import com.cpb.oms.domain.model.trading.TradingInstruction;
import org.springframework.stereotype.Component;

@Component
public class TradingInstructionMapper {
    public TPS2OrderDetail buildTPS2OrderDetail(TradingInstruction tradingInstruction) {
        TPS2OrderDetail tps2OrderDetail = new TPS2OrderDetail();
        tps2OrderDetail.setTps2Id(tradingInstruction.getTps2Id());
        tps2OrderDetail.setTradeLinkId(tradingInstruction.getTradeLinkId());
        tps2OrderDetail.setAccountKey(tradingInstruction.getAccountKey());
        tps2OrderDetail.setAccountName(tradingInstruction.getAccountName());
        tps2OrderDetail.setMemberKey(tradingInstruction.getMemberKey());
        tps2OrderDetail.setMemberName(tradingInstruction.getMemberName());
        tps2OrderDetail.setCashAccount(tradingInstruction.getCashAccount());
        tps2OrderDetail.setIsin(tradingInstruction.getISIN());
        tps2OrderDetail.setIsinName(tradingInstruction.getISINName());
        tps2OrderDetail.setTradeState(tradingInstruction.getTradeState());
        tps2OrderDetail.setExchangeCode(tradingInstruction.getExchangeCode());
        tps2OrderDetail.setOrderType(tradingInstruction.getOrderType());
        tps2OrderDetail.setQuantity(tradingInstruction.getQuantity());
        tps2OrderDetail.setPrice(tradingInstruction.getPrice());
        tps2OrderDetail.setCreateTime(tradingInstruction.getCreateDateTime());
        tps2OrderDetail.setUpdateTime(tradingInstruction.getUpdateDateTime());
        return tps2OrderDetail;
    }

    public TradingInstruction buildTradingInstruction(TPS2OrderDetail tps2OrderDetail) {
        TradingInstruction tradingInstruction = new TradingInstruction();
        tradingInstruction.setTps2Id(tps2OrderDetail.getTps2Id());
        tradingInstruction.setTradeLinkId(tps2OrderDetail.getTradeLinkId());
        tradingInstruction.setAccountKey(tps2OrderDetail.getAccountKey());
        tradingInstruction.setAccountName(tps2OrderDetail.getAccountName());
        tradingInstruction.setMemberKey(tps2OrderDetail.getMemberKey());
        tradingInstruction.setMemberName(tps2OrderDetail.getMemberName());
        tradingInstruction.setCashAccount(tps2OrderDetail.getCashAccount());
        tradingInstruction.setISIN(tps2OrderDetail.getIsin());
        tradingInstruction.setISINName(tps2OrderDetail.getIsinName());
        tradingInstruction.setTradeState(tps2OrderDetail.getTradeState());
        tradingInstruction.setExchangeCode(tps2OrderDetail.getExchangeCode());
        tradingInstruction.setOrderType(tps2OrderDetail.getOrderType());
        tradingInstruction.setQuantity(tps2OrderDetail.getQuantity());
        tradingInstruction.setPrice(tps2OrderDetail.getPrice());
        tradingInstruction.setCreateDateTime(tps2OrderDetail.getCreateTime());
        tradingInstruction.setUpdateDateTime(tps2OrderDetail.getUpdateTime());
        return tradingInstruction;
    }
}
