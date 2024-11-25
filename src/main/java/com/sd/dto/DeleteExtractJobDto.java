package com.sd.dto;

import java.util.List;

import com.sd.model.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class DeleteExtractJobDto extends ComsDto{
	private List<Long> recordIds;

	public DeleteExtractJobDto(User user, List<Long> ids) {
		super(user);
		this.recordIds = ids;
	}	
}
