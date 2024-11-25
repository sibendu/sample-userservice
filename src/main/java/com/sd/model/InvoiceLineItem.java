package com.sd.model;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "invoice_line")
public class InvoiceLineItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String serviceType;   

	private Double totalUsage; 
	private Double pricePerUnit;
	
	private Double amount; 
	private Double tax;
	private Double total;
	
	private Date created;
	private Date updated;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Invoice invoice;

	public InvoiceLineItem(String serviceType, Double totalUsage, Double pricePerUnit, Double amount,
			Double tax, Double total) {
		super();
		this.serviceType = serviceType;
		this.totalUsage = totalUsage;
		this.pricePerUnit = pricePerUnit;
		this.amount = amount;
		this.tax = tax;
		this.total = total;
		
		this.created = new Date();
	}
}

	