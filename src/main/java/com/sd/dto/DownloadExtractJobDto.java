package com.sd.dto;

import com.sd.model.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class DownloadExtractJobDto extends ComsDto{
	private long jobId;
	private String type;

	public DownloadExtractJobDto(User user, long jobId, String type) { 
		super(user);
		this.jobId = jobId;
		this.type = type;
	}	
}
