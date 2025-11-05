package com.cpb.tradelink.entity;

import com.cpb.tradelink.enums.OrderRequestMode;
import com.cpb.tradelink.enums.OrderState;
import com.cpb.tradelink.enums.OrderType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ORDER_DETAIL")
@Data
public class OrderDetail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private Long orderId;
    
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
    
    @Column(name = "EXCHANGE_CODE", length = 10)
    private String exchangeCode;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "ORDER_TYPE", length = 20)
    private OrderType orderType;
    
    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity;

    @Column(name = "TOTAL_EXECUTED_QUANTITY")
    private Integer totalExecutedQuantity;

    @Column(name = "PRICE", precision = 20, scale = 4)
    private BigDecimal price;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "ORDER_REQUEST_MODE", length = 20)
    private OrderRequestMode orderRequestMode;
    
    @Column(name = "TPS2_ID")
    private Long tps2Id;

    @Enumerated(EnumType.STRING)
    @Column(name = "ORDER_STATE")
    private OrderState orderState;
    
    @Column(name = "CREATE_TIME", updatable = false)
    private LocalDateTime createTime;
    
    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;
    
}