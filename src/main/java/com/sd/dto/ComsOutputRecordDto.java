package com.sd.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sd.dto.document.ComsOutputField;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class ComsOutputRecordDto implements Serializable {
	
	List<ComsOutputField> fields;
	
	public ComsOutputRecordDto(List<ComsOutputField> fields) {
		this.fields = fields;
	}
	
	public void addField(ComsOutputField field) {
		if(this.fields == null) {
			this.fields = new ArrayList<>();
		}
		this.fields.add(field);
	}
	
	public ComsOutputField getField(String name) {
		ComsOutputField comsOutputField = null;
		for (Iterator iterator = fields.iterator(); iterator.hasNext();) {
			 comsOutputField = (ComsOutputField) iterator.next();
			if(comsOutputField.getName().equalsIgnoreCase(name)) {
				return comsOutputField;
			}
		}
		return comsOutputField;
	}
}
