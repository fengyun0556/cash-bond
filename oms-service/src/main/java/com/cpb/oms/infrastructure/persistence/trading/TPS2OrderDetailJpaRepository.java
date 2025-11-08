package com.cpb.oms.infrastructure.persistence.trading;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TPS2OrderDetailJpaRepository extends JpaRepository<TPS2OrderDetail, Long> {
    Optional<TPS2OrderDetail> getByTradeLinkId(Long tradeLinkId);
}
