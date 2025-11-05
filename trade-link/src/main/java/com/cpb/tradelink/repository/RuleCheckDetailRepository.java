package com.cpb.tradelink.repository;

import com.cpb.tradelink.entity.RuleCheckDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleCheckDetailRepository extends JpaRepository<RuleCheckDetail, Long> {
}