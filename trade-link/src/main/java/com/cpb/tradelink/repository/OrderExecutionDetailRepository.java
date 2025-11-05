package com.cpb.tradelink.repository;

import com.cpb.tradelink.entity.OrderExecutionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderExecutionDetailRepository extends JpaRepository<OrderExecutionDetail, Long> {
}
