package com.sd.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestBody;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.log.SysoCounter;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.clipper.Path;
import com.itextpdf.text.pdf.parser.clipper.Paths;
import com.sd.dto.DashboardDto;
import com.sd.dto.InvoiceRequestDto;
import com.sd.model.BillingEvent;
import com.sd.model.BillingEventRepository;
import com.sd.model.Company;
import com.sd.model.CompanyRepository;
import com.sd.model.Invoice;
import com.sd.model.InvoiceLineItem;
import com.sd.model.InvoiceRepository;
import com.sd.model.JobConfiguration;
import com.sd.model.JobConfigurationRepository;
import com.sd.model.User;
import com.sd.util.ComsUtil;

@Configuration
public class AdminService {

	@Value("${TAX_PERCENT}")
	private String TAX_PERCENT;
	
	@Autowired
	BillingEventRepository billingEventRepo;

	@Autowired
	CompanyRepository companyRepo;

	@Autowired
	InvoiceRepository invoiceRepo;

	@Autowired
	JobConfigurationRepository jobConfigRepo;

	@Autowired
	StorageService storageService;

	public Invoice generateInvoice(long companyId, Date startDate, Date endDate) throws Exception {

		System.out.println("Generating Invoice: " + companyId + " - " + startDate + " - " + endDate);

		double taxPercent = Double.valueOf(TAX_PERCENT);

		Company company = companyRepo.findById(companyId).get();
		
		List<BillingEvent> eventsBilled = new ArrayList<>();

		List<JobConfiguration> jobConfigs = jobConfigRepo.findByCompanyId(companyId);
		List<String> jobTypes = new ArrayList<>();

		String lastType = null;
		for (JobConfiguration config : jobConfigs) {
			if (lastType != null && config.getType().equalsIgnoreCase(lastType)) {
				// Skip it, type already added to list
			} else {
				lastType = config.getType();
				jobTypes.add(lastType);
			}
		}

		Invoice invoice = new Invoice(companyId, startDate, endDate, Double.valueOf(0), Double.valueOf(0),
				Double.valueOf(0), ComsUtil.INVOICE_STATUS_NEW);

		// Now iterate over all jobTypes this company has, and add InvoiceLineItems
		for (String type : jobTypes) {

			System.out.println("LineItem for " + type);
			// Find all events for this company and the jobType, which are still not billed
			List<BillingEvent> events = billingEventRepo
					.findAllByCompanyIdAndServiceTypeAndBillingStatusOrderByCreatedAsc(companyId, type,
							ComsUtil.FLAG_NO);
			System.out.println("Events Found = " + events.size());
			BillingEvent e = null;
			double totalUsage = 0;
			Double pricePerUnit = null;
			double amount = 0;
			Double tax = null;
			Double total = null;

			if (events != null && events.size() > 0) {
				for (Iterator iterator = events.iterator(); iterator.hasNext();) {
					e = (BillingEvent) iterator.next();

					if (e.getCreated().after(startDate) && e.getCreated().before(endDate)) {
						totalUsage = totalUsage + e.getBillingCount().doubleValue();
						pricePerUnit = e.getPricePerUnit();
						amount = amount + e.getBillingCount().doubleValue() * e.getPricePerUnit().doubleValue();

						// Add this event to list for updating status at the end
						e.setBillingStatus(ComsUtil.FLAG_YES);
						eventsBilled.add(e);
					}
				}

				tax = Double.valueOf(amount * taxPercent);
				total = Double.valueOf(amount + tax.doubleValue());

				InvoiceLineItem line = new InvoiceLineItem(type, Double.valueOf(totalUsage), pricePerUnit,
						Double.valueOf(amount), tax, total);
				line.setInvoice(invoice);

				invoice.addLineItem(line);
				System.out.println("LineItem added for " + type + ", total = " + total);

			} else {
				System.out.println("No unbilled events found during this period for " + type);
			}

		}

		
		invoice = invoiceRepo.save(invoice);

		billingEventRepo.saveAll(eventsBilled);
		
		String invoiceFile = storageService.generateInvoice(company, invoice);
		System.out.println(invoiceFile + " generated");
		invoice.setFile(invoiceFile);
		
		invoice = invoiceRepo.save(invoice);
		
		return invoice;
	}

	public DashboardDto calculateUsage(long companyId) throws Exception {

		DashboardDto dto = new DashboardDto();
		double billedYTD = 0;
		double unbilledYTD = 0;
		double thisEventCost = 0;

		List<BillingEvent> billEvents = billingEventRepo.findAllByCompanyIdOrderByCreatedAsc(companyId);
		BillingEvent event = null;
		SimpleDateFormat sdf = new SimpleDateFormat("MMM");
		String thisMonth, lastMonth = "";
		long thisMonthUsage = 0;
		List<String> months = new ArrayList<>();
		List<Long> noOfJobs = new ArrayList<>();

		for (Iterator iterator = billEvents.iterator(); iterator.hasNext();) {
			event = (BillingEvent) iterator.next();
			thisEventCost = event.getBillingCount().doubleValue() * event.getPricePerUnit().doubleValue();

			thisMonth = sdf.format(event.getCreated());
			if (lastMonth != null && !thisMonth.equalsIgnoreCase(lastMonth)) {
				// its first event of a new month

				// add calculated values till last month
				months.add(lastMonth);
				noOfJobs.add(Long.valueOf(thisMonthUsage));

				// Reset and Start calculatingfor new month
				thisMonthUsage = 0;
			}

			thisMonthUsage = thisMonthUsage + 1;

			if (event.getBillingStatus().equalsIgnoreCase(ComsUtil.FLAG_YES)) {
				billedYTD = billedYTD + thisEventCost;
			} else if (event.getBillingStatus().equalsIgnoreCase(ComsUtil.FLAG_NO)) {
				unbilledYTD = unbilledYTD + thisEventCost;
			} else {
				throw new Exception();
			}

			// Assign variable lastMonth for checking next billing event
			lastMonth = thisMonth;
		}

		// add calculated values for final month
		months.add(lastMonth);
		noOfJobs.add(Long.valueOf(thisMonthUsage));

		String[] allMonths = new String[months.size()];
		long[] countJobs = new long[months.size()];
		for (int k = 0; k < months.size(); k++) {
			allMonths[k] = (String) months.get(k);
			countJobs[k] = noOfJobs.get(k).longValue();
		}

		dto.setLabels(allMonths);
		dto.setNoOfJobs(countJobs);

		dto.setBilledYTD(billedYTD);
		dto.setUnbilledYTD(unbilledYTD);

		return dto;
	}

}
