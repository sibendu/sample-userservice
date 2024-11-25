package com.sd.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "job_configuration")
public class JobConfiguration {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String type; // Type of Service. Currently only DATA_EXTRACTION
	private long companyId; 
	private String documentType; // Different type of 
	private String fileType;
	private String processor;
	private Double pricePerUnit;
	
	public JobConfiguration(String type, long companyId, String documentType, String fileType, String processor, Double pricePerUnit) {
		super();
		this.type = type;
		this.companyId = companyId;
		this.documentType = documentType;
		this.fileType = fileType;
		this.processor = processor;
		this.pricePerUnit = pricePerUnit;
	}		
}

	