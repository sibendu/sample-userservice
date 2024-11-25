package com.sd.dto;

import java.io.Serializable;
import java.util.List;

import com.sd.model.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class ComsDto implements Serializable {
	
	private User user;
	public ComsDto(User user) {
		this.user = user;
	}
}
