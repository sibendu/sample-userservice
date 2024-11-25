package com.sd.dto.document;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class ComsOutputField implements Serializable{

	private String name;
	private String value;
	public ComsOutputField(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}
	
	
}
