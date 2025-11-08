package com.cpb.tradelink.infrastructure.persistence.jpa;

import com.cpb.tradelink.infrastructure.persistence.entity.OrderExecutionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderExecutionDetailRepository extends JpaRepository<OrderExecutionDetail, Long> {
    List<OrderExecutionDetail> findByOrderIdOrderByExecutionId(Long orderId);
}
