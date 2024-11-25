package com.sd.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>{
	
	public List<Job> findByCompanyId(long companyId);
	public List<Job> findByUserId(long userId);
	
}