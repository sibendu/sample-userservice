package com.sd.dto;

import java.util.Date;
import java.util.List;

import com.sd.model.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class InvoiceRequestDto extends ComsDto{

	private long companyId;
	private Date startDate;
	private Date endDate;
	
	public InvoiceRequestDto(User user, long companyId, Date startDate, Date endDate) {
		super(user);
		this.companyId = companyId;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	
}
