package com.ibm.nas.engine.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;



import com.ibm.nas.engine.util.Constants;
import com.ibm.nas.engine.util.StringUtils;
import com.ibm.nas.common.DBConnection;
import com.ibm.nas.common.PropertyReader;
import com.ibm.nas.dto.AgencyResponseMessage;
import com.ibm.nas.dto.webservice.LeadCaptureServiceFirstVersDO;
import com.ibm.nas.engine.dao.CaptureLeadDataDAO;
import com.ibm.nas.engine.dataobjects.AgencyCaptureLeadDO;
import com.ibm.nas.engine.dataobjects.CaptureLeadDO;
import com.ibm.nas.engine.exception.DAOException;
import com.ibm.nas.services.MasterService;
import com.ibm.nas.services.impl.MasterServiceImpl;
//import com.ibm.xtq.bcel.generic.RET;
import com.ibm.nas.sms.SendSMSXML;


public class CaptureLeadDataDAOImpl implements CaptureLeadDataDAO {
	
	//private static final Logger logger = Logger.getLogger(CaptureLeadDataDAOImpl.class);
		
		private static final Logger logger = Logger.getLogger(CaptureLeadDataDAOImpl.class);
		//static final Logger logger = Logger.getLogger("debugLogger");
//	    static final Logger leadLog = Logger.getLogger("LeadLogger");
	
	public static Map<String, String> statusMap = new HashMap<String, String>();
	public static String GET_MAP="select LEAD_STATUS_ID,LEAD_STATUS from LEAD_STATUS  ";
	
	//private static final String SQL_CHECK_PRODUCT="select PRODUCT_ID from PRODUCT_SYNONYM where ucase(PRODUCT_SYNONYM_NAME) = ?   ";
	/* Modified by Parnika for change that only products with active status should be considered */
	
	private static final String SQL_CHECK_PRODUCT="select pm.PRODUCT_ID from PRODUCT_MSTR pm , PRODUCT_SYNONYM ps where pm.PRODUCT_ID = ps.PRODUCT_ID and pm.STATUS = 'A' and ucase(PRODUCT_SYNONYM_NAME) = ?  "; 

	/* End of changes by Parnika */
	private static final String SQL_INSERT_DIRTY_LEAD="INSERT INTO DIRTY_LEAD(PROSPECT_NAME, PROSPECT_MOBILE_NUMBER, ALTERNATE_CONTACT_NUMBER, "
													+" ADDRESS1, CITY, PINCODE, CIRCLE, " 
													+" PRODUCT, EMAIL, CREATE_TIME , ERROR_MESSAGE , FID , CID, LEAD_SUB_SOURCE, CAMPAIGN, REFERER_URL, REFERER_PAGE ) " 
													+" VALUES( ? ,? ,? ,? ,? ,?,? ,? ,?,current_timestamp , ? , ? , ?, ?, ?, ?, ? )";
	
		private StringBuffer SQL_GET_DUPLICATE_LEAD = new StringBuffer("");

	
	/* End of changes by Parnika */
	
	private static final String SQL_GET_MULTIPLE_LEAD="select PROSPECT_ID from LEAD_PROSPECT_CUSTOMER where PROSPECT_MOBILE_NUMBER = ?   ";
	
	private static final String SQL_INSERT_DUPLICATE_LEAD="INSERT INTO DUPLICATE_LEAD(PROSPECT_NAME, PROSPECT_MOBILE_NUMBER, ALTERNATE_CONTACT_NUMBER, "
												+" ADDRESS1, CITY, PINCODE, CIRCLE, " 
												+" PRODUCT, EMAIL, CREATE_TIME , FID , CID ,LEAD_SUB_SOURCE, CAMPAIGN, REFERER_URL, REFERER_PAGE  ) " 
												+" VALUES( ? ,? ,? ,? ,? ,? ,? ,? ,?,current_timestamp , ? , ? , ?, ?, ?, ? )";

	private static final String SEQ_NAME_LEAD_ID="SEQ_LEAD_ID";
	private static final String SEQ_NAME_PROSPECTS_ID="SEQ_PROSPECTS_ID";
	/* Commented By Parnika 
	private static final String SELECT_LEAD_PROSPECT_ID="Select LEAD_PROSPECT_ID from LEAD_PROSPECT_DETAIL where PROSPECT_ID=? and PRODUCT_LOB_ID=?   ";//Added by neetika on 20 nov	
	 */
	private static final String SELECT_LEAD_PROSPECT_ID="Select LEAD_PROSPECT_ID from LEAD_PROSPECT_DETAIL where LEAD_ID=?   ";//Added by neetika on 20 nov
	
	/* Commented By Parnika 
	private static final String SQL_INSERT_LEAD_DATA="INSERT INTO LEAD_DATA(PRODUCT_ID, LEAD_STATUS_ID, " +
			" LEAD_ID, PROSPECT_ID, CREATE_TIME,  FID, CID, FROM_PAGE, REFERER_PAGE, " +
			" REFERER_URL, SERVICE, PLAN , KEYWORD , CAMPAIGN , SUB_SOURCE,SOURCE ,LEAD_PROSPECT_ID,UPDATED_DT,UPDATED_BY) " +
			" VALUES(?,?,?,?,current_timestamp,?,?,?,?,?,?,? , ? , ? ,? , ? ,?,current timestamp,?)" ;// 3 columns added by Neetika for LMS phase 2
			
	End of commented By Parnika */
	
	/* Modified By Parnika for Capturing Extra Parameters */
	
	private static final String SQL_INSERT_LEAD_DATA="INSERT INTO LEAD_DATA(PRODUCT_ID, LEAD_STATUS_ID, " +
	" LEAD_ID, PROSPECT_ID, CREATE_TIME,  FID, CID, FROM_PAGE, REFERER_PAGE, " +
	" REFERER_URL, SERVICE, PLAN , KEYWORD , CAMPAIGN , SUB_SOURCE,SOURCE ,LEAD_PROSPECT_ID,UPDATED_DT,UPDATED_BY, ONLINE_CAF_NO, ALLOCATED_NO, LEAD_SUBMIT_TIME, LEAD_CATEGORY, UTM_SOURCE, UTM_TERM, UTM_MEDIUM, UTM_CAMPAIGN) " +
	" VALUES(?,?,?,?,current timestamp,?,?,?,?,?,?,? , ? , ? ,? , ? ,?,current timestamp,?, ?, ?, ?, ?, ?, ?, ?, ?)" ;
	
	/* End Of Modified By Parnika */

	
	private static final String GET_PRODUCT_ID="select b.PRODUCT_ID as  PRODUCT_ID,c.PRODUCT_LOB_ID as PRODUCT_LOB_ID  "
											+" from PRODUCT_SYNONYM a,PRODUCT_MSTR b,PRODUCT_LOB c "
											+" where ucase(PRODUCT_SYNONYM_NAME)=? "
											+" and a.PRODUCT_ID=b.PRODUCT_ID "
											+" and b.PRODUCT_LOB_ID=c.PRODUCT_LOB_ID "
											+"  ";
	
	private static final String GET_CIRCLE_ID="select CIRCLE_ID from CIRCLE_MSTR  where ucase(CIRCLE_NAME)=? AND LOB_ID=?  ";  // changed this query Neetika

	/* Changes By Parnika for getting City Name */
	
	private static final String GET_CITY_ID="select cm.CITY_CODE ,cm.ZONE_CODE from CITY_MSTR cm , ZONE_MSTR zm, CIRCLE_MSTR cim where ucase(cm.CITY_NAME)= ? and cm.zone_code = zm.zone_code and zm.circle_mstr_id = cim.circle_mstr_id and cim.lob_id = ? AND CIM.CIRCLE_ID = ?   "; 

	/* End of changes by Parnika */

	private static final String GET_Source_ID="select SOURCE_ID from SOURCE_MSTR  where ucase(SOURCE_NAME)=?  ";

	/*private static final String SQL_INSERT_PROSPECTS_CUST="INSERT INTO LEAD_PROSPECT_CUSTOMER(CUSTOMER_NAME, CITY," +
			"  EMAIL, PROSPECT_MOBILE_NUMBER, CIRCLE_ID, ADDRESS1, PINCODE, " +
			" ALTERNATE_CONTACT_NUMBER, ADDRESS2, PROSPECT_ID, CUSTOMER_FNAME, " +
			" CUSTOMER_LNAME, COMPANY, IS_CUSTOMER,UPDATED_DT,UPDATED_BY)  VALUES(?,?,? ,?,?,?,?,?,?,?,?,?,?,?,current timestamp,'"+Constants.LEAD_UPDATED_BY+"')"; //2 columns aadded by neetika on 20 nov for phase -2
*/	
	private static final String SQL_INSERT_PROSPECTS_CUST="INSERT INTO LEAD_PROSPECT_CUSTOMER(CUSTOMER_NAME, " +
	"  EMAIL, PROSPECT_MOBILE_NUMBER,   " +
	" ALTERNATE_CONTACT_NUMBER,  PROSPECT_ID, CUSTOMER_FNAME, " +
	" CUSTOMER_LNAME, COMPANY, UPDATED_DT,UPDATED_BY)  VALUES(?,?,?,?,?,?,?,?,current timestamp,'"+Constants.LEAD_UPDATED_BY+"')"; //2 columns aadded by neetika on 20 nov for phase -2
	
	/* Modified By Parnika for Capturing Extra Parameters */
	
	/* Commented by Parnika 
	private static final String INSERT_LEAD_PROSPECT_DETAIL =" INSERT INTO LEAD_PROSPECT_DETAIL (CITY_CODE, CIRCLE_ID, ADDRESS2,PINCODE, ALTERNATE_CONTACT_NUMBER, ADDRESS1, PROSPECT_ID, IS_CUSTOMER, UPDATED_DT, UPDATED_BY,PRODUCT_LOB_ID)"+
	 " VALUES( ?, ?, ?, ?, ?, ?, ?, ?,  current timestamp, ?,?)"; //Query added by neetika  */
	
	private static final String INSERT_LEAD_PROSPECT_DETAIL =" INSERT INTO LEAD_PROSPECT_DETAIL (CITY_CODE, CIRCLE_ID, ADDRESS2,PINCODE, ALTERNATE_CONTACT_NUMBER, ADDRESS1, PROSPECT_ID, IS_CUSTOMER, UPDATED_DT, UPDATED_BY," +
			"PRODUCT_LOB_ID, ZONE_CODE, CITY_ZONE_CODE, RENTAL, MYOP_ID, PYT_AMT, TRAN_REFNO, QUAL_LEAD_PARAM, AD_PARAMETER, EXTRA_PARAMS_2, EXTRA_PARAMS_3, GEO_IP_CITY, TAG, LEAD_ID)"+
	 " VALUES( ?, ?, ?, ?, ?, ?, ?, ?,  current timestamp, ?,?,?,?,?,?,?,?,?,?,?,?,?,? ,?)";
	
	/* End of changes by Parnika */
	
	private static final String SQL_INSERT_LEAD_TRANSACTION="INSERT INTO LEAD_TRANSACTION(PRODUCT_ID, LEAD_ASSIGNMENT_TIME, LEAD_STATUS_ID, LEAD_ID,TRANSACTION_TIME , SUB_STATUS_ID , UPDATED_BY, LEAD_PRODUCT_ID ) " 
														    +" VALUES( ? , current_timestamp , ? , ? , current_timestamp , ? , ?,? )";//1 column added by Neetika
	
	/* Added by Parnika for LMS Phase 2 */
	
	private static final String SELECT_AUTO_ASSIGN_LEAD = "SELECT * FROM AUTO_ASSIGNMENT_MATRIX WHERE LOB_ID = ? AND PRODUCT_ID = ? AND CIRCLE_ID = ? AND CITY_CODE = ? AND STATUS = 'A'  ";
	
	private static final String SQL_GET_MULTIPLE_LEAD_FROM_DETAIL = "SELECT LEAD_PROSPECT_ID FROM LEAD_PROSPECT_DETAIL WHERE PROSPECT_ID = ? AND PRODUCT_LOB_ID = (SELECT PRODUCT_LOB_ID FROM PRODUCT_MSTR WHERE PRODUCT_ID = ? )  ";
	
	private static final String GET_CITY_ZONE_CODE_FROM_CITY="select CZM.CITY_ZONE_CODE  from CITY_ZONE_MSTR CZM , CITY_MSTR CM where ucase(CZM.CITY_ZONE_NAME)= ? and CZM.CITY_CODE = CM.CITY_CODE and czm.CITY_CODE = ?  "; 

	private static final String GET_CITY_ZONE_CODE_FROM_CIRCLE="SELECT CZM.CITY_ZONE_CODE, CZM.CITY_CODE, CM.ZONE_CODE FROM CITY_ZONE_MSTR CZM, CITY_MSTR CM, ZONE_MSTR ZM , CIRCLE_MSTR CIM WHERE ucase(CZM.CITY_ZONE_NAME)= ? AND CZM.CITY_CODE = CM.CITY_CODE AND CM.ZONE_CODE = ZM.ZONE_CODE AND ZM.CIRCLE_MSTR_ID = CIM.CIRCLE_MSTR_ID AND CIM.LOB_ID = ? AND CIM.CIRCLE_ID = ?  "; 

	private static final String GET_ZONE_CODE_FROM_CIRCLE="SELECT ZM.ZONE_CODE FROM  ZONE_MSTR ZM ,CIRCLE_MSTR CM WHERE ucase(ZM.ZONE_NAME)= ? AND  ZM.CIRCLE_MSTR_ID = CM.CIRCLE_MSTR_ID AND CM.LOB_ID = ? AND CM.CIRCLE_ID = ?  "; 

	private static final String GET_ALL_FROM_PINCODE= "SELECT pm.PINCODE, pm.CITY_ZONE_CODE,  czm.CITY_CODE, cim.ZONE_CODE, zm.CIRCLE_MSTR_ID FROM PINCODE_MSTR pm, CITY_ZONE_MSTR czm, CITY_MSTR cim, ZONE_MSTR zm, CIRCLE_MSTR cm WHERE pm.CITY_ZONE_CODE = czm.CITY_ZONE_CODE AND czm.CITY_CODE = cim.CITY_CODE AND cim.ZONE_CODE = zm.ZONE_CODE AND zm.CIRCLE_MSTR_ID = cm.CIRCLE_MSTR_ID AND pm.PINCODE = ? AND cm.CIRCLE_ID = ? AND cm.LOB_ID = ?  "; 

	private static final String UPDATE_LEAD_PROSPECT_CUSTOMER_EMAIL ="UPDATE LEAD_PROSPECT_CUSTOMER SET EMAIL =?  WHERE PROSPECT_ID =?   ";
	
	private static final String FIND_MAX_LEAD_CAPTURED_ID =" SELECT MAX(LEAD_CAPTURED_DATA_ID) AS LEAD_CAPTURED_DATA_ID  FROM LEAD_CAPTURE  ";
	private static final String INSERT_LEAD_CAPTURED_DATA = "INSERT INTO LEAD_CAPTURE(PROSPECT_NAME, PROSPECT_MOBILE_NUMBER, ALTERNATE_CONTACT_NUMBER, LANDLINE_NUMBER, ADDRESS1, ADDRESS2, CITY, PINCODE, STATE, CIRCLE, MARITAL_STATUS, SOURCE, SUB_SOURCE, REQUEST_CATEGORY, APPOINTMENT_TIME, OPPORTUNITY_TIME, PRODUCT, EMAIL, PRIMARY_LANGUAGE, SUB_ZONE, ZONE, REMARKS, CAMPAIGN, REFER_PAGE, QUAL_LEAD_PARAM, GEO_IP_CITY, LEAD_SUBMIT_TIME, T_ID, F_ID, KEYWORD, FROM_PAGE, SERVICE, " +
			"TAG, IS_CUSTOMER, AD_PARAMETER, UTM_LABELS, REFER_URL, COMPANY, CITY_ZONE_CODE, PLAN, RENTAL, ALLOCATED_NO," +
			" ONLINE_CAF_NO, PYT_AMT, TRAN_REF_NO, AGENCY_ID, NDNC_DISCLAIMER, IP_ADDRESS, UD_ID, CITY_ID, CIRCLE_ID," +
			" FEASIBILITY_PARAM, PLAN_ID, CUSTOMER_INFO_ID, RENTAL_TYPE, FREEBIE_COUNT, BOOSTER_COUNT, FREEBIE_TAKEN, " +
			"BOOSTER_TAKEN, FLAG, PREPAID_NUMBER, OFFER, DOWNLOAD_LIMIT, DEVICE_MRP, VOICE_BENEFIT, DATA_QUOTA, USER_TYPE, DEVICE_TAKEN, BENEFIT, PKG_DURATION, HLR_NO, RSU_CODE," +
			" DOB,LC_ISDONE,EXTRA_PARAM1,EXTRA_PARAM2,EXTRA_PARAM3,EXTRA_PARAM4,EXTRA_PARAM5,EXTRA_PARAM6,EXTRA_PARAM7) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,'1',?,?,?,?,?,?,?)   ";
	public static final String INSERT_OTHER_LEAD_DETAILS = "INSERT INTO LEAD_DETAILS(LEAD_ID)VALUES(?)  ";

	public static final String INSERT_LEAD_CAPTUREDATA_DATA_VES="INSERT INTO LEAD_CAPTURE_DATA(LEAD_CAPTURED_DATA_ID, SIM_NUM�, CUSTOMER_SEGMENT, RELATION_NAME, NATIONALITY�, IDENTITYPROOFTYPE, IDENTITYPROOFID, GENDER, PAYMENT_DATE, UPC, UPC_GEN_DATE�, PREVIOUS_OPERATOR, PREVIOUS_CIRCLE, EXISTING_PART, " +
			"MNP_STATUS, DOC_COLLECTED_FLAG, PLAN_TYPE, RENTAL_PLAN, APPOINMENT_ENDTIME,CREATE_TIME) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, current_timestamp)";
	

	public static final String SELECT_CREATE_LEAD_ID="select Max(LEAD_CAPTURED_DATA_ID) as LEAD_CAPTURED_DATA_ID from LEAD_CAPTURE  ";
	
	public static Map getStatusMap()
	{
		//logger.info("Calling getStatusMap function for populating status");
		Map<String, String> map = new HashMap<String, String>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs=null;
		try
		{ 
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(GET_MAP);
			rs=ps.executeQuery();
			while(rs.next())
			{
				map.put(rs.getString("LEAD_STATUS"), rs.getString("LEAD_STATUS_ID"));
			}
		}
		catch(Exception ex)
		{
			logger.info("Exception::"+ex);
			
		}
		finally
		{
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch(Exception ex)
			{
				logger.info(ex);
			}
		}
		return map;
	}														
	public static void main(String[] args) throws DAOException {
		PropertyConfigurator.configure("log4j.properties");
		 
		CaptureLeadDO obj=new CaptureLeadDO();
		CaptureLeadDO obj1=new CaptureLeadDO();
		obj.setProspectsMobileNumber("745698243242342347");
		obj.setCircle("Rajasthan");
		obj.setProduct("am synonym");
		obj.setUtmLabels("234234eg8gasvds67rteqw6t 6dfg67drt6wetbtr5wertwe5rb5r7wefg57sf7");
		obj.setKeyWord("1111");
		obj.setCity("Ahmedabad");
		obj.setFid("FIDddddd");
		obj.setTid("TIDDDDDD");
		/*obj.setZone("Hyderabad Zone 1");
		obj.setSource("Outbound Center");
		obj.setSubSource("airtel.in Website");*/
		
		//obj.setProspectsName("naveen");
		//obj.setAddress1("ggn");
		//obj.setAgencyId("010");
		obj.setProduct("Mobility");
		//obj.setCircle("UPE");
		//obj.setState("delhi");
		//obj.setPinecode("123456");
		obj1.setProspectsMobileNumber("8562541251");
		obj1.setCity("Gurgaon");
		/*obj1.setZone("Noida Zone");
		obj1.setSource("Cross Selling");
		obj1.setSubSource("Cold Calling");*/
		obj1.setCircle("M&G");
		obj1.setCampaign("2222222");
		obj1.setProduct("4G LTE");
		
		CaptureLeadDO[] leadData=new CaptureLeadDO[2];
		leadData[0]=obj;
		leadData[1]=obj1;
		new CaptureLeadDataDAOImpl().captureLeadData(leadData);
		
	}
	
	public boolean captureLeadData(CaptureLeadDO[] leadData) throws DAOException
	{
		//logger.info("-------------started method captureLeadData-----------------");
		Connection con = null;
		CaptureLeadDO leadDtoObj=null;
		PreparedStatement ps = null;
		PreparedStatement psDirtyLead = null;
		ResultSet rs = null;
		
		PreparedStatement psDuplicate= null;
		PreparedStatement psDuplicateInsert= null;
		ResultSet rsDuplicate = null;
		
		PreparedStatement psProduct= null;
		ResultSet rsProduct = null;
		
		PreparedStatement psCircle= null;
		ResultSet rsCircle = null;
		PreparedStatement psLeadData= null;
		PreparedStatement psProspectsData= null;
		
		PreparedStatement psMultiple= null;
		ResultSet rsMultiple= null;
		PreparedStatement psTrans= null;
		
		ResultSet rsCity= null;
		PreparedStatement psCity= null;
		
		PreparedStatement psSource = null;
		ResultSet rsSource = null;
		
		PreparedStatement pstmtdetails=null;
		PreparedStatement pstmtselect=null;
		
		PreparedStatement psLeadMultiple= null;
		ResultSet rsLeadMultiple = null;
		
		PreparedStatement psCityZone= null;
		ResultSet rsCityZone = null;
		
		PreparedStatement psCityZoneFromCircle= null;
		ResultSet rsCityZoneFromCircle = null;
		
		PreparedStatement psZone= null;
		ResultSet rsZone = null;
		
		PreparedStatement psPincode= null;
		ResultSet rsPincode= null;
		  CallableStatement cs = null;
		//added By Beeru
		PreparedStatement psLeadProspectdt= null;
		PreparedStatement psLeadDetails= null;

		
		//PreparedStatement psSubSourceInsert = null;
		
		//PreparedStatement psSubSourceMax = null;
		//ResultSet rsSubSourceMax = null;
		
		/* Added by Parnika for modification of duplicate logic*/
		  
	        PreparedStatement psSelectSms=null;
	        PreparedStatement psInsertSms=null;
	        ResultSet rsSelectSms=null;
	        MasterService mstrService = new MasterServiceImpl();
		
		if (PropertyReader.getAppValue("duplicate.onzee.logic").trim().equalsIgnoreCase("Y") ){		
					SQL_GET_DUPLICATE_LEAD.append("select PROSPECT_MOBILE_NUMBER from LEAD_DATA a,LEAD_PROSPECT_CUSTOMER b,PRODUCT_SYNONYM c "
					+" where a.PROSPECT_ID=b.PROSPECT_ID" 
					+" and a.PRODUCT_ID=c.PRODUCT_ID "
					+" and b.PROSPECT_MOBILE_NUMBER =? ");
			
			if (PropertyReader.getAppValue("duplicate.onzee.daywise").trim().equalsIgnoreCase("Y") ){
				SQL_GET_DUPLICATE_LEAD.append("AND date(a.CREATE_TIME) >= current date - ? days ");
				
				if (PropertyReader.getAppValue("duplicate.onzee.productwise").trim().equalsIgnoreCase("Y") ){
					SQL_GET_DUPLICATE_LEAD.append(" and ucase(c.PRODUCT_SYNONYM_NAME) = ? ");
				}
				
			}
			else if (PropertyReader.getAppValue("duplicate.onzee.productwise").trim().equalsIgnoreCase("Y") ){
					SQL_GET_DUPLICATE_LEAD.append(" and ucase(c.PRODUCT_SYNONYM_NAME) = ? ");
				}
			SQL_GET_DUPLICATE_LEAD.append("  ");
			logger.info("Query for Duplicate Logic with ONZEE is "+SQL_GET_DUPLICATE_LEAD);
			System.out.println("Query for Duplicate Logic with ONZEE is "+SQL_GET_DUPLICATE_LEAD);
				
			}	
		/* End of changes by Parnika */
		
		boolean flag=true;
		boolean dirtyleadFlag=false;
		String message="";
		String productId="";
		String PRODUCT_LOB_ID="";
		int circleId=-1;
		String leadStatus="";
		StringBuilder sb = new StringBuilder();
		String leadId = "";
		String prospectsId="";
		String cityId="";
		
		int src=0;
		String subSrc="";
		//int subSourceId=0;
		
		boolean multipleLeadFlag=false;
		
		boolean multipleCustomerFlag=false;
		boolean autoAssign = false;
		String leadProspectId = "";
		String zoneCode="";
		String cityZoneCode = "";
		
		/* Added by Parnika */	
		String utmSource = null;
		String utmTerm = null;
		String utmMedium = null;
		String utmCampaign = null;		
		/* End of changes by Parnika */
		try
		{ 
			//logger.info("11111111111111111");
			statusMap = getStatusMap();
			con = DBConnection.getDBConnection();
			//logger.info("44444444444");
			ps = con.prepareStatement(SQL_CHECK_PRODUCT);
			psDirtyLead = con.prepareStatement(SQL_INSERT_DIRTY_LEAD);
			if (PropertyReader.getAppValue("duplicate.onzee.logic").trim().equalsIgnoreCase("Y") ){	
				psDuplicate =  con.prepareStatement(SQL_GET_DUPLICATE_LEAD.toString());
			}
			psDuplicateInsert=con.prepareStatement(SQL_INSERT_DUPLICATE_LEAD);
			psProduct = con.prepareStatement(GET_PRODUCT_ID);
			psCircle = con.prepareStatement(GET_CIRCLE_ID);
			psLeadData= con.prepareStatement(SQL_INSERT_LEAD_DATA);
			psProspectsData = con.prepareStatement(SQL_INSERT_PROSPECTS_CUST);
			pstmtdetails=con.prepareStatement(INSERT_LEAD_PROSPECT_DETAIL);//Added by neetika
			psMultiple = con.prepareStatement(SQL_GET_MULTIPLE_LEAD);
			psTrans = con.prepareStatement(SQL_INSERT_LEAD_TRANSACTION);
			psCity = con.prepareStatement(GET_CITY_ID);
			psSource = con.prepareStatement(GET_Source_ID);
			pstmtselect = con.prepareStatement(SELECT_LEAD_PROSPECT_ID.toString());
			
			/* Added by parnika */
			
			psLeadMultiple = con.prepareStatement(SQL_GET_MULTIPLE_LEAD_FROM_DETAIL);
			psCityZone = con.prepareStatement(GET_CITY_ZONE_CODE_FROM_CITY);
			psCityZoneFromCircle = con.prepareStatement(GET_CITY_ZONE_CODE_FROM_CIRCLE);
			psZone = con.prepareStatement(GET_ZONE_CODE_FROM_CIRCLE);
			psPincode = con.prepareStatement(GET_ALL_FROM_PINCODE);
			
			psLeadProspectdt = con.prepareStatement(UPDATE_LEAD_PROSPECT_CUSTOMER_EMAIL);
		
			psLeadDetails  = con.prepareStatement(INSERT_OTHER_LEAD_DETAILS);
			/* End of changes By parnika */
			
			//psSubSourceInsert = con.prepareStatement(INSERT_SUB_SOURCE);
			//psSubSourceMax = con.prepareStatement(MAX_SUB_SOURCE_ID);
			//logger.info("555555555");
			
			String mobileNumber="";
			if(leadData != null && leadData.length > 0) {
			
			for(int i=0; i< leadData.length ; i++)
			{
				productId="";
				PRODUCT_LOB_ID="";
				src=0;
				subSrc="";
				
				leadDtoObj = leadData[i];
				
			try {
				
				con.setAutoCommit(false);
				dirtyleadFlag = false;
				message="";
				prospectsId = "";
				cityId="";
				circleId=-1;
				leadStatus="";
				//subSourceId = 0;
				long imobileNumber =0;
				long iPinCode =0;
				multipleCustomerFlag = false;
				multipleLeadFlag=false;
				leadProspectId = "";
				
				mobileNumber = leadDtoObj.getProspectsMobileNumber();
				// cheking for mobile number validation*************************
				logger.info("mobileNumber::"+mobileNumber + "  Product::" + leadDtoObj.getProduct() );

				if(mobileNumber != null && mobileNumber.startsWith("0") )
				{
					mobileNumber = mobileNumber.substring(1, mobileNumber.length());
				}
				if(mobileNumber == null || mobileNumber.length() != 10) // changed by beeru
				{
					/*** validation failed for Mobile number. so this is dirty lead*****/
					dirtyleadFlag = true;
					message = Constants.MSG_INVALID_MOBILE;
				}
				if(mobileNumber != null && mobileNumber.length() == 10) {
					try {
						imobileNumber =  Long.parseLong(mobileNumber+"");
					}
					catch(Exception ex)
					{
						logger.error("mobileNumber is not valid::" + ex);
						dirtyleadFlag = true;
						message = Constants.MSG_INVALID_MOBILE;
					}
				}
				/*if(leadDtoObj.getPinecode() == null || leadDtoObj.getPinecode().length() != 6)
				{
					logger.info("getPinecode is not valid::");
					dirtyleadFlag = true;
					message = Constants.MSG_INVALID_PIN;
				}*/
				if(leadDtoObj.getPinecode() != null ) {
					try {
						iPinCode =  Long.parseLong(leadDtoObj.getPinecode());
					}
					catch(Exception ex)
					{
						leadDtoObj.setPinecode(null);
						logger.error("iPinCode is not valid::");
						//dirtyleadFlag = true;
						//message = Constants.MSG_INVALID_PIN;
					}
				}
				
				/* Added By Parnika for UTM Label in Dirty Duplicate */
				
				subSrc = leadDtoObj.getUtmLabels();
				if(subSrc != null && !(subSrc.equals("")) && subSrc.contains("Source:") && subSrc.contains("|Term:") && subSrc.contains("|Medium:") && subSrc.contains("|Campaign:")){
					
					int startSource = subSrc.indexOf("Source:");
					int startTerm = subSrc.indexOf("|Term:");
					
					// Source: airtel_sitelinks_dr_google_search |Term:  |Medium: cpc |Campaign: airtel_sitelinks
					utmSource = subSrc.substring(startSource+7, startTerm);
					
			
				}
				/* End of changes by Parnika */	
				if(!dirtyleadFlag)
				{
					//check for product
					ps.setString(1, leadDtoObj.getProduct().toUpperCase());
				//	logger.info(leadDtoObj.getProduct().toUpperCase());
					rs = ps.executeQuery();
					if(rs.next())
					{
					//	logger.info("if product");
						productId = rs.getString("PRODUCT_ID");
					}
					else
					{
						//logger.info("else product");
						/*** validation failed for Product. so this is dirty lead*****/
						dirtyleadFlag = true;
						message = Constants.MSG_INVALID_PODUCT;
					}
					DBConnection.releaseResources(null, null, rs);
				}
				logger.info("dirtyleadFlag::::::::::"+dirtyleadFlag);
				logger.info("message::::::::::"+message);
				if(dirtyleadFlag)
				{
					//logger.info("Inserting Dirty lead");
					psDirtyLead.setString(1, leadDtoObj.getProspectsName());
					psDirtyLead.setString(2, mobileNumber);
					psDirtyLead.setString(3, leadDtoObj.getAlternateContactNumber());
					//psDirtyLead.setString(4, leadDtoObj.getLandlineNumber());
					psDirtyLead.setString(4, leadDtoObj.getAddress());
					//psDirtyLead.setString(5, leadDtoObj.getAddress2());
					psDirtyLead.setString(5, leadDtoObj.getCity());
					psDirtyLead.setString(6, leadDtoObj.getPinecode());
					//psDirtyLead.setString(9, leadDtoObj.getState());
					psDirtyLead.setString(7, leadDtoObj.getCircle());
					//psDirtyLead.setString(11, leadDtoObj.getMaritialStatus());
					//psDirtyLead.setString(12, leadDtoObj.getSource());
					//psDirtyLead.setString(13, leadDtoObj.getSubSource());
					//psDirtyLead.setString(14, leadDtoObj.getRequestType());
					//psDirtyLead.setString(15, leadDtoObj.getPreperredAppointmentTime());
					//psDirtyLead.setString(16, leadDtoObj.getOpportunityTime());
					psDirtyLead.setString(8, leadDtoObj.getProduct());
					psDirtyLead.setString(9, leadDtoObj.getEmail());
					//psDirtyLead.setString(19, leadDtoObj.getPrimaryLanguage());
					//psDirtyLead.setString(20, leadDtoObj.getSubZone());
					//psDirtyLead.setString(21, leadDtoObj.getZone());
					psDirtyLead.setString(10, message);
					psDirtyLead.setString(11, leadDtoObj.getFid());
					
					psDirtyLead.setString(12, leadDtoObj.getTid());
					/* Added By parnika on 4 June 2014 */
					psDirtyLead.setString(13, utmSource);
					psDirtyLead.setString(14, leadDtoObj.getCampaign());
					psDirtyLead.setString(15, leadDtoObj.getReferUrl());
					psDirtyLead.setString(16, leadDtoObj.getReferPage());
					/* End of changes by parnika */
					
					psDirtyLead.executeUpdate();
				}
				
				else {
					
					
					/*
					 * Check for multiple lead
					 */
					//logger.info("Check for multiple lead");
					psMultiple.setString(1, mobileNumber);
					rsMultiple=psMultiple.executeQuery();
					if(rsMultiple.next())
					{
						prospectsId = rsMultiple.getString("PROSPECT_ID");
						multipleLeadFlag = true;
						//multipleCustomerFlag = true;
					}
					DBConnection.releaseResources(null, null, rsMultiple);
					try {
					
						if(prospectsId !=null && prospectsId.length() >0 && leadDtoObj.getEmail() !=null && leadDtoObj.getEmail().length() >0) {
							psLeadProspectdt.setString(1, leadDtoObj.getEmail());
							psLeadProspectdt.setInt(2, Integer.parseInt(prospectsId));
							psLeadProspectdt.executeUpdate();
						}
						
					} catch (Exception e) {
						logger.info(prospectsId+"*******Prospect email Id  Updatetion fail due to :"+e.getMessage());// TODO: handle exception
					}
					
					
					/* Added by Parnika for LMS Phase 2 as multiple Lead cannot be determined only from Mobile Number , it should check mobile number and LOB.*/
					
					/* Commented By Parnika
					if(multipleCustomerFlag){						
						psLeadMultiple.setString(1, prospectsId);
						psLeadMultiple.setString(2, productId);
						rsLeadMultiple= psLeadMultiple.executeQuery();
						if(rsLeadMultiple.next()){
							leadProspectId = rsLeadMultiple.getString("LEAD_PROSPECT_ID");
							multipleLeadFlag = true;
						}
					} */
						
					/* End of changes By Parnika */
					
					
				/************Now check for de-duplication********************
				 */
				//logger.info("Now check for de-duplication");
			/* Modified by Parnika for changes in Duplicate Logic */

			if (PropertyReader.getAppValue("duplicate.onzee.logic").trim().equalsIgnoreCase("Y") ){	
				psDuplicate.setString(1, mobileNumber);
				
				if (PropertyReader.getAppValue("duplicate.onzee.daywise").trim().equalsIgnoreCase("Y") ){
					psDuplicate.setString(2, PropertyReader.getAppValue("duplicate.onzee.days"));
					
					if (PropertyReader.getAppValue("duplicate.onzee.productwise").trim().equalsIgnoreCase("Y") ){
						psDuplicate.setString(3, leadDtoObj.getProduct().toUpperCase());
					}
					
				}

				else if (PropertyReader.getAppValue("duplicate.onzee.productwise").trim().equalsIgnoreCase("Y") ){
					 psDuplicate.setString(2, leadDtoObj.getProduct().toUpperCase());
					}
				
				rsDuplicate=psDuplicate.executeQuery();
				
			}					
				
			/* End of modification by Parnika */
			if (PropertyReader.getAppValue("duplicate.onzee.logic").trim().equalsIgnoreCase("Y") ){	

				if(rsDuplicate.next())
				{
					//*******This is duplicate lead. so inserting into error table***********************/
					logger.info("This is duplicate lead. so inserting into DUPLICATE_LEAD table");
					psDuplicateInsert.setString(1, leadDtoObj.getProspectsName());
					psDuplicateInsert.setString(2, mobileNumber);
					psDuplicateInsert.setString(3, leadDtoObj.getAlternateContactNumber());
					//psDuplicateInsert.setString(4, leadDtoObj.getLandlineNumber());
					psDuplicateInsert.setString(4, leadDtoObj.getAddress());
					//psDuplicateInsert.setString(6, leadDtoObj.getAddress2());
					psDuplicateInsert.setString(5, leadDtoObj.getCity());
					psDuplicateInsert.setString(6, leadDtoObj.getPinecode());
					//psDuplicateInsert.setString(9, leadDtoObj.getState());
					psDuplicateInsert.setString(7, leadDtoObj.getCircle());
					//psDuplicateInsert.setString(11, leadDtoObj.getMaritialStatus());
					//psDuplicateInsert.setString(12, leadDtoObj.getSource());
					//psDuplicateInsert.setString(13, leadDtoObj.getSubSource());
					//psDuplicateInsert.setString(14, leadDtoObj.getRequestType());
					//psDuplicateInsert.setString(15, leadDtoObj.getPreperredAppointmentTime());
					//psDuplicateInsert.setString(16, leadDtoObj.getOpportunityTime());
					psDuplicateInsert.setString(8, leadDtoObj.getProduct());
					psDuplicateInsert.setString(9, leadDtoObj.getEmail());
					//psDuplicateInsert.setString(19, leadDtoObj.getPrimaryLanguage());
					//psDuplicateInsert.setString(20, leadDtoObj.getSubZone());
					//psDuplicateInsert.setString(21, leadDtoObj.getZone());
					
					psDuplicateInsert.setString(10, leadDtoObj.getFid());
					psDuplicateInsert.setString(11, leadDtoObj.getTid());
					
					/* Added By parnika on 4 June 2014 */
					psDuplicateInsert.setString(12, utmSource);
					psDuplicateInsert.setString(13, leadDtoObj.getCampaign());
					psDuplicateInsert.setString(14, leadDtoObj.getReferUrl());
					psDuplicateInsert.setString(15, leadDtoObj.getReferPage());
					/* End of changes by parnika */
					
					psDuplicateInsert.executeUpdate();
					dirtyleadFlag = true;
					message = "Duplicate Lead";
				}
				else
				{
					/***********This is valid lead so inserting into lead data tables*****************************/

					leadId = getLeadId(Long.parseLong(mobileNumber),con)+"";	
					
					psProduct.setString(1, leadDtoObj.getProduct().toUpperCase());
					rsProduct = psProduct.executeQuery();
					if(rsProduct.next())
					{
						productId=rsProduct.getString("PRODUCT_ID");
						PRODUCT_LOB_ID = rsProduct.getString("PRODUCT_LOB_ID");
					}
					else {
						logger.info("Invalid Product received: " + leadDtoObj.getProduct().toUpperCase() );
					}
					DBConnection.releaseResources(null, null, rsProduct);
					
					if(!multipleLeadFlag)  {
						
						/* New Prospect Customer Entry */
						prospectsId=getNextVal(SEQ_NAME_PROSPECTS_ID,con);
						
					} // End of if for Multiple Lead Flag
					
						//To find out the circle id from circle entered in webservice input 
						psCircle.setString(1, leadDtoObj.getCircle().toUpperCase());
						psCircle.setString(2, PRODUCT_LOB_ID);//Added this field as circle validation has changed now
						rsCircle= psCircle.executeQuery();
						if(rsCircle.next())
						{
							circleId=rsCircle.getInt("CIRCLE_ID");
						} else {
							logger.info("Invalid Circle received: " + leadDtoObj.getCircle() + " changing circle to Default circle." );
						}
						DBConnection.releaseResources(null, null, rsCircle);
						
					
						
						/* Adding Geo-IP City Value to City, If City is not captured */
						
						if(leadDtoObj.getCity() == null || leadDtoObj.getCity().trim().equals("")){
							leadDtoObj.setCity(leadDtoObj.getGeoIpCity());
						}
						
						//To find out the Zone-code, City Code, City-Zone Code from Pincode entered in web-service input
						if(leadDtoObj.getPinecode() != null && !(leadDtoObj.getPinecode().trim().equals(""))){
							psPincode.setString(1, leadDtoObj.getPinecode());
							psPincode.setInt(2, circleId);
							psPincode.setString(3, PRODUCT_LOB_ID);
							rsPincode = psPincode.executeQuery();
							if(rsPincode.next()){							
								zoneCode = rsPincode.getString("ZONE_CODE");
								cityId = rsPincode.getString("CITY_CODE");
								cityZoneCode = rsPincode.getString("CITY_ZONE_CODE");
							}
						}
				
						//To find out the city code from city Name entered in web-service input 
						else if(leadDtoObj.getCity() != null && !(leadDtoObj.getCity().trim().equals("")) && cityId.trim().equals("")){								
							psCity.setString(1, leadDtoObj.getCity().toUpperCase());
							psCity.setString(2, PRODUCT_LOB_ID);
							psCity.setInt(3, circleId);
							rsCity= psCity.executeQuery();
							if(rsCity.next())
							{
								cityId=rsCity.getString("CITY_CODE");
								zoneCode = rsCity.getString("ZONE_CODE");
							}
							DBConnection.releaseResources(null, null, rsCity);
													
								//To find out the city-zone code entered in web-service input 
								if(leadDtoObj.getCityZoneCode() != null && !(leadDtoObj.getCityZoneCode().trim().equals(""))){
								/* If Valid City Code is received */
								if(!cityId.trim().equals("")){
								psCityZone.setString(1, leadDtoObj.getCityZoneCode().toUpperCase());
								psCityZone.setString(2, cityId);
								rsCityZone= psCityZone.executeQuery();
								if(rsCityZone.next())
								{
									cityZoneCode=rsCityZone.getString("CITY_ZONE_CODE");
								}
								DBConnection.releaseResources(null, null, rsCityZone);
								}
								/* If Valid City Code is not received */
								else{
									
									psCityZoneFromCircle.setString(1, leadDtoObj.getCityZoneCode().toUpperCase());
									psCityZoneFromCircle.setString(2, PRODUCT_LOB_ID);
									psCityZoneFromCircle.setInt(3, circleId);
									rsCityZoneFromCircle= psCityZoneFromCircle.executeQuery();
									if(rsCityZoneFromCircle.next())
									{
										cityZoneCode=rsCityZoneFromCircle.getString("CITY_ZONE_CODE");
										cityId=rsCityZoneFromCircle.getString("CITY_CODE");
										zoneCode = rsCityZoneFromCircle.getString("ZONE_CODE");
									}
									DBConnection.releaseResources(null, null, rsCityZoneFromCircle);
									
								}
							}
						}
						else if(leadDtoObj.getCityZoneCode() != null && !(leadDtoObj.getCityZoneCode().trim().equals(""))){
									// If city Name is not available/ Valid , then reverse populating city and zone from city zone
									psCityZoneFromCircle.setString(1, leadDtoObj.getCityZoneCode().toUpperCase());
									psCityZoneFromCircle.setString(2, PRODUCT_LOB_ID);
									psCityZoneFromCircle.setInt(3, circleId);
									rsCityZoneFromCircle= psCityZoneFromCircle.executeQuery();
									if(rsCityZoneFromCircle.next())
									{
										cityZoneCode=rsCityZoneFromCircle.getString("CITY_ZONE_CODE");
										cityId=rsCityZoneFromCircle.getString("CITY_CODE");
										zoneCode = rsCityZoneFromCircle.getString("ZONE_CODE");
									}
									DBConnection.releaseResources(null, null, rsCityZoneFromCircle);	
						}
						// If City Zone Code is not Valid, then populating zone from zone Name
						else if(cityZoneCode.equals("") && leadDtoObj.getZoneCode() != null && !(leadDtoObj.getZoneCode().trim().equals("")) ){
							psZone.setString(1, leadDtoObj.getZoneCode().toUpperCase());
							psZone.setString(2, PRODUCT_LOB_ID);
							psZone.setInt(3, circleId);
							rsZone= psZone.executeQuery();
							if(rsZone.next())
							{
								zoneCode = rsZone.getString("ZONE_CODE");
							}
							DBConnection.releaseResources(null, null, rsZone);
						}
						
						/* Added by Parnika to check whether to Auto Assign the Lead */
						if (PropertyReader.getAppValue("lms.online.lead.qualified").trim().equalsIgnoreCase("Y") ){							
						autoAssign = isValidAutoAssignment(PRODUCT_LOB_ID, productId, circleId, cityId, con);						
						}
						
						//Added by Neetika for Lead prospect details table entry on 20 nov 
						/* Modified by Parnika Added within this if block instead of outside if block */
						/* Modified by Parnika Added outside if block instead of inside if block */
						
						int parameterIndex=1;
						pstmtdetails.setString(parameterIndex++,cityId);						
						//pstmtdetails.setString(parameterIndex++, leadDtoObj.getEmail());
						pstmtdetails.setInt(parameterIndex++,circleId);
						pstmtdetails.setString(parameterIndex++, leadDtoObj.getAddress());
						
						if(leadDtoObj.getPinecode() ==null || leadDtoObj.getPinecode().equals("") || leadDtoObj.getPinecode().equalsIgnoreCase("nil"))
							pstmtdetails.setString(parameterIndex++, null);
						else
							pstmtdetails.setString(parameterIndex++, leadDtoObj.getPinecode());
						
			            pstmtdetails.setString(parameterIndex++, leadDtoObj.getAlternateContactNumber());	
			          
			            pstmtdetails.setString(parameterIndex++, leadDtoObj.getAddress());
			            pstmtdetails.setString(parameterIndex++, prospectsId);
			         
			            if(leadDtoObj.getIsCustomer() != null && leadDtoObj.getIsCustomer().equalsIgnoreCase("Y"))
						{
			            	pstmtdetails.setString(parameterIndex++, leadDtoObj.getIsCustomer());
						}
						else
						{
							pstmtdetails.setString(parameterIndex++, "N");
						}
			           
			            pstmtdetails.setString(parameterIndex++,Constants.LEAD_UPDATED_BY);
			            pstmtdetails.setString(parameterIndex++,PRODUCT_LOB_ID);
			            
			            /* Added By Parnika for inserting extra Parameters in Lead Detail Table for Phase 2*/
			            
			            pstmtdetails.setString(parameterIndex++, zoneCode);
			            pstmtdetails.setString(parameterIndex++, cityZoneCode);
			            pstmtdetails.setString(parameterIndex++, leadDtoObj.getRental());
			            pstmtdetails.setString(parameterIndex++, leadDtoObj.getMyopId());
			            pstmtdetails.setString(parameterIndex++, leadDtoObj.getPytAmt());
			            pstmtdetails.setString(parameterIndex++, leadDtoObj.getTranRefno());
			            pstmtdetails.setString(parameterIndex++, leadDtoObj.getQualLeadParam());
			            pstmtdetails.setString(parameterIndex++, leadDtoObj.getAdParameter());
			            pstmtdetails.setString(parameterIndex++, leadDtoObj.getExtraParams2());
			            pstmtdetails.setString(parameterIndex++, leadDtoObj.getExtraParams3());
			            pstmtdetails.setString(parameterIndex++, leadDtoObj.getGeoIpCity());
			            pstmtdetails.setString(parameterIndex++, leadDtoObj.getTag());
			            
			            /* End of changes by Parnika */
			            
			            /* Added By Parnika on 12 May 2014 */
			            pstmtdetails.setString(parameterIndex++, leadId);			            
			            /* End of changes by Parnika */
			          		     
			            
			            pstmtdetails.executeUpdate();
			            pstmtdetails.clearParameters();
						
		            
		            int leadprospectId=0;
		            /* Commented By Parnika as Prospect Customer Detail Table is Lead Wise 
		            pstmtselect.setInt(1,Integer.parseInt(prospectsId));
		            pstmtselect.setInt(2,Integer.parseInt(PRODUCT_LOB_ID));  */
		            /* Added By Parnika on 12 May 2014 */
		            pstmtselect.setString(1,leadId);
		            /* End of changes by Parnika */
		            
					rs = pstmtselect.executeQuery();
					if(rs.next())
					leadprospectId = rs.getInt("LEAD_PROSPECT_ID");
					pstmtselect.clearParameters();
					logger.info("leadprospectId"+leadprospectId+"prospectId"+prospectsId+" lob: "+PRODUCT_LOB_ID);
		            
		           
					
					logger.info("leadId::"+leadId+" inserting into lead data table");
					
					psLeadData.setString(1, productId);
					/* Added by Parnika */
					int leadStatusId = 0;
					if(autoAssign){
						psLeadData.setString(2, statusMap.get(Constants.LEAD_STATUS_NAME_QUALIFIED));
						leadStatusId = Integer.parseInt(statusMap.get(Constants.LEAD_STATUS_NAME_QUALIFIED));
					}
					else{
						psLeadData.setString(2, statusMap.get(Constants.LEAD_STATUS_OPEN));
						leadStatusId = Integer.parseInt(statusMap.get(Constants.LEAD_STATUS_OPEN));
						
					}					
					psLeadData.setString(3, leadId);
					psLeadData.setString(4, prospectsId);
					//psLeadData.setString(7, leadDtoObj.getRequestType());
					psLeadData.setString(5, leadDtoObj.getFid());
					psLeadData.setString(6, leadDtoObj.getTid());
					psLeadData.setString(7, leadDtoObj.getFromPage());
					//psLeadData.setString(11, leadDtoObj.getProductType());
					psLeadData.setString(8, leadDtoObj.getReferPage());
					psLeadData.setString(9, leadDtoObj.getReferUrl());
					psLeadData.setString(10, leadDtoObj.getService());
					psLeadData.setString(11, leadDtoObj.getPlan());
					
					psLeadData.setString(12, leadDtoObj.getKeyWord());
					psLeadData.setString(13, leadDtoObj.getCampaign());
					
					src=0;
					subSrc="";
					
					if(leadDtoObj.getUtmLabels() != null && (leadDtoObj.getUtmLabels().equalsIgnoreCase(Constants.LEAD_SOURCE_SMS) || leadDtoObj.getUtmLabels().equalsIgnoreCase(Constants.LEAD_SOURCE_USSD)) )
					{
						psSource.setString(1, leadDtoObj.getUtmLabels().toUpperCase());
						rsSource = psSource.executeQuery();
						if(rsSource.next())
						{
							src = rsSource.getInt("SOURCE_ID");
						}
						subSrc="";
					}
					else
					{
						
						psSource.setString(1, Constants.LEAD_SOURCE_ONLINE);
						rsSource = psSource.executeQuery();
						if(rsSource.next())
						{
							src = rsSource.getInt("SOURCE_ID");
						}
						subSrc=leadDtoObj.getUtmLabels() ;
						/* Added by Parnika for LMS Phase 2 splitting of fields */
						if(subSrc != null && !(subSrc.equals("")) && subSrc.contains("Source:") && subSrc.contains("|Term:") && subSrc.contains("|Medium:") && subSrc.contains("|Campaign:")){
							
							int startSource = subSrc.indexOf("Source:");
							int startTerm = subSrc.indexOf("|Term:");
							int startMedium = subSrc.indexOf("|Medium:");
							int startCampaign = subSrc.indexOf("|Campaign:");
							
							// Source: airtel_sitelinks_dr_google_search |Term:  |Medium: cpc |Campaign: airtel_sitelinks

							utmSource = subSrc.substring(startSource+7, startTerm);
							utmTerm = subSrc.substring(startTerm+6, startMedium);
							utmMedium = subSrc.substring(startMedium+8, startCampaign);
							utmCampaign = subSrc.substring(startCampaign+10);
							
						}
						
						/* End of changes by parnika */
					}
					DBConnection.releaseResources(null, null, rsSource);
					if(subSrc != null && subSrc.length() >0) {
						psLeadData.setString(14, subSrc);
					}else {
						psLeadData.setString(14, String.valueOf(Constants.LEAD_SUB_SOURCE_OTHER));	
					}
					psLeadData.setInt(15, src);
					psLeadData.setInt(16, leadprospectId);//Added by neetika
					psLeadData.setString(17, Constants.LEAD_UPDATED_BY);//Added by neetika
					
					/* Added by Parnika */
					
					psLeadData.setString(18, leadDtoObj.getOnlineCafNo());
					psLeadData.setString(19, leadDtoObj.getAllocatedNo());
					psLeadData.setString(20, leadDtoObj.getLeadSubmitTime());
					if(autoAssign){
						psLeadData.setString(21, PropertyReader.getAppValue("lms.online.lead.auto"));
					}
					else{
						psLeadData.setString(21, PropertyReader.getAppValue("lms.online.lead.normal"));
					}
					psLeadData.setString(22, utmSource);
					psLeadData.setString(23, utmTerm);
					psLeadData.setString(24, utmMedium);
					psLeadData.setString(25, utmCampaign);
					
					/* End of changes By Parnika */
					
					psLeadData.executeUpdate();
					logger.info("Inserted Successfully into lead_data table"+PRODUCT_LOB_ID+"  productId "+productId);
					//added By Beeru in duplicate
					try {
						psLeadDetails.setString(1, leadId);
						psLeadDetails.executeUpdate();
						
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					
					psTrans.setString(1, PRODUCT_LOB_ID);
					
					/* Added By Parnika for Qualified Status in transaction table in case of Auto Assign */
					if(autoAssign){
						if (PRODUCT_LOB_ID.equalsIgnoreCase(Constants.TELEMEDIA_PRODUCT))
							psTrans.setString(2, statusMap.get(Constants.LEAD_STATUS_NAME_VERIFICATION));
						else
							psTrans.setString(2, statusMap.get(Constants.LEAD_STATUS_NAME_QUALIFIED));
					}
					else{
						psTrans.setString(2, statusMap.get(Constants.LEAD_STATUS_OPEN));
					}					
					psTrans.setString(3, leadId);
					if(autoAssign){
						if (PRODUCT_LOB_ID.equalsIgnoreCase(Constants.TELEMEDIA_PRODUCT))
							psTrans.setString(4, statusMap.get(Constants.LEAD_STATUS_NAME_VERIFICATION));
						else
							psTrans.setString(4, statusMap.get(Constants.LEAD_STATUS_NAME_QUALIFIED));
					}
					else{
						psTrans.setString(4, statusMap.get(Constants.LEAD_STATUS_OPEN));
					}
					
					/* End of changes By Parnika */
					psTrans.setString(5, Constants.LEAD_UPDATED_BY);
					psTrans.setString(6, productId);//Neetika  
					
					psTrans.executeUpdate();
					//logger.info("Inserted Successfully into lead_transaction table");
					
					/* Added by Parnika for inserting another row in Lead Transaction with 305 status in case of telemedia leads auto assignment */
					if (autoAssign && PRODUCT_LOB_ID.equalsIgnoreCase(Constants.TELEMEDIA_PRODUCT)){
						psTrans.setString(1, PRODUCT_LOB_ID);
						psTrans.setString(2, statusMap.get(Constants.LEAD_STATUS_NAME_QUALIFIED));					
						psTrans.setString(3, leadId);
						psTrans.setString(4, statusMap.get(Constants.LEAD_STATUS_NAME_QUALIFIED));
						psTrans.setString(5, Constants.LEAD_UPDATED_BY);
						psTrans.setString(6, productId);  						
						psTrans.executeUpdate();
					}
					/* End of changes by Parnika */
					
					if(!multipleLeadFlag)  {
						/*	psCircle.setString(1, leadDtoObj.getCircle().toUpperCase());
							rsCircle= psCircle.executeQuery();
							if(rsCircle.next())
							{
								circleId=rsCircle.getInt("CIRCLE_ID");
							} else {
								logger.info("Invalid Circle received: " + leadDtoObj.getCircle() + " changing circle to Default circle." );
							}
							DBConnection.releaseResources(null, null, rsCircle);*/
						//above code commented by neetika
							/*psState.setString(1, leadDtoObj.getState().toUpperCase());
							rsState= psState.executeQuery();
							if(rsState.next())
							{
								stateId=rsState.getString("STATE_CODE");
							}
							DBConnection.releaseResources(null, null, rsState);*/
							
							
							/*psZone.setString(1, leadDtoObj.getZone().toUpperCase());
							rsZone= psZone.executeQuery();
							if(rsZone.next())
							{
								zoneId=rsZone.getString("ZONE_CODE");
							}
							DBConnection.releaseResources(null, null, rsZone);*/
							
							
							/*psCity.setString(1, leadDtoObj.getCity().toUpperCase());
							rsCity= psCity.executeQuery();
							if(rsCity.next())
							{
								cityId=rsCity.getString("CITY_CODE");
							}
							DBConnection.releaseResources(null, null, rsCity);*/
						
						
							psProspectsData.setString(1, (leadDtoObj.getProspectsName()==null || leadDtoObj.getProspectsName()=="")?"LMSOTHER":leadDtoObj.getProspectsName());
							//psProspectsData.setString(2,cityId );
							
							/*psProspectsData.setString(3, stateId);
							psProspectsData.setString(4, zoneId);*/
							psProspectsData.setString(2, leadDtoObj.getEmail());
							psProspectsData.setString(3, mobileNumber);
							//psProspectsData.setInt(5, circleId);
							//psProspectsData.setString(6, leadDtoObj.getAddress());
							//psProspectsData.setString(9, leadDtoObj.getPrimaryLanguage());
							/*if(leadDtoObj.getPinecode() ==null || leadDtoObj.getPinecode().equals("") || leadDtoObj.getPinecode().equalsIgnoreCase("nil"))
								psProspectsData.setString(7, "0");
							else
								psProspectsData.setString(7, leadDtoObj.getPinecode());*/
							
							psProspectsData.setString(4, leadDtoObj.getAlternateContactNumber());
							//psProspectsData.setString(12, leadDtoObj.getLandlineNumber());
							
							/*if(leadDtoObj.getPreperredAppointmentTime() == null || leadDtoObj.getPreperredAppointmentTime().equals(""))
							{
								psProspectsData.setString(12, new java.sql.Timestamp(new java.util.Date().getTime())+"");
							}
							else
							{*/
							//psProspectsData.setString(13, leadDtoObj.getPreperredAppointmentTime());
							//}
							//psProspectsData.setString(9, leadDtoObj.getAddress());
							psProspectsData.setString(5, prospectsId);
							psProspectsData.setString(6, "");
							psProspectsData.setString(7, "");
							psProspectsData.setString(8, leadDtoObj.getCompany());
							/*if(leadDtoObj.getIsCustomer() != null && leadDtoObj.getIsCustomer().equalsIgnoreCase("Y"))
							{
								psProspectsData.setString(9, leadDtoObj.getIsCustomer());
							}
							else
							{
								psProspectsData.setString(9, "N");
							}*/
							
							psProspectsData.executeUpdate();
							logger.info("Inserted Successfully into lead_prospect_master table");
							
							
							
							
				}
					/* Lead Priority  */
					try {
						if (leadId != null && !leadId.equalsIgnoreCase("0")){
							
							ResourceBundle rb=ResourceBundle.getBundle("ApplicationResources");
							String schemaName=rb.getString("lms.schema.bulk.download");
							cs = con.prepareCall("{call "+schemaName+".PRC_CALULATE_SCORE(?,?,?,?,?)}");
							cs.setLong(1, (Long.parseLong(leadId)));
							if(leadStatusId==Integer.parseInt(Constants.LEAD_STATUS_OPEN_ID))
								cs.setShort(2,Constants.SCORE_ONE);
							else if(leadStatusId==Integer.parseInt(Constants.LEAD_STATUS_QUALIFIED_ID) && !(PRODUCT_LOB_ID.equalsIgnoreCase(Constants.TELEMEDIA_PRODUCT)) )
								cs.setShort(2,Constants.SCORE_TWO);
							else if(leadStatusId==Integer.parseInt(Constants.LEAD_STATUS_VERIFICATION_ID) && PRODUCT_LOB_ID.equalsIgnoreCase(Constants.TELEMEDIA_PRODUCT) )
								cs.setShort(2,Constants.SCORE_TWO);
							else
								cs.setShort(2,Constants.SCORE_ONE);
							cs.registerOutParameter(3, java.sql.Types.VARCHAR);
							cs.registerOutParameter(4, java.sql.Types.VARCHAR);
							cs.registerOutParameter(5, java.sql.Types.INTEGER);
							cs.execute();
						}
					} catch (Exception e) {
						
						e.printStackTrace();
					}
					/* End of Lead Priority */
					
					/* Added by Parnika for Sending SMS to Customer in LMS Phase2 drop 3 */
					try {
						String smsFlag = mstrService.getParameterName(PropertyReader.getAppValue("leadRegisteration.smsFlag"));
						if (leadId != null && !leadId.equalsIgnoreCase("0") && smsFlag.equalsIgnoreCase("Y") && !autoAssign){
							
				    		String SELECT_SMS_TEMPLATE = "SELECT MESSAGE_TEMPLATE FROM ALERT_MSTR  WHERE ALERT_ID =" +PropertyReader.getAppValue("lms.leadRegisteration.template")+ "  AND ACTIVE = 'A'  ";
				    		final String INSERT_SMS_TRANSACTIONS = " INSERT INTO CUSTOMER_SEND_SMS_DETAILS( MOBILE_NUMBER, MESSAGE, STATUS, SENT_ON, CREATED_ON, RESPONSE_MSG) VALUES( ?, ?, ?, CURRENT TIMESTAMP, CURRENT TIMESTAMP, ?)";
							String smsMessage = null;

					        psSelectSms = con.prepareStatement(SELECT_SMS_TEMPLATE);
					        rsSelectSms = psSelectSms.executeQuery();
							if(rsSelectSms.next()){
								smsMessage = rsSelectSms.getString("MESSAGE_TEMPLATE")+" " + leadId;							 
							}
							if(smsMessage != null && smsMessage.trim().equals("")){
								new SendSMSXML().sendSms(mobileNumber+"",smsMessage);
								psInsertSms=con.prepareStatement(INSERT_SMS_TRANSACTIONS);
								psInsertSms.setString(1, mobileNumber+"");
								psInsertSms.setString(2, smsMessage);
								psInsertSms.setInt(3, 1);
								psInsertSms.setString(4, "");								
								psInsertSms.executeUpdate();
							
							}															

							
						}				
						} catch (Exception e) {
							logger.info("Error in Sending SMS to customer for LEAD ID: "+leadId+" with Mobile Number : "+mobileNumber);
							DBConnection.releaseResources(null, psSelectSms, null);
							DBConnection.releaseResources(null, psInsertSms, null);
							e.printStackTrace();
						}
					
					/* End of changes By Parnika for sending sms to customer for Open Leads*/
				}			
 
			}
				else
				{
					/***********This is valid lead so inserting into lead data tables*****************************/

					leadId = getLeadId(Long.parseLong(mobileNumber),con)+"";	
					
					psProduct.setString(1, leadDtoObj.getProduct().toUpperCase());
					rsProduct = psProduct.executeQuery();
					if(rsProduct.next())
					{
						productId=rsProduct.getString("PRODUCT_ID");
						PRODUCT_LOB_ID = rsProduct.getString("PRODUCT_LOB_ID");
					}
					else {
						logger.info("Invalid Product received: " + leadDtoObj.getProduct().toUpperCase() );
					}
					DBConnection.releaseResources(null, null, rsProduct);
					
					if(!multipleLeadFlag)  {
						
						/* New Prospect Customer Entry */
						prospectsId=getNextVal(SEQ_NAME_PROSPECTS_ID,con);
						
						//To find out the circle id from circle entered in webservice input 
						psCircle.setString(1, leadDtoObj.getCircle().toUpperCase());
						psCircle.setString(2, PRODUCT_LOB_ID);//Added this field as circle validation has changed now
						rsCircle= psCircle.executeQuery();
						if(rsCircle.next())
						{
							circleId=rsCircle.getInt("CIRCLE_ID");
						} else {
							logger.info("Invalid Circle received: " + leadDtoObj.getCircle() + " changing circle to Default circle." );
						}
						DBConnection.releaseResources(null, null, rsCircle);
						
						/* Adding Geo-IP City Value to City, If City is not captured */
						
						if(leadDtoObj.getCity() == null || leadDtoObj.getCity().trim().equals("")){
							leadDtoObj.setCity(leadDtoObj.getGeoIpCity());
						}
						
						//To find out the Zone-code, City Code, City-Zone Code from Pincode entered in web-service input
						if(leadDtoObj.getPinecode() != null && !(leadDtoObj.getPinecode().trim().equals(""))){
							psPincode.setString(1, leadDtoObj.getPinecode());
							psPincode.setInt(2, circleId);
							psPincode.setString(3, PRODUCT_LOB_ID);
							rsPincode = psPincode.executeQuery();
							if(rsPincode.next()){							
								zoneCode = rsPincode.getString("ZONE_CODE");
								cityId = rsPincode.getString("CITY_CODE");
								cityZoneCode = rsPincode.getString("CITY_ZONE_CODE");
							}
						}
				
						//To find out the city code from city Name entered in web-service input 
						else if(leadDtoObj.getCity() != null && !(leadDtoObj.getCity().trim().equals("")) && cityId.trim().equals("")){								
							psCity.setString(1, leadDtoObj.getCity().toUpperCase());
							psCity.setString(2, PRODUCT_LOB_ID);
							psCity.setInt(3, circleId);
							rsCity= psCity.executeQuery();
							if(rsCity.next())
							{
								cityId=rsCity.getString("CITY_CODE");
								zoneCode = rsCity.getString("ZONE_CODE");
							}
							DBConnection.releaseResources(null, null, rsCity);
													
								//To find out the city-zone code entered in web-service input 
								if(leadDtoObj.getCityZoneCode() != null && !(leadDtoObj.getCityZoneCode().trim().equals(""))){
								/* If Valid City Code is received */
								if(!cityId.trim().equals("")){
								psCityZone.setString(1, leadDtoObj.getCityZoneCode().toUpperCase());
								psCityZone.setString(2, cityId);
								rsCityZone= psCityZone.executeQuery();
								if(rsCityZone.next())
								{
									cityZoneCode=rsCityZone.getString("CITY_ZONE_CODE");
								}
								DBConnection.releaseResources(null, null, rsCityZone);
								}
								/* If Valid City Code is not received */
								else{
									
									psCityZoneFromCircle.setString(1, leadDtoObj.getCityZoneCode().toUpperCase());
									psCityZoneFromCircle.setString(2, PRODUCT_LOB_ID);
									psCityZoneFromCircle.setInt(3, circleId);
									rsCityZoneFromCircle= psCityZoneFromCircle.executeQuery();
									if(rsCityZoneFromCircle.next())
									{
										cityZoneCode=rsCityZoneFromCircle.getString("CITY_ZONE_CODE");
										cityId=rsCityZoneFromCircle.getString("CITY_CODE");
										zoneCode = rsCityZoneFromCircle.getString("ZONE_CODE");
									}
									DBConnection.releaseResources(null, null, rsCityZoneFromCircle);
									
								}
							}
						}
						else if(leadDtoObj.getCityZoneCode() != null && !(leadDtoObj.getCityZoneCode().trim().equals(""))){
									// If city Name is not available/ Valid , then reverse populating city and zone from city zone
									psCityZoneFromCircle.setString(1, leadDtoObj.getCityZoneCode().toUpperCase());
									psCityZoneFromCircle.setString(2, PRODUCT_LOB_ID);
									psCityZoneFromCircle.setInt(3, circleId);
									rsCityZoneFromCircle= psCityZoneFromCircle.executeQuery();
									if(rsCityZoneFromCircle.next())
									{
										cityZoneCode=rsCityZoneFromCircle.getString("CITY_ZONE_CODE");
										cityId=rsCityZoneFromCircle.getString("CITY_CODE");
										zoneCode = rsCityZoneFromCircle.getString("ZONE_CODE");
									}
									DBConnection.releaseResources(null, null, rsCityZoneFromCircle);	
						}
						// If City Zone Code is not Valid, then populating zone from zone Name
						else if(cityZoneCode.equals("") && leadDtoObj.getZoneCode() != null && !(leadDtoObj.getZoneCode().trim().equals("")) ){
							psZone.setString(1, leadDtoObj.getZoneCode().toUpperCase());
							psZone.setString(2, PRODUCT_LOB_ID);
							psZone.setInt(3, circleId);
							rsZone= psZone.executeQuery();
							if(rsZone.next())
							{
								zoneCode = rsZone.getString("ZONE_CODE");
							}
							DBConnection.releaseResources(null, null, rsZone);
						}
						
						/* Added by Parnika to check whether to Auto Assign the Lead */
						if (PropertyReader.getAppValue("lms.online.lead.qualified").trim().equalsIgnoreCase("Y") ){							
						autoAssign = isValidAutoAssignment(PRODUCT_LOB_ID, productId, circleId, cityId, con);						
						}
						
						//Added by Neetika for Lead prospect details table entry on 20 nov 
						/* Modified by Parnika Added within this if block instead of outside if block */
						
						int parameterIndex=1;
						pstmtdetails.setString(parameterIndex++,cityId);						
						//pstmtdetails.setString(parameterIndex++, leadDtoObj.getEmail());
						pstmtdetails.setInt(parameterIndex++,circleId);
						pstmtdetails.setString(parameterIndex++, leadDtoObj.getAddress());
						
						if(leadDtoObj.getPinecode() ==null || leadDtoObj.getPinecode().equals("") || leadDtoObj.getPinecode().equalsIgnoreCase("nil"))
							pstmtdetails.setString(parameterIndex++, null);
						else
							pstmtdetails.setString(parameterIndex++, leadDtoObj.getPinecode());
						
			            pstmtdetails.setString(parameterIndex++, leadDtoObj.getAlternateContactNumber());	
			          
			            pstmtdetails.setString(parameterIndex++, leadDtoObj.getAddress());
			            pstmtdetails.setString(parameterIndex++, prospectsId);
			         
			            if(leadDtoObj.getIsCustomer() != null && leadDtoObj.getIsCustomer().equalsIgnoreCase("Y"))
						{
			            	pstmtdetails.setString(parameterIndex++, leadDtoObj.getIsCustomer());
						}
						else
						{
							pstmtdetails.setString(parameterIndex++, "N");
						}
			           
			            pstmtdetails.setString(parameterIndex++,Constants.LEAD_UPDATED_BY);
			            pstmtdetails.setString(parameterIndex++,PRODUCT_LOB_ID);
			            
			            /* Added By Parnika for inserting extra Parameters in Lead Detail Table for Phase 2*/
			            
			            pstmtdetails.setString(parameterIndex++, zoneCode);
			            pstmtdetails.setString(parameterIndex++, cityZoneCode);
			            pstmtdetails.setString(parameterIndex++, leadDtoObj.getRental());
			            pstmtdetails.setString(parameterIndex++, leadDtoObj.getMyopId());
			            pstmtdetails.setString(parameterIndex++, leadDtoObj.getPytAmt());
			            pstmtdetails.setString(parameterIndex++, leadDtoObj.getTranRefno());
			            pstmtdetails.setString(parameterIndex++, leadDtoObj.getQualLeadParam());
			            pstmtdetails.setString(parameterIndex++, leadDtoObj.getAdParameter());
			            pstmtdetails.setString(parameterIndex++, leadDtoObj.getExtraParams2());
			            pstmtdetails.setString(parameterIndex++, leadDtoObj.getExtraParams3());
			            pstmtdetails.setString(parameterIndex++, leadDtoObj.getGeoIpCity());
			            pstmtdetails.setString(parameterIndex++, leadDtoObj.getTag());
			            /* End of changes by Parnika */
			            
			            pstmtdetails.executeUpdate();
			            pstmtdetails.clearParameters();
						
					} // End of if for Multiple Lead Flag

		            
		            int leadprospectId=0;
		            pstmtselect.setInt(1,Integer.parseInt(prospectsId));
		            pstmtselect.setInt(2,Integer.parseInt(PRODUCT_LOB_ID));
					rs = pstmtselect.executeQuery();
					if(rs.next())
					leadprospectId = rs.getInt("LEAD_PROSPECT_ID");
					pstmtselect.clearParameters();
					logger.info("leadprospectId"+leadprospectId+"prospectId"+prospectsId+" lob: "+PRODUCT_LOB_ID);
		            
		            //end by NEETIKA on 20 nov
					
					/*psSource.setString(1, leadDtoObj.getSource().toUpperCase());
					rsSource= psSource.executeQuery();
					if(rsSource.next())
					{
						sourceId=rsSource.getString("SOURCE_ID");
					}
					DBConnection.releaseResources(null, null, rsSource);
					*/
					/*if(leadDtoObj.getUtm_labels() != null && !"".equals(leadDtoObj.getUtm_labels()))  { 
					psSubSource.setString(1, leadDtoObj.getUtm_labels().toUpperCase());
					rsSubSource= psSubSource.executeQuery();
					if(rsSubSource.next())
					{
						subSourceId=rsSubSource.getInt("SUBSOURCE_ID");
					}
					else
					{
						rsSubSourceMax = psSubSourceMax.executeQuery();
						if(rsSubSourceMax.next())
						{
							subSourceId = rsSubSourceMax.getInt(1);
						}
						DBConnection.releaseResources(null, null, rsSubSourceMax);
						
						
						psSubSourceInsert.setInt(1, subSourceId);
						psSubSourceInsert.setString(2,leadDtoObj.getUtm_labels().toUpperCase() );
						psSubSourceInsert.executeUpdate();
					}
					DBConnection.releaseResources(null, null, rsSubSource);
					} else {
						subSourceId = 0;
					}*/
					
					//psLeadData.setString(1, subSourceId);
					//psLeadData.setString(2, sourceId);
					
					logger.info("leadId::"+leadId+" inserting into lead data table");
					
					psLeadData.setString(1, productId);
					/* Added by Parnika */
					int leadStatusId = 0;
					if(autoAssign){
						psLeadData.setString(2, statusMap.get(Constants.LEAD_STATUS_NAME_QUALIFIED));
						leadStatusId = Integer.parseInt(statusMap.get(Constants.LEAD_STATUS_NAME_QUALIFIED));
					}
					else{
						psLeadData.setString(2, statusMap.get(Constants.LEAD_STATUS_OPEN));
						leadStatusId = Integer.parseInt(statusMap.get(Constants.LEAD_STATUS_OPEN));
						
					}					
					psLeadData.setString(3, leadId);
					psLeadData.setString(4, prospectsId);
					//psLeadData.setString(7, leadDtoObj.getRequestType());
					psLeadData.setString(5, leadDtoObj.getFid());
					psLeadData.setString(6, leadDtoObj.getTid());
					psLeadData.setString(7, leadDtoObj.getFromPage());
					//psLeadData.setString(11, leadDtoObj.getProductType());
					psLeadData.setString(8, leadDtoObj.getReferPage());
					psLeadData.setString(9, leadDtoObj.getReferUrl());
					psLeadData.setString(10, leadDtoObj.getService());
					psLeadData.setString(11, leadDtoObj.getPlan());
					
					psLeadData.setString(12, leadDtoObj.getKeyWord());
					psLeadData.setString(13, leadDtoObj.getCampaign());
					
					src=0;
					subSrc="";
					
					if(leadDtoObj.getUtmLabels() != null && (leadDtoObj.getUtmLabels().equalsIgnoreCase(Constants.LEAD_SOURCE_SMS) || leadDtoObj.getUtmLabels().equalsIgnoreCase(Constants.LEAD_SOURCE_USSD)) )
					{
						psSource.setString(1, leadDtoObj.getUtmLabels().toUpperCase());
						rsSource = psSource.executeQuery();
						if(rsSource.next())
						{
							src = rsSource.getInt("SOURCE_ID");
						}
						subSrc="";
					}
					else
					{
						
						psSource.setString(1, Constants.LEAD_SOURCE_ONLINE);
						rsSource = psSource.executeQuery();
						if(rsSource.next())
						{
							src = rsSource.getInt("SOURCE_ID");
						}
						subSrc=leadDtoObj.getUtmLabels() ;
						/* Added by Parnika for LMS Phase 2 splitting of fields */
						if(subSrc != null && !(subSrc.equals("")) && subSrc.contains("Source:") && subSrc.contains("|Term:") && subSrc.contains("|Medium:") && subSrc.contains("|Campaign:")){
							
							int startSource = subSrc.indexOf("Source:");
							int startTerm = subSrc.indexOf("|Term:");
							int startMedium = subSrc.indexOf("|Medium:");
							int startCampaign = subSrc.indexOf("|Campaign:");
							
							// Source: airtel_sitelinks_dr_google_search |Term:  |Medium: cpc |Campaign: airtel_sitelinks

							utmSource = subSrc.substring(startSource+7, startTerm);
							utmTerm = subSrc.substring(startTerm+6, startMedium);
							utmMedium = subSrc.substring(startMedium+8, startCampaign);
							utmCampaign = subSrc.substring(startCampaign+10);
							
						}
						
						/* End of changes by parnika */
					}
					DBConnection.releaseResources(null, null, rsSource);
					psLeadData.setString(14, subSrc);
					psLeadData.setInt(15, src);
					psLeadData.setInt(16, leadprospectId);//Added by neetika
					psLeadData.setString(17, Constants.LEAD_UPDATED_BY);//Added by neetika
					
					/* Added by Parnika */
					
					psLeadData.setString(18, leadDtoObj.getOnlineCafNo());
					psLeadData.setString(19, leadDtoObj.getAllocatedNo());
					psLeadData.setString(20, leadDtoObj.getLeadSubmitTime());
					if(autoAssign){
						psLeadData.setString(21, PropertyReader.getAppValue("lms.online.lead.auto"));
					}
					else{
						psLeadData.setString(21, PropertyReader.getAppValue("lms.online.lead.normal"));
					}
					psLeadData.setString(22, utmSource);
					psLeadData.setString(23, utmTerm);
					psLeadData.setString(24, utmMedium);
					psLeadData.setString(25, utmCampaign);
					
					/* End of changes By Parnika */
					
					psLeadData.executeUpdate();
					logger.info("Inserted Successfully into lead_data table"+PRODUCT_LOB_ID+"  productId "+productId);
					
					psTrans.setString(1, PRODUCT_LOB_ID);
					
					/* Added By Parnika for Qualified Status in transaction table in case of Auto Assign */
					if(autoAssign){
						if (PRODUCT_LOB_ID.equalsIgnoreCase(Constants.TELEMEDIA_PRODUCT))
							psTrans.setString(2, statusMap.get(Constants.LEAD_STATUS_NAME_VERIFICATION));
						else
							psTrans.setString(2, statusMap.get(Constants.LEAD_STATUS_NAME_QUALIFIED));
					}
					else{
						psTrans.setString(2, statusMap.get(Constants.LEAD_STATUS_OPEN));
					}					
					psTrans.setString(3, leadId);
					if(autoAssign){
						if (PRODUCT_LOB_ID.equalsIgnoreCase(Constants.TELEMEDIA_PRODUCT))
							psTrans.setString(4, statusMap.get(Constants.LEAD_STATUS_NAME_VERIFICATION));
						else
							psTrans.setString(4, statusMap.get(Constants.LEAD_STATUS_NAME_QUALIFIED));
					}
					else{
						psTrans.setString(4, statusMap.get(Constants.LEAD_STATUS_OPEN));
					}
					
					/* End of changes By Parnika */
					psTrans.setString(5, Constants.LEAD_UPDATED_BY);
					psTrans.setString(6, productId);//Neetika  
					
					psTrans.executeUpdate();
					//logger.info("Inserted Successfully into lead_transaction table");
					
					/* Added by Parnika for inserting another row in Lead Transaction with 305 status in case of telemedia leads auto assignment */
					if (autoAssign && PRODUCT_LOB_ID.equalsIgnoreCase(Constants.TELEMEDIA_PRODUCT)){
						psTrans.setString(1, PRODUCT_LOB_ID);
						psTrans.setString(2, statusMap.get(Constants.LEAD_STATUS_NAME_QUALIFIED));					
						psTrans.setString(3, leadId);
						psTrans.setString(4, statusMap.get(Constants.LEAD_STATUS_NAME_QUALIFIED));
						psTrans.setString(5, Constants.LEAD_UPDATED_BY);
						psTrans.setString(6, productId);  						
						psTrans.executeUpdate();
					}
					/* End of changes by Parnika */
					
					if(!multipleLeadFlag)  {
						/*	psCircle.setString(1, leadDtoObj.getCircle().toUpperCase());
							rsCircle= psCircle.executeQuery();
							if(rsCircle.next())
							{
								circleId=rsCircle.getInt("CIRCLE_ID");
							} else {
								logger.info("Invalid Circle received: " + leadDtoObj.getCircle() + " changing circle to Default circle." );
							}
							DBConnection.releaseResources(null, null, rsCircle);*/
						//above code commented by neetika
							/*psState.setString(1, leadDtoObj.getState().toUpperCase());
							rsState= psState.executeQuery();
							if(rsState.next())
							{
								stateId=rsState.getString("STATE_CODE");
							}
							DBConnection.releaseResources(null, null, rsState);*/
							
							
							/*psZone.setString(1, leadDtoObj.getZone().toUpperCase());
							rsZone= psZone.executeQuery();
							if(rsZone.next())
							{
								zoneId=rsZone.getString("ZONE_CODE");
							}
							DBConnection.releaseResources(null, null, rsZone);*/
							
							
							/*psCity.setString(1, leadDtoObj.getCity().toUpperCase());
							rsCity= psCity.executeQuery();
							if(rsCity.next())
							{
								cityId=rsCity.getString("CITY_CODE");
							}
							DBConnection.releaseResources(null, null, rsCity);*/
						
						
							psProspectsData.setString(1, leadDtoObj.getProspectsName());
							//psProspectsData.setString(2,cityId );
							
							/*psProspectsData.setString(3, stateId);
							psProspectsData.setString(4, zoneId);*/
							psProspectsData.setString(2, leadDtoObj.getEmail());
							psProspectsData.setString(3, mobileNumber);
							//psProspectsData.setInt(5, circleId);
							//psProspectsData.setString(6, leadDtoObj.getAddress());
							//psProspectsData.setString(9, leadDtoObj.getPrimaryLanguage());
							/*if(leadDtoObj.getPinecode() ==null || leadDtoObj.getPinecode().equals("") || leadDtoObj.getPinecode().equalsIgnoreCase("nil"))
								psProspectsData.setString(7, "0");
							else
								psProspectsData.setString(7, leadDtoObj.getPinecode());*/
							
							psProspectsData.setString(4, leadDtoObj.getAlternateContactNumber());
							//psProspectsData.setString(12, leadDtoObj.getLandlineNumber());
							
							/*if(leadDtoObj.getPreperredAppointmentTime() == null || leadDtoObj.getPreperredAppointmentTime().equals(""))
							{
								psProspectsData.setString(12, new java.sql.Timestamp(new java.util.Date().getTime())+"");
							}
							else
							{*/
							//psProspectsData.setString(13, leadDtoObj.getPreperredAppointmentTime());
							//}
							//psProspectsData.setString(9, leadDtoObj.getAddress());
							psProspectsData.setString(5, prospectsId);
							psProspectsData.setString(6, "");
							psProspectsData.setString(7, "");
							psProspectsData.setString(8, leadDtoObj.getCompany());
							/*if(leadDtoObj.getIsCustomer() != null && leadDtoObj.getIsCustomer().equalsIgnoreCase("Y"))
							{
								psProspectsData.setString(9, leadDtoObj.getIsCustomer());
							}
							else
							{
								psProspectsData.setString(9, "N");
							}*/
							
							psProspectsData.executeUpdate();
							logger.info("Inserted Successfully into lead_prospect_master table");
							try {
								psLeadDetails.setString(1, leadId);
								psLeadDetails.executeUpdate();
								
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
							
						
							
							
							
							
				}
					/* Lead Priority  */
					try {
						if (leadId != null && !leadId.equalsIgnoreCase("0")){
							
							ResourceBundle rb=ResourceBundle.getBundle("ApplicationResources");
							String schemaName=rb.getString("lms.schema.bulk.download");
							cs = con.prepareCall("{call "+schemaName+".PRC_CALULATE_SCORE(?,?,?,?,?)}");
							cs.setLong(1, (Long.parseLong(leadId)));
							if(leadStatusId==Integer.parseInt(Constants.LEAD_STATUS_OPEN_ID))
								cs.setShort(2,Constants.SCORE_ONE);
							else if(leadStatusId==Integer.parseInt(Constants.LEAD_STATUS_QUALIFIED_ID) && !(PRODUCT_LOB_ID.equalsIgnoreCase(Constants.TELEMEDIA_PRODUCT)) )
								cs.setShort(2,Constants.SCORE_TWO);
							else if(leadStatusId==Integer.parseInt(Constants.LEAD_STATUS_VERIFICATION_ID) && PRODUCT_LOB_ID.equalsIgnoreCase(Constants.TELEMEDIA_PRODUCT) )
								cs.setShort(2,Constants.SCORE_TWO);
							else
								cs.setShort(2,Constants.SCORE_ONE);
							cs.registerOutParameter(3, java.sql.Types.VARCHAR);
							cs.registerOutParameter(4, java.sql.Types.VARCHAR);
							cs.registerOutParameter(5, java.sql.Types.INTEGER);
							cs.execute();
						}
					} catch (Exception e) {
						
						e.printStackTrace();
					}
					/* End of Lead Priority */
					
				}
				}
				if(dirtyleadFlag) leadStatus ="Invalid";else leadStatus ="Valid";
				
				if(message != null && !message.equals(""))
					sb.append("\n"+leadDtoObj.toString() + "~"+ leadStatus +"~"+message);
				else
					sb.append("\n"+leadDtoObj.toString() + "~"+ leadStatus);
				
					con.commit();
				}
				catch(Exception ex )
				{
					con.rollback();
					logger.info("Exception while inserting lead capture details inner try block "+ex);
					logger.info(leadDtoObj.toString());
					//logger.info("Exception while inserting lead capture details "+ex);
					//logger.info("Exception while inserting lead capture details "+ex);
					ex.printStackTrace();
				}
			} 
			logger.info(sb.toString());
		}
			con.commit();
		}
		catch(Exception ex)
		{
			try
			{
				con.rollback();
			}
			catch(Exception e)
			{
				logger.info(e);
			}
		//	logger.info("Exception while inserting lead capture details "+ex);
		//	logger.info("Exception while inserting lead capture details "+ex);
			ex.printStackTrace();
			throw new DAOException(ex.getMessage());
		}
		finally
		{
			try
			{
				DBConnection.releaseResources(null, ps, null); 
				DBConnection.releaseResources(null, psDirtyLead, null);
				DBConnection.releaseResources(null, psDuplicate, null);
				DBConnection.releaseResources(null, psDuplicateInsert, null);
				DBConnection.releaseResources(null, psProduct, null);
				DBConnection.releaseResources(null, psCircle, null);
				DBConnection.releaseResources(null, psLeadData, null);
				DBConnection.releaseResources(null, psProspectsData, null);
				DBConnection.releaseResources(null, psMultiple, null);
				DBConnection.releaseResources(null, psTrans, null);		
				
				DBConnection.releaseResources(null, psSource, rsSource);		
				DBConnection.releaseResources(null, psLeadDetails, null);		
				//DBConnection.releaseResources(null, psSubSourceMax, null);		*/
				
				DBConnection.releaseResources(null, psCity, null);	
				DBConnection.releaseResources(con, ps, null);
			}
			catch(Exception ex)
			{
				ps=null;
				con=null;
			}
		}
		return flag;
	}
	 // Obtain LEAD ID from SEQ
	String SELECT_SEQ_LEAD_NO = "SELECT NEXTVAL FOR LMS.SEQ_LEAD_ID AS SEQ_ID FROM sysibm.SYSDUMMY1";
	public  Long getLeadId(Long mobileNo , Connection con) throws DAOException
	{
		Long leadId=0l;
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			ps = con.prepareStatement(SELECT_SEQ_LEAD_NO);
			rs = ps.executeQuery();
			rs.next();
			leadId = Long.parseLong((mobileNo/100000)+""+ rs.getInt("SEQ_ID"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException("Exception occured while getting lead Id :  "+ e.getMessage(),e);
		} finally {
			try {
				DBConnection.releaseResources(null, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return leadId;
	}
	public String getNextVal(String seqName,Connection con) throws DAOException
	{
		String leadId="";
		PreparedStatement ps=null;
		ResultSet rs=null;
		String SQL_GET_LEAD_SEQ_ID="select nextval for "+seqName+" as NEXTVAL from sysibm.SYSDUMMY1  ";
		
		try {
			 ps=con.prepareStatement(SQL_GET_LEAD_SEQ_ID);
			 rs=ps.executeQuery();
			 if(rs.next())
			 {
				 leadId=rs.getString("NEXTVAL");
			 }
		} catch (Exception e) {
			throw new DAOException(e);
		}
		finally
		{
			try
			{	
					DBConnection.releaseResources(null, ps, rs);
			}
			catch(Exception ex)
			{
				rs=null;
				ps = null;
				throw new DAOException(ex);
			}
		}
		return leadId;
	}
	
	/* Added by Parnika for getting Auto - Assignment */
	
	public boolean isValidAutoAssignment(String productLobId, String productId, int circleId, String cityCode, Connection con) throws DAOException
	{	
		boolean autoAssign = false;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			ps = con.prepareStatement(SELECT_AUTO_ASSIGN_LEAD);
			ps.setString(1, productLobId);
			ps.setString(2, productId);
			ps.setInt(3, circleId);
			ps.setString(4, cityCode);
			rs = ps.executeQuery();
			if(rs.next()){
				autoAssign = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException("Exception occured while getting isValidAutoAssignment :  "+ e.getMessage(),e);
		} finally {
			try {
				DBConnection.releaseResources(null, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return autoAssign;
	}
	
	/* End of changes by Parnika */
	private void createAgencyBatch(PreparedStatement ps, AgencyCaptureLeadDO[] leadData) throws SQLException
	{
		
		for (AgencyCaptureLeadDO aLeadDO: leadData) 
		{
			
			ps.setString(1, aLeadDO.getProspectsName());
			ps.setString(2, aLeadDO.getProspectsMobileNumber());
			ps.setString(3, aLeadDO.getAlternateContactNumber());
			ps.setString(4, aLeadDO.getLandlineNumber());
			ps.setString(5, aLeadDO.getAddress());
			ps.setString(6, aLeadDO.getAddress2());
			ps.setString(7, aLeadDO.getCity());
			ps.setString(8, aLeadDO.getPincode());
			ps.setString(9, aLeadDO.getState());
			ps.setString(10, aLeadDO.getCircle());
			ps.setString(11, aLeadDO.getMaritalStatus());
			ps.setString(12, aLeadDO.getSource());
			ps.setString(13, aLeadDO.getSubSource());
			ps.setString(14, aLeadDO.getRequestCategory());
			ps.setString(15, aLeadDO.getAppointmentTime());
			ps.setString(16, aLeadDO.getOpportunityTime());
			ps.setString(17, aLeadDO.getProduct());
			ps.setString(18, aLeadDO.getEmail());
			ps.setString(19, aLeadDO.getPrimaryLanguage());
			ps.setString(20, aLeadDO.getSubZone());
			ps.setString(21, aLeadDO.getZone());
			ps.setString(22, aLeadDO.getRemarks());
			ps.setString(23, aLeadDO.getCampaign());
			ps.setString(24, aLeadDO.getReferPage());
			ps.setString(25, aLeadDO.getQualLeadParam());
			ps.setString(26, aLeadDO.getGeoIpCity());
			ps.setString(27, aLeadDO.getLeadSubmitTime());
			ps.setString(28, aLeadDO.getTid());
			ps.setString(29, aLeadDO.getFid());
			ps.setString(30, aLeadDO.getKeyWord());
			ps.setString(31, aLeadDO.getFromPage());
			ps.setString(32, aLeadDO.getService());
			ps.setString(33, aLeadDO.getTag());
			ps.setString(34, aLeadDO.getIsCustomer());
			ps.setString(35, aLeadDO.getAdParameter());
			ps.setString(36, aLeadDO.getUtmLAbels());
			ps.setString(37, aLeadDO.getReferUrl());
			ps.setString(38, aLeadDO.getCompany());
			ps.setString(39, aLeadDO.getCityZoneCode());
			ps.setString(40, aLeadDO.getPlan());
			ps.setString(41, aLeadDO.getRental());
			ps.setString(42, aLeadDO.getAllocatedNo());
			ps.setString(43, aLeadDO.getOnlineCafNo());
			ps.setString(44, aLeadDO.getPytAmt());
			ps.setString(45, aLeadDO.getTranRefno());
			ps.setString(46, aLeadDO.getAgencyId());
			ps.setString(47, aLeadDO.getNdncDisclaimer());
			ps.setString(48, aLeadDO.getIpAddress());
			ps.setString(49, aLeadDO.getUdId());
			ps.setString(50, aLeadDO.getCityId());
			ps.setString(51, aLeadDO.getCircleId());
			ps.setString(52, aLeadDO.getFeasibilityparam());
			ps.setString(53, aLeadDO.getPlanId());
			ps.setString(54, aLeadDO.getCustomerInfoId());
			ps.setString(55, aLeadDO.getRentaltype());
			ps.setString(56, aLeadDO.getFreebiecount());
			ps.setString(57, aLeadDO.getBoostercount());
			ps.setString(58, aLeadDO.getFreebietaken());
			ps.setString(59, aLeadDO.getBoostertaken());
			ps.setString(60, aLeadDO.getFlag());
			ps.setString(61, aLeadDO.getPrepaidnumber());
			ps.setString(62, aLeadDO.getOffer());
			ps.setString(63, aLeadDO.getDownloadlimit());
			ps.setString(64, aLeadDO.getDevicemrp());
			ps.setString(65, aLeadDO.getVoicebenefit());
			ps.setString(66, aLeadDO.getDataquota());
			ps.setString(67, aLeadDO.getUsertype());
			ps.setString(68, aLeadDO.getDevicetaken());
			ps.setString(69, aLeadDO.getBenefit());
			ps.setString(70, aLeadDO.getPkgduration());
			ps.setString(71, aLeadDO.getHlrno());
			ps.setString(72, aLeadDO.getRsuCode());
			ps.setString(73, aLeadDO.getDob());
			ps.setString(74, aLeadDO.getExtraParam1());
			ps.setString(75, aLeadDO.getExtraParam2());
			ps.setString(76, aLeadDO.getExtraParam3());
			ps.setString(77, aLeadDO.getExtraParam4());
			ps.setString(78, aLeadDO.getExtraParam5());
			ps.setString(79, aLeadDO.getExtraParam6());
			ps.setString(80, aLeadDO.getExtraParam7());
			ps.addBatch();
			
			
		}
	}
	
	public AgencyResponseMessage captureLeadData(AgencyCaptureLeadDO[] leadData) throws DAOException
	{
		//logger.info("Now creating connection");
		System.out.println("inside lead capture****************");
		Connection con = null;
		PreparedStatement ps = null;
		String responce ="";
		AgencyResponseMessage rseponseMsg =new AgencyResponseMessage();;
		try
		{ 
			responce = getResponceWithTrnxId(leadData);
			if(responce.equalsIgnoreCase("0")){
				rseponseMsg.setResponseCode("1");
				rseponseMsg.setResponseMsg("Internal Error Occured");
				return rseponseMsg;
			}
			con = DBConnection.getDBConnection();
			con.setAutoCommit(false);
			ps = con.prepareStatement(INSERT_LEAD_CAPTURED_DATA);
			
			createAgencyBatch(ps, leadData);
			//logger.info("Now creating connection");
			ps.executeBatch();
			con.commit();
			logger.info("Now  commit");
			if(!responce.equalsIgnoreCase("0")){
				rseponseMsg.setResponseCode(responce);
				rseponseMsg.setResponseMsg("SUCCESS");
				return rseponseMsg; 
			}
		}
		catch(SQLException sqle)
		{
			
			SQLException	current = sqle;
			SQLException	temp = sqle;
			   do {
				  
				   temp=current.getNextException();
				   temp.printStackTrace();
			   } while ((current = current.getNextException()) != null);
			sqle.printStackTrace();
			rseponseMsg.setResponseCode("1");
			rseponseMsg.setResponseMsg("Internal Error Occured");
			return rseponseMsg;
		}
		catch(Exception e)
		{
			rseponseMsg.setResponseCode("1");
			rseponseMsg.setResponseMsg("Internal Error Occured");
			e.printStackTrace();
			return rseponseMsg;
		}
		finally
		{
			try {
				DBConnection.releaseResources(con, ps, null);	
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
			
		}
		return rseponseMsg;
	}
	
	public int getMaxId() {
		int maxId =0;
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs =null;
		try {
			
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(FIND_MAX_LEAD_CAPTURED_ID);
			rs = ps.executeQuery();
			if(rs.next()) {
				maxId  = rs.getInt("LEAD_CAPTURED_DATA_ID");
				return maxId;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		finally
		{
			try
			{
				DBConnection.releaseResources(con, ps, rs);
			}
			catch(Exception sqle)
			{
				sqle.printStackTrace();
				
			}
		}
		return 0;
	}
	
	//Added by satish
	
	public String  getLeadData(LeadCaptureServiceFirstVersDO leadData) throws DAOException {
		
		
		System.out.println("inside getleaddata");
		Connection con = null;
		PreparedStatement ps = null;
		PreparedStatement ps1=null;
		PreparedStatement ps2=null;
		
		//String SELECT_CREATE_LEAD_ID="select Max(LEAD_CAPTURED_DATA_ID) as LEAD_CAPTURED_DATA_ID from LEAD_CAPTURE";
				
		ResultSet rs = null;
		String LEAD_CAPTURED_DATA_ID=null;
		try{
		con = DBConnection.getDBConnection();
		con.setAutoCommit(false);
		
	
			ps = con.prepareStatement(INSERT_LEAD_CAPTURED_DATA);
			createLeadBatch(ps, leadData);
			ps.executeBatch();
			
			ps1=con.prepareStatement(SELECT_CREATE_LEAD_ID);
			rs=ps1.executeQuery();
			if(rs.next()){
				LEAD_CAPTURED_DATA_ID=rs.getString("LEAD_CAPTURED_DATA_ID");
			}
			
			
			System.out.println("\nLEAD_CAPTURED_DATA_ID                     ::::::::"+LEAD_CAPTURED_DATA_ID+"\n\n\n");
			ps2=con.prepareStatement(INSERT_LEAD_CAPTUREDATA_DATA_VES);
			createLeadDataBatch(ps2, leadData,LEAD_CAPTURED_DATA_ID);
			
			ps2.executeBatch();
		
		con.commit();
		
		
		return LEAD_CAPTURED_DATA_ID;
		
		
		}catch(SQLException se){
			logger.info("request data ***" +StringUtils.converObjectToJson(leadData));
			int count = 1;
		      while (se != null) {
		    	  logger.info("SQLException " + count+"Code: " + se.getErrorCode()+"SqlState: " + se.getSQLState()+"Error Message: " + se.getMessage());
		        se = se.getNextException();
		        count++;
		      }
			try {
				con.rollback();
				System.out.println("inside roolback\n");
			} catch (Exception e) {
			} 
			throw new DAOException(se);
		}catch(Exception e) {
			logger.info("request data ***" +StringUtils.converObjectToJson(leadData));
			e.printStackTrace();
			throw new DAOException(e);
		}
		finally
		{
			try {
				DBConnection.releaseResources(con, ps, null);
				DBConnection.releaseResources(null, ps1, rs);
				DBConnection.releaseResources(null, ps2, null);
				
					
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
			
		}
		//return null;
		
	}
		
		
		
	
	
	private void createLeadDataBatch(PreparedStatement ps, LeadCaptureServiceFirstVersDO aLeadDO,String LEAD_CAPTURED_DATA_ID) throws SQLException{
		
			ps.setLong(1, Long.parseLong(LEAD_CAPTURED_DATA_ID));
			ps.setString(2, aLeadDO.getSimNo());
			ps.setString(3, aLeadDO.getCustomerSegment());
			ps.setString(4, aLeadDO.getRelationName());
			ps.setString(5, aLeadDO.getNationality());
			ps.setString(6, aLeadDO.getIdentityProofType());
			ps.setString(7, aLeadDO.getIdentityProofID());
			ps.setString(8, aLeadDO.getGender());
			ps.setString(9, aLeadDO.getPayment_date());
			ps.setString(10, aLeadDO.getUpc());
			ps.setString(11, aLeadDO.getUpc_gen_date());
			ps.setString(12, aLeadDO.getPreviousOerator());
			ps.setString(13, aLeadDO.getPreviousCircle());
			ps.setString(14, aLeadDO.getPartExisting());
			ps.setString(15, aLeadDO.getMnpStatus());
			ps.setString(16, aLeadDO.getDocumentCollectedFlag());
			ps.setString(17, aLeadDO.getPlanType());
			ps.setString(18, aLeadDO.getExtraParam9());               //rental plan in LEAD_CAPTURE_DATA
			ps.setString(19, aLeadDO.getExtraParam10());             //APPOINTMENT_ENDTIME in LEAD_CAPTURE_DATA
			ps.addBatch();
		
	}
	
	
	private void createLeadBatch(PreparedStatement ps, LeadCaptureServiceFirstVersDO aLeadDO) throws SQLException
	{
		
			ps.setString(1, aLeadDO.getProspectsName());
			ps.setString(2, aLeadDO.getProspectsMobileNumber());
			ps.setString(3, aLeadDO.getAlternateContactNumber());
			ps.setString(4, aLeadDO.getLandlineNumber());
			ps.setString(5, aLeadDO.getAddress());
			ps.setString(6, aLeadDO.getAddress2());
			ps.setString(7, aLeadDO.getCity());
			ps.setString(8, aLeadDO.getPincode());
			ps.setString(9, aLeadDO.getState());
			ps.setString(10, aLeadDO.getCircle());
			ps.setString(11, aLeadDO.getMaritalStatus());
			ps.setString(12, aLeadDO.getSource());
			ps.setString(13, aLeadDO.getSubSource());
			ps.setString(14, aLeadDO.getRequestCategory());
			ps.setString(15, aLeadDO.getAppointmentTime());
			ps.setString(16, aLeadDO.getOpportunityTime());
			ps.setString(17, aLeadDO.getProduct());
			ps.setString(18, aLeadDO.getEmail());
			ps.setString(19, aLeadDO.getPrimaryLanguage());
			ps.setString(20, aLeadDO.getSubZone());
			ps.setString(21, aLeadDO.getZone());
			ps.setString(22, aLeadDO.getRemarks());
			ps.setString(23, aLeadDO.getCampaign());
			ps.setString(24, aLeadDO.getReferPage());
			ps.setString(25, aLeadDO.getQualLeadParam());
			ps.setString(26, aLeadDO.getGeoIpCity());
			ps.setString(27, aLeadDO.getLeadSubmitTime());
			ps.setString(28, aLeadDO.getTid());
			ps.setString(29, aLeadDO.getFid());
			ps.setString(30, aLeadDO.getKeyWord());
			ps.setString(31, aLeadDO.getFromPage());
			ps.setString(32, aLeadDO.getService());
			ps.setString(33, aLeadDO.getTag());
			ps.setString(34, aLeadDO.getIsCustomer());
			ps.setString(35, aLeadDO.getAdParameter());
			ps.setString(36, aLeadDO.getUtmLAbels());
			ps.setString(37, aLeadDO.getReferUrl());
			ps.setString(38, aLeadDO.getCompany());
			ps.setString(39, aLeadDO.getCityZoneCode());
			ps.setString(40, aLeadDO.getPlan());
			ps.setString(41, aLeadDO.getRental());
			ps.setString(42, aLeadDO.getAllocatedNo());
			ps.setString(43, aLeadDO.getOnlineCafNo());
			ps.setString(44, aLeadDO.getPytAmt());
			ps.setString(45, aLeadDO.getTranRefno());
			ps.setString(46, aLeadDO.getAgencyId());
			ps.setString(47, aLeadDO.getNdncDisclaimer());
			ps.setString(48, aLeadDO.getIpAddress());
			ps.setString(49, aLeadDO.getUdId());
			ps.setString(50, aLeadDO.getCityId());
			ps.setString(51, aLeadDO.getCircleId());
			ps.setString(52, aLeadDO.getFeasibilityparam());
			ps.setString(53, aLeadDO.getPlanId());
			ps.setString(54, aLeadDO.getCustomerInfoId());
			ps.setString(55, aLeadDO.getRentaltype());
			ps.setString(56, aLeadDO.getFreebiecount());
			ps.setString(57, aLeadDO.getBoostercount());
			ps.setString(58, aLeadDO.getFreebietaken());
			ps.setString(59, aLeadDO.getBoostertaken());
			ps.setString(60, aLeadDO.getFlag());
			ps.setString(61, aLeadDO.getPrepaidnumber());
			ps.setString(62, aLeadDO.getOffer());
			ps.setString(63, aLeadDO.getDownloadlimit());
			ps.setString(64, aLeadDO.getDevicemrp());
			ps.setString(65, aLeadDO.getVoicebenefit());
			ps.setString(66, aLeadDO.getDataquota());
			ps.setString(67, aLeadDO.getUsertype());
			ps.setString(68, aLeadDO.getDevicetaken());
			ps.setString(69, aLeadDO.getBenefit());
			ps.setString(70, aLeadDO.getPkgduration());
			ps.setString(71, aLeadDO.getHlrno());
			ps.setString(72, aLeadDO.getRsuCode());
			ps.setString(73, aLeadDO.getDob());
			ps.setString(74, aLeadDO.getPaymentAmtDue()); //EXTRA_PARAM1
			ps.setString(75, aLeadDO.getPaymentCollected()); //EXTRA_PARAM2
			ps.setString(76, aLeadDO.getExtraParam3());//latitude
			ps.setString(77, aLeadDO.getExtraParam4());//longitude
			ps.setString(78, aLeadDO.getPaymentType()); //EXTRA_PARAM5
			ps.setString(79, aLeadDO.getPaymentStatus()); //EXTRA_PARAM6
			ps.setString(80, aLeadDO.getExtraParam7());
			ps.addBatch();
			
			
	}
	
	
	
	//Added by satish
	
	private String getResponceWithTrnxId(AgencyCaptureLeadDO[] leadData) {
		int maxId  =getMaxId();
		if(maxId==0)
			return "0";
		  maxId  = maxId+1;
		 String trx ="";
		 try {
		
		 if(leadData.length < 2) {
				return "TXN-"+String.valueOf(maxId);
			}else {
				for(int i=0; i< leadData.length ; i++) {
					 trx= trx +"CID:"+maxId++;
				 }
				return "TXN-"+trx;
			}
		 } catch (Exception e) {
				e.printStackTrace();
			}
		 return "0";
	}
}
