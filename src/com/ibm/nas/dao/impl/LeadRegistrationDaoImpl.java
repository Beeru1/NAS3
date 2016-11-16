package com.ibm.nas.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.ibm.nas.common.Constants;
import com.ibm.nas.common.DBConnection;
import com.ibm.nas.common.LMSStatusCodes;
import com.ibm.nas.common.PropertyReader;
import com.ibm.nas.common.Utility;
import com.ibm.nas.dao.LeadRegistrationDao;
import com.ibm.nas.dao.MasterDao;
import com.ibm.nas.dto.BulkUploadMsgDto;
import com.ibm.nas.dto.LeadDetailsDTO;
import com.ibm.nas.dto.ProductDTO;
import com.ibm.nas.engine.dao.impl.CaptureLeadDataDAOImpl;
import com.ibm.nas.engine.util.ServerPropertyReader;
import com.ibm.nas.exception.DAOException;
import com.ibm.nas.exception.LMSException;
import com.ibm.nas.forms.LeadRegistrationFormBean;
import com.ibm.nas.services.MasterService;
import com.ibm.nas.services.impl.MasterServiceImpl;
import com.ibm.nas.sms.SendSMSXML;

public class LeadRegistrationDaoImpl implements LeadRegistrationDao
{
	private static final Logger logger;
	static {
		logger = Logger.getLogger(LeadRegistrationDaoImpl.class);
	}
	
	//Added by srikant
private static LeadRegistrationDaoImpl leadRegistrationDaoImpl=null;
	
	private LeadRegistrationDaoImpl(){
		
	}
	
	public static LeadRegistrationDaoImpl leadRegistrationDaoInstance()
	{
		if(leadRegistrationDaoImpl==null)
		{
			leadRegistrationDaoImpl = new LeadRegistrationDaoImpl();
		}
		return leadRegistrationDaoImpl;
		
	}

 
    public ArrayList<BulkUploadMsgDto> insertRecord(List<LeadDetailsDTO> leadList, int source) throws DAOException
    {
    	
    	
    	ArrayList<BulkUploadMsgDto> arr = new ArrayList<BulkUploadMsgDto>();
        Connection con=null;
        PreparedStatement ps=null;
        PreparedStatement pstmt=null;
        PreparedStatement pstmt1=null;
        PreparedStatement pstmt2=null;
        PreparedStatement pstmt3=null;
        PreparedStatement pstmt4=null;
        PreparedStatement pstmt5=null;
        PreparedStatement pstmt6=null;
        PreparedStatement pstmt8=null;
        PreparedStatement pstmtLTX=null;
        PreparedStatement pstmtLTX1=null;
        PreparedStatement pstmt7=null;
        PreparedStatement pstmtCityZone=null;
        PreparedStatement pstmtCity=null;
        PreparedStatement pstmtZoneFromCity=null;
        PreparedStatement pstmtPinCode=null;
        PreparedStatement pstmtleadprosDt=null;
        PreparedStatement pstmtUpdleadprosDt=null;
        PreparedStatement ps4=null;
        PreparedStatement pstmtld=null;
        PreparedStatement pstmtld1=null;
        MasterService mstrService = new MasterServiceImpl();
        ResultSet rsTemp=null;
        ResultSet rs=null;
        ResultSet rsleadprosDt=null;
        ResultSet rsUpdleadprosDt=null;
        ResultSet rs1=null;
        ResultSet rsCityZone=null;
        ResultSet rsCity=null;
        ResultSet rsZoneFromCity=null;
        int  prospectId = 0;
        int  leadprospectId = 0; //Neetika 19 Nov
        String clientip=null;
        Long leadId = 0l;
        int parameterIndex=1;
        int paramIndex=1;
        boolean productFound=false;
        ResultSet rsPin=null;
        CallableStatement cs = null;
        try
        {   
        	con = DBConnection.getDBConnection(); 
        	
            /* Added by Parnika for all queries outside loop */
            
    		StringBuffer select_existing_prospect_id_query = new StringBuffer("select PROSPECT_ID from LEAD_PROSPECT_CUSTOMER where PROSPECT_MOBILE_NUMBER = ?  ");
    		pstmt = con.prepareStatement(select_existing_prospect_id_query.toString());
    		
    		StringBuffer select_existing_lead_id_query = new StringBuffer("SELECT LEAD_ID FROM LEAD_DATA WHERE PROSPECT_ID = ? AND " +
    				"LEAD_STATUS_ID NOT IN  " +PropertyReader.getAppValue("duplicate.exclude"));
    		
    		/* Added by Parnika for making duplicate logic Parameterized */
    		
    		if((source == Constants.SOURCE_TYPE_BULK_UPLOAD) || (source ==  Constants.SOURCE_TYPE_CALL_CENTER)){
    		
    		if (PropertyReader.getAppValue("duplicate.open.logic").trim().equalsIgnoreCase("Y") ){ // To check whether duplicate logic is applicable or not.
    			
    			if (PropertyReader.getAppValue("duplicate.open.daywise").trim().equalsIgnoreCase("Y") ){ // To check whether day-wise duplicate logic is applicable or not.
    				select_existing_lead_id_query.append(" AND date(CREATE_TIME) >= current date - ? days ");
  					if (PropertyReader.getAppValue("duplicate.open.productwise").trim().equalsIgnoreCase("Y") )
  					{ // To check whether Product wise duplicate logic is applicable or not.		
  						
				    			select_existing_lead_id_query.append(" AND PRODUCT_ID = ? ");
				    	
				    			
				    	}
    			}
    			else{ // If not day-wise then 
    					if (PropertyReader.getAppValue("duplicate.open.productwise").trim().equalsIgnoreCase("Y") )
    					{ // To check whether Product wise duplicate logic is applicable or not.		
    						
				    			select_existing_lead_id_query.append("  AND PRODUCT_ID = ? ");
				    		
				    	}	
    			}
    		}
    		
    		} // End of duplicate query for open leads
    		
    		if(source == Constants.SOURCE_TYPE_OUTBOUND){
        		
        		if (PropertyReader.getAppValue("duplicate.qualified.logic").trim().equalsIgnoreCase("Y") ){ // To check whether duplicate logic is applicable or not.
        			
        			if (PropertyReader.getAppValue("duplicate.qualified.daywise").trim().equalsIgnoreCase("Y") ){ // To check whether day-wise duplicate logic is applicable or not.
        				select_existing_lead_id_query.append(" AND date(CREATE_TIME) >= current date - ? days ");
      					if (PropertyReader.getAppValue("duplicate.qualified.productwise").trim().equalsIgnoreCase("Y") )
      					{ // To check whether Product wise duplicate logic is applicable or not.
      						
    				    			select_existing_lead_id_query.append(" AND PRODUCT_ID = ? ");	
    				    	
    				    	}
        			}
        			else{ // If not day-wise then 
        					if (PropertyReader.getAppValue("duplicate.qualified.productwise").trim().equalsIgnoreCase("Y") )
        					{ // To check whether Product wise duplicate logic is applicable or not.			
        						
    				    			select_existing_lead_id_query.append("  AND PRODUCT_ID = ? ");
    				    				
    				    	}	
        			}
        		}
        		
        		} // End of duplicate query for qualified Leads
    		
    		select_existing_lead_id_query.append("   ");
    		pstmt1 = con.prepareStatement(select_existing_lead_id_query.toString());
    		
    		logger.info("Duplicate logic query:"+ select_existing_lead_id_query);
    		/* End of changes by Parnika for making duplicate logic Parameterized */
    		
            //Commented by Neetika to add column updated-dt and updated_by
            //String INSERT_LEAD_PROSPECT_CUSTOMER ="INSERT INTO LEAD_PROSPECT_CUSTOMER(PROSPECT_ID, CUSTOMER_NAME, PROSPECT_MOBILE_NUMBER, ALTERNATE_CONTACT_NUMBER, LANDLINE_NUMBER, ADDRESS1, ADDRESS2, CITY, PINCODE, STATE, CIRCLE_ID, APPOINTMENT_TIME, EMAIL, PRIMARY_LANGUAGE ,IS_CUSTOMER,ZONE,SUB_ZONE, MARITAL_STATUS,RSU_ID ) VALUES(NEXTVAL FOR SEQ_PROSPECTS_ID,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";        
    		//String INSERT_LEAD_PROSPECT_CUSTOMER ="INSERT INTO LEAD_PROSPECT_CUSTOMER(PROSPECT_ID, CUSTOMER_NAME, PROSPECT_MOBILE_NUMBER, ALTERNATE_CONTACT_NUMBER, LANDLINE_NUMBER, ADDRESS1-, ADDRESS2-, CITY-, PINCODE-, STATE-, CIRCLE_ID-, APPOINTMENT_TIME-, EMAIL, PRIMARY_LANGUAGE ,IS_CUSTOMER-,ZONE-,SUB_ZONE-, MARITAL_STATUS,RSU_ID-,UPDATED_DT,UPDATED_BY ) VALUES(NEXTVAL FOR SEQ_PROSPECTS_ID,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,current timestamp,?)";
    		
    		/* Insert Query for Lead Prospect Customer */
    		String INSERT_LEAD_PROSPECT_CUSTOMER ="INSERT INTO LEAD_PROSPECT_CUSTOMER(PROSPECT_ID, CUSTOMER_NAME, PROSPECT_MOBILE_NUMBER, ALTERNATE_CONTACT_NUMBER, LANDLINE_NUMBER,  EMAIL, PRIMARY_LANGUAGE , MARITAL_STATUS,UPDATED_DT,UPDATED_BY ) VALUES(NEXTVAL FOR SEQ_PROSPECTS_ID,?,?,?,?,?,?,?,current timestamp,?)";
    		pstmt3 = con.prepareStatement(INSERT_LEAD_PROSPECT_CUSTOMER);
    		
    		
    		/* Insert Query for Lead Prospect Detail */
    		String INSERT_LEAD_PROSPECT_DETAIL =" INSERT INTO LEAD_PROSPECT_DETAIL ( CITY_ZONE_CODE,CITY_CODE, STATE, ZONE_CODE, CIRCLE_ID, ADDRESS2,PRIMARY_LANGUAGE,PINCODE, ALTERNATE_CONTACT_NUMBER, LANDLINE_NUMBER, APPOINTMENT_TIME, ADDRESS1,PROSPECT_ID,  IS_CUSTOMER, MARITAL_STATUS, SUB_ZONE, RSU_CODE, UPDATED_DT, UPDATED_BY,PRODUCT_LOB_ID,EXTRA_PARAMS_2,EXTRA_PARAMS_3, LEAD_ID,APPOINMENT_ENDTIME ,PYT_AMT,TRAN_REFNO,PYT_AMT_DUE)"+
    		 " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, current timestamp, ?,?, ?,?,?,? ,?,?,?)"; //Query added by neetika // Lead Id Added By Parnika on 13 May 2014
    		pstmt6=con.prepareStatement(INSERT_LEAD_PROSPECT_DETAIL);
    		//Insert query for Lead_details added by bhaskar
    		
    		String INSERT_LEAD_DETAILS =" INSERT INTO LEAD_DETAILS ( LEAD_ID,CREATED_DATE,EXTRA_PARAM6,FEASIBILITY_PARAM,DEVICE_TAKEN,EXTRA_PARAM7,LEAD_CAPTURED_DATA_ID)"+
   		 	" VALUES( ?,current timestamp,?,?,?,?,?)"; //Query added by neetika // Lead Id Added By Parnika on 13 May 2014
    		pstmtld=con.prepareStatement(INSERT_LEAD_DETAILS);
    		
    		String INSERT_LEAD_SCHEDULE_DATA =" INSERT INTO LEAD_SCCHEDULE_DATA (LEAD_ID,APPOINTMENT_STATUS,CREATE_TIME)"+
   		 	" VALUES( ?,?,current timestamp)"; //Query added by neetika // Lead Id Added By Parnika on 13 May 2014
    		pstmtld1=con.prepareStatement(INSERT_LEAD_SCHEDULE_DATA);
    		
    		
    		//added by amarjeet to select alert template for sms in drop 3
    		String SELECT_SMS_TEMPLATE = "SELECT MESSAGE_TEMPLATE FROM ALERT_MSTR  WHERE ALERT_ID = ?  AND ACTIVE = 'A'  ";
    		final String INSERT_SMS_TRANSACTIONS = " INSERT INTO CUSTOMER_SEND_SMS_DETAILS( MOBILE_NUMBER, MESSAGE, STATUS, SENT_ON, CREATED_ON, RESPONSE_MSG) VALUES( ?, ?, ?, CURRENT TIMESTAMP, CURRENT TIMESTAMP, ?)";
    		
    		//2 columns added b neetika
    		
    		//String SELECT_LEAD_PROSPECT_ID="Select LEAD_PROSPECT_ID from LEAD_PROSPECT_DETAIL where PROSPECT_ID=? and PRODUCT_LOB_ID=?   ";
    		
    		String SELECT_LEAD_PROSPECT_ID="Select LEAD_PROSPECT_ID from LEAD_PROSPECT_DETAIL where PROSPECT_ID=? and LEAD_ID=?   ";
    		
    		/* Insert Query for Lead Data */
    		//2 coulmn added by amarjeet in drop 3
    		String INSERT_LEAD_DATA = "INSERT INTO LEAD_DATA(LEAD_ID,PROSPECT_ID, SUB_SOURCE, SOURCE, PRODUCT_ID, LEAD_STATUS_ID, REQUEST_TYPE, CREATE_TIME, REMARKS,LEAD_PROSPECT_ID,UPDATED_DT,UPDATED_BY,CAMPAIGN, LEAD_CATEGORY , LEAD_SUB_STATUS_ID, LEAD_SUB_SUB_STATUS_ID,CAF,PLAN) VALUES(?,?, ?,?,?,?,?, current timestamp, ?,?,current timestamp, ?,?, ? ,?,?,?,?)";//3 columns added by neetika//campaign added by pratap
    		pstmt5 = con.prepareStatement(INSERT_LEAD_DATA);
    		
    		/* Insert Query for Lead Transaction */
    		String INSERT_LEAD_TRANSACTION = "INSERT INTO LEAD_TRANSACTION(PRODUCT_ID, LEAD_ASSIGNMENT_TIME, LEAD_STATUS_ID,SUB_STATUS_ID, LEAD_ID, REMARKS, TRANSACTION_TIME,UPDATED_BY, UD_ID, EXPECTED_CLOSURE_DATE, LEAD_CLOSURE_TIME,LEAD_PRODUCT_ID,CLIENT_IP)  VALUES((SELECT PRODUCT_LOB_ID  FROM PRODUCT_MSTR WHERE PRODUCT_ID = ?), current timestamp, ?, ?,?,?,current timestamp,?,?,current timestamp, current timestamp,?,? )  ";//2 columns added b neetika
    		pstmtLTX = con.prepareStatement(INSERT_LEAD_TRANSACTION);
    		
    		/* Another Insert Query for Lead Transaction only in case of Qualified Leads */
    		pstmtLTX1 = con.prepareStatement(INSERT_LEAD_TRANSACTION);
    		/* End of changes by Parnika */
    		
    		String sqlClient="select client_ip from KM_LOGIN_DATA where USER_LOGIN_ID=? and LOGIN_TIME=(select max(LOGIN_TIME) from KM_LOGIN_DATA where USER_LOGIN_ID=?)  ";        
    		pstmt7=con.prepareStatement(sqlClient);
    		
    		pstmt8 = con.prepareStatement(SELECT_LEAD_PROSPECT_ID.toString());
    		
    		String GET_CITY_CODE=" select CITY_CODE  from CITY_ZONE_MSTR where CITY_ZONE_CODE=?  ";
    		String GET_ZONE_CODE=" select zone_code  from CITY_MSTR where CITY_CODE= ( select CITY_CODE  from CITY_ZONE_MSTR where CITY_ZONE_CODE=?)   ";
    		String GET_ZONE_CODE_FROM_CITY=" select zone_code  from CITY_MSTR where CITY_CODE= ?   ";
    		
    		//String GET_CITYZONE_FROM_PIN_CODE=" SELECT city_zone_code FROM CITY_ZONE_MSTR WHERE CITY_CODE=(SELECT CITY_CODE FROM city_mstr WHERE ZONE_CODE=(SELECT ZONE_CODE FROM zone_mstr WHERE circle_mstr_id =(SELECT CIRCLE_MSTR_ID FROM CIRCLE_MSTR WHERE CIRCLE_MSTR_ID in (SELECT CIRCLE_MSTR_ID FROM zone_mstr WHERE ZONE_CODE in(SELECT ZONE_CODE FROM city_mstr WHERE city_code in(SELECT city_code FROM CITY_ZONE_MSTR WHERE CITY_ZONE_CODE in(SELECT CITY_ZONE_CODE FROM PINCODE_MSTR WHERE PINCODE=?)))) and lob_id=?))) ";
    		
    		String GET_CITYZONE_FROM_PIN_CODE="SELECT pm.CITY_ZONE_CODE,czm.CITY_ZONE_NAME, czm.CITY_CODE,ctm.CITY_NAME,ctm.ZONE_CODE,zm.ZONE_NAME,zm.CIRCLE_MSTR_ID FROM CIRCLE_MSTR CM,ZONE_MSTR ZM,CITY_MSTR CTM ,CITY_ZONE_MSTR CZM,PINCODE_MSTR PM WHERE  PM.CITY_ZONE_CODE=czm.CITY_ZONE_CODE AND czm.CITY_CODE=ctm.CITY_CODE AND ctm.ZONE_CODE= zm.ZONE_CODE AND zm.CIRCLE_MSTR_ID= cm.CIRCLE_MSTR_ID and pm.PINCODE=? AND cm.CIRCLE_MSTR_ID=? and cm.LOB_ID=? AND pm.STATUS='A'   " ;
    		
    		
    		String city_code="";
    		String zone_code="";
    		String cityZone_code="";
    		pstmtCityZone = con.prepareStatement(GET_CITY_CODE);
    		pstmtCity = con.prepareStatement(GET_ZONE_CODE);
    		pstmtZoneFromCity = con.prepareStatement(GET_ZONE_CODE_FROM_CITY);
    		pstmtPinCode = con.prepareStatement(GET_CITYZONE_FROM_PIN_CODE);
    		String CHECK_LEAD_PROS_DT="select count(*) from lead_prospect_detail where prospect_id=? and PRODUCT_LOB_ID=?  ";
    		pstmtleadprosDt = con.prepareStatement(CHECK_LEAD_PROS_DT);
    		String UPDATE_LEAD_PROS_DETAILS=" update LEAD_PROSPECT_DETAIL set CITY_ZONE_CODE=?, CITY_CODE=?,STATE=?, ZONE_CODE=?, CIRCLE_ID=?, ADDRESS2=?,PRIMARY_LANGUAGE=?,PINCODE=?, ALTERNATE_CONTACT_NUMBER=?, LANDLINE_NUMBER=?, " +
    				"APPOINTMENT_TIME=?, ADDRESS1=?,IS_CUSTOMER=?, MARITAL_STATUS=?, SUB_ZONE=?, RSU_CODE=?, UPDATED_DT=current timestamp, updated_by=?,APPOINMENT_ENDTIME=? " +
    				"where PROSPECT_ID=? and PRODUCT_LOB_ID=?";
    		pstmtUpdleadprosDt = con.prepareStatement(UPDATE_LEAD_PROS_DETAILS);
    		
    		
    		
    		/* For Loop Begins for Each Lead To be Created */
        	for(int ii=0; ii< leadList.size(); ii++)
			{
        		
        		int productLobId = 0;
        		
        		parameterIndex=1;
        		paramIndex= 1;
				LeadDetailsDTO leadDetailsDTO = new LeadDetailsDTO();
				BulkUploadMsgDto msgDto = null;
				leadDetailsDTO = leadList.get(ii);
				long mobileNumber = leadDetailsDTO.getContactNo(); 
				
				int productIds[] = new int[leadDetailsDTO.getProductIds().length];
				productIds = leadDetailsDTO.getProductIds();
				//System.out.println("");
				int productIdsToBeInserted[] = new int[productIds.length];
				 
				// Starting a Transaction 				 
				try
				{
				  con.setAutoCommit(false);
		            
				// check for existing prospect Id				
				//StringBuffer select_existing_prospect_id_query = new StringBuffer("select PROSPECT_ID from LEAD_PROSPECT_CUSTOMER where PROSPECT_MOBILE_NUMBER = ?  ");
				//pstmt = con.prepareStatement(select_existing_prospect_id_query.toString());
				  
				//Code added by Neetika to find out City zone, City code, zone code if we have the pin code in Qualified leads 
				  if (!leadDetailsDTO.getPinCode().equalsIgnoreCase(""))
		    		{
					  	pstmtPinCode.setString(1, leadDetailsDTO.getPinCode().trim());
					  	pstmtPinCode.setInt(2, leadDetailsDTO.getCircleMasterId());
					  	pstmtPinCode.setInt(3, leadDetailsDTO.getProductLobId());
					  	rsPin=pstmtPinCode.executeQuery();
		    			if(rsPin.next())
		    			{
		    				cityZone_code=rsPin.getString("CITY_ZONE_CODE");
		    				leadDetailsDTO.setCityZoneCode(cityZone_code);
		    				city_code=rsPin.getString("CITY_CODE");
		    				leadDetailsDTO.setCityCode(city_code);
		    				zone_code=rsPin.getString("ZONE_CODE");
		    				leadDetailsDTO.setZoneCode(zone_code);
		    				
		    			}
		    			/*pstmtCityZone.setString(1, leadDetailsDTO.getPinCode().trim());
		    			pstmtCityZone.setInt(2, leadDetailsDTO.getProductLobId());
		    			rsCityZone=pstmtCityZone.executeQuery();
		    			if(rsCityZone.next())
		    			{
		    				city_code=rsCityZone.getString("CITY_CODE");
		    				leadDetailsDTO.setCityCode(city_code);
		    				
		    			}
		    			pstmtCity.setString(1, leadDetailsDTO.getPinCode().trim());
		    			pstmtCity.setInt(2, leadDetailsDTO.getProductLobId());
		    			rsCity=pstmtCity.executeQuery();
		    			if(rsCity.next())
		    			{
		    				zone_code=rsCity.getString("zone_code");
		    				leadDetailsDTO.setZoneCode(zone_code);
		    				
		    			}*/
		    			logger.info(cityZone_code+"cityZone_code .. "+"city_code... "+city_code+"zone_code.. "+zone_code);
		    			
		    		}
					//Code added by Neetika to find out City code, zone code if we have the city zone code in open leads 
				  //find out citzone, City code, zone code if we have the pin code in qualified leads
				  	if (leadDetailsDTO.getPinCode().equalsIgnoreCase(""))
		    		{
		    		if (!leadDetailsDTO.getCityZoneCode().trim().equalsIgnoreCase("") && leadDetailsDTO.getCityZoneCode().length()>0)
		    		{
		    			pstmtCityZone.setString(1, leadDetailsDTO.getCityZoneCode().trim());
		    			rsCityZone=pstmtCityZone.executeQuery();
		    			if(rsCityZone.next())
		    			{
		    				city_code=rsCityZone.getString("CITY_CODE");
		    				leadDetailsDTO.setCityCode(city_code);
		    				
		    			}
		    			pstmtCity.setString(1, leadDetailsDTO.getCityZoneCode().trim());
		    			rsCity=pstmtCity.executeQuery();
		    			if(rsCity.next())
		    			{
		    				zone_code=rsCity.getString("zone_code");
		    				leadDetailsDTO.setZoneCode(zone_code);
		    				
		    			}
		    			logger.info("city_code"+city_code+"zone_code "+zone_code);
		    			
		    		}
		    		}
				  	//if pin code is blank , cityzone is blank but we have city code
				  	if (leadDetailsDTO.getPinCode().equalsIgnoreCase(""))
		    		{
		    		if (leadDetailsDTO.getCityZoneCode().equalsIgnoreCase("")) 
		    		{ // cityzone if present then dont check city code..if cityzone absent then only check city code based on circle and product id
					if (!leadDetailsDTO.getCityCode().equals("")) 
						{
							pstmtZoneFromCity.setString(1, leadDetailsDTO.getCityCode().trim());
							rsZoneFromCity=pstmtZoneFromCity.executeQuery();
			    			if(rsZoneFromCity.next())
			    			{
			    				zone_code=rsZoneFromCity.getString("zone_code");
			    				leadDetailsDTO.setZoneCode(zone_code);
			    				
			    			}
		    		
		    			logger.info("city_code"+leadDetailsDTO.getCityCode().trim()+"zone_code "+zone_code);
		    			
						}
		    		}
		    		}
		    		//code addition by Neetika ends..
				  
				pstmt.setLong(1, leadDetailsDTO.getContactNo());		
				rs = pstmt.executeQuery();
				
				if(rs.next())
				{
					// Prospect Id found, customer details already exist.
					prospectId = rs.getInt("PROSPECT_ID");
					
					// Check for open Lead 				
					String numberOfDays = "";
					/* New Code by parnika */
					
		    		if((source == Constants.SOURCE_TYPE_BULK_UPLOAD) || (source ==  Constants.SOURCE_TYPE_CALL_CENTER)){
		    			 	            		
	            		if (PropertyReader.getAppValue("duplicate.open.logic").trim().equalsIgnoreCase("Y") ){ // To check whether duplicate logic is applicable or not.
	            			pstmt1.setInt(1, prospectId);
	            			if (PropertyReader.getAppValue("duplicate.open.daywise").trim().equalsIgnoreCase("Y") ){ // To check whether day-wise duplicate logic is applicable or not.
	            				numberOfDays =PropertyReader.getAppValue("duplicate.open.days").trim();
	            				pstmt1.setInt(2, Integer.parseInt(numberOfDays));
	          					if (PropertyReader.getAppValue("duplicate.open.productwise").trim().equalsIgnoreCase("Y") ){ // To check whether Product wise duplicate logic is applicable or not.				        		
	        						int kk=0;
	        						for (int jj = 0; jj < productIds.length; jj++)
	        						{	
	        							
	        							pstmt1.setInt(3, productIds[jj]);
	        							rs1 = pstmt1.executeQuery();
	        							if(rs1.next())
	        							{
	        								productFound = true;
	        								// Lead Id found in open state, do nothing
	        								leadId = rs1.getLong("LEAD_ID");
	        								logger.info("Lead Already Available for the Customer in product wise:"+leadId);
	        								// set specific message for existing lead - TBD
	        								msgDto = new BulkUploadMsgDto();
	        								msgDto.setMessage("Lead Already Exists.");
	        								msgDto.setMsgId(Constants.LEAD_INSERT_EXISTING_LEAD);
	        								msgDto.setLeadId(leadId);
	        								arr.add(msgDto);
	        							}
	        							else
	        							{   productFound = false; 
	        								productIdsToBeInserted[kk++]=leadDetailsDTO.getProductIds()[jj];
	        							}

	        				    	}

	            			}
	          					else{// If not product wise only day-wise
	          						
	    						rs1 = pstmt1.executeQuery();		    						
	    						int kk=0;
	    						for (int jj = 0; jj < productIds.length; jj++)
	    						{
	    							
	    								if(rs1.next())
	    								{
	    									productFound = true;
	    									// Lead Id found in open state, do nothing
	    									leadId = rs1.getLong("LEAD_ID");
	    									logger.info("Lead Already Available for the Customer :"+leadId);
	    									// set specific message for existing lead - TBD
	    									msgDto = new BulkUploadMsgDto();
	    									msgDto.setMessage("Lead Already Exists.");
	    									msgDto.setMsgId(Constants.LEAD_INSERT_EXISTING_LEAD);
	    									msgDto.setLeadId(leadId);
	    									arr.add(msgDto);
	    								}
	    								else
	    								{   productFound = false; 
	    									productIdsToBeInserted[kk++]=leadDetailsDTO.getProductIds()[jj];
	    								}
	    						}
	          					}
	    						
	    					}
	            			else{ // If not day-wise then 
	            					if (PropertyReader.getAppValue("duplicate.open.productwise").trim().equalsIgnoreCase("Y") ){ // To check whether Product wise duplicate logic is applicable or not.				        		
		        						int kk=0;
		        						for (int jj = 0; jj < productIds.length; jj++)
		        						{	
		        							
		        							pstmt1.setInt(2, productIds[jj]);
		        							rs1 = pstmt1.executeQuery();
		        							if(rs1.next())
		        							{
		        								productFound = true;
		        								// Lead Id found in open state, do nothing
		        								leadId = rs1.getLong("LEAD_ID");
		        								logger.info("Lead Already Available for the Customer :"+leadId);
		        								// set specific message for existing lead - TBD
		        								msgDto = new BulkUploadMsgDto();
		        								msgDto.setMessage("Lead Already Exists.");
		        								msgDto.setMsgId(Constants.LEAD_INSERT_EXISTING_LEAD);
		        								msgDto.setLeadId(leadId);
		        								arr.add(msgDto);
		        							}
		        							else
		        							{   productFound = false; 
		        								productIdsToBeInserted[kk++]=leadDetailsDTO.getProductIds()[jj];
		        							}
	
		        				    	}
	        				    	}
	            					else{
		            					// Only Based on prospect Id
		        						int kk=0;
		        						for (int jj = 0; jj < productIds.length; jj++)
		        						{	
		        							
		        							rs1 = pstmt1.executeQuery();
		        							if(rs1.next())
		        							{
		        								productFound = true;
		        								// Lead Id found in open state, do nothing
		        								leadId = rs1.getLong("LEAD_ID");
		        								logger.info("Lead Already Available for the Customer :"+leadId);
		        								// set specific message for existing lead - TBD
		        								msgDto = new BulkUploadMsgDto();
		        								msgDto.setMessage("Lead Already Exists.");
		        								msgDto.setMsgId(Constants.LEAD_INSERT_EXISTING_LEAD);
		        								msgDto.setLeadId(leadId);
		        								arr.add(msgDto);
		        							}
		        							else
		        							{   productFound = false; 
		        								productIdsToBeInserted[kk++]=leadDetailsDTO.getProductIds()[jj];
		        							}
	
		        				    	}
	            					}
	            			}
	            		}
		    			
		    		} // End of duplicate query for open leads
		        		
		        		if(source == Constants.SOURCE_TYPE_OUTBOUND){
		        			
		        			
		            		
		            		if (PropertyReader.getAppValue("duplicate.qualified.logic").trim().equalsIgnoreCase("Y") ){ // To check whether duplicate logic is applicable or not.
		            			pstmt1.setInt(1, prospectId);
		            			if (PropertyReader.getAppValue("duplicate.qualified.daywise").trim().equalsIgnoreCase("Y") ){ // To check whether day-wise duplicate logic is applicable or not.
		            				numberOfDays =PropertyReader.getAppValue("duplicate.qualified.days").trim();
		            				pstmt1.setInt(2, Integer.parseInt(numberOfDays));
		          					if (PropertyReader.getAppValue("duplicate.qualified.productwise").trim().equalsIgnoreCase("Y") ){ // To check whether Product wise duplicate logic is applicable or not.				        		
		        						int kk=0;
		        						for (int jj = 0; jj < productIds.length; jj++)
		        						{	
		        							pstmt1.setInt(3, productIds[jj]);
		        							rs1 = pstmt1.executeQuery();
		        							if(rs1.next())
		        							{
		        								productFound = true;
		        								// Lead Id found in open state, do nothing
		        								leadId = rs1.getLong("LEAD_ID");
		        								logger.info("Lead Already Available for the Customer :"+leadId);
		        								// set specific message for existing lead - TBD
		        								msgDto = new BulkUploadMsgDto();
		        								msgDto.setMessage("Lead Already Exists.");
		        								msgDto.setMsgId(Constants.LEAD_INSERT_EXISTING_LEAD);
		        								msgDto.setLeadId(leadId);
		        								arr.add(msgDto);
		        							}
		        							else
		        							{   productFound = false; 
		        								productIdsToBeInserted[kk++]=leadDetailsDTO.getProductIds()[jj];
		        							}
	
		        				    	}

		            			}
		          					else{// If not product wise only day-wise
		    						rs1 = pstmt1.executeQuery();		    						
		    						int kk=0;
		    						for (int jj = 0; jj < productIds.length; jj++)
		    						{
		    								if(rs1.next())
		    								{
		    									productFound = true;
		    									// Lead Id found in open state, do nothing
		    									leadId = rs1.getLong("LEAD_ID");
		    									logger.info("Lead Already Available for the Customer :"+leadId);
		    									// set specific message for existing lead - TBD
		    									msgDto = new BulkUploadMsgDto();
		    									msgDto.setMessage("Lead Already Exists.");
		    									msgDto.setMsgId(Constants.LEAD_INSERT_EXISTING_LEAD);
		    									msgDto.setLeadId(leadId);
		    									arr.add(msgDto);
		    								}
		    								else
		    								{   productFound = false; 
		    									productIdsToBeInserted[kk++]=leadDetailsDTO.getProductIds()[jj];
		    								}
		    						}
		            			}
		    						
		    					}
		            			else{ // If not day-wise then 
		            					if (PropertyReader.getAppValue("duplicate.qualified.productwise").trim().equalsIgnoreCase("Y") ){ // To check whether Product wise duplicate logic is applicable or not.				        		
			        						int kk=0;
			        						for (int jj = 0; jj < productIds.length; jj++)
			        						{	
			        							pstmt1.setInt(2, productIds[jj]);
			        							rs1 = pstmt1.executeQuery();
			        							if(rs1.next())
			        							{
			        								productFound = true;
			        								// Lead Id found in open state, do nothing
			        								leadId = rs1.getLong("LEAD_ID");
			        								logger.info("Lead Already Available for the Customer :"+leadId);
			        								// set specific message for existing lead - TBD
			        								msgDto = new BulkUploadMsgDto();
			        								msgDto.setMessage("Lead Already Exists.");
			        								msgDto.setMsgId(Constants.LEAD_INSERT_EXISTING_LEAD);
			        								msgDto.setLeadId(leadId);
			        								arr.add(msgDto);
			        							}
			        							else
			        							{   productFound = false; 
			        								productIdsToBeInserted[kk++]=leadDetailsDTO.getProductIds()[jj];
			        							}
		
			        				    	}
		        				    	}
		            					else{
		            					// Only Based on prospect Id
		        						int kk=0;
		        						for (int jj = 0; jj < productIds.length; jj++)
		        						{	
		        							rs1 = pstmt1.executeQuery();
		        							if(rs1.next())
		        							{
		        								productFound = true;
		        								// Lead Id found in open state, do nothing
		        								leadId = rs1.getLong("LEAD_ID");
		        								logger.info("Lead Already Available for the Customer :"+leadId);
		        								// set specific message for existing lead - TBD
		        								msgDto = new BulkUploadMsgDto();
		        								msgDto.setMessage("Lead Already Exists.");
		        								msgDto.setMsgId(Constants.LEAD_INSERT_EXISTING_LEAD);
		        								msgDto.setLeadId(leadId);
		        								arr.add(msgDto);
		        							}
		        							else
		        							{   productFound = false; 
		        								productIdsToBeInserted[kk++]=leadDetailsDTO.getProductIds()[jj];
		        							}
	
		        				    	}
		            					}
		            			}
		            		}
		            		
		            		} // End of duplicate query for qualified Leads
		        		
					
					
					if(!productFound){ // Update only if it is not a duplicate lead
					
						StringBuffer UPDATE_PROSPECT_CUSTOMER = new StringBuffer("UPDATE LEAD_PROSPECT_CUSTOMER SET CUSTOMER_NAME = ? ");
						
						
						if (leadDetailsDTO.getEmail() != null && !(leadDetailsDTO.getEmail().trim().equals("")) ){
							UPDATE_PROSPECT_CUSTOMER.append(",  EMAIL=? ");
						}
						if (leadDetailsDTO.getAlternateContactNo() != null && !(leadDetailsDTO.getAlternateContactNo().trim().equals("")) ){
							UPDATE_PROSPECT_CUSTOMER.append(", ALTERNATE_CONTACT_NUMBER=? ");
						}
						if (leadDetailsDTO.getLandlineNo() != null && !(leadDetailsDTO.getLandlineNo().trim().equals("")) ){
							UPDATE_PROSPECT_CUSTOMER.append(", LANDLINE_NUMBER=? ");
						}
										
						
						UPDATE_PROSPECT_CUSTOMER.append("  WHERE PROSPECT_ID = ?");
				
					pstmt2 = con.prepareStatement(UPDATE_PROSPECT_CUSTOMER.toString());
					pstmt2.setString(paramIndex++,leadDetailsDTO.getCustomerName() );
					
					if (leadDetailsDTO.getEmail() != null && !(leadDetailsDTO.getEmail().trim().equals("")) ){
						pstmt2.setString(paramIndex++,leadDetailsDTO.getEmail() );
					}
					if (leadDetailsDTO.getAlternateContactNo() != null && !(leadDetailsDTO.getAlternateContactNo().trim().equals("")) ){
						pstmt2.setString(paramIndex++,leadDetailsDTO.getAlternateContactNo() );
					}
					if (leadDetailsDTO.getLandlineNo() != null && !(leadDetailsDTO.getLandlineNo().trim().equals("")) ){
						pstmt2.setString(paramIndex++,leadDetailsDTO.getLandlineNo() );
					}
					
					pstmt2.setInt(paramIndex++, prospectId);
					
					pstmt2.execute();
					
					}
		            
					/* End of changes by Parnika for updating customer */

				}
				else
				{
					// No existing prospect Id found, Insert customer details, get prospect Id.
		            //String INSERT_LEAD_PROSPECT_CUSTOMER ="INSERT INTO LEAD_PROSPECT_CUSTOMER(PROSPECT_ID, CUSTOMER_NAME, PROSPECT_MOBILE_NUMBER, ALTERNATE_CONTACT_NUMBER, LANDLINE_NUMBER, ADDRESS1, ADDRESS2, CITY, PINCODE, STATE, CIRCLE_ID, APPOINTMENT_TIME, EMAIL, PRIMARY_LANGUAGE ,IS_CUSTOMER,ZONE,SUB_ZONE, MARITAL_STATUS,RSU_ID ) VALUES(NEXTVAL FOR SEQ_PROSPECTS_ID,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		                       
		            //pstmt3 = con.prepareStatement(INSERT_LEAD_PROSPECT_CUSTOMER);
					logger.info("For value of ii:::: in insertion in lead prospect customer" + ii);
		            
		            pstmt3.setString(parameterIndex++, leadDetailsDTO.getCustomerName());
		            pstmt3.setLong(parameterIndex++, leadDetailsDTO.getContactNo());
	            	pstmt3.setString(parameterIndex++, leadDetailsDTO.getAlternateContactNo());
		            pstmt3.setString(parameterIndex++, leadDetailsDTO.getLandlineNo());		            
		           
		            pstmt3.setString(parameterIndex++, leadDetailsDTO.getEmail());
		            pstmt3.setString(parameterIndex++, leadDetailsDTO.getLanguage());            
		           // pstmt3.setString(parameterIndex++, leadDetailsDTO.getExistingCustomer());
		           // pstmt3.setString(parameterIndex++, leadDetailsDTO.getZoneCode());
		           // pstmt3.setString(parameterIndex++, leadDetailsDTO.getSubZoneName());
		            pstmt3.setString(parameterIndex++, leadDetailsDTO.getMaritalStatus());
		           // pstmt3.setString(parameterIndex++, leadDetailsDTO.getRsuCode());
		            pstmt3.setString(parameterIndex++, leadDetailsDTO.getCreatedBy());//Neetika Updated by column
		            pstmt3.executeUpdate();
		            
					pstmt4 = con.prepareStatement(select_existing_prospect_id_query.toString());
					pstmt4.setLong(1, leadDetailsDTO.getContactNo());
					rs = pstmt4.executeQuery();
					
					if(rs.next())
					{
						// Prospect Id obtained for new request.
						prospectId = rs.getInt("PROSPECT_ID");
					}
					
					
					
				}
				
				// Insert Lead Details as Prospect Id is available either existing or new. 
				//String INSERT_LEAD_DATA = "INSERT INTO LEAD_DATA(LEAD_ID,PROSPECT_ID, SUB_SOURCE, SOURCE, PRODUCT_ID, LEAD_STATUS_ID, REQUEST_TYPE, CREATE_TIME, REMARKS) VALUES(?,?, ?,?,?,?,?, current timestamp, ?)";
				
				//String INSERT_LEAD_TRANSACTION = "INSERT INTO LEAD_TRANSACTION(PRODUCT_ID, LEAD_ASSIGNMENT_TIME, LEAD_STATUS_ID,SUB_STATUS_ID, LEAD_ID, REMARKS, TRANSACTION_TIME,UPDATED_BY, UD_ID, EXPECTED_CLOSURE_DATE, LEAD_CLOSURE_TIME)  VALUES((SELECT PRODUCT_LOB_ID  FROM PRODUCT_MSTR WHERE PRODUCT_ID = ?), current timestamp, ?, ?,?,?,current timestamp,?,?,current timestamp, current timestamp )  ";
				
				//Neetika 19Nov
				if(!productFound)
				{
				logger.info("For value of ii:::: in insertion in lead prospect detailss" + ii+" City code   "+leadDetailsDTO.getCityCode());
				//CITY_CODE, STATE, ZONE_CODE, EMAIL, CIRCLE_ID, ADDRESS2,PRIMARY_LANGUAGE,PINCODE, ALTERNATE_CONTACT_NUMBER, LANDLINE_NUMBER, 
				//APPOINTMENT_TIME, ADDRESS1,PROSPECT_ID,  IS_CUSTOMER, MARITAL_STATUS, SUB_ZONE, RSU_CODE, UPDATED_DT, UPDATED_BY
				for (int jj = 0; jj < productIds.length && productIds[jj]>0; jj++)
				{
							productLobId =0;
							leadId = getLeadId( leadDetailsDTO.getContactNo());		
							productLobId  =mstrService.getProductLobId(productIds[jj]);
							/* End of changes by Parnika */
							
							/* No Entry Found in Lead Detail , So inserting Data */
							parameterIndex=1;
							pstmt6.setString(parameterIndex++,leadDetailsDTO.getCityZoneCode());
							pstmt6.setString(parameterIndex++,leadDetailsDTO.getCityCode());
						
							pstmt6.setString(parameterIndex++, leadDetailsDTO.getStateCode());
							pstmt6.setString(parameterIndex++, leadDetailsDTO.getZoneCode());
							//pstmt6.setString(parameterIndex++, leadDetailsDTO.getEmail());
							pstmt6.setInt(parameterIndex++, leadDetailsDTO.getCircleId());
							pstmt6.setString(parameterIndex++, leadDetailsDTO.getAddress2());
							pstmt6.setString(parameterIndex++, leadDetailsDTO.getLanguage());  
							 
				            if("".equals(leadDetailsDTO.getPinCode()))
				            {		            	
				            	pstmt6.setString(parameterIndex++, null);
				            }else
				            {
				            	pstmt6.setInt(parameterIndex++, Integer.parseInt(leadDetailsDTO.getPinCode()));
				            }
				            pstmt6.setString(parameterIndex++, leadDetailsDTO.getAlternateContactNo());	
							pstmt6.setString(parameterIndex++, leadDetailsDTO.getLandlineNo());		
						    pstmt6.setString(parameterIndex++, leadDetailsDTO.getAppointmentTime());
						    pstmt6.setString(parameterIndex++, leadDetailsDTO.getAddress1());
						    pstmt6.setInt(parameterIndex++, prospectId);
						    pstmt6.setString(parameterIndex++, leadDetailsDTO.getExistingCustomer());
						    pstmt6.setString(parameterIndex++, leadDetailsDTO.getMaritalStatus());
						    pstmt6.setString(parameterIndex++, leadDetailsDTO.getSubZoneName());
						    pstmt6.setString(parameterIndex++, leadDetailsDTO.getRsuCode());
						    pstmt6.setString(parameterIndex++, leadDetailsDTO.getCreatedBy());//Neetika Updated by column
							pstmt6.setInt(parameterIndex++, productLobId);
							
							
							//Added By Bhaskar for three fields
							// pstmt6.setString(parameterIndex++,leadDetailsDTO.getAppointmentTime());
							
							pstmt6.setString(parameterIndex++,leadDetailsDTO.getExtraParam1());
							/* Added by Parnika on 13 May 2014 */
							//added by Nancy
							pstmt6.setString(parameterIndex++,leadDetailsDTO.getSalesChannelCode());
							pstmt6.setString(parameterIndex++,leadId.toString());	
							pstmt6.setString(parameterIndex++,leadDetailsDTO.getAppointmentEndTime());
							pstmt6.setString(parameterIndex++,leadDetailsDTO.getPytAmt());	
							pstmt6.setString(parameterIndex++,leadDetailsDTO.getTranRefno());
							pstmt6.setString(parameterIndex++,leadDetailsDTO.getTotalDue());
							
							/* End of changes by Parnika */
				            pstmt6.executeUpdate();
				            pstmt6.clearParameters();
				         
					
				
				   //Added By Bhaskar for lead creation time
				            CaptureLeadDataDAOImpl  dataDAOImpl = new CaptureLeadDataDAOImpl();
				   String maxId  ="100"+""+dataDAOImpl.getMaxId() ;
				    parameterIndex=1;      
				    pstmtld.setString(parameterIndex++,leadId.toString());	
				    pstmtld.setString(parameterIndex++,leadDetailsDTO.getExtraParam6());
				    pstmtld.setString(parameterIndex++,leadDetailsDTO.getFeasibilityParam());
				    pstmtld.setString(parameterIndex++,leadDetailsDTO.getDevicetaken());
				    
				    pstmtld.setString(parameterIndex++,leadDetailsDTO.getExtraParam7());
				    pstmtld.setString(parameterIndex++,maxId);
				  //  pstmtld.setString(parameterIndex++,leadDetailsDTO.getExtraParam1());
				    pstmtld.executeUpdate();
				            
				    //end By Bhaskar         
				      
				    //ADDED BY NANCY@@@@@@@@@@@@@@@@@ changes by beeru
				    if(PropertyReader.getAppValue("lms.4G.productLobId").trim().equalsIgnoreCase(String.valueOf(productLobId)) && leadDetailsDTO.getAppointmentStatus() !=null && leadDetailsDTO.getAppointmentStatus().length() >0) {
				    	pstmtld1.setString(1,leadId.toString());	
				    	pstmtld1.setString(2,leadDetailsDTO.getAppointmentStatus());
				    	pstmtld1.executeUpdate();
				    }
					            
				            
	           
	            pstmt8.setInt(1,prospectId);
	            pstmt8.setString(2, leadId.toString());
	            /* Commented By Parnika
	            pstmt8.setInt(2,mstrService.getProductLobId(productIds[jj])); //27 End Of commented by Parnika */
				rs = pstmt8.executeQuery();
				if(rs.next())
				leadprospectId = rs.getInt("LEAD_PROSPECT_ID");
				pstmt8.clearParameters();
				logger.info("leadprospectId"+leadprospectId+"prospectId"+prospectId+" lob: "+mstrService.getProductLobId(productIds[jj]));
				/* Commented by Parnika } */
				
				if(productFound)
				{
					productIds =  productIdsToBeInserted;
				}
				
				MasterDao mdao = MasterDaoImpl.masterDaoInstance();
	//			List<ProductDTO> telemediaProductList =  mdao.getTelemediaProductList();
				logger.info("leadStatusCode : "+ leadDetailsDTO.getLeadStatusId());
				
				
					parameterIndex = 1;
					
					
					
					// identifying product type & lead status					
					int leadStatusCode = leadDetailsDTO.getLeadStatusId();					
					
				
					
					pstmt5.setLong(parameterIndex++,leadId );
					pstmt5.setInt(parameterIndex++,prospectId );
					
					 if(leadDetailsDTO.getSubSourceId() > 0)
			            {
						 	pstmt5.setString(parameterIndex++, String.valueOf(leadDetailsDTO.getSubSourceId()));
			            }else
			            {
			            	pstmt5.setString(parameterIndex++, String.valueOf(Constants.SUB_SOURCE_TYPE_CALL_CENTER));
			            	
			            }
					 
					 if(leadDetailsDTO.getSourceId() > 0)
			            {
						 	pstmt5.setInt(parameterIndex++, leadDetailsDTO.getSourceId());
			            }else
			            {
			            	pstmt5.setInt(parameterIndex++, Constants.SOURCE_TYPE_CALL_CENTER);
			            }
					 
						     
		           
					pstmt5.setInt(parameterIndex++, productIds[jj]);

						pstmt5.setInt(parameterIndex++, leadStatusCode);
					
		            if(leadDetailsDTO.getRequestType() ==0)
		            {
		            	pstmt5.setString(parameterIndex++, null);
		            }else
		            {
		            	 pstmt5.setInt(parameterIndex++, leadDetailsDTO.getRequestType());  
		            }
					pstmt5.setString(parameterIndex++, leadDetailsDTO.getRemarks());
					//Added by neetika
					pstmt5.setInt(parameterIndex++, leadprospectId); //make it as lead prospect id
					pstmt5.setString(parameterIndex++, leadDetailsDTO.getCreatedBy());
				
					//end by neetika
					// adding by create
					pstmt5.setString(parameterIndex++, leadDetailsDTO.getCampaign());// added by pratap for campaign field added in bulk lead regi
					/* Added by Parnika */
					pstmt5.setString(parameterIndex++, leadDetailsDTO.getLeadCategory()); 
					/* End of changes by Parnika */
					/* Added by Amarjeet */
					pstmt5.setInt(parameterIndex++, leadStatusCode);
					pstmt5.setInt(parameterIndex++, leadDetailsDTO.getLeadSubSubStatusid());
					
					pstmt5.setString(parameterIndex++, leadDetailsDTO.getCaf());
					pstmt5.setString(parameterIndex++, leadDetailsDTO.getPlan());
					
					/* End of changes by Amarjeet */
					pstmt5.execute();	
					
			   // Inserting LEAD TRANSACTION Details into LEAD_TRANSACTION table with status 305 for non-telemedia and status 300 for telemedia in case of Qualified Leads 
				// Status 200 in case of Open Leads.
					
					//pstmtLTX = con.prepareStatement(INSERT_LEAD_TRANSACTION);
					int parameterIndex2=1;
					logger.info("INSERT_LEAD_TRANSACTION "+productIds[jj]+" leadStatusCode  "+leadStatusCode+" leadId "+leadId+" created by "+leadDetailsDTO.getCreatedBy()+" prod id "+productIds[jj]+" lead pros : "+leadprospectId);
					pstmtLTX.setInt(parameterIndex2++, productIds[jj]);	
					pstmtLTX.setInt(parameterIndex2++,leadStatusCode );
					pstmtLTX.setInt(parameterIndex2++,leadStatusCode );
					pstmtLTX.setLong(parameterIndex2++,leadId );
					pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getRemarks());	
					pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getCreatedBy());
					pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getUdId());
					//Added by neetika
				
					pstmtLTX.setInt(parameterIndex2++, (productIds[jj])); //lead product id
					pstmt7.setString(1,leadDetailsDTO.getCreatedBy() );
					pstmt7.setString(2,leadDetailsDTO.getCreatedBy() );
					
					rs=pstmt7.executeQuery();
					if(rs.next())
					{
						clientip=rs.getString(1);
					}
					pstmtLTX.setString(parameterIndex2++, clientip);//change it to IP
					//end
					pstmtLTX.execute();
					
					
				/* Added by Parnika , if Product is telemedia , then a row for qualified (305) is added in transaction table for Qualified Leads */
					if (PropertyReader.getAppValue("lms.telemedia.productLobId").equals(mstrService.getProductLobId(productIds[jj])+"") && source == Constants.SOURCE_TYPE_OUTBOUND){
						
						
						//pstmtLTX1 = con.prepareStatement(INSERT_LEAD_TRANSACTION);
						parameterIndex2=1;
						pstmtLTX1.setInt(parameterIndex2++, productIds[jj]);	
						pstmtLTX1.setInt(parameterIndex2++,Integer.parseInt(Constants.LEAD_STATUS_QUALIFIED));
						pstmtLTX1.setInt(parameterIndex2++,Integer.parseInt(Constants.LEAD_STATUS_QUALIFIED) );
						pstmtLTX1.setLong(parameterIndex2++,leadId );
						pstmtLTX1.setString(parameterIndex2++, leadDetailsDTO.getRemarks());	
						pstmtLTX1.setString(parameterIndex2++, leadDetailsDTO.getCreatedBy());
						pstmtLTX1.setString(parameterIndex2++, leadDetailsDTO.getUdId());
						//Added by neetika
						pstmtLTX1.setInt(parameterIndex2++, (productIds[jj])); //lead product id
						pstmt7.setString(1,leadDetailsDTO.getCreatedBy() );
						pstmt7.setString(2,leadDetailsDTO.getCreatedBy() );
						
						rs=pstmt7.executeQuery();
						if(rs.next())
						{
							clientip=rs.getString(1);
						}
						pstmtLTX1.setString(parameterIndex2++,clientip); //change it to IP
						//end
						pstmtLTX1.execute();
						
					}
					
					if((source == Constants.SOURCE_TYPE_BULK_UPLOAD) || (source ==  Constants.SOURCE_TYPE_CALL_CENTER)){
							String meassage = null;
							ps = con.prepareStatement(SELECT_SMS_TEMPLATE);
							ps.setString(1, PropertyReader.getAppValue("lms.leadRegisteration.template"));
							rsTemp = ps.executeQuery();
							while(rsTemp.next()){
								 meassage = rsTemp.getString("MESSAGE_TEMPLATE")+" " + leadId;
								 
							}
							try{
								String smsFlag = mstrService.getParameterName(PropertyReader.getAppValue("leadRegisteration.smsFlag"));
								if(smsFlag.equalsIgnoreCase("Y")){
									new SendSMSXML().sendSms(leadDetailsDTO.getContactNo()+"",meassage);
									ps4=con.prepareStatement(INSERT_SMS_TRANSACTIONS);
									ps4.setString(1, leadDetailsDTO.getContactNo()+"");
									ps4.setString(2, meassage);
									ps4.setInt(3, 1);
									ps4.setString(4, "");
									ps4.executeUpdate();
								}
								
						
							}
							catch(Exception e){
								e.printStackTrace();
							}
						
					}
					
					//sms sending to sales executive no
					String salesExecutiveNo   = leadDetailsDTO.getExtraParam1();
					if(salesExecutiveNo !=null && leadDetailsDTO.getLeadStatusId() > 0 && leadDetailsDTO.getLeadStatusId() == Integer.parseInt(Constants.LEAD_STATUS_QUALIFIED) && productLobId == Constants.PRODUCT_LOB_4G){
						if(salesExecutiveNo != null && salesExecutiveNo.trim().length() == 10){
							String meassage = null;
							ps = con.prepareStatement(SELECT_SMS_TEMPLATE);
							ps.setString(1, PropertyReader.getAppValue("lms.4GleadRegisteration.template"));
							rsTemp = ps.executeQuery();
							if(rsTemp.next()){
								meassage = rsTemp.getString("MESSAGE_TEMPLATE");
							try{
								
								if(meassage !=null && meassage.contains(Constants.ILMS_LEAD))
									meassage = meassage.replace(Constants.ILMS_LEAD, leadId+"");
								if(meassage !=null &&  meassage.contains(Constants.LEAD_PROSPECT_CUSTOMER))
									meassage = meassage.replace(Constants.LEAD_PROSPECT_CUSTOMER, leadDetailsDTO.getContactNo()+"");
								
								String smsFlag = mstrService.getParameterName(PropertyReader.getAppValue("4GleadRegisteration.smsFlag"));
								if(meassage !=null &&  smsFlag.equalsIgnoreCase("Y")){
									new SendSMSXML().sendSms(salesExecutiveNo+"",meassage.toString());
									ps4=con.prepareStatement(INSERT_SMS_TRANSACTIONS);
									ps4.setString(1, salesExecutiveNo+"");
									ps4.setString(2, meassage.toString());
									ps4.setInt(3, 1);
									ps4.setString(4, "");
									ps4.executeUpdate();
								}
							}
							catch(Exception e){
								e.printStackTrace();
							}
						}
						}
						
					}
					
				/* End of changes by Parnika */
					
					msgDto = new BulkUploadMsgDto();
					msgDto.setMessage("Lead Created Successfully.");

					
					msgDto.setMsgId(Constants.LEAD_INSERT_NEW_LEAD);
					msgDto.setLeadId(leadId);
					arr.add(msgDto);
					/* Added by Parnika for Lead Priority */
					//productLobId = mstrService.getProductLobId(productIds[jj]);
				}
				con.commit();
				
				// Committing current Prospect's details

				try {
					if (leadId!=0){
						
						ResourceBundle rb=ResourceBundle.getBundle("ApplicationResources");
						String schemaName=rb.getString("lms.schema.bulk.download");
						cs = con.prepareCall("{call "+schemaName+".PRC_CALULATE_SCORE(?,?,?,?,?)}");
						cs.setLong(1, leadId);
						if(leadDetailsDTO.getLeadStatusId()==Integer.parseInt(Constants.LEAD_STATUS_OPEN)){
							cs.setShort(2,Constants.SCORE_ONE);
							
						}
							
						else if(leadDetailsDTO.getLeadStatusId()==Integer.parseInt(Constants.LEAD_STATUS_QUALIFIED)){
							cs.setShort(2,Constants.SCORE_TWO);
							
						}
							
						else if(leadDetailsDTO.getLeadStatusId()==Integer.parseInt(Constants.LEAD_STATUS_VERIFICATION) && productLobId == Constants.PRODUCT_LOB_ID ){
							cs.setShort(2,Constants.SCORE_TWO);
							
						}
							
						else{
							cs.setShort(2,Constants.SCORE_ONE);
							
						}
							
						cs.registerOutParameter(3, java.sql.Types.VARCHAR);
						cs.registerOutParameter(4, java.sql.Types.VARCHAR);
						cs.registerOutParameter(5, java.sql.Types.INTEGER);
						cs.execute();
					}
				} catch (Exception e) {
					
					e.printStackTrace();
				}
					
					
				con.commit();
				
				}
				}
				catch (Exception e) {
					con.rollback();
					e.printStackTrace();
					msgDto = new BulkUploadMsgDto();
					msgDto.setMessage("Lead create Failed.");
					msgDto.setMsgId(Constants.LEAD_INSERT_FAIL);
					msgDto.setMobileNo(mobileNumber);		
					arr.add(msgDto);
				}	
				///changes to be made
			}
        }
        catch(Exception e1)
	        {
	            e1.printStackTrace();
	            throw new LMSException(e1.getMessage(), e1);
	        }
        
        finally{
		        try
		        {
		        	con.setAutoCommit(true);
		          /* //DBConnection.releaseResources(con, pstmt, rs);
		            //DBConnection.releaseResources(null, pstmt1, null);
		            //DBConnection.releaseResources(null, pstmt2, null);
		            //DBConnection.releaseResources(null, pstmt3, null);
		            //DBConnection.releaseResources(null, pstmt4, null);
		            //DBConnection.releaseResources(null, pstmt5, null);
		            //DBConnection.releaseResources(null, pstmtLTX, null);
		            //DBConnection.releaseResources(null, null, rs1);
		            //DBConnection.releaseResources(null, pstmtLTX1, null);*/
		            
		            /* Added By Neetika */
		           /* //DBConnection.releaseResources(null, pstmt6, null);
		            //DBConnection.releaseResources(null, pstmt8, null);		          
		    		//DBConnection.releaseResources(null, pstmtCityZone, rsCityZone);
		    		//DBConnection.releaseResources(null, pstmtCity, rsCity);
		    		//DBConnection.releaseResources(null, pstmtZoneFromCity, rsZoneFromCity);
		    		//DBConnection.releaseResources(null, pstmtPinCode, rsPin);
		    		//DBConnection.releaseResources(null, pstmtleadprosDt, rsleadprosDt);
		    		//DBConnection.releaseResources(null, pstmtUpdleadprosDt, null);	*/	  		
		    		/* End of changes by Neetika */
		    		
		        }
		        catch(Exception e)
		        {
		            e.printStackTrace();
		            throw new DAOException(e.getMessage(), e);
		        }
        }
        logger.info("Array Size:"+arr.size());
        return arr;      
    }
    
    public void  insertDirtyRecord(List<LeadDetailsDTO> leadList, int source) throws DAOException
    {
    	Connection con=null;
        PreparedStatement pstmt=null;
        PreparedStatement pstmtProduct=null;
        ResultSet rsetProduct=null;
        int productIds[];
        int productIdsToBeInserted[];
        String prospectMobileNo=null;
        String prospectName=null;
		String alternateContNo=null;
		String address1=null;
		String address2=null;
		String city=null;
		String pincode=null;
		String circleId=null;
		// use source from argument of method
		String subSourceId=null;
		String requestType=null;
		String appointmentTime=null;
		String product=null;
		// get product name from DB on basis of product id
		String email=null;
		String primaryLanguage=null;
		String DIRTY_LEAD_INSERT_QUERY="INSERT INTO DIRTY_LEAD(PROSPECT_NAME, PROSPECT_MOBILE_NUMBER," +
		" ALTERNATE_CONTACT_NUMBER, ADDRESS1, ADDRESS2, CITY, PINCODE, CIRCLE, SOURCE, SUB_SOURCE," +
		" REQUEST_TYPE, APPOINTMENT_TIME,  PRODUCT, EMAIL, PRIMARY_LANGUAGE," +
		" CREATE_TIME, ERROR_MESSAGE) "+ 
		" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current timestamp, ?)";
		String PRODUCT_NAME_BY_PROD_ID="SELECT PRODUCT_NAME FROM PRODUCT_MSTR WHERE PRODUCT_ID=?  ";
    	try
    	{
            con = DBConnection.getDBConnection(); 
            con.setAutoCommit(false);
            pstmt= con.prepareStatement(DIRTY_LEAD_INSERT_QUERY);
            pstmtProduct=con.prepareStatement(PRODUCT_NAME_BY_PROD_ID);
            for(int ii=0; ii< leadList.size(); ii++)
			{
            	int jj=0;
				LeadDetailsDTO leadDetailsDTO = new LeadDetailsDTO();
				leadDetailsDTO = leadList.get(ii);
				productIds = new int[leadDetailsDTO.getProductIds().length];
				productIds = leadDetailsDTO.getProductIds();
				productIdsToBeInserted = new int[productIds.length];
				productIdsToBeInserted[ii]=leadDetailsDTO.getProductIds()[jj];
				pstmtProduct.setInt(1, productIdsToBeInserted[ii]);
				rsetProduct=pstmtProduct.executeQuery();
				if(rsetProduct.next())
				{
					product=rsetProduct.getString("PRODUCT_NAME");
				}
				prospectMobileNo = String.valueOf(leadDetailsDTO.getContactNo()); 
				prospectName=leadDetailsDTO.getCustomerName();
				alternateContNo=leadDetailsDTO.getAlternateContactNo();
				address1=leadDetailsDTO.getAddress1();
				address2=leadDetailsDTO.getAddress2();
				 city=leadDetailsDTO.getCityCode();
				pincode=leadDetailsDTO.getPinCode();
				 circleId=String.valueOf(leadDetailsDTO.getCircleId());
				// use source from argument of method
				 subSourceId=String.valueOf(leadDetailsDTO.getSubSourceId());
				 requestType=String.valueOf(leadDetailsDTO.getRequestType());
				appointmentTime=leadDetailsDTO.getAppointmentTime();
				// get product name from DB on basis of product id
				 email=leadDetailsDTO.getEmail();
				 primaryLanguage=leadDetailsDTO.getLanguage();
				// set create time as current timestamp
				// set error msg as DIRTY LEAD
				 pstmt.setString(1, prospectName);
				 pstmt.setString(2, prospectMobileNo);
				 pstmt.setString(3, alternateContNo);
				 pstmt.setString(4, address1);
				 pstmt.setString(5, address2);
				 pstmt.setString(6, city);
				 pstmt.setString(7, pincode);
				 pstmt.setString(8, circleId);
				 pstmt.setString(9, String.valueOf(source));
				 pstmt.setString(10, subSourceId);
				 pstmt.setString(11, requestType);
				 pstmt.setString(12, appointmentTime);
				 pstmt.setString(13, product);
				 pstmt.setString(14, email);
				 pstmt.setString(15, primaryLanguage);
				 pstmt.setString(16, "DIRTY_LEAD");
				 pstmt.executeUpdate();		 
			}
            con.commit();
            logger.info("Dirty lead inserted successfullyy:::::::::::::::");
    	}
    	catch(Exception ex)
    	{
    		 ex.printStackTrace();
	            throw new LMSException(ex.getMessage(), ex);
    	}
    	 finally{
		        try
		        {
		        	con.setAutoCommit(true);
		        	con.close();
		        	pstmt.close();
		        	pstmtProduct.close();
		        	rsetProduct.close();
		        }
		        catch(Exception e)
		        {
		            e.printStackTrace();
		            throw new DAOException(e.getMessage(), e);
		        }
     }
    }
    
    // Obtain LEAD ID from SEQ
	String SELECT_SEQ_LEAD_NO = "SELECT NEXTVAL FOR SEQ_LEAD_ID AS SEQ_ID FROM sysibm.SYSDUMMY1  ";
	public  Long getLeadId(Long mobileNo) throws DAOException
	{
		Long leadId=0l;
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<LeadDetailsDTO> leadList = new ArrayList<LeadDetailsDTO>();
		LeadDetailsDTO dto = null;
		try {
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(SELECT_SEQ_LEAD_NO);
			rs = ps.executeQuery();
			//rs.next();
			if(rs.next())
			leadId = Long.parseLong((mobileNo/100000)+""+ rs.getInt("SEQ_ID"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException("Exception occured while getting lead Id :  "+ e.getMessage(),e);
		} finally {
			try {
				//DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return leadId;
	}
	
	
	
    
    // change the query for Lead ID search - currently on mobile
    //protected static final String SQL_SELECT_LEAD_LIST ="select LD.LEAD_ID AS LEAD_ID, PM.PRODUCT_NAME AS PRODUCT_NAME, LPC.CUSTOMER_NAME AS CUSTOMER_NAME, LPC.PROSPECT_MOBILE_NUMBER AS PROSPECT_MOBILE_NUMBER, CM.CIRCLE_DESC AS CIRCLE_DESC, varchar_format (LD.CREATE_TIME, 'YYYY-MM-DD HH:MI:SS AM') AS CREATE_TIME, (SELECT LEAD_STATUS_NAME AS LEAD_STATUS FROM  LEAD_STATUS WHERE LEAD_STATUS_ID = LD.LEAD_STATUS_ID) FROM LEAD_DATA LD, LEAD_PROSPECT_CUSTOMER LPC , PRODUCT_MSTR PM, CIRCLE_MSTR CM WHERE LD.LEAD_ID = ?  AND LD.PROSPECT_ID = LPC.PROSPECT_ID AND LD.PRODUCT_ID = PM.PRODUCT_ID AND LPC.CIRCLE_ID = CM.CIRCLE_ID  ";
    // this query changed by pratap on as new table added for customer detail LEAD_PROSPECT_DETAILS
	protected static final String SQL_SELECT_LEAD_LIST ="SELECT	LD.LEAD_ID AS LEAD_ID,PM.PRODUCT_NAME AS PRODUCT_NAME,LPC.CUSTOMER_NAME AS CUSTOMER_NAME,LD.PRODUCT_ID AS PRODUCT_ID,LPC.PROSPECT_MOBILE_NUMBER AS PROSPECT_MOBILE_NUMBER,CM.CIRCLE_DESC AS CIRCLE_DESC,varchar_format (LD.CREATE_TIME, 'YYYY-MM-DD HH:MI:SS AM') AS CREATE_TIME, (	SELECT LEAD_STATUS_NAME AS LEAD_STATUS FROM	LEAD_STATUS	WHERE LEAD_STATUS_ID = LD.LEAD_STATUS_ID ),(SELECT PRODUCT_LOB_ID FROM PRODUCT_MSTR WHERE PRODUCT_ID=LD.PRODUCT_ID) FROM LEAD_DATA LD,LEAD_PROSPECT_CUSTOMER LPC ,PRODUCT_MSTR PM,CIRCLE_MSTR CM,LEAD_PROSPECT_DETAIL LPD WHERE	LD.LEAD_ID = ? AND LD.PROSPECT_ID = LPC.PROSPECT_ID AND	LD.PRODUCT_ID = PM.PRODUCT_ID AND LPD.CIRCLE_ID = CM.CIRCLE_ID AND LPD.PRODUCT_LOB_ID=CM.LOB_ID AND LPD.LEAD_PROSPECT_ID=LD.LEAD_PROSPECT_ID  ";
	//changed by aman
	
	protected static final String SQL_SELECT_MOBIL_NO ="SELECT * FROM LEAD_PROSPECT_CUSTOMER WHERE PROSPECT_ID = (SELECT PROSPECT_ID FROM LEAD_DATA WHERE LEAD_ID=?)  ";
	
	
	protected static final String SQL_SELECT_LEAD_LIST_NEW ="SELECT	distinct LD.LEAD_ID AS LEAD_ID,PM.PRODUCT_NAME AS PRODUCT_NAME,LPC.CUSTOMER_NAME AS CUSTOMER_NAME,LPC.PROSPECT_MOBILE_NUMBER AS PROSPECT_MOBILE_NUMBER ,CM.CIRCLE_NAME AS CIRCLE_NAME,varchar_format (LT.LEAD_ASSIGNMENT_TIME, 'YYYY-MM-DD HH:MI:SS AM') AS LEAD_ASSIGNMENT_TIME, (	SELECT LEAD_STATUS_NAME AS LEAD_STATUS FROM	LEAD_STATUS	WHERE LEAD_STATUS_ID = LD.LEAD_STATUS_ID ) FROM	LEAD_DATA LD,LEAD_PROSPECT_CUSTOMER LPC ,PRODUCT_MSTR PM,CIRCLE_MSTR CM,LEAD_PROSPECT_DETAIL LPD , LEAD_TRANSACTION LT WHERE LD.LEAD_ID = ? AND LD.PROSPECT_ID = LPC.PROSPECT_ID AND	LD.PRODUCT_ID = PM.PRODUCT_ID AND LPD.CIRCLE_ID = CM.CIRCLE_ID  AND LPD.PRODUCT_LOB_ID=CM.LOB_ID AND LPD.LEAD_PROSPECT_ID=LD.LEAD_PROSPECT_ID  and LD.LEAD_ID=LT.LEAD_ID and LD.LEAD_STATUS_ID in ( "+LMSStatusCodes.ASSIGNED+","+LMSStatusCodes.REASSIGNED+") and LD.LEAD_STATUS_ID=LT.LEAD_STATUS_ID  and LT.LEAD_ASSIGNMENT_TIME <=(current timestamp - " +Constants.CONFIGURATION_TIME+ ") order by LEAD_ASSIGNMENT_TIME desc fetch first row only  ";
	
	
	
	//protected static final String SQL_SELECT_LEAD_LIST1 ="SELECT distinct LD.LEAD_ID AS LEAD_ID,PM.PRODUCT_NAME AS PRODUCT_NAME,LPC.CUSTOMER_NAME AS CUSTOMER_NAME,LPC.PROSPECT_MOBILE_NUMBER AS PROSPECT_MOBILE_NUMBER,CM.CIRCLE_DESC AS CIRCLE_DESC,varchar_format (LD.CREATE_TIME, 'YYYY-MM-DD HH:MI:SS AM') AS CREATE_TIME,(	SELECT LEAD_STATUS_NAME AS LEAD_STATUS FROM	LEAD_STATUS	WHERE LEAD_STATUS_ID = LD.LEAD_STATUS_ID ) FROM	LEAD_DATA LD,LEAD_PROSPECT_CUSTOMER LPC ,PRODUCT_MSTR PM,CIRCLE_MSTR CM,LEAD_PROSPECT_DETAIL LPD, LEAD_TRANSACTION LT WHERE LT.LEAD_ID=LD.LEAD_ID AND LD.PROSPECT_ID = LPC.PROSPECT_ID AND	LD.PRODUCT_ID = PM.PRODUCT_ID AND LPD.CIRCLE_ID = CM.CIRCLE_ID AND LPD.PRODUCT_LOB_ID=CM.LOB_ID AND LPD.LEAD_PROSPECT_ID=LD.LEAD_PROSPECT_ID AND LT.LEAD_STATUS_ID=400 and LT.TRANSACTION_TIME >= date('+startDate+') and LT.TRANSACTION_TIME <= date('+endDate+') and LT.LEAD_ASSIGNED_PRIMARY_USER=?  ";	
	
	public  ArrayList<LeadDetailsDTO> getLeadListByLeadContact(Long leadId) throws DAOException
	{
	Connection con = null;
	PreparedStatement ps = null;
	PreparedStatement psMobil = null;
	ResultSet rs = null;
	ResultSet rsMobil = null;
	Long mobile_number = 0L;
	ArrayList<LeadDetailsDTO> leadList = new ArrayList<LeadDetailsDTO>();
	LeadDetailsDTO dto = null;
	try {
		con = DBConnection.getDBConnection();
		psMobil= con.prepareStatement(SQL_SELECT_MOBIL_NO);
		psMobil.setLong(1, leadId);
		rsMobil = psMobil.executeQuery();
		while(rsMobil.next())
		{
			mobile_number= rsMobil.getLong("PROSPECT_MOBILE_NUMBER");	
		}
		
		StringBuffer buffer = SQL_SELECT_LEAD_LIST_MOBILE_NO.append(" and LD.CREATE_TIME >= current date - "+ServerPropertyReader.getString("Dialer.popup.days")+" days");
		ps = con.prepareStatement(buffer.toString());
		ps.setLong(1, mobile_number);
		rs = ps.executeQuery();
		
		while(rs.next()) {
			dto = new LeadDetailsDTO();
			dto.setLeadId(rs.getLong("LEAD_ID"));
			dto.setProductName(rs.getString("PRODUCT_NAME"));
			dto.setCustomerName(rs.getString("CUSTOMER_NAME"));
			dto.setCircleName(rs.getString("CIRCLE_DESC"));
			dto.setLeadStatusName(rs.getString("LEAD_STATUS"));
			dto.setProductLobId(rs.getInt("PRODUCT_LOB_ID"));
			dto.setAddress1(rs.getString("ADDRESS"));
			dto.setAddress2(rs.getString("ADDRESS2"));
			leadList.add(dto);
		
		     }
	}
	catch (Exception e) {
		e.printStackTrace();
		throw new DAOException("Exception occured while getting product list :  "+ e.getMessage(),e);
	} finally {
		try {
			//DBConnection.releaseResources(con, ps, rs);
		} catch (Exception e) {				
			throw new DAOException(e.getMessage(), e);
		}
	}
	return leadList;
}

	public  ArrayList<LeadDetailsDTO> getLeadListByLeadId(Long leadId) throws DAOException
	{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<LeadDetailsDTO> leadList = new ArrayList<LeadDetailsDTO>();
		LeadDetailsDTO dto = null;
		try {
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(SQL_SELECT_LEAD_LIST);
			ps.setLong(1, leadId);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				dto = new LeadDetailsDTO();
				dto.setLeadId(rs.getLong("LEAD_ID"));
				dto.setProductName(rs.getString("PRODUCT_NAME"));
				dto.setCustomerName(rs.getString("CUSTOMER_NAME"));
				dto.setContactNo(rs.getLong("PROSPECT_MOBILE_NUMBER"));
				dto.setCircleName(rs.getString("CIRCLE_DESC"));
				dto.setOppertunityTime(rs.getString("CREATE_TIME"));
				dto.setLeadStatusName(rs.getString("LEAD_STATUS"));
				dto.setProductId(rs.getInt("PRODUCT_ID"));
				dto.setProductLobId(rs.getInt("PRODUCT_LOB_ID"));
				String prodata  =ServerPropertyReader.getString("4G.leaddata.Product");
				if(dto.getProductLobId()==Integer.parseInt(PropertyReader.getAppValue("lms.4G.productLobId")) && prodata.contains(String.valueOf(dto.getProductId())))
				{
				  dto.setProductflag("true");
				}
				else if(dto.getProductLobId()!=Integer.parseInt(PropertyReader.getAppValue("lms.4G.productLobId")))
				{
					dto.setProductflag("false");
				}
				
				else if(dto.getProductLobId()==Integer.parseInt(PropertyReader.getAppValue("lms.4G.productLobId")) && !prodata.contains(String.valueOf(dto.getProductId())))
					{
					  dto.setProductflag("4Gdifferent");
					}
				leadList.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException("Exception occured while getting product list :  "+ e.getMessage(),e);
		} finally {
			try {
				////DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return leadList;
	}
	
	
	public  ArrayList<LeadDetailsDTO> getLeadListByLeadId(LeadRegistrationFormBean leadRegistrationFormBean) throws DAOException
	{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<LeadDetailsDTO> leadList = new ArrayList<LeadDetailsDTO>();
		LeadDetailsDTO dto = null;
		String olmId="";
		String startDate="";
		String endDate="";
		int circleId;
		Date start,end;
		Long leadId	=0L;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy");
		try {
			con = DBConnection.getDBConnection();
			
			//String SQL_SELECT_LEAD_LIST1 ="SELECT distinct LD.LEAD_ID AS LEAD_ID,PM.PRODUCT_NAME AS PRODUCT_NAME,LPC.CUSTOMER_NAME AS CUSTOMER_NAME,LPC.PROSPECT_MOBILE_NUMBER AS PROSPECT_MOBILE_NUMBER,CM.CIRCLE_DESC AS CIRCLE_DESC,varchar_format (LD.CREATE_TIME, 'YYYY-MM-DD HH:MI:SS AM') AS CREATE_TIME,(SELECT LEAD_STATUS_NAME AS LEAD_STATUS FROM LEAD_STATUS WHERE LEAD_STATUS_ID = LD.LEAD_STATUS_ID ) FROM	LEAD_DATA LD,LEAD_PROSPECT_CUSTOMER LPC ,PRODUCT_MSTR PM,CIRCLE_MSTR CM,LEAD_PROSPECT_DETAIL LPD, LEAD_TRANSACTION LT WHERE LT.LEAD_ID=LD.LEAD_ID AND LD.PROSPECT_ID = LPC.PROSPECT_ID AND	LD.PRODUCT_ID = PM.PRODUCT_ID AND LPD.CIRCLE_ID = CM.CIRCLE_ID AND LPD.PRODUCT_LOB_ID=CM.LOB_ID AND LPD.LEAD_PROSPECT_ID=LD.LEAD_PROSPECT_ID AND LT.LEAD_STATUS_ID=400 and LT.TRANSACTION_TIME >= ? and LT.TRANSACTION_TIME <= ? and LT.LEAD_ASSIGNED_PRIMARY_USER= ?  ";
			
		//	String SQL_SELECT_LEAD_LIST1 ="SELECT distinct LD.LEAD_ID AS LEAD_ID,PM.PRODUCT_NAME AS PRODUCT_NAME,LPC.CUSTOMER_NAME AS CUSTOMER_NAME,LPC.PROSPECT_MOBILE_NUMBER AS PROSPECT_MOBILE_NUMBER, CM.CIRCLE_NAME AS CIRCLE_NAME,varchar_format (LT.LEAD_ASSIGNMENT_TIME, 'YYYY-MM-DD HH:MI:SS AM') AS LEAD_ASSIGNMENT_TIME, (SELECT LEAD_STATUS_NAME AS LEAD_STATUS FROM LEAD_STATUS WHERE LEAD_STATUS_ID = LD.LEAD_STATUS_ID )  FROM	LEAD_DATA LD,LEAD_PROSPECT_CUSTOMER LPC ,PRODUCT_MSTR PM,CIRCLE_MSTR CM,LEAD_PROSPECT_DETAIL LPD, LEAD_TRANSACTION LT WHERE LT.LEAD_ID=LD.LEAD_ID AND LD.PROSPECT_ID = LPC.PROSPECT_ID AND LD.PRODUCT_ID = PM.PRODUCT_ID AND LPD.CIRCLE_ID = CM.CIRCLE_ID AND LPD.PRODUCT_LOB_ID=CM.LOB_ID AND LPD.LEAD_PROSPECT_ID=LD.LEAD_PROSPECT_ID AND LT.LEAD_STATUS_ID in ( "+LMSStatusCodes.ASSIGNED+","+LMSStatusCodes.REASSIGNED+") and LD.LEAD_STATUS_ID=LT.LEAD_STATUS_ID and LT.LEAD_ASSIGNMENT_TIME <=(current timestamp - " +Constants.CONFIGURATION_TIME+ ") and LT.TRANSACTION_TIME >= ? and LT.TRANSACTION_TIME <= ? and LT.LEAD_ASSIGNED_PRIMARY_USER= ?  ";
			StringBuffer SQL_SELECT_LEAD_LIST  = new StringBuffer("");
			SQL_SELECT_LEAD_LIST.append("SELECT distinct LD.LEAD_ID AS LEAD_ID,PM.PRODUCT_LOB_ID,PM.PRODUCT_NAME AS PRODUCT_NAME,LPC.CUSTOMER_NAME AS CUSTOMER_NAME,LPC.PROSPECT_MOBILE_NUMBER AS PROSPECT_MOBILE_NUMBER, CM.CIRCLE_NAME AS CIRCLE_NAME,varchar_format (LT.LEAD_ASSIGNMENT_TIME, 'YYYY-MM-DD HH:MI:SS AM') AS LEAD_ASSIGNMENT_TIME, (SELECT LEAD_STATUS_NAME AS LEAD_STATUS FROM LEAD_STATUS WHERE LEAD_STATUS_ID = LD.LEAD_STATUS_ID )  " +
					" FROM   LEAD_DATA LD,LEAD_PROSPECT_CUSTOMER LPC,PRODUCT_MSTR PM,CIRCLE_MSTR CM,LEAD_PROSPECT_DETAIL LPD,LEAD_TRANSACTION LT " +
					" WHERE LT.LEAD_ID=LD.LEAD_ID AND LD.PROSPECT_ID = LPC.PROSPECT_ID AND LD.PRODUCT_ID = PM.PRODUCT_ID AND LPD.CIRCLE_ID = CM.CIRCLE_ID AND LPD.PRODUCT_LOB_ID=CM.LOB_ID AND LPD.LEAD_PROSPECT_ID=LD.LEAD_PROSPECT_ID AND LT.LEAD_STATUS_ID in ("+LMSStatusCodes.ASSIGNED+","+LMSStatusCodes.REASSIGNED+") and LD.LEAD_STATUS_ID=LT.LEAD_STATUS_ID and LT.LEAD_ASSIGNMENT_TIME <=(current timestamp - " +Constants.CONFIGURATION_TIME+ ") ");
			olmId=leadRegistrationFormBean.getOlmId();
			startDate=leadRegistrationFormBean.getStartDate();
			endDate=leadRegistrationFormBean.getEndDate();
			
			
			if(leadRegistrationFormBean.getLeadId().equals("") && leadRegistrationFormBean.getLeadId() != null){
				
				//System.out.println("circleId"+leadRegistrationFormBean.getCircleId());
				//circleId=Integer.parseInt(leadRegistrationFormBean.getCircleId());
				start = (Date) dateFormat.parse(startDate);
				startDate = dateFormat1.format(start);
			
				end = (Date) dateFormat.parse(endDate);
				endDate = dateFormat1.format(end);
				int count = 1; 
				
				SQL_SELECT_LEAD_LIST.append(" and LT.TRANSACTION_TIME >= ? and LT.TRANSACTION_TIME <= ?  ");
				
				if(!(olmId==null || "".equals(olmId))){
					SQL_SELECT_LEAD_LIST.append(" and LT.LEAD_ASSIGNED_PRIMARY_USER= ? ");
				}
				
				if(leadRegistrationFormBean.getSelectedProductLobId()>0){
						SQL_SELECT_LEAD_LIST.append(" and pm.PRODUCT_LOB_ID=? ");
				}

				if(leadRegistrationFormBean.getSelectedCircleId()>0 ){
					SQL_SELECT_LEAD_LIST.append(" and cm.CIRCLE_ID=? ");
				}
					
				SQL_SELECT_LEAD_LIST.append("   ");
				
				ps = con.prepareStatement(SQL_SELECT_LEAD_LIST.toString());
				
				ps.setDate(count++, Utility.getSqlDateFromString(startDate, "MM/dd/yyyy"));
				ps.setDate(count++, Utility.getSqlDateFromString(endDate, "MM/dd/yyyy"));
				
				
				if(!(olmId==null || "".equals(olmId))){
					ps.setString(count++,olmId);
				}
				
				if(leadRegistrationFormBean.getSelectedProductLobId()>0){
					ps.setInt(count++,leadRegistrationFormBean.getSelectedProductLobId());
				}
				if(leadRegistrationFormBean.getSelectedCircleId()>0){
					ps.setInt(count++,leadRegistrationFormBean.getSelectedCircleId());
				}
				
			}else{ //if(leadRegistrationFormBean.getLeadId()!="")
				SQL_SELECT_LEAD_LIST.append(" and LD.LEAD_ID = ? order by LEAD_ASSIGNMENT_TIME desc fetch first row only   ");
				leadId = Long.parseLong(leadRegistrationFormBean.getLeadId());				
				ps = con.prepareStatement(SQL_SELECT_LEAD_LIST.toString());
				ps.setLong(1, leadId);
			}
			rs = ps.executeQuery();
			
			while(rs.next()) {
				
				dto = new LeadDetailsDTO();
				dto.setLeadId(rs.getLong("LEAD_ID"));
				dto.setProductName(rs.getString("PRODUCT_NAME"));
				dto.setCustomerName(rs.getString("CUSTOMER_NAME"));
				dto.setContactNo(rs.getLong("PROSPECT_MOBILE_NUMBER"));
				dto.setCircleName(rs.getString("CIRCLE_NAME"));
				dto.setOppertunityTime(rs.getString("LEAD_ASSIGNMENT_TIME"));
				dto.setLeadStatusName(rs.getString("LEAD_STATUS"));
				leadList.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException("Exception occured while getting product list :  "+ e.getMessage(),e);
		} finally {
			try {
				////DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return leadList;
	}
	

   // protected static final String SQL_SELECT_LEAD_LIST_MOBILE_NO ="select LD.LEAD_ID AS LEAD_ID, PM.PRODUCT_NAME AS PRODUCT_NAME, LPC.CUSTOMER_NAME AS CUSTOMER_NAME, LPC.PROSPECT_MOBILE_NUMBER AS PROSPECT_MOBILE_NUMBER, CM.CIRCLE_DESC AS CIRCLE_DESC, varchar_format (LD.CREATE_TIME, 'YYYY-MM-DD HH:MI:SS AM') AS CREATE_TIME, (SELECT LEAD_STATUS_NAME AS LEAD_STATUS FROM  LEAD_STATUS WHERE LEAD_STATUS_ID = LD.LEAD_STATUS_ID) FROM LEAD_DATA LD, LEAD_PROSPECT_CUSTOMER LPC , PRODUCT_MSTR PM, CIRCLE_MSTR CM WHERE LPC.PROSPECT_MOBILE_NUMBER = ?  AND LD.PROSPECT_ID = LPC.PROSPECT_ID AND LD.PRODUCT_ID = PM.PRODUCT_ID AND LPC.CIRCLE_ID = CM.CIRCLE_ID  ";
	// this query changed by pratap as new table added LEAD_PROSPECT_DETAILS 
	StringBuffer SQL_SELECT_LEAD_LIST_MOBILE_NO= new StringBuffer("SELECT LD.LEAD_ID AS LEAD_ID, PM.PRODUCT_NAME AS PRODUCT_NAME, LPC.CUSTOMER_NAME AS CUSTOMER_NAME,LPD.ADDRESS1 AS ADDRESS, LPD.ADDRESS2 AS ADDRESS2,LD.PRODUCT_ID AS PRODUCT_ID,LPC.PROSPECT_MOBILE_NUMBER AS PROSPECT_MOBILE_NUMBER, CM.CIRCLE_DESC AS CIRCLE_DESC, varchar_format (LD.CREATE_TIME, 'YYYY-MM-DD HH:MI:SS AM') AS CREATE_TIME,(SELECT LEAD_STATUS_NAME AS LEAD_STATUS FROM  LEAD_STATUS WHERE LEAD_STATUS_ID = LD.LEAD_STATUS_ID),(SELECT PRODUCT_LOB_ID FROM PRODUCT_MSTR WHERE PRODUCT_ID=LD.PRODUCT_ID) FROM LEAD_DATA LD, LEAD_PROSPECT_CUSTOMER LPC , PRODUCT_MSTR PM, CIRCLE_MSTR CM ,LEAD_PROSPECT_DETAIL LPD WHERE LPC.PROSPECT_MOBILE_NUMBER = ? AND LD.PROSPECT_ID = LPC.PROSPECT_ID AND LD.LEAD_PROSPECT_ID = LPD.LEAD_PROSPECT_ID AND LD.PRODUCT_ID = PM.PRODUCT_ID AND LPD.CIRCLE_ID = CM.CIRCLE_ID AND LPD.PRODUCT_LOB_ID=CM.LOB_ID ");   
    public  ArrayList<LeadDetailsDTO> getLeadListByMobileNo(long mobileNo) throws DAOException
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String flag="false";
		ArrayList<LeadDetailsDTO> leadList = new ArrayList<LeadDetailsDTO>();
		LeadDetailsDTO dto = null;
		try {
			conn = DBConnection.getDBConnection();
			ps = conn.prepareStatement(SQL_SELECT_LEAD_LIST_MOBILE_NO.toString());
			ps.setLong(1, mobileNo);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				dto = new LeadDetailsDTO();
				dto.setLeadId(rs.getLong("LEAD_ID"));
				dto.setProductName(rs.getString("PRODUCT_NAME"));
				dto.setCustomerName(rs.getString("CUSTOMER_NAME"));
				dto.setContactNo(rs.getLong("PROSPECT_MOBILE_NUMBER"));
				dto.setCircleName(rs.getString("CIRCLE_DESC"));
				dto.setOppertunityTime(rs.getString("CREATE_TIME"));
				dto.setLeadStatusName(rs.getString("LEAD_STATUS"));
				dto.setProductId(rs.getInt("PRODUCT_ID"));
				dto.setProductLobId(rs.getInt("PRODUCT_LOB_ID"));
				String prodata  =PropertyReader.getAppValue("4G.leaddata.Product");
				if(dto.getProductLobId()==Integer.parseInt(PropertyReader.getAppValue("lms.4G.productLobId")) && prodata.contains(String.valueOf(dto.getProductId())))
				{
				  dto.setProductflag("true");
				}
				else if(dto.getProductLobId()!=Integer.parseInt(PropertyReader.getAppValue("lms.4G.productLobId")))
				{
					dto.setProductflag("false");
				}
				
				else if(dto.getProductLobId()==Integer.parseInt(PropertyReader.getAppValue("lms.4G.productLobId")) && !prodata.contains(String.valueOf(dto.getProductId())))
					{
					  dto.setProductflag("4Gdifferent");
					}	
				
				leadList.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException("Exception occured while getting product list :  "+ e.getMessage(),e);
		} finally {
			try {
				////DBConnection.releaseResources(conn, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return leadList;
	}
     
/*    // change the query for Lead ID search - currently on mobile
    protected static final String SQL_SELECT_LEAD_DETAILS =" select LD.LEAD_ID AS LEAD_ID,LD.PRODUCT_ID AS PRODUCT_ID,LD.PROSPECT_ID AS PROSPECT_ID, PM.PRODUCT_NAME AS PRODUCT_NAME,"+
    " LPC.CUSTOMER_NAME AS CUSTOMER_NAME, LPC.PROSPECT_MOBILE_NUMBER AS PROSPECT_MOBILE_NUMBER,  LPC.ALTERNATE_CONTACT_NUMBER "+
    " AS ALTERNATE_CONTACT_NUMBER,LPC.LANDLINE_NUMBER AS LANDLINE_NUMBER, LPC.ADDRESS1 AS ADDRESS1, LPC.ADDRESS2 AS ADDRESS2,"+
    " (SELECT CM.CITY_NAME AS CITY FROM CITY_MSTR CM WHERE CM.CITY_CODE = LPC.CITY),LPC.CITY as CITY_CODE, LPC.EMAIL AS EMAIL, LPC.PRIMARY_LANGUAGE AS PRIMARY_LANGUAGE, LPC.PINCODE AS PINCODE," +
    " LPC.MARITAL_STATUS AS MARITAL_STATUS, LPC.SUB_ZONE AS SUB_ZONE, LPC.ZONE AS ZONE_CODE, LPC.IS_CUSTOMER AS IS_CUSTOMER, " +
    " (SELECT ZM.ZONE_NAME AS ZONE FROM ZONE_MSTR ZM WHERE ZM.ZONE_CODE = LPC.ZONE), LPC.RSU_ID AS RSU_ID , "+
    " (SELECT SM.STATE_DESC AS STATE_DESC FROM STATE_MSTR SM WHERE SM.STATE_ID = LPC.STATE), LPC.STATE AS STATE_ID, CM.CIRCLE_DESC AS "+
    " CIRCLE_DESC, LPC.CIRCLE_ID AS CIRCLE_ID, (SELECT SCM.SOURCE_NAME AS SOURCE_NAME FROM SOURCE_MSTR SCM WHERE SCM.SOURCE_ID = LD.SOURCE), LD.SOURCE AS SOURCE_ID, "+
    "  LD.SUB_SOURCE AS SUBSOURCE_NAME , "+
    " (SELECT RTM.REQUEST_TYPE AS REQUEST_TYPE FROM REQUEST_TYPE_MSTR RTM WHERE RTM.REQUEST_ID = LD.REQUEST_TYPE), LD.REQUEST_TYPE AS REQUEST_TYPE_ID, LPC.APPOINTMENT_TIME as APPOINTMENT_TIME, varchar_format (LD.CREATE_TIME, 'YYYY-MM-DD HH:MI:SS AM') "+
    " AS OPPORTUNITY_TIME,(SELECT LEAD_STATUS AS LEAD_STATUS FROM  LEAD_STATUS WHERE LEAD_STATUS_ID = LD.LEAD_STATUS_ID), LD.LEAD_STATUS_ID AS LEAD_STATUS_ID ,LD.REMARKS AS REMARKS ,(SELECT SUB_STATUS FROM LEAD_SUB_STATUS WHERE SUB_STATUS_ID = " +
    "(SELECT SUB_STATUS_ID FROM LEAD_TRANSACTION WHERE LEAD_ID = LD.LEAD_ID ORDER BY TRANSACTION_TIME DESC FETCH FIRST ROW ONLY)) AS SUB_STATUS,(SELECT SUB_STATUS_ID FROM LEAD_TRANSACTION WHERE LEAD_ID = LD.LEAD_ID ORDER BY TRANSACTION_TIME DESC FETCH FIRST ROW ONLY) AS SUB_STATUS_ID "+
    " FROM LEAD_DATA LD, LEAD_PROSPECT_CUSTOMER LPC , PRODUCT_MSTR PM, CIRCLE_MSTR CM WHERE LD.LEAD_ID = ?  AND LD.PROSPECT_ID "+
    " = LPC.PROSPECT_ID AND LD.PRODUCT_ID = PM.PRODUCT_ID AND LPC.CIRCLE_ID = CM.CIRCLE_ID  ";*/
   
    // Changes as per new created table LEAD_PROSPECT_DETAIL on 27/11/2013 - Amarjeet
   /* protected static final String SQL_SELECT_LEAD_DETAILS ="select LD.LEAD_ID AS LEAD_ID,LD.PRODUCT_ID AS PRODUCT_ID,LD.PROSPECT_ID AS PROSPECT_ID, PM.PRODUCT_NAME AS PRODUCT_NAME,"+
    " LPC.CUSTOMER_NAME AS CUSTOMER_NAME, LPC.PROSPECT_MOBILE_NUMBER AS PROSPECT_MOBILE_NUMBER,  LPD.ALTERNATE_CONTACT_NUMBER "+
      "AS ALTERNATE_CONTACT_NUMBER,LPD.LANDLINE_NUMBER AS LANDLINE_NUMBER, LPD.ADDRESS1 AS ADDRESS1, LPD.ADDRESS2 AS ADDRESS2,"+
      "(SELECT CM.CITY_NAME AS CITY FROM CITY_MSTR CM WHERE CM.CITY_CODE = LPD.CITY_CODE),LPD.CITY_CODE as CITY_CODE, LPD.EMAIL AS EMAIL, LPD.PRIMARY_LANGUAGE AS PRIMARY_LANGUAGE, LPD.PINCODE AS PINCODE, "+
      "LPD.MARITAL_STATUS AS MARITAL_STATUS, LPD.SUB_ZONE AS SUB_ZONE, LPD.CITY_ZONE_CODE AS ZONE_CODE, LPD.IS_CUSTOMER AS IS_CUSTOMER,  "+
      "(SELECT ZM.ZONE_NAME AS ZONE FROM ZONE_MSTR ZM WHERE ZM.ZONE_CODE = LPD.CITY_ZONE_CODE), LPD.RSU_CODE AS RSU_ID ,"+ 
      "(SELECT SM.STATE_DESC AS STATE_DESC FROM STATE_MSTR SM WHERE SM.STATE_ID = LPD.STATE), LPD.STATE AS STATE_ID, CM.CIRCLE_DESC AS "+
      "CIRCLE_DESC,CM.CIRCLE_MSTR_ID, LPD.CIRCLE_ID AS 
      
      CIRCLE_ID, (SELECT SCM.SOURCE_NAME AS SOURCE_NAME FROM SOURCE_MSTR SCM WHERE SCM.SOURCE_ID = LD.SOURCE), LD.SOURCE AS SOURCE_ID,"+ 
       "LD.SUB_SOURCE AS SUBSOURCE_NAME , "+
      "(SELECT RTM.REQUEST_TYPE AS REQUEST_TYPE FROM REQUEST_TYPE_MSTR RTM WHERE RTM.REQUEST_ID = LD.REQUEST_TYPE), LD.REQUEST_TYPE AS REQUEST_TYPE_ID, LPD.APPOINTMENT_TIME as APPOINTMENT_TIME, varchar_format (LD.CREATE_TIME, 'YYYY-MM-DD HH:MI:SS AM') "+
     " AS OPPORTUNITY_TIME,(SELECT LEAD_STATUS AS LEAD_STATUS FROM  LEAD_STATUS WHERE LEAD_STATUS_ID = LD.LEAD_STATUS_ID), LD.LEAD_STATUS_ID AS LEAD_STATUS_ID ,LD.REMARKS AS REMARKS ,(SELECT SUB_STATUS FROM LEAD_SUB_STATUS WHERE SUB_STATUS_ID = "+ 
    "(SELECT SUB_STATUS_ID FROM LEAD_TRANSACTION WHERE LEAD_ID = LD.LEAD_ID ORDER BY TRANSACTION_TIME DESC FETCH FIRST ROW ONLY)) AS SUB_STATUS,(SELECT SUB_STATUS_ID FROM LEAD_TRANSACTION WHERE LEAD_ID = LD.LEAD_ID ORDER BY TRANSACTION_TIME DESC FETCH FIRST ROW ONLY) AS SUB_STATUS_ID "+
     " FROM LEAD_DATA LD, LEAD_PROSPECT_CUSTOMER LPC,LEAD_PROSPECT_DETAIL LPD , PRODUCT_MSTR PM, CIRCLE_MSTR CM WHERE LD.LEAD_ID = ?  AND LD.PROSPECT_ID "+
     "= LPC.PROSPECT_ID AND LPD.PROSPECT_ID "+
     "= LPC.PROSPECT_ID AND LD.PRODUCT_ID = PM.PRODUCT_ID AND LPC.CIRCLE_ID = CM.CIRCLE_ID  ";*/
   
    // changed done by pratap for circle join from lead_prospect_detail table and city zone code to cirlce master
    //Two Columns Added Extra_param2 and Caf
    protected static final String SQL_SELECT_LEAD_DETAILS ="select LD.LEAD_ID AS LEAD_ID,LD.PRODUCT_ID AS PRODUCT_ID,LD.PROSPECT_ID AS PROSPECT_ID, LD.UTM_SOURCE AS UTM_SOURCE, LD.UTM_TERM as UTM_TERM, LD.UTM_MEDIUM AS UTM_MEDIUM, LD.UTM_CAMPAIGN AS UTM_CAMPAIGN, (SELECT LEAD_PRIORITY_NAME FROM LEAD_PRIORITY_SCORE_MAPPING WHERE LEAD_PRIORITY_VALUE = LD.LEAD_PRIORITY ), PM.PRODUCT_NAME AS PRODUCT_NAME,"+
     "LPC.CUSTOMER_NAME AS CUSTOMER_NAME, LPC.PROSPECT_MOBILE_NUMBER AS PROSPECT_MOBILE_NUMBER, LPD.ALTERNATE_CONTACT_NUMBER "+ 
      "AS ALTERNATE_CONTACT_NUMBER,LPD.LANDLINE_NUMBER AS LANDLINE_NUMBER, LPD.ADDRESS1 AS ADDRESS1, LPD.ADDRESS2 AS ADDRESS2,"+
      "(SELECT CM.CITY_NAME AS CITY FROM CITY_MSTR CM WHERE CM.CITY_CODE = LPD.CITY_CODE),LPD.CITY_CODE as CITY_CODE, LPC.EMAIL AS EMAIL, LPD.PRIMARY_LANGUAGE AS PRIMARY_LANGUAGE, LPD.PINCODE AS PINCODE,"+ 
      "LPD.MARITAL_STATUS AS MARITAL_STATUS, LPD.SUB_ZONE AS SUB_ZONE, LPD.ZONE_CODE AS ZONE_CODE, LPD.IS_CUSTOMER AS IS_CUSTOMER,"+  
      "(SELECT ZM.ZONE_NAME AS ZONE FROM ZONE_MSTR ZM WHERE ZM.ZONE_CODE = LPD.ZONE_CODE), LPD.RSU_CODE AS RSU_ID ,"+
      "(SELECT SM.STATE_DESC AS STATE_DESC FROM STATE_MSTR SM WHERE SM.STATE_ID = LPD.STATE), LPD.STATE AS STATE_ID, CM.CIRCLE_DESC AS "+ 
      "CIRCLE_DESC,CM.CIRCLE_MSTR_ID, LPD.CIRCLE_ID AS CIRCLE_ID, LPD.CITY_ZONE_CODE AS CITY_ZONE_CODE,"+ 
      "(SELECT CZM.CITY_ZONE_NAME AS CITY_ZONE_NAME FROM CITY_ZONE_MSTR CZM WHERE CZM.CITY_ZONE_CODE = LPD.CITY_ZONE_CODE),"+ 
       "(SELECT SCM.SOURCE_NAME AS SOURCE_NAME FROM SOURCE_MSTR SCM WHERE SCM.SOURCE_ID = LD.SOURCE), LD.SOURCE AS SOURCE_ID,"+ 
       "(SELECT SSM.SUBSOURCE_NAME AS SUBSOURCE_NAME FROM SUB_SOURCE_MSTR SSM WHERE char(SSM.SUBSOURCE_ID) = LD.SUB_SOURCE) AS SUBSOURCE_NAME , LPD.LEAD_PROSPECT_ID AS LEAD_PROSPECT_ID ,"+
      "(SELECT RTM.REQUEST_TYPE AS REQUEST_TYPE FROM REQUEST_TYPE_MSTR RTM WHERE RTM.REQUEST_ID = LD.REQUEST_TYPE), LD.REQUEST_TYPE AS REQUEST_TYPE_ID, LPD.APPOINTMENT_TIME as APPOINTMENT_TIME,LPD.APPOINMENT_ENDTIME as APPOINMENT_ENDTIME, varchar_format (LD.CREATE_TIME, 'YYYY-MM-DD HH:MI:SS AM') "+ 
      "AS OPPORTUNITY_TIME,LD.REMARKS AS REMARKS ,(SELECT LEAD_STATUS AS LEAD_STATUS FROM  LEAD_STATUS WHERE LEAD_STATUS_ID = LD.LEAD_STATUS_ID), LD.LEAD_STATUS_ID AS LEAD_STATUS_ID ,"+
    "(SELECT SUB_STATUS FROM LEAD_SUB_STATUS WHERE  LEAD_STATUS_ID = (SELECT LEAD_STATUS_ID FROM LEAD_TRANSACTION WHERE LEAD_ID = LD.LEAD_ID ORDER BY TRANSACTION_TIME DESC FETCH FIRST ROW ONLY) AND  SUB_STATUS_ID = (SELECT SUB_STATUS_ID FROM LEAD_TRANSACTION WHERE LEAD_ID = LD.LEAD_ID ORDER BY TRANSACTION_TIME DESC FETCH FIRST ROW ONLY) AND PRODUCT_LOB_ID=(SELECT PRODUCT_ID FROM LEAD_TRANSACTION WHERE LEAD_ID = LD.LEAD_ID ORDER BY TRANSACTION_TIME DESC FETCH FIRST ROW ONLY)) AS SUB_STATUS,LD.LEAD_SUB_STATUS_ID as SUB_STATUS_ID, "+
    "(SELECT LEAD_PRODUCT_ID FROM LEAD_TRANSACTION WHERE LEAD_ID = LD.LEAD_ID ORDER BY TRANSACTION_TIME DESC FETCH FIRST ROW ONLY) AS LEAD_PRODUCT_ID,LD.CAMPAIGN AS CAMPAIGN,LPD.MYOP_ID AS MYOP_ID,LPD.RENTAL AS RENTAL,LPD.PYT_AMT AS PYT_AMT,LD.ALLOCATED_NO AS ALLOCATED_NO,LPD.TRAN_REFNO AS TRAN_REFNO,LD.ONLINE_CAF_NO AS ONLINE_CAF_NO,PM.PRODUCT_LOB_ID AS PRODUCT_LOB_ID, "+ 
    "(SELECT LEAD_CATEGORY FROM LEAD_CATEGORY_MSTR LCM WHERE LCM.LEAD_CATEGORY_ID =LD.LEAD_CATEGORY) AS LEAD_CATEGORY,LDT.REQUEST_CATEGORY_ID,LDT.PLAN_ID AS PLAN_ID,LDT.USER_TYPE AS USER_TYPE,LDT.RENTAL_TYPE AS RENTAL_TYPE,LDT.FREEBIE_COUNT AS FREEBIE_COUNT ,LDT.BOOSTER_COUNT AS BOOSTER_COUNT,LDT.BOOSTER_TAKEN AS BOOSTER_TAKEN,LDT.FREEBIE_TAKEN AS FREEBIE_TAKEN,LDT.PREPAID_NUMBER AS PREPAID_NUMBER,LDT.OFFER AS OFFER,LDT.DOWNLOAD_LIMIT AS DOWNLOAD_LIMIT,"+
    "LDT.DEVICE_MRP AS DEVICE_MRP,LDT.VOICE_BENEFIT AS VOICE_BENEFIT,LDT.DATA_QUOTA AS DATA_QUOTA,LDT.FEASIBILITY_PARAM AS FEASIBILITY_PARAM,LPD.QUAL_LEAD_PARAM AS QUAL_LEAD_PARAM,LPD.GEO_IP_CITY AS GEO_IP_CITY,LD.LEAD_SUBMIT_TIME AS LEAD_SUBMIT_TIME,LD.FID AS FID,LD.CID AS CID,LD.KEYWORD AS KEYWORD,LD.FROM_PAGE AS FROM_PAGE,LD.SERVICE AS SERVICE,LDT.UTM_LABELS AS UTM_LABELS,LD.REFERER_URL AS REFERER_URL,LDT.FLAG AS FLAG,LPC.COMPANY AS COMPANY,LD.PLAN AS PLAN,LDT.DEVICE_TAKEN AS DEVICE_TAKEN,LDT.BENEFIT AS BENEFIT,LDT.PKG_DURATION AS PKG_DURATION,LD.REFERER_PAGE AS REFERER_PAGE,LDT.NDNC_DISCLAIMER AS NDNC_DISCLAIMER,LDT.STATE AS STATE,LDT.HLR_NO AS HLR_NO,LPD.EXTRA_PARAMS_2 AS EXTRA_PARAMS_2 ,LPD.EXTRA_PARAMS_3 AS SALES_CHANNEL_CODE,LD.CAF AS CAF,(SELECT RCM.REQUEST_CATEGORY AS REQUEST_CATEGORY FROM REQUEST_CATEGORY_MSTR RCM WHERE RCM.REQUEST_CATEGORY_ID = LDT.REQUEST_CATEGORY_ID),LDT.REQUEST_CATEGORY_ID AS REQUEST_CATEGORY,"+
    "LDT.EXTRA_PARAM3 AS LATITUDE,LDT.EXTRA_PARAM4 AS LONGITUDE,LDT.EXTRA_PARAM5 AS PAYMENT_TYPE,LDT.TASK_START_TIME AS TASK_START_TIME, LDT.TASK_END_TIME AS TASK_END_TIME,LPD.PYT_AMT_DUE AS TOTAL_AMT_DUE,LDT.EXTRA_PARAM6 AS PAYMENT_STATUS  "+
    "FROM LEAD_DATA LD, LEAD_PROSPECT_CUSTOMER LPC,LEAD_PROSPECT_DETAIL LPD , PRODUCT_MSTR PM, CIRCLE_MSTR CM ,LEAD_DETAILS LDT WHERE LD.LEAD_ID =?  AND LD.PROSPECT_ID "+ 
     "= LPC.PROSPECT_ID AND LPD.LEAD_PROSPECT_ID "+   
     "= LD.LEAD_PROSPECT_ID AND LD.PRODUCT_ID = PM.PRODUCT_ID AND LPD.CIRCLE_ID = CM.CIRCLE_ID AND LPD.PRODUCT_LOB_ID=CM.LOB_ID AND LD.LEAD_ID=LDT.LEAD_ID  ";
//,LDT.EXTRA_PARAM7 AS SERVICE_STATUS
    
   
    protected static final String SQL_SELECT_LEAD_DATA_OPEN_PRODUCT_IDS ="select PRODUCT_ID from LEAD_DATA where PROSPECT_ID= ?  AND LEAD_STATUS_ID NOT IN (500,600,700,710)  ";
    
    public  ArrayList<LeadDetailsDTO> getLeadDetails(long leadId) throws DAOException
	{
		Connection con = null;
		PreparedStatement ps = null;  
		ResultSet rs = null;
		ArrayList<LeadDetailsDTO> leadList = new ArrayList<LeadDetailsDTO>();
		LeadDetailsDTO dto = null;
		String appointmentTime="";
        String appointmentEndTime="";
		try {
			con = DBConnection.getDBConnection();
			
			ps = con.prepareStatement(SQL_SELECT_LEAD_DETAILS);
		
			ps.setLong(1, leadId);
			rs = ps.executeQuery();
			
			if(rs.next()) {
				dto = new LeadDetailsDTO();
				dto.setLeadId(rs.getLong("LEAD_ID"));
				
				dto.setProspectId(rs.getInt("PROSPECT_ID"));
				dto.setLeadProspectId(rs.getInt("LEAD_PROSPECT_ID"));
				
				dto.setProductName(rs.getString("PRODUCT_NAME"));
				dto.setCustomerName(rs.getString("CUSTOMER_NAME"));				
				dto.setContactNo(rs.getLong("PROSPECT_MOBILE_NUMBER"));
			    dto.setLeadProductId(rs.getInt("LEAD_PRODUCT_ID"));
				
				dto.setProductId(rs.getInt("PRODUCT_ID"));
				
				dto.setAddress1(rs.getString("ADDRESS1"));
				dto.setAddress2(rs.getString("ADDRESS2"));
				dto.setCityCode(rs.getString("CITY_CODE"));
				dto.setCityName(rs.getString("CITY"));
				dto.setPinCode(rs.getString("PINCODE"));
				dto.setPostalCode(rs.getString("PINCODE"));
				dto.setStateName(rs.getString("STATE_DESC"));
				dto.setCircleName(rs.getString("CIRCLE_DESC"));
				dto.setCircleMasterId(rs.getInt("CIRCLE_MSTR_ID"));//Added by amarjeet on 28/11/2013
				dto.setCityZoneCode(rs.getString("CITY_ZONE_CODE"));//Added by amarjeet on 5/12/2013
				dto.setCityZoneName(rs.getString("CITY_ZONE_NAME"));// Added by pratap on 13-Dec-2013
				dto.setEmail(rs.getString("EMAIL"));				
				dto.setAlternateContactNo(rs.getString("ALTERNATE_CONTACT_NUMBER"));			
				dto.setLandlineNo(rs.getString("LANDLINE_NUMBER"));
				dto.setLanguage(rs.getString("PRIMARY_LANGUAGE"));
				dto.setMaritalStatus(rs.getString("MARITAL_STATUS"));
				dto.setExistingCustomer(rs.getString("IS_CUSTOMER"));

				String requestType = rs.getString("REQUEST_TYPE");
				
					if (null != requestType )
					{
						dto.setRequestTypeDesc(requestType);
					}
					
					String stateId = rs.getString("STATE_ID");
					
					if (null != stateId )
					{
						dto.setStateCode(stateId);
					}
					
					String circleId = rs.getString("CIRCLE_ID");
					
					if (null != circleId )
					{
						dto.setCircleId(Integer.parseInt(circleId));
					}
					
						
					    			
					String source = rs.getString("SOURCE_ID");
					
					if (null != source )
					{
						dto.setSourceId(Integer.parseInt(source));
					}
					
					
					
					String requestTypeId = rs.getString("REQUEST_TYPE_ID");
					
					if (null != requestTypeId )
					{
						dto.setRequestType(Integer.parseInt(requestTypeId));
					}
					
					String leadStatusId = rs.getString("LEAD_STATUS_ID");
					
					if (null != leadStatusId )
					{
						dto.setLeadStatusId(Integer.parseInt(leadStatusId));
						
					}
				
				dto.setZoneCode(rs.getString("ZONE_CODE"));
				
			
				
				if(rs.getInt("PRODUCT_LOB_ID")==Integer.parseInt(PropertyReader.getAppValue("lms.4G.productLobId")))
				{
					 appointmentTime = rs.getString("APPOINTMENT_TIME");
					String appointmentEndTime1= rs.getString("APPOINMENT_ENDTIME");
					dto.setAppointmentTime(appointmentTime);
					dto.setAppointmentEndTime(appointmentEndTime1);
				}
				
				
				/*else if(rs.getInt("PRODUCT_ID") == (Integer.parseInt(ServerPropertyReader.getString("Dailler.leaddata.Product1")))||rs.getInt("PRODUCT_ID") == (Integer.parseInt(ServerPropertyReader.getString("Dailler.leaddata.Product2")))||rs.getInt("PRODUCT_ID") == (Integer.parseInt(ServerPropertyReader.getString("Dailler.leaddata.Product3")))||rs.getInt("PRODUCT_ID") == (Integer.parseInt(ServerPropertyReader.getString("Dailler.leaddata.Product4")))||rs.getInt("PRODUCT_ID") == (Integer.parseInt(ServerPropertyReader.getString("Dailler.leaddata.Product5"))))
				{
					 appointmentTime = rs.getString("APPOINTMENT_TIME");
					  appointmentEndTime = rs.getString("APPOINMENT_ENDTIME");//added by satish
					if (appointmentTime !=null && !"".equals(appointmentTime.trim()))
					{
						String appointmentDate ="";
						String appointmentHour ="";
						String appointmentMinute ="";
						
						String times[] = appointmentTime.split(" ");
						
						appointmentDate = times[0];
						if(times.length == 2){
							appointmentHour = times[1].split(":")[0];
							appointmentMinute = times[1].split(":")[1];
						}		

						dto.setAppointmentDate(appointmentDate);
						dto.setAppointmentHour(appointmentHour);
						dto.setAppointmentMinute(appointmentMinute);
						
						dto.setAppointmentTime(appointmentDate+" "+appointmentHour+" : "+appointmentMinute);
					}
                  //added by satish
					
					if (appointmentEndTime !=null && !"".equals(appointmentEndTime.trim() ))
					{
						String appointmentEndDate ="";
						String appointmentEndHour ="";
						String appointmentEndMinute ="";
						
						String times[] = appointmentEndTime.split(" ");
						
						appointmentEndDate = times[0];
						if(times.length == 2){
							appointmentEndHour = times[1].split(":")[0];
							appointmentEndMinute = times[1].split(":")[1];
						}		

						dto.setAppointmentEndDate(appointmentEndDate);
						dto.setAppointmentEndHour(appointmentEndHour);
						dto.setAppointmentEndMinute(appointmentEndMinute);
						
						dto.setAppointmentEndTime(appointmentEndDate+" "+appointmentEndHour+" : "+appointmentEndMinute);
					}
					
					
					//ended by satish
					
				}*/
				//satish
				
				dto.setRemarks(rs.getString("REMARKS"));				
				dto.setSourceName(rs.getString("SOURCE_NAME")); 
				if(rs.getString("SUBSOURCE_NAME") != null && !"".equalsIgnoreCase(rs.getString("SUBSOURCE_NAME")) && !"0".equalsIgnoreCase(rs.getString("SUBSOURCE_NAME")))
			 	dto.setSubSourceName(rs.getString("SUBSOURCE_NAME")); 
				dto.setOppertunityTime(rs.getString("OPPORTUNITY_TIME"));   
				dto.setSubZoneName(rs.getString("SUB_ZONE"));  
				dto.setZoneName(rs.getString("ZONE"));        
				dto.setRsuCode(rs.getString("RSU_ID"));   				
				dto.setLeadStatusName(rs.getString("LEAD_STATUS")); 
				dto.setRemarks(rs.getString("REMARKS"));
				if(rs.getString("SUB_STATUS") != null && !"".equalsIgnoreCase(rs.getString("SUB_STATUS")))
				if(!(rs.getString("LEAD_STATUS")).equalsIgnoreCase(rs.getString("SUB_STATUS")))
					dto.setLeadStatusName(rs.getString("LEAD_STATUS")+"("+rs.getString("SUB_STATUS")+")"); 
				dto.setLeadSubStatusId(rs.getInt("SUB_STATUS_ID"));
				//adding by pratap for lead search dialer page change on 19 dec 2013
				dto.setCampaign(rs.getString("CAMPAIGN"));
				dto.setPlanId(rs.getString("MYOP_ID"));
				dto.setRental(rs.getString("RENTAL"));
				dto.setPayment(rs.getString("PYT_AMT"));
				dto.setAllocatedNo(rs.getString("ALLOCATED_NO"));
				dto.setOnlineCafNo(rs.getString("ONLINE_CAF_NO"));
				dto.setTransactionRefNo(rs.getString("TRAN_REFNO")); 
				dto.setProductLobId(rs.getInt("PRODUCT_LOB_ID"));
				dto.setLeadCategory(rs.getString("LEAD_CATEGORY"));
				// end of adding by pratap
				
				/* Added by Parnika for Adding 4 additional fields from Sub Source */
				dto.setLeadPriority(rs.getString("LEAD_PRIORITY_NAME"));
				dto.setUtmSource(rs.getString("UTM_SOURCE"));
				dto.setUtmCampaign(rs.getString("UTM_CAMPAIGN"));
				dto.setUtmTerm(rs.getString("UTM_TERM"));
				dto.setUtmMedium(rs.getString("UTM_MEDIUM"));
				//Added By Bhaskar
				dto.setPlanId(rs.getString("PLAN_ID"));
				dto.setUserType(rs.getString("USER_TYPE"));
				dto.setRentalType(rs.getString("RENTAL_TYPE"));
				dto.setFreebieCount(rs.getString("FREEBIE_COUNT"));
				dto.setBoosterCount(rs.getString("BOOSTER_COUNT"));
				dto.setBoosterTaken(rs.getString("BOOSTER_TAKEN"));
				dto.setFreebieTaken(rs.getString("FREEBIE_TAKEN"));
				dto.setPrepaidNumber(rs.getString("PREPAID_NUMBER"));
				dto.setOffer(rs.getString("OFFER"));
				dto.setDownloadLimit(rs.getString("DOWNLOAD_LIMIT"));
				dto.setDeviceMrp(rs.getString("DEVICE_MRP"));
				dto.setVoiceBenefit(rs.getString("VOICE_BENEFIT"));
				dto.setDataQuota(rs.getString("DATA_QUOTA"));
				dto.setFeasibilityParam(rs.getString("FEASIBILITY_PARAM"));
				dto.setQualLeadParam(rs.getString("QUAL_LEAD_PARAM"));
				dto.setGeoIpCity(rs.getString("GEO_IP_CITY"));
				dto.setLeadSubmitTime(rs.getString("LEAD_SUBMIT_TIME"));
				dto.setFid(rs.getString("FID"));
				dto.setTid(rs.getString("CID"));
				dto.setKeyWord(rs.getString("KEYWORD"));
				dto.setFromPage(rs.getString("FROM_PAGE"));
				dto.setService(rs.getString("SERVICE"));
				dto.setUtmLabels(rs.getString("UTM_LABELS"));
				dto.setReferUrl(rs.getString("REFERER_URL"));
				dto.setFlag(rs.getBoolean("FLAG"));
				dto.setCompany(rs.getString("COMPANY"));
				dto.setPlan(rs.getString("PLAN"));
				dto.setDevicetaken(rs.getString("DEVICE_TAKEN"));
				dto.setBenefit(rs.getString("BENEFIT"));
				dto.setPkgDuration(rs.getString("PKG_DURATION"));
				dto.setReferPage(rs.getString("REFERER_PAGE"));
				dto.setNdncDisclaimer(rs.getString("NDNC_DISCLAIMER"));
				dto.setState(rs.getString("STATE"));
				dto.setHlrNo(rs.getString("HLR_NO"));
				dto.setRequestCategoryName(rs.getString("REQUEST_CATEGORY"));
				dto.setRequestCategory(rs.getString("REQUEST_CATEGORY"));
				dto.setRequestCategoryId(rs.getString("REQUEST_CATEGORY_ID"));
				dto.setCaf(rs.getString("CAF"));
				dto.setExtraParam1(rs.getString("EXTRA_PARAMS_2"));
				
				
				dto.setSalesChannelCode(rs.getString("SALES_CHANNEL_CODE"));// extra param 3 for sales channel Code
				dto.setExtraParam3(rs.getString("LATITUDE")); //latitude
				dto.setExtraParam4(rs.getString("LONGITUDE")); //longitude
				dto.setTaskStartTime(rs.getString("TASK_START_TIME"));//task start time
				dto.setTaskEndTime(rs.getString("TASK_END_TIME"));//task end time
				dto.setTotalDue(rs.getString("TOTAL_AMT_DUE"));// total amount due
				dto.setExtraParam6(rs.getString("PAYMENT_STATUS"));//payment status
                dto.setExtraParam5(rs.getString("PAYMENT_TYPE"));//payment type
				
			//	dto.setExtraParam7(rs.getString("SERVICE_STATUS")); //service status
				
				String prodata  =PropertyReader.getAppValue("4G.leaddata.Product");
				if(dto.getProductLobId()==Integer.parseInt(PropertyReader.getAppValue("lms.4G.productLobId")) && prodata.contains(String.valueOf(dto.getProductId())))
				{
				  dto.setProductflag("true");
				}
				else if(dto.getProductLobId()!=Integer.parseInt(PropertyReader.getAppValue("lms.4G.productLobId")))
				{
					dto.setProductflag("false");
				}
				
				else if(dto.getProductLobId()==Integer.parseInt(PropertyReader.getAppValue("lms.4G.productLobId")) && !prodata.contains(String.valueOf(dto.getProductId())))
					{
					  dto.setProductflag("4Gdifferent");
					}
				
				leadList.add(dto);
			}
		}
    catch (Exception e) {
			e.printStackTrace();
			throw new DAOException("Exception occured while getting product list :  "+ e.getMessage(),e);
		} 
    finally {
			try {
				////DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return leadList;
	}
   
    //query changed by Nancy
    protected static final String SQL_SELECT_LEAD_TRNS_DETAILS =" select  varchar_format (TRANSACTION_TIME, 'DD-MM-YYYY HH:MI:SS AM') as TRANSACTION_TIME, TRANSACTION_TIME as TRANSACTION_TIME_TEMP,"+
    " coalesce (LEAD_ASSIGNED_PRIMARY_USER,'') as LEAD_ASSIGNED_PRIMARY_USER, " +
    "(select PARTNER_NAME as CHANNEL_PARTNER_NAME from NAS_USER_MSTR UM where UM.USER_LOGIN_ID = LT.LEAD_ASSIGNED_PRIMARY_USER),"+
    " varchar_format (EXPECTED_CLOSURE_DATE, 'DD-MM-YYYY HH:MI:SS AM') as EXPECTED_CLOSURE_DATE,   varchar_format (LEAD_CLOSURE_TIME, 'DD-MM-YYYY HH:MI:SS AM') as  LEAD_CLOSURE_TIME , "+
    " (SELECT distinct LEAD_STATUS AS LEAD_STATUS FROM  LEAD_STATUS WHERE LEAD_STATUS_ID = LT.LEAD_STATUS_ID), (SELECT distinct SUB_STATUS AS "+
    " SUB_STATUS FROM  LEAD_SUB_STATUS WHERE SUB_STATUS_ID = LT.SUB_STATUS_ID AND PRODUCT_LOB_ID=LT.PRODUCT_ID), " +
    " (SELECT DISTINCT SUB_SUB_STATUS FROM LEAD_SUB_SUB_STATUS WHERE SUB_SUB_STATUS_ID = LT.LEAD_SUB_SUB_STATUS_ID), " +
    " coalesce (REMARKS,'') as REMARKS from "+
    " LEAD_TRANSACTION LT where LEAD_ID = ?  order by TRANSACTION_TIME_TEMP desc  ";
         
    public  ArrayList<LeadDetailsDTO> getLeadTransactinDetails(long leadId) throws DAOException
	{
		Connection con = null;
		PreparedStatement ps = null;  
		ResultSet rs = null;
		ArrayList<LeadDetailsDTO> leadTrnsDetailsList = new ArrayList<LeadDetailsDTO>();
		LeadDetailsDTO dto = null;
		try {
			con = DBConnection.getDBConnection();
			
			ps = con.prepareStatement(SQL_SELECT_LEAD_TRNS_DETAILS);
		
			ps.setLong(1, leadId);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				dto = new LeadDetailsDTO();
				dto.setTransactionTime(rs.getString("TRANSACTION_TIME"));
				dto.setAssignedPrimaryUser(rs.getString("LEAD_ASSIGNED_PRIMARY_USER"));
				dto.setUserFname(rs.getString("CHANNEL_PARTNER_NAME"));
				dto.setExpectedCloserDate(rs.getString("EXPECTED_CLOSURE_DATE"));				
				dto.setCloserTime(rs.getString("LEAD_CLOSURE_TIME"));
				dto.setLeadStatusName(rs.getString("LEAD_STATUS"));
				dto.setLeadSubStatusName(rs.getString("SUB_STATUS"));
				dto.setAllStatusName(rs.getString("SUB_SUB_STATUS"));
				dto.setRemarks(rs.getString("REMARKS"));

				leadTrnsDetailsList.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException("Exception occured while getting product list :  "+ e.getMessage(),e);
		} finally {
			try {
				////DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return leadTrnsDetailsList;
	}
    
    
    public  ArrayList<LeadDetailsDTO> getProductIDsOpenLeads(int prospectId) throws DAOException
	{
		Connection con = null;
		PreparedStatement ps = null;  
		ResultSet rs = null;
		ArrayList<LeadDetailsDTO> leadList = new ArrayList<LeadDetailsDTO>();
		LeadDetailsDTO dto = null;
		try {
			con = DBConnection.getDBConnection();
			
			ps = con.prepareStatement(SQL_SELECT_LEAD_DATA_OPEN_PRODUCT_IDS);
			ps.setInt(1, prospectId);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				dto = new LeadDetailsDTO();
				dto.setProductId(rs.getInt("PRODUCT_ID"));				
				leadList.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException("Exception occured while getting product list :  "+ e.getMessage(),e);
		} finally {
			try {
				//DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return leadList;
	}
    
    public String updateLeadDetailsDialler(List<LeadDetailsDTO> leadList,String flag) throws DAOException
    {
        Connection con=null;
        PreparedStatement pstmt=null;
        PreparedStatement pstmt1=null;
        PreparedStatement pstmt2=null;
        PreparedStatement pstmt3=null;
        PreparedStatement pstmt4=null;
        PreparedStatement pstmtLTX=null;
        PreparedStatement pstmtLD=null;
        Long leadId = 0l;
        ResultSet rs=null;
        ResultSet rs1=null;
        String  updateStatus = "SUCCESS"+"#"+leadId;
        int  prospectId = 0;
        MasterService mstrService = new MasterServiceImpl();
        int parameterIndex=1;    
        CallableStatement cs = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentdate = sdf.format(new java.util.Date().getTime());
        
		String currentdateplus2 = sdf.format(new java.util.Date().getTime() + 2*24*60*60*1000);
        try
        {   
        	
        	con = DBConnection.getDBConnection(); 
        	for(int ii=0; ii< leadList.size(); ii++)
			{
				LeadDetailsDTO leadDetailsDTO = new LeadDetailsDTO();
				leadDetailsDTO = leadList.get(ii);		

				// Cross sell products are those which are coming in selected products, as open lead products are disabled on form.
				int productIdsCrossSell[] = new int[leadDetailsDTO.getProductIds().length];
				productIdsCrossSell = leadDetailsDTO.getProductIds(); 
				
				try
				{
				  // Starting a Transaction 
				  con.setAutoCommit(false);
				  int indexCount = 1;
				  int ijk = 0;
				  int ijk1= 0;
				  int productId =0;
				        	// Update prospect data	
				  for (int jj = 0; jj < productIdsCrossSell.length ; jj++)
					{	
					 productId =  productIdsCrossSell[jj] ;
					}
				
				  if(productId==0) // No Product for cross sell, updating the Lead Details of existing Lead
				  {
					  
						String UPDATE_LEAD_PROSPECT_CUSTOMER = "UPDATE LEAD_PROSPECT_CUSTOMER SET CUSTOMER_NAME=?, PROSPECT_MOBILE_NUMBER=?, EMAIL=?, UPDATED_DT= current timestamp , UPDATED_BY=? WHERE PROSPECT_ID = ?  ";
						
						pstmt1 = con.prepareStatement(UPDATE_LEAD_PROSPECT_CUSTOMER);
						//int indexCount = 1;
						pstmt1.setString(1, leadDetailsDTO.getCustomerName());
						pstmt1.setLong(2, leadDetailsDTO.getContactNo());
						pstmt1.setString(3, leadDetailsDTO.getEmail());
						pstmt1.setString(4, leadDetailsDTO.getCreatedBy());
						pstmt1.setInt(5, leadDetailsDTO.getProspectId());
						int ijkl = pstmt1.executeUpdate();
					//Added by satish
						String UPDATE_LEAD_UPDATE_DATA="UPDATE LEAD_UPDATE_DATA SET PREVIOUS_OPERATOR=?,RELATION_NAME=?,DOC_COLLECTED_FLAG=?,PREVIOUS_CIRCLE=? where LEAD_ID=? ";
						pstmt4=con.prepareStatement(UPDATE_LEAD_UPDATE_DATA);
						
						pstmt4.setString(1, leadDetailsDTO.getPreviousOperator());
						pstmt4.setString(2, leadDetailsDTO.getRelationName());
						pstmt4.setString(3, leadDetailsDTO.getDocumentCollectedFlag());
						pstmt4.setString(4, leadDetailsDTO.getPreviousCircle());
						
						System.out.println("\nleadDetailsDTO.getPreviouscircle():::::::::::::::::::::::::"+leadDetailsDTO.getPreviousCircle()+"\n\n");
						
						pstmt4.setLong(5, leadDetailsDTO.getLeadId());
						pstmt4.executeUpdate();
						
						//Ended by satish	
                    if(!flag.equalsIgnoreCase("yes"))  //update all the details of the existing lead when no is chosen only the frst time
						{
							String UPDATE_LEAD_PROSPECT_DETAILS = "UPDATE LEAD_PROSPECT_DETAIL SET  CITY_ZONE_CODE=?, CITY_CODE=?, STATE=?, ZONE_CODE=?,  CIRCLE_ID=?, ADDRESS2=?, PRIMARY_LANGUAGE=?, PINCODE=?, ALTERNATE_CONTACT_NUMBER=?, LANDLINE_NUMBER=?, APPOINTMENT_TIME=?, ADDRESS1=?, IS_CUSTOMER=?, SUB_ZONE=?, UPDATED_DT= current timestamp, UPDATED_BY=?, PRODUCT_LOB_ID=?, RENTAL=?, PYT_AMT=? , TRAN_REFNO=? , MYOP_ID = ? , EXTRA_PARAMS_2 = ? , EXTRA_PARAMS_3 = ? ,APPOINMENT_ENDTIME =? WHERE LEAD_PROSPECT_ID = ?   ";
							pstmt = con.prepareStatement(UPDATE_LEAD_PROSPECT_DETAILS);
						pstmt.setString(indexCount++, leadDetailsDTO.getCityZoneCode());
						pstmt.setString(indexCount++, leadDetailsDTO.getCityCode());
						pstmt.setString(indexCount++, leadDetailsDTO.getStateCode());				
						pstmt.setString(indexCount++, leadDetailsDTO.getZoneCode());
					
						
						pstmt.setInt(indexCount++, leadDetailsDTO.getCircleId());
						pstmt.setString(indexCount++, leadDetailsDTO.getAddress2());
						pstmt.setString(indexCount++, leadDetailsDTO.getLanguage());
						
						if("".equals(leadDetailsDTO.getPinCode()))
			            {		            	
			            	pstmt.setString(indexCount++, null);
			            }else
			            {
			            	pstmt.setInt(indexCount++, Integer.parseInt(leadDetailsDTO.getPinCode()));
			            }	            
						
						pstmt.setString(indexCount++, leadDetailsDTO.getAlternateContactNo());
						pstmt.setString(indexCount++, leadDetailsDTO.getLandlineNo());
					//pstmt.setString(indexCount++, leadDetailsDTO.getAppointmentTime());
                       	//Added by satish
						if("".equals(leadDetailsDTO.getAppointmentTime())){
							
							pstmt.setString(indexCount++,currentdate );
							
						}else{
						pstmt.setString(indexCount++, leadDetailsDTO.getAppointmentTime());}
						
						//Added by satish
                        pstmt.setString(indexCount++, leadDetailsDTO.getAddress1());
						pstmt.setString(indexCount++, leadDetailsDTO.getExistingCustomer());
						pstmt.setString(indexCount++, leadDetailsDTO.getSubZoneName());
						pstmt.setString(indexCount++, leadDetailsDTO.getCreatedBy());
						pstmt.setInt(indexCount++, leadDetailsDTO.getProductLobId());	
						pstmt.setString(indexCount++, leadDetailsDTO.getRental());
						pstmt.setString(indexCount++, leadDetailsDTO.getPayment());
						pstmt.setString(indexCount++, leadDetailsDTO.getTransactionRefNo());
						  if(!leadDetailsDTO.getPlanId().equalsIgnoreCase(null) && !leadDetailsDTO.getPlanId().equalsIgnoreCase("") ){
						    	pstmt.setString(indexCount++, leadDetailsDTO.getPlanId());	
						    }
						  else{
							  pstmt.setString(indexCount++, "");
						  }
						  pstmt.setString(indexCount++, leadDetailsDTO.getHlrNo());
						  pstmt.setString(indexCount++, leadDetailsDTO.getSalesChannelCode());
                          //Added by satish
						  if(leadDetailsDTO.getAppointmentEndTime()==null||leadDetailsDTO.getAppointmentEndTime()==""){
							  pstmt.setString(indexCount++,currentdateplus2);
						  }
						  else{
						  pstmt.setString(indexCount++,leadDetailsDTO.getAppointmentEndTime());
						  
						  }
						  
						  //Added by satish
						  
						pstmt.setInt(indexCount++, leadDetailsDTO.getLeadProspectId());
						ijk = pstmt.executeUpdate();  
                       
						//Added by satish
						//String UPDATE_LEAD_UPDATE_DATA="UPDATE LEAD_UPDATE_DATA SET PREVIOUS_OPERATOR=?,RELATION_NAME=?,DOC_COLLECTED_FLAG=?,PREVIOUS_CIRCLE=? where LEAD_ID=?";
						pstmt4=con.prepareStatement(UPDATE_LEAD_UPDATE_DATA);
						
						pstmt4.setString(1, leadDetailsDTO.getPreviousOperator());
						pstmt4.setString(2, leadDetailsDTO.getRelationName());
						pstmt4.setString(3, leadDetailsDTO.getDocumentCollectedFlag());
						pstmt4.setString(4, leadDetailsDTO.getPreviousCircle());
						pstmt4.setLong(5, leadDetailsDTO.getLeadId());
//						pstmt4.executeUpdate();
						
						//Ended by satish
						
                   }

				  if(flag.equalsIgnoreCase("yes")) //update all the details except the location details when no is chosen both the times
				  {
					  //logger.info("No cross sell lead generated............................");
					String UPDATE_LEAD_PROSPECT_DETAILS_NO = "UPDATE LEAD_PROSPECT_DETAIL SET   ADDRESS2=?, PRIMARY_LANGUAGE=?, ALTERNATE_CONTACT_NUMBER=?, LANDLINE_NUMBER=?, APPOINTMENT_TIME=?, ADDRESS1=?, IS_CUSTOMER=?, UPDATED_DT= current timestamp, UPDATED_BY=?, RENTAL=?, PYT_AMT=? , TRAN_REFNO=? , MYOP_ID = ? , EXTRA_PARAMS_2 = ? , EXTRA_PARAMS_3 = ? ,APPOINMENT_ENDTIME =? WHERE LEAD_PROSPECT_ID = ?   ";
					pstmt2 = con.prepareStatement(UPDATE_LEAD_PROSPECT_DETAILS_NO);
					pstmt2.setString(indexCount++, leadDetailsDTO.getAddress2());
					pstmt2.setString(indexCount++, leadDetailsDTO.getLanguage());
					pstmt2.setString(indexCount++, leadDetailsDTO.getAlternateContactNo());
					pstmt2.setString(indexCount++, leadDetailsDTO.getLandlineNo());
				//	pstmt2.setString(indexCount++, leadDetailsDTO.getAppointmentTime());
                 //Added by satish
					if("".equals(leadDetailsDTO.getAppointmentTime())){
						
						pstmt.setString(indexCount++,currentdate );
						
					}else{
					pstmt.setString(indexCount++, leadDetailsDTO.getAppointmentTime());}
					
					//Added by satish
                    pstmt2.setString(indexCount++, leadDetailsDTO.getAddress1());
					pstmt2.setString(indexCount++, leadDetailsDTO.getExistingCustomer());
					pstmt2.setString(indexCount++, leadDetailsDTO.getCreatedBy());
					pstmt2.setString(indexCount++, leadDetailsDTO.getRental());
					pstmt2.setString(indexCount++, leadDetailsDTO.getPayment());
					pstmt2.setString(indexCount++, leadDetailsDTO.getTransactionRefNo());
					  if(!leadDetailsDTO.getPlanId().equalsIgnoreCase(null) && !leadDetailsDTO.getPlanId().equalsIgnoreCase("") ){
					    	pstmt2.setString(indexCount++, leadDetailsDTO.getPlanId());	
					    }
					  else{
						  pstmt2.setString(indexCount++, "");
					  }
					  pstmt2.setString(indexCount++, leadDetailsDTO.getHlrNo());
					  pstmt2.setString(indexCount++, leadDetailsDTO.getSalesChannelCode());
					  //pstmt2.setString(indexCount++,leadDetailsDTO.getAppointmentEndTime());
                       if(leadDetailsDTO.getAppointmentEndTime()==null||leadDetailsDTO.getAppointmentEndTime()==""){
						  pstmt.setString(indexCount++,currentdateplus2);
					  }
					  else{
					  pstmt.setString(indexCount++,leadDetailsDTO.getAppointmentEndTime());
					  
					  }
pstmt2.setInt(indexCount++, leadDetailsDTO.getLeadProspectId());
					ijk1 = pstmt2.executeUpdate();  
//Added by satish
				//	String UPDATE_LEAD_UPDATE_DATA="UPDATE LEAD_UPDATE_DATA SET PREVIOUS_OPERATOR=?,RELATION_NAME=?,DOC_COLLECTED_FLAG=?,PREVIOUS_CIRCLE=? where LEAD_ID=?";
					pstmt4=con.prepareStatement(UPDATE_LEAD_UPDATE_DATA);
					
					pstmt4.setString(1, leadDetailsDTO.getPreviousOperator());
					pstmt4.setString(2, leadDetailsDTO.getRelationName());
					pstmt4.setString(3, leadDetailsDTO.getDocumentCollectedFlag());
					pstmt4.setString(4, leadDetailsDTO.getPreviousCircle());
					pstmt4.setLong(5, leadDetailsDTO.getLeadId());
					pstmt4.executeUpdate();
					
					//Ended by satish
				  }
				  }
				  
			// Handling Cross Sell - Insert Lead Details for cross sell product
				
				String INSERT_LEAD_DATA = "INSERT INTO LEAD_DATA(LEAD_ID,PROSPECT_ID, SUB_SOURCE, SOURCE, PRODUCT_ID," +
						" LEAD_STATUS_ID, REQUEST_TYPE, CREATE_TIME, CROSSSELL_REF_LEAD_ID,REMARKS ,LEAD_PROSPECT_ID , UPDATED_DT, UPDATED_BY, LEAD_SUB_STATUS_ID, LEAD_SUB_SUB_STATUS_ID , ONLINE_CAF_NO, ALLOCATED_NO, LEAD_CATEGORY ) VALUES(?,?, ?,?,?,?,?, current timestamp,?, ? ,(Select LEAD_PROSPECT_ID from LEAD_PROSPECT_DETAIL where PROSPECT_ID=? and LEAD_ID =?   ) ,current timestamp ,?, ?, ? ,? , ? , ? )  ";
				// changed by amarjeet to add new editable fields
				String INSERT_LEAD_PROSPECT_DETAIL =" INSERT INTO LEAD_PROSPECT_DETAIL ( CITY_ZONE_CODE,CITY_CODE, STATE, ZONE_CODE, CIRCLE_ID, ADDRESS2,PRIMARY_LANGUAGE,PINCODE, ALTERNATE_CONTACT_NUMBER, LANDLINE_NUMBER, APPOINTMENT_TIME, ADDRESS1,PROSPECT_ID,  IS_CUSTOMER, MARITAL_STATUS, SUB_ZONE, RSU_CODE, UPDATED_DT, UPDATED_BY,PRODUCT_LOB_ID , RENTAL, PYT_AMT , TRAN_REFNO , MYOP_ID ,EXTRA_PARAMS_2 , EXTRA_PARAMS_3,APPOINMENT_ENDTIME,LEAD_ID)"+
	    		 " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, current timestamp, ?,? ,?, ?, ? ,? , ?, ?,?,? )"; //Query added by neetika // Lead Id Added By parnika
				
				//String SELECT_LEAD_PROSPECT_ID="Select LEAD_PROSPECT_ID from LEAD_PROSPECT_DETAIL where PROSPECT_ID=? and PRODUCT_LOB_ID=?   ";
				
				String SELECT_LEAD_PROSPECT_ID="Select LEAD_PROSPECT_ID from LEAD_PROSPECT_DETAIL where PROSPECT_ID=? and LEAD_ID=?   ";
				
				String INSERT_LEAD_TRANSACTION = "INSERT INTO LEAD_TRANSACTION(PRODUCT_ID, LEAD_ASSIGNMENT_TIME, LEAD_STATUS_ID,SUB_STATUS_ID, LEAD_ID, REMARKS, TRANSACTION_TIME,UPDATED_BY, UD_ID, LEAD_PRODUCT_ID ,CLIENT_IP, LEAD_ASSIGNED_PRIMARY_USER,LEAD_SUB_SUB_STATUS_ID)  VALUES((SELECT PRODUCT_LOB_ID  FROM PRODUCT_MSTR WHERE PRODUCT_ID = ?), current timestamp, ?, ?,?,?,current timestamp,?,?,?,?,?,?)  ";
				
			 String INSERT_LEAD_DETAILS="INSERT INTO LEAD_DETAILS(LEAD_ID, USER_TYPE, PLAN_ID, RENTAL_TYPE, STATE, BOOSTER_COUNT, FREEBIE_TAKEN, BOOSTER_TAKEN, PREPAID_NUMBER, OFFER, DOWNLOAD_LIMIT, DEVICE_MRP, VOICE_BENEFIT, DATA_QUOTA, NDNC_DISCLAIMER, FEASIBILITY_PARAM, DEVICE_TAKEN, FREEBIE_COUNT, BENEFIT, PKG_DURATION,HLR_NO,EXTRA_PARAM5,EXTRA_PARAM6,EXTRA_PARAM7) " +
			"VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";
		
			// String INSERT_LEAD_DETAILS="INSERT INTO LEAD_DETAILS(LEAD_ID, CREATED_DATE, UPDATED_DATE) " +
			//	"VALUES( ?,current timestamp, current timestamp)";
		
		
				MasterDao mdao = MasterDaoImpl.masterDaoInstance();
				List<ProductDTO> telemediaProductList =  mdao.getTelemediaProductList();
				
				// Inserting LEAD Details into LEAD_DATA table for Cross-sell Products.
				for (int jj = 0; jj < productIdsCrossSell.length && productIdsCrossSell[jj]>0; jj++)
				{					
					parameterIndex = 1;
					// Obtaining Lead Id for Cross-sell product
					leadId = getLeadId( leadDetailsDTO.getContactNo());
					logger.info("leadId generated for Cros-sell Product ::"+leadId);
					
					// identifying product type & lead status					
					int leadStatusCode = LMSStatusCodes.QUALIFIED ; //leadDetailsDTO.getLeadStatusId();
					boolean isCrossSellTelemediaProduct = false;
					for(int kk = 0; kk < telemediaProductList.size(); kk++)
					{
						if(telemediaProductList.get(kk).getProductId() == productIdsCrossSell[jj])
						{
							leadStatusCode = LMSStatusCodes.QUALIFICATION ;
							isCrossSellTelemediaProduct = true;
						break;
						}					
					}
											
					//Insert into LEAD_PROSPECT_DETAIL for crossSell//
					pstmt2=con.prepareStatement(INSERT_LEAD_PROSPECT_DETAIL);
					parameterIndex=1;
					pstmt2.setString(parameterIndex++,leadDetailsDTO.getCityZoneCode());
					pstmt2.setString(parameterIndex++,leadDetailsDTO.getCityCode());
				
					pstmt2.setString(parameterIndex++, leadDetailsDTO.getStateCode());
					pstmt2.setString(parameterIndex++, leadDetailsDTO.getZoneCode());
					//pstmt6.setString(parameterIndex++, leadDetailsDTO.getEmail());
					pstmt2.setInt(parameterIndex++, leadDetailsDTO.getCircleId());
					pstmt2.setString(parameterIndex++, leadDetailsDTO.getAddress2());
					pstmt2.setString(parameterIndex++, leadDetailsDTO.getLanguage());  
					 
		            if("".equals(leadDetailsDTO.getPinCode()))
		            {		            	
		            	pstmt2.setString(parameterIndex++, null);
		            }else
		            {
		            	pstmt2.setInt(parameterIndex++, Integer.parseInt(leadDetailsDTO.getPinCode()));
		            }
		            pstmt2.setString(parameterIndex++, leadDetailsDTO.getAlternateContactNo());	
		            pstmt2.setString(parameterIndex++, leadDetailsDTO.getLandlineNo());		
                    //pstmt2.setString(parameterIndex++, leadDetailsDTO.getAppointmentTime());
                     if("".equals(leadDetailsDTO.getAppointmentTime())){
		            	
		            	pstmt2.setString(parameterIndex++,currentdate);
		            	
		            }else{
		            
		            pstmt2.setString(parameterIndex++, leadDetailsDTO.getAppointmentTime());
		            
		            }
                    pstmt2.setString(parameterIndex++, leadDetailsDTO.getAddress1());
		            pstmt2.setInt(parameterIndex++, leadDetailsDTO.getProspectId());
				    pstmt2.setString(parameterIndex++, leadDetailsDTO.getExistingCustomer());
				    pstmt2.setString(parameterIndex++, leadDetailsDTO.getMaritalStatus());
				    pstmt2.setString(parameterIndex++, leadDetailsDTO.getSubZoneName());
				    pstmt2.setString(parameterIndex++, leadDetailsDTO.getRsuCode());
				    pstmt2.setString(parameterIndex++, leadDetailsDTO.getCreatedBy());//Neetika Updated by column
				    //,PRODUCT_LOB_ID
				    pstmt2.setInt(parameterIndex++,mstrService.getProductLobId(productIdsCrossSell[jj]));//27
				    pstmt2.setString(parameterIndex++, leadDetailsDTO.getRental());
				    pstmt2.setString(parameterIndex++, leadDetailsDTO.getPayment());
				    pstmt2.setString(parameterIndex++, leadDetailsDTO.getTransactionRefNo());
				    if(!leadDetailsDTO.getPlanId().equalsIgnoreCase(null) && !leadDetailsDTO.getPlanId().equalsIgnoreCase("") ){
				    	pstmt2.setString(parameterIndex++, leadDetailsDTO.getPlanId());	
				    }
				  else{
					  pstmt2.setString(parameterIndex++, "0");
				  }
				    pstmt2.setString(parameterIndex++, leadDetailsDTO.getHlrNo());
				    /* Added By Parnika on 13 May 2014 */
				    //added by Nancy
				    pstmt2.setString(parameterIndex++, leadDetailsDTO.getSalesChannelCode());
					  
				    if("".equals(leadDetailsDTO.getAppointmentEndTime()))
		            {		            	
		            	pstmt2.setString(parameterIndex++, currentdateplus2);
		            }else
		            {
		            	pstmt2.setString(parameterIndex++, leadDetailsDTO.getAppointmentEndTime());
		            }
					pstmt2.setString(parameterIndex++, leadId.toString());
					/* End of changes By Parnika */
				    pstmt2.executeUpdate();
				    
				    
				    //Added BY Bhaskar
				   // String INSERT_LEAD_DETAILS="INSERT INTO LEAD_DETAILS(LEAD_ID, USER_TYPE, PLAN_ID, RENTAL_TYPE, STATE, BOOSTER_COUNT, FREEBIE_TAKEN, BOOSTER_TAKEN, PREPAID_NUMBER, OFFER, DOWNLOAD_LIMIT, DEVICE_MRP, VOICE_BENEFIT, DATA_QUOTA, NDNC_DISCLAIMER, FEASIBILITY_PARAM, DEVICE_TAKEN, FREEBIE_COUNT, BENEFIT, PKG_DURATION,HLR_NO,CREATED_DATE, UPDATED_DATE, UPDATED_BY) " +
					//"VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current timestamp, current timestamp, ?)  ";
			
				  //Insert into LEAD_DETAILS for crossSell//
				    pstmtLD=con.prepareStatement(INSERT_LEAD_DETAILS);
					parameterIndex=1;
					pstmtLD.setString(parameterIndex++,leadId.toString());
				pstmtLD.setString(parameterIndex++,leadDetailsDTO.getUserType());
				
					pstmtLD.setString(parameterIndex++, leadDetailsDTO.getPlanId());
					pstmtLD.setString(parameterIndex++, leadDetailsDTO.getRentalType());
					pstmtLD.setString(parameterIndex++, leadDetailsDTO.getState());
					pstmtLD.setString(parameterIndex++, leadDetailsDTO.getBoosterCount());
					pstmtLD.setString(parameterIndex++, leadDetailsDTO.getFreebieTaken());  
					pstmtLD.setString(parameterIndex++, leadDetailsDTO.getBoosterTaken());	
					//pstmtLD.setBoolean(parameterIndex++, leadDetailsDTO.getFlag());		
					pstmtLD.setString(parameterIndex++, leadDetailsDTO.getPrepaidNumber());
					pstmtLD.setString(parameterIndex++, leadDetailsDTO.getOffer());
					pstmtLD.setString(parameterIndex++, leadDetailsDTO.getDownloadLimit());
					pstmtLD.setString(parameterIndex++, leadDetailsDTO.getDeviceMrp());
					pstmtLD.setString(parameterIndex++, leadDetailsDTO.getVoiceBenefit());
					pstmtLD.setString(parameterIndex++, leadDetailsDTO.getDataQuota());
					pstmtLD.setString(parameterIndex++, leadDetailsDTO.getNdncDisclaimer());
					pstmtLD.setString(parameterIndex++, leadDetailsDTO.getFeasibilityParam());
					pstmtLD.setString(parameterIndex++, leadDetailsDTO.getDevicetaken());
					pstmtLD.setString(parameterIndex++, leadDetailsDTO.getFreebieCount());
					pstmtLD.setString(parameterIndex++, leadDetailsDTO.getBenefit());
					pstmtLD.setString(parameterIndex++, leadDetailsDTO.getPkgDuration());
					pstmtLD.setString(parameterIndex++, leadDetailsDTO.getHlrNo());
					pstmtLD.setString(parameterIndex++, leadDetailsDTO.getExtraParam5());
					pstmtLD.setString(parameterIndex++, leadDetailsDTO.getExtraParam6());
					pstmtLD.setString(parameterIndex++, leadDetailsDTO.getExtraParam7());
				
					pstmtLD.executeUpdate();
//Added by satish
					String UPDATE_LEAD_UPDATE_DATA="UPDATE LEAD_UPDATE_DATA SET PREVIOUS_OPERATOR=?,RELATION_NAME=?,DOC_COLLECTED_FLAG=?,PREVIOUS_CIRCLE=? where LEAD_ID=?";
					pstmt4=con.prepareStatement(UPDATE_LEAD_UPDATE_DATA);
					
					pstmt4.setString(1, leadDetailsDTO.getPreviousOperator());
					pstmt4.setString(2, leadDetailsDTO.getRelationName());
					pstmt4.setString(3, leadDetailsDTO.getDocumentCollectedFlag());
					pstmt4.setString(4, leadDetailsDTO.getPreviousCircle());
					pstmt4.setLong(5, leadDetailsDTO.getLeadId());
					pstmt4.executeUpdate();
					
					//Ended by satish
					logger.info("updateLeadDetailsDialler  INSERT_LEAD_DETAILS >>>>> lead id "+leadId);	
					
				    //End By Bhaskar
				    
				    // Commented By Parnika on 13 May 2014}
					// Inserting Lead Details
					pstmt = con.prepareStatement(INSERT_LEAD_DATA);
					parameterIndex=1;
					pstmt.setLong(parameterIndex++,leadId );
					pstmt.setInt(parameterIndex++,leadDetailsDTO.getProspectId());		
					
					 if(leadDetailsDTO.getSubSourceId() ==0)
			            {
			            	pstmt.setString(parameterIndex++, String.valueOf(Constants.SUB_SOURCE_TYPE_CALL_CENTER));
			            }else
			            {
			            	 pstmt.setInt(parameterIndex++,leadDetailsDTO.getSubSourceId());
			            }
					 if(leadDetailsDTO.getSourceId() ==0)
			            {
			            	pstmt.setString(parameterIndex++, null);
			            }else
			            {
			            	//pstmt.setInt(parameterIndex++, Constants.DOC_TYPE_SOP_BD);
			            	//changed by amarjeet			            	
			            	pstmt.setInt(parameterIndex++, leadDetailsDTO.getSourceId());
			            }
		           
		            pstmt.setInt(parameterIndex++, productIdsCrossSell[jj]);
		            pstmt.setInt(parameterIndex++, leadStatusCode);
		           
		            if(leadDetailsDTO.getRequestType() ==0)
		            {
		            	pstmt.setString(parameterIndex++, null);
		            }else
		            {
		            	 pstmt.setInt(parameterIndex++, leadDetailsDTO.getRequestType());  
		            }
		            pstmt.setLong(parameterIndex++, leadDetailsDTO.getLeadId());
					pstmt.setString(parameterIndex++, leadDetailsDTO.getRemarks());
					pstmt.setInt(parameterIndex++, leadDetailsDTO.getProspectId());
					/* Commented By Parnika 
					pstmt.setInt(parameterIndex++,mstrService.getProductLobId(productIdsCrossSell[jj]));//27 */
					
					/* Added By Parnika  on 13 May 2014 */
					pstmt.setString(parameterIndex++,leadId.toString());
					/* End of changes By parnika */
					pstmt.setString(parameterIndex++, leadDetailsDTO.getCreatedBy());
					pstmt.setInt(parameterIndex++, leadStatusCode);
					pstmt.setInt(parameterIndex++, leadDetailsDTO.getLeadSubSubStatusid());
					pstmt.setString(parameterIndex++, leadDetailsDTO.getOnlineCafNo());
					pstmt.setString(parameterIndex++, leadDetailsDTO.getAllocatedNo());
					pstmt.setInt(parameterIndex++, Constants.LEAD_CATEGORY_CROSSEL);				
					pstmt.execute();					
					
					logger.info("updateLeadDetailsDialler  INSERT_LEAD_TRANSACTION >>>>> lead id "+leadId);	
					
					// Inserting LEAD TRANSACTION Details into LEAD_TRANSACTION table
					
					int parameterIndex2=1;

						// First record as Lead QUALIFIED in case of Non Telemedia product and Qualification in case of Telemedia Products			
						pstmtLTX = con.prepareStatement(INSERT_LEAD_TRANSACTION);
						parameterIndex2=1;
						pstmtLTX.setInt(parameterIndex2++, productIdsCrossSell[jj]);
						pstmtLTX.setInt(parameterIndex2++, leadStatusCode );
						pstmtLTX.setInt(parameterIndex2++, leadStatusCode );
						pstmtLTX.setLong(parameterIndex2++,leadId );
						pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getRemarks());	
						pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getCreatedBy());
						pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getUdId());
						pstmtLTX.setInt(parameterIndex2++, productIdsCrossSell[jj]);
						pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getIpAddress());
						pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getCreatedBy());
						//						added by Bhaskar
						pstmtLTX.setInt(parameterIndex2++, leadDetailsDTO.getLeadSubSubStatusid());
						pstmtLTX.execute();	
						logger.info("First record as Lead QUALIFIED Status in case of Non Telemedia product");	
						
						if(isCrossSellTelemediaProduct)
						{
							// Second record as Lead Qualified for Telemedia product.				
							pstmtLTX = con.prepareStatement(INSERT_LEAD_TRANSACTION);
							parameterIndex2=1;
							pstmtLTX.setInt(parameterIndex2++, productIdsCrossSell[jj]);
							pstmtLTX.setInt(parameterIndex2++, LMSStatusCodes.QUALIFIED);
							pstmtLTX.setInt(parameterIndex2++, LMSStatusCodes.QUALIFIED );
							pstmtLTX.setLong(parameterIndex2++,leadId );
							pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getRemarks());	
							pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getCreatedBy());
							pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getUdId());
							pstmtLTX.setInt(parameterIndex2++, productIdsCrossSell[jj]);
							pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getIpAddress());
							pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getCreatedBy());
							//						added by Bhaskar
							pstmtLTX.setInt(parameterIndex2++, leadDetailsDTO.getLeadSubSubStatusid());
							pstmtLTX.execute();	
//Added by satish
				//			String UPDATE_LEAD_UPDATE_DATA="UPDATE LEAD_UPDATE_DATA SET PREVIOUS_OPERATOR=?,RELATION_NAME=?,DOC_COLLECTED_FLAG=?,PREVIOUS_CIRCLE=? where LEAD_ID=?";
							pstmt4=con.prepareStatement(UPDATE_LEAD_UPDATE_DATA);
							
							pstmt4.setString(1, leadDetailsDTO.getPreviousOperator());
							pstmt4.setString(2, leadDetailsDTO.getRelationName());
							pstmt4.setString(3, leadDetailsDTO.getDocumentCollectedFlag());
							pstmt4.setString(4, leadDetailsDTO.getPreviousCircle());
							pstmt4.setLong(5, leadDetailsDTO.getLeadId());
							pstmt4.executeUpdate();
							
							//Ended by satish
							
						}
				}
				
		// End of Handling Cross Sell
	
				boolean isTelemediaProduct = false;
				
				for(int kk = 0; kk < telemediaProductList.size(); kk++)
				{
					if(telemediaProductList.get(kk).getProductId() == leadDetailsDTO.getProductId() )
					{
						isTelemediaProduct = true;		
						break;
					}					
				}	
				//System.out.println("outside tekemedia check block*****************************"+leadDetailsDTO.getLeadStatusId()+"*******"+(leadDetailsDTO.getLeadStatusId() == LMSStatusCodes.LOST)+"***************"+isTelemediaProduct+"*******"+productId);
				if(isTelemediaProduct && productId == 0)  //Lead data update in case of telemedia product and not a cross-sell product
				{
					
					//System.out.println("inside is telemedia product block**************************"+leadDetailsDTO.getLeadStatusId()+"*******"+(leadDetailsDTO.getLeadStatusId() == LMSStatusCodes.LOST));
					// Incase Telemedia product than Main is not updated for QUALIFIED
					// Main is updated only for UNQUALIFIED, UNQUALIFIED_INVALID, FEASIBILITY_INFO_DONE					
					   
					if((leadDetailsDTO.getLeadStatusId() == LMSStatusCodes.LOST) ||	(leadDetailsDTO.getLeadStatusId() == LMSStatusCodes.FEASIBILITY_INFO_DONE))
					{
						//System.out.println("inside condition true block**************************"+leadDetailsDTO.getLeadStatusId());
					// Update Lead data	for existing lead 			
					String UPDATE_LEAD_DATA = "UPDATE LEAD_DATA SET LEAD_STATUS_ID=?, REQUEST_TYPE=?, UPDATED_DT= CURRENT TIMESTAMP , REMARKS=? , LEAD_SUB_STATUS_ID = ? , LEAD_SUB_SUB_STATUS_ID = ? , ONLINE_CAF_NO = ? , ALLOCATED_NO = ?  WHERE LEAD_ID=?  ";
					pstmt = con.prepareStatement(UPDATE_LEAD_DATA);
					indexCount = 1;
					
					//pstmt.setString(indexCount++, leadDetailsDTO.getSubSourceId()+"");
					//pstmt.setString(indexCount++, leadDetailsDTO.getSourceId()+"");
					pstmt.setInt(indexCount++, leadDetailsDTO.getLeadStatusId());				
					pstmt.setString(indexCount++, leadDetailsDTO.getRequestType()+"");
					pstmt.setString(indexCount++, leadDetailsDTO.getRemarks());
					//pstmt.setLong(indexCount++, leadDetailsDTO.getLeadSubSubStatusid());
					pstmt.setInt(indexCount++, leadDetailsDTO.getLeadSubStatusId());	
					pstmt.setInt(indexCount++, leadDetailsDTO.getLeadSubSubStatusid());
					pstmt.setString(indexCount++, leadDetailsDTO.getOnlineCafNo());	
					pstmt.setString(indexCount++, leadDetailsDTO.getAllocatedNo());	
				
					pstmt.setLong(indexCount++, leadDetailsDTO.getLeadId());
				
					ijk = pstmt.executeUpdate();
					
					
					// Inserting LEAD tRANSACTION Details
					//System.out.println("insert new entry into lead transcation*********************");
					String INSERT_TRANSACTION = "INSERT INTO LEAD_TRANSACTION(PRODUCT_ID, LEAD_ASSIGNMENT_TIME, LEAD_STATUS_ID,SUB_STATUS_ID, LEAD_ID, REMARKS, TRANSACTION_TIME,UPDATED_BY, UD_ID, LEAD_PRODUCT_ID ,CLIENT_IP ,LEAD_ASSIGNED_PRIMARY_USER, LEAD_SUB_SUB_STATUS_ID, EXPECTED_CLOSURE_DATE)  VALUES((SELECT PRODUCT_LOB_ID  FROM PRODUCT_MSTR WHERE PRODUCT_ID = ?),(SELECT LEAD_ASSIGNMENT_TIME FROM LEAD_TRANSACTION WHERE LEAD_ID = ? ORDER BY TRANSACTION_TIME DESC FETCH FIRST ROW ONLY), ?, ?,?,?,current timestamp,?,?,?,?,?,?";
					StringBuffer query = new StringBuffer(INSERT_TRANSACTION);
					if(leadDetailsDTO.getIsSecondcall())
					query.append(",(select EXPECTED_CLOSURE_DATE from LEAD_TRANSACTION where LEAD_ID = ? and SUB_STATUS_ID = 370 order by TRANSACTION_TIME desc fetch first row only))  ");
					else
					query.append(",(select EXPECTED_CLOSURE_DATE from LEAD_TRANSACTION where LEAD_ID = ?  order by TRANSACTION_TIME desc fetch first row only))  ");
					pstmtLTX = con.prepareStatement(query.toString());
					int parameterIndex2=1;
					pstmtLTX.setInt(parameterIndex2++, leadDetailsDTO.getProductId());
					pstmtLTX.setLong(parameterIndex2++,leadDetailsDTO.getLeadId() );
					//pstmtLTX.setLong(parameterIndex2++,leadDetailsDTO.getLeadId() );
					pstmtLTX.setInt(parameterIndex2++,leadDetailsDTO.getLeadStatusId());
					pstmtLTX.setInt(parameterIndex2++,leadDetailsDTO.getLeadSubStatusId());
					pstmtLTX.setLong(parameterIndex2++,leadDetailsDTO.getLeadId());
					pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getRemarks());	
					pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getCreatedBy());
					pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getUdId());
					pstmtLTX.setInt(parameterIndex2++, leadDetailsDTO.getLeadProductId());
					pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getIpAddress());
					pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getCreatedBy());
					// Added by Parnika 
					pstmtLTX.setInt(parameterIndex2++, leadDetailsDTO.getLeadSubSubStatusid());
					pstmtLTX.setLong(parameterIndex2++,leadDetailsDTO.getLeadId());					
					pstmtLTX.execute();	
					
					}
					else if(leadDetailsDTO.getLeadStatusId() == LMSStatusCodes.QUALIFIED)
					{
						//System.out.println("inside qualified block************************"+leadDetailsDTO.getLeadStatusId());
						// Inserting LEAD tRANSACTION Details
						StringBuffer query2 = new StringBuffer("INSERT INTO LEAD_TRANSACTION(PRODUCT_ID, LEAD_ASSIGNMENT_TIME, LEAD_STATUS_ID,SUB_STATUS_ID, LEAD_ID, REMARKS, TRANSACTION_TIME,UPDATED_BY, UD_ID, LEAD_PRODUCT_ID ,CLIENT_IP ,LEAD_ASSIGNED_PRIMARY_USER,LEAD_SUB_SUB_STATUS_ID, EXPECTED_CLOSURE_DATE)  VALUES((SELECT PRODUCT_LOB_ID  FROM PRODUCT_MSTR WHERE PRODUCT_ID = ?),(SELECT LEAD_ASSIGNMENT_TIME FROM LEAD_TRANSACTION WHERE LEAD_ID = ? ORDER BY TRANSACTION_TIME DESC FETCH FIRST ROW ONLY), ?, ?,?,?,current timestamp,?,?,?,?,?,?");
						if(leadDetailsDTO.getIsSecondcall())
							query2.append(",(select EXPECTED_CLOSURE_DATE from LEAD_TRANSACTION where LEAD_ID = ? and SUB_STATUS_ID = 370 order by TRANSACTION_TIME desc fetch first row only))  ");
							else
							query2.append(",(select EXPECTED_CLOSURE_DATE from LEAD_TRANSACTION where LEAD_ID = ?  order by TRANSACTION_TIME desc fetch first row only))  ");
		
						pstmtLTX = con.prepareStatement(query2.toString());
						int parameterIndex2=1;
						pstmtLTX.setInt(parameterIndex2++, leadDetailsDTO.getProductId());
						pstmtLTX.setLong(parameterIndex2++,leadDetailsDTO.getLeadId() );
						//pstmtLTX.setLong(parameterIndex2++,leadDetailsDTO.getLeadId() );
						pstmtLTX.setInt(parameterIndex2++,leadDetailsDTO.getLeadStatusId());
						pstmtLTX.setInt(parameterIndex2++,leadDetailsDTO.getLeadSubStatusId());
						pstmtLTX.setLong(parameterIndex2++,leadDetailsDTO.getLeadId());
						pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getRemarks());	
						pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getCreatedBy());
						pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getUdId());
						pstmtLTX.setInt(parameterIndex2++, leadDetailsDTO.getLeadProductId());
						pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getIpAddress());
						pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getCreatedBy());
						pstmtLTX.setInt(parameterIndex2++, leadDetailsDTO.getLeadSubSubStatusid());
						pstmtLTX.setLong(parameterIndex2++,leadDetailsDTO.getLeadId());						
						pstmtLTX.execute();	
					}
					else
					{
						//System.out.println("final else block*********************");
						// Inserting LEAD tRANSACTION Details
						StringBuffer query3 = new StringBuffer("INSERT INTO LEAD_TRANSACTION(PRODUCT_ID, LEAD_ASSIGNMENT_TIME, LEAD_STATUS_ID,SUB_STATUS_ID, LEAD_ID, REMARKS, TRANSACTION_TIME,UPDATED_BY, UD_ID, LEAD_PRODUCT_ID ,CLIENT_IP ,LEAD_ASSIGNED_PRIMARY_USER,LEAD_SUB_SUB_STATUS_ID, EXPECTED_CLOSURE_DATE)  VALUES((SELECT PRODUCT_LOB_ID  FROM PRODUCT_MSTR WHERE PRODUCT_ID = ?),(SELECT LEAD_ASSIGNMENT_TIME FROM LEAD_TRANSACTION WHERE LEAD_ID = ? ORDER BY TRANSACTION_TIME DESC FETCH FIRST ROW ONLY), (SELECT LEAD_STATUS_ID FROM LEAD_TRANSACTION WHERE LEAD_ID = ? ORDER BY TRANSACTION_TIME DESC FETCH FIRST ROW ONLY), ?,?,?,current timestamp,?,?,?,?,?,?");
						if(leadDetailsDTO.getIsSecondcall())
							query3.append(",(select EXPECTED_CLOSURE_DATE from LEAD_TRANSACTION where LEAD_ID = ? and SUB_STATUS_ID = 370 order by TRANSACTION_TIME desc fetch first row only))  ");
							else
							query3.append(",(select EXPECTED_CLOSURE_DATE from LEAD_TRANSACTION where LEAD_ID = ?  order by TRANSACTION_TIME desc fetch first row only))  ");
		
						pstmtLTX = con.prepareStatement(query3.toString());
						int parameterIndex2=1;
						pstmtLTX.setInt(parameterIndex2++, leadDetailsDTO.getProductId());
						pstmtLTX.setLong(parameterIndex2++,leadDetailsDTO.getLeadId() );
						pstmtLTX.setLong(parameterIndex2++,leadDetailsDTO.getLeadId() );
						//pstmtLTX.setInt(parameterIndex2++,leadDetailsDTO.getLeadStatusId());
						pstmtLTX.setInt(parameterIndex2++,leadDetailsDTO.getLeadSubStatusId());
						pstmtLTX.setLong(parameterIndex2++,leadDetailsDTO.getLeadId());
						pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getRemarks());	
						pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getCreatedBy());
						pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getUdId());
						pstmtLTX.setInt(parameterIndex2++, leadDetailsDTO.getLeadProductId());
						pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getIpAddress());
						pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getCreatedBy());
						pstmtLTX.setInt(parameterIndex2++, leadDetailsDTO.getLeadSubSubStatusid());
						pstmtLTX.setLong(parameterIndex2++,leadDetailsDTO.getLeadId());						
						pstmtLTX.execute();	
					}
					//Request Type used as  product Type in lead data table change by Beeru on 25--08-2014
					try {
					if(leadDetailsDTO.getRequestType() > 0) {
						String UPDATE_LEAD_DATA = "UPDATE LEAD_DATA SET  REQUEST_TYPE=?, UPDATED_DT= CURRENT TIMESTAMP  WHERE LEAD_ID=?  ";
						pstmt = con.prepareStatement(UPDATE_LEAD_DATA);
						indexCount = 1;
						
						//pstmt.setString(indexCount++, leadDetailsDTO.getSubSourceId()+"");
						//pstmt.setString(indexCount++, leadDetailsDTO.getSourceId()+"");
						pstmt.setString(indexCount++, leadDetailsDTO.getRequestType()+"");
						//pstmt.setLong(indexCount++, leadDetailsDTO.getLeadSubSubStatusid());
						pstmt.setLong(indexCount++, leadDetailsDTO.getLeadId());
					
						ijk = pstmt.executeUpdate();
					}
					}catch(Exception e){
						logger.info("Request Type not update due to error:" +e.getMessage());
					}
					
				} else { // For all other products main is updated as QUALIFIED
					
			 if(productId==0)
					  {
				int leadUpdatedStatus = leadDetailsDTO.getLeadStatusId(); 
				//System.out.println("inside product id=0 block************************"+leadDetailsDTO.getLeadStatusId());
				// Update Lead data	for existing lead 			
				String UPDATE_LEAD_DATA = "UPDATE LEAD_DATA SET  LEAD_STATUS_ID=?, REQUEST_TYPE=?,UPDATED_DT= CURRENT TIMESTAMP , REMARKS=? , LEAD_SUB_STATUS_ID = ? , LEAD_SUB_SUB_STATUS_ID = ? , ONLINE_CAF_NO = ?, ALLOCATED_NO = ? WHERE LEAD_ID=?  ";
				pstmt = con.prepareStatement(UPDATE_LEAD_DATA);
				indexCount = 1;
				
				//pstmt.setString(indexCount++, leadDetailsDTO.getSubSourceId()+"");
				//pstmt.setString(indexCount++, leadDetailsDTO.getSourceId()+"");
				pstmt.setInt(indexCount++, leadUpdatedStatus);				
				pstmt.setString(indexCount++, leadDetailsDTO.getRequestType()+"");
				pstmt.setString(indexCount++, leadDetailsDTO.getRemarks());
				pstmt.setInt(indexCount++, leadDetailsDTO.getLeadSubStatusId());	
				pstmt.setInt(indexCount++, leadDetailsDTO.getLeadSubSubStatusid());
				pstmt.setString(indexCount++, leadDetailsDTO.getOnlineCafNo());
				pstmt.setString(indexCount++, leadDetailsDTO.getAllocatedNo());
			
				pstmt.setLong(indexCount++, leadDetailsDTO.getLeadId());
					
				ijk = pstmt.executeUpdate();
				
				// Inserting LEAD tRANSACTION Details for existing lead
				String INSERT_LEAD_TRANSACTION_FOR_EXISTING_LEAD= "INSERT INTO LEAD_TRANSACTION(PRODUCT_ID, LEAD_ASSIGNMENT_TIME, LEAD_STATUS_ID,SUB_STATUS_ID, LEAD_ID, REMARKS, TRANSACTION_TIME,UPDATED_BY , UD_ID, LEAD_PRODUCT_ID ,CLIENT_IP ,LEAD_ASSIGNED_PRIMARY_USER,EXPECTED_CLOSURE_DATE, LEAD_SUB_SUB_STATUS_ID)  VALUES((SELECT PRODUCT_LOB_ID  FROM PRODUCT_MSTR WHERE PRODUCT_ID = ?),(SELECT LEAD_ASSIGNMENT_TIME FROM LEAD_TRANSACTION WHERE LEAD_ID = ? ORDER BY TRANSACTION_TIME DESC FETCH FIRST ROW ONLY), ?, ?,?,?,current timestamp,?,?,?,?,?,(select EXPECTED_CLOSURE_DATE from LEAD_TRANSACTION where LEAD_ID = ? order by TRANSACTION_TIME desc fetch first row only),?)  ";
				pstmtLTX = con.prepareStatement(INSERT_LEAD_TRANSACTION_FOR_EXISTING_LEAD);
				int parameterIndex2=1;
				pstmtLTX.setInt(parameterIndex2++, leadDetailsDTO.getProductId());
				pstmtLTX.setLong(parameterIndex2++,leadDetailsDTO.getLeadId());
				pstmtLTX.setInt(parameterIndex2++,leadUpdatedStatus);
				pstmtLTX.setInt(parameterIndex2++,leadDetailsDTO.getLeadSubStatusId());
				pstmtLTX.setLong(parameterIndex2++,leadDetailsDTO.getLeadId() );
				pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getRemarks());	
				pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getCreatedBy());
				pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getUdId());
				pstmtLTX.setInt(parameterIndex2++, leadDetailsDTO.getLeadProductId());
				pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getIpAddress());
				pstmtLTX.setString(parameterIndex2++, leadDetailsDTO.getCreatedBy());
				pstmtLTX.setLong(parameterIndex2++,leadDetailsDTO.getLeadId());
				pstmtLTX.setInt(parameterIndex2++, leadDetailsDTO.getLeadSubSubStatusid());
				pstmtLTX.execute();	
				}
				}
				// Committing current Prospect's details   
				con.commit();
				updateStatus = "SUCCESS"+"#"+leadId;
				
				try {
						if (leadDetailsDTO.getLeadId() !=0){
							
							ResourceBundle rb=ResourceBundle.getBundle("ApplicationResources");
							String schemaName=rb.getString("lms.schema.bulk.download");
							cs = con.prepareCall("{call "+schemaName+".PRC_CALULATE_SCORE(?,?,?,?,?)}");
							cs.setLong(1, (leadDetailsDTO.getLeadId()));
							if(leadDetailsDTO.getLeadStatusId()==Integer.parseInt(Constants.LEAD_STATUS_OPEN)){
								cs.setShort(2,Constants.SCORE_ONE);
							}
								
							else if(leadDetailsDTO.getLeadStatusId()==Integer.parseInt(Constants.LEAD_STATUS_QUALIFIED) && leadDetailsDTO.getProductLobId() != Constants.PRODUCT_LOB_ID ){
								cs.setShort(2,Constants.SCORE_TWO);
							}
								
							else if(leadDetailsDTO.getLeadStatusId()==Integer.parseInt(Constants.LEAD_STATUS_VERIFICATION) && leadDetailsDTO.getProductLobId() == Constants.PRODUCT_LOB_ID ){
								cs.setShort(2,Constants.SCORE_TWO);
							}
								
							else{
								cs.setShort(2,Constants.SCORE_ONE);
							}
								
							cs.registerOutParameter(3, java.sql.Types.VARCHAR);
							cs.registerOutParameter(4, java.sql.Types.VARCHAR);
							cs.registerOutParameter(5, java.sql.Types.INTEGER);
							cs.execute();
						}
					} catch (Exception e) {
						
						e.printStackTrace();
					}
				
				
				}
				catch (Exception e) {
					con.rollback();
					e.printStackTrace();
					updateStatus="UPDATE_FAILURE"+"#"+leadId;
				}
			}
        }
        catch(Exception e1)
	        {
	            e1.printStackTrace();
	            throw new LMSException(e1.getMessage(), e1);
	        }
        
        finally{
		        try
		        {
		        	con.setAutoCommit(true);
		            //DBConnection.releaseResources(con, pstmt, rs);
		            //DBConnection.releaseResources(null, pstmtLTX, null);
		        }
		        catch(Exception e)
		        {
		            e.printStackTrace();
		            throw new DAOException(e.getMessage(), e);
		        }
        }
        return updateStatus;      
    }


	public String insertLeadSearchTransaction(LeadRegistrationFormBean leadRegistrationFormBean, String userLoginId,String ipaddress) throws DAOException {
		
		
		String insertLeadSearchTransaction = null;
		Connection con = null;
		PreparedStatement ps = null;  
		ResultSet rs = null;
		int i=0;
		
		String  SQL_INSERT_LEAD_SEARCH_TRANSACTION_DETAILS="INSERT INTO LEAD_SEARCH_TRANSACTION (LEAD_ID,OLM_ID,SEARCH_TIME,IP_ADDRESS,SEARCH_TYPE) VALUES(?,?,CURRENT TIMESTAMP,?,?)  ";
		try {
			con = DBConnection.getDBConnection();
			
			ps = con.prepareStatement(SQL_INSERT_LEAD_SEARCH_TRANSACTION_DETAILS);
		
			System.out.println("--------------"+leadRegistrationFormBean.getLeadId()+""+userLoginId.toUpperCase()+"-"+ipaddress);	
			ps.setLong(1,Long.parseLong(leadRegistrationFormBean.getLeadId()));
			ps.setString(2,userLoginId.toUpperCase());
			ps.setString(3, ipaddress);
			ps.setString(4,"Lead Search");
			
			i=ps.executeUpdate();
			
			}
		catch (Exception e) {
			e.printStackTrace();
			throw new DAOException("Exception occured while getting product list :  "+ e.getMessage(),e);
		} finally {
			try {
				////DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return insertLeadSearchTransaction;
	}


	public String insertLeadSearchDialerTransaction(LeadRegistrationFormBean leadRegistrationFormBean, String userLoginId,String ipaddress) throws DAOException {

		
		String insertLeadSearchDialerTransaction = null;
		Connection con = null;
		PreparedStatement ps = null;  
		ResultSet rs = null;
		int i=0;
		
		String  SQL_INSERT_LEAD_SEARCH_DIALER_TRANSACTION_DETAILS="INSERT INTO LEAD_SEARCH_TRANSACTION (LEAD_ID,OLM_ID,SEARCH_TIME,IP_ADDRESS,SEARCH_TYPE) VALUES(?,?,CURRENT TIMESTAMP,?,?)  ";
		
		try {
			con = DBConnection.getDBConnection();
			
			ps = con.prepareStatement(SQL_INSERT_LEAD_SEARCH_DIALER_TRANSACTION_DETAILS);
		
				
			ps.setLong(1,Long.parseLong(leadRegistrationFormBean.getLeadId()));
			ps.setString(2,userLoginId.toUpperCase());
			ps.setString(3,ipaddress);
			ps.setString(4,"Lead Search Dialer");
			
			i=ps.executeUpdate();
			
			}
		catch (Exception e) {
			e.printStackTrace();
			throw new DAOException("Exception occured while getting product list :  "+ e.getMessage(),e);
		} finally {
			try {
				//DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return insertLeadSearchDialerTransaction;
	}
//sud
	public  ArrayList<ProductDTO> getProductLobList() throws DAOException
	{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ProductDTO> productList = new ArrayList<ProductDTO>();
		ProductDTO dto = null;
		try {
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement("SELECT PRODUCT_LOB, PRODUCT_LOB_ID FROM PRODUCT_LOB  ");
			rs = ps.executeQuery();
			while(rs.next()) {
				dto = new ProductDTO();
				dto.setProductLobId(rs.getInt("PRODUCT_LOB_ID"));
				dto.setProductLobName(rs.getString("PRODUCT_LOB"));
				productList.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException("Exception occured while getting product list :  "+ e.getMessage(),e);
		} finally {
			try {
				//DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return productList;
	}
	//sud

	protected static final String SQL_SELECT_LEAD_LIST_TID ="SELECT LD.LEAD_ID AS LEAD_ID, PM.PRODUCT_NAME AS PRODUCT_NAME, LPC.CUSTOMER_NAME AS CUSTOMER_NAME,LPC.PROSPECT_MOBILE_NUMBER AS PROSPECT_MOBILE_NUMBER, CM.CIRCLE_DESC AS CIRCLE_DESC, varchar_format (LD.CREATE_TIME, 'YYYY-MM-DD HH:MI:SS AM') AS CREATE_TIME,(SELECT LEAD_STATUS_NAME AS LEAD_STATUS FROM  LEAD_STATUS WHERE LEAD_STATUS_ID = LD.LEAD_STATUS_ID),LD.PRODUCT_ID AS PRODUCT_ID,(SELECT PRODUCT_LOB_ID FROM PRODUCT_MSTR WHERE PRODUCT_ID=LD.PRODUCT_ID) FROM LEAD_DATA LD, LEAD_PROSPECT_CUSTOMER LPC , PRODUCT_MSTR PM, CIRCLE_MSTR CM ,LEAD_PROSPECT_DETAIL LPD,LEAD_DETAILS LDT WHERE LDT.LEAD_CAPTURED_DATA_ID =? AND LD.PROSPECT_ID = LPC.PROSPECT_ID AND LD.LEAD_PROSPECT_ID = LPD.LEAD_PROSPECT_ID AND LD.PRODUCT_ID = PM.PRODUCT_ID AND LPD.CIRCLE_ID = CM.CIRCLE_ID AND LPD.PRODUCT_LOB_ID=CM.LOB_ID AND LD.LEAD_ID=LDT.LEAD_ID   ";
	//protected static final String SQL_SELECT_LEAD_LIST_TID ="SELECT LD.LEAD_ID AS LEAD_ID, PM.PRODUCT_NAME AS PRODUCT_NAME, LPC.CUSTOMER_NAME AS CUSTOMER_NAME,LPC.PROSPECT_MOBILE_NUMBER AS PROSPECT_MOBILE_NUMBER, CM.CIRCLE_DESC AS CIRCLE_DESC, varchar_format (LD.CREATE_TIME, 'YYYY-MM-DD HH:MI:SS AM') AS CREATE_TIME,(SELECT LEAD_STATUS_NAME AS LEAD_STATUS FROM  LEAD_STATUS WHERE LEAD_STATUS_ID = LD.LEAD_STATUS_ID) FROM LEAD_DATA LD, LEAD_PROSPECT_CUSTOMER LPC , PRODUCT_MSTR PM, CIRCLE_MSTR CM ,LEAD_PROSPECT_DETAIL LPD WHERE LD.CID = ? AND LD.PROSPECT_ID = LPC.PROSPECT_ID AND LD.LEAD_PROSPECT_ID = LPD.LEAD_PROSPECT_ID AND LD.PRODUCT_ID = PM.PRODUCT_ID AND LPD.CIRCLE_ID = CM.CIRCLE_ID AND LPD.PRODUCT_LOB_ID=CM.LOB_ID   ";
	public ArrayList<LeadDetailsDTO> getLeadListByTid(String tid)
			throws DAOException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<LeadDetailsDTO> leadList = new ArrayList<LeadDetailsDTO>();
		LeadDetailsDTO dto = null;
		try {
			conn = DBConnection.getDBConnection();
			ps = conn.prepareStatement(SQL_SELECT_LEAD_LIST_TID);
			ps.setString(1, tid);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				dto = new LeadDetailsDTO();
				dto.setLeadId(rs.getLong("LEAD_ID"));
				dto.setProductName(rs.getString("PRODUCT_NAME"));
				dto.setCustomerName(rs.getString("CUSTOMER_NAME"));
				dto.setContactNo(rs.getLong("PROSPECT_MOBILE_NUMBER"));
				dto.setCircleName(rs.getString("CIRCLE_DESC"));
				dto.setOppertunityTime(rs.getString("CREATE_TIME"));
				dto.setLeadStatusName(rs.getString("LEAD_STATUS"));
				dto.setProductId(rs.getInt("PRODUCT_ID"));
				dto.setProductLobId(rs.getInt("PRODUCT_LOB_ID"));
				String prodata  =PropertyReader.getAppValue("4G.leaddata.Product");
				if(dto.getProductLobId()==Integer.parseInt(PropertyReader.getAppValue("lms.4G.productLobId")) && prodata.contains(String.valueOf(dto.getProductId())))
				{
				  dto.setProductflag("true");
				}
				else if(dto.getProductLobId()!=Integer.parseInt(PropertyReader.getAppValue("lms.4G.productLobId")))
				{
					dto.setProductflag("false");
				}
				
				else if(dto.getProductLobId()==Integer.parseInt(PropertyReader.getAppValue("lms.4G.productLobId")) && !prodata.contains(String.valueOf(dto.getProductId())))
					{
					  dto.setProductflag("4Gdifferent");
					}
				leadList.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException("Exception occured while getting product list :  "+ e.getMessage(),e);
		} finally {
			try {
				////DBConnection.releaseResources(conn, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return leadList;
	}
	
	private final static String GET_HISTORY="SELECT * FROM ((SELECT  '' As CUSTOMER_NAME,'' AS EMAIL,'' AS PROSPECT_MOBILE_NUMBER ,'' AS REQUEST_CATEGORY, LPDH.ADDRESS1,LPDH.APPOINTMENT_TIME,LPDH.APPOINMENT_ENDTIME FROM LEAD_PROSPECT_DETAIL_HIST LPDH WHERE LPDH.LEAD_PROSPECT_ID=(SELECT LEAD_PROSPECT_ID FROM LEAD_DATA LD WHERE  LD.LEAD_ID=?)) UNION ALL " +
	"(SELECT LPCH.CUSTOMER_NAME,LPCH.EMAIL,LPCH.PROSPECT_MOBILE_NUMBER ,'' AS REQUEST_CATEGORY, '' As ADDRESS1 ,'' AS APPOINTMENT_TIME,'' AS APPOINMENT_ENDTIME from LEAD_PROSPECT_CUSTOMER_HIST LPCH where LPCH.PROSPECT_ID=(SELECT LD.PROSPECT_ID FROM LEAD_DATA LD WHERE  LD.LEAD_ID=?) ) UNION ALL" +
	"(SELECT   '' As CUSTOMER_NAME,'' AS EMAIL,'' AS PROSPECT_MOBILE_NUMBER ,LDH.REQUEST_CATEGORY ,'' As ADDRESS1 ,'' AS APPOINTMENT_TIME,'' AS APPOINMENT_ENDTIME  FROM LEAD_DETAILS_HISTORY LDH, LEAD_DETAILS LDT,LEAD_DATA LD WHERE  LD.LEAD_ID=LDT.LEAD_ID AND LDT.LEAD_ID=LDH.LEAD_ID AND  LDH.LEAD_ID=? ) ) as temp WHERE CUSTOMER_NAME !='' or EMAIL !='' or PROSPECT_MOBILE_NUMBER !='' or  REQUEST_CATEGORY !='' or  ADDRESS1 !='' or   APPOINTMENT_TIME !='' or   APPOINMENT_ENDTIME !=''  ";

	public ArrayList<LeadDetailsDTO> getHistoryDetails(long leadId) throws DAOException 
	{
			
			Connection con = null;
			PreparedStatement ps1=null;
			ResultSet rs1=null;
				
			ArrayList<LeadDetailsDTO> leadHstDetailsList = new ArrayList<LeadDetailsDTO>();
			
			LeadDetailsDTO dto1=null;
		
		
			try{
				
				con = DBConnection.getDBConnection(); 
						StringBuffer query1= new StringBuffer(GET_HISTORY);
						ps1 = con.prepareStatement(query1.toString());
						ps1.setLong(1, leadId);
						ps1.setLong(2, leadId);
						ps1.setLong(3, leadId);
						
						rs1=ps1.executeQuery();
						while(rs1.next())
						{
							dto1=new LeadDetailsDTO();
							dto1.setCustomerName(rs1.getString("CUSTOMER_NAME"));
							String email=rs1.getString("EMAIL");
							try {
								if(email!=null && email.length() > 0 && !email.equalsIgnoreCase("null"))
								{
									dto1.setEmail(email);	
								}	
								else
								{
									dto1.setEmail("");
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							String mobileNumber=rs1.getString("PROSPECT_MOBILE_NUMBER");
							try {
								if(mobileNumber!=null && mobileNumber.length() > 0 && !mobileNumber.equalsIgnoreCase("null"))
								{
									dto1.setContact(mobileNumber);	
								}	
								else
								{
									dto1.setContact("");
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							dto1.setRequestCategory(rs1.getString("REQUEST_CATEGORY"));
							dto1.setAddress1(rs1.getString("ADDRESS1"));
							dto1.setAppointmentTime(rs1.getString("APPOINTMENT_TIME"));
							dto1.setAppointmentEndTime(rs1.getString("APPOINMENT_ENDTIME"));
							leadHstDetailsList.add(dto1);
					
			}
			}
		
			catch (Exception e) {
				e.printStackTrace();
				System.out.println("error"+e);
						throw new DAOException("Exception occured while getting history list :  "+ e.getMessage(),e);
					} finally 
					{
						try {
							////DBConnection.releaseResources(con, ps1, rs1);
							} catch (Exception e)
							{				
							throw new DAOException(e.getMessage(), e);
						}
					}
					return leadHstDetailsList;
				}
	
	public String update4GRecord(LeadDetailsDTO leadDetailsDTO) throws DAOException
    {
		 Connection con=null;
      
        PreparedStatement pstmt1=null;
        PreparedStatement pstmt2=null;
        PreparedStatement pstmt3=null;
        PreparedStatement psSubStatus =null;
		PreparedStatement psSubStatus1 =null;
		ResultSet rsSubStatus = null;
		String lead_Id=null;
        
        ResultSet rs=null;
        ResultSet rs1=null;
        String  updateStatus = "SUCCESS"+"#"+leadDetailsDTO.getLeadId();
        String infoFlag="";
        
        MasterService mstrService = new MasterServiceImpl();
        int i=0;
            
        try
        {   
        	
        	con = DBConnection.getDBConnection(); 
        	
						String UPDATE_LEAD_PROSPECT_CUSTOMER = "UPDATE LEAD_DATA SET PLAN=? WHERE LEAD_ID = ?  ";
						String UPDATE_LEAD_PROSPECT_DETAILS = "UPDATE LEAD_PROSPECT_DETAIL SET  CITY_ZONE_CODE=?, CITY_CODE=?, CIRCLE_ID=?, ADDRESS2=?, ADDRESS1=?,PINCODE=?, TRAN_REFNO=? ,PYT_AMT_DUE=? ,APPOINTMENT_TIME=?, APPOINMENT_ENDTIME=? WHERE LEAD_ID = ?   ";
						String UPDATE_LEAD_DETAILS="UPDATE LEAD_DETAILS SET EXTRA_PARAM6=? ,FEASIBILITY_PARAM=?,DEVICE_TAKEN=?,REQUEST_CATEGORY_ID=?,EXTRA_PARAM7=? WHERE LEAD_ID=?  ";
						String SELECT_EXISTING_LEAD="select * from LEAD_SCCHEDULE_DATA where LEAD_ID=?  ";
						String UPDATE_EXISTING_LEAD="update LEAD_SCCHEDULE_DATA set APPOINTMENT_STATUS=?,UPDATED_DT=current timestamp, REQUEST_CATEGORY_ID=?,SMSEMAIL_INFOFLAG=? where LEAD_ID=?  "; 
						String INSERT_LEAD_SCHEDULE="INSERT INTO LEAD_SCCHEDULE_DATA (LEAD_ID, APPOINTMENT_STATUS,CREATE_TIME,REQUEST_CATEGORY_ID,SMSEMAIL_INFOFLAG) VALUES(?,?,current timestamp,?,?)  ";
						
						psSubStatus1  = con.prepareStatement(SELECT_EXISTING_LEAD);
						psSubStatus1.setLong(1, leadDetailsDTO.getLeadId());
						rsSubStatus=psSubStatus1.executeQuery();
						if(rsSubStatus.next()) 
						{ 
							 infoFlag = rsSubStatus.getString("SMSEMAIL_INFOFLAG");
							lead_Id=rsSubStatus.getString("LEAD_ID");
						
						psSubStatus  = con.prepareStatement(UPDATE_EXISTING_LEAD);
						psSubStatus.setString(1, leadDetailsDTO.getAppointmentStatus());
						
						
						if((leadDetailsDTO.getRequestCategory()!= null && (leadDetailsDTO.getRequestCategory().length()>0) || (!leadDetailsDTO.getRequestCategory().equalsIgnoreCase("") && leadDetailsDTO.getRequestCategory().length()>0)))
						{
							psSubStatus.setInt(2, Integer.parseInt(leadDetailsDTO.getRequestCategory()));
							psSubStatus.setString(3, String.valueOf(Constants.smsInfoFlag));
						}	
						else
						{
							psSubStatus.setInt(2,Constants.emptyRC );
							psSubStatus.setString(3, infoFlag);
						}
						
						psSubStatus.setString(4, lead_Id);
						psSubStatus.executeUpdate();
						}
						
						else
						{
							psSubStatus  = con.prepareStatement(INSERT_LEAD_SCHEDULE);
							psSubStatus.setLong(1, leadDetailsDTO.getLeadId());
							psSubStatus.setString(2, leadDetailsDTO.getAppointmentStatus());
							
							if(leadDetailsDTO.getRequestCategory()== null && leadDetailsDTO.getRequestCategory().length()<0)
							{
								psSubStatus.setInt(3, Constants.emptyRC);
								psSubStatus.setInt(4, Constants.emptyRC);
							}
							else
							{
								psSubStatus.setString(3, leadDetailsDTO.getRequestCategory());
								psSubStatus.setInt(4, Constants.smsInfoFlag);
							}
							
							
							psSubStatus.executeUpdate();
							
						}
					
						
						pstmt1 = con.prepareStatement(UPDATE_LEAD_PROSPECT_CUSTOMER);
						pstmt2 = con.prepareStatement(UPDATE_LEAD_PROSPECT_DETAILS);
						pstmt3 = con.prepareStatement(UPDATE_LEAD_DETAILS);
					
						pstmt1.setString(1,leadDetailsDTO.getPlan());
						pstmt1.setLong(2, leadDetailsDTO.getLeadId());
						
						pstmt2.setString(1, leadDetailsDTO.getCityZoneCode());
						pstmt2.setString(2, leadDetailsDTO.getCityCode());
						pstmt2.setInt(3, leadDetailsDTO.getCircleId());
						pstmt2.setString(4, leadDetailsDTO.getAddress2());
						pstmt2.setString(5, leadDetailsDTO.getAddress1());
						pstmt2.setInt(6, Integer.parseInt(leadDetailsDTO.getPinCode()));
			            pstmt2.setString(7, leadDetailsDTO.getPayment());
						pstmt2.setString(8, leadDetailsDTO.getTransactionRefNo());
						
						pstmt2.setString(9,leadDetailsDTO.getAppointmentTime());
						pstmt2.setString(10,leadDetailsDTO.getAppointmentEndTime());
						pstmt2.setLong(11, leadDetailsDTO.getLeadId());
						
						
						pstmt3.setString(1, leadDetailsDTO.getExtraParam6());
						pstmt3.setString(3, leadDetailsDTO.getDevicetaken());
						pstmt3.setString(2, leadDetailsDTO.getFeasibilityParam());
						
						if((leadDetailsDTO.getRequestCategory()!= null && (leadDetailsDTO.getRequestCategory().length()>0) || (leadDetailsDTO.getRequestCategory().equalsIgnoreCase("") && leadDetailsDTO.getRequestCategory().length()>0)))
							
						{
							pstmt3.setInt(4, Integer.parseInt(leadDetailsDTO.getRequestCategory()));
						}
						else
						{
							pstmt3.setInt(4,Constants.emptyRC  );
						}
						
						if(leadDetailsDTO.getFeasibilityParam() !=null && leadDetailsDTO.getFeasibilityParam().equalsIgnoreCase("Y"))
						{
							leadDetailsDTO.setFeasibilityParam("Y");
							pstmt3.setString(2, leadDetailsDTO.getFeasibilityParam());
						}
						else
						{
							leadDetailsDTO.setFeasibilityParam("N");
							pstmt3.setString(2, leadDetailsDTO.getFeasibilityParam());	
						}
						
						pstmt3.setString(5, leadDetailsDTO.getExtraParam7());
						
						pstmt3.setLong(6, leadDetailsDTO.getLeadId());
						
						i=pstmt1.executeUpdate();
						
						
						pstmt2.executeUpdate();
			
						pstmt3.executeUpdate();
						
        }
					catch (Exception e) {
						//con.rollback();
						e.printStackTrace();
						updateStatus="UPDATE_FAILURE"+"#"+ leadDetailsDTO.getLeadId();
					}
			
        
        finally{
		        try
		        {
		        	con.setAutoCommit(true);
		            //DBConnection.releaseResources(con, pstmt1, null);
		            //DBConnection.releaseResources(con, pstmt2, null);
		            //DBConnection.releaseResources(con, pstmt3, null);
		            
		        }
		        catch(Exception e)
		        {
		            e.printStackTrace();
		            throw new DAOException(e.getMessage(), e);
		        }
        }
        
        return updateStatus;      
    }
/*
 * 
 * 
 * Added by naresh chander 
 * (non-Javadoc)
 * @see com.ibm.lms.dao.LeadRegistrationDao#getChannelPartnerFlag(long)
 */
	String SQL_SELECT_COUNT_LEAD_TRANSACTION="select count(*) as COUNT from LEAD_TRANSACTION where  TRANSACTION_TIME=(select  max(TRANSACTION_TIME) from LEAD_TRANSACTION where LEAD_ID=? and LEAD_ASSIGNED_PRIMARY_USER=?)";
	
	public Boolean getChannelPartnerFlag(long leadId,String loginId) throws DAOException {
		// TODO Auto-generated method stub
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Boolean flag=false;
		
		try {
			conn = DBConnection.getDBConnection();
			ps = conn.prepareStatement(SQL_SELECT_COUNT_LEAD_TRANSACTION);
			ps.setLong(1, leadId);
			ps.setString(2, loginId);
			rs = ps.executeQuery();
			while(rs.next())
			{
			String count=rs.getString("COUNT");
			int countFlag = Integer.parseInt(count);
			System.out.println("**********countFlag"+countFlag);
			if(countFlag>0){
				flag=true;	
			}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	return flag;
	}
	
	
	///added by Pernica for postpaid lead search
	
	//String SQL_SELECT_LEAD_POSTPAID="SELECT	LD.LEAD_ID AS LEAD_ID,LD.PRODUCT_ID AS PRODUCT_ID,LUD.IDENTITYPROOFID AS IDENTITY_PROOF_ID, LUD.RELATION_NAME AS RELATION_NAME, LUD.UPC AS UPC, LUD.UPC_GEN_DATE AS UPC_GEN_DATE, LUD.PREVIOUS_OPERATOR AS PREVIOUS_OPERATOR,LUD.PREVIOUS_CIRCLE AS PREVIOUS_CIRCLE, LUD.EXISTING_PART AS EXISTING_PART, LUD.MNP_STATUS AS MNP_STATUS, LUD.DOC_COLLECTED_FLAG, LUD.PLAN_TYPE,LDE.EXTRA_PARAM1 AS CUSTOMER_SEGMENT, LDE.EXTRA_PARAM2 AS SIM_NO, LDE.EXTRA_PARAM5 AS GENDER, LDE.EXTRA_PARAM7 AS NATIONALITY,LDE.EXTRA_PARAM8 AS IDENTITY_PROOF_TYPE FROM LEAD_DATA LD, LEAD_UPDATE_DATA LUD, LEAD_DETAILS LDE WHERE LD.LEAD_ID = LUD.LEAD_ID AND   LD.LEAD_ID = LDE.LEAD_ID AND LD.LEAD_ID = ?   ";
String SQL_SELECT_LEAD_POSTPAID="SELECT	LD.LEAD_ID AS LEAD_ID,LD.PRODUCT_ID AS PRODUCT_ID,LUD.IDENTITYPROOFID AS IDENTITY_PROOF_ID, LUD.RELATION_NAME AS RELATION_NAME, LUD.UPC AS UPC, LUD.UPC_GEN_DATE AS UPC_GEN_DATE, LUD.PREVIOUS_OPERATOR AS PREVIOUS_OPERATOR,LUD.PREVIOUS_CIRCLE AS PREVIOUS_CIRCLE, LUD.EXISTING_PART AS EXISTING_PART, LUD.MNP_STATUS AS MNP_STATUS, LUD.DOC_COLLECTED_FLAG, LUD.PLAN_TYPE,LDE.EXTRA_PARAM1 AS SIM_NO, LDE.EXTRA_PARAM2 AS CUSTOMER_SEGMENT, LDE.EXTRA_PARAM5 AS NATIONALITY, LDE.EXTRA_PARAM7 AS GENDER,LDE.EXTRA_PARAM8 AS IDENTITY_PROOF_TYPE FROM LEAD_DATA LD, LEAD_UPDATE_DATA LUD, LEAD_DETAILS LDE WHERE LD.LEAD_ID = LUD.LEAD_ID AND   LD.LEAD_ID = LDE.LEAD_ID AND LD.LEAD_ID = ?   ";
	
	public  ArrayList<LeadDetailsDTO> getLeadListByLeadIdAndProductID(Long leadId,ArrayList<LeadDetailsDTO> list) throws DAOException
	{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<LeadDetailsDTO> leadList = list;
		LeadDetailsDTO dto = null;
		try {
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(SQL_SELECT_LEAD_POSTPAID);
			ps.setLong(1, leadId);
			rs = ps.executeQuery();
			 dto  = list.size() >0 ? list.get(0) : null;
			if(rs.next() && dto !=null) {
				dto.setIdentityProofId(rs.getString("IDENTITY_PROOF_ID"));
				dto.setRelationName(rs.getString("RELATION_NAME"));
				dto.setUpc(rs.getString("UPC"));
				dto.setUpcGenDate(rs.getString("UPC_GEN_DATE"));
				dto.setPreviousOperator(rs.getString("PREVIOUS_OPERATOR"));
				dto.setPreviousCircle(rs.getString("PREVIOUS_CIRCLE"));
				dto.setExistingPart(rs.getString("EXISTING_PART"));
				dto.setMnpStatus(rs.getString("MNP_STATUS"));
				dto.setDocumentCollectedFlag(rs.getString("DOC_COLLECTED_FLAG"));
				dto.setPlanType(rs.getString("PLAN_TYPE"));
				dto.setCustomerSegment(rs.getString("CUSTOMER_SEGMENT"));
				dto.setSimNumber(rs.getString("SIM_NO"));
				dto.setGender(rs.getString("GENDER"));
				dto.setNationality(rs.getString("NATIONALITY"));
				dto.setIdentityProofType(rs.getString("IDENTITY_PROOF_TYPE"));
				//leadList.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException("Exception occured while getting product list :  "+ e.getMessage(),e);
		} finally {
			try {
				////DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return leadList;
	}

}
