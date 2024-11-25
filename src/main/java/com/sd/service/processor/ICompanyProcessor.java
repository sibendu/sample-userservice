package com.sd.service.processor;

import java.util.List;

import com.sd.dto.ComsOutputRecordDto;
import com.sd.dto.document.AbstractResult;

public interface ICompanyProcessor {

	String[] getFields();
	ComsOutputRecordDto transformResult(AbstractResult result);

}