package com.sd.service.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sd.dto.ComsOutputRecordDto;
import com.sd.dto.document.AbstractResult;
import com.sd.dto.document.AnalysisParagraph;
import com.sd.dto.document.ComsOutputField;
import com.sd.dto.document.DocumentAnalysisResult;
import com.sd.dto.document.KeyValuePair;
import com.sd.model.Company;
import com.sd.model.User;
import com.sd.service.AzureDocumentIntelligenceClient;
import com.sd.service.StorageService;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class GenericProcessor implements ICompanyProcessor {
		
	private String[] fields;
	
	public static void main(String[] args) throws Exception {
		
		GenericProcessor thisProcessor = new GenericProcessor();
		
		String SERVICE_URL = "https://eastus.api.cognitive.microsoft.com//documentintelligence/documentModels/prebuilt-layout:analyze?api-version=2023-10-31-preview&features=keyValuePairs";
		String API_KEY = "";
		String resultUrl = "https://eastus.api.cognitive.microsoft.com/documentintelligence/documentModels/prebuilt-layout/analyzeResults/7ea993f6-1e7d-4aff-ac38-33f2688f972c?api-version=2023-10-31-preview";
		
		AzureDocumentIntelligenceClient documentService = new AzureDocumentIntelligenceClient(SERVICE_URL, API_KEY);
		
		DocumentAnalysisResult result = documentService.getProcessingResult(resultUrl);
		
		ComsOutputRecordDto record = thisProcessor.transformResult(result);
		
		List<ComsOutputField> recordFields = record.getFields();
		
		String[] fields = new String[recordFields.size()];
		
		int k = 0;
		for (ComsOutputField f : recordFields) {
			fields[k++] = f.getName();
			System.out.println(f.getName() + "  -- " +f.getValue());
		}
		
		/*
		List<ComsOutputRecordDto> records = new ArrayList<>();
		records. add(record);
		
		Company company = new Company(null,"INDIVIDUAL", null, null, null);
		company.setId(1);
		User user = new User();
		user.setCompany(company);
		StorageService storageService = new StorageService();
		String outputFileName = "Output.csv";
		String data = storageService.transform(fields, records);
		System.out.println(data);
		storageService.createFile(user, outputFileName, data);
		System.out.println("Output file written - "+outputFileName);
		*/
	}

	@Override
	public String[] getFields() {
		return this.fields;
	}
	
	@Override
	public ComsOutputRecordDto transformResult(AbstractResult res){		
		
		ComsOutputRecordDto dto = new ComsOutputRecordDto();
		DocumentAnalysisResult result = (DocumentAnalysisResult)res;
		List<AnalysisParagraph> paragraphs = result.getAnalyzeResult().getParagraphs();
		
		String fieldName, fieldValue;
		
		List<KeyValuePair> kvp = result.getAnalyzeResult().getKeyValuePairs();
		int k = 0;
		for (KeyValuePair kv : kvp) {
			fieldName = kv.getKey().getContent();
			fieldName = fieldName.replaceAll("\n", " ");
			fieldValue = kv.getValue() == null ? "" : kv.getValue().getContent();
			dto.addField(new ComsOutputField(fieldName, fieldValue));
		}
		
		return dto;
	}
}
