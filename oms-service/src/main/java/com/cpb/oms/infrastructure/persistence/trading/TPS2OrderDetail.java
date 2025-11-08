package com.cpb.oms.infrastructure.persistence.trading;

import com.cpb.oms.domain.enums.OrderType;
import com.cpb.oms.domain.enums.TradeState;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TPS2_ORDER_DETAIL")
@Data
public class TPS2OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TPS2_ID")
    private Long tps2Id;

    @Column(name = "TRADE_LINK_ID")
    private Long tradeLinkId;

    @Column(name = "ACCOUNT_KEY", length = 50)
    private String accountKey;

    @Column(name = "ACCOUNT_NAME", length = 100)
    private String accountName;

    @Column(name = "MEMBER_KEY", length = 50)
    private String memberKey;

    @Column(name = "MEMBER_NAME", length = 100)
    private String memberName;

    @Column(name = "CASH_ACCOUNT", length = 50)
    private String cashAccount;

    @Column(name = "ISIN", length = 12)
    private String isin;

    @Column(name = "ISIN_NAME", length = 100)
    private String isinName;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRADE_STATE")
    private TradeState tradeState;

    @Column(name = "EXCHANGE_CODE", length = 10)
    private String exchangeCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "ORDER_TYPE", length = 20)
    private OrderType orderType;

    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity;

    @Column(name = "PRICE", precision = 20, scale = 4)
    private BigDecimal price;

    @Column(name = "CREATE_TIME", updatable = false)
    private LocalDateTime createTime;

    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;

}
