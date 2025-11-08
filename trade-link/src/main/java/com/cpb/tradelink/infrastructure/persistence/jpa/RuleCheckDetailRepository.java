package com.cpb.tradelink.infrastructure.persistence.jpa;

import com.cpb.tradelink.infrastructure.persistence.entity.RuleCheckDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleCheckDetailRepository extends JpaRepository<RuleCheckDetail, Long> {

    List<RuleCheckDetail> findAllByOrderIdOrderById(Long orderId);
}