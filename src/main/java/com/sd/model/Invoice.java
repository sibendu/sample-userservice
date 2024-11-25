package com.sd.model;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sd.util.ComsUtil;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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
@Table(name = "invoice")
public class Invoice {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private long companyId;
	private Date startDate;
	private Date endDate;

	private Double amount; 
	private Double tax;
	private Double total;

	private String file;
	
	private String status;

	private Date created;
	private Date updated;
	
	@OneToMany(mappedBy = "invoice", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
	@JsonIgnore
    private Set<InvoiceLineItem> lineItems = new HashSet();
	
	
	public Invoice(long companyId, Date startDate, Date endDate, Double amount, Double tax,
			Double total, String status) {
		super();
		this.companyId = companyId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.amount = amount;
		this.tax = tax;
		this.total = total;
		
		this.status = ComsUtil.INVOICE_STATUS_NEW;
		this.created = new Date();
	}
	
	public void addLineItem(InvoiceLineItem line) {
		this.lineItems.add(line);
		
		this.amount = Double.valueOf(this.amount.doubleValue() + line.getAmount().doubleValue());
		this.tax = Double.valueOf(this.tax.doubleValue() + line.getTax().doubleValue());
		this.total = Double.valueOf(this.total.doubleValue() + line.getTotal().doubleValue());
	}
	
}

	