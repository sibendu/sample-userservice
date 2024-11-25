package com.sd.service.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sd.dto.ComsOutputRecordDto;
import com.sd.dto.document.AbstractResult;
import com.sd.dto.document.AnalysisParagraph;
import com.sd.dto.document.ComsOutputField;
import com.sd.dto.document.DocumentAnalysisResult;
import com.sd.model.Company;
import com.sd.model.User;
import com.sd.service.AzureDocumentIntelligenceClient;
import com.sd.service.StorageService;

//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;

//@Getter @Setter @NoArgsConstructor
public class Company1Processor implements ICompanyProcessor {

	private static String[] fields = new String[] {"Invoice #", "PO #", "SubTotal", "Sales Tax", "Total", "Payment Terms"};
	
	public static void main(String[] args) throws Exception {
		
		Company1Processor thisProcessor = new Company1Processor();
		
		String SERVICE_URL = "https://eastus.api.cognitive.microsoft.com//documentintelligence/documentModels/prebuilt-layout:analyze?api-version=2023-10-31-preview&features=keyValuePairs";
		String API_KEY = "";
		String resultUrl = "https://westus.api.cognitive.microsoft.com/formrecognizer/documentModels/prebuilt-layout/analyzeResults/6bc6c1c1-7f14-41d7-85eb-1239e7259fa7?api-version=2023-07-31https://westus.api.cognitive.microsoft.com/formrecognizer/documentModels/prebuilt-layout/analyzeResults/ac3734c0-44c0-42f0-b5f6-d4adbe648883?api-version=2023-07-31";
		
		AzureDocumentIntelligenceClient documentService = new AzureDocumentIntelligenceClient(SERVICE_URL, API_KEY);
		
		//String inputFileName = "C:\\Temp\\platform\\invoice2.jpeg";
		//DocumentAnalysisResult result = documentService.process(inputFileName);
				
		DocumentAnalysisResult result = documentService.getProcessingResult(resultUrl);
		
		String[] fields = thisProcessor.getFields();
		ComsOutputRecordDto record = thisProcessor.transformResult(result);
		
		List<ComsOutputRecordDto> records = new ArrayList<>();
		records. add(record);
		
		Company company = new Company(null,"INDIVIDUAL", null, null, null);
		company.setId(1);
		User user = new User();
		user.setCompany(company);
		StorageService storageService = new StorageService();
		String outputFileName = "Output.csv";
		String data = storageService.transform(fields, records);
		storageService.createFile(user, outputFileName, data);
		System.out.println("Output file written - "+outputFileName);
		
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
		
		/*
		for (int k = 0; k < paragraphs.size() ; k++) {
			AnalysisParagraph para = (AnalysisParagraph) paragraphs.get(k);
			System.out.print(k+" :: "+ para.getContent() + "   :::: ");
		}
		System.out.println("\n");
		*/
		
		String fieldName, fieldValue;
		
		//{"Invoice#", "PO#", "SubTotal", "SalesTax", "Total", "PaymentTerms"};
		
		fieldName = fields[0];
		fieldValue = paragraphs.get(10).getContent();
		fieldValue = (fieldValue.length() > 9 && fieldValue.indexOf("DATE:") != -1) ? fieldValue.substring(9, fieldValue.indexOf("DATE:")) : fieldValue;
		dto.addField(new ComsOutputField(fieldName, fieldValue));
		
		fieldName = fields[1];
		fieldValue = paragraphs.get(12).getContent();
		fieldValue = fieldValue.length() > 13 ? fieldValue.substring(13, fieldValue.length()) : fieldValue;
		dto.addField(new ComsOutputField(fieldName, fieldValue));
		
		fieldName = fields[2];
		fieldValue = paragraphs.get(22).getContent();
		dto.addField(new ComsOutputField(fieldName, fieldValue));
		
		fieldName = fields[3];
		fieldValue = paragraphs.get(24).getContent();
		dto.addField(new ComsOutputField(fieldName, fieldValue));
		
		fieldName = fields[4];
		fieldValue = paragraphs.get(26).getContent();
		dto.addField(new ComsOutputField(fieldName, fieldValue));
		
		fieldName = fields[5];
		fieldValue = paragraphs.get(8).getContent();
		dto.addField(new ComsOutputField(fieldName, fieldValue));
		
		return dto;
	}
}
