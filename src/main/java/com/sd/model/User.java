package com.sd.model;


import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sd.util.ComsUtil;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
@Table(name = "person")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String firstName;
	private String lastName;			
	//private String email;
	private String phone;
	
	@Column(unique=true)
	private String username;
	
	private String password;
	
	private String status;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Company company;
	
	public User(String firstName, String lastName, String phone, String username, String password) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		//this.email = email;
		this.phone = phone;
		this.username = username;
		this.password = password;
		
		this.status = ComsUtil.STATUS_ACTIVE;
	}
}

	