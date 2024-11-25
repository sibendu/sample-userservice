package com.sd.dto;

import java.util.List;

import com.sd.dto.document.ComsOutputField;
import com.sd.model.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class DashboardDto extends ComsDto{

	private long[] noOfJobs;
	private String[] labels;
	private double billedYTD;
	private double unbilledYTD;
	
	private String storageSetting;
	
	public DashboardDto(User user, long[] noOfJobs, String[] labels, double billedYTD, double unbilledYTD) {
		super(user);
		this.noOfJobs = noOfJobs;
		this.labels = labels;
		this.billedYTD = billedYTD;
		this.unbilledYTD = unbilledYTD;	
	}
}
