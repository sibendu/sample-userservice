package com.sd.controller;

import org.springframework.web.bind.annotation.RestController;

import com.sd.dto.CompanyUpdateRequestDto;
import com.sd.dto.DashboardDto;
import com.sd.model.*;
import com.sd.model.Module;
import com.sd.service.AdminService;
import com.sd.service.StorageService;
import com.sd.util.ComsUtil;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class UserController {	
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	CompanyRepository companyRepo;
	
	@Autowired
	JobConfigurationRepository jobConfigRepo;
	
	@Autowired
	PlatformEventRepository platformEventRepo;
	
	@Autowired
	ModuleRepository moduleRepo;
	
	@Autowired
	StorageService storageService;
	
	
	@Autowired
	AdminService adminService;
	
	@Value("${ALLOCATED_STORAGE}")
	private String ALLOCATED_STORAGE;
	
	private static Logger logger = LogManager.getLogger(UserController.class);
	
	@GetMapping("/user")
	public List<?> getAllUsers (@RequestParam(required = false) String type){
		List<User> users = new ArrayList<>();
		List<User> allUsers = userRepo.findAll();
		
		if(type != null && type.trim().equalsIgnoreCase(ComsUtil.USER_TYPE_COMPANY)) {			
			for (User temp : allUsers) {
				if(temp.getCompany().getType().equals(ComsUtil.USER_TYPE_COMPANY)) {
					users.add(temp);
				}
			}		
		}else if(type != null && type.trim().equalsIgnoreCase(ComsUtil.USER_TYPE_INDIVIDUAL)){
			for (User temp : allUsers) {
				if(temp.getCompany().getType().equals(ComsUtil.USER_TYPE_INDIVIDUAL)) {
					users.add(temp);
				}
			}					
		}else {
			users = allUsers;
		}
		return users;
	}
	
	
	@GetMapping("/company/{id}/user")
	public List<User> getAllUsersForCompanyId (@PathVariable Long id){
		Company comp = companyRepo.getById(id);
		List<User> usersOfCompany = new ArrayList<>();
		Set<User> users= comp.getUsers();
		users.forEach(u -> {
			usersOfCompany.add(u);
		});
		return usersOfCompany;
	}
	
	
	@GetMapping("/user/{id}")
	public User geUser (@PathVariable Long id){
		System.out.println("Getting user details: "+id);
		return userRepo.findById(id).get();
	}
	
	@GetMapping("/company")
	public List<Company> getCompanies (){
		List<Company> companies = companyRepo.findAll();
		return companies;
	}
	
	@GetMapping("/company/{id}")
	public Company getCompany (@PathVariable Long id){
		return companyRepo.findById(id).get();
	}
	
	@GetMapping("/jobconfig/{companyId}")
	public List<JobConfiguration> getJobConfgurations(@PathVariable Long companyId){
		return jobConfigRepo.findByCompanyId(companyId);
	}
	
	@GetMapping("/module/{companyId}")
	public Set<Module> getModules(@PathVariable Long companyId){
		return companyRepo.getById(companyId).getModules();
	}
	
	@GetMapping("/platformevent")
	public List<PlatformEvent> getPlatformEvents(){
		return platformEventRepo.findAll();
	}
	
	@PostMapping("/auth")
	public User auth(@RequestBody User u) {		
		User user = userRepo.findByUsername(u.getUsername());
		String msg = "";
		if(user != null && user.getCompany() != null) {
			
			if(user.getPassword().equals(u.getPassword())) {
				PlatformEvent pe = new PlatformEvent(user.getCompany().getId(), user.getId(), user.getUsername(), ComsUtil.EVENT_LOGIN, null);
				platformEventRepo.save(pe); 
				msg = "CompanyId " + user.getCompany().getId() + " User "+user.getUsername() + " (id="+user.getId()+") logging in";
			}else {
				user = u;
				msg = "Invalid password";
				user.setStatus(msg);
				u.setStatus("Invalid password");
			}
		}else {
			user = u;
			msg = "User record does not exists for "+u.getUsername();
			user.setStatus(msg);
			System.out.println(msg);
		}
		
		System.out.println(msg);
		logger.info(msg);
		
		return user;
	}
	
	@PostMapping("/setting")
	public String getSetting(@RequestBody User u) {
		System.out.println("Check Setting - "+u.getUsername()+ " of CompanyId "+u.getCompany());
		User user = userRepo.findByUsername(u.getUsername());
		String inputFolder = storageService.getInputFolder(user);
		String outputFolder = storageService.getOutputFolder(user);
		//System.out.println(inputFolder + " - " + outputFolder);
		long size = storageService.getFolderSize(new File(inputFolder)) + storageService.getFolderSize(new File(outputFolder));
		
		
	    String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
	    int unitIndex = (int) (Math.log10(size) / 3);
	    double unitValue = 1 << (unitIndex * 10);
	    
	    //System.out.println(size +  "  " + unitValue+ "  "+ unitIndex);	    
	    String readableSize = "0";
	    
	    if(size > 0) {
	    	readableSize = new DecimalFormat("#,##0.#")
	                                .format(size / unitValue) + " " 
	                                + units[unitIndex];
		}
		String result = readableSize +" of " + ALLOCATED_STORAGE;
		//System.out.println(result);
		return result;
	}
	
	@PostMapping("/dashboard")
	public DashboardDto getDashoard(@RequestBody User u) throws Exception{
		System.out.println("Dashboard - "+u.getUsername()+ " of CompanyId "+u.getCompany());
		User user = userRepo.findByUsername(u.getUsername());
		
		DashboardDto dto = adminService.calculateUsage(user.getCompany().getId());
		dto.setUser(user);
		
		String stoageSetting = getSetting(u);
		dto.setStorageSetting(stoageSetting);
		
		System.out.println("Dashboard - "+dto.getLabels());
		return dto;
	}
	
	@PostMapping("/company")
	public Company saveCompany(@RequestBody CompanyUpdateRequestDto dto) throws Exception {
		
		Company company = companyRepo.findById(dto.getCompanyId()).get();
		
		Set<Module> targetModules = dto.getTargetModules();
		if(targetModules != null && targetModules.size() > 0) {
			company.setModules(targetModules);
			company = companyRepo.save(company);
			System.out.println("Updated module acceess for companyId="+dto.getCompanyId());
		}
		
		JobConfiguration jc = dto.getJobConfig();
		if(jc != null) {
			jc.setCompanyId(dto.getCompanyId());
			jc = jobConfigRepo.save(jc);
			System.out.println("Added job configuration for companyId="+dto.getCompanyId());
		}
		
		return company;
	}
	
	@DeleteMapping("/jobconfig/{id}")
	public void deleteJobConfiguration(@PathVariable Long id) throws Exception {
		jobConfigRepo.deleteById(id);
	}	
}
