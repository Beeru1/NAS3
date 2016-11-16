package com.ibm.nas.forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.ibm.nas.dto.CircleDTO;
import com.ibm.nas.dto.CircleForProductDTO;
import com.ibm.nas.dto.CityDTO;
import com.ibm.nas.dto.CityZoneDTO;
import com.ibm.nas.dto.LeadStatusDTO;
import com.ibm.nas.dto.PINCodeDTO;
import com.ibm.nas.dto.PreviousOperatorDTO;
import com.ibm.nas.dto.ProductDTO;
import com.ibm.nas.dto.ProductLobDTO;
import com.ibm.nas.dto.RSUDTO;
import com.ibm.nas.dto.RequestCategoryDTO;
import com.ibm.nas.dto.RequestTypeDTO;
import com.ibm.nas.dto.SourceDTO;
import com.ibm.nas.dto.StateDTO;
import com.ibm.nas.dto.SubSourceDTO;
import com.ibm.nas.dto.ZoneDTO;

public class LeadRegistrationFormBean extends ActionForm {
	private String salesChannelCode="" ;
	private String methodName;
	private String methodTempName;
	private String leadStatus="";
	private int lobId;
	private String lobName="";
	 //added by nancy
	private String appointmentStatus="";
	private String appointment_Status="";
	private String leadSubStatusDisplay="";
	private int subStatusCode;
	private String autoZoneCode="";
	private String prospectId="";
	private String leadId="";	
	private String customerName="";
	private String contactNo="";
	private String alternateContactNo="";
	private String[] productIds;
	private int productId;
	private String[] productIdsOpen;
	private String checkslot;
	private String address1="";
	private String productflag ="false";
	private String GISFlag ="false";
	private String GISUrlAddress;
	private String password=""; //gis password
	private String username=""; // gir username
	private String fourGflag =  "";
	
	
	private String customerSegment="";
	private String simNumber="";
	private String relationName="";
	private String address2="";
	private String cityCode="";
	private String pinCode="";
	private String stateId="";	
	private String circleId="";
	private int selectedCircleId;
	private String language="";
	private String email="";
	private String landlineNo="";
	private String existingCustomer="";
	private String maritalStatus="";
	private String sourceId="";
	private String subSourceId="";
	private String appointmentDate="";
	private String appointmentHour="";
	private String appointmentMinute="";
	private String previousOperator="";
	private String appointmentTime="";
	
	private int selectedProductLobId;
	private String appointmentEndDate="";
	private String appointmentEndHour="";
	private String appointmentEndMinute="";
	
	private String appointmentEndTime="";
	private String appointmentStartTime="";
	private String circleName="";
	private String cityName="";
	private String cityZoneName="";
	private String circle="";
	
	private String requestType="";
	private String remarks="";
	private String createdBy="";	
	private String zoneCode;
	private String subZoneName;
	private String rsuCode="";
	private String sourceName="";
	private String subSourceName="";
	private String initStatus = "true";	
	private static final long serialVersionUID = 1L;
	private String udid = "";
	private String startDate = "";
	private String endDate = "";
	private String olmId = "";
	private ArrayList<ProductDTO> productList = null;
	private ArrayList<CityDTO> cityList = null;
	private ArrayList<PINCodeDTO> pinCodeList = null;
	
	//Added by satish
	
	private ArrayList<PreviousOperatorDTO> PreviousOperatorList = null;
	
	
	
	//Added by satish

	private ArrayList<StateDTO> stateList = null;
	private ArrayList<CircleDTO> circleList = null;
	private ArrayList<RequestTypeDTO> requestTypeList = null;	
	private ArrayList<SourceDTO> sourceList = null;
	private ArrayList<SubSourceDTO> subSourceList = null;	
	private ArrayList<ZoneDTO> zoneList = null;	
	private ArrayList<RSUDTO> rsuList = null;
	private ArrayList<LeadStatusDTO> leadStatusList = null;
	private String leadStatusId="";
	private String leadStatusName="";
	private String leadStatusDisplay="";
	private String extraParam3;
	private String extraParam4;
	private String totalDue;
	private ArrayList<ProductLobDTO> productLobList = null;
	private ArrayList<ProductDTO> lobList = null;	
	private ArrayList<ProductDTO> telemediaProductList = null;
	private ArrayList<ProductDTO> otherProductsList = null;
	private ArrayList<ProductDTO> fourGProductsList = null;
	private ArrayList<ProductDTO> mobilityProductList = null; // added by pratap
	private ArrayList<ProductDTO> dthProductList = null;
	private Boolean isSecondCall=false;
	private String tableValues;
	private String cityZoneCode=""; //Neetika on 19 Nov2013 for Phase 2 for bulk lead registration;
	private String remotAddress;
	private ArrayList<CityZoneDTO> cityZoneList = null; //added by amarjeet on 28/11/2013
	private Boolean flag;
	private ArrayList<CircleForProductDTO> circleForProductList = null;//added by amarjeet on 05/12/2013
	private int leadProspectId=0;
	private String ipAddress="";
	private int leadProductId=0;
	// adding by pratap as lead search dialer page change 19-Dec 2013
	private String campaign="";
	private String geoIpCity="";
	private String planId="";
	private String rental="";
	private String payment="";
	private String allocatedNo="";
	private String transactionRefNo="";
	private String onlineCafNo="";
	private int  productLobId=0;
	private ArrayList<Integer> restrictedProductList=null;
	private String leadCategory="";
	private boolean dirtyLead=false;	
	private boolean qualifiedLeadStatus=false;
	private String hlrNo="";
	private FormFile newFile;
	private String leadSubStatus="";
	private int subStatusId;
	private int leadSubSubStatusid;
	private int leadSubStatusId;
	private List leadSubSubStatusList;//added by Nancy
	private int leadSubSubStatusId=0;
	private int leadNewSubSubStatusid =0;
	public int getLeadNewSubSubStatusid() {
		return leadNewSubSubStatusid;
	}
	public void setLeadNewSubSubStatusid(int leadNewSubSubStatusid) {
		this.leadNewSubSubStatusid = leadNewSubSubStatusid;
	}

	private String leadSubSubStatus="";
	private String leadSubSubStatusDisplay="";
	private List leadSubStatusList;
	private String message;
	private String isError="false";
	private String view;
	private String plan="";
	private String rentalType="";
	private String freebieCount="";
	private String boosterCount="";
	private String freebieTaken="";
	private String boosterTaken="";
	private String prepaidNumber="";
	private String pytAmt="";
	private String tranRefno="";
	private String offer="";
	private String downloadLimit="";
	private String deviceMrp="";
	private String voiceBenefit="";
	private String dataQuota="";
	private String userType="";
	private String devicetaken="";
	private String benefit="";
	private String pkgDuration="";
	private String qualLeadParam ="";
	private String leadSubmitTime ="";
	private String tid  ="";
	private String fid  ="";
	private String keyWord  ="";
	private String fromPage  ="";
	private String service  ="";
	private String feasibilityParam  ="";
	private String referUrl  ="";
	private String company  ="";
	private String state="";
	private String caf="";
	private String extraParam6;
	private String extraParam7;
	private String previousCircle="";
	private String identityProofId="";
	public String getExtraParam7() {
		return extraParam7;
	}
	public void setExtraParam7(String extraParam7) {
		this.extraParam7 = extraParam7;
	}

	private String extraParam5;
	private int date;
	private int month;
	private int year;
	private String utmLabels  ="";
	private String referPage  ="";
	private String ndncDisclaimer="";
	private String extraParam1="";
	private int productLobID;
	private String postalCode="";
	private String taskType="";
	private Date currentDate;
	private Date lastDate;
	private String products;
	private String existingPart="";
	private String mnpStatus="";
	
	private String pageStatus="";
	private ArrayList<PreviousOperatorDTO> previousoperatornameList = null;
	
	//Added by satish 
	
	private String simno;//lead details ext1---------------------y
	
	private String customer_Segment;//lead details ext2---------------------y
	private String FamilymemberName;			//Fathers/Mothers/Husbands Name edit------y
	private String nationality;//lead details ext5------------------------------------------------y
	private String identityProofType;//lead details ext8 -------------------------------------------y
	private String identityProofID;//----------------------------------------------------------------y
	private String gender;//lead details ext7--------------------------------------------------------y
	private String age;
	private String paymentDate;//-------- y
	private String upc;//-------------------y
	private String upcGenDate;//--------------y
	private String previousoperatorname;//edit-----------------------y
	private String previouscircle;//edit--------------------------y
	private String existingpart;//---------------------y
	private String mnpstatus;//----------------------y
	private String documentCollectedFlag;//edit---------------y
	private String planType;//---------------------------------y
	private String relationname;//-------------------------------------y
	
	//Added by satish
	private String allStatusId ;
	private String allStatusName;
	
	
	public String getAllStatusId() {
		return allStatusId;
	}
	public void setAllStatusId(String allStatusId) {
		this.allStatusId = allStatusId;
	}
	public String getAllStatusName() {
		return allStatusName;
	}
	public void setAllStatusName(String allStatusName) {
		this.allStatusName = allStatusName;
	}
	public String getPageStatus() {
		return pageStatus;
	}
	public void setPageStatus(String pageStatus) {
		this.pageStatus = pageStatus;
	}

	private boolean selectLeadStatus=false;
	
	public String getSalesChannelCode() {
		return salesChannelCode;
	}
	public void setSalesChannelCode(String salesChannelCode) {
		this.salesChannelCode = salesChannelCode;
	}
	public boolean isSelectLeadStatus() {
		return selectLeadStatus;
	}
	public void setSelectLeadStatus(boolean selectLeadStatus) {
		this.selectLeadStatus = selectLeadStatus;
	}
	public Date getCurrentDate() {
		return currentDate;
	}
	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}
	public Date getLastDate() {
		return lastDate;
	}
	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

		//Added By Bhaskar
		private String appointmentStartDate="";
		private String appointmentStartHour="";
		private String appointmentStartMinute="";
		
	
		//added by Nancy
	private ArrayList<RequestCategoryDTO> categoryForStatusList = null;
	public String getAppointmentStartDate() {
		return appointmentStartDate;
	}
	public void setAppointmentStartDate(String appointmentStartDate) {
		this.appointmentStartDate = appointmentStartDate;
	}
	public String getAppointmentStartHour() {
		return appointmentStartHour;
	}
	public void setAppointmentStartHour(String appointmentStartHour) {
		this.appointmentStartHour = appointmentStartHour;
	}
	public String getAppointmentStartMinute() {
		return appointmentStartMinute;
	}
	public void setAppointmentStartMinute(String appointmentStartMinute) {
		this.appointmentStartMinute = appointmentStartMinute;
	}

	
	public String getAppointmentStartTime() {
		return appointmentStartTime;
	}
	public void setAppointmentStartTime(String appointmentStartTime) {
		this.appointmentStartTime = appointmentStartTime;
	}

	
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public int getLobId() {
		return lobId;
	}
	public void setLobId(int lobId) {
		this.lobId = lobId;
	}
	public String getLobName() {
		return lobName;
	}
	public void setLobName(String lobName) {
		this.lobName = lobName;
	}
	public String getAppointmentStatus() {
		return appointmentStatus;
	}
	public void setAppointmentStatus(String appointmentStatus) {
		this.appointmentStatus = appointmentStatus;
	}
	public String getAppointmentEndDate() {
		return appointmentEndDate;
	}
	public void setAppointmentEndDate(String appointmentEndDate) {
		this.appointmentEndDate = appointmentEndDate;
	}
	public String getAppointmentEndHour() {
		return appointmentEndHour;
	}
	public void setAppointmentEndHour(String appointmentEndHour) {
		this.appointmentEndHour = appointmentEndHour;
	}
	public String getAppointmentEndMinute() {
		return appointmentEndMinute;
	}
	public void setAppointmentEndMinute(String appointmentEndMinute) {
		this.appointmentEndMinute = appointmentEndMinute;
	}
	public String getAppointmentEndTime() {
		return appointmentEndTime;
	}
	public void setAppointmentEndTime(String appointmentEndTime) {
		this.appointmentEndTime = appointmentEndTime;
	}
	public String getCircleName() {
		return circleName;
	}
	public void setCircleName(String circleName) {
		this.circleName = circleName;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getCityZoneName() {
		return cityZoneName;
	}
	public void setCityZoneName(String cityZoneName) {
		this.cityZoneName = cityZoneName;
	}
	public String getExtraParam3() {
		return extraParam3;
	}
	public void setExtraParam3(String extraParam3) {
		this.extraParam3 = extraParam3;
	}
	public String getExtraParam4() {
		return extraParam4;
	}
	public void setExtraParam4(String extraParam4) {
		this.extraParam4 = extraParam4;
	}
	public String getTotalDue() {
		return totalDue;
	}
	public void setTotalDue(String totalDue) {
		this.totalDue = totalDue;
	}
	public ArrayList<ProductDTO> getFourGProductsList() {
		return fourGProductsList;
	}
	public void setFourGProductsList(ArrayList<ProductDTO> fourGProductsList) {
		this.fourGProductsList = fourGProductsList;
	}
	public int getSubStatusId() {
		return subStatusId;
	}
	public void setSubStatusId(int subStatusId) {
		this.subStatusId = subStatusId;
	}
	public int getLeadSubSubStatusid() {
		return leadSubSubStatusid;
	}
	public void setLeadSubSubStatusid(int leadSubSubStatusid) {
		this.leadSubSubStatusid = leadSubSubStatusid;
	}
	public int getLeadSubStatusId() {
		return leadSubStatusId;
	}
	public void setLeadSubStatusId(int leadSubStatusId) {
		this.leadSubStatusId = leadSubStatusId;
	}
	public List getLeadSubSubStatusList() {
		return leadSubSubStatusList;
	}
	public void setLeadSubSubStatusList(List leadSubSubStatusList) {
		this.leadSubSubStatusList = leadSubSubStatusList;
	}
	public int getLeadSubSubStatusId() {
		return leadSubSubStatusId;
	}
	public void setLeadSubSubStatusId(int leadSubSubStatusId) {
		this.leadSubSubStatusId = leadSubSubStatusId;
	}
	public String getLeadSubSubStatus() {
		return leadSubSubStatus;
	}
	public void setLeadSubSubStatus(String leadSubSubStatus) {
		this.leadSubSubStatus = leadSubSubStatus;
	}
	public List getLeadSubStatusList() {
		return leadSubStatusList;
	}
	public void setLeadSubStatusList(List leadSubStatusList) {
		this.leadSubStatusList = leadSubStatusList;
	}
	public String getExtraParam6() {
		return extraParam6;
	}
	public void setExtraParam6(String extraParam6) {
		this.extraParam6 = extraParam6;
	}
	public String getExtraParam5() {
		return extraParam5;
	}
	public void setExtraParam5(String extraParam5) {
		this.extraParam5 = extraParam5;
	}
	public int getDate() {
		return date;
	}
	public void setDate(int date) {
		this.date = date;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getProductLobID() {
		return productLobID;
	}
	public void setProductLobID(int productLobID) {
		this.productLobID = productLobID;
	}
	public String getCaf() {
		return caf;
	}
	public void setCaf(String caf) {
		this.caf = caf;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getNdncDisclaimer() {
		return ndncDisclaimer;
	}

	public void setNdncDisclaimer(String ndncDisclaimer) {
		this.ndncDisclaimer = ndncDisclaimer;
	}
	
	public String getReferPage() {
		return referPage;
	}

	public String getExtraParam1() {
		return extraParam1;
	}

	public void setExtraParam1(String extraParam1) {
		this.extraParam1 = extraParam1;
	}

	public void setReferPage(String referPage) {
		this.referPage = referPage;
	}

	public String getQualLeadParam() {
		return qualLeadParam;
	}

	public void setQualLeadParam(String qualLeadParam) {
		this.qualLeadParam = qualLeadParam;
	}

	public String getLeadSubmitTime() {
		return leadSubmitTime;
	}

	public void setLeadSubmitTime(String leadSubmitTime) {
		this.leadSubmitTime = leadSubmitTime;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getFeasibilityParam() {
		return feasibilityParam;
	}

	public void setFeasibilityParam(String feasibilityParam) {
		this.feasibilityParam = feasibilityParam;
	}

	public String getReferUrl() {
		return referUrl;
	}

	public void setReferUrl(String referUrl) {
		this.referUrl = referUrl;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getRentalType() {
		return rentalType;
	}

	public void setRentalType(String rentalType) {
		this.rentalType = rentalType;
	}

	public String getFreebieCount() {
		return freebieCount;
	}

	public void setFreebieCount(String freebieCount) {
		this.freebieCount = freebieCount;
	}

	public String getBoosterCount() {
		return boosterCount;
	}

	public void setBoosterCount(String boosterCount) {
		this.boosterCount = boosterCount;
	}

	public String getFreebieTaken() {
		return freebieTaken;
	}

	public void setFreebieTaken(String freebieTaken) {
		this.freebieTaken = freebieTaken;
	}

	public String getBoosterTaken() {
		return boosterTaken;
	}

	public void setBoosterTaken(String boosterTaken) {
		this.boosterTaken = boosterTaken;
	}

	

	public String getPrepaidNumber() {
		return prepaidNumber;
	}

	public void setPrepaidNumber(String prepaidNumber) {
		this.prepaidNumber = prepaidNumber;
	}

	public String getPytAmt() {
		return pytAmt;
	}

	public void setPytAmt(String pytAmt) {
		this.pytAmt = pytAmt;
	}

	public String getTranRefno() {
		return tranRefno;
	}

	public void setTranRefno(String tranRefno) {
		this.tranRefno = tranRefno;
	}

	public String getOffer() {
		return offer;
	}

	public void setOffer(String offer) {
		this.offer = offer;
	}

	public String getDownloadLimit() {
		return downloadLimit;
	}

	public void setDownloadLimit(String downloadLimit) {
		this.downloadLimit = downloadLimit;
	}

	public String getDeviceMrp() {
		return deviceMrp;
	}

	public void setDeviceMrp(String deviceMrp) {
		this.deviceMrp = deviceMrp;
	}

	public String getVoiceBenefit() {
		return voiceBenefit;
	}

	public void setVoiceBenefit(String voiceBenefit) {
		this.voiceBenefit = voiceBenefit;
	}

	public String getDataQuota() {
		return dataQuota;
	}

	public void setDataQuota(String dataQuota) {
		this.dataQuota = dataQuota;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getDevicetaken() {
		return devicetaken;
	}

	public void setDevicetaken(String devicetaken) {
		this.devicetaken = devicetaken;
	}

	public String getBenefit() {
		return benefit;
	}

	public void setBenefit(String benefit) {
		this.benefit = benefit;
	}

	public String getPkgDuration() {
		return pkgDuration;
	}

	public void setPkgDuration(String pkgDuration) {
		this.pkgDuration = pkgDuration;
	}

	private String utmSource="";


	private String utmTerm="";
	private String utmMedium="";
	private String utmCampaign="";
	private String leadPriority = "";
	
	/*Added By Bhaskar*/
	private String productName="";
	private String productLobName="";
	private String requestCategory="";
	private String requestCategoryName="";
	private String requestCategoryId="";
	

	/* End of changes by Bhaskar */

	

	public String getRequestCategoryId() {
		return requestCategoryId;
	}
	public void setRequestCategoryId(String requestCategoryId) {
		this.requestCategoryId = requestCategoryId;
	}
	public String getRequestCategoryName() {
		return requestCategoryName;
	}
	public void setRequestCategoryName(String requestCategoryName) {
		this.requestCategoryName = requestCategoryName;
	}
	public String getProductName() {
		return productName;
	}

	public String getRequestCategory() {
		return requestCategory;
	}

	public void setRequestCategory(String requestCategory) {
		this.requestCategory = requestCategory;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductLobName() {
		return productLobName;
	}

	public void setProductLobName(String productLobName) {
		this.productLobName = productLobName;
	}

	private String param = "";
	public String getHlrNo() {
		return hlrNo;
	}

	public void setHlrNo(String hlrNo) {
		this.hlrNo = hlrNo;
	}

	public String getLeadSubStatus() {
		return leadSubStatus;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getLeadPriority() {
		return leadPriority;
	}

	public void setLeadPriority(String leadPriority) {
		this.leadPriority = leadPriority;
	}

	public String getUtmSource() {
		return utmSource;
	}

	public void setUtmSource(String utmSource) {
		this.utmSource = utmSource;
	}

	public String getUtmTerm() {
		return utmTerm;
	}

	public void setUtmTerm(String utmTerm) {
		this.utmTerm = utmTerm;
	}

	public String getUtmMedium() {
		return utmMedium;
	}

	public void setUtmMedium(String utmMedium) {
		this.utmMedium = utmMedium;
	}

	public String getUtmCampaign() {
		return utmCampaign;
	}

	public void setUtmCampaign(String utmCampaign) {
		this.utmCampaign = utmCampaign;
	}

	public void setLeadSubStatus(String leadSubStatus) {
		this.leadSubStatus = leadSubStatus;
	}

	public ArrayList<Integer> getRestrictedProductList() {
		return restrictedProductList;
	}

	public boolean isQualifiedLeadStatus() {
		return qualifiedLeadStatus;
	}

	public void setQualifiedLeadStatus(boolean qualifiedLeadStatus) {
		this.qualifiedLeadStatus = qualifiedLeadStatus;
	}

	//public boolean isLeadStatus() {
		//return leadStatus;
	//}

//	public void setLeadStatus(boolean leadStatus) {
	//	this.leadStatus = leadStatus;
	//}

	public boolean isDirtyLead() {
		return dirtyLead;
	}

	public void setDirtyLead(boolean dirtyLead) {
		this.dirtyLead = dirtyLead;
	}

	public String getLeadCategory() {
		return leadCategory;
	}

	public void setLeadCategory(String leadCategory) {
		this.leadCategory = leadCategory;
	}

	public void setRestrictedProductList(ArrayList<Integer> restrictedProductList) {
		this.restrictedProductList = restrictedProductList;
	}
	

	public ArrayList<ProductDTO> getMobilityProductList() {
		return mobilityProductList;
	}

	
	public void setMobilityProductList(ArrayList<ProductDTO> mobilityProductList) {
		this.mobilityProductList = mobilityProductList;
	}

	public int getProductLobId() {
		return productLobId;
	}

	public void setProductLobId(int productLobId) {
		this.productLobId = productLobId;
	}

	public String getOnlineCafNo() {
		return onlineCafNo;
	}

	public void setOnlineCafNo(String onlineCafNo) {
		this.onlineCafNo = onlineCafNo;
	}

	public String getTransactionRefNo() {
		return transactionRefNo;
	}

	public void setTransactionRefNo(String transactionRefNo) {
		this.transactionRefNo = transactionRefNo;
	}

	public String getAllocatedNo() {
		return allocatedNo;
	}

	public void setAllocatedNo(String allocatedNo) {
		this.allocatedNo = allocatedNo;
	}

	public String getPayment() {
		return payment;
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	public String getRental() {
		return rental;
	}

	public void setRental(String rental) {
		this.rental = rental;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getGeoIpCity() {
		return geoIpCity;
	}

	public void setGeoIpCity(String geoIpCity) {
		this.geoIpCity = geoIpCity;
	}

	public String getCampaign() {
		return campaign;
	}

	public void setCampaign(String campaign) {
		this.campaign = campaign;
	}

	public int getLeadProductId() {
		return leadProductId;
	}

	public void setLeadProductId(int leadProductId) {
		this.leadProductId = leadProductId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getLeadProspectId() {
		return leadProspectId;
	}

	public void setLeadProspectId(int leadProspectId) {
		this.leadProspectId = leadProspectId;
	}

	public ArrayList<CircleForProductDTO> getCircleForProductList() {
		return circleForProductList;
	}

	public void setCircleForProductList(ArrayList<CircleForProductDTO> circleForProductList) {
		this.circleForProductList = circleForProductList;
	}

	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}

	public ArrayList<CityZoneDTO> getCityZoneList() {
		return cityZoneList;
	}

	public void setCityZoneList(ArrayList<CityZoneDTO> cityZoneList) {
		this.cityZoneList = cityZoneList;
	}

	public String getRemotAddress() {
		return remotAddress;
	}

	public void setRemotAddress(String remotAddress) {
		this.remotAddress = remotAddress;
	}

	public String getAutoZoneCode() {
		return autoZoneCode;
	}

	public void setAutoZoneCode(String autoZoneCode) {
		this.autoZoneCode = autoZoneCode;
	}

	public String getCityZoneCode() {
		return cityZoneCode;
	}

	public void setCityZoneCode(String cityZoneCode) {
		this.cityZoneCode = cityZoneCode;
	}
	public String getTableValues() {
		return tableValues;
	}

	public void setTableValues(String tableValues) {
		this.tableValues = tableValues;
	}
	public Boolean getIsSecondCall() {
		return isSecondCall;
	}

	public void setIsSecondCall(Boolean isSecondCall) {
		this.isSecondCall = isSecondCall;
	}

	public ArrayList<ProductDTO> getDthProductList() {
		return dthProductList;
	}

	public void setDthProductList(ArrayList<ProductDTO> dthProductList) {
		this.dthProductList = dthProductList;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getSubSourceName() {
		return subSourceName;
	}

	public void setSubSourceName(String subSourceName) {
		this.subSourceName = subSourceName;
	}

	public ArrayList<ProductLobDTO> getProductLobList() {
		return productLobList;
	}

	public void setProductLobList(ArrayList<ProductLobDTO> productLobList) {
		this.productLobList = productLobList;
	}

	
	public ArrayList<ProductDTO> getTelemediaProductList() {
		return telemediaProductList;
	}

	public void setTelemediaProductList(ArrayList<ProductDTO> telemediaProductList) {
		this.telemediaProductList = telemediaProductList;
	}

	public String getLeadStatusName() {
		return leadStatusName;
	}

	public void setLeadStatusName(String leadStatusName) {
		this.leadStatusName = leadStatusName;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	

	public String[] getProductIdsOpen() {
		return productIdsOpen;
	}

	public void setProductIdsOpen(String[] productIdsOpen) {
		this.productIdsOpen = productIdsOpen;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public ArrayList<PINCodeDTO> getPinCodeList() {
		return pinCodeList;
	}

	public void setPinCodeList(ArrayList<PINCodeDTO> pinCodeList) {
		this.pinCodeList = pinCodeList;
	}

	public ArrayList<CityDTO> getCityList() {
		return cityList;
	}

	public void setCityList(ArrayList<CityDTO> cityList) {
		this.cityList = cityList;
	}

	public String getZoneCode() {
		return zoneCode;
	}

	public void setZoneCode(String zoneCode) {
		this.zoneCode = zoneCode;
	}

	public String getSubZoneName() {
		return subZoneName;
	}

	public void setSubZoneName(String subZoneName) {
		this.subZoneName = subZoneName;
	}

	public String getRsuCode() {
		return rsuCode;
	}

	public void setRsuCode(String rsuCode) {
		this.rsuCode = rsuCode;
	}

	public String getLeadStatusId() {
		return leadStatusId;
	}

	public void setLeadStatusId(String leadStatusId) {
		this.leadStatusId = leadStatusId;
	}

	public ArrayList<ZoneDTO> getZoneList() {
		return zoneList;
	}

	public void setZoneList(ArrayList<ZoneDTO> zoneList) {
		this.zoneList = zoneList;
	}

	public ArrayList<RSUDTO> getRsuList() {
		return rsuList;
	}

	public void setRsuList(ArrayList<RSUDTO> rsuList) {
		this.rsuList = rsuList;
	}

	public ArrayList<LeadStatusDTO> getLeadStatusList() {
		return leadStatusList;
	}

	public void setLeadStatusList(ArrayList<LeadStatusDTO> leadStatusList) {
		this.leadStatusList = leadStatusList;
	}

	public ArrayList<SourceDTO> getSourceList() {
		return sourceList;
	}

	public void setSourceList(ArrayList<SourceDTO> sourceList) {
		this.sourceList = sourceList;
	}

	public ArrayList<SubSourceDTO> getSubSourceList() {
		return subSourceList;
	}

	public void setSubSourceList(ArrayList<SubSourceDTO> subSourceList) {
		this.subSourceList = subSourceList;
	}

	public String getLeadId() {
		return leadId;
	}

	public void setLeadId(String leadId) {
		this.leadId = leadId;
	}

	public String getInitStatus() {
		return initStatus;
	}

	public void setInitStatus(String initStatus) {
		this.initStatus = initStatus;
	}

	public ArrayList<RequestTypeDTO> getRequestTypeList() {
		return requestTypeList;
	}

	public void setRequestTypeList(ArrayList<RequestTypeDTO> requestTypeList) {
		this.requestTypeList = requestTypeList;
	}

	public String getProspectId() {
		return prospectId;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public void setProspectId(String prospectId) {
		this.prospectId = prospectId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getAlternateContactNo() {
		return alternateContactNo.trim();
	}

	public void setAlternateContactNo(String alternateContactNo) {
		this.alternateContactNo = alternateContactNo;
	}

	public String[] getProductIds() {
		return productIds;
	}

	public void setProductIds(String[] productIds) {
		this.productIds = productIds;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	
	

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getStateId() {
		return stateId;
	}

	public void setStateId(String stateId) {
		this.stateId = stateId;
	}

	public String getCircleId() {
		return circleId;
	}

	public void setCircleId(String circleId) {
		this.circleId = circleId;
	}
	public String getCircle() {
		return circle;
	}

	public void setCircle(String circle) {
		this.circle = circle;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLandlineNo() {
		return landlineNo;
	}

	public void setLandlineNo(String landlineNo) {
		this.landlineNo = landlineNo;
	}

	public String getExistingCustomer() {
		return existingCustomer;
	}

	public void setExistingCustomer(String existingCustomer) {
		this.existingCustomer = existingCustomer;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getSubSourceId() {
		return subSourceId;
	}

	public void setSubSourceId(String subSourceId) {
		this.subSourceId = subSourceId;
	}

	public String getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(String appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public String getAppointmentHour() {
		return appointmentHour;
	}

	public void setAppointmentHour(String appointmentHour) {
		this.appointmentHour = appointmentHour;
	}

	public String getAppointmentMinute() {
		return appointmentMinute;
	}

	public void setAppointmentMinute(String appointmentMinute) {
		this.appointmentMinute = appointmentMinute;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public ArrayList<ProductDTO> getProductList() {
		return productList;
	}

	public void setProductList(ArrayList<ProductDTO> productList) {
		this.productList = productList;
	}

	public ArrayList<StateDTO> getStateList() {
		return stateList;
	}

	public void setStateList(ArrayList<StateDTO> stateList) {
		this.stateList = stateList;
	}

	public ArrayList<CircleDTO> getCircleList() {
		return circleList;
	}

	public void setCircleList(ArrayList<CircleDTO> circleList) {
		this.circleList = circleList;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
	

	public String getAppointmentTime() {
		return appointmentTime;
	}

	public void setAppointmentTime(String appointmentTime) {
		this.appointmentTime = appointmentTime;
	}

	public String getOlmId() {
		return olmId;
	}

	public void setOlmId(String olmId) {
		this.olmId = olmId;
	}

	/**
	 * @param udid the udid to set
	 */
	public void setUdid(String udid) {
		this.udid = udid;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the udid
	 */
	public String getUdid() {
		return udid;
	}

	public void reset(ActionMapping mapping, HttpServletRequest request) {
		
		prospectId="";
		leadId="";;	
		customerName="";
		contactNo="";
		alternateContactNo="";
		productIds=null;
		address1="";
		address2="";
		cityCode="";
		pinCode="";
		stateId="";	
		circleId="";
		language="";
		email="";
		landlineNo="";
		existingCustomer="";
		maritalStatus="";
		sourceId="";
		subSourceId="";
		appointmentDate="";
		appointmentHour="";
		appointmentMinute="";
		requestType="";
		remarks="";
		createdBy="";	
		appointmentTime="";
		zoneCode = "";
		udid = "";
		leadStatusId="";

		leadSubStatus="";
		
		/* Added by Parnika */
		//leadStatus = true;
		campaign = "";
		//selectedProductLobId=0;
		//productLobId=0;
			
		/* End of changes by Parnika */

	}

	


	public String getLeadStatus() {
		return leadStatus;
	}



	public void setLeadStatus(String leadStatus) {
		this.leadStatus = leadStatus;
	}



	public String getLeadSubStatusDisplay() {
		return leadSubStatusDisplay;
	}



	public void setLeadSubStatusDisplay(String leadSubStatusDisplay) {
		this.leadSubStatusDisplay = leadSubStatusDisplay;
	}



	public int getSubStatusCode() {
		return subStatusCode;
	}



	public void setSubStatusCode(int subStatusCode) {
		this.subStatusCode = subStatusCode;
	}



	public String getLeadStatusDisplay() {
		return leadStatusDisplay;
	}



	public void setLeadStatusDisplay(String leadStatusDisplay) {
		this.leadStatusDisplay = leadStatusDisplay;
	}



	public String getLeadSubSubStatusDisplay() {
		return leadSubSubStatusDisplay;
	}



	public void setLeadSubSubStatusDisplay(String leadSubSubStatusDisplay) {
		this.leadSubSubStatusDisplay = leadSubSubStatusDisplay;
	}



	public void setLeadSubStatusList(ArrayList<LeadStatusDTO> leadSubStatusList) {
		this.leadSubStatusList = leadSubStatusList;
	}
	public void setLeadSubSubStatusList(ArrayList<LeadStatusDTO> leadSubSubStatusList) {
		this.leadSubSubStatusList = leadSubSubStatusList;
	}
	public int getSelectedProductLobId() {
		return selectedProductLobId;
	}

	public void setSelectedProductLobId(int selectedProductLobId) {
		this.selectedProductLobId = selectedProductLobId;
	}

	public ArrayList<ProductDTO> getLobList() {
		return lobList;
	}

	public void setLobList(ArrayList<ProductDTO> lobList) {
		this.lobList = lobList;
	}

	public ArrayList<ProductDTO> getOtherProductsList() {
		return otherProductsList;
	}

	public void setOtherProductsList(ArrayList<ProductDTO> otherProductsList) {
		this.otherProductsList = otherProductsList;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getSelectedCircleId() {
		return selectedCircleId;
	}

	public void setSelectedCircleId(int selectedCircleId) {
		this.selectedCircleId = selectedCircleId;
	}

	public String getIsError() {
		return isError;
	}

	public void setIsError(String isError) {
		this.isError = isError;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public FormFile getNewFile() {
		return newFile;
	}

	public void setNewFile(FormFile newFile) {
		this.newFile = newFile;
	}
	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}
	
	public String getUtmLabels() {
		return utmLabels;
	}

	public void setUtmLabels(String utmLabels) {
		this.utmLabels = utmLabels;
	}

	public ArrayList<RequestCategoryDTO> getCategoryForStatusList() {
		return categoryForStatusList;
	}

	public void setCategoryForStatusList(ArrayList<RequestCategoryDTO> categoryForStatusList) {
		this.categoryForStatusList = categoryForStatusList;
	}
	public String getMethodTempName() {
		return methodTempName;
	}
	public void setMethodTempName(String methodTempName) {
		this.methodTempName = methodTempName;
	}
	public String getProducts() {
		return products;
	}
	public void setProducts(String products) {
		this.products = products;
	}
	public String getProductflag() {
		return productflag;
	}
	public void setProductflag(String productflag) {
		this.productflag = productflag;
	}
	public String getGISFlag() {
		return GISFlag;
	}
	public void setGISFlag(String flag) {
		GISFlag = flag;
	}
	public String getGISUrlAddress() {
		return GISUrlAddress;
	}
	public void setGISUrlAddress(String urlAddress) {
		GISUrlAddress = urlAddress;
	}
	public String getAppointment_Status() {
		return appointment_Status;
	}
	public void setAppointment_Status(String appointment_Status) {
		this.appointment_Status = appointment_Status;
	}

	public String getCheckslot() {
		return checkslot;
	}
	public void setCheckslot(String checkslot) {
		this.checkslot = checkslot;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFourGflag() {
		return fourGflag;
	}
	public void setFourGflag(String fourGflag) {
		this.fourGflag = fourGflag;
	}
	public String getSimno() {
		return simno;
	}
	public void setSimno(String simno) {
		this.simno = simno;
	}
	public String getCustomer_Segment() {
		return customer_Segment;
	}
	public void setCustomer_Segment(String customer_Segment) {
		this.customer_Segment = customer_Segment;
	}
	public String getFamilymemberName() {
		return FamilymemberName;
	}
	public void setFamilymemberName(String familymemberName) {
		FamilymemberName = familymemberName;
	}
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	public String getIdentityProofType() {
		return identityProofType;
	}
	public void setIdentityProofType(String identityProofType) {
		this.identityProofType = identityProofType;
	}
	public String getIdentityProofID() {
		return identityProofID;
	}
	public void setIdentityProofID(String identityProofID) {
		this.identityProofID = identityProofID;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}
	public String getUpc() {
		return upc;
	}
	public void setUpc(String upc) {
		this.upc = upc;
	}
	public String getUpcGenDate() {
		return upcGenDate;
	}
	public void setUpcGenDate(String upcGenDate) {
		this.upcGenDate = upcGenDate;
	}
	public String getPreviousoperatorname() {
		System.out.println("*************************bean***********************"+previousoperatorname);
		return previousoperatorname;
	}
	public void setPreviousoperatorname(String previousoperatorname) {
		this.previousoperatorname = previousoperatorname;
	}
	public String getPreviouscircle() {
		System.out.println("*************************bean***********************"+previousoperatorname);
		return previouscircle;
	}
	public void setPreviouscircle(String previouscircle) {
		this.previouscircle = previouscircle;
	}
	public String getExistingpart() {
		return existingpart;
	}
	public void setExistingpart(String existingpart) {
		this.existingpart = existingpart;
	}
	public String getMnpstatus() {
		return mnpstatus;
	}
	public void setMnpstatus(String mnpstatus) {
		this.mnpstatus = mnpstatus;
	}
	public String getDocumentCollectedFlag() {
		System.out.println("*************************bean***********************"+documentCollectedFlag);
		return documentCollectedFlag;
	}
	public void setDocumentCollectedFlag(String documentCollectedFlag) {
		this.documentCollectedFlag = documentCollectedFlag;
	}
	public String getPlanType() {
		return planType;
	}
	public void setPlanType(String planType) {
		this.planType = planType;
	}
	public ArrayList<PreviousOperatorDTO> getPreviousoperatornameList() {
		return previousoperatornameList;
	}
	public void setPreviousoperatornameList(ArrayList<PreviousOperatorDTO> previousoperatornameList) {
		this.previousoperatornameList = previousoperatornameList;
	}
	public ArrayList<PreviousOperatorDTO> getPreviousOperatorList() {
		return PreviousOperatorList;
	}
	public void setPreviousOperatorList(ArrayList<PreviousOperatorDTO> previousOperatorList) {
		PreviousOperatorList = previousOperatorList;
	}
	public String getRelationname() {
		System.out.println("*************************bean***********************"+relationname);
		return relationname;
	}
	public void setRelationname(String relationname) {
		this.relationname = relationname;
	}
	public String getCustomerSegment() {
		return customerSegment;
	}
	public void setCustomerSegment(String customerSegment) {
		this.customerSegment = customerSegment;
	}
	public String getSimNumber() {
		return simNumber;
	}
	public void setSimNumber(String simNumber) {
		this.simNumber = simNumber;
	}
	public String getRelationName() {
		return relationName;
	}
	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}
	public String getPreviousOperator() {
		return previousOperator;
	}
	public void setPreviousOperator(String previousOperator) {
		this.previousOperator = previousOperator;
	}
	public String getExistingPart() {
		return existingPart;
	}
	public void setExistingPart(String existingPart) {
		this.existingPart = existingPart;
	}
	public String getMnpStatus() {
		return mnpStatus;
	}
	public void setMnpStatus(String mnpStatus) {
		this.mnpStatus = mnpStatus;
	}
	public String getPreviousCircle() {
		return previousCircle;
	}
	public void setPreviousCircle(String previousCircle) {
		this.previousCircle = previousCircle;
	}
	public String getIdentityProofId() {
		return identityProofId;
	}
	public void setIdentityProofId(String identityProofId) {
		this.identityProofId = identityProofId;
	}
	
	
}
