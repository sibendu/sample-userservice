package com.sd.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class ExtractJobDto extends ComsDto{
	
	private long id; 
	private String type;
	private String inputFile;
	private String outputFile;
	private String description;
	private String status;
}
