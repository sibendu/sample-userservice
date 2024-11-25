package com.sd.dto.document;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class AnalysisResult implements Serializable {

	private String apiVersion;
	private String modelId;
	private String content;
	private List<AnalysisParagraph> paragraphs;
	private List<KeyValuePair> keyValuePairs;

}
