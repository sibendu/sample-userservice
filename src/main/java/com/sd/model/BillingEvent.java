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
@Table(name = "billing_event")
public class BillingEvent {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private long companyId;
	private String serviceType;   
	private long referenceId; 

	private Double billingCount; 
	private String billingStatus;
	private Double pricePerUnit;
	
	private Date created;
	private Date updated;
	
	
	public BillingEvent(long companyId, String serviceType, long referenceId, Double billingCount, Double pricePerUnit) {
		super();
		this.companyId = companyId;
		this.serviceType = serviceType;
		this.referenceId = referenceId;
		this.billingCount = billingCount;
		this.pricePerUnit = pricePerUnit;
		
		this.billingStatus = ComsUtil.FLAG_NO;
		this.created = new Date();

	}	
}

	