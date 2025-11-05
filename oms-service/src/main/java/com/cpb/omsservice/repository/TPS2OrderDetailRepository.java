package com.cpb.omsservice.repository;

import com.cpb.omsservice.entity.TPS2OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TPS2OrderDetailRepository extends JpaRepository<TPS2OrderDetail, Long> {
    Optional<TPS2OrderDetail> getByTradeLinkId(Long tradeLinkId);
}
