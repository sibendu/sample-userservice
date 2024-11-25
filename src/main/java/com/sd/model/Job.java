package com.sd.model;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sd.util.ComsUtil;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "job")
public class Job {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String type;
	private long userId; 
	private long companyId; 
	private String status;
	private String description;
	private String inputFile;
	private String outputFile;
	private String message;
	private Date created;
	private Date updated;
	
	@OneToMany(mappedBy = "job", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
	private Set<JobDetail> jobDetails = new HashSet();
	
	public Job(String type, long userId, long companyId, String description, String inputFile) {
		super();
		this.type = type;
		this.userId = userId;
		this.companyId = companyId;
		this.status = ComsUtil.JOB_STATUS_NEW;
		this.description = description;
		this.inputFile = inputFile;
		this.created = new Date();
	}
	
	public void addJobdetail(JobDetail jd) {
		this.jobDetails.add(jd);
	}
}

	