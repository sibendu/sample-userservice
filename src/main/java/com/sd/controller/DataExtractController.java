package com.sd.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sd.dto.DeleteExtractJobDto;
import com.sd.dto.DownloadExtractJobDto;
import com.sd.dto.ExtractJobDto;
import com.sd.model.*;
import com.sd.service.DataExtractService;
import com.sd.service.StorageService;
import com.sd.util.ComsUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class DataExtractController {	
	
	@Value("${DATA_EXTRACT_SERVICE_URL}")
	private String DATA_EXTRACT_SERVICE_URL;
	
	@Value("${DATA_EXTRACT_API_KEY}")
	private String DATA_EXTRACT_API_KEY;
	
	@Autowired
	JobConfigurationRepository jobConfigRepo;
	
	@Autowired
	JobRepository jobRepo;
	
	@Autowired
	StorageService storageService;
	
	@Autowired
	JobDetailRepository jobDetailRepo;
	
	@Autowired
	BillingEventRepository billingEventRepo;
	
	@Autowired
	InvoiceRepository invoiceRepo;
	
	@Autowired
	CompanyRepository companyRepository;
	
	@GetMapping("/company/{id}/jobconfig")
	public List<JobConfiguration> getJobConfig(@PathVariable Long id) throws Exception{
		return jobConfigRepo.findByTypeAndCompanyId(ComsUtil.JOB_TYPE_DATA_EXTRACTION, id);
	}
	
	@GetMapping("/job/{id}")
	public Job getJob (@PathVariable Long id) throws Exception{
		Job j = jobRepo.findById(id).get();
		//System.out.println(j.getJobDetails() == null? "-1": j.getJobDetails().size());
		return j;
	}
	
	@GetMapping("/job/{id}/details")
	public List<JobDetail> getJobDetails (@PathVariable Long id) throws Exception{
		List<JobDetail> details = jobDetailRepo.findByJobId(id);
		return details;
	}
	
	@PostMapping("/submit")
	public ExtractJobDto submitJob(@RequestParam String data, @RequestParam MultipartFile file) throws Exception{
		
		ObjectMapper objectMapper = new ObjectMapper();
		ExtractJobDto dto = objectMapper.readValue(data, ExtractJobDto.class);
		String fileName = file.getOriginalFilename();
		User user = dto.getUser();
		System.out.println("CompanyId " + user.getCompany().getId() + " user "+user.getUsername() + ": File submitted "+ fileName + " size " + file.getSize());				
		
	
		storageService.storeFile(user, fileName, file);
		System.out.println("CompanyId " + user.getCompany().getId() + " user "+user.getUsername() + ": FIle saved ");				
				
		Job job = new Job(dto.getType(), user.getId(), user.getCompany().getId(), dto.getDescription(), fileName);
		jobRepo.save(job);		
		System.out.println("CompanyId " + user.getCompany().getId() + " user "+user.getUsername() + "Job request created ; id ="+job.getId());				
		
		dto.setId(job.getId());
		dto.setStatus(job.getStatus());
		
		long t1 = System.currentTimeMillis();
		ExecutorService executor = Executors.newSingleThreadExecutor();
		System.out.println("Calling: "+DATA_EXTRACT_SERVICE_URL);
		DataExtractService service = new DataExtractService(job.getId(), user, jobConfigRepo, jobRepo, jobDetailRepo, billingEventRepo, storageService, DATA_EXTRACT_SERVICE_URL, DATA_EXTRACT_API_KEY);
		executor.submit(service);
		long t2 = System.currentTimeMillis();
		
		System.out.println("CompanyId " + user.getCompany().getId() + " Job "+job.getId() + ": DataExtractService started at "+ System.currentTimeMillis() + " (" + (new Date()) + ")");		
				
		return dto;
	}
	
	@PostMapping("/jobs")
	public List<Job> getJobs (@RequestParam String data) throws Exception{
		ObjectMapper objectMapper = new ObjectMapper();
		ExtractJobDto dto = objectMapper.readValue(data, ExtractJobDto.class);
		
		User user = dto.getUser();		
		List<Job> jobs = jobRepo.findByCompanyId(user.getCompany().getId());
		return jobs;
	}
	
	@DeleteMapping("/jobs")
	public String deleteJobs (@RequestBody DeleteExtractJobDto dto) throws Exception{
		System.out.println("DataExtractJobController.deleteJobs: "+dto.getRecordIds());
		jobRepo.deleteAllById(dto.getRecordIds());
		//System.out.println(data);
		return "success";
	}
	
	@PostMapping("/download")
    public void zipDownload(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) throws IOException {
        
		ObjectMapper objectMapper = new ObjectMapper();
		DownloadExtractJobDto dto = objectMapper.readValue(data, DownloadExtractJobDto.class);
		//System.out.println(dto.getJobId()+" - "+dto.getType());
		
		if(dto.getType().equalsIgnoreCase("INVOICE")) {
			
			System.out.println("Downloadig invoice");
			response.setContentType("application/zip"); // zip archive format
	        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
	                                                                            .filename("output.zip", StandardCharsets.UTF_8)
	                                                                            .build()
	                                                                            .toString());
	      
	        Invoice invoice = invoiceRepo.getById(dto.getJobId());
	        
	        Company company = companyRepository.findById(dto.getUser().getCompany().getId()).get();
	        String invoiceFile = storageService.getInvoiceFolder(company) + invoice.getFile();
	        
	        List<Path> files = Arrays.asList(Paths.get(invoiceFile));
	        		
	        // Archiving multiple files and responding to the client
	        try(ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())){
	            for (Path file : files) {
	                try (InputStream inputStream = Files.newInputStream(file)) {
	                    zipOutputStream.putNextEntry(new ZipEntry(file.getFileName().toString()));
	                    StreamUtils.copy(inputStream, zipOutputStream);
	                    zipOutputStream.flush();
	                }
	            }
	        }
	        System.out.println("CompanyId " + dto.getUser().getCompany().getId() + ": Invoice downloaded ");
		}else {
		
			response.setContentType("application/zip"); // zip archive format
	        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
	                                                                            .filename("output.zip", StandardCharsets.UTF_8)
	                                                                            .build()
	                                                                            .toString());
	      
	        Job job = jobRepo.getById(dto.getJobId());
	        String fileName = dto.getType().equalsIgnoreCase("INPUT")? job.getInputFile(): job.getOutputFile();
	        
	        List<Path> files = storageService.getFiles(dto.getUser(), dto.getJobId(), dto.getType(), fileName);
	        
	        // Archiving multiple files and responding to the client
	        try(ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())){
	            for (Path file : files) {
	                try (InputStream inputStream = Files.newInputStream(file)) {
	                    zipOutputStream.putNextEntry(new ZipEntry(file.getFileName().toString()));
	                    StreamUtils.copy(inputStream, zipOutputStream);
	                    zipOutputStream.flush();
	                }
	            }
	        }
	        System.out.println("CompanyId " + dto.getUser().getCompany().getId() + " Job "+ dto.getJobId() + ": File downloaded "+ job.getOutputFile());	
		}
    }

}
