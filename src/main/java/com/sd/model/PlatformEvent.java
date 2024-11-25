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
@Table(name = "platform_event")
public class PlatformEvent {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private long companyId;
	private long userId;
	private String username;
	private String eventType;  
	private String reference;  
	
	private Date created;

	public PlatformEvent(long companyId, long userId, String username, String eventType, String reference) {
		super();
		this.companyId = companyId;
		this.userId = userId;
		this.username=username;
		this.eventType = eventType;
		this.reference=reference;
		this.created = new Date();
	}
	
}

	