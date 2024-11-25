package com;

import java.util.Iterator;
import java.util.Set;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Testing {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String json = "{\r\n" + "      \"acctIDCHPDB\":1,\r\n" + "      \"accountId_ICON\":9,\r\n"
				+ "      \"name\":\"Al Essa Medical & Scientific Equipment Co. W.L.L\",\r\n"
				+ "      \"address\":\"Street No 38, Sector C, Block 118\",\r\n" + "      \"zip\":\"13036\",\r\n"
				+ "      \"poBox\":\"\",\r\n" + "      \"city\":\"Kuwait City\",\r\n"
				+ "      \"fullAddress\":\"Street No 38, Sector C, Block 118, 13036, 3562Kuwait City\",\r\n"
				+ "      \"email\":\"alessa@alessakuwait.com\",\r\n" + "      \"phone\":\"868-640-2482\",\r\n"
				+ "      \"fax\":\"868-640-8159\",\r\n" + "      \"countryId\":228,\r\n"
				+ "      \"countryName\":\"Trinidad And Tobago\",\r\n" + "      \"contractNumber\":\"NLA012\",\r\n"
				+ "      \"soldTo_CommercialAccountCode\":\"562210, 562209\",\r\n"
				+ "      \"businessAreaFullName\":\"Health Systems - LATAM - North Latam - North Latam DX - Trinidad And Tobago - Trinidad And Tobago\",\r\n"
				+ "      \"businessAreaFullName2\":\"\",\r\n" + "      \"businessAreaFullName3\":\"\",\r\n"
				+ "      \"accountTerritories\":\"Trinidad And Tobago,\",\r\n"
				+ "      \"philipsLegalEntity\":\"Philips Medical Systems Nederland B.V.\",\r\n"
				+ "      \"signerORU\":\"\",\r\n" + "      \"oruController\":\"\",\r\n"
				+ "      \"signerCostCentreNumber\":\"\",\r\n" + "      \"accountGroupId\":37,\r\n"
				+ "      \"accountGroupName\":\"Indirect Trade Partner\",\r\n" + "      \"accountChannelId\":75,\r\n"
				+ "      \"accountChannelName\":\"B2G\",\r\n" + "      \"accountTypeId\":469,\r\n"
				+ "      \"accountTypeName\":\"Partner Sales and Service Delivery\",\r\n"
				+ "      \"companyGroupName\":\"\",\r\n" + "      \"isMainAccount\":\"Yes\",\r\n"
				+ "      \"accountStatusId\":1,\r\n" + "      \"accountStatus\":\"Active\",\r\n"
				+ "      \"dateCreated\":null,\r\n" + "      \"initialStartDate\":null,\r\n"
				+ "      \"endDate\":null,\r\n" + "      \"statusChangeReason\":null,\r\n"
				+ "      \"additionalNotes\":\"\r\n" + "WWW.AALAQUIS.COM\r\n"
				+ "Agreement number: LTM-BC-012/10-B.\",\r\n" + "      \"additionalInformation\":\"\",\r\n"
				+ "      \"additionalInformation2\":\"Health Systems - LATAM - North Latam - Republica Domenicana & Caribe\",\r\n"
				+ "      \"isAccountIndirect\":1,\r\n" + "      \"isEUImporter\":0,\r\n"
				+ "      \"isEUTradePartner\":0,\r\n" + "      \"ddpStatusId\":3,\r\n"
				+ "      \"ddpStatus\":\"Renewal In Progress\",\r\n" + "      \"ddpValidityChangedByFICS\":\"No\",\r\n"
				+ "      \"ddpValidUntil\":1712082600000,\r\n"
				+ "      \"plannedStartDateDueDilligence\":1680978600000,\r\n"
				+ "      \"corruptionPerceptionIndex\":4.1,\r\n" + "      \"businessCriteriaCurrency\":\"EUR\",\r\n"
				+ "      \"brandNames\":\"AED,Cardiology Informatics,Computed Tomography,CS Computed Tomography,CS DXR,CS Interventional X-Ray,CS MRI Systems-Equipment and magnets,CS Multi-Vendor,CS Nuclear Medicine,CS PCMS,CS Professional Services,CS Ultrasound,CS Undivided,DECG,Diagnostic X-Ray WHC,DXR,ECR,Enterprise Imaging Informatics,Generator, Tube and Components,Goldway,Imaging Systems,Interventional X-Ray,Invivo Monitoring,Med Cons. and Childrens Med. Ventures,MRI SYSTEMS,Nuclear Medicine,Patient Monitoring and Systems,PCMS,Philips Radiotherapy Oncology Systems,Respironics Home Healthcare,Respironics Hospital,Sector Healthcare - Other FS items,Sector Healthcare Management Adjustments,Specialty and Emerging Markets,Ultrasound\",\r\n"
				+ "      \"second_Line_Commercial_Contacts\":\"Perez, Irene [irene.perez@philips.com];Mata, Asdrubal [asdrubal.mata@philips.com];Araujo, Bruna Cristina Gomes de [bruna.araujo@philips.com];Costa, Alexei [alexei.costa@philips.com];Chuffi, Cristina [cristina.chuffi.r@philips.com];Paz, Lila [lila.paz@philips.com];Saldana, Alfonso [Alfonso.Saldana@philips.com];Garcia Landazabal, Jorge [jorge.e.garcia@philips.com];Vitorino, Sergio [sergio.vitorino@philips.com]\",\r\n"
				+ "      \"first_Line_Controllers\":\"Da Silva, Jessika [jessika.dasilva@philips.com]\",\r\n"
				+ "      \"second_Line_Controllers\":\"\",\r\n"
				+ "      \"first_Line_Legal_Counsels\":\"RODRIGUEZ CAPRILES, MARIA EMILIA [mariaemilia.rodriguezcapriles@philips.com]\",\r\n"
				+ "      \"second_Line_Legal_Counsels\":\"Naufel, Patricia Frossard [patricia.frossard.naufel@philips.com];Cuartas Salazar, Santiago [santiago.cuartas.salazar@philips.com]\",\r\n"
				+ "      \"account_Managers\":\"\",\r\n"
				+ "      \"indirect_Channel_Managers\":\"Amaya, Karime [karime.amaya@philips.com];Perez, Irene [irene.perez@philips.com];Faberlle, Teresa [Teresa.Faberlle@philips.com];Cuitino, Delfina [Delfina.Cuitino@philips.com];Carrasquilla, Luis Miguel [luismiguel.carrasquilla@philips.com]\",\r\n"
				+ "      \"contract_Managers\":\"Walisiak, Klaudia [Klaudia.Walisiak@philips.com];Stepniewska, Magdalena [Magdalena.Stepniewska@philips.com]\",\r\n"
				+ "      \"currency\":\"USD\",\r\n" + "      \"paymentCurrency\":\"US Dollar\",\r\n"
				+ "      \"creditLimit\":\"180 000\",\r\n" + "      \"paymentTermInDays\":90,\r\n"
				+ "      \"specialTermsSecured\":\"Approval required\",\r\n"
				+ "      \"specialTermsOpenAccount\":\"\",\r\n" + "      \"website\":\"\",\r\n"
				+ "      \"timeZoneId\":null,\r\n" + "      \"timeZone\":\"\",\r\n"
				+ "      \"accountTier\":\"Tier 3\",\r\n" + "      \"bankAccountNumber\":\"\",\r\n"
				+ "      \"bank\":\"\",\r\n" + "      \"stateInstitutionBankAccountNumber\":\"\",\r\n"
				+ "      \"vatNumber\":\"111222333\",\r\n" + "      \"chamberOfCommerceNumber\":\"\",\r\n"
				+ "      \"chamberOfCommerceNumberForPhilips\":\"\",\r\n"
				+ "      \"placeOfArbitration\":\"Amsterdam\",\r\n" + "      \"applicableLaw\":\"The Netherlands\",\r\n"
				+ "      \"correspondentBankAccountNumber\":\"\",\r\n" + "      \"shipToAddress\":\"\",\r\n"
				+ "      \"registeredCapital\":\"\",\r\n" + "      \"referenceProjects\":\"\",\r\n"
				+ "      \"shipToFunloc\":\"\",\r\n" + "      \"billToFunloc\":\"\",\r\n"
				+ "      \"invoiceFunloc\":\"\",\r\n" + "      \"isAffiliatedAccount\":\"NO\",\r\n"
				+ "      \"mainAccountId\":null,\r\n" + "      \"philipsSalesRevenueAboveThreshold\":\"YES\",\r\n"
				+ "      \"termsOfLC\":\"\",\r\n" + "      \"prePayment\":\"YES\",\r\n"
				+ "      \"letterOfCredit\":\"NO\",\r\n" + "      \"bankGuarantee\":\"NO\",\r\n"
				+ "      \"nameInLocal\":\"\",\r\n" + "      \"requestSubmittedBy\":\"\",\r\n"
				+ "      \"accountContractSignerName\":\"Amos Laquis\",\r\n"
				+ "      \"accountContractSignerPosition\":\"Managing Director\",\r\n"
				+ "      \"accountContractSignerEmail\":\"amos@aaltrinidad.com\",\r\n"
				+ "      \"businessAreaSector\":\"Health Systems\",\r\n" + "      \"businessAreaRegion\":\"LATAM\",\r\n"
				+ "      \"businessAreaMarket\":\"North Latam\",\r\n"
				+ "      \"businessAreaDistrict\":\"North Latam DX\",\r\n"
				+ "      \"businessAreaCountry\":\"Trinidad And Tobago\",\r\n"
				+ "      \"businessAreaSubTerritory\":\"Trinidad And Tobago\",\r\n"
				+ "      \"thirdPartyDataManagers\":\"\",\r\n" + "      \"autoRestartEmployeeFormEnabled\":\"Yes\",\r\n"
				+ "      \"employeeFormStatusId\":null,\r\n" + "      \"employeeFormStatus\":\"\",\r\n"
				+ "      \"employeeFormLastCompletionDate\":1702119647000,\r\n"
				+ "      \"sfdcId\":\"001d000001ZZygc\",\r\n" + "      \"sfdcAccountName\":\"\",\r\n"
				+ "      \"sdfcPhilipsIndustryClassificationLevel1\":\"\",\r\n"
				+ "      \"sdfcPhilipsIndustryClassificationLevel2\":\"\",\r\n"
				+ "      \"isAccountDuplicated\":\"No\",\r\n" + "      \"primaryAccountId\":null,\r\n"
				+ "      \"primaryAccountName\":\"\",\r\n" + "      \"duplicatedOn\":null,\r\n"
				+ "      \"caStatus\":\"\",\r\n" + "      \"caFrequency\":\"5 Years\",\r\n"
				+ "      \"caRiskName\":\"5 Years\",\r\n" + "      \"caDueDate\":1743445800000,\r\n"
				+ "      \"caPlannedDate\":1680201000000,\r\n" + "      \"caReviewedOn\":null,\r\n"
				+ "      \"createDt\":null,\r\n" + "      \"updateDt\":1706146821271,\r\n"
				+ "      \"partnerInLoyaltyPrg\":false,\r\n" + "      \"partnerEmpDataCleansed\":false,\r\n"
				+ "      \"newPartnerInIcon\":false\r\n" + "   }";

		String html = generateHTML(json);
		System.out.println(html);
	}

	public static String generateHTML(String json) {
		JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
		Set<String> keys = jsonObject.keySet();

		String NEWLINE = "\n";
		String BLANK_ROW = "<tr><td></td><td></td></tr>";
		String html = "<table border=1 width=80%>" + NEWLINE;
		html = html + BLANK_ROW + NEWLINE;
		html = html + "<tr><td bgcolor=#F4B183><b>Account Informtion</b></td><td></td></tr>" + NEWLINE;
		html = html + BLANK_ROW + NEWLINE;
		for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			html = html + addRow(key, jsonObject) + NEWLINE;
		}
		html = html + "</table>";
		
		html = html + "<BR/>";
		html = html + "<BR/>";
		html = html + "<table border=1 width=80%>" + NEWLINE;
		html = html + "<tr><td bgcolor=#A9D18E><b>Delegate Informtion</b></td><td></td></tr>" + NEWLINE;
		for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			html = html + addRow(key, jsonObject) + NEWLINE;
		}
		
		html = html + "</table>";
		return html;
	}

	public static String addRow(String key, JsonObject jsonObject) {
		String row = "<tr>";
		row = row + "<td bgcolor=#B4C7E7>";
		row = row + key;
		row = row + "</td>";
		row = row + "<td>";
		row = row + jsonObject.get(key);
		row = row + "</td>";
		row = row + "</tr>";
		return row;
	}

}
