package com.sd.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobDetailRepository extends JpaRepository<JobDetail, Long>{
	public List<JobDetail> findByJobId(long jobId);	
}