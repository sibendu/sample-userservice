package com.sd.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobConfigurationRepository extends JpaRepository<JobConfiguration, Long>{
	
	public List<JobConfiguration> findByCompanyId(long companyId);
	public List<JobConfiguration> findByTypeAndCompanyId(String type, long companyId);
	public JobConfiguration findByTypeAndCompanyIdAndDocumentType(String type, long companyId, String documentType);
}