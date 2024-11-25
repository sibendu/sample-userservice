package com.sd.dto;

import java.util.List;
import java.util.Set;

import com.sd.model.JobConfiguration;
import com.sd.model.User;
import com.sd.model.Module;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class CompanyUpdateRequestDto extends ComsDto{

	private long companyId;
	private Set<Module> targetModules;
	private JobConfiguration jobConfig;
	
	public CompanyUpdateRequestDto(User user, long companyId, Set<Module> targetModules, JobConfiguration jobConfig) {
		super(user);
		this.companyId = companyId;
		this.targetModules = targetModules;
		this.jobConfig = jobConfig;
	}
}
