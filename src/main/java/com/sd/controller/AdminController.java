package com.sd.controller;

import org.springframework.web.bind.annotation.RestController;

import com.sd.dto.InvoiceRequestDto;
import com.sd.model.*;
import com.sd.model.Module;
import com.sd.service.AdminService;
import com.sd.service.StorageService;
import com.sd.util.ComsUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class AdminController {	
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	CompanyRepository companyRepo;
	
	@Autowired
	ModuleRepository moduleRepo;
	
	@Autowired
	JobRepository jobRepo;

	@Autowired
	JobConfigurationRepository jobConfigRepo;
	
	@Autowired
	InvoiceRepository invoiceRepo;
	
	@Autowired
	StorageService storageService;
	
	@Autowired
	AdminService adminService;
	
	@Autowired
	BillingEventRepository billingEventRepo;
	
	@Autowired
	PlatformEventRepository platformEventRepo;

	@GetMapping("/module")
	public List<Module> getModules(){
		return moduleRepo.findAll();
	}
	
	
	@GetMapping("/billingevent")
	public List<BillingEvent> getBillingEvents(){
		return billingEventRepo.findAllByOrderByCreatedDesc();
	}
	
	@PostMapping("/invoice/generate")
	public Invoice generateInvoice(@RequestBody InvoiceRequestDto dto) throws Exception {
		return adminService.generateInvoice(dto.getCompanyId(), dto.getStartDate(), dto.getEndDate()); 		
	}
	
	@PostMapping("/user")
	public User saveUser(@RequestBody User u) throws Exception {
		
		User currUser = null;
		String message = null;
		System.out.println("saveUser(): "+u.getId());
		if(u.getId() == 0) {
			System.out.println("Creating a new user");
			
			currUser = new User(u.getFirstName(), u.getLastName(), u.getPhone(), u.getUsername(), u.getPassword());			
			
			Company currCompany = null;
			if(u.getCompany().getId() == 0) {
				
				// Create New Company & Add this as RootUser
				currCompany = new Company(u.getCompany().getName(),ComsUtil.USER_TYPE_INDIVIDUAL, u.getUsername() , u.getCompany().getAddress(), u.getCompany().getCountry());
				
				if(u.getCompany().getType().equalsIgnoreCase(ComsUtil.USER_TYPE_COMPANY)) {
					currCompany.setType(ComsUtil.USER_TYPE_COMPANY); 
				}
				
				currCompany.addModule(moduleRepo.findByName(ComsUtil.MODULE_DASHBOARD));
				currCompany.addModule(moduleRepo.findByName(ComsUtil.MODULE_DATA_EXTRACTION));
				currCompany.addModule(moduleRepo.findByName(ComsUtil.MODULE_SETTINGS));
				
				currCompany.addUser(currUser);
				currUser.setCompany(currCompany);
				
				currCompany = companyRepo.save(currCompany);
				
				//Do Setup activities for tis Company. this is crucial ! 
				setupCompany(currCompany.getId());
				
				//Add Generic DATA_EXTRACT
				JobConfiguration jc = new JobConfiguration(ComsUtil.JOB_TYPE_DATA_EXTRACTION,currCompany.getId(), ComsUtil.DOCUMENT_TYPE_GENERIC, "jpeg", "com.sd.service.processor.GenericProcessor", Double.parseDouble("0.65"));					
				jc = jobConfigRepo.save(jc);
				
				message = "CompanyId " + currCompany.getId() + " User "+currUser.getUsername() + ": New Company & User created";
				
			}else {

				// Add this user to an existing Company
				currCompany = companyRepo.findById(u.getCompany().getId()).get();
				
				currCompany.addUser(currUser);
				
				currUser = userRepo.save(currUser);
				
				message = "CompanyId " + u.getCompany().getId() + ":  New user added - " +currUser.getId() + " - "+ currUser.getUsername();
			}			
			
		}else {
			currUser = userRepo.getById(u.getId()); 
			
			currUser.setFirstName(u.getFirstName());
			currUser.setLastName(u.getLastName());
			currUser.setPhone(u.getPhone());
			
			currUser.getCompany().setName(u.getCompany().getName());
			currUser.getCompany().setAddress(u.getCompany().getAddress());
			currUser.getCompany().setCountry(u.getCompany().getCountry());
			
			currUser = userRepo.save(currUser);
			
			message = "CompanyId " + currUser.getCompany().getId() + " User "+currUser.getUsername() + ": Updated successfully";
		}
		
		System.out.println(message);		
		return currUser;
	}

	//@GetMapping("/company/{id}/setup")
	public void setupCompany (@PathVariable Long id) throws Exception{		
		
		//Step 1. Setup storage 
		Company company = companyRepo.findById(id.longValue()).get();
		//List<User> users = userRepo.findByCompanyId(id);
		Set<User> users = company.getUsers();
		User user = null;
		for (Iterator iterator = users.iterator(); iterator.hasNext();) {
			user = (User) iterator.next();
		}
		storageService.setupCompanyStorage(user);
		
		//Step 2. Setup company entitlements for jobs/ service offerings in the platform
		JobConfiguration config1 = new JobConfiguration(ComsUtil.JOB_TYPE_DATA_EXTRACTION, company.getId(), "Invoice", "jpeg", "com.sd.service.processor.Company1Processor", Double.valueOf(1));
		JobConfiguration config2 = new JobConfiguration(ComsUtil.JOB_TYPE_DATA_EXTRACTION, company.getId(), "Memo", "jpeg", "com.sd.service.processor.Company1Processor",Double.valueOf(0.5));
		jobConfigRepo.save(config1);
		jobConfigRepo.save(config2);		
	}

	
	@DeleteMapping("/user/{id}")
	public void deleteUser (@PathVariable Long id, @RequestParam(defaultValue="false") boolean permanentlyRemove) throws Exception{
		User user = userRepo.getById(id);
		Company comp = user.getCompany();
		
		if(user.getUsername().equalsIgnoreCase(comp.getRootuser())) {
			//Removing rootUser
			
			if(permanentlyRemove) {
				//Permamnently deleting rootUser; 
				storageService.cleanupCompanyStorage(user);
				
				companyRepo.delete(comp);
				
				System.out.println("CompanyId " + comp.getId() + " UserId "+user.getId() + ": Permanently Removed User and Company Setup for userName="+ user.getUsername());				
			}else {
				//Deactivating rootUser, so Company status=DELETED
				user.setStatus(ComsUtil.STATUS_DELETED);
				userRepo.save(user);
				
				comp.setStatus(ComsUtil.STATUS_DELETED);
				companyRepo.save(comp);
				
				System.out.println("CompanyId " + comp.getId() + " UserId "+user.getId() + ": Deactivated User and Company for userName="+ user.getUsername());				
			}
		}else {
			//Not a root user
			if(permanentlyRemove) {
				
				userRepo.delete(user);
				System.out.println("CompanyId " + comp.getId() + " UserId "+user.getId() + ": Permanently Removed User. userName="+ user.getUsername());				
			
			}else {
				
				user.setStatus(ComsUtil.STATUS_DELETED);
				userRepo.save(user);
				
				System.out.println("CompanyId " + comp.getId() + " UserId "+user.getId() + ": Deactivated User. userName="+ user.getUsername());				
				
			}
		}
	}
	
	@GetMapping("/clean")
	public String clean(){
		
		platformEventRepo.deleteAll();
		billingEventRepo.deleteAll();
		invoiceRepo.deleteAll();
		jobRepo.deleteAll();
		String message = "All transactional data cleaned - billingevent, job";
		System.out.println(message);
		
		userRepo.deleteAll();
		companyRepo.deleteAll();
		moduleRepo.deleteAll();
		jobConfigRepo.deleteAll();
		
		message = "All master data cleaned - user, company, module, jobConfig";
		System.out.println(message);
		return message;
	}
	
	
	@GetMapping("/seed")
	public String seed() throws Exception{
		
		storageService.setupPlatformStorage();
		
		Module m1 = new Module(ComsUtil.MODULE_DASHBOARD);
		Module m2 = new Module(ComsUtil.MODULE_DATA_EXTRACTION);
		Module m3 = new Module(ComsUtil.MODULE_SETTINGS);
		Module m4 = new Module(ComsUtil.MODULE_ADMINISTRATION);
		  
		moduleRepo.save(m1);
		moduleRepo.save(m2);
		moduleRepo.save(m3);
		moduleRepo.save(m4);
		System.out.println("Populated module");
		
		
		Company comp1= new Company("Platform Administrators",ComsUtil.USER_TYPE_COMPANY, "admin@gmail.com", "---", "INDIA");
		User user1 = new User("Sibendu", "Das", "9898989898", "admin@gmail.com", "pass");
		
		comp1.addUser(user1);
		user1.setCompany(comp1);
		
		comp1.addModule(m1);
		comp1.addModule(m2);
		comp1.addModule(m3);
		comp1.addModule(m4);

		companyRepo.save(comp1);
				
		setupCompany(comp1.getId());

		String message = "Data seeded - module, admin user & company, jobConfig";
		System.out.println(message);
		return message;
	}
	
	
	@PostMapping("/invoice")
	public List<Invoice> getInvoice(@RequestBody User u) throws Exception {
		if(u.getCompany().getName().equals("Platform Administrators")) {
			System.out.println("All invoices for admin");
			return invoiceRepo.findAll();	
		}else {
			return invoiceRepo.findByCompanyIdOrderByCreatedDesc(u.getCompany().getId());	
		}
	}	
	
}
