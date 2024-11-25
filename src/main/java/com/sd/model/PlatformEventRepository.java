package com.sd.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformEventRepository extends JpaRepository<PlatformEvent, Long>{
	
	public List<PlatformEvent> findByEventTypeOrderByCreatedDesc(String eventType);
	public List<PlatformEvent> findByCompanyIdAndEventTypeOrderByCreatedDesc(long companyId, String eventType);
	public List<PlatformEvent> findByCompanyIdAndUserIdAndEventTypeOrderByCreatedDesc(long companyId, long userId, String eventType);
}