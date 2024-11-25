package com.sd.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingEventRepository extends JpaRepository<BillingEvent, Long>{
	
	public List<BillingEvent> findAllByOrderByCreatedDesc();
	public List<BillingEvent> findAllByCompanyIdOrderByCreatedAsc(long companyId);
	public List<BillingEvent> findAllByCompanyIdAndServiceTypeAndBillingStatusOrderByCreatedAsc(long companyId, String serviceType, String billiStatus);
}