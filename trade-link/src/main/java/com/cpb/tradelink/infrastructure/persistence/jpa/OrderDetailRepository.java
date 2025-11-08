package com.cpb.tradelink.infrastructure.persistence.jpa;

import com.cpb.tradelink.infrastructure.persistence.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}