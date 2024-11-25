package com.sd.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import com.sd.dto.ComsOutputRecordDto;
import com.sd.dto.document.DocumentAnalysisResult;
import com.sd.model.BillingEvent;
import com.sd.model.BillingEventRepository;
import com.sd.model.Job;
import com.sd.model.JobConfiguration;
import com.sd.model.JobConfigurationRepository;
import com.sd.model.JobDetail;
import com.sd.model.JobDetailRepository;
import com.sd.model.JobRepository;
import com.sd.model.User;
import com.sd.service.processor.ICompanyProcessor;
import com.sd.util.ComsUtil;

public class DataExtractService implements Callable<Void>{
	
	private long jobId;
	private User user;
	private JobConfigurationRepository jobConfigRepo;
	private JobRepository jobRepo;
	private JobDetailRepository jobDetailRepo;
	private StorageService storageService;
	private BillingEventRepository billingEventRepo;
	
	private String DATA_EXTRACT_SERVICE_URL;
	private String DATA_EXTRACT_API_KEY;
	
	public DataExtractService(long jobId, User user, JobConfigurationRepository jobConfigRepo, JobRepository jobRepo, JobDetailRepository jobDetailRepo, BillingEventRepository billingEventRepo, StorageService storageService, String DATA_EXTRACT_SERVICE_URL, String DATA_EXTRACT_API_KEY) {
		this.jobId = jobId;
		this.user = user;
		this.jobRepo = jobRepo;
		this.storageService = storageService;
		this.jobConfigRepo = jobConfigRepo;
		this.billingEventRepo = billingEventRepo;
		
		this.DATA_EXTRACT_SERVICE_URL=DATA_EXTRACT_SERVICE_URL;
		this.DATA_EXTRACT_API_KEY=DATA_EXTRACT_API_KEY;
	}
	
	
	
	@Override
	public Void call() {
		Job job = null;
		try {
			System.out.println("CompanyId " + user.getCompany().getId() + " Job "+jobId + ": Job processing started at "+ System.currentTimeMillis() + " (" + (new Date()) + ")");		
			//Thread.sleep(5000L);
			
			job = jobRepo.findById(jobId).get();
			System.out.println("CompanyId " + user.getCompany().getId() + " Job "+jobId + ": Found job details");
			System.out.println("Retreived job details - "+job.getDescription());
			job.setStatus(ComsUtil.JOB_STATUS_WIP);
			job.setUpdated(new Date());
			jobRepo.save(job);
			System.out.println("CompanyId " + user.getCompany().getId() + " Job "+jobId + ": Marked started");
						
			String inputFileName= job.getInputFile();			
			System.out.println("CompanyId " + user.getCompany().getId() + " Job "+jobId + ": Sending document for processing :" + inputFileName);			
			AzureDocumentIntelligenceClient documentClient = new AzureDocumentIntelligenceClient(DATA_EXTRACT_SERVICE_URL, DATA_EXTRACT_API_KEY);
			
			
			JobConfiguration jobConfig = jobConfigRepo.findByTypeAndCompanyIdAndDocumentType(ComsUtil.JOB_TYPE_DATA_EXTRACTION, user.getCompany().getId(), job.getType());
			String processorClass = jobConfig.getProcessor(); //"com.sd.service.processor.Company1Processor";
			System.out.println("CompanyId " + user.getCompany().getId() + " Job "+jobId + ": Configuration found, processor is "+ processorClass);
			
			ICompanyProcessor processor = (ICompanyProcessor)Class.forName(processorClass).newInstance();
			String[] fields = fields = processor.getFields();
			System.out.println("CompanyId " + user.getCompany().getId() + " Job "+jobId + ": Processor "+ processorClass+" instantiated");
			
			String validationMsg = storageService.validateInputDocument(user, jobConfig, job);
			System.out.println("CompanyId " + user.getCompany().getId() + " Job "+jobId + ": Input validated - "+ validationMsg);
			
							
			if(validationMsg != null && validationMsg.equalsIgnoreCase("SUCCESS")) {
				
				String[] inputFiles = storageService.getInputFiles(user, job);
				String inputFolder = storageService.getInputFolder(user);
				
				if(inputFiles.length > 1) {
					//There are more than one input files, it must have been a zip file that is unzipped
					inputFolder = inputFolder + storageService.getSeparator() + job.getId()+ storageService.getSeparator();
				}
				
				System.out.println("CompanyId " + user.getCompany().getId() + " Job "+jobId + ": No of input files in this job is "+ inputFiles.length+ " in folder "+ inputFolder);
				
				String resultUrls[] = new String[inputFiles.length];
				AzureDocumentIntelligenceClient documentService = new AzureDocumentIntelligenceClient(DATA_EXTRACT_SERVICE_URL, DATA_EXTRACT_API_KEY);
				
				//String dummy = "https://westus.api.cognitive.microsoft.com/formrecognizer/documentModels/prebuilt-layout/analyzeResults/2272beb4-f5c3-44f6-a206-9dfd26cbb51c?api-version=2023-07-31";
				//String[] resultDummyUrls = new String[]{dummy, dummy, dummy};
				
				for (int i = 0; i < inputFiles.length; i++) {
					//Submit the document for processing and retrieve resulting Url location					
					resultUrls[i] = documentService.submitDocument(inputFolder + inputFiles[i]);
					//resultUrls[i] = resultDummyUrls[i];
					System.out.println("CompanyId " + user.getCompany().getId() + " Job "+jobId + ": Input document submiited "+ inputFiles[i] + ". ResultUrl: " +resultUrls[i]);		
					
					JobDetail jd = new JobDetail(inputFiles[i], ComsUtil.JOB_STATUS_WIP, "Submitted", new Date(), null);
					jd.setJob(job);
					job.addJobdetail(jd);					
				}

				// JobDetail records are added to the job. Save them
				jobRepo.save(job);
				System.out.println("CompanyId " + user.getCompany().getId() + " Job "+jobId + ": JobDetail records updated ");		
				
				BillingEvent billingEvent = new BillingEvent(user.getCompany().getId(), ComsUtil.JOB_TYPE_DATA_EXTRACTION, job.getId(), Double.valueOf(inputFiles.length), jobConfig.getPricePerUnit());
				billingEventRepo.save(billingEvent);
				System.out.println("CompanyId " + user.getCompany().getId() + " Job "+jobId + ": BillingEvent recorded, serviceType="+ComsUtil.JOB_TYPE_DATA_EXTRACTION+", count="+inputFiles.length);		
				
				//Document submitted for processing, Good !! Lets clean up the files 
				//Only if it was zipped, let the original input file be there)
				storageService.cleanUp(user, jobConfig, job);
				System.out.println("CompanyId " + user.getCompany().getId() + " Job "+jobId + ": Clean up done");		
				
				Thread.sleep(15000L);
				
				//Collect results
				List<ComsOutputRecordDto> results = new ArrayList<>();
				for (int j = 0; j < resultUrls.length; j++) {

					// Retrieve document processing result 
					//To-Do: Handle 404 error, partial processing success, transformation, input reference in output
					
					DocumentAnalysisResult  result = documentClient.getProcessingResult(resultUrls[j]);			
					System.out.println("CompanyId " + user.getCompany().getId() + " Job "+jobId + ": Document processing results retrieved");
					
					ComsOutputRecordDto record = processor.transformResult(result);	
					results.add(record);
					
					//Update corresponding JobDetail record
					Object[] jDs = job.getJobDetails().toArray();
					JobDetail thisJD = null;
					for (int k = 0; k < jDs.length; k++) {
						thisJD = (JobDetail)jDs[k];
						if(thisJD.getInputFile().equals(inputFiles[j])) {
							thisJD.setUpdated(new Date());
							thisJD.setStatus(ComsUtil.JOB_STATUS_SUCCESS);
							thisJD.setMessage(ComsUtil.JOB_STATUS_SUCCESS);
						}
						System.out.println("Updated JobDetails for "+ inputFiles[j] + ", JobDetail Id = "+ thisJD.getId());
					}
					
				}
				System.out.println("CompanyId " + user.getCompany().getId() + " Job "+jobId + ": Results result retrieved and transformed. No of output records = "+results.size());															
				
				// JobDetail records are updated. Save them
				jobRepo.save(job);
				System.out.println("CompanyId " + user.getCompany().getId() + " Job "+jobId + ": JobDetail records updated ");	
				
				
				//Generate output file
				String outputFileName = "Output_"+jobId+".csv";
				String data = storageService.transform(fields, results);
				storageService.createFile(user, outputFileName, data);
				System.out.println("CompanyId " + user.getCompany().getId() + " Job "+jobId + ": Output file written - "+outputFileName);
				
				//Update job completion
				job.setStatus(ComsUtil.JOB_STATUS_SUCCESS);
				job.setUpdated(new Date());
				job.setOutputFile(outputFileName);
				job.setMessage("Successfully processed");
				jobRepo.save(job);
			
			}else {
			
				//Update job completion
				job.setStatus(ComsUtil.JOB_STATUS_ERROR);
				job.setUpdated(new Date());
				job.setMessage(validationMsg);
				jobRepo.save(job);
			}
			
			System.out.println("CompanyId " + user.getCompany().getId() + " Job "+jobId + ": Job processing completed at "+ System.currentTimeMillis() + " (" + (new Date()) + ")");		
						
		}catch(Exception e){
			
			//Record error and Update job completion with status=ERROR
			try {
				System.out.println("CompanyId " + user.getCompany().getId() + " Error at "+ System.currentTimeMillis() + " (" + (new Date()) + ") : "+ e.getMessage());					
				e.printStackTrace();
				
//				StringWriter sw = new StringWriter();
//				PrintWriter pw = new PrintWriter(sw);
//				e.printStackTrace(pw);				
				
				job.setStatus(ComsUtil.JOB_STATUS_ERROR);
				job.setUpdated(new Date());
				job.setMessage(e.getMessage());
				jobRepo.save(job);
				
			}catch (Exception ex) {
				System.out.println("CompanyId " + user.getCompany().getId() + " FATAL Error at "+ System.currentTimeMillis() + " (" + (new Date()) + ") : "+ ex.getMessage());									
				ex.printStackTrace();
			}
		}		
		return null;
	}
}
