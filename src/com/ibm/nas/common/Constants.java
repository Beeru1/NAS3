package com.ibm.nas.common;

public class Constants {
	public static final int DOC_TYPE_FILE = 1;
	public static final int DOC_TYPE_PRODUCT = 2;
	public static final int DOC_TYPE_SOP = 3;
	public static final int DOC_TYPE_SOP_BD = 4;
	public static final int DOC_TYPE_RC = 5;
	public static final int DOC_TYPE_BP = 6;
	public static final int DOC_TYPE_DTH = 7;
	public static final int TOTAL_HEADERS_BULK_AGENCY_CREATE=10;
	public static final int TOTAL_HEADERS_BULK_CHANNEL_ID=3;
	public static final int TOTAL_HEADERS_SUB_STATUS_CREATE=6;
	public static final int TOTAL_HEADERS_SUB_SUB_STATUS_CREATE=5;
	public static final int SOURCE_TYPE_CALL_CENTER = 8;
	public static final int SUB_SOURCE_TYPE_CALL_CENTER = 10;
	
	public static final int SOURCE_TYPE_BULK_UPLOAD = 2;
	public static final int SOURCE_TYPE_OUTBOUND = 9;
	
	public static final String SUPER_ADMIN  =  "1";
	public static final String CIRCLE_ADMIN =  "2";
	public static final String CIRCLE_USER  =  "3";
	public static final String CIRCLE_CSR   =  "4";
	public static final String LOB_ADMIN    =  "5";
	public static final String CATEGORY_CSR =  "6";
	public static final String REPORT_ADMIN =  "7";
	public static final String TSG_USER     =  "8";
	public static final String PAN_INDIA_SCROLLER = "1";
	public static final String TOP_LINKS = "1";
	public static final String BULK_USER_CREATION = "1";
	public static final String BULK_USER_UPDATION = "2";
	public static final String BULK_USER_DELETION = "3";
	
	public static final String LEAD_STATUS_VERIFICATION = "300";
	
	public static final String LEAD_STATUS_ID_DISPOSITION= "720";	
	
	public static final String LEAD_STATUS_QUALIFICATION = "QUALIFICATION";
	public static final String LEAD_STATUS_DISPOSITION= "INVALID_LEAD";	
	
	public static final String LEAD_STATUS_QUALIFIED = "305";
	public static final String LEAD_STATUS_OPEN = "200";
	
	//added by beeru on  28 Augst
	public static final int KM_SUPERADMIN_ACTOR_ID = 1;
	public static final int KM_PANINDIA_USER_ACTOR_ID = 2;
	public static final int KM_ACTOR_ID = 3;
	public static final int KM_USERCREATOR_ACTOR_ID = 16;
	
	public static final int CIRCLE_TABLE_ID = -2;
	
//	public static final String LEAD_STATUS_QUALIFICATION = "300";
//	public static final String LEAD_STATUS_DISPOSITION= "710";
	
	public static final int ASSIGNMENT_SOFT_DELETE = 0;
	public static final int ASSIGNMENT_REASSIGN = 1;
	public static final String ACTIVE_ASSIGNMENT = "A";
	public static final String DEACTIVE_ASSIGNMENT = "D";
	public static final int PRIMARY_ASSIGNMENT = 1;
	public static final int SECONDARY_ASSIGNMENT = 0;
	//public static final int TOTAL_HEADERS_BULK_OPEN_LEAD_REGISTARTION = 9;
	public static final int TOTAL_HEADERS_BULK_OPEN_LEAD_REGISTARTION = 12;// changed by Neetika as Zone_CODE is added new column for Open leads// again changed by pratap as compaing is added in excel sheet
	public static final int TOTAL_HEADERS_BULK_LEAD_ASSIGNMENT = 13;
	public static final int TOTAL_HEADERS_BULK_USER_CREATION = 13;//changed by sugandha for bulk user creation LMS-phase-2
	public static final int TOTAL_HEADERS_BULK_QUALIFIED_LEAD_REGISTARTION = 19 ;//changed from 15 to 14 by Neetika Zonecode and RSU code removed , added cityzonecode.. // again changed by pratap for campaign field added  
	
	
	public static final int TOTAL_HEADERS_BULK_ASSIGNMENT_NEW = 17;
	public static final int PRODUCT_LOB_ID=2;
	public static final int SERVICE_MSG_SUCCESS = 1;
	public static final int SERVICE_MSG_FAIL = 2;
	
	public static final int LEAD_INSERT_NEW_LEAD = 1;
	public static final int LEAD_INSERT_EXISTING_LEAD = 2;
	public static final int LEAD_INSERT_FAIL = 3;
	
	public static final int INVALID_EXCEL = 3;
	public static final int SERVICE_MSG_ERROR = 4;
	public static final int SERVICE_PENDING_ERROR = 30;
	public static final int BLANK_EXCEL = 5;
	public static final String LEAD_CROSS_SELLING = "Cross Selling";
	public static final int CIRCLE_ID_PAN_INDIA = 0;
	
	/* Added By Parnika */	
	public static final int INVALID_FILESIZE = 15;	
	/* End of changes by Parnika */
	//added by aman
	public static final int TOTAL_HEADERS_BULK_ZONE_CREATE = 4;
	//public static final int BULK_ZONE_MESSAGE_SUCCESS = 1;
	public static final int BULK_UPLOAD_INVALID_EXCEL = 0;
	public static final int BULK_UPLOAD_BLANK_EXCEL = 1;
	public static final int TOTAL_HEADERS_BULK_CITY_CREATE = 3;
	public static final int TOTAL_HEADERS_BULK_AUTO_ASSIGNMENT_MATRIX = 8;
	public static final int TOTAL_HEADERS_BULK_CHANNELpARTNER_CREATE = 3;
	public static final int BULK_UPLOAD_SUCCESS = 2;
	public static final int BULK_UPLOAD_FAIL = 5;
	public static final int MAX_LENGTH = 20;
	public static final int MAX_ZONE_NAME_LENGTH = 30;
	public static final int MAX_LENGTH1 = 5;
	public static final int TOTAL_HEADERS_ESCALATION_MATRIX = 4;
	
	public static final int CIRCLE_EXISTS = 5;
	
	/*Added by sugandha for bulk User Creation LMS-Phase-2 */
	public static final int Super_Admin_ID =1;
	public static final int  Panindia_User_ID = 2;
	public static final int Circle_Coordinator_ID = 3;
	public static final int ZBM_ID = 4;
	public static final int ZSM_ID= 5;
	public static final int Marketing_Head_ID =6;
	public static final int Product_Head_ID =7;
	public static final int Agency_Admin_ID =8;
	public static final int GIS_User_ID = 9;
	public static final int Channel_Partners_ID = 10;
	public static final int Manual_Campaign_Agencies_ID = 11;
	public static final int LMS_Center_Agent_ID = 12;  
	public static final int Outbound_Center_Agent_ID  = 13;
	public static final int Inbound_Center_Agent_ID = 14;
	public static final int Zone_Coordinator_ID = 15;
	public static final String CITY_ZONE_CODE_FLAG_VALUE = "CZ";
	public static final String ZONE_CODE_FLAG_VALUE = "Z";
	/*End of changes done by sugandha */
	
		/* Added by Parnika for LMS Phase 2 */

	public static final String SUPER_ADMIN_ACTOR = "1";
	public static final String PAN_INDIA_ACTOR = "2";
	public static final String CIRCLE_COORDINATOR_ACTOR = "3";
	public static final String ZBM_ACTOR = "4";
	public static final String MARKETING_HEAD_ACTOR = "5";
	public static final String ZSM_ACTOR = "6";
	public static final String PRODUCT_HEAD_ACTOR = "7";
	public static final String AGENCY_ADMIN_ACTOR = "8";
	public static final String GIS_USER_ACTOR = "9";
	public static final String CHANNEL_PARTNER_ACTOR = "10";
	public static final String MANUAL_CAMPAIGN_AGENCIES_ACTOR = "11";
	public static final String LMS_CENTER_AGENT_ACTOR = "12";
	public static final String OUTBOUND_CENTER_AGENT_ACTOR = "13";
	public static final String INBOUND_CENTER_AGENT_ACTOR = "14";
	public static final String ZONAL_COORDINATOR_ACTOR = "15";
	
	public static final String PANINDIA_CIRCLE_ID = "0";
	public static final String MOBILITY_LOB = "1";

	public static final String LEAD_STATUS_FEASIBILITY_INFO_DONE="321";
	public static final String LEAD_STATUS_UNQUALIFIED="700";
	public static final String LEAD_STATUS_ASSIGNED="400";
	public static final String LEAD_STATUS_RE_ASSIGNED="401";
	public static final String LEAD_STATUS_WON="500";
	public static final String LEAD_STATUS_WIRED="311";
	public static final String LEAD_STATUS_UNWIRED="315";
	public static final String LEAD_STATUS_INFO_INADEQUATE="320";
	public static final String LEAD_SUB_STATUS_DIALER_FAIL="210";
	public static final String LEAD_SUB_STATUS_CONTACT="800";
	public static final String LEAD_SUB_STATUS_CALLBACK = "350";
	public static final String LEAD_SUB_STATUS_NOT_MET = "403";
	public static final String LEAD_SUB_STATUS_YET_TO_GET_FEEDBACK = "404";
	public static final String LEAD_SUB_STATUS_FOLLOW_UP = "405";
	public static final String LEAD_STATUS_DIALER_SECOND_CALL="370";
	public static final String LEAD_STATUS_FEASIBILITY="310";
	public static final String LEAD_STATUS_FEASIBILITY_CALL="390";
	public static final String LEAD_SUB_STATUS_LOST = "600";
	public static final String LEAD_STATUS_AUTO_CLOSE="650";
	
	
	/* End of changes by Parnika */
	/* added by pratap * */
	public static final String NON_SERVICEABLE_PINCODE="NON_SERVICEABLE_PINCODE";

	
	public static final String LOB_TELEMEDIA_ID = "Telemedia";
	public static final String FEASIBILITY = "FEASIBILITY";
	public static final String WIRED = "WIRED";
	public static final String UNWIRED = "UNWIRED";
	public static final String INFO_INADEQUATE = "INFO_INADEQUATE";
	
	
	public static final String LEAD_DETAILS_LEAD_ID="Lead ID";
	public static final String LEAD_DETAILS_CUSTOMER_NAME="Customer Name";
	public static final String LEAD_DETAILS_MOBILE_NUMBER="Mobile Number";
	public static final String LEAD_DETAILS_PRODUCT_NAME="Product Name";
	public static final String LEAD_DETAILS_CIRCLE_NAME="Circle";
	public static final String LEAD_DETAILS_CITY="City";
	public static final String LEAD_DETAILS_ZONE="Zone";
	public static final String LEAD_DETAILS_PINCODE="Pincode";
	public static final String LEAD_DETAILS_LEAD_STATUS="Lead Status";
	public static final String LEAD_DETAILS_CREATE_TIME="Lead Created";
	public static final String LEAD_DETAILS_PENDING_WITH="Pending With";
	public static final String LEAD_SUBMIT_TIME="Lead Submit Time";
	

	public static final String LEAD_DETAILS_SOURCE="Source";
	public static final String LEAD_DETAILS_RSU_ID="RSU ID";

	public static final String LEAD_DETAILS_PRODUCT_LOB="Lob Name";
	public static final String LEAD_DETAILS_ZONE_NAME="Zone Name";
	public static final String LEAD_DETAILS_LEAD_SUB_STATUS="Sub Status";
	public static final String LEAD_DETAILS_TRANSACTION_TIME="Transaction Time";
	public static final String LEAD_DETAILS_PARTNER_NAME="Partner Name";
	public static final String LEAD_DETAILS_ACTOR_NAME="Actor Name";
	public static final String LEAD_DETAILS_SUB_SOURCE="Sub Source";
	public static final String LEAD_DETAILS_ADDRESS1="Address1";
	public static final String LEAD_DETAILS_ADDRESS2="Address2";
	public static final String LEAD_DETAILS_EMAIL="Email";
	public static final String CAF_NUMBER="CAF No.";
	public static final String REMARKS="Remarks";
	public static final String CAMPAIGN="Campaign";
	public static final String REFERER_PAGE="Referer Page";
	public static final String REFERER_URL="Referer URL";
	public static final String FID="FID";
	public static final String CID="CID";
	public static final String DISPOSITION="Disposition";
	public static final String CITY_ZONE="City Zone Name";
	public static final String CHANNEL_PARTNER_NAME="Channel Partner Name";
	public static final String ADDRESS="Address";
	public static final String LEADPRIORITY="Lead Priority";

	//added by aman
	public static final String CONFIGURATION_TIME="72 hour";

	
	public static final short SCORE_ONE = 1;
	public static final short SCORE_TWO = 2;
	
	/* Added by Parnika */
	public static final String QUALIFIED_LEAD = "3";
	public static final String NORMAL = "0";
	/* End of changes by parnika */
	//Added By Bhaskar
	public static final int CHANNEL_PARTNER_ID = 10;
	//added by amarjeet
	public static final int LEAD_CATEGORY_CROSSEL = 4;
	
	//added by sudhanshu
	public static final String LEAD_DETAILS_VERIFICATION_DATE = "Verification Date";
	public static final String LEAD_DETAILS_ASSIGNED_TO_CENTER_DATE = "Assigned to Center Date & Time";
	public static final String LEAD_DETAILS_CONTACT_DATE = "Contact Date & Time";
	public static final String LEAD_DETAILS_QUALIFIED_DATE = "Qualified Date & Time";
	public static final String LEAD_DETAILS_ASSIGNED_DATE = "Assigned Date & Time";
	public static final String LEAD_DETAILS_REASSIGNED = "Reassigned";
	public static final String LEAD_DETAILS_CLOSURE = "Closure";
	public static final String LEAD_DETAILS_INSERT_DATE = "Insert Date";
	
	public static  final int LEAD_PROSPECT_CUSTOMER_ADDRESS_ONE_SIZE =200;
	public static final int  ALTERNATECONTACT_NUMBER =10;
	public static final int  LANDLINE_NUMBER =11;
	public static final int  OTHER_SIZE = 25;
	public static final int COMPANY_SIZE = 50;
	public static final int	PROSPECT_SIZE =50 ;
	public static final int ID_SIZE = 30;
	public static final int GOOGLE_SIZE =255;
	public static final int PRODUCT_SIZE = 20;
	public static final int RENTAL_SIZE = 100;
	public static  final int TELIMEDIA_LOB=2;
	public static  final int PRODUCT_LOB_4G = 24;
	
	public static final int EXCEL_INDEX_A =7;
	public static final int EXCEL_INDEX_B =8;
	public static final int EXCEL_INDEX_C =9;
	public static final int EXCEL_INDEX_D =10;
	public static final int EXCEL_INDEX_E =11;
	public static final int EXCEL_INDEX_F =4;
	public static final int EXCEL_INDEX_G =29;
	public static final int EXCEL_HEADER_SIZE =25;
	public static final int EXCEL_HEADER1_SIZE=56;
	public static final int EXCEL_HEADER2_SIZE=60;
	public static final int EXCEL_HEADER_BULK_FORWARD=9;
	
	public static final String CHECKED_YES ="YES";
	
	public static final String RC_SHOW_ACTION = "SHOW_ACTION";
	public static final String RC_SHOW_REPORT = "SHOW_REPORT"; 
	public static final String RC_EDIT_REPORT_NAME = "EDIT_REPORT_NAME";
	public static final String RC_SHOW_REPORT_TYPES = "SHOW_REPORT_TYPES"; 
	public static final String RC_SHOW_ROLES = "SHOW_ROLES";
	public static final String RC_SHOW_FREQUENCY = "SHOW_FREQUENCY";
	public static final String RC_SHOW_TIMINGS = "SHOW_TIMINGS";
	public static final String RC_SHOW_EMAILS = "SHOW_EMAILS";
	public static final String RC_SHOW_LOBS = "SHOW_LOBS";
	public static final String RC_SHOW_PRODUCTS = "SHOW_PRODUCTS";
	public static final String RC_SHOW_FIDS = "SHOW_FIDS";
	public static final String RC_SHOW_COLUMNS = "SHOW_COLUMNS";
	public static final String RC_ALL_REPORTS = "ALL_REPORTS";
	public static final int TOTAL_HEADERS_BULK_WORKFLOW_ASSIGNMENT=9;
	public  static final String  ILMS_LEAD = "<ILMS-Lead-ID>";
	public static final String LEAD_PROSPECT_CUSTOMER  ="<Customer-MSISDN>";
	public static final String APPOINTMENT_DATE_SLOT_FORMAT ="dd-mm-yyyy hh:mm";
	
	
	public static final String APPOINTMENT_CANCEL="2";
	public static final String APPOINTMENT_CHOSEN="1";
	public static final String APPOINTMENT_NOTCHOSEN="3";
	public static final int lob4g=24;
	public static final int smsInfoFlag=1;
	public static final int emptyRC=0;
	
	public static final int OTP_alertID=9;
	public static final String OTP_VAR="<OTP>";
	
	
	public static final int ASSIGNMENTMATRIX_ALERTID = 10;
	public static final String olmid = "<OLM ID>";
	public static final int EMAILFLAG=2;
	public static final int SMSFLAG=1;
	public static final int BOTH=3;
	public static final int TOTAL_HEADERS_BULK_CAPABILITY_UPLOAD=3;
	

	}
