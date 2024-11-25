package com.sd.dto.document;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class DocumentAnalysisResult extends AbstractResult{

	private String status;
	private AnalysisResult analyzeResult;

}
