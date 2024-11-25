package com.sd.model;


import java.util.HashSet;
import java.util.Set;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "company")
public class Company {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;
	private String type;
	private String rootuser;
	private String address;
	private String country;
	
	private String status;
	
	@OneToMany(mappedBy = "company", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
	@JsonIgnore
    private Set<User> users = new HashSet();

	@ManyToMany
	@JoinTable(
	  name = "assoc_company_module", 
	  joinColumns = @JoinColumn(name = "company_id"), 
	  inverseJoinColumns = @JoinColumn(name = "module_id"))
	private Set<Module> modules = new HashSet();
	
	public Company(String name, String type, String rootuser, String address, String country) {
		super();
		this.name = name;
		this.type = type;
		this.rootuser = rootuser;
		this.address = address;
		this.country = country;
		
		this.status = ComsUtil.STATUS_ACTIVE;
	}
	
	public void addUser(User u) {
		this.users.add(u);
	}
	
	public void addModule(Module m) {
		this.modules.add(m);
	}

}

	