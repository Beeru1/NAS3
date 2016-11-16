package com.ibm.nas.dao.impl;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.ibm.nas.common.Constants;
import com.ibm.nas.common.DBConnection;
import com.ibm.nas.common.LMSStatusCodes;
import com.ibm.nas.common.PropertyReader;
import com.ibm.nas.common.Utility;
import com.ibm.nas.dao.AssignedLeadsDAO;
import com.ibm.nas.dao.LeadRegistrationDao;
import com.ibm.nas.dto.LeadDetailsDTO;
import com.ibm.nas.dto.Leads;
import com.ibm.nas.dto.UserDto;
import com.ibm.nas.exception.DAOException;
import com.ibm.nas.exception.LMSException;
import com.ibm.nas.forms.LeadForm;
import com.ibm.nas.wf.dto.BulkFeasibilityDTO;
import com.ibm.nas.wf.dto.Constant;
import com.ibm.nas.wf.dto.LeadDetailDTO;

public class AssignedLeadsDAOImpl implements AssignedLeadsDAO {

	private static Logger logger =
		Logger.getLogger(AssignedLeadsDAOImpl.class);

	public static final String SQL_SELECT_ASSIGNED_LEADS ="SELECT LEAD.LEAD_ID,LPD.PINCODE, LEAD.PRODUCT_ID, LEAD.LEAD_ASSIGNED_PRIMARY_USER,LEAD_ASSIGNMENT_TIME,EXPECTED_CLOSURE_DATE,PRO.PRODUCT_NAME , (select ls.SUB_STATUS  from LEAD_SUB_STATUS ls, LEAD_TRANSACTION tr where ls.SUB_STATUS_ID = tr.SUB_STATUS_ID and ls.PRODUCT_LOB_ID = tr.PRODUCT_ID  AND tr.LEAD_ID = lead.LEAD_ID AND tr.LEAD_STATUS_ID =DATA.LEAD_STATUS_ID and tr.TRANSACTION_TIME = (select max(TRANSACTION_TIME) FROM LEAD_TRANSACTION t where  t.LEAD_ID = lead.LEAD_ID AND t.LEAD_STATUS_ID =DATA.LEAD_STATUS_ID AND t.SUB_STATUS_ID NOT IN ("+LMSStatusCodes.FOLLOW_UP_DIALER_CALL+"))),(select lss.SUB_SUB_STATUS from LEAD_SUB_SUB_STATUS lss , LEAD_TRANSACTION tr,LEAD_SUB_STATUS LS where LS.UNIQUE_ID=(select  ls.UNIQUE_ID  from LEAD_SUB_STATUS ls, LEAD_TRANSACTION tr where ls.SUB_STATUS_ID = tr.SUB_STATUS_ID and ls.PRODUCT_LOB_ID = tr.PRODUCT_ID  AND tr.LEAD_ID = lead.LEAD_ID AND tr.LEAD_STATUS_ID =DATA.LEAD_STATUS_ID and tr.TRANSACTION_TIME = (select max(TRANSACTION_TIME) FROM LEAD_TRANSACTION t where  t.LEAD_ID = lead.LEAD_ID AND t.LEAD_STATUS_ID =DATA.LEAD_STATUS_ID AND t.SUB_STATUS_ID NOT IN (380))) AND ls.UNIQUE_ID=lss.SUBSTATUS_CODE and   lss.SUB_SUB_STATUS_ID= tr.LEAD_SUB_SUB_STATUS_ID   AND tr.LEAD_ID = lead.LEAD_ID AND tr.LEAD_STATUS_ID =DATA.LEAD_STATUS_ID and tr.TRANSACTION_TIME = (select max(TRANSACTION_TIME) FROM LEAD_TRANSACTION t where  t.LEAD_ID = lead.LEAD_ID AND t.LEAD_STATUS_ID =DATA.LEAD_STATUS_ID AND t.SUB_STATUS_ID NOT IN ("+LMSStatusCodes.FOLLOW_UP_DIALER_CALL+")))  " +
			"FROM LEAD_TRANSACTION LEAD,PRODUCT_MSTR PRO,LEAD_DATA DATA,LEAD_PROSPECT_DETAIL LPD WHERE LEAD.LEAD_STATUS_ID IN ( "+LMSStatusCodes.ASSIGNED+","+LMSStatusCodes.REASSIGNED+" ) AND LEAD.SUB_STATUS_ID NOT IN ("+LMSStatusCodes.FOLLOW_UP_DIALER_CALL+") AND DATA.LEAD_STATUS_ID = LEAD.LEAD_STATUS_ID AND LEAD.LEAD_ID=DATA.LEAD_ID AND PRO.PRODUCT_ID = DATA.PRODUCT_ID AND LEAD.SUB_STATUS_ID <> "+LMSStatusCodes.DIALER_CON_FAIL +" " +
			" and LEAD.TRANSACTION_TIME = (select max(TRANSACTION_TIME) FROM LEAD_TRANSACTION tr where  tr.LEAD_ID = lead.LEAD_ID AND tr.LEAD_STATUS_ID =DATA.LEAD_STATUS_ID AND tr.SUB_STATUS_ID NOT IN ("+LMSStatusCodes.FOLLOW_UP_DIALER_CALL+")) AND DATA.LEAD_ID=LPD.LEAD_ID AND ";
	
	public static final String SQL_SELECT_ASSIGNED_LEADS1 = "SELECT LEAD.LEAD_ID , LPD.PINCODE,LEAD.PRODUCT_ID, LEAD.LEAD_ASSIGNED_PRIMARY_USER,LEAD_ASSIGNMENT_TIME,EXPECTED_CLOSURE_DATE,PRO.PRODUCT_NAME FROM LEAD_TRANSACTION LEAD,PRODUCT_MSTR PRO,LEAD_DATA DATA,LEAD_PROSPECT_DETAIL LPD WHERE LEAD.LEAD_STATUS_ID IN("+LMSStatusCodes.ASSIGNED+","+LMSStatusCodes.REASSIGNED+") AND DATA.LEAD_STATUS_ID = LEAD.LEAD_STATUS_ID AND LEAD.LEAD_ID=DATA.LEAD_ID AND PRO.PRODUCT_ID = DATA.PRODUCT_ID AND LEAD.SUB_STATUS_ID <> "+LMSStatusCodes.DIALER_CON_FAIL +" AND LEAD.SUB_STATUS_ID IN ("+LMSStatusCodes.REASSIGNED+") AND " +
							" LEAD.TRANSACTION_TIME = (select max(TRANSACTION_TIME) FROM LEAD_TRANSACTION tr where  tr.LEAD_ID = lead.LEAD_ID) AND LPD.LEAD_ID=DATA.LEAD_ID AND " ;
	//public static final String SQL_SELECT_ASSIGNED_LEADS2 ="SELECT LEAD.LEAD_ID , LEAD.PRODUCT_ID, LEAD.LEAD_ASSIGNED_PRIMARY_USER,LEAD_ASSIGNMENT_TIME,EXPECTED_CLOSURE_DATE,PRO.PRODUCT_NAME FROM LEAD_TRANSACTION LEAD,PRODUCT_MSTR PRO,LEAD_DATA DATA WHERE LEAD.LEAD_STATUS_ID IN("+LMSStatusCodes.ASSIGNED+","+LMSStatusCodes.REASSIGNED+") AND DATA.LEAD_STATUS_ID = LEAD.LEAD_STATUS_ID AND LEAD.LEAD_ID=DATA.LEAD_ID AND PRO.PRODUCT_ID = DATA.PRODUCT_ID AND LEAD.SUB_STATUS_ID <> "+LMSStatusCodes.DIALER_CON_FAIL +" AND LEAD.SUB_STATUS_ID IN ("+LMSStatusCodes.FOLLOW_UP_DIALER_CALL+") AND " +
	//						" TRANSACTION_TIME = (select max(TRANSACTION_TIME) FROM LEAD_TRANSACTION tr where  tr.LEAD_ID = lead.LEAD_ID AND tr.sub_status_id = "+LMSStatusCodes.FOLLOW_UP_DIALER_CALL+")  AND ";
	//changed by gaurav bhasin
	public static final String SQL_SELECT_ASSIGNED_LEADS2 ="SELECT LEAD.LEAD_ID , LEAD.PRODUCT_ID, LEAD.LEAD_ASSIGNED_PRIMARY_USER,LEAD_ASSIGNMENT_TIME,EXPECTED_CLOSURE_DATE,PRO.PRODUCT_NAME FROM LEAD_TRANSACTION LEAD,PRODUCT_MSTR PRO,LEAD_DATA DATA,LEAD_PROSPECT_DETAIL LPD WHERE LEAD.LEAD_STATUS_ID IN("+LMSStatusCodes.ASSIGNED+","+LMSStatusCodes.REASSIGNED+") AND DATA.LEAD_STATUS_ID = LEAD.LEAD_STATUS_ID AND LEAD.LEAD_ID=DATA.LEAD_ID AND PRO.PRODUCT_ID = DATA.PRODUCT_ID AND LEAD.SUB_STATUS_ID <> "+LMSStatusCodes.DIALER_CON_FAIL +" AND LEAD.SUB_STATUS_ID IN ("+LMSStatusCodes.FOLLOW_UP_DIALER_CALL+") AND " +
	" LEAD.TRANSACTION_TIME = (select max(TRANSACTION_TIME) FROM LEAD_TRANSACTION tr where  tr.LEAD_ID = lead.LEAD_ID) AND LPD.LEAD_ID = DATA.LEAD_ID AND ";
	
	
	
public static final String SQL_SELECT_VIEW_ASSIGNED_LEAD = "SELECT DISTINCT (D.LEAD_ID), D.TRANSACTION_ID, D.PRODUCT_ID,D.LEAD_ASSIGNMENT_TIME, D.LEAD_CLOSURE_TIME, D.LEAD_STATUS_ID, D.LEAD_ASSIGNED_PRIMARY_USER, D.REMARKS, D.TRANSACTION_TIME, D.EXPECTED_CLOSURE_DATE, D.PRIMARY_AUTH, D.SUB_STATUS_ID, D.UPDATED_BY, D.UD_ID, D.LEAD_PRODUCT_ID, D.CLIENT_IP, D.SUB_STATUS_ID ,(SELECT PRODUCT_NAME  FROM PRODUCT_MSTR where c.PRODUCT_ID=PRODUCT_ID) as PRODUCT_NAME	FROM ASSIGNMENT_MATRIX A,LEAD_PROSPECT_CUSTOMER B,LEAD_DATA C,LEAD_TRANSACTION D,LEAD_PROSPECT_DETAIL LPD,LEAD_DETAILS N, " +
" (select max(TRANSACTION_TIME) as TRANSACTION_TIME ,lead_id from LEAD_TRANSACTION TR where TR.lead_id = lead_id and LEAD_STATUS_ID IN("+LMSStatusCodes.ASSIGNED+","+LMSStatusCodes.REASSIGNED+")  group by lead_id) maxT " +
"where 	A.OLM_ID =? AND	A.PRIMARY_AUTH=0 and A.status='A' AND A.CIRCLE_ID= LPD.CIRCLE_ID AND B.PROSPECT_ID=C.PROSPECT_ID AND LPD.LEAD_PROSPECT_ID = c.LEAD_PROSPECT_ID and A.PRODUCT_LOB_ID = LPD.PRODUCT_LOB_ID AND C.LEAD_STATUS_ID IN ("+LMSStatusCodes.ASSIGNED+","+LMSStatusCodes.REASSIGNED+") AND " +
"C.LEAD_ID=D.LEAD_ID AND C.LEAD_ID=N.LEAD_ID AND D.LEAD_STATUS_ID IN("+LMSStatusCodes.ASSIGNED+","+LMSStatusCodes.REASSIGNED+") and c.LEAD_STATUS_ID=d.LEAD_STATUS_ID and (D.TRANSACTION_TIME) = (maxT.TRANSACTION_TIME) ";

	

public static final String SQL_SELECT_ASSIGNED_LEADS_ESCALATION ="SELECT LEAD.LEAD_ID, LEAD.PRODUCT_ID, LEAD.LEAD_ASSIGNED_PRIMARY_USER,LEAD_ASSIGNMENT_TIME,EXPECTED_CLOSURE_DATE,PRO.PRODUCT_NAME , "+
"(select SUB_STATUS from  LEAD_SUB_STATUS ls , LEAD_TRANSACTION tr where ls.SUB_STATUS_ID = tr.SUB_STATUS_ID and ls.PRODUCT_LOB_ID = tr.PRODUCT_ID AND tr.LEAD_ID = lead.LEAD_ID AND tr.TRANSACTION_TIME ="+
 "(select max(TRANSACTION_TIME) FROM LEAD_TRANSACTION t where t.LEAD_ID = lead.LEAD_ID )) FROM LEAD_TRANSACTION LEAD,PRODUCT_MSTR PRO,LEAD_DETAILS N,LEAD_DATA DATA WHERE LEAD.LEAD_STATUS_ID IN ("+LMSStatusCodes.ESCALATION+") AND LEAD.SUB_STATUS_ID IN ("+LMSStatusCodes.ESCALATION1+","+LMSStatusCodes.ESCALATION2+","+LMSStatusCodes.ESCALATION3+","+LMSStatusCodes.ESCALATION4+") AND LEAD.LEAD_ASSIGNED_PRIMARY_USER = ? AND LEAD.LEAD_ID=DATA.LEAD_ID and LEAD.LEAD_ID=N.LEAD_ID "+
"AND PRO.PRODUCT_ID = DATA.PRODUCT_ID AND DATA.LEAD_STATUS_ID IN (" + LMSStatusCodes.ASSIGNED + " ," + LMSStatusCodes.REASSIGNED +" )";

public static final String SQL_SELECT_ASSIGNED_LEADS_FEASIBILITY ="SELECT LEAD.LEAD_ID, LEAD.PRODUCT_ID, LEAD.LEAD_ASSIGNED_PRIMARY_USER,LEAD_ASSIGNMENT_TIME,EXPECTED_CLOSURE_DATE,PRO.PRODUCT_NAME ,"+
"(select SUB_STATUS from  LEAD_SUB_STATUS ls , LEAD_TRANSACTION tr where ls.SUB_STATUS_ID = tr.SUB_STATUS_ID and ls.PRODUCT_LOB_ID = tr.PRODUCT_ID AND tr.LEAD_ID = lead.LEAD_ID and tr.TRANSACTION_TIME ="+
"(select max(TRANSACTION_TIME) FROM LEAD_TRANSACTION t where  t.LEAD_ID = lead.LEAD_ID )) FROM LEAD_TRANSACTION LEAD,LEAD_DETAILS N,PRODUCT_MSTR PRO,LEAD_DATA DATA WHERE LEAD.LEAD_STATUS_ID IN ( "+LMSStatusCodes.FEASIBILITY5+") AND LEAD.SUB_STATUS_ID IN ( "+LMSStatusCodes.FEASIBILITY1+","+LMSStatusCodes.FEASIBILITY2+","+LMSStatusCodes.FEASIBILITY3+","+LMSStatusCodes.FEASIBILITY4+")  AND LEAD.LEAD_ASSIGNED_PRIMARY_USER = ? AND LEAD.LEAD_ID=DATA.LEAD_ID and LEAD.LEAD_ID=N.LEAD_ID " +
		"AND PRO.PRODUCT_ID = DATA.PRODUCT_ID  AND DATA.LEAD_STATUS_ID IN (" + LMSStatusCodes.FEASIBILITY + " ) ";




public static final String SQL_ASSIGNED_CHANNEL_LEADS = "SELECT LCT.LEAD_ID,LPD.PINCODE,LEAD.PRODUCT_ID, LCT.LEAD_ASSIGNED_CHANNEL_USER,LCT.TRANSACTION_TIME,EXPECTED_CLOSURE_DATE,PRO.PRODUCT_NAME , (select ls.SUB_STATUS  from LEAD_SUB_STATUS ls, LEAD_TRANSACTION tr where ls.SUB_STATUS_ID = tr.SUB_STATUS_ID and ls.PRODUCT_LOB_ID = tr.PRODUCT_ID  AND tr.LEAD_ID = lead.LEAD_ID AND tr.LEAD_STATUS_ID =DATA.LEAD_STATUS_ID and tr.TRANSACTION_TIME = (select max(TRANSACTION_TIME) FROM LEAD_TRANSACTION t where  t.LEAD_ID = lead.LEAD_ID AND t.LEAD_STATUS_ID =DATA.LEAD_STATUS_ID AND t.SUB_STATUS_ID NOT IN ("+LMSStatusCodes.FOLLOW_UP_DIALER_CALL+"))),(select lss.SUB_SUB_STATUS from LEAD_SUB_SUB_STATUS lss , LEAD_TRANSACTION tr,LEAD_SUB_STATUS LS where LS.UNIQUE_ID=(select  ls.UNIQUE_ID  from LEAD_SUB_STATUS ls, LEAD_TRANSACTION tr where ls.SUB_STATUS_ID = tr.SUB_STATUS_ID and ls.PRODUCT_LOB_ID = tr.PRODUCT_ID  AND tr.LEAD_ID = lead.LEAD_ID AND tr.LEAD_STATUS_ID =DATA.LEAD_STATUS_ID and tr.TRANSACTION_TIME = (select max(TRANSACTION_TIME) FROM LEAD_TRANSACTION t where  t.LEAD_ID = lead.LEAD_ID AND t.LEAD_STATUS_ID =DATA.LEAD_STATUS_ID AND t.SUB_STATUS_ID NOT IN (380))) AND ls.UNIQUE_ID=lss.SUBSTATUS_CODE and   lss.SUB_SUB_STATUS_ID= tr.LEAD_SUB_SUB_STATUS_ID   AND tr.LEAD_ID = lead.LEAD_ID AND tr.LEAD_STATUS_ID =DATA.LEAD_STATUS_ID and tr.TRANSACTION_TIME = (select max(TRANSACTION_TIME) FROM LEAD_TRANSACTION t where  t.LEAD_ID = lead.LEAD_ID AND t.LEAD_STATUS_ID =DATA.LEAD_STATUS_ID AND t.SUB_STATUS_ID NOT IN ("+LMSStatusCodes.FOLLOW_UP_DIALER_CALL+")))  " +
		"FROM LEAD_TRANSACTION LEAD,PRODUCT_MSTR PRO,LEAD_DATA DATA,LEAD_CHANNEL_TRANSACTION LCT,LEAD_PROSPECT_DETAIL LPD WHERE LEAD.LEAD_STATUS_ID IN ( "+LMSStatusCodes.ASSIGNED+","+LMSStatusCodes.REASSIGNED+" ) AND LEAD.SUB_STATUS_ID NOT IN ("+LMSStatusCodes.FOLLOW_UP_DIALER_CALL+") AND DATA.LEAD_STATUS_ID = LEAD.LEAD_STATUS_ID AND LEAD.LEAD_ID=DATA.LEAD_ID AND PRO.PRODUCT_ID = DATA.PRODUCT_ID AND LEAD.SUB_STATUS_ID <> "+LMSStatusCodes.DIALER_CON_FAIL +" " +
		" and LEAD.TRANSACTION_TIME = (select max(TRANSACTION_TIME) FROM LEAD_TRANSACTION tr where  tr.LEAD_ID = lead.LEAD_ID AND tr.LEAD_STATUS_ID =DATA.LEAD_STATUS_ID AND tr.SUB_STATUS_ID NOT IN ("+LMSStatusCodes.FOLLOW_UP_DIALER_CALL+")) AND LCT.LEAD_ID=DATA.LEAD_ID AND DATA.PRODUCT_ID=LCT.PRODUCT_ID AND LCT.PRODUCT_ID = DATA.PRODUCT_ID AND LPD.LEAD_ID=DATA.LEAD_ID AND ";

		
	//public static final String SQL_SELECT_ASSIGNED_LEADS2 ="SELECT LEAD.LEAD_ID , LEAD.PRODUCT_ID, LEAD.LEAD_ASSIGNED_PRIMARY_USER,LEAD_ASSIGNMENT_TIME,EXPECTED_CLOSURE_DATE,PRO.PRODUCT_NAME FROM LEAD_TRANSACTION LEAD,PRODUCT_MSTR PRO,LEAD_DATA DATA WHERE LEAD.LEAD_STATUS_ID IN("+LMSStatusCodes.ASSIGNED+","+LMSStatusCodes.REASSIGNED+") AND DATA.LEAD_STATUS_ID = LEAD.LEAD_STATUS_ID AND LEAD.LEAD_ID=DATA.LEAD_ID AND PRO.PRODUCT_ID = DATA.PRODUCT_ID AND LEAD.SUB_STATUS_ID <> "+LMSStatusCodes.DIALER_CON_FAIL +" AND LEAD.SUB_STATUS_ID IN ("+LMSStatusCodes.FOLLOW_UP_DIALER_CALL+") AND " +
//							" TRANSACTION_TIME = (select max(TRANSACTION_TIME) FROM LEAD_TRANSACTION tr where  tr.LEAD_ID = lead.LEAD_ID AND tr.sub_status_id = "+LMSStatusCodes.FOLLOW_UP_DIALER_CALL+")  AND ";
	//changed by gaurav bhasin


		
		public static final String SQL_SELECT_CHANNEL_LEADS1 = "SELECT LCT.LEAD_ID ,LPD.PINCODE,LEAD.PRODUCT_ID, LCT.LEAD_ASSIGNED_CHANNEL_USER,LCT.TRANSACTION_TIME,EXPECTED_CLOSURE_DATE,PRO.PRODUCT_NAME " +
			"FROM LEAD_TRANSACTION LEAD,PRODUCT_MSTR PRO,LEAD_DATA DATA,LEAD_CHANNEL_TRANSACTION LCT,LEAD_PROSPECT_DETAIL LPD WHERE LEAD.LEAD_STATUS_ID IN(400,401) AND " +
			"DATA.LEAD_STATUS_ID = LEAD.LEAD_STATUS_ID AND LEAD.LEAD_ID=DATA.LEAD_ID AND PRO.PRODUCT_ID = DATA.PRODUCT_ID AND LEAD.SUB_STATUS_ID <> 210 AND LEAD.SUB_STATUS_ID IN (401) AND  LEAD.TRANSACTION_TIME = (select max(TRANSACTION_TIME) " +
			"FROM LEAD_TRANSACTION tr where  tr.LEAD_ID = lead.LEAD_ID) AND  LCT.LEAD_ID=DATA.LEAD_ID AND LCT.PRODUCT_ID = DATA.PRODUCT_ID AND LPD.LEAD_ID=DATA.LEAD_ID AND ";


	 public static final String SQL_ASSIGNED_CHANNEL_LEADS2 = "SELECT LCT.LEAD_ID ,LPD.PINCODE,LEAD.PRODUCT_ID, LCT.LEAD_ASSIGNED_CHANNEL_USER,LCT.TRANSACTION_TIME,EXPECTED_CLOSURE_DATE,PRO.PRODUCT_NAME FROM " +
			"LEAD_TRANSACTION LEAD,PRODUCT_MSTR PRO,LEAD_DATA DATA,LEAD_CHANNEL_TRANSACTION LCT,LEAD_PROSPECT_DETAIL LPD WHERE LEAD.LEAD_STATUS_ID IN(400,401) AND DATA.LEAD_STATUS_ID = LEAD.LEAD_STATUS_ID " +
			"AND LEAD.LEAD_ID=DATA.LEAD_ID AND PRO.PRODUCT_ID = DATA.PRODUCT_ID AND LEAD.SUB_STATUS_ID <> 210 AND LEAD.SUB_STATUS_ID IN (380) AND  " +
			"LEAD.TRANSACTION_TIME = (select max(TRANSACTION_TIME) FROM LEAD_TRANSACTION tr where  tr.LEAD_ID = lead.LEAD_ID) AND LCT.LEAD_ID=DATA.LEAD_ID AND LCT.PRODUCT_ID = DATA.PRODUCT_ID AND LPD.LEAD_ID=DATA.LEAD_ID AND ";
	 








public static final String SQL_SELECT_ACTION = "SELECT LEAD_STATUS_ID, LEAD_STATUS, LEAD_STATUS_NAME  FROM LEAD_STATUS WHERE ";

public static final String SQL_USERS_LEAD_FORWARD ="SELECT KA.KM_ACTOR_NAME,USER_ID, UM.USER_LOGIN_ID, USER_FNAME, USER_MNAME, USER_LNAME  FROM USER_MSTR UM, KM_ACTORS KA,CIRCLE_MSTR CM, USER_MAPPING UMAP  " +
		" WHERE UM.KM_ACTOR_ID in (" +Constants.ZONAL_COORDINATOR_ACTOR+ " , " +Constants.CHANNEL_PARTNER_ACTOR+  ")  AND um.USER_LOGIN_ID = UMAP.USER_LOGIN_ID  AND UMAP.CIRCLE_ID = CM.CIRCLE_ID  AND UMAP.LOB_ID = CM.LOB_ID  and UM.KM_ACTOR_ID = KA.KM_ACTOR_ID";


public static final String SQL_UPDATE_LEAD_STATUS = "UPDATE LEAD_DATA ";


public static final String SQL_INSERT_LEAD_TRANSACTION = "INSERT INTO LEAD_TRANSACTION (LEAD_ID, PRODUCT_ID, LEAD_ASSIGNMENT_TIME, LEAD_CLOSURE_TIME, LEAD_STATUS_ID,LEAD_SUB_SUB_STATUS_ID, LEAD_ASSIGNED_PRIMARY_USER, REMARKS, TRANSACTION_TIME, EXPECTED_CLOSURE_DATE, PRIMARY_AUTH, SUB_STATUS_ID , UPDATED_BY, UD_ID,LEAD_PRODUCT_ID,CLIENT_IP)  SELECT LEAD_ID,PRODUCT_ID,LEAD_ASSIGNMENT_TIME,CURRENT TIMESTAMP ";

public static final String SQL_INSERT_LEAD_TRANSACTION_REASSIGN = "INSERT INTO LEAD_TRANSACTION (LEAD_ID, PRODUCT_ID, LEAD_ASSIGNMENT_TIME, LEAD_CLOSURE_TIME, LEAD_STATUS_ID, LEAD_ASSIGNED_PRIMARY_USER, REMARKS, TRANSACTION_TIME, EXPECTED_CLOSURE_DATE, PRIMARY_AUTH, SUB_STATUS_ID, UPDATED_BY, UD_ID,LEAD_PRODUCT_ID , CLIENT_IP) SELECT LEAD_ID,PRODUCT_ID,CURRENT TIMESTAMP,LEAD_CLOSURE_TIME,?";

public static final String SQL_LEAD_DETAIL = "SELECT (SELECT  PRODUCT_NAME FROM PRODUCT_MSTR where PRODUCT_ID = a.PRODUCT_ID)as PRODUCT_NAME,a.LEAD_STATUS_ID ,"
        + " (SELECT LEAD_STATUS FROM LEAD_STATUS WHERE LEAD_STATUS_ID =a.LEAD_STATUS_ID) AS LEAD_STATUS,"
        + " (SELECT SUB_STATUS FROM LEAD_SUB_STATUS WHERE LEAD_STATUS_ID =a.LEAD_STATUS_ID AND SUB_STATUS_ID =a.LEAD_SUB_STATUS_ID AND PRODUCT_LOB_ID=(select PRODUCT_LOB_ID FROM PRODUCT_MSTR PM WHERE PM.PRODUCT_ID=a.PRODUCT_ID)) AS SUB_STATUS"
        + " ,a.LEAD_ID, a.CROSSSELL_REF_LEAD_ID,b.CUSTOMER_NAME,lpd.RSU_CODE,"
        + " (SELECT  CITY_NAME FROM CITY_MSTR where CITY_CODE=lpd.CITY_CODE) as CITY_NAME,"
        + " (SELECT  STATE_NAME FROM STATE_MSTR where STATE_ID =lpd.STATE) as STATE_NAME,"
        + " (SELECT LEAD_PRIORITY_NAME FROM LEAD_PRIORITY_SCORE_MAPPING WHERE LEAD_PRIORITY_VALUE = A.LEAD_PRIORITY ),"
        + " (SELECT ZONE_NAME FROM ZONE_MSTR where ZONE_CODE=lpd.ZONE_CODE) as ZONE_NAME ,"
        + " b.EMAIL,b.PROSPECT_MOBILE_NUMBER, cm.CIRCLE_NAME ,lpd.PINCODE,lpd.ALTERNATE_CONTACT_NUMBER," 
        + " lpd.LANDLINE_NUMBER,LPD.ADDRESS1, lpd.COMPANY,"
        + " (SELECT CITY_ZONE_NAME FROM CITY_ZONE_MSTR where CITY_ZONE_CODE=lpd.CITY_ZONE_CODE) as CITY_ZONE_NAME, "
        + " lpd.ZONE_CODE, lpd.CITY_CODE, lpd.CITY_ZONE_CODE, lpd.CIRCLE_ID, lpd.RENTAL, "
        + " A.ONLINE_CAF_NO,A.ALLOCATED_NO, LPD.MYOP_ID, LPD.PYT_AMT, LPD.TRAN_REFNO, pm.PRODUCT_LOB_ID ,LPD.GEO_IP_CITY ," 
        + " (Select LEAD_CATEGORY from LEAD_CATEGORY_MSTR where LEAD_CATEGORY_ID=A.LEAD_CATEGORY and STATUS='A' ) as LEAD_CATEGORY "//changed by sudhanshu
        + " ,(SELECT SOURCE_NAME FROM SOURCE_MSTR WHERE SOURCE_ID = a.SOURCE  AND STATUS ='A') AS SOURCE_NAME,"
        + " (SELECT SUBSOURCE_NAME FROM SUB_SOURCE_MSTR WHERE char(SUBSOURCE_ID) = a.SUB_SOURCE AND  STATUS ='A') as SUBSOURCE_NAME" 
        + " ,A.UTM_CAMPAIGN ," 
        + " (SELECT PRODUCT_NAME FROM PRODUCT_MSTR where char(PRODUCT_ID) = a.REQUEST_TYPE)AS REQUEST_TYPE," 
        + " a.FID, a.CID, a.FROM_PAGE, a.SERVICE, a.KEYWORD, a.LEAD_SUBMIT_TIME, lpd.ADDRESS2, lpd.APPOINTMENT_TIME, lpd.QUAL_LEAD_PARAM,"
        + " ld.USER_TYPE,ld.RENTAL_TYPE,ld.STATE,ld.BOOSTER_COUNT,ld.FREEBIE_TAKEN,ld.BOOSTER_TAKEN,ld.FLAG,ld.PREPAID_NUMBER,ld.OFFER,"
        + " ld.DOWNLOAD_LIMIT,ld.DEVICE_MRP,ld.VOICE_BENEFIT,ld.DATA_QUOTA,ld.DEVICE_TAKEN,ld.FREEBIE_COUNT, ld.BENEFIT,ld.PKG_DURATION,ld.HLR_NO, a.PLAN, ld.PLAN_ID , ld.EXTRA_PARAM3, ld.EXTRA_PARAM4 "
        + " FROM 	LEAD_DATA 	a, LEAD_PROSPECT_CUSTOMER 	b, LEAD_PROSPECT_DETAIL    lpd, PRODUCT_MSTR pm, CIRCLE_MSTR  cm, LEAD_DETAILS ld"
        + " where a.LEAD_ID=? and a.PROSPECT_ID = b.PROSPECT_ID and a.LEAD_PROSPECT_ID= lpd.LEAD_PROSPECT_ID"
        + " and pm.product_id = a.product_id and cm.CIRCLE_ID = lpd.CIRCLE_ID and cm.LOB_ID = pm.PRODUCT_LOB_ID" 
        + " and ld.LEAD_ID = a.LEAD_ID";



public static final String SQL_SELECT_ASSIGNED_LEAD_EXCEL = "SELECT PRO.PRODUCT_NAME,LT.LEAD_ID,LP.CUSTOMER_NAME,LPD.ADDRESS1,LP.PROSPECT_MOBILE_NUMBER,LT.LEAD_ASSIGNMENT_TIME," +
		"LT.EXPECTED_CLOSURE_DATE,  (SELECT ZONE_NAME FROM ZONE_MSTR where ZONE_CODE=lpd.ZONE_CODE) as ZONE_NAME  , (SELECT LEAD_PRIORITY_NAME FROM LEAD_PRIORITY_SCORE_MAPPING WHERE LEAD_PRIORITY_VALUE = LD.LEAD_PRIORITY ), (SELECT CITY_NAME FROM CITY_MSTR WHERE CITY_CODE = LPD.CITY_CODE ),(SELECT CITY_ZONE_NAME FROM CITY_ZONE_MSTR where CITY_ZONE_CODE=lpd.CITY_ZONE_CODE) as CITY_ZONE_NAME  " +
		" ,LPD.RENTAL, LD.ONLINE_CAF_NO,LD.ALLOCATED_NO, LPD.MYOP_ID, LPD.PYT_AMT, LPD.TRAN_REFNO,LPD.EXTRA_PARAMS_2, LPD.GEO_IP_CITY , (Select LEAD_CATEGORY from LEAD_CATEGORY_MSTR where LEAD_CATEGORY_ID=LD.LEAD_CATEGORY and STATUS='A' ) as LEAD_CATEGORY ,(SELECT pr.PRODUCT_NAME as REQUEST_TYPE FROM PRODUCT_MSTR pr WHERE pr.PRODUCT_ID = LD.REQUEST_TYPE), LPD.PINCODE,LPD.RSU_CODE,	" +
		" LD.FID, LD.CID,LD.FROM_PAGE,LD.SERVICE,LD.PLAN,LD.KEYWORD,LP.COMPANY,LPD.CITY_ZONE_CODE,LPD.CITY_CODE,LPD.ZONE_CODE,LPD.Address2,LPD.APPOINTMENT_TIME," +
	    " LPD.GEO_IP_CITY,LPD.QUAL_LEAD_PARAM,LS.USER_TYPE,LS.PLAN_ID,LS.STATE,LS.BOOSTER_COUNT,LS.FREEBIE_TAKEN,LS.BOOSTER_TAKEN,LS.FLAG,LS.PREPAID_NUMBER, " +
	    " LS.OFFER,LS.DOWNLOAD_LIMIT,LS.DEVICE_MRP,LS.VOICE_BENEFIT,LS.DATA_QUOTA,LS.DEVICE_TAKEN,LS.FREEBIE_COUNT,LS.BENEFIT,LS.PKG_DURATION,LPD.EXTRA_PARAMS_3 as SALES_CHANNEL_CODE, LS.EXTRA_PARAM3 AS LATITUDE, LS.EXTRA_PARAM4 as LONGITUDE, LD.LEAD_STATUS_ID as STATUS " +
		" from LEAD_DATA LD,LEAD_PROSPECT_CUSTOMER LP,LEAD_TRANSACTION LT,PRODUCT_MSTR PRO , LEAD_PROSPECT_DETAIL LPD, LEAD_DETAILS LS WHERE " +
		" LD.LEAD_ID=LS.LEAD_ID and LD.PROSPECT_ID = LP.PROSPECT_ID AND LD.LEAD_PROSPECT_ID= LPD.LEAD_PROSPECT_ID and LD.LEAD_STATUS_ID IN ("+LMSStatusCodes.ASSIGNED+","+LMSStatusCodes.REASSIGNED+") AND LT.SUB_STATUS_ID <> "+LMSStatusCodes.DIALER_CON_FAIL +" AND LT.LEAD_ID = LD.LEAD_ID AND LT.LEAD_STATUS_ID = LD.LEAD_STATUS_ID AND LD.PRODUCT_ID = PRO.PRODUCT_ID AND LT.LEAD_ASSIGNED_PRIMARY_USER = ? " +
		" AND  LT.TRANSACTION_TIME = (select max(TRANSACTION_TIME) FROM LEAD_TRANSACTION tr where  tr.LEAD_ID = LT.LEAD_ID AND tr.LEAD_STATUS_ID =LD.LEAD_STATUS_ID AND tr.LEAD_ASSIGNED_PRIMARY_USER = ? ) ";
		


public static final String SQL_SELECT_ZONAL_COORDINATOR = "SELECT KA.KM_ACTOR_NAME,UM.USER_ID, UM.USER_LOGIN_ID, UM.USER_FNAME, UM.USER_MNAME, UM.USER_LNAME FROM USER_MSTR UM,KM_ACTORS KA, CIRCLE_MSTR CM, " +
		" USER_MAPPING UMAP WHERE UM.KM_ACTOR_ID in (" +Constants.CIRCLE_COORDINATOR_ACTOR+ " , " +Constants.CHANNEL_PARTNER_ACTOR+ " , "+Constants.ZONAL_COORDINATOR_ACTOR+   ") AND UM.STATUS='A'  AND um.USER_LOGIN_ID = UMAP.USER_LOGIN_ID " + //changed again for addition of ZonalCoordinator
				"  AND UMAP.CIRCLE_ID = CM.CIRCLE_ID  AND UMAP.LOB_ID = CM.LOB_ID  and UM.KM_ACTOR_ID = KA.KM_ACTOR_ID   ";

		public static final String SQL_SELECT_ZONAL_COORDINATORS = "SELECT KA.KM_ACTOR_NAME,UM.USER_ID, UM.USER_LOGIN_ID, UM.USER_FNAME, UM.USER_MNAME, UM.USER_LNAME FROM USER_MSTR UM,KM_ACTORS KA, CIRCLE_MSTR CM, " +
		" USER_MAPPING UMAP WHERE UM.KM_ACTOR_ID in ("+Constants.ZONAL_COORDINATOR_ACTOR+   ") AND UM.STATUS='A' AND um.USER_LOGIN_ID = UMAP.USER_LOGIN_ID " + //changed again for addition of ZonalCoordinator
				"  AND UMAP.CIRCLE_ID = CM.CIRCLE_ID  AND UMAP.LOB_ID = CM.LOB_ID  and UM.KM_ACTOR_ID = KA.KM_ACTOR_ID   ";




		public static final String SQL_SELECT_CIRCLE_COORDINATOR = "SELECT KA.KM_ACTOR_NAME,UM.USER_ID, UM.USER_LOGIN_ID, UM.USER_FNAME, UM.USER_MNAME, UM.USER_LNAME FROM USER_MSTR UM,KM_ACTORS KA, CIRCLE_MSTR CM, " +
				" USER_MAPPING UMAP WHERE UM.KM_ACTOR_ID in (" +Constants.CIRCLE_COORDINATOR_ACTOR+  " , "+Constants.ZONAL_COORDINATOR_ACTOR+ ") AND UM.STATUS='A' AND um.USER_LOGIN_ID = UMAP.USER_LOGIN_ID " +
						"  AND UMAP.CIRCLE_ID = CM.CIRCLE_ID  AND UMAP.LOB_ID = CM.LOB_ID  and UM.KM_ACTOR_ID = KA.KM_ACTOR_ID   ";
		



	//	private static final String SQL_QUALIFIED_LEAD_REMARKS_LATEST="SELECT * FROM LEAD_TRANSACTION WHERE LEAD_ID=? order by TRANSACTION_TIME desc fetch first row only ";
		//changed by sudhanshu 


		private static final String SQL_QUALIFIED_LEAD_REMARKS = "SELECT * FROM LEAD_TRANSACTION WHERE LEAD_ID=? and LEAD_STATUS_ID = "+ Constants.LEAD_STATUS_QUALIFIED +" order by TRANSACTION_TIME desc fetch first row only ";
		private static final String SQL_QUALIFIED_LEAD_REMARKS_LATEST="SELECT * FROM LEAD_TRANSACTION WHERE LEAD_ID=? order by TRANSACTION_TIME desc fetch first row only ";
		//changed by sudhanshu 	


		public static final String SELECT_CIRCLE_CO0RDINATORS = "SELECT KA.KM_ACTOR_NAME,UM.USER_ID, UM.USER_LOGIN_ID, UM.USER_FNAME, UM.USER_MNAME, UM.USER_LNAME FROM USER_MSTR UM,KM_ACTORS KA, CIRCLE_MSTR CM, " +
				 " USER_MAPPING UMAP WHERE UM.KM_ACTOR_ID in (" +Constants.CIRCLE_COORDINATOR_ACTOR+" ) AND UM.STATUS='A' AND um.USER_LOGIN_ID = UMAP.USER_LOGIN_ID " + //
					"  AND UMAP.CIRCLE_ID = CM.CIRCLE_ID  AND UMAP.LOB_ID = CM.LOB_ID  and UM.KM_ACTOR_ID = KA.KM_ACTOR_ID   ";
				 //changed by sudhanshu>
		
		
		
		public static final String GET_USERID_FROM_USER_MSTR = " SELECT USER_LOGIN_ID FROM USER_MSTR WHERE  USER_MOBILE_NUMBER = ? AND STATUS='A' ";
		
		public static final String GET_USERID_FROM_USER_MSTR_ADDITIONAL = " SELECT USER_LOGIN_ID FROM USER_MSTR_ADDITIONAL WHERE  USER_MOBILE_NUMBER = ? AND STATUS='A' ";
		//changed by sudhanshu >
		
		
		
		
		
		

SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy");
	public ArrayList<Leads> listAssignedLeads(String loginID,String startDate,String endDate,String view) throws LMSException {
		Connection con = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		Statement stmt = null;
		Leads dto;
		int counter = 1;
		//System.out.println("startDate-------------------"+startDate);
		
		int recordslimitdays = Integer.parseInt(PropertyReader.getAppValue("records.limit.days"));
		ArrayList<Leads> assignedList = new ArrayList<Leads>();
		logger.info("Inside DAO");
		try {
			con = DBConnection.getDBConnection();
			StringBuffer query=new StringBuffer();
			StringBuffer query2=new StringBuffer();
			StringBuffer channelQuery1=new StringBuffer();
			StringBuffer channelQuery2=new StringBuffer();
			Date start,end;
			
			if(startDate !=null && !"".equalsIgnoreCase(startDate))
			{
				start = (Date) dateFormat.parse(startDate);
				startDate = dateFormat1.format(start);
			}
			if(endDate !=null && !"".equalsIgnoreCase(endDate))
				
			{
				end = (Date) dateFormat.parse(endDate);
				endDate = dateFormat1.format(end);
			}
			
			//
			
			stmt = con.createStatement();
			rs4= stmt.executeQuery("select * from CHANNEL_CIRCLE_MAPPING where AGENCY='"+loginID+"'");
			
			if(!rs4.next())
			{
				System.out.println("Control in if block");
				
			if("N".equalsIgnoreCase(view)){
				query.append(SQL_SELECT_ASSIGNED_LEADS);
			
			/* Commented by Parnika 
			  query.append(" (LEAD.LEAD_ASSIGNED_PRIMARY_USER = ? ) and LEAD.SUB_STATUS_ID NOT IN ( "+LMSStatusCodes.REASSIGNED + " , "+ LMSStatusCodes.ASSIGNED +" , "+LMSStatusCodes.DIALER_SECOND_CALL+","+LMSStatusCodes.FOLLOW_UP_DIALER_CALL+" ) and LEAD.SUB_STATUS_ID=LSS.SUB_STATUS_ID ");			 
			*/
				
				query.append(" LEAD.LEAD_ASSIGNED_PRIMARY_USER = ?  ");
			if(startDate !=null && !"".equalsIgnoreCase(startDate))
				query.append(" AND LEAD_ASSIGNMENT_TIME >= ? ");
			else
				query.append(" AND LEAD_ASSIGNMENT_TIME >= current timestamp - "+recordslimitdays +" days ");
			if(endDate !=null && !"".equalsIgnoreCase(endDate))
				query.append(" AND LEAD_ASSIGNMENT_TIME - 1 day <= ? ");
			System.out.println(query+"************************");
			ps1 = con.prepareStatement(query.append(" ORDER BY LEAD_ID ").toString());
			
			// System.out.println(query);
				ps1.setString(counter++, loginID);
				if(startDate !=null && !"".equalsIgnoreCase(startDate))
					ps1.setDate(counter++, Utility.getSqlDateFromString(startDate, "MM/dd/yyyy"));
				if(endDate !=null && !"".equalsIgnoreCase(endDate))
					ps1.setDate(counter++, Utility.getSqlDateFromString(endDate, "MM/dd/yyyy"));
			//System.out.println("parameter saet dya"+loginID +" "+Utility.getSqlDateFromString(startDate, "MM/dd/yyyy")+" "+Utility.getSqlDateFromString(endDate, "MM/dd/yyyy"));	
			rs1 = ps1.executeQuery();
			logger.info("SQL Stmt Query1:" + query);
			//System.out.println("SQL Stmt query0 :" +query);
			while (rs1.next()) {
				dto = new Leads();
				dto.setLeadID(rs1.getString("LEAD_ID"));
				dto.setPrimaryUser(rs1.getString("LEAD_ASSIGNED_PRIMARY_USER"));
				//dto.setSecondaryUser(rs.getString("LEAD_ASSIGNED_SECONDARY_USER"));
				dto.setProduct(rs1.getString("PRODUCT_NAME"));
				dto.setPincode(rs1.getString("PINCODE"));
				SimpleDateFormat otdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
				if(rs1.getTimestamp("LEAD_ASSIGNMENT_TIME") != null){
					dto.setStartTime(otdf.format(rs1.getTimestamp("LEAD_ASSIGNMENT_TIME")));
				}
				else{
					dto.setStartTime("");
				}
				if(rs1.getTimestamp("EXPECTED_CLOSURE_DATE") != null){
					dto.setEndTime(otdf.format(rs1.getTimestamp("EXPECTED_CLOSURE_DATE")));
				}
				else{
					dto.setEndTime("");
				}
				
				dto.setSubStatus(rs1.getString("SUB_STATUS"));
				dto.setSubSubStatus(rs1.getString("SUB_SUB_STATUS"));
				assignedList.add(dto);
				System.out.println("stttttttttttt"+assignedList.size());

			}
			counter = 1;
			StringBuffer query1=new StringBuffer();
				query1.append(SQL_SELECT_ASSIGNED_LEADS1);
				query1.append(" (LEAD.LEAD_ASSIGNED_PRIMARY_USER = ? ) ");
			if(startDate !=null && !"".equalsIgnoreCase(startDate))
				query1.append(" AND LEAD_ASSIGNMENT_TIME >= ? ");
			else
				query1.append(" AND LEAD_ASSIGNMENT_TIME >= current timestamp - "+recordslimitdays +" days ");
			if(endDate !=null && !"".equalsIgnoreCase(endDate))
				query1.append(" AND LEAD_ASSIGNMENT_TIME - 1 day <= ? ");
			//ArrayList<Leads> assignedList = new ArrayList<Leads>();
		//	con = DBConnection.getDBConnection();
			ps2 = con.prepareStatement(query1.append(" ORDER BY LEAD_ID ").toString());
				ps2.setString(counter++, loginID);
				if(startDate !=null && !"".equalsIgnoreCase(startDate))
					ps2.setDate(counter++, Utility.getSqlDateFromString(startDate, "MM/dd/yyyy"));
				if(endDate !=null && !"".equalsIgnoreCase(endDate))
					ps2.setDate(counter++, Utility.getSqlDateFromString(endDate, "MM/dd/yyyy"));
			rs2 = ps2.executeQuery();
			logger.info("SQL Stmt  List2:" + query1);
		//System.out.println("SQL Stmt query1 :" +query1);
			while (rs2.next()) {
				dto = new Leads();
				dto.setLeadID(rs2.getString("LEAD_ID"));
				dto.setPrimaryUser(rs2.getString("LEAD_ASSIGNED_PRIMARY_USER"));
				//dto.setSecondaryUser(rs.getString("LEAD_ASSIGNED_SECONDARY_USER"));
				dto.setProduct(rs2.getString("PRODUCT_NAME"));
				dto.setPincode(rs2.getString("PINCODE"));
				SimpleDateFormat otdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
				if(rs2.getTimestamp("LEAD_ASSIGNMENT_TIME") != null ){
					dto.setStartTime(otdf.format(rs2.getTimestamp("LEAD_ASSIGNMENT_TIME")));
				}
				else{
					dto.setStartTime("");
				}
				if(rs2.getTimestamp("EXPECTED_CLOSURE_DATE") != null){
					dto.setEndTime(otdf.format(rs2.getTimestamp("EXPECTED_CLOSURE_DATE")));
				}
				else{
					dto.setEndTime("");
				}
				
				assignedList.add(dto);

			}
			counter = 1;
				query2.append(SQL_SELECT_ASSIGNED_LEADS2);
				query2.append(" (LEAD.LEAD_ASSIGNED_PRIMARY_USER = ? ) ");
			if(startDate !=null && !"".equalsIgnoreCase(startDate))
				query2.append(" AND LEAD_ASSIGNMENT_TIME >= ? ");
			else
				query2.append(" AND LEAD_ASSIGNMENT_TIME >= current timestamp - "+recordslimitdays +" days ");
			if(endDate !=null && !"".equalsIgnoreCase(endDate))
				query2.append(" AND LEAD_ASSIGNMENT_TIME - 1 day <= ? ");
			
			}
			else
			{
				query2.append(SQL_SELECT_VIEW_ASSIGNED_LEAD);
				if(startDate !=null && !"".equalsIgnoreCase(startDate))
					query2.append(" AND LEAD_ASSIGNMENT_TIME >= ? ");
				else
					query2.append(" AND LEAD_ASSIGNMENT_TIME >= current timestamp - "+recordslimitdays +" days ");
				if(endDate !=null && !"".equalsIgnoreCase(endDate))
					query2.append(" AND LEAD_ASSIGNMENT_TIME - 1 day <= ? ");
			}
			ps3 = con.prepareStatement(query2.append(" ORDER BY LEAD_ID ").toString());
			ps3.setString(counter++, loginID);
			if(startDate !=null && !"".equalsIgnoreCase(startDate))
				ps3.setDate(counter++, Utility.getSqlDateFromString(startDate, "MM/dd/yyyy"));
			if(endDate !=null && !"".equalsIgnoreCase(endDate))
				ps3.setDate(counter++, Utility.getSqlDateFromString(endDate, "MM/dd/yyyy"));
		rs3 = ps3.executeQuery();
		logger.info("SQL Stmt List 3:" + query2);
		//System.out.println("SQL Stmt query2 :" +query2);
		while (rs3.next()) {
			dto = new Leads();
			dto.setLeadID(rs3.getString("LEAD_ID"));
			dto.setPrimaryUser(rs3.getString("LEAD_ASSIGNED_PRIMARY_USER"));
			//dto.setSecondaryUser(rs.getString("LEAD_ASSIGNED_SECONDARY_USER"));
			dto.setProduct(rs3.getString("PRODUCT_NAME"));
			dto.setPincode(rs3.getString("PINCODE"));
			SimpleDateFormat otdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
			if(rs3.getTimestamp("LEAD_ASSIGNMENT_TIME") != null){
				dto.setStartTime(otdf.format(rs3.getTimestamp("LEAD_ASSIGNMENT_TIME")));
			}
			else{
				dto.setStartTime("");
			}
			if(rs3.getTimestamp("EXPECTED_CLOSURE_DATE") != null){
				dto.setEndTime(otdf.format(rs3.getTimestamp("EXPECTED_CLOSURE_DATE")));
			}
			else{
				dto.setEndTime("");
			}
			
			
			assignedList.add(dto);

		}
	}
			
			
			
			
			
			
			
			///////////////////
			
			
			
			
			
			
			
			else
			{
				System.out.println("control in else block");

				if("N".equalsIgnoreCase(view)){
					channelQuery1.append(SQL_ASSIGNED_CHANNEL_LEADS);
				
				/* Commented by Parnika 
				  query.append(" (LEAD.LEAD_ASSIGNED_PRIMARY_USER = ? ) and LEAD.SUB_STATUS_ID NOT IN ( "+LMSStatusCodes.REASSIGNED + " , "+ LMSStatusCodes.ASSIGNED +" , "+LMSStatusCodes.DIALER_SECOND_CALL+","+LMSStatusCodes.FOLLOW_UP_DIALER_CALL+" ) and LEAD.SUB_STATUS_ID=LSS.SUB_STATUS_ID ");			 
				*/
					
					channelQuery1.append(" LCT.LEAD_ASSIGNED_CHANNEL_USER = ?  ");
				if(startDate !=null && !"".equalsIgnoreCase(startDate))
					channelQuery1.append(" AND LCT.TRANSACTION_TIME >= ? ");
				else
					channelQuery1.append(" AND LCT.TRANSACTION_TIME >= current timestamp - "+recordslimitdays +" days ");
				if(endDate !=null && !"".equalsIgnoreCase(endDate))
					channelQuery1.append(" AND LCT.TRANSACTION_TIME - 1 day <= ? ");
				ps1 = con.prepareStatement(channelQuery1.append(" ORDER BY LEAD_ID ").toString());
				
				// System.out.println(query);
					ps1.setString(counter++, loginID);
					if(startDate !=null && !"".equalsIgnoreCase(startDate))
						ps1.setDate(counter++, Utility.getSqlDateFromString(startDate, "MM/dd/yyyy"));
					if(endDate !=null && !"".equalsIgnoreCase(endDate))
						ps1.setDate(counter++, Utility.getSqlDateFromString(endDate, "MM/dd/yyyy"));
				//System.out.println("parameter saet dya"+loginID +" "+Utility.getSqlDateFromString(startDate, "MM/dd/yyyy")+" "+Utility.getSqlDateFromString(endDate, "MM/dd/yyyy"));	
					logger.info("SQL Stmt Query1:" + channelQuery1);
					rs1 = ps1.executeQuery();
				logger.info("SQL Stmt Query1:" + channelQuery1);
				//System.out.println("SQL Stmt query0 :" +query);
				while (rs1.next()) {
					dto = new Leads();
					dto.setLeadID(rs1.getString("LEAD_ID"));
					dto.setPrimaryUser(rs1.getString("LEAD_ASSIGNED_CHANNEL_USER"));
					//dto.setSecondaryUser(rs.getString("LEAD_ASSIGNED_SECONDARY_USER"));
					dto.setProduct(rs1.getString("PRODUCT_NAME"));
					dto.setPincode(rs1.getString("PINCODE"));
					SimpleDateFormat otdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
					if(rs1.getTimestamp("TRANSACTION_TIME") != null){
						dto.setStartTime(otdf.format(rs1.getTimestamp("TRANSACTION_TIME")));
					}
					else{
						dto.setStartTime("");
					}
					if(rs1.getTimestamp("EXPECTED_CLOSURE_DATE") != null){
						dto.setEndTime(otdf.format(rs1.getTimestamp("EXPECTED_CLOSURE_DATE")));
					}
					else{
						dto.setEndTime("");
					}
					
					dto.setSubStatus(rs1.getString("SUB_STATUS"));
					dto.setSubSubStatus(rs1.getString("SUB_SUB_STATUS"));
					assignedList.add(dto);
					System.out.println("stttttttttttt"+assignedList.size());

				}
				counter = 1;
				StringBuffer query1=new StringBuffer();
					query1.append(SQL_SELECT_CHANNEL_LEADS1);
					query1.append(" (LCT.LEAD_ASSIGNED_CHANNEL_USER = ? ) ");
				if(startDate !=null && !"".equalsIgnoreCase(startDate))
					query1.append(" AND LCT.TRANSACTION_TIME >= ? ");
				else
					query1.append(" AND LCT.TRANSACTION_TIME >= current timestamp - "+recordslimitdays +" days ");
				if(endDate !=null && !"".equalsIgnoreCase(endDate))
					query1.append(" AND LCT.TRANSACTION_TIME - 1 day <= ? ");
				//ArrayList<Leads> assignedList = new ArrayList<Leads>();
			//	con = DBConnection.getDBConnection();
				ps2 = con.prepareStatement(query1.append(" ORDER BY LEAD_ID ").toString());
					ps2.setString(counter++, loginID);
					if(startDate !=null && !"".equalsIgnoreCase(startDate))
						ps2.setDate(counter++, Utility.getSqlDateFromString(startDate, "MM/dd/yyyy"));
					if(endDate !=null && !"".equalsIgnoreCase(endDate))
						ps2.setDate(counter++, Utility.getSqlDateFromString(endDate, "MM/dd/yyyy"));
				rs2 = ps2.executeQuery();
				logger.info("SQL Stmt  List2:" + query1);
			//System.out.println("SQL Stmt query1 :" +query1);
				while (rs2.next()) {
					dto = new Leads();
					dto.setLeadID(rs2.getString("LEAD_ID"));
					dto.setPrimaryUser(rs2.getString("LEAD_ASSIGNED_CHANNEL_USER"));
					//dto.setSecondaryUser(rs.getString("LEAD_ASSIGNED_SECONDARY_USER"));
					dto.setProduct(rs2.getString("PRODUCT_NAME"));
					dto.setPincode(rs2.getString("PINCODE"));
					SimpleDateFormat otdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
					if(rs2.getTimestamp("TRANSACTION_TIME") != null ){
						dto.setStartTime(otdf.format(rs2.getTimestamp("TRANSACTION_TIME")));
					}
					else{
						dto.setStartTime("");
					}
					if(rs2.getTimestamp("EXPECTED_CLOSURE_DATE") != null){
						dto.setEndTime(otdf.format(rs2.getTimestamp("EXPECTED_CLOSURE_DATE")));
					}
					else{
						dto.setEndTime("");
					}
					
					assignedList.add(dto);

				}
				counter = 1;
				channelQuery2.append(SQL_ASSIGNED_CHANNEL_LEADS2);
				channelQuery2.append(" (LCT.LEAD_ASSIGNED_CHANNEL_USER = ? ) ");
				if(startDate !=null && !"".equalsIgnoreCase(startDate))
					channelQuery2.append(" AND LCT.TRANSACTION_TIME >= ? ");
				else
					channelQuery2.append(" AND LCT.TRANSACTION_TIME >= current timestamp - "+recordslimitdays +" days ");
				if(endDate !=null && !"".equalsIgnoreCase(endDate))
					channelQuery2.append(" AND LCT.TRANSACTION_TIME - 1 day <= ? ");
				
				}
				ps3 = con.prepareStatement(channelQuery2.append(" ORDER BY LEAD_ID ").toString());
				ps3.setString(counter++, loginID);
				if(startDate !=null && !"".equalsIgnoreCase(startDate))
					ps3.setDate(counter++, Utility.getSqlDateFromString(startDate, "MM/dd/yyyy"));
				if(endDate !=null && !"".equalsIgnoreCase(endDate))
					ps3.setDate(counter++, Utility.getSqlDateFromString(endDate, "MM/dd/yyyy"));
			rs3 = ps3.executeQuery();
			logger.info("SQL Stmt List 3:" + channelQuery2);
			//System.out.println("SQL Stmt query2 :" +query2);
			while (rs3.next()) {
				dto = new Leads();
				dto.setLeadID(rs3.getString("LEAD_ID"));
				dto.setPrimaryUser(rs3.getString("LEAD_ASSIGNED_CHANNEL_USER"));
				//dto.setSecondaryUser(rs.getString("LEAD_ASSIGNED_SECONDARY_USER"));
				dto.setProduct(rs3.getString("PRODUCT_NAME"));
				dto.setPincode(rs3.getString("PINCODE"));
				SimpleDateFormat otdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
				if(rs3.getTimestamp("TRANSACTION_TIME") != null){
					dto.setStartTime(otdf.format(rs3.getTimestamp("TRANSACTION_TIME")));
				}
				else{
					dto.setStartTime("");
				}
				if(rs3.getTimestamp("EXPECTED_CLOSURE_DATE") != null){
					dto.setEndTime(otdf.format(rs3.getTimestamp("EXPECTED_CLOSURE_DATE")));
				}
				else{
					dto.setEndTime("");
				}
				
				
				assignedList.add(dto);

			}
		}
			
			//ArrayList<Leads> assignedList = new ArrayList<Leads>();
		//	con = DBConnection.getDBConnection();
			
			Collections.sort(assignedList) ; 
			logger.info("circle user success"+assignedList.size());
			
			return assignedList;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("SQL Exception occured while Viewing assigned Leads."+ "SQL Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Exception occured while Viewing assigned Leads."	+ " Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				/*DBConnection.releaseResources(null, ps1, rs1);
				DBConnection.releaseResources(null, ps2, rs2);
				DBConnection.releaseResources(null, ps3, rs3);
				DBConnection.releaseResources(con, stmt, rs4);*/
				
			} catch (Exception e) {
				logger.error("DAO Exception occured while Viewing assigned Leads."	+ "DAO Exception Message: "	+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}

	}
	
	public ArrayList<Leads> listAssignedLeadsEscalation(String loginID,String startDate,String endDate,String view) throws LMSException {
		Connection con = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		Leads dto;
		int counter = 1;
		//System.out.println("startDate-------------------"+startDate);
		
		int recordslimitdays = Integer.parseInt(PropertyReader.getAppValue("records.limit.days"));
		ArrayList<Leads> assignedList = new ArrayList<Leads>();
		logger.info("Inside DAO");
		try {
			con = DBConnection.getDBConnection();
			StringBuffer query=new StringBuffer();
			Date start,end;
			StringBuffer query2=new StringBuffer();
			if(startDate !=null && !"".equalsIgnoreCase(startDate))
			{
				start = (Date) dateFormat.parse(startDate);
				startDate = dateFormat1.format(start);
			}
			if(endDate !=null && !"".equalsIgnoreCase(endDate))
				
			{
				end = (Date) dateFormat.parse(endDate);
				endDate = dateFormat1.format(end);
			}		
			
			
				query2.append(SQL_SELECT_ASSIGNED_LEADS_ESCALATION);
				if(startDate !=null && !"".equalsIgnoreCase(startDate))
					query2.append(" AND LEAD.TRANSACTION_TIME >= ? ");
				else
					query2.append(" AND LEAD.TRANSACTION_TIME >= current timestamp - "+recordslimitdays +" days ");
				if(endDate !=null && !"".equalsIgnoreCase(endDate))
					query2.append(" AND LEAD.TRANSACTION_TIME - 1 day <= ? ");
			
			
			//ArrayList<Leads> assignedList = new ArrayList<Leads>();
		//	con = DBConnection.getDBConnection();
			ps3 = con.prepareStatement(query2.append(" ORDER BY LEAD_ID   ").toString());
			
				ps3.setString(counter++, loginID);
				
				if(startDate !=null && !"".equalsIgnoreCase(startDate))
					ps3.setDate(counter++, Utility.getSqlDateFromString(startDate, "MM/dd/yyyy"));
				if(endDate !=null && !"".equalsIgnoreCase(endDate))
					ps3.setDate(counter++, Utility.getSqlDateFromString(endDate, "MM/dd/yyyy"));
			rs3 = ps3.executeQuery();
			//logger.info("SQL Stmt :" + query2);
			//System.out.println("SQL Stmt query2 :" +query2);
			while (rs3.next()) {
				dto = new Leads();
				dto.setLeadID(rs3.getString("LEAD_ID"));
				dto.setPrimaryUser(rs3.getString("LEAD_ASSIGNED_PRIMARY_USER"));
				//dto.setSecondaryUser(rs.getString("LEAD_ASSIGNED_SECONDARY_USER"));
				dto.setProduct(rs3.getString("PRODUCT_NAME"));
				SimpleDateFormat otdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
				dto.setStartTime(otdf.format(rs3.getTimestamp("LEAD_ASSIGNMENT_TIME")));
				dto.setEndTime(otdf.format(rs3.getTimestamp("EXPECTED_CLOSURE_DATE")));
				dto.setSubStatus(rs3.getString("SUB_STATUS"));
				assignedList.add(dto);

			}
			Collections.sort(assignedList) ; 
			logger.info("circle user success");
			return assignedList;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("SQL Exception occured while Viewing assigned Leads."+ "SQL Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Exception occured while Viewing assigned Leads."	+ " Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps1, rs1);
				DBConnection.releaseResources(con, ps2, rs2);
				DBConnection.releaseResources(con, ps3, rs3);
			} catch (Exception e) {
				logger.error("DAO Exception occured while Viewing assigned Leads."	+ "DAO Exception Message: "	+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}

	}
	
	
	
	
	
	
	public ArrayList<Leads> listAssignedLeadsFeasibility(String loginID,String startDate,String endDate,String view) throws LMSException {
		Connection con = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		Leads dto;
		int counter = 1;
		//System.out.println("startDate-------------------"+startDate);
		
		int recordslimitdays = Integer.parseInt(PropertyReader.getAppValue("records.limit.days"));
		ArrayList<Leads> assignedList = new ArrayList<Leads>();
		logger.info("Inside DAO");
		try {
			con = DBConnection.getDBConnection();
			StringBuffer query=new StringBuffer();
			Date start,end;
			StringBuffer query2=new StringBuffer();
			if(startDate !=null && !"".equalsIgnoreCase(startDate))
			{
				start = (Date) dateFormat.parse(startDate);
				startDate = dateFormat1.format(start);
			}
			if(endDate !=null && !"".equalsIgnoreCase(endDate))
				
			{
				end = (Date) dateFormat.parse(endDate);
				endDate = dateFormat1.format(end);
			}		
			
			
				query2.append(SQL_SELECT_ASSIGNED_LEADS_FEASIBILITY);
				if(startDate !=null && !"".equalsIgnoreCase(startDate))
					query2.append(" AND LEAD.TRANSACTION_TIME >= ? ");
				else
					query2.append(" AND LEAD.TRANSACTION_TIME >= current timestamp - "+recordslimitdays +" days ");
				if(endDate !=null && !"".equalsIgnoreCase(endDate))
					query2.append(" AND LEAD.TRANSACTION_TIME - 1 day <= ? ");
			
			
			//ArrayList<Leads> assignedList = new ArrayList<Leads>();
		//	con = DBConnection.getDBConnection();
			ps3 = con.prepareStatement(query2.append(" ORDER BY LEAD_ID   ").toString());
				ps3.setString(counter++, loginID);
			//ps3.setString(1, loginID);
				if(startDate !=null && !"".equalsIgnoreCase(startDate))
					ps3.setDate(counter++, Utility.getSqlDateFromString(startDate, "MM/dd/yyyy"));
				if(endDate !=null && !"".equalsIgnoreCase(endDate))
					ps3.setDate(counter++, Utility.getSqlDateFromString(endDate, "MM/dd/yyyy"));
			rs3 = ps3.executeQuery();
			//logger.info("SQL Stmt :" + query2);
			//System.out.println("SQL Stmt query2 :" +query2);
			while (rs3.next()) {
				dto = new Leads();
				
				dto.setLeadID(rs3.getString("LEAD_ID"));
				dto.setPrimaryUser(rs3.getString("LEAD_ASSIGNED_PRIMARY_USER"));
				//dto.setSecondaryUser(rs.getString("LEAD_ASSIGNED_SECONDARY_USER"));
				dto.setProduct(rs3.getString("PRODUCT_NAME"));
				SimpleDateFormat otdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
				dto.setStartTime(otdf.format(rs3.getTimestamp("LEAD_ASSIGNMENT_TIME")));
				dto.setEndTime(otdf.format(rs3.getTimestamp("EXPECTED_CLOSURE_DATE")));
				dto.setSubStatus(rs3.getString("SUB_STATUS"));
				assignedList.add(dto);

			}
			Collections.sort(assignedList) ; 
			logger.info("circle user success");
			return assignedList;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("SQL Exception occured while Viewing assigned Leads."+ "SQL Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Exception occured while Viewing assigned Leads."	+ " Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps1, rs1);
				DBConnection.releaseResources(con, ps2, rs2);
				DBConnection.releaseResources(con, ps3, rs3);
			} catch (Exception e) {
				logger.error("DAO Exception occured while Viewing assigned Leads."	+ "DAO Exception Message: "	+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public ArrayList<Constant> getActionList(String keyName) throws LMSException {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		Constant dto;
		int counter = 1;
		try {
			StringBuffer query=new StringBuffer(SQL_SELECT_ACTION);
			logger.info("Inside DAO : " + keyName);
			query.append(" LEAD_STATUS_NAME = ? ");
			ArrayList<Constant> ActionList = new ArrayList<Constant>();
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(query.append("ORDER By LEAD_STATUS   ").toString());
				ps.setString(counter++, keyName);
			rs = ps.executeQuery();
/*			logger.info("SQL Stmt :" + query);
			System.out.println(query);
*/		
        	while (rs.next()) {
				dto = new Constant();
				dto.setKeyValue(rs.getString("LEAD_STATUS"));
				dto.setID(Long.parseLong(String.valueOf(rs.getInt("LEAD_STATUS_ID"))));
				ActionList.add(dto);

			}
			logger.info("circle user success");
			return ActionList;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("SQL Exception occured while Viewing assigned Leads."	+ "SQL Exception Message: "	+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(	" Exception occured while Viewing assigned Leads."+ " Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {
				logger.error("DAO Exception occured while Viewing assigned Leads."	+ "DAO Exception Message: "	+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}

	}
	public ArrayList<UserDto> getUsersList(ArrayList circleList,  ArrayList lobList) throws LMSException {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		UserDto dto;
		int counter = 1;
		try {
			StringBuffer query=new StringBuffer(SQL_USERS_LEAD_FORWARD);
			logger.info("Inside DAO");
			
			
			if (circleList != null && circleList.size()>0 ){
				StringBuffer stringCircleIds = new StringBuffer();
				if(circleList.size()==1){
					for (Object circleId:circleList){
						stringCircleIds.append(circleId.toString());
					}
					query.append(" AND CM.CIRCLE_MSTR_ID in(");
					query.append(stringCircleIds);
					query.append(" ) ");
				}else{
					
					for (int i=0;i<circleList.size();i++){
						Object circleId=circleList.get(i);
						stringCircleIds.append(circleId.toString());
						if (i==circleList.size()-1){
							break;
						}
						stringCircleIds.append(" , ");
					}
					
					
					query.append(" AND CM.CIRCLE_MSTR_ID in(");
					query.append(stringCircleIds);
					query.append(" ) ");
				}


			}else if(lobList.size()>0 || lobList != null){
				StringBuffer stringLobIds = new StringBuffer();
				
				if(lobList.size()==1){
					for (Object lobId:lobList){
						stringLobIds.append(lobId.toString());
					}
				}else{
					
					for (int i=0;i<lobList.size();i++){
						Object lobId=lobList.get(i);
						stringLobIds.append(lobId.toString());
						if (i==lobList.size()-1){
							break;
						}
						stringLobIds.append(" , ");
					}
				}
				
				query.append(" And UMAP.LOB_ID in(" );
				query.append(stringLobIds);
				System.out.println("ffffffff"+stringLobIds);
				query.append(" ) ");
			}
			
			query.append(" order by  UM.KM_ACTOR_ID");
			
			ArrayList<UserDto> masterList = new ArrayList<UserDto>();
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(query.append("   ").toString());
		//		ps.setInt(1,Integer.parseInt(circleID.toString()));
			rs = ps.executeQuery();
			//logger.info("SQL Stmt :" + query);
			//System.out.println(query);
			while (rs.next()) {
				dto = new UserDto();
				dto.setUserLoginId(rs.getString("USER_LOGIN_ID"));
				dto.setUserFname(rs.getString("USER_FNAME")+" "+rs.getString("USER_MNAME")+" "+rs.getString("USER_LNAME")+" ( "+rs.getString("USER_LOGIN_ID")+" , "+rs.getString("KM_ACTOR_NAME")+" ) ");
				dto.setKmActorName(rs.getString("KM_ACTOR_NAME"));
				masterList.add(dto);

			}
			logger.info("circle user success");
			return masterList;			
/*			
	========		
			query.append(" WHERE CIRCLE_ID=(SELECT CIRCLE_ID FROM USER_MSTR WHERE USER_LOGIN_ID = ?) AND USER_LOGIN_ID <> ? AND KM_ACTOR_ID IN(4,5,6,9,10) ");
			ArrayList<UserDto> masterList = new ArrayList<UserDto>();
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(query.append("   ").toString());
				ps.setString(counter++, loginID);
				ps.setString(counter++, loginID);
			rs = ps.executeQuery();
			//logger.info("SQL Stmt :" + query);
			//System.out.println(query);
			while (rs.next()) {
				dto = new UserDto();
				dto.setUserLoginId(rs.getString("USER_LOGIN_ID"));
				dto.setUserFname(rs.getString("USER_FNAME")+" "+rs.getString("USER_MNAME")+" "+rs.getString("USER_LNAME")+" ( "+rs.getString("USER_LOGIN_ID")+" ) ");
				masterList.add(dto);

			}
			logger.info("circle user success");
			return masterList;*/
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("SQL Exception occured while Viewing assigned Leads."+ "SQL Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Exception occured while Viewing assigned Leads."	+ " Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {
				logger.error("DAO Exception occured while Viewing assigned Leads."+ "DAO Exception Message: "+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	}

	public Boolean closeTheLead(ArrayList masterList) throws LMSException {
		Connection con = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		int counter = 1;
		ResultSet rs = null;		
		boolean flag = false;
		int count = 0;
		Leads lead = null;
		StringBuffer query= null;
		int subStatus = 0;
		
		try {
			con = DBConnection.getDBConnection();
			//con.setAutoCommit(false);
			for(int i = 0;i<masterList.size();i++){
				
				counter = 1;
				lead = new Leads();
				lead = (Leads)masterList.get(i);
				query= new StringBuffer();
			logger.info("Inside DAO");
			int status = Integer.parseInt(lead.getStatus());
			
			/* Added by Parnika if sub-status is not captured , main status is added into sub status*/
			if(lead.getSubStatus() == null || lead.getSubStatus().trim().equals("") && (LMSStatusCodes.WON != status || LMSStatusCodes.LOST != status)){
				subStatus = Integer.parseInt(lead.getStatus());
			}
			else if(lead.getSubStatus() != null && !(lead.getSubStatus().trim().equals(""))) {
				subStatus = Integer.parseInt(lead.getSubStatus());
			}
			/* End of changes by Parnika */
			
			if(LMSStatusCodes.WON == status || LMSStatusCodes.LOST == status){
			query.append(SQL_UPDATE_LEAD_STATUS);
		
			/* Modified By Parnika */
			query.append(" SET  LEAD_STATUS_ID = ?, LEAD_SUB_SUB_STATUS_ID =? ,REMARKS = ?, LEAD_SUB_STATUS_ID = ? ,LEAD_CLOSURE_TIME = CURRENT TIMESTAMP,CAF = ? , UPDATED_DT = CURRENT TIMESTAMP, UPDATED_BY = ?   WHERE LEAD_ID = ? "); 	//changed by sudhanshu
			/* End of changes by Parnika */
			ps1 = con.prepareStatement(query.append("   ").toString());
				
				
				if(LMSStatusCodes.WON == status ){
					ps1.setInt(counter++, status);
					ps1.setInt(counter++, LMSStatusCodes.WON_CHANNEL_PARTNER);
				}else if(LMSStatusCodes.LOST == status){
					ps1.setInt(counter++, status);
					ps1.setInt(counter++, LMSStatusCodes.LOST_CHANNEL_PARTNER);
				}
				
				ps1.setString(counter++, lead.getRemarks());
				ps1.setInt(counter++, subStatus);   	//changed by sudhanshu
				ps1.setString(counter++, lead.getCafNumber());
				ps1.setString(counter++, lead.getUpdatedBy()); 	//changed by sudhanshu
				ps1.setString(counter++, lead.getLeadID());
				
			count = ps1.executeUpdate();
			
			ps1=con.prepareStatement("UPDATE LEAD_DETAILS SET EXTRA_PARAM3=? , EXTRA_PARAM4=? where LEAD_ID=? ");
			ps1.setString(1, lead.getLattitude());
			ps1.setString(2, lead.getLongitude());
			ps1.setLong(3,Long.parseLong(lead.getLeadID()));
			ps1.executeUpdate();
			}

			
			logger.info("lead status updated success");
				count = 0;
				counter = 1;
				StringBuffer dmlStatement = new StringBuffer(SQL_INSERT_LEAD_TRANSACTION);
				if(LMSStatusCodes.WON == status || LMSStatusCodes.LOST == status)
					dmlStatement.append(",?,? ");
				else{
					dmlStatement.append(",LEAD_STATUS_ID,LEAD_SUB_SUB_STATUS_ID ");
				}
				dmlStatement.append(",LEAD_ASSIGNED_PRIMARY_USER,?,CURRENT TIMESTAMP,EXPECTED_CLOSURE_DATE,PRIMARY_AUTH,?,?,?,LEAD_PRODUCT_ID ,(SELECT CLIENT_IP FROM KM_LOGIN_DATA WHERE USER_LOGIN_ID = ?  ORDER BY LOGIN_TIME DESC  FETCH FIRST ROW ONLY ) FROM LEAD_TRANSACTION ");//changed by sudhanshu
				dmlStatement.append(" WHERE LEAD_ID = ? AND SUB_STATUS_ID <> 210 AND SUB_STATUS_ID <> ? AND LEAD_STATUS_ID IN ( "+LMSStatusCodes.ASSIGNED+","+LMSStatusCodes.REASSIGNED+" )  ");

				dmlStatement.append(" ORDER BY TRANSACTION_TIME DESC FETCH FIRST ROW ONLY   ");
				
				ps2 = con.prepareStatement(dmlStatement.toString());
				if(LMSStatusCodes.WON == status ){
					ps2.setInt(counter++, status);
					ps2.setInt(counter++, LMSStatusCodes.WON_CHANNEL_PARTNER);
				}else if(LMSStatusCodes.LOST == status){
					ps2.setInt(counter++, status);
					ps2.setInt(counter++, LMSStatusCodes.LOST_CHANNEL_PARTNER);
				}
					
				ps2.setString(counter++, lead.getRemarks());
/*				if(LMSStatusCodes.WON == status || LMSStatusCodes.LOST == status){
					ps2.setInt(counter++, Integer.parseInt(lead.getSubStatus()));
				}					
				else{
					ps2.setInt(counter++, status);
				}*/
					
				ps2.setInt(counter++,subStatus);
				ps2.setString(counter++, lead.getUpdatedBy());
				ps2.setString(counter++, lead.getUdId());
				ps2.setString(counter++, lead.getUpdatedBy());
				ps2.setString(counter++, lead.getLeadID());
				ps2.setInt(counter++ , LMSStatusCodes.DIALER_SECOND_CALL);
				count = ps2.executeUpdate();
				if(count == 1){
					flag = true;
				}
				
				ps1 = null;
				ps2 = null;
				
			}
			return flag;
			
		} catch (SQLException e) {
			/*try{
				con.rollback();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}*/
			e.printStackTrace();
			logger.error("SQL Exception occured while Closing the lead."+ "SQL Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Exception occured while Closing the lead."	+ " Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps1, rs);
				DBConnection.releaseResources(con, ps2, rs);
			} catch (Exception e) {
				logger.error("DAO Exception occured while Closing the lead."+ "DAO Exception Message: "+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	}

	
	public Boolean closeTheLeadCloseLoop(ArrayList masterList) throws LMSException {
		Connection con = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		int counter = 1;
		ResultSet rs = null;		
		boolean flag = false;
		int count = 0;
		Leads lead = null;
		StringBuffer query= null;
		int subStatus = 0;
		
		try {
			con = DBConnection.getDBConnection();
			//con.setAutoCommit(false);
			for(int i = 0;i<masterList.size();i++){
				
				counter = 1;
				lead = new Leads();
				lead = (Leads)masterList.get(i);
				query= new StringBuffer();
			logger.info("Inside DAO");
			int status = Integer.parseInt(lead.getStatus());
			
			/* Added by Parnika if sub-status is not captured , main status is added into sub status*/
			if(lead.getSubStatus() == null || lead.getSubStatus().trim().equals("") && (LMSStatusCodes.WON != status || LMSStatusCodes.LOST != status)){
				subStatus = Integer.parseInt(lead.getStatus());
			}
			else if(lead.getSubStatus() != null && !(lead.getSubStatus().trim().equals(""))) {
				subStatus = Integer.parseInt(lead.getSubStatus());
			}
			/* End of changes by Parnika */
			
			if(LMSStatusCodes.WON == status || LMSStatusCodes.LOST == status){
			query.append(SQL_UPDATE_LEAD_STATUS);
		
			/* Modified By Parnika */
			query.append(" SET  LEAD_STATUS_ID = ?, LEAD_SUB_SUB_STATUS_ID =? ,REMARKS = ?, LEAD_SUB_STATUS_ID = ? ,LEAD_CLOSURE_TIME = CURRENT TIMESTAMP,CAF = ? , UPDATED_DT = CURRENT TIMESTAMP, UPDATED_BY = ?   WHERE LEAD_ID = ? "); 	//changed by sudhanshu
			/* End of changes by Parnika */
			ps1 = con.prepareStatement(query.append("   ").toString());
				
				
				if(LMSStatusCodes.WON == status ){
					ps1.setInt(counter++, status);
					ps1.setInt(counter++, LMSStatusCodes.WON_CLOSE_LOOPING);
				}else if(LMSStatusCodes.LOST == status){
					ps1.setInt(counter++, status);
					ps1.setInt(counter++, LMSStatusCodes.LOST_CLOSE_LOOPING);
				}
				
				ps1.setString(counter++, lead.getRemarks());
				ps1.setInt(counter++, subStatus);   	//changed by sudhanshu
				ps1.setString(counter++, lead.getCafNumber());
				ps1.setString(counter++, lead.getUpdatedBy()); 	//changed by sudhanshu
				ps1.setString(counter++, lead.getLeadID());
				
			count = ps1.executeUpdate();
			}

			
			logger.info("lead status updated success");
				count = 0;
				counter = 1;
				StringBuffer dmlStatement = new StringBuffer(SQL_INSERT_LEAD_TRANSACTION);
				if(LMSStatusCodes.WON == status || LMSStatusCodes.LOST == status)
					dmlStatement.append(",?,? ");
				else{
					dmlStatement.append(",LEAD_STATUS_ID,LEAD_SUB_SUB_STATUS_ID ");
				}
				dmlStatement.append(",LEAD_ASSIGNED_PRIMARY_USER,?,CURRENT TIMESTAMP,EXPECTED_CLOSURE_DATE,PRIMARY_AUTH,?,?,?,LEAD_PRODUCT_ID ,(SELECT CLIENT_IP FROM KM_LOGIN_DATA WHERE USER_LOGIN_ID = ?  ORDER BY LOGIN_TIME DESC  FETCH FIRST ROW ONLY ) FROM LEAD_TRANSACTION ");//changed by sudhanshu
				dmlStatement.append(" WHERE LEAD_ID = ? AND SUB_STATUS_ID <> 210 AND SUB_STATUS_ID <> ? AND LEAD_STATUS_ID IN ( "+LMSStatusCodes.ASSIGNED+","+LMSStatusCodes.REASSIGNED+" )  ");

				dmlStatement.append(" ORDER BY TRANSACTION_TIME DESC FETCH FIRST ROW ONLY   ");
				
				ps2 = con.prepareStatement(dmlStatement.toString());
				if(LMSStatusCodes.WON == status ){
					ps2.setInt(counter++, status);
					ps2.setInt(counter++, LMSStatusCodes.WON_CLOSE_LOOPING);
				}else if(LMSStatusCodes.LOST == status){
					ps2.setInt(counter++, status);
					ps2.setInt(counter++, LMSStatusCodes.LOST_CLOSE_LOOPING);
				}
					
				ps2.setString(counter++, lead.getRemarks());
/*				if(LMSStatusCodes.WON == status || LMSStatusCodes.LOST == status){
					ps2.setInt(counter++, Integer.parseInt(lead.getSubStatus()));
				}					
				else{
					ps2.setInt(counter++, status);
				}*/
					
				ps2.setInt(counter++,subStatus);
				ps2.setString(counter++, lead.getUpdatedBy());
				ps2.setString(counter++, lead.getUdId());
				ps2.setString(counter++, lead.getUpdatedBy());
				ps2.setString(counter++, lead.getLeadID());
				ps2.setInt(counter++ , LMSStatusCodes.DIALER_SECOND_CALL);
				count = ps2.executeUpdate();
				if(count == 1){
					flag = true;
				}
				
				ps1 = null;
				ps2 = null;
				
			}
			return flag;
			
		} catch (SQLException e) {
			/*try{
				con.rollback();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}*/
			e.printStackTrace();
			logger.error("SQL Exception occured while Closing the lead."+ "SQL Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Exception occured while Closing the lead."	+ " Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps1, rs);
				DBConnection.releaseResources(con, ps2, rs);
			} catch (Exception e) {
				logger.error("DAO Exception occured while Closing the lead."+ "DAO Exception Message: "+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	}

	public Boolean reAssignTheLead(LeadForm commonForm) throws LMSException {
		
		Connection con = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		int counter = 1;
		ResultSet rs = null;
		ResultSet rs2=null;
		boolean flag = false;
		int count = 0;
		
		try {
			int len = commonForm.getLeadIds().length;
			con = DBConnection.getDBConnection();
			for(int i=0;i<len;i++){
			//int status = Integer.parseInt(commonForm.getActionType());
				count = 0;
				counter = 1;
				StringBuffer query=new StringBuffer(SQL_UPDATE_LEAD_STATUS);
				StringBuffer dmlStatement = new StringBuffer(SQL_INSERT_LEAD_TRANSACTION_REASSIGN);
				
			logger.info("Inside DAO");
			query.append(" SET  LEAD_STATUS_ID = ? , LEAD_SUB_STATUS_ID = ? , UPDATED_DT = CURRENT TIMESTAMP, UPDATED_BY = ? WHERE LEAD_ID = ? ");  //changed by sudhanshu
			ps1 = con.prepareStatement(query.append("   ").toString());
				ps1.setInt(counter++, LMSStatusCodes.REASSIGNED);
				ps1.setInt(counter++, LMSStatusCodes.ASSIGNED); //changed by Sudhanshu
				ps1.setString(counter++, commonForm.getUpdatedBy());
				ps1.setString(counter++, commonForm.getLeadIds()[i]);
			count = ps1.executeUpdate();
			//logger.info("SQL Stmt :" + query);
			//System.out.println(query);
		
			logger.info("circle user success");
			if(count == 1)
			{
				count = 0;
				counter = 1;
				dmlStatement.append(",?,?,CURRENT TIMESTAMP,EXPECTED_CLOSURE_DATE,PRIMARY_AUTH,SUB_STATUS_ID,?,? ,LEAD_PRODUCT_ID,(SELECT CLIENT_IP FROM KM_LOGIN_DATA WHERE USER_LOGIN_ID = ?  ORDER BY LOGIN_TIME DESC  FETCH FIRST ROW ONLY )  FROM LEAD_TRANSACTION ");
				dmlStatement.append(" WHERE LEAD_ID = ? AND SUB_STATUS_ID <> 210 AND SUB_STATUS_ID <> ?  ORDER BY TRANSACTION_TIME DESC FETCH FIRST ROW ONLY ");
				ps2 = con.prepareStatement(dmlStatement.toString());
				ps2.setInt(counter++, LMSStatusCodes.REASSIGNED);
				ps2.setString(counter++, commonForm.getForwardTo());
				ps2.setString(counter++, commonForm.getForwardComments());
				ps2.setString(counter++, commonForm.getUpdatedBy());
				ps2.setString(counter++, commonForm.getUdId());
				ps2.setString(counter++, commonForm.getUpdatedBy());
				ps2.setString(counter++,  commonForm.getLeadIds()[i]);
				ps2.setInt(counter++ , LMSStatusCodes.DIALER_SECOND_CALL);
				//System.out.println(dmlStatement);
				count = ps2.executeUpdate();
				if(count == 1)
				flag = true;
				
				/*ps2=con.prepareStatement("select USER_MOBILE_NUMBER from USER_MSTR where USER_LOGIN_ID=?  ");
				ps2.setString(1, commonForm.getForwardTo());
				rs=ps2.executeQuery();
				MasterService masterService=new MasterServiceImpl();
				
				if(rs.next()){
					SendSMSXML smsXml=new SendSMSXML();
					ps1=con.prepareStatement("SELECT MESSAGE_TEMPLATE FROM ALERT_MSTR WHERE ALERT_ID=5");
					rs2=ps1.executeQuery();
					
					String message="";
					if(rs2.next()){
						message=rs2.getString("MESSAGE_TEMPLATE");
					}
					message=message.replace("{LEAD_ID}",commonForm.getLeadIds()[i]);
					String lattQuery="";
					lattQuery="select LD.EXTRA_PARAM3 as LATITUDE, LD.EXTRA_PARAM4 as LONGITUDE, LPC.PROSPECT_MOBILE_NUMBER, LDT.PRODUCT_ID from LEAD_DETAILS LD, LEAD_DATA LDT, LEAD_PROSPECT_DETAIL LPD ,LEAD_PROSPECT_CUSTOMER LPC "
						+" where LD.LEAD_ID=LDT.LEAD_ID"
						+" and LDT.LEAD_ID=LPD.LEAD_ID"
						+" and LDT.PROSPECT_ID=LPC.PROSPECT_ID"
						+" and LD.LEAD_ID=?  " ;
					ps1=con.prepareStatement(lattQuery);
					ps1.setLong(1, Long.parseLong(commonForm.getLeadIds()[i]));
					rs2=ps1.executeQuery();
					String mobileno="";
					String latt="";
					String longi="";
					int prod=0;
					if(rs2.next()){
						latt=rs2.getString("LATITUDE");
						longi=rs2.getString("LONGITUDE");
						mobileno=rs2.getString("PROSPECT_MOBILE_NUMBER");
						prod=rs2.getInt("PRODUCT_ID");
					}
					if(mobileno==null || mobileno.length()==0){
						mobileno="";
					}
					if(latt==null || latt.length()==0){
						latt="";
					}
					if(longi==null || longi.length()==0){
						longi="";
					}
					message=message.replace("{CUST_MOB}", mobileno);
					message=message.replace("{LATT}", latt);
					message=message.replace("{LONG}", longi);
					
					try{
						List prodConfList=Arrays.asList(masterService.getParameterName("NEW_PRODUCT").trim().split(","));
						if(prodConfList.contains(prod))
							smsXml.sendSms(rs.getString("USER_MOBILE_NUMBER"), message);
					}catch(Exception expx){
						expx.printStackTrace();
						logger.info("Unable to send SMS");
					}
				}*/
			}
			}
			return flag;
			
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("SQL Exception occured while Forwarding the Leads."+ "SQL Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Exception occured while Forwarding the Leads."	+ " Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps1, rs);
				DBConnection.releaseResources(con, ps2, rs2);
			} catch (Exception e) {
				logger.error("DAO Exception occured while Forwarding the Leads."+ "DAO Exception Message: "+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	}


	public LeadDetailDTO viewLeadDetail(Long leadID) throws LMSException {
		LeadDetailDTO leadDetail = new LeadDetailDTO();
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		int counter = 1;
		try {
			StringBuffer query=new StringBuffer(SQL_LEAD_DETAIL);
			logger.info("Inside DAO");
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(query.append("   ").toString());
				ps.setLong(counter++, leadID);
			rs = ps.executeQuery();
			//logger.info("SQL Stmt :" + query);
			//System.out.println("query-----"+query);
//			System.out.println(query);
			if (rs.next()) {
				
				leadDetail.setProduct_name(rs.getString("PRODUCT_NAME"));
				leadDetail.setAddress1(rs.getString("ADDRESS1"));
				leadDetail.setAlternate_contact_number(rs.getString("ALTERNATE_CONTACT_NUMBER"));
				leadDetail.setCircle_name(rs.getString("CIRCLE_NAME"));
				leadDetail.setCity_name(rs.getString("CITY_NAME"));
				leadDetail.setCustomer_name(rs.getString("CUSTOMER_NAME"));
				leadDetail.setEmail(rs.getString("EMAIL"));
				leadDetail.setLandline_number(rs.getString("LANDLINE_NUMBER"));
				leadDetail.setPincode(rs.getString("PINCODE"));
				leadDetail.setProspect_mobile_number(rs.getString("PROSPECT_MOBILE_NUMBER"));
				leadDetail.setRsu_id(rs.getString("RSU_CODE"));
				leadDetail.setState_name(rs.getString("STATE_NAME"));
				leadDetail.setZone_name(rs.getString("ZONE_NAME"));
				leadDetail.setLead_status(String.valueOf(rs.getInt("LEAD_STATUS_ID")));
				leadDetail.setCityZoneName(rs.getString("CITY_ZONE_NAME"));
				//changed by sudhanshu
				leadDetail.setRental(rs.getString("RENTAL"));  								
				leadDetail.setOnlineCafNumber(rs.getString("ONLINE_CAF_NO")); 	
				leadDetail.setAllocatedNumber(rs.getString("ALLOCATED_NO")); 				 
				
				leadDetail.setMyOpId(rs.getString("MYOP_ID")); 				
				leadDetail.setPaymentAmt(rs.getString("PYT_AMT")); 				
				leadDetail.setTransRefNo(rs.getString("TRAN_REFNO"));
				leadDetail.setLobId(rs.getString("PRODUCT_LOB_ID"));
				leadDetail.setGeoIpCity(rs.getString("GEO_IP_CITY"));
				leadDetail.setLeadPriority(rs.getString("LEAD_PRIORITY_NAME"));
				/* Added by Parnika */
				leadDetail.setZoneCode(rs.getString("ZONE_CODE"));
				leadDetail.setCityCode(rs.getString("CITY_CODE"));
				leadDetail.setCityZoneCode(rs.getString("CITY_ZONE_CODE"));
				leadDetail.setCircleId(rs.getInt("CIRCLE_ID"));
				/* End of changes by Parnika */
				leadDetail.setZoneCode(rs.getString("ZONE_CODE"));
				leadDetail.setStatus(rs.getString("LEAD_STATUS"));
				leadDetail.setSubStatus(rs.getString("SUB_STATUS"));
				leadDetail.setSource(rs.getString("SOURCE_NAME"));
				leadDetail.setSubSource(rs.getString("SUBSOURCE_NAME"));
				leadDetail.setCompaign(rs.getString("UTM_CAMPAIGN"));
				leadDetail.setRequest_type(rs.getString("REQUEST_TYPE"));
				
				
				leadDetail.setFid(rs.getString("FID"));
				leadDetail.setCid(rs.getString("CID"));
				leadDetail.setFromPage(rs.getString("FROM_PAGE"));
				leadDetail.setService(rs.getString("SERVICE"));
				leadDetail.setKeyword(rs.getString("KEYWORD"));
				leadDetail.setLeadSubmitTime(rs.getString("LEAD_SUBMIT_TIME"));
				leadDetail.setAddress2(rs.getString("ADDRESS2"));
				leadDetail.setAppointmentTime(rs.getString("APPOINTMENT_TIME"));
				leadDetail.setQualLeadParam(rs.getString("QUAL_LEAD_PARAM"));
				leadDetail.setUserType(rs.getString("USER_TYPE"));
				leadDetail.setRental_type(rs.getString("RENTAL_TYPE"));
				leadDetail.setState(rs.getString("STATE"));
				leadDetail.setBoosterCount(rs.getString("BOOSTER_COUNT"));
				leadDetail.setFreebieTaken(rs.getString("FREEBIE_TAKEN"));
				leadDetail.setBoosterTaken(rs.getString("BOOSTER_TAKEN"));
				leadDetail.setFlag(rs.getString("FLAG"));
				leadDetail.setPrepaidNo(rs.getString("PREPAID_NUMBER"));
				leadDetail.setOffer(rs.getString("OFFER"));
				leadDetail.setDownoadLimit(rs.getString("DOWNLOAD_LIMIT"));
				leadDetail.setDeviceMrp(rs.getString("DEVICE_MRP"));
				leadDetail.setVoiceBenefit(rs.getString("VOICE_BENEFIT"));
				leadDetail.setDataQuota(rs.getString("DATA_QUOTA"));
				leadDetail.setDeviceTaken(rs.getString("DEVICE_TAKEN"));
				leadDetail.setFreebieCount(rs.getString("FREEBIE_COUNT"));
				leadDetail.setBenefit(rs.getString("BENEFIT"));
				leadDetail.setPkgDuration(rs.getString("PKG_DURATION"));
				leadDetail.setHlrNo(rs.getString("HLR_NO"));
				leadDetail.setHlrNo(rs.getString("PLAN"));
				leadDetail.setHlrNo(rs.getString("PLAN_ID"));
				leadDetail.setLatitude(rs.getString("EXTRA_PARAM3"));
				leadDetail.setLongitude(rs.getString("EXTRA_PARAM4"));
				
		//		leadDetail.setRentalString(concatRental(rs.getString("PRODUCT_LOB_ID"),rs.getString("PYT_AMT"),rs.getString("RENTAL"),rs.getString("TRAN_REFNO")));
				leadDetail.setRemarks(getQualifiedLeadRemarks(leadID));
				leadDetail.setRemarksLatest(getRemarksLatest(leadID));
			}
			logger.info("circle user success");
			return leadDetail;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("SQL Exception occured while Viewing Lead Detail"	+ "SQL Exception Message: "	+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(	" Exception occured while Viewing Lead Detail."+ " Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {
				logger.error("DAO Exception occured while Viewing Lead Detail"	+ "DAO Exception Message: "	+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	}
	public ArrayList<BulkFeasibilityDTO> listAssignedLeadsExcel(String loginID,String startDate,String endDate) throws LMSException {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		BulkFeasibilityDTO dto;
		int counter = 1;
		int recordslimitdays = Integer.parseInt(PropertyReader.getAppValue("records.limit.days"));
		//String sql = "";
		try {
			Date start,end;
			if(startDate !=null && !"".equalsIgnoreCase(startDate))
			{
				start = (Date) dateFormat.parse(startDate);
				startDate = dateFormat1.format(start);
			}
			if(endDate !=null && !"".equalsIgnoreCase(endDate))
			{
				end = (Date) dateFormat.parse(endDate);
				endDate = dateFormat1.format(end);
			}
			StringBuffer query=new StringBuffer(SQL_SELECT_ASSIGNED_LEAD_EXCEL);
			logger.info("Inside DAO");
			ArrayList<BulkFeasibilityDTO> masterList = new ArrayList<BulkFeasibilityDTO>();
			con = DBConnection.getDBConnection();
			if(startDate !=null && !"".equalsIgnoreCase(startDate))
				query.append(" AND LEAD_ASSIGNMENT_TIME >= ? ");
			else
				query.append(" AND LEAD_ASSIGNMENT_TIME >= current timestamp - "+recordslimitdays +" days ");
			if(endDate !=null && !"".equalsIgnoreCase(endDate))
				query.append(" AND LEAD_ASSIGNMENT_TIME - 1 day <= ? ");
			
			ps = con.prepareStatement(query.append("   ").toString());
			
				ps.setString(counter++, loginID);
				ps.setString(counter++, loginID);
			if(startDate !=null && !"".equalsIgnoreCase(startDate))
					ps.setDate(counter++, Utility.getSqlDateFromString(startDate, "MM/dd/yyyy"));
				if(endDate !=null && !"".equalsIgnoreCase(endDate))
					ps.setDate(counter++, Utility.getSqlDateFromString(endDate, "MM/dd/yyyy"));				
			rs = ps.executeQuery();
			//logger.info("SQL Stmt :" + query);
			//System.out.println(query);
			while (rs.next()) {
				dto = new BulkFeasibilityDTO();
				dto.setLeadID(rs.getString("LEAD_ID"));
				dto.setProductName(rs.getString("PRODUCT_NAME"));
				dto.setLeadAssignmentTime(rs.getTimestamp("LEAD_ASSIGNMENT_TIME"));
				dto.setCustomerName(rs.getString("CUSTOMER_NAME"));
				dto.setAddress(rs.getString("ADDRESS1"));
				dto.setMobileNumber(rs.getString("PROSPECT_MOBILE_NUMBER"));
				dto.setLeadClosureTime(rs.getTimestamp("EXPECTED_CLOSURE_DATE"));
				dto.setZoneName(rs.getString("ZONE_NAME"));
				dto.setCityName(rs.getString("CITY_NAME"));
				dto.setCityZoneName(rs.getString("CITY_ZONE_NAME"));
				
				dto.setRental(rs.getString("RENTAL"));
				dto.setOnlineCafNumber(rs.getString("ONLINE_CAF_NO"));
				dto.setAllocatedNumber(rs.getString("ALLOCATED_NO"));
				
				dto.setMyOpId(rs.getString("MYOP_ID")); 				//changed by sudhanshu 
				dto.setPaymentAmt(rs.getString("PYT_AMT")); 				//changed by sudhanshu  
				dto.setTransRefNo(rs.getString("TRAN_REFNO"));
				dto.setLeadCategory(rs.getString("LEAD_CATEGORY"));
				dto.setLeadPriority(rs.getString("LEAD_PRIORITY_NAME"));
				dto.setDialerRemarks(getQualifiedLeadRemarks(Long.parseLong(dto.getLeadID())));
				dto.setRequestType(rs.getString("REQUEST_TYPE"));
				/* Added by Parnika for LMS Phase 2 drop 3*/
				dto.setPinCode(rs.getString("PINCODE"));
				dto.setHlrNo(rs.getString("EXTRA_PARAMS_2"));
				
				 dto.setFid(rs.getString("FID"));
				 dto.setCid(rs.getString("CID"));
				 dto.setFromPage(rs.getString("FROM_PAGE"));
				 dto.setService(rs.getString("SERVICE"));
				 dto.setPlan(rs.getString("PLAN"));
				 dto.setKeyword(rs.getString("KEYWORD"));
				 dto.setCompany(rs.getString("COMPANY"));
				 dto.setCityZoneCode(rs.getString("CITY_ZONE_CODE"));
				 dto.setCityCode(rs.getString("CITY_CODE"));
				 dto.setZoneCOde(rs.getString("ZONE_CODE"));
				 dto.setAppointmentTime(rs.getString("APPOINTMENT_TIME"));
				 dto.setQualLeadParam(rs.getString("QUAL_LEAD_PARAM"));
				 dto.setUserType(rs.getString("USER_TYPE"));
				 dto.setPlanId(rs.getString("PLAN_ID"));
				 dto.setState(rs.getString("STATE"));
				 dto.setBoosterCount(rs.getString("BOOSTER_COUNT"));
				 dto.setFreebieTaken(rs.getString("FREEBIE_TAKEN"));
				 dto.setBoosterTaken(rs.getString("BOOSTER_TAKEN"));
				 dto.setFlag(rs.getString("FLAG"));
				 dto.setPrepaidNumber(rs.getString("PREPAID_NUMBER"));
				 dto.setOffer(rs.getString("OFFER"));
				 dto.setDownloadLimit(rs.getString("DOWNLOAD_LIMIT"));
				 dto.setDeviceMrp(rs.getString("DEVICE_MRP"));
				 dto.setVoiceBenefit(rs.getString("VOICE_BENEFIT"));
				 dto.setDataQuota(rs.getString("DATA_QUOTA"));
				 dto.setDeviceTaken(rs.getString("DEVICE_TAKEN"));
				 dto.setFreebieCount(rs.getString("FREEBIE_COUNT"));
				 dto.setBenefit(rs.getString("BENEFIT"));
				 dto.setPkgDuration(rs.getString("PKG_DURATION"));

				 //added by Nancy
				 dto.setRsuCode(rs.getString("RSU_CODE"));
				 dto.setSalesChannelCode(rs.getString("SALES_CHANNEL_CODE"));
				 dto.setLatitude(rs.getString("LATITUDE"));
				 dto.setLongitude(rs.getString("LONGITUDE"));
				 dto.setStatus(rs.getString("STATUS"));

				/* End of changes by Parnika */
				
				masterList.add(dto);
				
			}
			logger.info("circle user success");
			
			return masterList;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("SQL Exception occured while Viewing assigned Leads in Excel."+ "SQL Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Exception occured while Viewing assigned Leads in Excel."	+ " Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {
				logger.error("DAO Exception occured while Viewing assigned Leads in Excel."+ "DAO Exception Message: "+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	}

	public ArrayList<UserDto> getChannelPartnerList(ArrayList circleList,  ArrayList lobList) throws LMSException {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		UserDto dto;
		int counter = 1;
		try {
			StringBuffer query=new StringBuffer(SQL_SELECT_CIRCLE_COORDINATOR);
			logger.info("Inside DAO");
			
			if (circleList != null && circleList.size()>0 ){
				StringBuffer stringCircleIds = new StringBuffer();
				if(circleList.size()==1){
					for (Object circleId:circleList){
						stringCircleIds.append(circleId.toString());
					}
					query.append(" AND CM.CIRCLE_MSTR_ID in(");
					query.append(stringCircleIds);
					query.append(" ) ");
				}else{
					
					for (int i=0;i<circleList.size();i++){
						Object circleId=circleList.get(i);
						stringCircleIds.append(circleId.toString());
						if (i==circleList.size()-1){
							break;
						}
						stringCircleIds.append(" , ");
					}
					
					
					query.append(" AND CM.CIRCLE_MSTR_ID in(");
					query.append(stringCircleIds);
					query.append(" ) ");
				}


			}else if(lobList.size()>0 || lobList != null){
				StringBuffer stringLobIds = new StringBuffer();
				
				if(lobList.size()==1){
					for (Object lobId:lobList){
						stringLobIds.append(lobId.toString());
					}
				}else{
					
					for (int i=0;i<lobList.size();i++){
						Object lobId=lobList.get(i);
						stringLobIds.append(lobId.toString());
						if (i==lobList.size()-1){
							break;
						}
						stringLobIds.append(" , ");
					}
				}
				
				query.append(" And UMAP.LOB_ID in(" );
				query.append(stringLobIds);
				query.append(" ) ");
			}
			
			query.append(" order by  UM.KM_ACTOR_ID");
			
			ArrayList<UserDto> masterList = new ArrayList<UserDto>();
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(query.append("   ").toString());
		//		ps.setInt(1,Integer.parseInt(circleID.toString()));
			rs = ps.executeQuery();
			//logger.info("SQL Stmt :" + query);
			//System.out.println(query);
			while (rs.next()) {
				dto = new UserDto();
				dto.setUserLoginId(rs.getString("USER_LOGIN_ID"));
				dto.setUserFname(rs.getString("USER_FNAME")+" "+rs.getString("USER_MNAME")+" "+rs.getString("USER_LNAME")+" ( "+rs.getString("USER_LOGIN_ID")+" , "+rs.getString("KM_ACTOR_NAME")+" ) ");
				dto.setKmActorName(rs.getString("KM_ACTOR_NAME"));
				masterList.add(dto);

			}
			logger.info("circle user success");
			return masterList;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("SQL Exception occured while Viewing assigned Leads."+ "SQL Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Exception occured while Viewing assigned Leads."	+ " Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {
				logger.error("DAO Exception occured while Viewing assigned Leads."+ "DAO Exception Message: "+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	}
//Added by Sudhanshu
	public ArrayList<UserDto> getZonalCoordinatorList(ArrayList circleList,  ArrayList lobList) throws LMSException {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		UserDto dto;
		int counter = 1;
		try {
			StringBuffer query=new StringBuffer(SQL_SELECT_ZONAL_COORDINATOR);
			logger.info("Inside getZonalCoordinatorList");
			
			if (circleList != null && circleList.size()>0 ){
				StringBuffer stringCircleIds = new StringBuffer();
				if(circleList.size()==1){
					for (Object circleId:circleList){
						stringCircleIds.append(circleId.toString());
					}
					query.append(" AND CM.CIRCLE_MSTR_ID in(");
					query.append(stringCircleIds);
					query.append(" ) ");
				}else{
					
					for (int i=0;i<circleList.size();i++){
						Object circleId=circleList.get(i);
						stringCircleIds.append(circleId.toString());
						if (i==circleList.size()-1){
							break;
						}
						stringCircleIds.append(" , ");
					}
					
					
					query.append(" AND CM.CIRCLE_MSTR_ID in(");
					query.append(stringCircleIds);
					query.append(" ) ");
				}


			}else if(lobList.size()>0 || lobList != null){
				StringBuffer stringLobIds = new StringBuffer();
				
				if(lobList.size()==1){
					for (Object lobId:lobList){
						stringLobIds.append(lobId.toString());
					}
				}else{
					
					for (int i=0;i<lobList.size();i++){
						Object lobId=lobList.get(i);
						stringLobIds.append(lobId.toString());
						if (i==lobList.size()-1){
							break;
						}
						stringLobIds.append(" , ");
					}
				}
				
				query.append(" And UMAP.LOB_ID in(" );
				query.append(stringLobIds);
				query.append(" ) ");
			}
			
			query.append(" order by  UM.KM_ACTOR_ID");
			
			ArrayList<UserDto> masterList = new ArrayList<UserDto>();
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(query.append("   ").toString());
		//		ps.setInt(1,Integer.parseInt(circleID.toString()));
			rs = ps.executeQuery();
			//logger.info("SQL Stmt :" + query);
			//System.out.println(query);
			while (rs.next()) {
				dto = new UserDto();
				dto.setUserLoginId(rs.getString("USER_LOGIN_ID"));
				dto.setUserFname(rs.getString("USER_FNAME")+" "+rs.getString("USER_MNAME")+" "+rs.getString("USER_LNAME")+" ( "+rs.getString("USER_LOGIN_ID")+" , "+rs.getString("KM_ACTOR_NAME")+" ) ");
				dto.setKmActorName(rs.getString("KM_ACTOR_NAME"));
				masterList.add(dto);

			}
			logger.info("circle user success");
			return masterList;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("SQL Exception occured while Viewing assigned Leads."+ "SQL Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Exception occured while Viewing assigned Leads."	+ " Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {
				logger.error("DAO Exception occured while Viewing assigned Leads."+ "DAO Exception Message: "+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	}	
	
	 private String concatRental(String lobId, String paymentAmt, String rental, String transRefNo){
		
		StringBuffer rentalStr = null;
	if (lobId!=null){
			
		rentalStr = new StringBuffer(rental+"");
		
		switch(Integer.parseInt(lobId)){
		
		case 1: rentalStr.append(" Security Deposit:").append(paymentAmt);
				break;
		case 2:	rentalStr.append(" Activation Fee:").append(paymentAmt);
				break;			
		case 23: 	rentalStr.append(" Dongle MRP:").append(paymentAmt);
				break;
		case 24: rentalStr.append(" Dongle MRP:").append(paymentAmt);
				break;
		default: rentalStr.append(" Amount:").append(paymentAmt);
				break;
		}
	
	}
	rentalStr.append(" TransRefNo:").append(transRefNo);
	return rentalStr.toString();
		
	}

	 private String getRemarksLatest( long leadId) throws DAOException{
			
			logger.info("Inside getQualifiedLeadRemarks");
			Connection connection= null;
			ResultSet rst = null;
			PreparedStatement pst = null;
			int counter=1;
			String remarks= null;
			StringBuffer query=new StringBuffer(SQL_QUALIFIED_LEAD_REMARKS_LATEST);
			try {
				connection = DBConnection.getDBConnection();
				pst = connection.prepareStatement(query.append("   ").toString());		
				pst.setLong(counter++, leadId);
				rst = pst.executeQuery();
				while(rst.next()){
					remarks = rst.getString("REMARKS");
				}
		
			} catch (SQLException e) {		
				e.printStackTrace();
				logger.error("SQL Exception occured in getQualifiedLeadRemarks"	+ "SQL Exception Message: "	+ e.getMessage());
				throw new DAOException(e.getMessage());
				
			}finally{
				try {
					DBConnection.releaseResources(connection, pst, rst);
				} catch (DAOException e) {
					// TODO Auto-generated catch block
					logger.error("DAO Exception occured in getQualifiedLeadRemarks"	+ "DAO Exception Message: "	+ e.getMessage());
				}
			}
			logger.info("Exiting getQualifiedLeadRemarks ");
			return remarks;
			
		}
	
	private String getQualifiedLeadRemarks( long leadId) throws DAOException{
		
		logger.info("Inside getQualifiedLeadRemarks");
		Connection connection= null;
		ResultSet rst = null;
		PreparedStatement pst = null;
		int counter=1;
		String remarks= null;
		StringBuffer query=new StringBuffer(SQL_QUALIFIED_LEAD_REMARKS);
		try {
			connection = DBConnection.getDBConnection();
			pst = connection.prepareStatement(query.append("   ").toString());		
			pst.setLong(counter++, leadId);
			rst = pst.executeQuery();
			while(rst.next()){
				remarks = rst.getString("REMARKS");
			}
	
		} catch (SQLException e) {		
			e.printStackTrace();
			logger.error("SQL Exception occured in getQualifiedLeadRemarks"	+ "SQL Exception Message: "	+ e.getMessage());
			throw new DAOException(e.getMessage());
			
		}finally{
			try {
				DBConnection.releaseResources(connection, pst, rst);
			} catch (DAOException e) {
				// TODO Auto-generated catch block
				logger.error("DAO Exception occured in getQualifiedLeadRemarks"	+ "DAO Exception Message: "	+ e.getMessage());
			}
		}
		logger.info("Exiting getQualifiedLeadRemarks ");
		return remarks;
		
	}
	/* End of changes by Sudhanshu */
	
	/* Added by Parnika for LMS Phase 2 */
	public ArrayList<UserDto> getChannelPartnerListForLead(ArrayList circleList, LeadDetailDTO detailDTO) throws LMSException {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		UserDto dto;
		int counter = 1;
		ResultSet rs1 = null;
		PreparedStatement ps1 = null;
		String tempZoneVal = null;
		String tempCityZoneCode = null;
		try {
			StringBuffer query=new StringBuffer("");
			logger.info("Inside DAO");
			con = DBConnection.getDBConnection();
			if (detailDTO.getZoneCode() != null && !detailDTO.getZoneCode().equals("")) {
				StringBuffer query1=new StringBuffer("SELECT ZONE_CODE, ZONE_NAME FROM ZONE_MSTR where ucase(zone_name)='DUMMY' and ZONE_CODE = ?    ");														
				ps1 = con.prepareStatement(query1.toString());
				ps1.setString(1, detailDTO.getZoneCode());
				rs1 = ps1.executeQuery();
				if(rs1.next()){
					tempZoneVal = rs1.getString("ZONE_CODE");
				}
			}
				if (tempZoneVal == null && detailDTO.getZoneCode() != null && !detailDTO.getZoneCode().equals("")) {
					query= query.append("SELECT KA.KM_ACTOR_NAME,A.USER_ID, A.USER_LOGIN_ID, A.USER_FNAME, A.USER_MNAME, A.USER_LNAME FROM USER_MSTR A, USER_MAPPING B ,KM_ACTORS KA , CIRCLE_MSTR CM where A.USER_LOGIN_ID =B.USER_LOGIN_ID AND A.STATUS='A' AND B.CIRCLE_ID = CM.CIRCLE_ID  AND B.LOB_ID = CM.LOB_ID  and A.KM_ACTOR_ID = KA.KM_ACTOR_ID AND A.KM_ACTOR_ID="+Constants.ZONAL_COORDINATOR_ACTOR+" and B.ZONE_CODE= '"+detailDTO.getZoneCode()+"' AND B.LOB_ID= "+detailDTO.getLobId());
				
				} else if (detailDTO.getCityZoneCode() != null && !detailDTO.getCityZoneCode().equals("")) {
						tempCityZoneCode = detailDTO.getCityZoneCode().trim();
						query= query.append("SELECT KA.KM_ACTOR_NAME,A.USER_ID, A.USER_LOGIN_ID, A.USER_FNAME, A.USER_MNAME, A.USER_LNAME FROM  USER_MSTR A, USER_MAPPING B, KM_ACTORS KA , CIRCLE_MSTR CM  where A.USER_LOGIN_ID =B.USER_LOGIN_ID AND A.STATUS='A' AND B.CIRCLE_ID = CM.CIRCLE_ID  AND B.LOB_ID = CM.LOB_ID  and A.KM_ACTOR_ID = KA.KM_ACTOR_ID AND A.KM_ACTOR_ID="+Constants.ZONAL_COORDINATOR_ACTOR+" and B.CITY_ZONE_CODE= '"+tempCityZoneCode+"' AND B.LOB_ID= "+detailDTO.getLobId());
					} else {
						query= query.append("SELECT KA.KM_ACTOR_NAME,A.USER_ID, A.USER_LOGIN_ID, A.USER_FNAME, A.USER_MNAME, A.USER_LNAME FROM  USER_MSTR A, USER_MAPPING B, KM_ACTORS KA , CIRCLE_MSTR CM  where A.USER_LOGIN_ID =B.USER_LOGIN_ID AND A.STATUS='A' AND B.CIRCLE_ID = CM.CIRCLE_ID  AND B.LOB_ID = CM.LOB_ID  and A.KM_ACTOR_ID = KA.KM_ACTOR_ID AND A.KM_ACTOR_ID="+Constants.CIRCLE_COORDINATOR_ACTOR+" and B.CIRCLE_ID= "+ detailDTO.getCircleId()+"  AND B.LOB_ID="+detailDTO.getLobId());
					}
			
			query.append(" order by  A.KM_ACTOR_ID");
			
			ArrayList<UserDto> masterList = new ArrayList<UserDto>();
			
			ps = con.prepareStatement(query.append("   ").toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				dto = new UserDto();
				dto.setUserLoginId(rs.getString("USER_LOGIN_ID"));
				dto.setUserFname(rs.getString("USER_FNAME")+" "+rs.getString("USER_MNAME")+" "+rs.getString("USER_LNAME")+" ( "+rs.getString("USER_LOGIN_ID")+" , "+rs.getString("KM_ACTOR_NAME")+" ) ");
				dto.setKmActorName(rs.getString("KM_ACTOR_NAME"));
				masterList.add(dto);

			}
			logger.info("circle user success");
			return masterList;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("SQL Exception occured while getChannelPartnerListForLead."+ "SQL Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Exception occured while getChannelPartnerListForLead."	+ " Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
				DBConnection.releaseResources(con, ps1, rs1);
			} catch (Exception e) {
				logger.error("DAO Exception occured while getChannelPartnerListForLead."+ "DAO Exception Message: "+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	}
	
	/* End of changes by sud */
	public ArrayList<UserDto> getChannelPartnerListForLeadImproved(ArrayList circleList, LeadDetailDTO detailDTO,ArrayList lobList) throws LMSException {
		Connection con = null;
		
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
	
		UserDto dto;

		int zonalCordsForZone = 0; //zonal coordinator for the given zone in the lead 
		int zonalCordsForCity = 0; // Zonal Coordinator for the given city zone in the lead
		
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		
		StringBuffer query1=null;
		StringBuffer query2=null;
		StringBuffer query3=null;
	
		
		StringBuffer finalQuery = null;
		
		String tempCityZoneCode = null;
		Boolean isDummyZone = false;
		ArrayList<UserDto> masterList = null;
		
		try {
			StringBuffer query=new StringBuffer("");
	
			logger.info("Inside DAO");
			con = DBConnection.getDBConnection();
				
			if (detailDTO.getZoneCode() != null && !detailDTO.getZoneCode().trim().equals("")) {
				query1=new StringBuffer("");
				query1.append("SELECT ZONE_CODE, ZONE_NAME FROM ZONE_MSTR where ucase(zone_name)='DUMMY' and ZONE_CODE = ?    ");														
				ps1 = con.prepareStatement(query1.toString());
				ps1.setString(1, detailDTO.getZoneCode());
				rs1 = ps1.executeQuery();
				
					if(rs1.next()){
						isDummyZone = true;
					}
				ps1= null;
				rs1= null;
				
					if(!isDummyZone){
						query2 = new StringBuffer("");
						query2.append("SELECT count(*) as count FROM USER_MSTR A, USER_MAPPING B ,KM_ACTORS KA , CIRCLE_MSTR CM where A.USER_LOGIN_ID =B.USER_LOGIN_ID AND A.STATUS='A' AND B.CIRCLE_ID = CM.CIRCLE_ID  AND B.LOB_ID = CM.LOB_ID  and A.KM_ACTOR_ID = KA.KM_ACTOR_ID AND A.KM_ACTOR_ID in("+Constants.ZONAL_COORDINATOR_ACTOR+" ) and B.ZONE_CODE= '"+detailDTO.getZoneCode()+"' AND B.LOB_ID= "+detailDTO.getLobId());
						query2.append("   ");
						ps1 = con.prepareStatement(query2.toString());
						rs1= ps1.executeQuery();
						while(rs1.next()){
							zonalCordsForZone = rs1.getInt(1);//if zonalCordsForZone is >0 then zonal coordinators exist in the zone
						}
						
						if(zonalCordsForZone >= 1){
							 finalQuery= query.append("SELECT KA.KM_ACTOR_NAME,A.USER_ID, A.USER_LOGIN_ID, A.USER_FNAME, A.USER_MNAME, A.USER_LNAME FROM USER_MSTR A, USER_MAPPING B ,KM_ACTORS KA , CIRCLE_MSTR CM where A.USER_LOGIN_ID =B.USER_LOGIN_ID AND A.STATUS='A' AND B.CIRCLE_ID = CM.CIRCLE_ID  AND B.LOB_ID = CM.LOB_ID  and A.KM_ACTOR_ID = KA.KM_ACTOR_ID AND A.KM_ACTOR_ID in("+Constants.ZONAL_COORDINATOR_ACTOR+" ) and B.ZONE_CODE= '"+detailDTO.getZoneCode()+"' AND B.LOB_ID= "+detailDTO.getLobId());
						//finalQuerry gives the number of zonal cords in that zone
								finalQuery.append(" order by  A.KM_ACTOR_ID");
								
								 masterList = new ArrayList<UserDto>();
								
								ps = con.prepareStatement(query.append("   ").toString());
								rs = ps.executeQuery();
								while (rs.next()) {
									dto = new UserDto();
									dto.setUserLoginId(rs.getString("USER_LOGIN_ID"));
									dto.setUserFname(rs.getString("USER_FNAME")+" "+rs.getString("USER_MNAME")+" "+rs.getString("USER_LNAME")+" ( "+rs.getString("USER_LOGIN_ID")+" , "+rs.getString("KM_ACTOR_NAME")+" ) ");
									dto.setKmActorName(rs.getString("KM_ACTOR_NAME"));
									masterList.add(dto);

								}
						}
						
						ps1= null;
						rs1= null;
					}//check		
				}if ((zonalCordsForZone<1)&&(detailDTO.getCityZoneCode() != null && !detailDTO.getCityZoneCode().trim().equals(""))) {
					tempCityZoneCode = detailDTO.getCityZoneCode().trim();
					query3 = new StringBuffer("");
					query3.append("SELECT COUNT(*) FROM  USER_MSTR A, USER_MAPPING B, KM_ACTORS KA , CIRCLE_MSTR CM  where A.USER_LOGIN_ID =B.USER_LOGIN_ID AND A.STATUS='A' AND B.CIRCLE_ID = CM.CIRCLE_ID  AND B.LOB_ID = CM.LOB_ID  and A.KM_ACTOR_ID = KA.KM_ACTOR_ID AND A.KM_ACTOR_ID in("+Constants.ZONAL_COORDINATOR_ACTOR+") and B.CITY_ZONE_CODE= '"+tempCityZoneCode+"' AND B.LOB_ID= "+detailDTO.getLobId());
					query3.append("   ");
					ps1 = con.prepareStatement(query3.toString());
					rs1= ps1.executeQuery();
					while(rs1.next()){
						zonalCordsForCity = rs1.getInt(1);//if count is >0 then zonal cordinators exist in the cityZone
					}
					
					if(zonalCordsForCity >= 1){
						 finalQuery= query.append("SELECT KA.KM_ACTOR_NAME,A.USER_ID, A.USER_LOGIN_ID, A.USER_FNAME, A.USER_MNAME, A.USER_LNAME FROM  USER_MSTR A, USER_MAPPING B, KM_ACTORS KA , CIRCLE_MSTR CM  where A.USER_LOGIN_ID =B.USER_LOGIN_ID AND A.STATUS='A' AND B.CIRCLE_ID = CM.CIRCLE_ID  AND B.LOB_ID = CM.LOB_ID  and A.KM_ACTOR_ID = KA.KM_ACTOR_ID AND A.KM_ACTOR_ID in("+Constants.ZONAL_COORDINATOR_ACTOR+") and B.CITY_ZONE_CODE= '"+tempCityZoneCode+"' AND B.LOB_ID= "+detailDTO.getLobId());
						//finalQuerry gives the number of zonal cords in that City zone
						 
							finalQuery.append(" order by  A.KM_ACTOR_ID");
							
							 masterList = new ArrayList<UserDto>();
							
							ps = con.prepareStatement(query.append("   ").toString());
							rs = ps.executeQuery();
							while (rs.next()) {
								dto = new UserDto();
								dto.setUserLoginId(rs.getString("USER_LOGIN_ID"));
								dto.setUserFname(rs.getString("USER_FNAME")+" "+rs.getString("USER_MNAME")+" "+rs.getString("USER_LNAME")+" ( "+rs.getString("USER_LOGIN_ID")+" , "+rs.getString("KM_ACTOR_NAME")+" ) ");
								dto.setKmActorName(rs.getString("KM_ACTOR_NAME"));
								masterList.add(dto);

							}
					}
				
				}
				
				
				if	(((detailDTO.getZoneCode() == null || detailDTO.getZoneCode().equals("")) && (detailDTO.getCityZoneCode() == null || detailDTO.getCityZoneCode().equals("")))///both zone and cityZone are not available in the lead
							||(isDummyZone==true && ((detailDTO.getCityZoneCode() == null || detailDTO.getCityZoneCode().equals(""))||(zonalCordsForCity<1)))//if zone is dummy and (city zone = null or zonalCordsForCity=0)
							||((zonalCordsForZone<1)&&(zonalCordsForCity<1))){ //if both zonal and city zonal coordinators don't exist
					 // masterList = new ArrayList<UserDto>();
					 masterList=new ArrayList<UserDto>();//getZonalCoordinatorList(circleList, lobList);
					 StringBuffer listOfUsers = new StringBuffer(SQL_SELECT_ZONAL_COORDINATORS);
					 listOfUsers.append("AND CM.CIRCLE_ID in("+detailDTO.getCircleId() +")  And UMAP.LOB_ID in("+detailDTO.getLobId()+")");
					 listOfUsers.append(" order by  UM.KM_ACTOR_ID");
					 ps = con.prepareStatement(listOfUsers.append("   ").toString());
						rs = ps.executeQuery();
						while (rs.next()) {
							dto = new UserDto();
							dto.setUserLoginId(rs.getString("USER_LOGIN_ID"));
							dto.setUserFname(rs.getString("USER_FNAME")+" "+rs.getString("USER_MNAME")+" "+rs.getString("USER_LNAME")+" ( "+rs.getString("USER_LOGIN_ID")+" , "+rs.getString("KM_ACTOR_NAME")+" ) ");
							dto.setKmActorName(rs.getString("KM_ACTOR_NAME"));
							masterList.add(dto);
						}
				
				}
				
				StringBuffer getCircleCoordinators = new StringBuffer(SELECT_CIRCLE_CO0RDINATORS);
				getCircleCoordinators.append("AND CM.CIRCLE_ID in("+detailDTO.getCircleId() +")  And UMAP.LOB_ID in("+detailDTO.getLobId()+")");
				getCircleCoordinators.append(" order by  UM.KM_ACTOR_ID");
				
				ps2 = con.prepareStatement(getCircleCoordinators.append("   ").toString());
					rs2 = ps2.executeQuery();
					while (rs2.next()) {
						dto = new UserDto();
						dto.setUserLoginId(rs2.getString("USER_LOGIN_ID"));
						dto.setUserFname(rs2.getString("USER_FNAME")+" "+rs2.getString("USER_MNAME")+" "+rs2.getString("USER_LNAME")+" ( "+rs2.getString("USER_LOGIN_ID")+" , "+rs2.getString("KM_ACTOR_NAME")+" ) ");
						dto.setKmActorName(rs2.getString("KM_ACTOR_NAME"));
						masterList.add(dto);
					}
				
				
				logger.info("circle user success");
			
			return masterList;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("SQL Exception occured while getChannelPartnerListForLead."+ "SQL Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Exception occured while getChannelPartnerListForLead."	+ " Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
				DBConnection.releaseResources(con, ps1, rs1);
				DBConnection.releaseResources(con, ps2, rs2);
			} catch (Exception e) {
				logger.error("DAO Exception occured while getChannelPartnerListForLead."+ "DAO Exception Message: "+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	}
	//added by Sudhanshu 
	public Boolean closeTheLeadSms(ArrayList masterList) throws LMSException {
		Connection con = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		int counter = 1;
		ResultSet rs = null;		
		boolean flag = false;
		int count = 0;
		Leads lead = null;
		StringBuffer query= null;
		int subStatus = 0;
		
		try {
			con = DBConnection.getDBConnection();
			//con.setAutoCommit(false);
			for(int i = 0;i<masterList.size();i++){
				
				counter = 1;
				lead = new Leads();
				lead = (Leads)masterList.get(i);
				query= new StringBuffer();
			logger.info("Inside DAO");
			int status = Integer.parseInt(lead.getStatus());
			LeadRegistrationDao dao= LeadRegistrationDaoImpl.leadRegistrationDaoInstance();
			
			int leadStatus = 0;
			
			if(lead.getLeadID()!=null){
				
				List<LeadDetailsDTO> LeadDetailsDTOList = dao.getLeadDetails(Long.parseLong(lead.getLeadID()));
				LeadDetailsDTO leadDetailsDTO = LeadDetailsDTOList.get(0);
				leadStatus = leadDetailsDTO.getLeadStatusId();
			}
			
			/* Added by Parnika if sub-status is not captured , main status is added into sub status*/
			if(lead.getSubStatus() == null || lead.getSubStatus().trim().equals("") && (LMSStatusCodes.WON != status || LMSStatusCodes.LOST != status)){
				subStatus = Integer.parseInt(lead.getStatus());
			}
			else if(lead.getSubStatus() != null && !(lead.getSubStatus().trim().equals(""))) {
				subStatus = Integer.parseInt(lead.getSubStatus());
			}
			/* End of changes by Parnika */
			
			if(LMSStatusCodes.WON == status || LMSStatusCodes.LOST == status){
			query.append(SQL_UPDATE_LEAD_STATUS);
		
			/* Modified By Parnika */
			query.append(" SET  LEAD_STATUS_ID = ?, LEAD_SUB_SUB_STATUS_ID =? ,REMARKS = ?, LEAD_SUB_STATUS_ID = ? ,LEAD_CLOSURE_TIME = CURRENT TIMESTAMP,CAF = ? , UPDATED_DT = CURRENT TIMESTAMP, UPDATED_BY = ?   WHERE LEAD_ID = ? "); 	//changed by sudhanshu
			/* End of changes by Parnika */
			ps1 = con.prepareStatement(query.append("   ").toString());
				
				
				if(LMSStatusCodes.WON == status ){
					ps1.setInt(counter++, status);
					ps1.setInt(counter++, LMSStatusCodes.WON_SMS);
				}else if(LMSStatusCodes.LOST == status){
					ps1.setInt(counter++, status);
					ps1.setInt(counter++, LMSStatusCodes.LOST_SMS);
				}
				
				ps1.setString(counter++, lead.getRemarks());
				ps1.setInt(counter++, subStatus);   	//changed by sudhanshu
				ps1.setString(counter++, lead.getCafNumber());
				ps1.setString(counter++, lead.getUpdatedBy()); 	//changed by sudhanshu
				ps1.setString(counter++, lead.getLeadID());
				if(leadStatus!= LMSStatusCodes.WON && leadStatus!= LMSStatusCodes.LOST ){
					count = ps1.executeUpdate();
				}
			}

			
			logger.info("lead status updated success");
				count = 0;
				counter = 1;
				StringBuffer dmlStatement = new StringBuffer(SQL_INSERT_LEAD_TRANSACTION);
				if(LMSStatusCodes.WON == status || LMSStatusCodes.LOST == status)
					dmlStatement.append(",?,? ");
				else{
					dmlStatement.append(",LEAD_STATUS_ID,LEAD_SUB_SUB_STATUS_ID ");
				}
				dmlStatement.append(",LEAD_ASSIGNED_PRIMARY_USER,?,CURRENT TIMESTAMP,EXPECTED_CLOSURE_DATE,PRIMARY_AUTH,?,?,?,LEAD_PRODUCT_ID ,(SELECT CLIENT_IP FROM KM_LOGIN_DATA WHERE USER_LOGIN_ID = ?  ORDER BY LOGIN_TIME DESC  FETCH FIRST ROW ONLY ) FROM LEAD_TRANSACTION ");//changed by sudhanshu
				dmlStatement.append(" WHERE LEAD_ID = ? AND LEAD_STATUS_ID NOT IN (" +LMSStatusCodes.WON+","+LMSStatusCodes.LOST+ ") AND SUB_STATUS_ID <> 210 AND SUB_STATUS_ID <> ? AND LEAD_STATUS_ID IN ( "+LMSStatusCodes.ASSIGNED+","+LMSStatusCodes.REASSIGNED+" )  ");

				dmlStatement.append(" ORDER BY TRANSACTION_TIME DESC FETCH FIRST ROW ONLY   ");
				
				ps2 = con.prepareStatement(dmlStatement.toString());
				if(LMSStatusCodes.WON == status ){
					ps2.setInt(counter++, status);
					ps2.setInt(counter++, LMSStatusCodes.WON_SMS);
				}else if(LMSStatusCodes.LOST == status){
					ps2.setInt(counter++, status);
					ps2.setInt(counter++, LMSStatusCodes.LOST_SMS);
				}
					
				ps2.setString(counter++, lead.getRemarks());
/*				if(LMSStatusCodes.WON == status || LMSStatusCodes.LOST == status){
					ps2.setInt(counter++, Integer.parseInt(lead.getSubStatus()));
				}					
				else{
					ps2.setInt(counter++, status);
				}*/
					
				ps2.setInt(counter++,subStatus);
				ps2.setString(counter++, lead.getUpdatedBy());
				ps2.setString(counter++, lead.getUdId());
				ps2.setString(counter++, lead.getUpdatedBy());
				ps2.setString(counter++, lead.getLeadID());
				ps2.setInt(counter++ , LMSStatusCodes.DIALER_SECOND_CALL);
				if(leadStatus!= LMSStatusCodes.WON && leadStatus!= LMSStatusCodes.LOST ){
					
					count = ps2.executeUpdate();
					if(count == 1){
						flag = true;
					}
				}
				
				
				ps1 = null;
				ps2 = null;
				
			}
			return flag;
			
		} catch (SQLException e) {
			/*try{
				con.rollback();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}*/
			e.printStackTrace();
			logger.error("SQL Exception occured while Closing the lead."+ "SQL Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Exception occured while Closing the lead."	+ " Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps1, rs);
				DBConnection.releaseResources(con, ps2, rs);
			} catch (Exception e) {
				logger.error("DAO Exception occured while Closing the lead."+ "DAO Exception Message: "+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	}
	
	public String getUserID(String mobleNo)throws LMSException{
		
		Connection con = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;		
		StringBuffer query= null;
		String userId = null;


		try {
			
			con = DBConnection.getDBConnection();
			
			query= new StringBuffer();
			logger.info("Inside DAO");
			query.append(GET_USERID_FROM_USER_MSTR);	
			ps1 = con.prepareStatement(query.append("   ").toString());
				ps1.setString(1, mobleNo);
				rs = ps1.executeQuery();
			while(rs.next()){
				userId = rs.getString("USER_LOGIN_ID");
			}
			
			if (userId==null || userId.equals("")){
			
				query = new StringBuffer("");
				query.append(GET_USERID_FROM_USER_MSTR_ADDITIONAL);	
				ps1=null;
				ps1 = con.prepareStatement(query.append("   ").toString());
					ps1.setString(1, mobleNo);
					rs = ps1.executeQuery();
					while(rs.next()){
						userId = rs.getString("USER_LOGIN_ID");
					}
			}
			
		} catch (SQLException e) {
			throw new LMSException("SQLException: " + e.getMessage(), e);
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new LMSException("Exception: " + e.getMessage(), e);
		}
		return userId;
		
	}


	public String  closeTheFinalLead(ArrayList<Leads> masterList) throws LMSException {
		

		Connection con = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		int counter = 1;
		ResultSet rs = null;		
		boolean flag = false;
		int count = 0;
		Leads lead = null;
		StringBuffer query= null;
		int subStatus = 0;
		String msg = "success";
		
		try {
			con = DBConnection.getDBConnection();
			for(int i = 0;i<masterList.size();i++){
				
				counter = 1;
				lead = new Leads();
				lead = (Leads)masterList.get(i);
				query= new StringBuffer();
			logger.info("Inside DAO");
			int status = Integer.parseInt(lead.getStatus());
			
			
			/* Added by Parnika if sub-status is not captured , main status is added into sub status*/
			if(lead.getSubStatus() == null || lead.getSubStatus().trim().equals("") && (LMSStatusCodes.WON != status || LMSStatusCodes.LOST != status)){
				subStatus = Integer.parseInt(lead.getStatus());
			}
			else if(lead.getSubStatus() != null && !(lead.getSubStatus().trim().equals(""))) {
				subStatus = Integer.parseInt(lead.getSubStatus());
			}
			/* End of changes by Parnika */
			
			if(LMSStatusCodes.WON == status || LMSStatusCodes.LOST == status){
			query.append(SQL_UPDATE_LEAD_STATUS);
		
			/* Modified By Parnika */
			query.append(" SET  LEAD_STATUS_ID = ?, LEAD_SUB_SUB_STATUS_ID =? ,REMARKS = ?, LEAD_SUB_STATUS_ID = ? ,LEAD_CLOSURE_TIME = CURRENT TIMESTAMP,CAF = ? , UPDATED_DT = CURRENT TIMESTAMP, UPDATED_BY = ?   WHERE LEAD_ID = ? "); 	//changed by sudhanshu
			/* End of changes by Parnika */
			ps1 = con.prepareStatement(query.append("   ").toString());
				
				
				if(LMSStatusCodes.WON == status ){
					ps1.setInt(counter++, status);
					ps1.setInt(counter++, LMSStatusCodes.WON_CLOSE_LOOPING);
				}else if(LMSStatusCodes.LOST == status){
					ps1.setInt(counter++, status);
					ps1.setInt(counter++, LMSStatusCodes.LOST_CLOSE_LOOPING);
				}
				
				ps1.setString(counter++, lead.getRemarks());
				ps1.setInt(counter++, subStatus);   	//changed by sudhanshu
				ps1.setString(counter++, lead.getCafNumber());
				ps1.setString(counter++, lead.getUpdatedBy()); 	//changed by sudhanshu
				ps1.setString(counter++, lead.getLeadID());
				
			count = ps1.executeUpdate();
			}

			
			
				count = 0;
				counter = 1;
				StringBuffer dmlStatement = new StringBuffer(SQL_INSERT_LEAD_TRANSACTION);
				if(LMSStatusCodes.WON == status || LMSStatusCodes.LOST == status)
					dmlStatement.append(",?,? ");
				else{
					dmlStatement.append(",LEAD_STATUS_ID,LEAD_SUB_SUB_STATUS_ID ");
				}
				dmlStatement.append(",LEAD_ASSIGNED_PRIMARY_USER,?,CURRENT TIMESTAMP,EXPECTED_CLOSURE_DATE,PRIMARY_AUTH,?,?,?,LEAD_PRODUCT_ID ,(SELECT CLIENT_IP FROM KM_LOGIN_DATA WHERE USER_LOGIN_ID = ?  ORDER BY LOGIN_TIME DESC  FETCH FIRST ROW ONLY ) FROM LEAD_TRANSACTION ");//changed by sudhanshu
				dmlStatement.append(" WHERE LEAD_ID = ? AND SUB_STATUS_ID <> 210 AND SUB_STATUS_ID <> ? AND LEAD_STATUS_ID IN ( "+LMSStatusCodes.ASSIGNED+","+LMSStatusCodes.REASSIGNED+" )  ");

				dmlStatement.append(" ORDER BY TRANSACTION_TIME DESC FETCH FIRST ROW ONLY   ");
				
				ps2 = con.prepareStatement(dmlStatement.toString());
				if(LMSStatusCodes.WON == status ){
					ps2.setInt(counter++, status);
					ps2.setInt(counter++, LMSStatusCodes.WON_CLOSE_LOOPING);
				}else if(LMSStatusCodes.LOST == status){
					ps2.setInt(counter++, status);
					ps2.setInt(counter++, LMSStatusCodes.LOST_CLOSE_LOOPING);
				}
					
				ps2.setString(counter++, lead.getRemarks());
/*				if(LMSStatusCodes.WON == status || LMSStatusCodes.LOST == status){
					ps2.setInt(counter++, Integer.parseInt(lead.getSubStatus()));
				}					
				else{
					ps2.setInt(counter++, status);
				}*/
					
				ps2.setInt(counter++,subStatus);
				ps2.setString(counter++, lead.getUpdatedBy());
				ps2.setString(counter++, lead.getUdId());
				ps2.setString(counter++, lead.getUpdatedBy());
				ps2.setString(counter++, lead.getLeadID());
				ps2.setInt(counter++ , LMSStatusCodes.DIALER_SECOND_CALL);
				count = ps2.executeUpdate();
				if(count == 1){
					flag = true;
				}
				
				ps1 = null;
				ps2 = null;
				
			}
			return msg;
			
		} catch (SQLException e) {
			/*try{
				con.rollback();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}*/
			e.printStackTrace();
			logger.error("SQL Exception occured while Closing the lead."+ "SQL Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Exception occured while Closing the lead."	+ " Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps1, rs);
				DBConnection.releaseResources(con, ps2, rs);
			} catch (Exception e) {
				logger.error("DAO Exception occured while Closing the lead."+ "DAO Exception Message: "+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	
		
	}




public ArrayList<UserDto> getUserListForNewProduct(LeadDetailDTO leadDetails,String userLoginId) throws LMSException
 	{
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		UserDto dto;
		int counter = 1;
		try {
			ArrayList<UserDto> masterList = new ArrayList<UserDto>();
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement("select unique um.USER_LOGIN_ID,upc.AGENT_NAME from USER_MSTR um,NEW_PROD_CONFIG upc where um.USER_LOGIN_ID=upc.OLMID and upc.CIRCLE_ID=? and LOB=? and um.STATUS='A' and um.USER_LOGIN_ID <> ?  ");
		    ps.setInt(2,Integer.parseInt(leadDetails.getLobId()));
		    ps.setInt(1,leadDetails.getCircleId());
		    ps.setString(3,userLoginId);
		    
			rs = ps.executeQuery();
			
			//ps1 = con.prepareStatement("select distinct(OLMID),AGENT_NAME from NEW_PROD_CONFIG where LOB=? and  CIRCLE_ID=? and OLMID=?");
			//logger.info("SQL Stmt :" + query);
			//System.out.println(query);
			while (rs.next()) {
				dto = new UserDto();
				/*ps1.setInt(1,Integer.parseInt(leadDetails.getLobId()));
			    ps1.setInt(2,leadDetails.getCircleId());
			    ps1.setString(3,rs.getString("USER_LOGIN_ID"));
				rs1=ps1.executeQuery();*/
				dto.setUserLoginId(rs.getString("USER_LOGIN_ID"));
				dto.setUserFname(rs.getString("AGENT_NAME")+" ( "+rs.getString("USER_LOGIN_ID")+" )");
				//dto.setKmActorName(rs.getString("KM_ACTOR_NAME"));
				masterList.add(dto);
			

			}
			logger.info("circle list for new prouct");
			return masterList;			
/*			
	========		
			query.append(" WHERE CIRCLE_ID=(SELECT CIRCLE_ID FROM USER_MSTR WHERE USER_LOGIN_ID = ?) AND USER_LOGIN_ID <> ? AND KM_ACTOR_ID IN(4,5,6,9,10) ");
			ArrayList<UserDto> masterList = new ArrayList<UserDto>();
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(query.append("   ").toString());
				ps.setString(counter++, loginID);
				ps.setString(counter++, loginID);
			rs = ps.executeQuery();
			//logger.info("SQL Stmt :" + query);
			//System.out.println(query);
			while (rs.next()) {
				dto = new UserDto();
				dto.setUserLoginId(rs.getString("USER_LOGIN_ID"));
				dto.setUserFname(rs.getString("USER_FNAME")+" "+rs.getString("USER_MNAME")+" "+rs.getString("USER_LNAME")+" ( "+rs.getString("USER_LOGIN_ID")+" ) ");
				masterList.add(dto);

			}
			logger.info("circle user success");
			return masterList;*/
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("SQL Exception occured while Viewing assigned Leads."+ "SQL Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Exception occured while Viewing assigned Leads."	+ " Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
				//DBConnection.releaseResources(con, ps1, rs1);
			} catch (Exception e) {
				logger.error("DAO Exception occured while Viewing assigned Leads."+ "DAO Exception Message: "+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	}
 public int getLeadStatus(long leadId) throws Exception{
	 
	 Connection con = null;
	 ResultSet rs = null;
	 PreparedStatement ps = null;
	 try{
		 con=DBConnection.getDBConnection();
		 ps=con.prepareStatement("SELECT LEAD_STATUS_ID FROM LEAD_DATA WHERE LEAD_ID=?  ");
		 ps.setLong(1, leadId);
		 rs=ps.executeQuery();
		 if(rs.next()){
			 return rs.getInt("LEAD_STATUS_ID");
		 }
	 }catch (DAOException daoEx){
		 
	 }finally{
		 DBConnection.releaseResources(con, ps, rs);
	 }
		
	return 0;
	 
 }
 
 public ArrayList<String> getSiteClosureList(String lob)  {
	 
	 Connection con = null;
	 ResultSet rs = null;
	 PreparedStatement ps = null;
	 ArrayList<String> cafList=null;
	 try{
		 
		 con=DBConnection.getDBConnection();
		
		 ps=con.prepareStatement("SELECT OLMID FROM NEW_PROD_CONFIG NPF, PRODUCT_LOB PL WHERE NPF.LOB=PL.PRODUCT_LOB_ID AND PL.PRODUCT_LOB_ID IN ("+lob+") AND NPF.STATUS='S'  ");
		 rs=ps.executeQuery();
		 cafList=new ArrayList<String>();
		 
		 while(rs.next()){
			 cafList.add(rs.getString("OLMID"));
		 }
	 }catch (Exception daoEx){
		 daoEx.printStackTrace();
	 }finally{
		 try {
		 DBConnection.releaseResources(con, ps, rs);
		 }catch(Exception e) {
			 
		 }
	 }
	 
	 
	return cafList;
	 
 }

 public String checkAsssignedPrimaryUser(Long leadId) throws Exception{
	 Connection con = null;
	 ResultSet rs = null;
	 PreparedStatement ps = null;
	 String user="";
 try{
		 
		 con=DBConnection.getDBConnection();
		
		 ps=con.prepareStatement("select LEAD_ASSIGNED_PRIMARY_USER from LEAD_TRANSACTION where lead_id="+leadId+" order by TRANSACTION_TIME desc fetch first row only  ");
		 
		 rs=ps.executeQuery();
		 while (rs.next()){
			 user= rs.getString("LEAD_ASSIGNED_PRIMARY_USER");
		 }
		
	 }catch (Exception daoEx){
		 daoEx.printStackTrace();
	 }finally{
		 try {
		 DBConnection.releaseResources(con, ps, rs);
		 }catch(Exception e) {
			 
		 }
	 }
	 return user;
 }
 
	public Leads getFSCDetails(String pincode, String channelId)
			throws LMSException {
		Connection con =null;
		PreparedStatement preStatement = null;
		Leads leads  = null;
		try {
			con = DBConnection.getDBConnection();
			preStatement  =  con.prepareStatement("SELECT CONCAT(CONCAT(u.USER_FNAME ,u.USER_MNAME) ,u.USER_LNAME) as USERNAME ,fsc.OLM_ID FROM FSC_ASSIGNMENT_MATRIX  fsc ,NAS_USER_MSTR  u WHERE  fsc.CHANNEL_PARTNER_ID = u.USER_ID and fsc.STATUS ='A' and u.STATUS ='A' AND U.OLM_ID = ? and fsc.PINCODE = ? fetch first row only");
			preStatement.setString(1, channelId);
			preStatement.setString(2, pincode);
			ResultSet rest =  preStatement.executeQuery();
			if(rest.next()) {
				 leads  = new Leads();
				leads.setPrimaryUser(rest.getString("USERNAME"));
				leads.setFwdOlmId(rest.getString("OLM_ID"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return leads;
	}
	
	public void leadForwordTOFSCs(String loginID, String actorId,String updateBy,String productId)
			throws LMSException {
		Connection con =null;
		PreparedStatement preStatement = null;
		try {
			con = DBConnection.getDBConnection();
			preStatement  =  con.prepareStatement("INSERT INTO LEAD_CHANNEL_TRANSACTION(STATUS_ID,LEAD_ID,PRODUCT_ID,LEAD_ASSIGNED_CHANNEL_USER ,UPDATED_BY,TRANSACTION_TIME) VALUES(430,?,(SELECT PRODUCT_ID FROM LEAD_DATA WHERE LEAD_ID = ?),?,?,current timestamp)");
			preStatement.setString(1, loginID);
			preStatement.setString(2, loginID);
			preStatement.setString(3, actorId);
			preStatement.setString(4, updateBy);
			int i  =preStatement.executeUpdate();
			con.commit();
			logger.info("row inserte=="+i);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				DBConnection.releaseResources(con, preStatement, null);
			} catch (Exception e) {
				logger.error("DAO Exception occured while Viewing assigned Leads."	+ "DAO Exception Message: "	+ e.getMessage());
			}
		}
		
	}


}
