package com.sd.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>{
	
	public List<Company> findByName(String name);
	
	public List<Company> findByTypeOrderByStatusDesc(String type);
	
	public List<Company> findByStatus(String status);

	public List<Company> findByTypeAndStatus(String type, String status);

}