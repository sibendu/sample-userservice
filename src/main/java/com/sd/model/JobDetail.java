package com.sd.model;


import java.util.Date;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sd.util.ComsUtil;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "job_detail")
public class JobDetail {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String inputFile;
	private String status;
	private String message;
	private Date created;
	private Date updated;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnore
	private Job job;
	
	public JobDetail(String inputFile, String status, String message, Date created, Date updated) {
		super();
		this.inputFile = inputFile;
		this.status = status;
		this.message = message;
		this.created = created;
		this.updated = updated;
	}
}

	