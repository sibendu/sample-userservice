package com.sd.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.sd.dto.ComsOutputRecordDto;
import com.sd.dto.document.ComsOutputField;
import com.sd.model.Company;
import com.sd.model.Invoice;
import com.sd.model.InvoiceLineItem;
import com.sd.model.Job;
import com.sd.model.JobConfiguration;
import com.sd.model.User;
import com.sd.util.ComsUtil;

@Configuration
public class StorageService {

	@Value("${DATA_EXTRACT_ROOT_FOLDER}")
	private String ROOT_FOLDER;

	@Value("${DATA_EXTRACT_SEPARATOR}")
	private String SEPARATOR;

	@Value("${RECORD_SEPARATOR}")
	private String RECORD_SEPARATOR;

	@Value("${FIELD_SEPARATOR}")
	private String FIELD_SEPARATOR;

	@Value("${INPUT_FOLDER}")
	private String INPUT_FOLDER;

	@Value("${OUTPUT_FOLDER}")
	private String OUTPUT_FOLDER;
	
	@Value("${INVOICE_FOLDER}")
	private String INVOICE_FOLDER;
	
	@Value("${INVOICE_PASSWORD}")
	private String INVOICE_PASSWORD;

	public String getInputFolder(User user) {
		String folder = ROOT_FOLDER + SEPARATOR + INPUT_FOLDER + SEPARATOR + user.getCompany().getType() + SEPARATOR
				+ user.getCompany().getId() + SEPARATOR;
		return folder;
	}

	public String getOutputFolder(User user) {
		String folder = ROOT_FOLDER + SEPARATOR + OUTPUT_FOLDER + SEPARATOR + user.getCompany().getType() + SEPARATOR
				+ user.getCompany().getId() + SEPARATOR;
		return folder;
	}

	public String getInvoiceFolder(Company company) {
		String folder = ROOT_FOLDER + SEPARATOR + INVOICE_FOLDER + SEPARATOR + company.getType() + SEPARATOR + company.getId() + SEPARATOR;
		return folder;
	}
	
	public void setupPlatformStorage() {
		
		String folder =  ROOT_FOLDER + SEPARATOR + INPUT_FOLDER;
		System.out.println("Setup storage : " + folder+" : "+ new File(folder).mkdir());
		
		folder =  ROOT_FOLDER + SEPARATOR + INPUT_FOLDER + SEPARATOR + ComsUtil.USER_TYPE_COMPANY;
		System.out.println("Setup storage : " + folder+" : "+ new File(folder).mkdir());
		
		folder =  ROOT_FOLDER + SEPARATOR + INPUT_FOLDER + SEPARATOR + ComsUtil.USER_TYPE_INDIVIDUAL;
		System.out.println("Setup storage : " + folder+" : "+ new File(folder).mkdir());
		
		
		folder =  ROOT_FOLDER + SEPARATOR + OUTPUT_FOLDER;
		System.out.println("Setup storage : " + folder+" : "+ new File(folder).mkdir());
		
		folder =  ROOT_FOLDER + SEPARATOR + OUTPUT_FOLDER + SEPARATOR + ComsUtil.USER_TYPE_COMPANY;
		System.out.println("Setup storage : " + folder+" : "+ new File(folder).mkdir());
		
		folder =  ROOT_FOLDER + SEPARATOR + OUTPUT_FOLDER + SEPARATOR + ComsUtil.USER_TYPE_INDIVIDUAL;
		System.out.println("Setup storage : " + folder+" : "+ new File(folder).mkdir());
		
		
		folder =  ROOT_FOLDER + SEPARATOR + INVOICE_FOLDER;
		System.out.println("Setup storage :" + folder+" : "+ new File(folder).mkdir());
		
		folder =  ROOT_FOLDER + SEPARATOR + INVOICE_FOLDER + SEPARATOR + ComsUtil.USER_TYPE_COMPANY;
		System.out.println("Setup storage :" + folder+" : "+ new File(folder).mkdir());
		
		folder =  ROOT_FOLDER + SEPARATOR + INVOICE_FOLDER + SEPARATOR + ComsUtil.USER_TYPE_INDIVIDUAL;
		System.out.println("Setup storage :" + folder+" : "+ new File(folder).mkdir());

	}
	
	public void setupCompanyStorage(User user) {

		String inputFolder = ROOT_FOLDER + SEPARATOR + INPUT_FOLDER + SEPARATOR + user.getCompany().getType();
		File inputDir = new File( inputFolder, user.getCompany().getId()+"");
		if(!inputDir.exists()) {
			System.out.println(inputDir.mkdir());
			System.out.println("Company " + user.getCompany().getId() + " Input folder created : " + inputDir.getAbsolutePath() + " : "+ inputDir.isDirectory());
		}
		
		String outputFolder = ROOT_FOLDER + SEPARATOR + OUTPUT_FOLDER + SEPARATOR + user.getCompany().getType();
		File outputDir = new File( outputFolder, user.getCompany().getId()+"");
		if(!outputDir.exists()) {
			System.out.println(outputDir.mkdir());
			System.out.println("Company " + user.getCompany().getId() + " Output folder created : " + outputDir.getAbsolutePath()+ " : "+ outputDir.isDirectory());
		}
		
		String invoiceFolder = ROOT_FOLDER + SEPARATOR + INVOICE_FOLDER + SEPARATOR + user.getCompany().getType();
		File invoiceDir = new File( invoiceFolder, user.getCompany().getId()+"");
		if(!invoiceDir.exists()) {
			System.out.println(invoiceDir.mkdir());
			System.out.println("Company " + user.getCompany().getId() + " Invoice folder created : " + invoiceDir.getAbsolutePath()+ " : "+ invoiceDir.isDirectory());
		}
	}

	public void cleanupCompanyStorage(User user) throws Exception{
		String inputFolder = getInputFolder(user);
		String outputFolder = getOutputFolder(user);
		deleteFolder(inputFolder);
		deleteFolder(outputFolder);		
		System.out.println("Company " + user.getCompany().getId() + ": Cleaned up storage");
	}
	
	public void deleteFolder(String folder) throws Exception {
			File[] files = new File(folder).listFiles();
			for (int i = 0; i < files.length; i++) {
				//System.out.println("Deleted : "+files[i].getName());
				files[i].delete();
			}
			new File(folder).delete();
			//System.out.println("Folder deleted");
	}
	
	public void storeFile(User user, String fileName, MultipartFile file) throws Exception {
		// System.out.println("Uploading files under path: "+ROOT_FOLDER+SEPARATOR);
		String uploadedFile = getInputFolder(user) + fileName;
		FileOutputStream fos = new FileOutputStream(uploadedFile);
		fos.write(file.getBytes());
		fos.close();
	}

	public List<Path> getFiles(User user, long jobId, String ioType, String fileName) throws IOException {

		String strFile = null;
		
		if (ioType.equals("Input")) {
			strFile = getInputFolder(user) + fileName;
		} else {
			strFile = getOutputFolder(user) + fileName;
		}
		return Arrays.asList(Paths.get(strFile));
	}

	public String transform(String[] fields, List<ComsOutputRecordDto> records) throws Exception {
		String data = "";
		String fieldVal = null;
		
		if(fields == null || fields.length == 0) {
			fields = new String[records.get(0).getFields().size()];
			
			List<ComsOutputField> allFields = records.get(0).getFields();
			int k = 0;
			for (ComsOutputField f : allFields) {
				fields[k++] = f.getName();
				//System.out.println(f.getName() + "  -- " +f.getValue());
			}
		}
		
		
		// Write Header line
		for (int i = 0; i < fields.length; i++) {
			data = data + fields[i] + FIELD_SEPARATOR;
		}
		data = data + RECORD_SEPARATOR;

		for (ComsOutputRecordDto rec : records) {
			for (int i = 0; i < fields.length; i++) {
				fieldVal = rec.getField(fields[i]).getValue() == null ? "" : rec.getField(fields[i]).getValue();
				fieldVal = escapeSpecialCharacters(fieldVal);
				data = data + fieldVal + FIELD_SEPARATOR;
			}
			data = data + RECORD_SEPARATOR;
		}
		return data;
	}

	public void createFile(User user, String fileName, String data) throws Exception {
		String outputFile = getOutputFolder(user) + fileName;
		FileOutputStream fos = new FileOutputStream(outputFile);
		// Write the file
		fos.write(data.getBytes());
		fos.close();
		// System.out.println("Created = "+outputFile);
		// file.transferTo(new File(folder));
	}

	public String escapeSpecialCharacters(String fieldVal) {
		String escapedData = fieldVal == null ? "" : fieldVal;
		if (fieldVal != null && !fieldVal.trim().equals("")) {
			escapedData = fieldVal.replaceAll("\\R", " ");
			if (fieldVal.contains(",") || fieldVal.contains("\"") || fieldVal.contains("'")) {
				fieldVal = fieldVal.replace("\"", "\"\"");
				escapedData = "\"" + fieldVal + "\"";
			}
		}
		return escapedData;
	}

	public long getFolderSize(File folder) {
		long length = 0;
		File[] files = folder.listFiles();

		if(files != null) {
			int count = files.length;
	
			// loop for traversing the directory
			for (int i = 0; i < count; i++) {
				if (files[i].isFile()) {
					length += files[i].length();
				} else {
					length += getFolderSize(files[i]);
				}
			}
		}
		return length;
	}

	public String validateInputDocument(User user, JobConfiguration jobConfig, Job job) throws Exception {
		String result = null;
		String destFolder = null;
		String srcFolder = getInputFolder(user);
		String fileName = job.getInputFile();
		String permittedFileTypes = jobConfig.getFileType();
		String ext = FilenameUtils.getExtension(srcFolder + SEPARATOR + fileName);
		if (ext.equals("zip")) {
			System.out.println("");
			destFolder = srcFolder + SEPARATOR + job.getId();
			boolean tempFolder = new File(destFolder).mkdirs();
			System.out.println(
					"CompanyId " + user.getCompany().getId() + " Job " + job.getId() + ": Temp folder created");
			unZipFile(srcFolder, fileName, destFolder);
			System.out.println(
					"CompanyId " + user.getCompany().getId() + " Job " + job.getId() + ": Input file unzipped");

			File[] files = new File(destFolder).listFiles();
			File thisFile = null;
			for (int i = 0; i < files.length; i++) {
				thisFile = files[i];
				ext = FilenameUtils.getExtension(thisFile.getPath() + SEPARATOR + thisFile.getName());
				System.out.println("Ext of file inside zip: " + ext);
				if (ext != null && ext.equals(permittedFileTypes)) {
					result = "success";
				} else {
					result = "Input files invalid. Permitted type(s) are " + permittedFileTypes + ", but input " + ext;
					break;
				}
			}
		} else {
			if (ext != null && ext.equals(permittedFileTypes)) {
				result = "success";
			} else {
				result = "Input files invalid. Permitted type(s) are " + permittedFileTypes + ", but input " + ext;
			}
		}
		return result;
	}
	
	

	public void unZipFile(String srcFolder, String fileName, String destFolder) throws Exception {
		ZipEntry zipentry;
		String file = srcFolder + SEPARATOR + fileName;
		System.out.println(file);
		ZipFile zf = new ZipFile(file);
		Enumeration entries = zf.entries();
		ZipInputStream zipinputstream = new ZipInputStream(new FileInputStream(file));
		int BUFFER_SIZE = 1000;
		while (entries.hasMoreElements()) {
			zipentry = (ZipEntry) entries.nextElement();
			System.out.println(zipentry);

			BufferedInputStream bufIS = new BufferedInputStream(zf.getInputStream(zipentry));
			int currentByte;

			String destFile = destFolder + SEPARATOR + zipentry.getName();

			// buffer for writing file
			byte data[] = new byte[BUFFER_SIZE];

			// write the current file to disk
			FileOutputStream fOS = new FileOutputStream(destFile);
			BufferedOutputStream bufOS = new BufferedOutputStream(fOS, BUFFER_SIZE);

			while ((currentByte = bufIS.read(data, 0, BUFFER_SIZE)) != -1) {
				bufOS.write(data, 0, currentByte);
			}

			// close BufferedOutputStream
			bufOS.flush();
			bufOS.close();
		}
	}
	
	public String getSeparator() {
		return SEPARATOR; 
	}
	
	public String[] getInputFiles(User user, Job job) {
		String[] fileNames = null;
		String srcFolder = getInputFolder(user);
		String fileName = job.getInputFile();
		String ext = FilenameUtils.getExtension(srcFolder + SEPARATOR + fileName);
		if(ext.equals("zip")) {
			srcFolder = srcFolder + SEPARATOR + job.getId();
			File[] files = new File(srcFolder).listFiles();
			fileNames = new String[files.length];
			for (int i = 0; i < files.length; i++) {
				fileNames[i] = files[i].getName();
			}	
		}else {
			fileNames = new String[] { fileName};
		}
		return fileNames;
	}
	
	public void cleanUp(User user, JobConfiguration jobConfig, Job job) throws Exception {
		String srcFolder = getInputFolder(user);
		String fileName = job.getInputFile();
		String ext = FilenameUtils.getExtension(srcFolder + SEPARATOR + fileName);
		//System.out.println(ext);
		if(ext.equals("zip")) {
			srcFolder = srcFolder + SEPARATOR + job.getId();
			deleteFolder(srcFolder);
		}
	}

	public String generateInvoice(Company company, Invoice invoice) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyyyy");
		String today = sdf.format(new Date());
		
		String invoiceFolder = getInvoiceFolder(company);
		
		String invoiceFileName = "invoice_"+ company.getId() + "_" + invoice.getId()+"_"+today+".pdf";
		String file = invoiceFolder + invoiceFileName;
		//String encFile = invoiceFolder + invoiceFileName;

		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));

		document.open();

//		Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
//		Chunk chunk = new Chunk("Hello World", font);
//
//		document.add(chunk);

		PdfPTable tabInvoice = new PdfPTable(3);
		tabInvoice.getDefaultCell().setBorder(0);

		tabInvoice.addCell("Invoice#: " + invoice.getId());
		tabInvoice.addCell("");
		tabInvoice.addCell("");

		tabInvoice.addCell("To: " + company.getName());
		tabInvoice.addCell("");
		tabInvoice.addCell("");
		
		tabInvoice.addCell(company.getAddress()+", "+company.getCountry());
		tabInvoice.addCell("");
		tabInvoice.addCell("");
		
		tabInvoice.addCell("Amount: $" + invoice.getAmount().doubleValue());
		tabInvoice.addCell("Tax: $" + invoice.getTax().doubleValue());
		tabInvoice.addCell("Total Amount: $" + invoice.getTotal().doubleValue());

		tabInvoice.addCell("");
		tabInvoice.addCell("");
		tabInvoice.addCell("");
		tabInvoice.addCell("");
		tabInvoice.addCell("");
		tabInvoice.addCell("");

		document.add(tabInvoice);

		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));

		PdfPTable table = new PdfPTable(6);

		String[] headers = new String[] { "Service", "Price/Unit($)", "Units Billed", "Amount ($)", "Tax($)",
				"Total($)" };
		Stream.of(headers).forEach(columnTitle -> {
			PdfPCell header = new PdfPCell();
			header.setBackgroundColor(BaseColor.LIGHT_GRAY);
			header.setBorderWidth(1);
			header.setPhrase(new Phrase(columnTitle));
			table.addCell(header);
		});

		Set<InvoiceLineItem> lines = invoice.getLineItems();
		InvoiceLineItem line = null;
		for (Iterator iterator = lines.iterator(); iterator.hasNext();) {
			line = (InvoiceLineItem) iterator.next();
			table.addCell(line.getServiceType());
			table.addCell(Double.toString(line.getTotalUsage()));
			table.addCell(Double.toString(line.getPricePerUnit()));
			table.addCell(Double.toString(line.getAmount()));
			table.addCell(Double.toString(line.getTax()));
			table.addCell(Double.toString(line.getTotal()));
		}

		document.add(table);

		document.close();
		writer.close();
		
		/*
		PdfReader pdfReader = new PdfReader(file);
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(encFile));

		pdfStamper.setEncryption("pass".getBytes(), "pass".getBytes(), 0, PdfWriter.ENCRYPTION_AES_256);

		pdfReader.close();
		*/
		
		//pdfStamper.close();

		//System.out.println("Deleting "+file);
		//File temp = new File(file);
		//System.out.println(temp.delete());
		
		System.out.println("CompanyId " + company.getId() + ": Invoice generated "+ file);
		
		return invoiceFileName;
	}
	
	
	public static void main(String[] args) throws Exception {
		StorageService svc = new StorageService();
		if (svc.SEPARATOR == null) {
			svc.SEPARATOR = "\\";
		}
		String srcFolder = "C:\\Temp\\platform\\files\\input\\INDIVIDUAL\\1";
		String destFolder = srcFolder;
		String file = "invoice1555077037.zip";
		svc.unZipFile(srcFolder, file, destFolder);
		
		/*
		PDFService pdfSvc = new PDFService();

		String invoiceFolder = "C:\\Temp\\platform";

		Company co = new Company("My Company", ComsUtil.USER_TYPE_COMPANY, "root", "100, Kings St., California", "USA");
		
		Invoice invoice = new Invoice(1, new Date(), new Date(), Double.valueOf(0), Double.valueOf(0),
				Double.valueOf(0), "NEW");
		invoice.setId(1001);
		
		invoice.addLineItem(new InvoiceLineItem(ComsUtil.JOB_TYPE_DATA_EXTRACTION, Double.valueOf(110),
				Double.valueOf(1.5), Double.valueOf(215), Double.valueOf(45), Double.valueOf(260)));
		invoice.addLineItem(new InvoiceLineItem(ComsUtil.JOB_TYPE_DATA_EXTRACTION, Double.valueOf(70),
				Double.valueOf(1), Double.valueOf(70), Double.valueOf(15), Double.valueOf(85)));

		pdfSvc.printInvoicePDF(co, invoice, invoiceFolder);
		*/
		
	}

}
