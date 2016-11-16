package com.ibm.nas.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.ibm.nas.common.Constants;
import com.ibm.nas.common.DBConnection;
import com.ibm.nas.common.PropertyReader;
import com.ibm.nas.dao.MasterDao;
import com.ibm.nas.dao.UserMstrDao;
import com.ibm.nas.dto.CircleDTO;
import com.ibm.nas.dto.CircleMstrDto;
import com.ibm.nas.dto.LOBDTO;
import com.ibm.nas.dto.UserDto;
import com.ibm.nas.dto.UserMstr;
import com.ibm.nas.exception.DAOException;
import com.ibm.nas.exception.LMSException;
import com.ibm.nas.services.MasterService;
import com.ibm.nas.services.impl.MasterServiceImpl;


public class UserMstrDaoImpl implements UserMstrDao {

	private static Logger logger =
		Logger.getLogger(UserMstrDaoImpl.class);
	
	public static final String SQL_DELETE_USER ="UPDATE NAS_USER_MSTR SET STATUS = 'D',UPDATED_DT=current timestamp " +
	"WHERE UPPER(USER_LOGIN_ID) = ? AND STATUS = 'A' ";

	public static final String SQL_GET_CIRCLE_USERS ="select USER_ID from NAS_USER_MSTR where element_id= ? and KM_ACTOR_ID = 3 ";


	protected static final String SQL_INSERT_WITH_ID ="INSERT INTO NAS_USER_MSTR (USER_ID, USER_LOGIN_ID, USER_FNAME, USER_MNAME, USER_LNAME, USER_MOBILE_NUMBER, USER_EMAILID, USER_PASSWORD, USER_PSSWRD_EXPRY_DT, CREATED_DT, CREATED_BY,  STATUS, CIRCLE_ID, GROUP_ID, KM_ACTOR_ID, KM_OWNER_ID, LOGIN_ATTEMPTED, USER_LOGIN_STATUS, ELEMENT_ID, FAV_CATEGORY_ID,OLM_ID,PARTNER,PBX_ID,BUSINESS_SEGEMENT,ACTIVITY,ROLE) VALUES (NEXTVAL FOR KM_USER_ID_SEQ, ?, ?, ?, ?, ?, ?, ?, current timestamp + 45 DAYS, current timestamp , ?, ?, ?, ?, ?, ?, 0, 'N',?,?,?,?,?,?,?,?)";
	protected static final String SQL_SELECT ="SELECT NAS_USER_MSTR.USER_ID, NAS_USER_MSTR.USER_LOGIN_ID, NAS_USER_MSTR.USER_FNAME, NAS_USER_MSTR.USER_MNAME, NAS_USER_MSTR.USER_LNAME,NAS_USER_MSTR.USER_MOBILE_NUMBER, NAS_USER_MSTR.USER_EMAILID, NAS_USER_MSTR.USER_PASSWORD, NAS_USER_MSTR.USER_PSSWRD_EXPRY_DT, NAS_USER_MSTR.CREATED_DT, UM.ZONE_CODE, UM.CITY_ZONE_CODE, NAS_USER_MSTR.UPDATED_DT, NAS_USER_MSTR.CREATED_BY, NAS_USER_MSTR.UPDATED_BY , UM.ZONE_FLAG , NAS_USER_MSTR.STATUS,UM.CIRCLE_ID, NAS_USER_MSTR.GROUP_ID, NAS_USER_MSTR.KM_ACTOR_ID, NAS_USER_MSTR.KM_OWNER_ID, NAS_USER_MSTR.LOGIN_ATTEMPTED, NAS_USER_MSTR.LAST_LOGIN_TIME, NAS_USER_MSTR.USER_LOGIN_STATUS, NAS_USER_MSTR.FAV_CATEGORY_ID, NAS_USER_MSTR.ELEMENT_ID, UM.LOB_ID ,NAS_USER_MSTR.FAV_CATEGORY_ID,NAS_USER_MSTR.PARTNER_NAME,AC.KM_ACTOR_NAME ,(select CIRCLE_NAME FROM CIRCLE_MSTR WHERE CIRCLE_ID =UM.CIRCLE_ID AND LOB_ID = UM.LOB_ID ) AS CIRCLE_NAME ,(select ZONE_NAME FROM ZONE_MSTR WHERE ZONE_CODE = UM.ZONE_CODE) AS ZONE_NAME,(select CITY_ZONE_NAME FROM CITY_ZONE_MSTR WHERE CITY_ZONE_CODE = UM.CITY_ZONE_CODE) AS CITY_ZONE_NAME FROM NAS_USER_MSTR ,KM_ACTORS AC, USER_MAPPING UM WHERE NAS_USER_MSTR.KM_ACTOR_ID = AC.KM_ACTOR_ID AND NAS_USER_MSTR.USER_LOGIN_ID = UM.USER_LOGIN_ID AND NAS_USER_MSTR.USER_ID = ?   ";
	protected static final String SQL_SELECT_FOR_EDIT ="SELECT NAS_USER_MSTR.USER_ID, NAS_USER_MSTR.USER_LOGIN_ID, NAS_USER_MSTR.USER_FNAME, NAS_USER_MSTR.USER_MNAME, NAS_USER_MSTR.USER_LNAME,NAS_USER_MSTR.USER_MOBILE_NUMBER, NAS_USER_MSTR.USER_EMAILID, NAS_USER_MSTR.USER_PASSWORD, NAS_USER_MSTR.USER_PSSWRD_EXPRY_DT, NAS_USER_MSTR.CREATED_DT,NAS_USER_MSTR.UPDATED_DT,NAS_USER_MSTR.CREATED_BY, NAS_USER_MSTR.UPDATED_BY ,(select ZONE_ID FROM ZONE_MSTR WHERE ZONE_CODE = UM.ZONE_CODE) AS ZONE_CODE,(select CITY_ZONE_ID FROM CITY_ZONE_MSTR WHERE CITY_ZONE_CODE = UM.CITY_ZONE_CODE) as CITY_ZONE_CODE, UM.ZONE_FLAG , NAS_USER_MSTR.STATUS,UM.CIRCLE_ID, NAS_USER_MSTR.GROUP_ID, NAS_USER_MSTR.KM_ACTOR_ID, NAS_USER_MSTR.KM_OWNER_ID, NAS_USER_MSTR.LOGIN_ATTEMPTED, NAS_USER_MSTR.LAST_LOGIN_TIME, NAS_USER_MSTR.USER_LOGIN_STATUS, NAS_USER_MSTR.FAV_CATEGORY_ID, NAS_USER_MSTR.ELEMENT_ID, UM.LOB_ID ,NAS_USER_MSTR.FAV_CATEGORY_ID,NAS_USER_MSTR.PARTNER_NAME,AC.KM_ACTOR_NAME ,(select CIRCLE_MSTR_ID FROM CIRCLE_MSTR WHERE CIRCLE_ID =UM.CIRCLE_ID AND LOB_ID = UM.LOB_ID ),(select PRODUCT_LOB FROM  PRODUCT_LOB WHERE PRODUCT_LOB_ID = UM.LOB_ID), (select CIRCLE_NAME FROM CIRCLE_MSTR WHERE CIRCLE_ID =UM.CIRCLE_ID AND LOB_ID = UM.LOB_ID ) AS CIRCLE_NAME ,(select ZONE_NAME FROM ZONE_MSTR WHERE ZONE_CODE = UM.ZONE_CODE) AS ZONE_NAME,(select CITY_ZONE_NAME FROM CITY_ZONE_MSTR WHERE CITY_ZONE_CODE = UM.CITY_ZONE_CODE) AS CITY_ZONE_NAME FROM USER_MSTR ,KM_ACTORS AC, USER_MAPPING UM WHERE NAS_USER_MSTR.KM_ACTOR_ID = AC.KM_ACTOR_ID AND NAS_USER_MSTR.USER_LOGIN_ID = UM.USER_LOGIN_ID AND NAS_USER_MSTR.USER_ID = ?   AND UM.LOB_ID = ?  ";

	protected static final String SQL_SELECT_USERS ="SELECT NAS_USER_MSTR.USER_ID, NAS_USER_MSTR.USER_LOGIN_ID, NAS_USER_MSTR.USER_FNAME, NAS_USER_MSTR.USER_MNAME, NAS_USER_MSTR.USER_LNAME, NAS_USER_MSTR.USER_MOBILE_NUMBER, NAS_USER_MSTR.USER_EMAILID, NAS_USER_MSTR.USER_PASSWORD, NAS_USER_MSTR.USER_PSSWRD_EXPRY_DT, NAS_USER_MSTR.CREATED_DT, NAS_USER_MSTR.CREATED_BY , NAS_USER_MSTR.UPDATED_DT, NAS_USER_MSTR.UPDATED_BY, NAS_USER_MSTR.STATUS, CIRCLE_MSTR.CIRCLE_NAME, NAS_USER_MSTR.GROUP_ID, KM_ACTORS.KM_ACTOR_NAME, NAS_USER_MSTR.KM_OWNER_ID, NAS_USER_MSTR.LOGIN_ATTEMPTED, NAS_USER_MSTR.LAST_LOGIN_TIME, NAS_USER_MSTR.USER_LOGIN_STATUS, NAS_USER_MSTR.FAV_CATEGORY_ID,NAS_USER_MSTR.PARTNER_NAME FROM NAS_USER_MSTR , CIRCLE_MSTR , KM_ACTORS WHERE  ";

	protected static final String SQL_SELECT_USERID ="SELECT USER_LOGIN_ID,ELEMENT_ID, USER_ID FROM NAS_USER_MSTR WHERE USER_LOGIN_ID = ?  ";

//Added BY Bhaskar
	
	protected static final String SQL_SELECT_USERFNAME ="SELECT USER_LOGIN_ID,ELEMENT_ID, USER_ID FROM NAS_USER_MSTR WHERE upper(USER_FNAME) = ? ";
	//end By Bhaskar
	protected static final String SQL_UPDATE_PWD ="UPDATE NAS_USER_MSTR SET USER_PASSWORD=?,UPDATED_DT=current timestamp,UPDATED_BY=?, USER_PSSWRD_EXPRY_DT =(current timestamp+45 DAYS), LAST_LOGIN_TIME=(current timestamp) WHERE USER_ID = ?";

	protected static final String SQL_SELECT_CIRCLE_APPROVER ="SELECT NAS_USER_MSTR.USER_ID, NAS_USER_MSTR.USER_LOGIN_ID, NAS_USER_MSTR.USER_FNAME, NAS_USER_MSTR.USER_MNAME, NAS_USER_MSTR.USER_LNAME, NAS_USER_MSTR.USER_MOBILE_NUMBER, NAS_USER_MSTR.USER_EMAILID, NAS_USER_MSTR.USER_PASSWORD, NAS_USER_MSTR.USER_PSSWRD_EXPRY_DT, NAS_USER_MSTR.CREATED_DT, NAS_USER_MSTR.CREATED_BY, NAS_USER_MSTR.UPDATED_DT, NAS_USER_MSTR.UPDATED_BY, NAS_USER_MSTR.STATUS, NAS_USER_MSTR.CIRCLE_ID, NAS_USER_MSTR.GROUP_ID, NAS_USER_MSTR.KM_ACTOR_ID, NAS_USER_MSTR.KM_OWNER_ID, NAS_USER_MSTR.LOGIN_ATTEMPTED, NAS_USER_MSTR.LAST_LOGIN_TIME, KM_CIRCLE_MSTR.CIRCLE_NAME FROM NAS_USER_MSTR, KM_CIRCLE_MSTR WHERE NAS_USER_MSTR.CIRCLE_ID = KM_CIRCLE_MSTR.CIRCLE_ID AND NAS_USER_MSTR.KM_ACTOR_ID = 2";

	protected static final String SQL_SELECT_CIRCLE_USER ="SELECT NAS_USER_MSTR.USER_ID, NAS_USER_MSTR.USER_LOGIN_ID, NAS_USER_MSTR.USER_FNAME, NAS_USER_MSTR.USER_MNAME, NAS_USER_MSTR.USER_LNAME, NAS_USER_MSTR.USER_MOBILE_NUMBER, NAS_USER_MSTR.USER_EMAILID, USER_MSTR.USER_PASSWORD, USER_MSTR.USER_PSSWRD_EXPRY_DT, USER_MSTR.CREATED_DT, USER_MSTR.CREATED_BY, USER_MSTR.UPDATED_DT, USER_MSTR.UPDATED_BY, USER_MSTR.STATUS, USER_MSTR.CIRCLE_ID, USER_MSTR.GROUP_ID, USER_MSTR.KM_ACTOR_ID, USER_MSTR.KM_OWNER_ID, USER_MSTR.LOGIN_ATTEMPTED, USER_MSTR.LAST_LOGIN_TIME, KM_CIRCLE_MSTR.CIRCLE_NAME FROM USER_MSTR, KM_CIRCLE_MSTR WHERE USER_MSTR.CIRCLE_ID = KM_CIRCLE_MSTR.CIRCLE_ID";

	protected static final String SQL_UPDATE_USER ="UPDATE USER_MSTR SET  USER_FNAME = ?, USER_MNAME = ?, USER_LNAME = ?, USER_MOBILE_NUMBER = ?, USER_EMAILID = ?, STATUS = ?,  UPDATED_BY = ?, UPDATED_DT = current timestamp, USER_LOGIN_STATUS =?,PARTNER_NAME = ?, KM_ACTOR_ID = ?";

	protected static final String SQL_INSERT_BULK_DATA_TRANSACTION_LOGS = "INSERT INTO BULK_DATA_TRANSACTION_LOGS(USER_LOGIN_ID,DOWNLOAD_TIME,MESSAGE,CLIENT_IP,FILENAME,LOB_ID,CIRCLE,ZONE_OR_CITYZONE,CHANNEL_PARTNER,STATUS,START_DATE,END_DATE) VALUES(?,current timestamp,?,?,?,?,?,?,?,?,?,?)";
	
	protected static final String SQL_UPDATE_USER_MAPPING ="UPDATE NAS_USER_MAPPING    SET  LOB_ID=?, CIRCLE_ID=?,  TRANSACTION_TIME=current timestamp, UPDATED_BY=?  WHERE USER_LOGIN_ID =? ";

	protected static final String SQL_DELETE_USER_MAPPING ="DELETE FROM NAS_USER_MAPPING  WHERE USER_LOGIN_ID =? AND LOB_ID = ?  ";
	
	protected static final String SQL_UPDATE_USER_BULK ="UPDATE NAS_USER_MSTR SET  USER_FNAME = ?, USER_MNAME = ?, USER_LNAME = ?, USER_MOBILE_NUMBER = ?, USER_EMAILID = ?,   STATUS = 'A', UPDATED_BY = ?, UPDATED_DT = current timestamp ";

	protected static final String SQL_SELECT_CIRCLE ="SELECT KM_CIRCLE_MSTR.CIRCLE_NAME, KM_CIRCLE_MSTR.CIRCLE_ID FROM KM_CIRCLE_MSTR WHERE STATUS = 'A' ORDER BY CIRCLE_NAME";

	protected static final String SQL_GET_FAV_CATEGORY ="SELECT FAV_CATEGORY_ID FROM USER_MSTR WHERE USER_ID = ? ";
	
	protected static final String SQL_GET_LOGIN_ATTEMPTED ="SELECT LOGIN_ATTEMPTED FROM NAS_USER_MSTR WHERE USER_LOGIN_ID = ? ";

	protected static final String UPDATE_LOGIN_ATTEMPTED ="update NAS_USER_MSTR set LOGIN_ATTEMPTED = LOGIN_ATTEMPTED+1 where USER_LOGIN_ID = ? ";

	
	protected static final String SQL_COUNT_ELEMENT_USERS ="select count(element_id) as user_count from USER_MSTR where element_id = ? ";

	protected static final String SQL_CHECK_FOR_FAVOURITE ="select count(element_id)as user_count from USER_MSTR where fav_category_id=?";

	protected static final String SQL_GET_MANAGERS =" select * from NAS_USER_MSTR usr where status='A' and  user_id = ";

	protected static final String SQL_GET_APPROVER ="select user_fname,user_lname from USER_MSTR usr, KM_escalation_matrix esc where usr.user_id=esc.CIRCLE_ADMIN_ID and esc.CIRCLE_ID=?";
	
	protected static final String SQL_GET_USERLOGIN_ID ="SELECT USER_LOGIN_ID FROM NAS_USER_MSTR WHERE USER_ID =?  ";

	/*
	 * 
	protected static final String SQL_INSERT_USER_MSTR="INSERT INTO USER_MSTR(" +
	"" +
	"USER_ID, USER_LOGIN_ID, USER_FNAME, USER_MNAME, USER_LNAME, USER_MOBILE_NUMBER, " +
	"" +
	"USER_EMAILID, USER_PASSWORD, USER_PSSWRD_EXPRY_DT, CREATED_DT, CREATED_BY, UPDATED_DT, " +
	"" +
	"UPDATED_BY, STATUS, CIRCLE_ID,  KM_ACTOR_ID, LOGIN_ATTEMPTED, LAST_LOGIN_TIME, USER_LOGIN_STATUS,  " +
	"" +
	"PARTNER_NAME, PASSWORD_RESET_TIME,OLM_ID) VALUES " +
	" (NEXTVAL FOR KM_USER_ID_SEQ,?,?,?,?,?,?,?,current timestamp + 45 days, " +
	"" +
	"current timestamp,?,null,null,'A',?,?,0,null,'N',?,current timestamp,?)";	
	
	*/
	
	/* Added by Parnika for LMS Phase 2 */
	protected static final String SQL_INSERT_USER_MSTR = "INSERT INTO NAS_USER_MSTR(USER_ID, USER_LOGIN_ID, USER_FNAME, " +
			"USER_MNAME, USER_LNAME, USER_MOBILE_NUMBER, USER_EMAILID, USER_PASSWORD, USER_PSSWRD_EXPRY_DT, CREATED_DT, " +
			"CREATED_BY, UPDATED_DT, UPDATED_BY, STATUS, KM_ACTOR_ID, LOGIN_ATTEMPTED, LAST_LOGIN_TIME, USER_LOGIN_STATUS,PARTNER_NAME, " +
			"PASSWORD_RESET_TIME,OLM_ID,PARENT_ID) VALUES (NEXTVAL FOR KM_USER_ID_SEQ,?,?,?,?,?,?,?,current timestamp + 45 days, current timestamp,?," +
			"null,null,'A',?,0,null,'N',?,current timestamp,?,?)";
	
	protected static final String SQL_INSERT_USER_MAPPING_DATA = "INSERT INTO NAS_USER_MAPPING(USER_LOGIN_ID, LOB_ID, CIRCLE_ID, " +
			"CITY_ZONE_CODE, CITY_CODE, ZONE_CODE, TRANSACTION_TIME, UPDATED_BY ,ZONE_FLAG) VALUES(?, ?, ? , ?, ?, ?, current timestamp, ? ,?)";
	
	protected static final String SQL_SELECT_CITY_ZONE_CODE = "SELECT cz.CITY_ZONE_CODE, cz.CITY_CODE , cm.ZONE_CODE FROM CITY_ZONE_MSTR cz, ZONE_MSTR zm , CITY_MSTR cm WHERE cz.CITY_CODE = cm.CITY_CODE and cm.ZONE_CODE = zm.ZONE_CODE and CITY_ZONE_ID = ?";
	protected static final String SQL_SELECT_ZONE_CODE = "SELECT ZONE_CODE FROM ZONE_MSTR WHERE ZONE_ID = ?  ";
	protected static final String SQL_UPDATE_USER_MSTR = "UPDATE USER_MSTR SET USER_FNAME = ?, USER_MNAME = ?, USER_LNAME = ? , USER_MOBILE_NUMBER = ?, USER_EMAILID = ?, UPDATED_DT = current timestamp, UPDATED_BY = ?, PARTNER_NAME = ? WHERE USER_LOGIN_ID = ?";


	
	/* End of changes by Parnika */
	
	//  LDAP implementation change finish


	/*kmphase2 partner search */		
	protected static final String SQL_GET_PARTNER="select PARTNER_NAME from KM_PARTNER_MSTR WHERE STATUS='A' order by PARTNER_NAME";

	protected static final String LAST_THREE_PASSWORD_CHECK="Select count(b.USER_PASSWORD) as matches from NAS_USER_MSTR a, PASSWORD_HISTORY b where a.USER_LOGIN_ID = ?  and b.USER_PASSWORD = ? and b.USER_LOGIN_ID = a.USER_LOGIN_ID   ";

	//Added by srikant 
	
private static UserMstrDaoImpl userMstrDaoImpl=null;
	
	private UserMstrDaoImpl(){
		
	}
	
	public static UserMstrDaoImpl userMstrDaoInstance()
	{
		if(userMstrDaoImpl==null)
		{
			userMstrDaoImpl=new UserMstrDaoImpl();
		}
		return userMstrDaoImpl;
		
	}
	//Insert into the table USER_MSTR

	public int insert(UserMstr dto) throws LMSException {

		logger.info("Entered insert for table USER_MSTR");

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql;
		int rowsUpdated = 0;

		try {
			con = getConnection();

			int userCount=0;
			int actorId=Integer.parseInt(dto.getKmActorId());
			if(actorId==7){
				ps = con.prepareStatement("SELECT COUNT(*) AS COUNT FROM USER_MSTR WHERE ELEMENT_ID = ? and KM_ACTOR_ID = ?  ");
				ps.setInt(1, Integer.parseInt(dto.getElementId()));
				ps.setInt(2, actorId);
				rs=ps.executeQuery();
				if(rs.next())
					userCount= Integer.parseInt(rs.getString("COUNT"));
				if(userCount>=Integer.parseInt(PropertyReader.getAppValue("max.report.admin.limit")))
					return -1;

			}

			//sql = SQL_INSERT_WITH_ID;





			ps = con.prepareStatement(SQL_INSERT_WITH_ID);
			int paramCount = 0;

			//	Preparing statement


			ps.setString(1, dto.getUserLoginId().trim());
			ps.setString(2, dto.getUserFname());
			ps.setString(3, dto.getUserMname());
			ps.setString(4, dto.getUserLname());
			ps.setString(5, dto.getUserMobileNumber());
			ps.setString(6, dto.getUserEmailid());
			ps.setString(7, dto.getUserPassword().trim());
			ps.setInt(8, Integer.parseInt(dto.getCreatedBy()));
			ps.setString(9, dto.getStatus());
			ps.setInt(10, 1);
			ps.setInt(11, 1);
			ps.setInt(12, actorId);
			ps.setInt(13, 2);
			ps.setInt(14, Integer.parseInt(dto.getElementId()));
			if (dto.getFavCategoryId() != null
					&& !dto.getFavCategoryId().equals(""))
				ps.setInt(15, Integer.parseInt(dto.getFavCategoryId()));
			else
				ps.setInt(15, 8);


			// added for OLM ID addition in 
			ps.setString(16,dto.getUserLoginId().trim().substring(0,8));
			//	Executing querry

			ps.setString(17, dto.getPartner());
			ps.setString(18, dto.getPbxId());
			ps.setString(19, dto.getBusinessSegment());
			ps.setString(20, dto.getActivity());
			ps.setString(21, dto.getRole());

			rowsUpdated = ps.executeUpdate();
			logger.info(
					"Row insertion successful on table:USER_MSTR. Inserted:"
					+ rowsUpdated
					+ " rows");

		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(
					"SQL Exception occured while inserting."
					+ "Exception Message: "
					+ e.getMessage());
			throw new LMSException("SQLException: " + e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(
					"Exception occured while inserting."
					+ "Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {

				if (con != null) {

					con.close();
				}
		//		DBConnection.releaseResources(null, ps, rs);
			} catch (Exception e) {
				logger.error(
						"DAO Exception occured while inserting."
						+ "Exception Message: "
						+ e.getMessage());
				throw new LMSException("DAO Exception: " + e.getMessage(), e);
			}
		}

		return rowsUpdated;
	}

	
	
	public void insertBulkDataTransactionLogs(UserMstr dto)throws LMSException{
		logger.info("Entered insert for table BulkDataTransactionLogs");

		Connection con = null;
		PreparedStatement ps = null;
		try{
		con = DBConnection.getDBConnection();
		ps = con.prepareStatement(SQL_INSERT_BULK_DATA_TRANSACTION_LOGS);
		logger.info("Before insert into BULK_DATA_TRANSACTION_LOGS table");
		ps.setString(1,dto.getUserLoginId());
		System.out.println(dto.getUserLoginId()+dto.getMessage()+dto.getIpaddress()+dto.getFileName());
		ps.setString(2,dto.getMessage());
		ps.setString(3,dto.getIpaddress());
		ps.setString(4,dto.getFileName());
		ps.setString(5,dto.getLobId());
		ps.setString(6,dto.getCircleId());
		ps.setString(7,dto.getZoneCode());
		ps.setString(8,dto.getChannelPartnerId());
		ps.setString(9,dto.getLeadStatus());
		ps.setString(10,dto.getStartDate());
		ps.setString(11,dto.getEndDate());
		
		int insertRows=ps.executeUpdate();
		if(insertRows==1)
		{
			dto.setLobId("");
			dto.setCircleId("");
			dto.setZoneCode("");
			dto.setChannelPartnerId("");
			dto.setLeadStatus("");
			dto.setStartDate("");
			dto.setEndDate("");
			dto.setFlag("");
			
			
		}
		logger.info(
				"insertion into BULK_DATA_TRANSACTION_LOGS"
				+ insertRows
				+ "  rows");

	} catch (SQLException e) {
		e.printStackTrace();
		logger.error(e);
		logger.error(
				"SQL Exception occured while update."
				+ "Exception Message: "
				+ e.getMessage());

		throw new LMSException("SQLException: " + e.getMessage(), e);
	} catch (Exception e) {
		e.printStackTrace();
		logger.error(e);
		logger.error(
				"Exception occured while update."
				+ "Exception Message: "
				+ e.getMessage());

		throw new LMSException("Exception: " + e.getMessage(), e);
	} finally {
		try {
	//		//////////////DBConnection.releaseResources(con, ps, null);
		} catch (Exception e) {
			logger.error(
					"DAO Exception occured while update."
					+ "Exception Message: "
					+ e.getMessage());

			throw new LMSException("DAO Exception: " + e.getMessage(), e);
		                          }
		
		      }
	}
	
	
	
	
	//Update the user details in the table USER_MSTR
	public int update(UserMstr dto) throws LMSException {

		logger.info("Entered update for table USER_MSTR");

		Connection con = null;
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		PreparedStatement ps4 = null;
		StringBuffer query2=null;
		StringBuffer query3=null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;

		int numRows = -1;
		int rows = -1;
		int circleId = -1;
		int zoneId = 0;
		int cityZoneId = 0;
		String zoneName = null;
		String cityZoneName = null;
		String zoneCode = null;
		String cityZoneCode = null;
		String[] multipleCircles = null;
		ArrayList<CircleDTO> circleForUserList = new ArrayList<CircleDTO>();
		MasterDao master = MasterDaoImpl.masterDaoInstance();;
		//String sql;
		try {
			StringBuffer query=new StringBuffer(SQL_UPDATE_USER);
			StringBuffer query1=new StringBuffer(SQL_INSERT_USER_MAPPING_DATA);
			query2=new StringBuffer(SQL_SELECT_CITY_ZONE_CODE);
			query3=new StringBuffer(SQL_SELECT_ZONE_CODE);
			query.append(" WHERE USER_ID = ? ").toString();
			
			logger.info("SQL is " + query);
			
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(query.append("  ").toString());
			//Delete from user Mapping
			ps1 = con.prepareStatement(SQL_DELETE_USER_MAPPING);
			ps2 = con.prepareStatement(query1.append("  ").toString());
			logger.info("Before preparing statement");
			ps.setString(1, dto.getUserFname()); //Error
			ps.setString(2, dto.getUserMname());
			ps.setString(3, dto.getUserLname());
			ps.setString(4, dto.getUserMobileNumber());
			ps.setString(5, dto.getUserEmailid());
			ps.setString(6, dto.getStatus());
			ps.setString(7, dto.getUpdatedBy());
			ps.setString(8, dto.getUserLoginStatus());
			ps.setString(9, dto.getPartnerName());
			ps.setInt(10, Integer.parseInt(dto.getKmActorId()));
			ps.setInt(11, Integer.parseInt(dto.getUserId()));
			System.out.println("role--------------"+Integer.parseInt(dto.getKmActorId()));
			numRows = ps.executeUpdate();
			// Deleting from User_mapping
			ps1.setString(1, dto.getUserLoginId());
			ps1.setInt(2, Integer.parseInt(dto.getLobId()));
			rows = ps1.executeUpdate();
			
			
			// For getting City Zone code 
			if (dto.getSelectedTypeId() == 2){				
				ps4 = con.prepareStatement(query2.append("   ").toString());
				ps4.setInt(1,  dto.getSelectedZoneId());
				rs2 = ps4.executeQuery();				
				if(rs2.next()){
					cityZoneCode = rs2.getString(1);
				}
			}			
			// For getting Zone Code
			else if (dto.getSelectedTypeId() == 1){				
				ps3 = con.prepareStatement(query3.append("   ").toString());
				ps3.setInt(1, dto.getSelectedZoneId());
				rs3 = ps3.executeQuery();			
				if(rs3.next()){
					zoneCode = rs3.getString(1);
				}
			}
			
			
			
				// Inserting multiple rows when Circle coordinator is created.
			
			
				multipleCircles = dto.getCreateMultiple();	
			
				if(multipleCircles != null)
				{
					for(int i =0 ; i< multipleCircles.length ; i++)
					{
/*						if(i==0 && multipleCircles.length > 1)
						{
							continue;
						}*/
						if (!(multipleCircles[i].trim().equalsIgnoreCase(""))){
							
							circleId = master.getCircleIdValue(Integer.parseInt(multipleCircles[i]));
							
							
						}
						else{							
								circleId = Integer.parseInt(Constants.PANINDIA_CIRCLE_ID);
							
							
						}
						/*circleForUserList = master.getCircleForUser(dto.getUserLoginId());
						boolean flag = true;
						for(int j=0 ; j < circleForUserList.size(); j++){
							int intcircles = circleForUserList.get(j).getCircleId();	
							System.out.println("intcircles"+intcircles);
							System.out.println("circleId"+circleId);
							if(intcircles == circleId){
								flag = false;
								break;
								}
							
						}							
						if(flag){*/
							ps2.setString(1, dto.getUserLoginId());
							if(dto.getLobId()!=null){
								ps2.setInt(2, Integer.parseInt(dto.getLobId()));
								}
								else
									ps2.setInt(2,0);
							ps2.setInt(3, circleId);
							if(dto.getSelectedTypeId()==1){
								ps2.setString(4, "");
							}
							else								
								ps2.setString(4, cityZoneCode);
							ps2.setString(5, "");
							
							if(dto.getSelectedTypeId()==1){
								ps2.setString(6, zoneCode);
							}
							else								
								ps2.setString(6, "");
							ps2.setString(7, dto.getUpdatedBy());
							if(dto.getSelectedTypeId()==1){
								
								ps2.setString(8, Constants.ZONE_CODE_FLAG_VALUE);
							}
							else
							ps2.setString(8, Constants.CITY_ZONE_CODE_FLAG_VALUE);
							ps2.executeUpdate();
						//}
						
						
					}
				}
				else{
					if (dto.getCircleId() != null && !dto.getCircleId().trim().equals(""))
					{
						
					//	circleId = Integer.parseInt(dto.getCircleId());
						circleId = master.getCircleIdValue(Integer.parseInt(dto.getCircleId()));
						//ps1.setInt(2, Integer.parseInt(dto.getCircleId()));
					}
					else
					{
						circleId = Integer.parseInt(Constants.PANINDIA_CIRCLE_ID);
					}
					ps2.setString(1, dto.getUserLoginId());
					if(dto.getLobId()!=null){
						ps2.setInt(2, Integer.parseInt(dto.getLobId()));
						}
						else
							ps2.setInt(2,0);
					ps2.setInt(3, circleId);
					if(dto.getSelectedTypeId()==1){
						ps2.setString(4, "");
					}
					else								
						ps2.setString(4, cityZoneCode);
					ps2.setString(5, "");
					
					if(dto.getSelectedTypeId()==1){
						ps2.setString(6, zoneCode);
					}
					else								
						ps2.setString(6, "");
					ps2.setString(7, dto.getUpdatedBy());
					if(dto.getSelectedTypeId()==1){
						
						ps2.setString(8, Constants.ZONE_CODE_FLAG_VALUE);
					}
					else
					ps2.setString(8, Constants.CITY_ZONE_CODE_FLAG_VALUE);
					ps2.executeUpdate();
				//}
				
				
				}
			
			
	
			
			logger.info(
					"Update successful on table:USER_MSTR. Updated:"
					+ numRows
					+ " rows");

		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e);
			logger.error(
					"SQL Exception occured while update."
					+ "Exception Message: "
					+ e.getMessage());

			throw new LMSException("SQLException: " + e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			logger.error(
					"Exception occured while update."
					+ "Exception Message: "
					+ e.getMessage());

			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				//////////////DBConnection.releaseResources(con, ps, null);
			} catch (Exception e) {
				logger.error(
						"DAO Exception occured while update."
						+ "Exception Message: "
						+ e.getMessage());

				throw new LMSException("DAO Exception: " + e.getMessage(), e);
			}
		}
		
		return numRows;

	}
	public int updatePassword(UserMstr dto) throws LMSException {

		logger.info("Entered update password for table USER_MSTR");

		Connection con = null;
		PreparedStatement ps = null;
		CallableStatement cs = null;
		int numRows = -1;
		int updateCnt = 1;
		int passwordcount=Integer.parseInt(PropertyReader.getGsdValue("PasswordHistoryLimit"));

		try {
			con = DBConnection.getDBConnection();
			StringBuffer query=new StringBuffer(SQL_UPDATE_PWD);
			ps = con.prepareStatement(query.toString());
			if (dto.getNewPassword() == null)
				ps.setNull(updateCnt++, Types.VARCHAR);
			else
				ps.setString(updateCnt++, dto.getNewPassword());
			if (dto.getUserId() == null)
				ps.setNull(updateCnt++, Types.VARCHAR);
			else
				ps.setString(updateCnt++, dto.getUserId());

			ps.setString(updateCnt++, dto.getUserId());

			numRows = ps.executeUpdate();

			if (1 == numRows) {

				logger.info("proceure calling");
				String schema = PropertyReader.getAppValue("lms.schema.bulk.download");
				cs = con.prepareCall("{call "+schema+".CHANGEPASSWORD(?,?,?,?)}");

				cs.setString(1, dto.getUserLoginId());
				cs.setString(2, dto.getNewPassword());
				cs.setString(3, dto.getOldPassword());
				cs.setInt(4, passwordcount);
				cs.execute();

			}

			logger.info(
					"Update successful for password on table:USER_MSTR. Updated:"
					+ numRows
					+ " rows");

		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(
					"SQL Exception occured while updatePassword."
					+ "Exception Message: "
					+ e.getMessage());
			throw new LMSException("SQLException: " + e.getMessage(), e);
		} catch (Exception e) {

			logger.error(
					"Exception occured while updatePassword."
					+ "Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (cs != null)
					cs.close();
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				logger.error(
						"DAO Exception occured while updatePassword."
						+ "Exception Message: "
						+ e.getMessage());
				throw new LMSException("DAO Exception: " + e.getMessage(), e);
			}
		}
		return numRows;
	}

	protected UserMstr fetchSingleResult(ResultSet rs) throws SQLException {
		if (rs.next()) {
			UserMstr dto = new UserMstr();
			populateDto(dto, rs);
			return dto;
		} else
			return null;
	}

	protected static void populateDto(UserMstr dto, ResultSet rs)
	throws SQLException {
		dto.setUserId(rs.getString("USER_ID"));

		dto.setUserLoginId(rs.getString("USER_LOGIN_ID"));

		dto.setUserFname(rs.getString("USER_FNAME"));

		dto.setUserMname(rs.getString("USER_MNAME"));

		dto.setUserLname(rs.getString("USER_LNAME"));

		dto.setUserMobileNumber(rs.getString("USER_MOBILE_NUMBER"));

		dto.setUserEmailid(rs.getString("USER_EMAILID"));

		dto.setUserPassword(rs.getString("USER_PASSWORD"));

		dto.setUserPsswrdExpryDt(rs.getTimestamp("USER_PSSWRD_EXPRY_DT"));

		dto.setCreatedDt(rs.getTimestamp("CREATED_DT"));

		dto.setCreatedBy(rs.getString("CREATED_BY"));

		dto.setUpdatedDt(rs.getTimestamp("UPDATED_DT"));

		dto.setUpdatedBy(rs.getString("UPDATED_BY"));

		dto.setStatus(rs.getString("STATUS"));

		dto.setCircleId(rs.getString("CIRCLE_ID"));

		dto.setGroupId(rs.getString("GROUP_ID"));

		dto.setKmActorId(rs.getString("KM_ACTOR_ID"));

		dto.setKmOwnerId(rs.getString("KM_OWNER_ID"));

		dto.setLoginAttempted(rs.getString("LOGIN_ATTEMPTED"));

		dto.setLastLoginTime(rs.getString("LAST_LOGIN_TIME"));

		dto.setUserLoginStatus(rs.getString("USER_LOGIN_STATUS"));

		dto.setCategoryId(rs.getString("FAV_CATEGORY_ID"));

	}

	//Check for the presense of user login id in the database to prevent duplication while inserting

	public boolean CheckUserId(String userLoginId) throws DAOException,LMSException {

		logger.info("Entered CheckUserId as active or not for table:USER_MSTR");

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean isExist = false;
		try {

			StringBuffer query= new StringBuffer("SELECT USER_ID FROM NAS_USER_MSTR WHERE USER_LOGIN_ID = ? AND STATUS = 'A'"); // AND STATUS = 'A'

			//StringBuffer query= new StringBuffer("SELECT USER_ID FROM USER_MSTR WHERE USER_LOGIN_ID = ? AND STATUS = 'A'  "); // AND STATUS = 'A'

			//String sql =
			query.append("  ").toString();
			con = DBConnection.getDBConnection();
			pst = con.prepareStatement(query.append("   ").toString());
			pst.setString(1, userLoginId);
			rs = pst.executeQuery();
			System.out.println("Queryyyyyyyyyyyyyyyy"+query);
			if (rs.next()) {
				isExist = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			e.printStackTrace();
			logger.error(
					"SQL Exception occured while CheckUserId."
					+ "Exception Message: "
					+ e.getMessage());
			throw new LMSException("SQLException: " + e.getMessage(), e);
		} catch (DAOException e) {

			logger.error(
					"SQL Exception occured while CheckUserId."
					+ "Exception Message: "
					+ e.getMessage());
			throw new DAOException("SQLException: " + e.getMessage(), e);
		}
		catch (Exception e) {

			logger.error(
					"Exception occured while CheckUserId."
					+ "Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				//DBConnection.releaseResources(con, pst, rs);
			} catch (Exception e) {
				logger.error(
						"DAO Exception occured while CheckUserId."
						+ "Exception Message: "
						+ e.getMessage());
				throw new LMSException("DAO Exception: " + e.getMessage(), e);
			}
		}
		return isExist;
	}

	public boolean checkOTPusers(String actorId)throws LMSException
	{
		logger.info("Entered for checking otp users");

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean isOTPUser = false;
		try {
			StringBuffer query= new StringBuffer("SELECT KM_ACTOR_NAME FROM KM_OTP_ACTORS WHERE KM_ACTOR_ID=?"); // AND STATUS = 'A'
			//String sql =
			query.append("  ").toString();
			con = DBConnection.getDBConnection();
			pst = con.prepareStatement(query.append("   ").toString());
			pst.setString(1,actorId);
			rs = pst.executeQuery();
			if (rs.next()) {
				isOTPUser = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			e.printStackTrace();
			logger.error(
					"SQL Exception occured while CheckUserId."
					+ "Exception Message: "
					+ e.getMessage());
			throw new LMSException("SQLException: " + e.getMessage(), e);
		} 
		catch (Exception e) {

			logger.error(
					"Exception occured while CheckUserId."
					+ "Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, pst, rs);
			} catch (Exception e) {
				logger.error(
						"DAO Exception occured while CheckUserId."
						+ "Exception Message: "
						+ e.getMessage());
				throw new LMSException("DAO Exception: " + e.getMessage(), e);
			}
		}
		
		
		return isOTPUser;
	}
	
	
	
	private Connection getConnection() throws LMSException {

		logger.info(
		"Entered getConnection for operation on table:USER_MSTR");

		try {
			return DBConnection.getDBConnection();
		} catch (DAOException e) {

			logger.info("Exception Occured while obtaining connection.");

			throw new LMSException(
					"Exception while trying to obtain a connection",
					e);
		}

	}

	
	public ArrayList viewCircleApprovers() throws LMSException {
		Connection con = null;
		ResultSet rs = null;
		UserDto dto;
		PreparedStatement ps = null;
		try {

			ArrayList userList = new ArrayList();
			StringBuffer query=new StringBuffer(SQL_SELECT_CIRCLE_APPROVER);
			//String sql =
			query.append(" ORDER BY STATUS, USER_LOGIN_ID").toString();
			con = DBConnection.getDBConnection();
			//ps.setInt(1, Integer.parseInt("2"));
			ps = con.prepareStatement(query.append("   ").toString());
			rs = ps.executeQuery();

			logger.info("success");
			while (rs.next()) {
				dto = new UserDto();
				dto.setUserId(rs.getString("USER_ID"));
				dto.setUserLoginId(rs.getString("USER_LOGIN_ID"));
				dto.setUserFname(rs.getString("USER_FNAME"));
				dto.setUserMname(rs.getString("USER_MNAME"));
				dto.setUserLname(rs.getString("USER_LNAME"));
				dto.setUserMobileNumber(rs.getString("USER_MOBILE_NUMBER"));
				dto.setUserEmailid(rs.getString("USER_EMAILID"));
				dto.setStatus(rs.getString("STATUS"));
				dto.setKmActorId(rs.getString("KM_ACTOR_ID"));
				dto.setUserType("CircleApprover");
				dto.setCircleName(rs.getString("CIRCLE_NAME"));

				userList.add(dto);
			}

			return userList;

		} catch (SQLException e) {

			logger.error(
					"SQL Exception occured while Viewing Circle Approvers."
					+ "SQL Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			logger.error(
					" Exception occured while Viewing Circle Approvers."
					+ " Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {
				logger.error(
						"DAO Exception occured while Viewing Circle Approvers."
						+ "DAO Exception Message: "
						+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	}

	
	public ArrayList viewCircleUsers(String circleId) throws LMSException {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		UserDto dto;
		try {
			StringBuffer query=new StringBuffer(SQL_SELECT_CIRCLE_USER);
			logger.info("Inside DAO");
			logger.info("Circle Id :" + circleId);
			ArrayList userList = new ArrayList();
			//String sql =
			query.append(" AND USER_MSTR.CIRCLE_ID = ? AND USER_MSTR.KM_ACTOR_ID != 2 AND USER_MSTR.USER_ID != 1 ORDER BY STATUS, USER_LOGIN_ID ").toString();
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(query.toString());
			ps.setInt(1, Integer.parseInt(circleId));
			rs = ps.executeQuery();

			while (rs.next()) {
				dto = new UserDto();
				dto.setUserId(rs.getString("USER_ID"));
				dto.setUserLoginId(rs.getString("USER_LOGIN_ID"));
				dto.setUserFname(rs.getString("USER_FNAME"));
				dto.setUserMname(rs.getString("USER_MNAME"));
				dto.setUserLname(rs.getString("USER_LNAME"));
				dto.setUserMobileNumber(rs.getString("USER_MOBILE_NUMBER"));
				dto.setUserEmailid(rs.getString("USER_EMAILID"));
				dto.setStatus(rs.getString("STATUS"));
				dto.setKmActorId(rs.getString("KM_ACTOR_ID"));
				dto.setUserType("CircleApprover");
				dto.setCircleName(rs.getString("CIRCLE_NAME"));
				String userType = rs.getString("KM_ACTOR_ID");

				if (userType.equals(Constants.CIRCLE_USER)) {
					dto.setUserType("Circle User");

				} else if (userType.equals(Constants.CIRCLE_CSR)) {
					dto.setUserType("CSR");

				}
				userList.add(dto);

			}
			logger.info("circle user success");
			return userList;
		} catch (SQLException e) {

			logger.error(
					"SQL Exception occured while Viewing Circle Users."
					+ "SQL Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {

			logger.error(
					" Exception occured while Viewing Circle Users."
					+ " Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);

		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {
				logger.error(
						"DAO Exception occured while Viewing Circle Users."
						+ "DAO Exception Message: "
						+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	}


	public UserDto selectUser(String userId, String loginActorId) throws LMSException {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		UserDto dto = null;
		UserDto actordto = null;
		StringBuffer sql = null;
		MasterService mstrService = new MasterServiceImpl();
		try {

			logger.info("Inside DAO");
			logger.info("User Id :" + userId);
			StringBuffer query=new StringBuffer(SQL_SELECT);
			ArrayList circleList = new ArrayList();
			ArrayList<CircleDTO> circleForUserList = new ArrayList<CircleDTO>();
			//ArrayList circleList = new ArrayList();
			
			if(Integer.parseInt(loginActorId) == 1){
				sql =new StringBuffer("select KM_ACTOR_ID, KM_ACTOR_NAME from KM_ACTORS where KM_ACTOR_ID <> 1");
			}
			else if(Integer.parseInt(loginActorId) == 3){
				sql =new StringBuffer("select KM_ACTOR_ID, KM_ACTOR_NAME from KM_ACTORS where KM_ACTOR_ID not in (1,2,3)");
			}
			else{
				sql =new StringBuffer("select KM_ACTOR_ID, KM_ACTOR_NAME from KM_ACTORS where KM_ACTOR_ID not in (1,2,3)");
			}
				
			//String sql = 
		
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(query.toString());
			ps.setInt(1, Integer.parseInt(userId));
			rs = ps.executeQuery();
			logger.info("Query to search user :: "+query);
			
			dto = new UserDto();
			
			while (rs.next()) 
			{
				dto.setUserId(rs.getString("USER_ID"));
				dto.setUserLoginId(rs.getString("USER_LOGIN_ID"));
				dto.setUserFname(rs.getString("USER_FNAME"));
				dto.setUserMname(rs.getString("USER_MNAME"));
				dto.setUserLname(rs.getString("USER_LNAME"));
				dto.setUserMobileNumber(rs.getString("USER_MOBILE_NUMBER"));
				dto.setUserEmailid(rs.getString("USER_EMAILID"));
				dto.setStatus(rs.getString("STATUS"));
				dto.setCircleId(rs.getString("CIRCLE_ID"));
				dto.setKmActorId(rs.getString("KM_ACTOR_ID"));
				dto.setElementId(rs.getString("ELEMENT_ID"));
				dto.setCategoryId(rs.getString("FAV_CATEGORY_ID"));
				dto.setPartnerName(rs.getString("PARTNER_NAME"));
				dto.setKmActorName(rs.getString("KM_ACTOR_NAME"));
				dto.setCircleName(rs.getString("CIRCLE_NAME"));
				dto.setZoneName(rs.getString("ZONE_NAME"));
				dto.setZoneCode(rs.getString("ZONE_CODE"));
				dto.setCityZoneCode(rs.getString("CITY_ZONE_CODE"));
				dto.setCityZoneName(rs.getString("CITY_ZONE_NAME"));
				dto.setZoneFlag(rs.getString("ZONE_FLAG"));
				dto.setLobId(rs.getInt("LOB_ID"));
				dto.setCreatedBy(rs.getString("CREATED_BY"));
				dto.setUpdatedBy(rs.getString("UPDATED_BY"));
				dto.setCreatedDt(rs.getTimestamp("CREATED_DT"));
				dto.setUpdatedDt(rs.getTimestamp("UPDATED_DT"));
			
			}
						
				// Added by Parnika for getting Role List and Circle List
				ps1 = con.prepareStatement(sql.append("    ").toString());
				rs1 = ps1.executeQuery();
				ArrayList list= new ArrayList();
				
				while(rs1.next())
				{
					actordto = new UserDto();
					actordto.setActorId(rs1.getInt("KM_ACTOR_ID"));
					actordto.setActorName(rs1.getString("KM_ACTOR_NAME"));
					list.add(actordto);
					
				}	
				
				circleList = mstrService.getCircleUserList();
				circleForUserList = mstrService.getCircleForUser(dto.getUserLoginId());
				//End of changes by Parnika
				dto.setCircleForUserList(circleForUserList);
				dto.setLobForUserList(mstrService.getLobForUser(dto.getUserLoginId()));
				dto.setActorList(list);
				dto.setCircleList(circleList);
				

			
			logger.info("user data retrieved successfully");
			return dto;
		} catch (SQLException e) 
		{
			e.printStackTrace();
			logger.error("SQL Exception occured while selecting Users."+ "SQL Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			logger.error(" Exception occured while selecting Users."+ " Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);

		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {
				logger.error(
						
						"DAO Exception occured while selecting Users."
						+ "DAO Exception Message: "
						+ e.getMessage());
				e.printStackTrace();
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	}
	
	public ArrayList viewCsrs(String circleId) throws LMSException {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		UserDto dto;
		try {
			StringBuffer query=new StringBuffer(SQL_SELECT_CIRCLE_USER);
			logger.info("Inside DAO");
			logger.info("Circle Id :" + circleId);
			ArrayList userList = new ArrayList();
			String sql =

				query.append(" AND USER_MSTR.CIRCLE_ID = ? AND USER_MSTR.KM_ACTOR_ID = 4  ORDER BY STATUS, USER_LOGIN_ID ").toString();
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(query.append("   ").toString());
			ps.setInt(1, Integer.parseInt(circleId));
			rs = ps.executeQuery();

			while (rs.next()) {
				dto = new UserDto();
				dto.setUserId(rs.getString("USER_ID"));
				dto.setUserLoginId(rs.getString("USER_LOGIN_ID"));
				dto.setUserFname(rs.getString("USER_FNAME"));
				dto.setUserMname(rs.getString("USER_MNAME"));
				dto.setUserLname(rs.getString("USER_LNAME"));
				dto.setUserMobileNumber(rs.getString("USER_MOBILE_NUMBER"));
				dto.setUserEmailid(rs.getString("USER_EMAILID"));
				dto.setStatus(rs.getString("STATUS"));
				dto.setKmActorId(rs.getString("KM_ACTOR_ID"));
				dto.setUserType("CircleApprover");
				dto.setCircleName(rs.getString("CIRCLE_NAME"));
				String userType = rs.getString("KM_ACTOR_ID");

				if (userType.equals(Constants.CIRCLE_USER)) {
					dto.setUserType("Circle User");

				} else if (userType.equals(Constants.CIRCLE_CSR)) {
					dto.setUserType("CSR");

				}
				userList.add(dto);

			}
			logger.info("circle user success");
			return userList;
		} catch (SQLException e) {
			logger.error(
					"SQL Exception occured while Viewing CSRS."
					+ "SQL Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			logger.error(
					" Exception occured while Viewing CSRS."
					+ " Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);

		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {
				logger.error(
						"DAO Exception occured while Viewing CSRS."
						+ "DAO Exception Message: "
						+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	}
	
	public ArrayList viewUsers(String loginActorId, String userId)
	throws LMSException {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		UserDto dto;
		
		try {
			StringBuffer query=new StringBuffer(SQL_SELECT_USERS);
			logger.info("Inside DAO");

			ArrayList userList = new ArrayList();
			
			query.append("  USER_MSTR.CIRCLE_ID = CIRCLE_MSTR.CIRCLE_ID and  USER_MSTR.KM_ACTOR_ID = KM_ACTORS.KM_ACTOR_ID and  USER_MSTR.KM_ACTOR_ID IN (2,3,4,5,6,7,8,9,10,11,12,13,14)   ORDER BY STATUS, USER_LOGIN_ID ").toString();
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(query.append("   ").toString());
			/*if (loginActorId.equals(Constants.CIRCLE_ADMIN)
					|| loginActorId.equals(Constants.CIRCLE_USER)
					|| loginActorId.equals(Constants.LOB_ADMIN))
				ps.setInt(1, Integer.parseInt(userId));*/
			rs = ps.executeQuery();
			logger.info("SQL Stmt :" + query);
			while (rs.next()) {
				dto = new UserDto();
				dto.setUserId(rs.getString("USER_ID"));
				dto.setUserLoginId(rs.getString("USER_LOGIN_ID"));
				dto.setUserFname(rs.getString("USER_FNAME"));
				dto.setUserMname(rs.getString("USER_MNAME"));
				dto.setUserLname(rs.getString("USER_LNAME"));
				dto.setUserMobileNumber(rs.getString("USER_MOBILE_NUMBER"));
				dto.setUserEmailid(rs.getString("USER_EMAILID"));
				dto.setStatus(rs.getString("STATUS"));
				//dto.setKmActorId(rs.getString("KM_ACTOR_ID"));
				dto.setElementName("");
				dto.setPartnerName(rs.getString("PARTNER_NAME"));
				
				/* Added By Parnika for removing Code dependency on role */
				dto.setCircleName(rs.getString("CIRCLE_NAME"));
				dto.setUserType(rs.getString("KM_ACTOR_NAME"));
				/* End of Changes by Parnika */
				
				/*int userType = rs.getInt("KM_ACTOR_ID");

				if (userType == 2) {
					dto.setUserType("Panindia User");

				} else if (userType == 3) {
					dto.setUserType("Circle Coordinator");

				} else if (userType == 4) {
					dto.setUserType("ZBM");

				} else if (userType == 5) {
					dto.setUserType("Marketing Head");

				} else if (userType == 6) {
					dto.setUserType("ZSM");


				} else if (userType == 7) {
					dto.setUserType("Product Head");

				}
				else if (userType == 8) {
					dto.setUserType("Agency Admin");

				}
				else if (userType == 9) {
					dto.setUserType("GIS User");

				}
				else if (userType==10) {
					dto.setUserType("Channel Partners");

				}
				else if (userType == 11) {
					dto.setUserType("Manual Campaign Agencies");

				}
				else if (userType == 12) {
					dto.setUserType("LMS Center Agent");

				}
				else if (userType == 13) {
					dto.setUserType("Outbound Center Agent");

				}
				else if (userType == 14) {
					dto.setUserType("Inbound Center Agent");

				}
				*/
				userList.add(dto);

			}
			logger.info("circle user success");
			return userList;
		} catch (SQLException e) {
			logger.error(
					"SQL Exception occured while Viewing Users."
					+ "SQL Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			logger.error(
					" Exception occured while Viewing Users."
					+ " Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {
				logger.error(
						"DAO Exception occured while Viewing Users."
						+ "DAO Exception Message: "
						+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	}
	
	public String[] getElementsUsers(String elementId) throws LMSException {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String userId = "";
		ArrayList userList = new ArrayList();
		try {
			StringBuffer query=new StringBuffer(SQL_GET_CIRCLE_USERS);
			//String sql = SQL_GET_CIRCLE_USERS;
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(query.append("   ").toString());
			ps.setInt(1, Integer.parseInt(elementId));
			rs = ps.executeQuery();
			logger.info("USER DAO" + elementId);
			int i = 0;
			while (rs.next()) {
				userId = rs.getString("USER_ID");
				userList.add(userId);
			}
			String[] userIds =
				(String[]) userList.toArray(new String[userList.size()]);
			return userIds;
		} catch (SQLException e) {
			logger.error(
					"SQL Exception occured while getting Element Users."
					+ "SQL Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			logger.error(
					" Exception occured while getting Element Users."
					+ " Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {
				logger.error(
						"DAO Exception occured while getting Element Users."
						+ "DAO Exception Message: "
						+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	}
	
	public String getFavCategory(String userId) throws LMSException {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String favCategory = "";
		ArrayList userList = new ArrayList();
		try {
			StringBuffer query=new StringBuffer(SQL_GET_FAV_CATEGORY);
			//String sql = SQL_GET_FAV_CATEGORY;
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(query.append("   ").toString());
			ps.setInt(1, Integer.parseInt(userId));
			rs = ps.executeQuery();
			if (rs.next()) {
				favCategory = rs.getString("FAV_CATEGORY_ID");
			}
			return favCategory;
		} catch (SQLException e) {
			logger.error(
					"SQL Exception occured while getting Fav Category."
					+ "SQL Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			logger.error(
					" Exception occured while getting Fav Category."
					+ " Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {
				logger.error(
						"SQL Exception occured while getting Fav Category."
						+ "SQL Exception Message: "
						+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	}

	
	public int getWrongPwdCount(String userId) throws LMSException {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		int WrongPwdCount = 0;
		try {
			StringBuffer query=new StringBuffer(SQL_GET_LOGIN_ATTEMPTED);
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(query.append("   ").toString());
			ps.setString(1, userId);
			rs = ps.executeQuery();
			if (rs.next()) {
				WrongPwdCount = rs.getInt("LOGIN_ATTEMPTED");
			}
			
		} catch (SQLException e) {
			logger.error(
					"SQL Exception occured while getting Fav Category."
					+ "SQL Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			logger.error(
					" Exception occured while getting Fav Category."
					+ " Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {
				logger.error(
						"SQL Exception occured while getting Fav Category."
						+ "SQL Exception Message: "
						+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
		return WrongPwdCount;
	}
	
	public int updateLoginAttempted(String userId) throws LMSException {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		int loginAttemptUpdateStatus = 0;
		try {
			StringBuffer query=new StringBuffer(UPDATE_LOGIN_ATTEMPTED);
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(query.append("   ").toString());
			ps.setString(1, userId);
			loginAttemptUpdateStatus = ps.executeUpdate();	
		} catch (SQLException e) {
			logger.error(
					"SQL Exception occured while getting Fav Category."
					+ "SQL Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			logger.error(
					" Exception occured while getting Fav Category."
					+ " Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {
				logger.error(
						"SQL Exception occured while getting Fav Category."
						+ "SQL Exception Message: "
						+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
		return loginAttemptUpdateStatus;
	}
	
	public int noOfElementUsers(String elementId) throws LMSException {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		int countUsers = 0;
		try {
			StringBuffer query=new StringBuffer(SQL_COUNT_ELEMENT_USERS);
			//String sql = SQL_COUNT_ELEMENT_USERS;
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(query.append("   ").toString());
			ps.setInt(1, Integer.parseInt(elementId));
			rs = ps.executeQuery();
			if (rs.next()) {
				countUsers = rs.getInt("user_count");
			}
			return countUsers;
		} catch (SQLException e) {
			logger.error(
					"SQL Exception occured while getting noOfElementUsers."
					+ "SQL Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			logger.error(
					" Exception occured while getting noOfElementUsers."
					+ " Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);

		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {
				logger.error(
						"DAO Exception occured while getting noOfElementUsers."
						+ "DAO Exception Message: "
						+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	}

	
	public boolean checkForFavourite(String elementId) throws LMSException {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		boolean isFavourite = false;
		int count = 0;
		try {
			StringBuffer query=new StringBuffer(SQL_CHECK_FOR_FAVOURITE);
			//String sql = SQL_CHECK_FOR_FAVOURITE;
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(query.append("   ").toString());
			ps.setInt(1, Integer.parseInt(elementId));
			rs = ps.executeQuery();
			if (rs.next()) {
				count = rs.getInt("user_count");
			}
			if (count > 0) {
				isFavourite = true;
			}
			return isFavourite;
		} catch (SQLException e) {
			logger.error(
					"SQL Exception occured while checking for Favourite."
					+ "SQL Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			logger.error(
					" Exception occured while checking for Favourite."
					+ " Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);

		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {
				logger.error(
						"DAO Exception occured while checking for Favourite."
						+ "DAO Exception Message: "
						+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	}

	
	public UserMstr getManagers(String circleId, int escCount)
	throws LMSException {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		UserMstr manager = null;
		try {

			StringBuffer sql = new StringBuffer(SQL_GET_MANAGERS);
			if (escCount == 1) {
				sql.append(
				" ( select manager1_user_id from KM_ESCALATION_MATRIX where circle_id =? ) ");
			} else {
				sql.append(
				" ( select manager2_user_id from KM_ESCALATION_MATRIX where circle_id =? ) ");
			}

			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(sql.toString() + "   ");
			ps.setInt(1, Integer.parseInt(circleId));
			rs = ps.executeQuery();
			if (rs.next()) {
				manager = new UserMstr();
				manager.setUserId(rs.getString("USER_ID"));
				manager.setUserFname(rs.getString("USER_FNAME"));
				manager.setUserLoginId(rs.getString("USER_LOGIN_ID"));
				manager.setUserFname(rs.getString("USER_FNAME"));
				manager.setUserLname(rs.getString("USER_LNAME"));
				manager.setUserEmailid(rs.getString("USER_EMAILID"));
			}
			return manager;
		} catch (SQLException e) {
			logger.error(
					"SQL Exception occured while getting managers."
					+ "SQl Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			logger.error(
					" Exception occured while getting managers."
					+ " Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);

		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {
				logger.error(
						"DAO Exception occured while getting managers."
						+ "DAO Exception Message: "
						+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	}

	
	public String getApprover(String circleId) throws LMSException {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String favCategory = "";
		ArrayList userList = new ArrayList();
		try {
			//String sql = SQL_GET_APPROVER;
			StringBuffer query=new StringBuffer(SQL_GET_APPROVER);
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(query.append("   ").toString());
			ps.setInt(1, Integer.parseInt(circleId));
			rs = ps.executeQuery();
			String approver = "";
			if (rs.next()) {
				approver =
					rs.getString("user_fname")
					+ " "
					+ rs.getString("user_lname");
			}
			return approver;
		} catch (SQLException e) {
			logger.error(
					"SQL Exception occured while getting Fav Category."
					+ "SQL Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			logger.error(
					" Exception occured while getting Fav Category."
					+ " Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {
				logger.error(
						"SQL Exception occured while getting Fav Category."
						+ "SQL Exception Message: "
						+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	}


	public int deleteUser(String userLoginId) throws LMSException {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		int count = 0;
		try {
			//String sql = SQL_DELETE_USER;
			StringBuffer query=new StringBuffer(SQL_DELETE_USER);
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(query.append("   ").toString());
			ps.setString(1, userLoginId.toUpperCase().trim());
			count=ps.executeUpdate();

		} catch (SQLException e) {
			logger.error(e);
			logger.error(
					"SQL Exception occured while deleting user."
					+ "SQL Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			logger.error(
					" Exception occured while deleting user."
					+ " Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {
				logger.error(
						"SQL Exception occured while deleting user."
						+ "SQL Exception Message: "
						+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
		return count;

	}

	public int insertUserData(UserMstr user) throws DAOException,LMSException {
		Connection con = null;
		PreparedStatement ps = null;
		int count=0;
		StringBuffer query=null;
		StringBuffer query1=null;
		StringBuffer query2=null;
		StringBuffer query3=null;
		StringBuffer query4=null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		PreparedStatement ps4 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		String cityZoneCode = "";
		String zoneCode	= "";
		String cityCode	= "";
		int circleId = -1;
		String[] multipleCircles = null;
		MasterDao master = MasterDaoImpl.masterDaoInstance();
		
		boolean checkUser = false;
		try {

			query=new StringBuffer(SQL_INSERT_USER_MSTR);
			query1=new StringBuffer(SQL_INSERT_USER_MAPPING_DATA);
			query2=new StringBuffer(SQL_SELECT_CITY_ZONE_CODE);
			query3=new StringBuffer(SQL_SELECT_ZONE_CODE);
			query4=new StringBuffer(SQL_UPDATE_USER_MSTR);

			con = getConnection();
			con.setAutoCommit(false);
			
		/*	// For getting City Zone code 
			if (user.getSelectedTypeId() == 2){				
				ps2 = con.prepareStatement(query2.append(" with ur ").toString());
				ps2.setInt(1, user.getZoneId());
				rs2 = ps2.executeQuery();				
				if(rs2.next()){
					cityZoneCode = rs2.getString("CITY_ZONE_CODE");
					zoneCode = rs2.getString("ZONE_CODE");
					cityCode = rs2.getString("CITY_CODE");
				}
			}			
			// For getting Zone Code
			else if (user.getSelectedTypeId() == 1){				
				ps3 = con.prepareStatement(query3.append(" with ur ").toString());
				ps3.setInt(1, user.getZoneId());
				rs3 = ps3.executeQuery();			
				if(rs3.next()){
					zoneCode = rs3.getString(1);
				}
			}*/
			
			checkUser = CheckUserId(user.getUserLoginId());
			if(!checkUser){
				
				// For inserting Data in User Master
				ps = con.prepareStatement(query.toString());		
				
				ps.setString(1, user.getUserLoginId());
				ps.setString(2, user.getUserFname());
				ps.setString(3, user.getUserMname());
				ps.setString(4, user.getUserLname());
				ps.setString(5, user.getUserMobileNumber());
				ps.setString(6, user.getUserEmailid().trim());
				ps.setString(7, user.getUserPassword().trim());
				ps.setInt(8, Integer.parseInt(user.getCreatedBy()));
				// ps.setInt(9, Integer.parseInt(user.getCircleId()));
				System.out.println("=====================================+++++++++++++++++++++++++++++++++++++++++++++++++++==============="+user.getKmActorId());
				ps.setInt(9, Integer.parseInt(user.getKmActorId()));
				ps.setString(10, user.getPartner());
				ps.setString(11, user.getUserLoginId());
			if("12".equalsIgnoreCase(user.getKmActorId())) {
				System.out.println("====================user.getZoneId()====================="+user.getZoneId());
				ps.setString(12, String.valueOf(user.getZoneId()));
			}else{
				ps.setString(12, user.getCreatedBy());
				}

				ps.executeUpdate();
			}
			else{
				
				// For updating Data in User Master
				ps = con.prepareStatement(query4.toString());

				ps.setString(1, user.getUserFname());
				ps.setString(2, user.getUserMname());
				ps.setString(3, user.getUserLname());
				ps.setString(4, user.getUserMobileNumber());
				ps.setString(5, user.getUserEmailid().trim());
				ps.setInt(6, Integer.parseInt(user.getCreatedBy()));
				ps.setString(7, user.getPartner());
				ps.setString(8, user.getUserLoginId());
				
				ps.executeUpdate();
				
			}

			
			// For inserting data in User Mapping
			
			ps1 = con.prepareStatement(query1.toString());
			
		
		
				ps1.setString(1, user.getUserLoginId());
				ps1.setInt(2, Integer.parseInt(user.getLobId()));
				ps1.setInt(3, Integer.parseInt(user.getCircleId()));
				ps1.setString(4, cityZoneCode);
				ps1.setString(5, cityCode);		
				ps1.setString(6, zoneCode);
				ps1.setString(7, user.getUpdatedBy());
				if(user.getSelectedTypeId()==1){
					
					ps1.setString(8, Constants.ZONE_CODE_FLAG_VALUE);
				}
				else if (user.getSelectedTypeId()==2){
					ps1.setString(8, Constants.CITY_ZONE_CODE_FLAG_VALUE);
				}
				else{
					ps1.setString(8, "");
				}
				
				ps1.executeUpdate();
		

			con.commit();
			count++;

		} catch (SQLException e) {
				try {
				con.rollback();
				}
				catch(Exception e1)
				{
					throw new DAOException("Exception occured while removeCircleAgencyMapping() :  "+ e.getMessage(),e);
				}
			logger.error(e);
			logger.error(
					" Exception occured while creating user."
					+ " Exception Message: "
					+ e.getMessage());
			e.printStackTrace();
			
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
				try {
				con.rollback();
				}
				catch(Exception e1)
				{
					throw new DAOException("Exception occured while removeCircleAgencyMapping() :  "+ e.getMessage(),e);
				}
			logger.error(e);
			logger.error(
					" Exception occured while craeting user."
					+ " Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try{
			con.setAutoCommit(true);
			//DBConnection.releaseResources(con, ps, null);
			}
			catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return count;
	}
	
	/* End of changes by Parnika for LMS Phase 2*/

		public void updateUserStatus(UserMstr userMstrDto) throws LMSException {

		logger.info("Entered update for table USER_MSTR");

		Connection con = null;
		PreparedStatement ps = null;
		String query=null;
		try {
			con = getConnection();
			if(userMstrDto.getUserLoginStatus().equals("Y")){
				query="INSERT INTO KM_LOGIN_DATA(USER_LOGIN_ID, LOGIN_TIME, ELEMENT_ID, FAV_CATEGORY_ID, USER_FNAME, USER_LNAME, SESSION_ID, CLIENT_IP) VALUES(?, current timestamp, ?,?,?,?,?,?) ";

				/* Inserting into KM_LOGIN_DATA table */				
				ps = con.prepareStatement(query);
				ps.setString(1, userMstrDto.getUserLoginId());
				ps.setString(2, "1");
				if(userMstrDto.getFavCategoryId().equals(""))
					ps.setNull(3, Types.INTEGER);
				else
					ps.setString(3, userMstrDto.getFavCategoryId());
				ps.setString(4, userMstrDto.getUserFname());
				ps.setString(5, userMstrDto.getUserLname());
				ps.setString(6, userMstrDto.getSessionID());
				ps.setString(7, userMstrDto.getIpaddress());
				ps.executeUpdate();

			}
			else{
				//System.out.println("Logout Process");
				query="update KM_LOGIN_DATA set LOGOUT_TIME = current timestamp where USER_LOGIN_ID = ?  and LOGOUT_TIME IS NULL and SESSION_ID = ? " ;

				// Updating Logout time

				ps = con.prepareStatement(query);
				ps.setString(1, userMstrDto.getUserLoginId());
				ps.setString(2, userMstrDto.getSessionID());
				ps.executeUpdate();
			}

			// Updating USER_MSTR for Login Status in both the cases
			System.out.println("userMstrDto.getUserLoginId() : "+userMstrDto.getUserLoginId());
			String loginStatusUpdateQuery="update NAS_USER_MSTR set USER_LOGIN_STATUS = ? where USER_LOGIN_ID = ?   " ;
			ps.clearParameters();
			ps = con.prepareStatement(loginStatusUpdateQuery);
			ps.setString(1, userMstrDto.getUserLoginStatus());
			ps.setString(2, userMstrDto.getUserLoginId());
			ps.executeUpdate();
		
			logger.info("User Status Updated");



		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(
					"SQL Exception occured while user status update."
					+ "Exception Message: "
					+ e.getMessage());

			throw new LMSException("SQLException: " + e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(
					"Exception occured while user status update."
					+ "Exception Message: "
					+ e.getMessage());

			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				////////////DBConnection.releaseResources(con, ps, null);
			} catch (Exception e) {
				logger.error(
						"DAO Exception occured while user status update."
						+ "Exception Message: "
						+ e.getMessage());

				throw new LMSException("DAO Exception: " + e.getMessage(), e);
			}
		}
	}

		
		
		public void updateForgotPasswordUser(UserMstr userMstrDto) throws LMSException {

			logger.info("Entered update for table USER_MSTR");

			Connection con = null;
			PreparedStatement ps = null;
			String query=null;
			try {
				con = getConnection();
					query="INSERT INTO KM_LOGIN_DATA(USER_LOGIN_ID,ELEMENT_ID,LOGIN_DATE,CLIENT_IP) VALUES(?,?,?,?) ";

					/* Inserting into KM_LOGIN_DATA table */				
					ps = con.prepareStatement(query);
					ps.setString(1, userMstrDto.getUserLoginId());
					ps.setString(2,"0");
					ps.setDate(3,null);
					ps.setString(4, userMstrDto.getIpaddress());
					ps.executeUpdate();
				logger.info("User Status Updated for Forgot password in KM_LOGIN_DATA table");



			} catch (SQLException e) {
				e.printStackTrace();
				logger.error(
						"SQL Exception occured while user status update for forgot password."
						+ "Exception Message: "
						+ e.getMessage());

				throw new LMSException("SQLException: " + e.getMessage(), e);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(
						"Exception occured while user status update for forgot password."
						+ "Exception Message: "
						+ e.getMessage());

				throw new LMSException("Exception: " + e.getMessage(), e);
			} finally {
				try {
					////////////DBConnection.releaseResources(con, ps, null);
				} catch (Exception e) {
					logger.error(
							"DAO Exception occured while user status update for forgot password."
							+ "Exception Message: "
							+ e.getMessage());

					throw new LMSException("DAO Exception: " + e.getMessage(), e);
				}
			}
		}

		
		
		
		
	// Not getting used
	public void updateSessionExpiry(UserMstr userMstrDto) throws LMSException {

		logger.info("Entered update for table USER_MSTR");

		Connection con = null;
		PreparedStatement ps = null;
		//String sql="";
		String query=null;
		try {

			query=("UPDATE NAS_USER_MSTR SET SESSION_EXPIRY_TIME = current timestamp + 5 minute WHERE USER_ID = ?  ");

			con = getConnection();
			ps = con.prepareStatement(query);
			ps.setString(1, userMstrDto.getUserId());
			ps.executeUpdate();
			logger.info(
			"User Status Updated");

			

		} catch (SQLException e) {

			logger.error(
					"SQL Exception occured while user status update."
					+ "Exception Message: "
					+ e.getMessage());

			throw new LMSException("SQLException: " + e.getMessage(), e);
		} catch (Exception e) {

			logger.error(
					"Exception occured while user status update."
					+ "Exception Message: "
					+ e.getMessage());

			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				////////////DBConnection.releaseResources(con, ps, null);
			} catch (Exception e) {
				logger.error(
						"DAO Exception occured while user status update."
						+ "Exception Message: "
						+ e.getMessage());

				throw new LMSException("DAO Exception: " + e.getMessage(), e);
			}
		}
	}
	 
	public String getUserStatus(String userLoginId) throws LMSException {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String status="";
		try {
			String sql ="SELECT STATUS,KM_ACTOR_ID,USER_LOGIN_STATUS FROM NAS_USER_MSTR WHERE USER_LOGIN_ID = ?  ";
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(sql);
			ps.setString(1, userLoginId);
			rs=ps.executeQuery();
			if(rs.next()){
				if(!rs.getString("STATUS").equalsIgnoreCase("A"))
				{	
					status="D";				
				}
				else if(rs.getString("KM_ACTOR_ID").equals(Constants.CIRCLE_CSR)||rs.getString("KM_ACTOR_ID").equals(Constants.CIRCLE_CSR)){
					status="C";
				}
				else{
					status="A";
				}
			}
			else{
				status="D";
			}

		} catch (SQLException e) {

			logger.error(
					"SQL Exception occured getting user details."
					+ "SQL Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} catch (Exception e) {
			logger.error(
					" Exception occured while deleting user."
					+ " Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {
				logger.error(
						"SQL Exception occured while deleting user."
						+ "SQL Exception Message: "
						+ e.getMessage());
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
		return status;

	}
	//Update the user details in the table USER_MSTR
	public int bulkUpdate(UserMstr dto) throws LMSException {

		logger.info("Entered update for table USER_MSTR");

		Connection con = null;
		PreparedStatement ps = null;
		Connection con1 = null;
		PreparedStatement ps1 = null;
		ResultSet rs1=null;
		int numRows = 0;
		String favCategoryId=null;

		try {


			try{
				con1 = DBConnection.getDBConnection();
				ps = con1.prepareStatement("select element_id  from KM_element_mstr where parent_id = ? and upper(ELEMENT_NAME) = ?  ");
				ps.setString(1, dto.getElementId());
				ps.setString(2, dto.getFavCategoryName().toUpperCase() );
				rs1=ps.executeQuery();
				if(rs1.next()){
					favCategoryId=rs1.getString("ELEMENT_ID");
				}

			}
			catch(Exception e){
				dto.setFavCategoryName("");
				logger.info("Exception occured while fetching fav category Id for Bul category CSR creation");
			}
			finally{
				DBConnection.releaseResources(con1, ps1, rs1);
			}




			StringBuffer query=new StringBuffer(SQL_UPDATE_USER_BULK);

			if (favCategoryId==null)
				query.append(" , PARTNER_NAME = ? , USER_PASSWORD = ?, KM_ACTOR_ID = 4 WHERE USER_LOGIN_ID = ? ").toString();
			else
				query.append(", PARTNER_NAME = ?, FAV_CATEGORY_ID = ?,    USER_PASSWORD = ?, KM_ACTOR_ID = 6 WHERE USER_LOGIN_ID = ? ").toString();

			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(query.append("  ").toString());


			ps.setString(1, dto.getUserFname()); //Error


			if(dto.getUserMname()!=null)
				ps.setString(2, dto.getUserMname());
			else
				ps.setString(2,null);

			ps.setString(3, dto.getUserLname());

			ps.setString(4, dto.getUserMobileNumber());

			ps.setString(5, dto.getUserEmailid());


			ps.setInt(6, Integer.parseInt(dto.getCreatedBy()));

			if(dto.getPartnerName()!=null){
				ps.setString(7, dto.getPartnerName());
			}
			else{
				ps.setString(7, null);
			}

			if (favCategoryId==null) {

				ps.setString(8, dto.getUserPassword());
				ps.setString(9, dto.getUserLoginId());
			} else {

				ps.setString(8, favCategoryId);
				ps.setString(9, dto.getUserPassword());
				ps.setString(10, dto.getUserLoginId());
			}
			numRows = ps.executeUpdate();
			logger.info(
					"Update successful on table:USER_MSTR. Updated:"
					+ numRows
					+ " rows");

		} catch (SQLException e) {

			logger.error(
					"SQL Exception occured while update."
					+ "Exception Message: "
					+ e.getMessage());

			throw new LMSException("SQLException: " + e.getMessage(), e);
		} catch (Exception e) {

			logger.error(
					"Exception occured while update."
					+ "Exception Message: "
					+ e.getMessage());

			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				////////////DBConnection.releaseResources(con, ps, null);
			} catch (Exception e) {
				logger.error(
						"DAO Exception occured while update."
						+ "Exception Message: "
						+ e.getMessage());

				throw new LMSException("DAO Exception: " + e.getMessage(), e);
			}
		}
		return numRows;

	}
	/* KM Phase2 partner serach */


	public ArrayList getPartner() throws LMSException{
		Connection con=null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		ArrayList partnerList=new ArrayList();
		try {

			//String sql = SQL_GET_PARTNER;
			StringBuffer query=new StringBuffer(SQL_GET_PARTNER);
			con=DBConnection.getDBConnection();
			//logger.info(elementId);
			ps=con.prepareStatement(query.append("   ").toString());
			UserMstr user= null;
			//ps.setInt(1, Integer.parseInt(elementId));
			rs = ps.executeQuery();
			while(rs.next()){
				user=new UserMstr();
				user.setPartnerName(rs.getString("PARTNER_NAME"));
				partnerList.add(user); 	
			}
			logger.info("List is returned :"+partnerList.size());	


		} catch (SQLException e) {

			logger.error(e);
			//	logger.severe("SQL Exception occured while find." + "Exception Message: " + e.getMessage());
			throw new LMSException("SQLException: " + e.getMessage(), e);
		} catch (Exception e) {

			logger.error(e);
			//		logger.severe("Exception occured while find." + "Exception Message: " + e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try{
				DBConnection.releaseResources(con,ps,rs);
			}catch(DAOException e){
				logger.error(e);
				throw new LMSException(e.getMessage(),e);
			}				
		}return partnerList;
	}


	public UserMstr getUserDetails(String user_login_id) throws LMSException, DAOException {
		logger.info("Entered CheckUserId for table:USER_MSTR");

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UserMstr user = null;
		try {
			StringBuffer query= new StringBuffer(SQL_SELECT_USERID);
			con = DBConnection.getDBConnection();
			pst = con.prepareStatement(query.append("   ").toString());
			pst.setString(1, user_login_id);
			rs = pst.executeQuery();
			if (rs.next()) {
				user= new UserMstr();
				user.setUserLoginId(rs.getString("USER_LOGIN_ID"));
				user.setElementId(rs.getString("ELEMENT_ID"));
				user.setUserId(rs.getString("USER_ID"));
			}
		} catch (SQLException e) {
			logger.error(e);
			logger.error(
					"SQL Exception occured while CheckUserId."
					+ "Exception Message: "
					+ e.getMessage());
			throw new LMSException("SQLException: " + e.getMessage(), e);
		} catch (DAOException e) {

			logger.error(
					"SQL Exception occured while CheckUserId."
					+ "Exception Message: "
					+ e.getMessage());
			throw new DAOException("SQLException: " + e.getMessage(), e);
		}
		catch (Exception e) {

			logger.error(
					"Exception occured while CheckUserId."
					+ "Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, pst, rs);
			} catch (Exception e) {
				logger.error(
						"DAO Exception occured while CheckUserId."
						+ "Exception Message: "
						+ e.getMessage());
				throw new LMSException("DAO Exception: " + e.getMessage(), e);
			}
		}
		return user;
	}



	public UserMstr CheckUserLoginId(String user_login_id) throws LMSException, DAOException {
		logger.info("Entered CheckUserId for table:USER_MSTR");

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UserMstr user = null;
		try {
			StringBuffer query= new StringBuffer(SQL_SELECT_USERID);
			//String sql =
			query.append(" WHERE NAS_USER_MSTR.USER_LOGIN_ID = ?  ").toString();
			con = DBConnection.getDBConnection();
			pst = con.prepareStatement(query.append("   ").toString());
			pst.setString(1, user_login_id);
			rs = pst.executeQuery();
			if (rs.next()) {
				user= new UserMstr();
				user.setUserLoginId(rs.getString("USER_LOGIN_ID"));
				user.setElementId(rs.getString("ELEMENT_ID"));
			}
		} catch (SQLException e) {
			logger.error(e);
			logger.error(
					"SQL Exception occured while CheckUserId."
					+ "Exception Message: "
					+ e.getMessage());
			throw new LMSException("SQLException: " + e.getMessage(), e);
		} catch (DAOException e) {

			logger.error(
					"SQL Exception occured while CheckUserId."
					+ "Exception Message: "
					+ e.getMessage());
			throw new DAOException("SQLException: " + e.getMessage(), e);
		}
		catch (Exception e) {

			logger.error(
					"Exception occured while CheckUserId."
					+ "Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, pst, rs);
			} catch (Exception e) {
				logger.error(
						"DAO Exception occured while CheckUserId."
						+ "Exception Message: "
						+ e.getMessage());
				throw new LMSException("DAO Exception: " + e.getMessage(), e);
			}
		}
		return user;
	}

	public UserMstr userIdFromMobile(String mobileNumber) throws DAOException {
		logger.info("Entered userIdFromMobile for table:USER_MSTR");

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UserMstr userDto=null;
		try {
			String query= "SELECT USER_ID,USER_LOGIN_ID,ELEMENT_ID,USER_FNAME,USER_LNAME FROM NAS_USER_MSTR WHERE KM_ACTOR_ID IN (2,1) AND USER_MOBILE_NUMBER = ? AND STATUS = 'A'    ";

			con = DBConnection.getDBConnection();
			pst = con.prepareStatement(query);
			pst.setString(1, mobileNumber);
			rs = pst.executeQuery();
			if (rs.next()) {
				userDto = new UserMstr();
				userDto.setUserId(rs.getString("USER_ID"));
				userDto.setElementId(rs.getString("ELEMENT_ID"));
				userDto.setUserFname(rs.getString("USER_FNAME"));
				userDto.setUserLname(rs.getString("USER_LNAME"));
				userDto.setUserLoginId(rs.getString("USER_LOGIN_ID"));

			}
		} catch (SQLException e) {
			logger.error(e);
			logger.error(
					"SQL Exception occured while userIdFromMobile."
					+ "Exception Message: "
					+ e.getMessage());
			throw new LMSException("SQLException: " + e.getMessage(), e);
		} catch (DAOException e) {

			logger.error(
					"SQL Exception occured while userIdFromMobile."
					+ "Exception Message: "
					+ e.getMessage());
			throw new DAOException("SQLException: " + e.getMessage(), e);
		}
		catch (Exception e) {

			logger.error(
					"Exception occured while userIdFromMobile."
					+ "Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, pst, rs);
			} catch (Exception e) {
				logger.error(
						"DAO Exception occured while userIdFromMobile."
						+ "Exception Message: "
						+ e.getMessage());
				throw new LMSException("DAO Exception: " + e.getMessage(), e);
			}
		}
		return userDto;
	}

	public UserMstr userIdFromMobile(String mobileNumber,String actorIDs) throws DAOException {
		logger.info("Entered userIdFromMobile for table:USER_MSTR");

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UserMstr userDto=null;
		try {
			String query= "SELECT USER_ID,USER_LOGIN_ID,ELEMENT_ID,USER_FNAME,USER_LNAME, UM.CIRCLE_ID CIRCLE_ID, CIRCLE_NAME " +
			"FROM NAS_USER_MSTR UM, KM_CIRCLE_MSTR CM  WHERE UM.CIRCLE_ID=CM.CIRCLE_ID  and KM_ACTOR_ID IN ("+actorIDs+") AND "+
			"USER_MOBILE_NUMBER = ? AND UM.STATUS = 'A'    ";
			con = DBConnection.getDBConnection();
			pst = con.prepareStatement(query);
			pst.setString(1, mobileNumber);
			rs = pst.executeQuery();
			if (rs.next()) {
				userDto = new UserMstr();
				userDto.setUserId(rs.getString("USER_ID"));
				userDto.setElementId(rs.getString("ELEMENT_ID"));
				userDto.setUserFname(rs.getString("USER_FNAME"));
				userDto.setUserLname(rs.getString("USER_LNAME"));
				userDto.setUserLoginId(rs.getString("USER_LOGIN_ID"));
				userDto.setCircleId(rs.getString("CIRCLE_ID"));
				userDto.setCircleName(rs.getString("CIRCLE_NAME"));
			}
		} catch (SQLException e) {
			logger.error(e);
			logger.error(
					"SQL Exception occured while userIdFromMobile."
					+ "Exception Message: "
					+ e.getMessage());
			throw new LMSException("SQLException: " + e.getMessage(), e);
		} catch (DAOException e) {

			logger.error(
					"SQL Exception occured while userIdFromMobile."
					+ "Exception Message: "
					+ e.getMessage());
			throw new DAOException("SQLException: " + e.getMessage(), e);
		}
		catch (Exception e) {

			logger.error(
					"Exception occured while userIdFromMobile."
					+ "Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				DBConnection.releaseResources(con, pst, rs);
			} catch (Exception e) {
				logger.error(
						"DAO Exception occured while userIdFromMobile."
						+ "Exception Message: "
						+ e.getMessage());
				throw new LMSException("DAO Exception: " + e.getMessage(), e);
			}
		}
		return userDto;
	}

	public boolean validateLastThreePasswords(String userLoginId, String password) throws LMSException, DAOException {
		Connection con=null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		int count=0;
		try {
			String query=new String(LAST_THREE_PASSWORD_CHECK);
			con=DBConnection.getDBConnection();
			ps=con.prepareStatement(query);
			ps.setString(1,userLoginId);
			ps.setString(2,password);
			rs = ps.executeQuery();	
			if(rs.next())	
				count=Integer.parseInt(rs.getString("matches"));
			if(count == 0)
				return false;
			else
				return true;

		} catch (SQLException e) {

			logger.error(e);
			throw new LMSException("SQLException: " + e.getMessage(), e);
		} catch (Exception e) {

			logger.error(e);
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try{
				DBConnection.releaseResources(con,ps,rs);
			}catch(DAOException e){
				logger.error(e);
				throw new LMSException(e.getMessage(),e);
			}				
		}

	}//validateLastThreePasswords

	public ArrayList getCircles()throws LMSException

	{
		Connection con;
		ResultSet rs;
		PreparedStatement ps;
		con = null;
		rs = null;
		ps = null;
		ArrayList circleList = new ArrayList();
		try
		{

			StringBuffer query = new StringBuffer("SELECT * FROM CIRCLE_MSTR WHERE  STATUS = 'A' ");
			CircleMstrDto dto;
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(query.append("order by lower(CIRCLE_NAME)   ").toString());
			for(rs = ps.executeQuery(); rs.next(); circleList.add(dto))
			{
				dto = new CircleMstrDto();
				dto.setCircleName(rs.getString("CIRCLE_NAME"));
				dto.setCircleId(rs.getString("CIRCLE_ID"));
			}

			logger.info("Circle List is Returned");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			throw new LMSException((new StringBuilder("SQLException: ")).append(e.getMessage()).toString(), e);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new LMSException((new StringBuilder("Exception: ")).append(e.getMessage()).toString(), e);
		}finally{
			try
			{
				DBConnection.releaseResources(con, ps, rs);
			}
			catch(DAOException e)
			{
				e.printStackTrace();
				throw new LMSException(e.getMessage(), e);
			}
		}
		return circleList;

	}

	public void sendMail(String userEmail,
			UserMstr kmUserMstr, String master,
			String activity) throws Exception {

		StringBuffer sbMessage = new StringBuffer();

		String txtMessage = null;
		try {

		String strSubject = PropertyReader.getAppValue("lms.user.create.mail.subject");
		sbMessage.append("Hi \n\n");
		
		sbMessage.append(PropertyReader.getAppValue("lms.user.create.mail.body")+"\n\n");

		sbMessage.append("Login ID : "
				+ kmUserMstr.getUserLoginId()
				+ "\n");

		sbMessage.append("Password : "
				+ kmUserMstr.getUserPassword()
				+ "\n");

		sbMessage.append("\n\nRegards ");
		sbMessage.append("\nLMS Administrator ");
		sbMessage
		.append("\n\n/** This is an Auto generated email.Please do not reply to this.**/");
		txtMessage = sbMessage.toString();
		
		//System.out.println("txtMessage : "+txtMessage);
		
		String strHost = PropertyReader.getAppValue("LOGIN.SMTP");
		String strFromEmail = PropertyReader.getAppValue("SENDER.EMAIL");
		//
		logger.info("Login SMTP:" + strHost + " Mail Id:" + strFromEmail);
		
			Properties prop = System.getProperties();
			prop.put("mail.smtp.host", strHost);
			Session ses = Session.getDefaultInstance(prop, null);
			MimeMessage msg = new MimeMessage(ses);
			msg.setFrom(new InternetAddress(strFromEmail));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
					userEmail));
			msg.setSubject(strSubject);
			msg.setText(txtMessage);
			Transport.send(msg);

		} catch (Exception e) {
			e.printStackTrace();
			//HAVE TO ADD DELETE METHOD FOR ROLLBACK THIS PROCESS
			//loginService.deleteUser(kmUserMstrFormBean.getUserLoginId());
			logger.error("Exception occured in sendMail():" + e.getMessage());
			throw new Exception(e.getMessage());

		}
	}
	
	/* Added by Parnika for LMS Phase 2*/
	
	//Check for the presence of user login id in the database to prevent duplication while inserting


	public boolean CheckUserIdBasedOnLobAndCircle(String userLoginId, String lobId, String actorId) throws DAOException,LMSException {

		logger.info("Entered CheckUserIdBasedOnLobAndCircle for table:USER_MSTR, USER");

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean isExist = false;
		try {
			StringBuffer query= new StringBuffer("SELECT um.USER_ID FROM NAS_USER_MSTR um, USER_MAPPING umap WHERE um.USER_LOGIN_ID = umap.USER_LOGIN_ID AND um.STATUS = 'A' AND um.USER_LOGIN_ID = ? and umap.LOB_ID = ? "); 
			con = DBConnection.getDBConnection();
			pst = con.prepareStatement(query.append("   ").toString());
			pst.setString(1, userLoginId);
			pst.setString(2, lobId);
			rs = pst.executeQuery();
			if (rs.next()) {
				isExist = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			e.printStackTrace();
			logger.error(
					"SQL Exception occured while CheckUserIdBasedOnLobAndCircle."
					+ "Exception Message: "
					+ e.getMessage());
			throw new LMSException("SQLException: " + e.getMessage(), e);
		} catch (DAOException e) {

			logger.error(
					"SQL Exception occured while CheckUserIdBasedOnLobAndCircle."
					+ "Exception Message: "
					+ e.getMessage());
			throw new DAOException("SQLException: " + e.getMessage(), e);
		}
		catch (Exception e) {

			logger.error(
					"Exception occured while CheckUserIdBasedOnLobAndCircle."
					+ "Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
			//DBConnection.releaseResources(con, pst, rs);
			} catch (Exception e) {
				logger.error(
						"DAO Exception occured while CheckUserIdBasedOnLobAndCircle."
						+ "Exception Message: "
						+ e.getMessage());
				throw new LMSException("DAO Exception: " + e.getMessage(), e);
			}
		}
		return isExist;
	}
	
	public boolean checkActorForUserLogin(String userLoginId, String actorId) throws DAOException,LMSException {

		logger.info("Entered checkActorForUserLogin for table");

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean differentActor = false;
		try {
			StringBuffer query= new StringBuffer("SELECT um.USER_ID, um.KM_ACTOR_ID FROM NAS_USER_MSTR um, USER_MAPPING umap WHERE um.USER_LOGIN_ID = umap.USER_LOGIN_ID AND um.STATUS = 'A' AND um.USER_LOGIN_ID = ? "); 
			con = DBConnection.getDBConnection();
			pst = con.prepareStatement(query.append("   ").toString());
			pst.setString(1, userLoginId);
			rs = pst.executeQuery();
			while(rs.next()) {
				if(!actorId.equalsIgnoreCase(rs.getString("KM_ACTOR_ID"))){
					differentActor = true;
				}				
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("SQL Exception occured while checkActorForUserLogin. Exception Message: "+ e.getMessage());
			throw new LMSException("SQLException: " + e.getMessage(), e);
		} catch (DAOException e) {
			logger.error("SQL Exception occured while checkActorForUserLogin. Exception Message: "+ e.getMessage());
			throw new DAOException("SQLException: " + e.getMessage(), e);
		}
		catch (Exception e) {
			logger.error("Exception occured while checkActorForUserLogin. Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				//DBConnection.releaseResources(con, pst, rs);
			} catch (Exception e) {
				logger.error("DAO Exception occured while checkActorForUserLogin. Exception Message: "+ e.getMessage());
				throw new LMSException("DAO Exception: " + e.getMessage(), e);
			}
		}
		return differentActor;
	}
	
	public ArrayList<LOBDTO> countUserLoginId(String userLoginId) throws DAOException,LMSException {

		logger.info("Entered countUserLoginId for table");

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		LOBDTO lobDto=null;
		ArrayList<LOBDTO> listOfLob=new ArrayList<LOBDTO>();
		try {
			StringBuffer query= new StringBuffer("SELECT DISTINCT(um.LOB_ID) As LOB_ID, pl.PRODUCT_LOB  FROM USER_MAPPING um, PRODUCT_LOB pl  WHERE um.LOB_ID = pl.PRODUCT_LOB_ID AND USER_LOGIN_ID = ? "); 
			con = DBConnection.getDBConnection();
			pst = con.prepareStatement(query.append("   ").toString());
			pst.setString(1, userLoginId);
			rs = pst.executeQuery();
			while(rs.next()) {
				lobDto=new LOBDTO();
				lobDto.setLobId(rs.getInt("LOB_ID"));
				lobDto.setLobName(rs.getString("PRODUCT_LOB"));
				listOfLob.add(lobDto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("SQL Exception occured while countUserLoginId. Exception Message: "+ e.getMessage());
			throw new LMSException("SQLException: " + e.getMessage(), e);
		} catch (DAOException e) {

			logger.error("SQL Exception occured while countUserLoginId. Exception Message: "+ e.getMessage());
			throw new DAOException("SQLException: " + e.getMessage(), e);
		}
		catch (Exception e) {

			logger.error("Exception occured while countUserLoginId. Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} finally {
			try {
				//DBConnection.releaseResources(con, pst, rs);
			} catch (Exception e) {
				logger.error("DAO Exception occured while countUserLoginId. Exception Message: "+ e.getMessage());
				throw new LMSException("DAO Exception: " + e.getMessage(), e);
			}
		}
		return listOfLob;
	}
	
	/* End of changes by Parnika */

	public boolean checkDuplicateUserIdLob(String userLoginId, int productLobId)
	throws DAOException {
			Connection con = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			boolean isValid=false;
			
			try {
	
				con = DBConnection.getDBConnection();
				ps = con.prepareStatement("SELECT * FROM NAS_USER_MSTR UM,USER_MAPPING UMP WHERE UM.USER_LOGIN_ID=UMP.USER_LOGIN_ID and UM.STATUS='A' and UMP.LOB_ID=? and UM.USER_LOGIN_ID=?  ");
				ps.setInt(1,productLobId);
				ps.setString(2,userLoginId);
				
				rs = ps.executeQuery();
				
				
				if(rs.next()) 
					isValid = true;
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					DBConnection.releaseResources(con, ps, rs);
				} catch (Exception e) {				
					e.printStackTrace();
				}
			}
			logger.info("checkDuplicateUserIdLob isValid:::: "+isValid);
			return isValid;
			}

			public boolean checkDuplicateUserIdCircle(String userLoginId,
				int productLobId, int circleId) throws DAOException {
			Connection con = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			boolean isValid=false;
			logger.info("checkDuplicateUserIdCircle in user mapping  table");
			try {
				// 
				con = DBConnection.getDBConnection();
				ps = con.prepareStatement("SELECT * FROM USER_MAPPING WHERE USER_LOGIN_ID=? and LOB_ID=? and CIRCLE_ID=?  ");
				ps.setString(1,userLoginId);
				ps.setInt(2,productLobId);
				ps.setInt(3,circleId);
				rs = ps.executeQuery();
				
				if(rs.next()) 
					isValid = true;
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					DBConnection.releaseResources(con, ps, rs);
				} catch (Exception e) {				
					e.printStackTrace();
				}
			}
			logger.info("checkDuplicateUserIdCircle  isValid "+isValid);
			return isValid;
			}
	//added by sugandha for LMS PHASE-2 to check duplicate entries for multi-circle users(Circle coordinator)
	public boolean checkDuplicateUserIdMultiCircle(String userLoginId,
		int productLobId, String circleListStr) throws DAOException {
	Connection con = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	boolean isValid = false;
	logger.info("inside  isValidBulkUserCircle_LOB");
	try {
			con = DBConnection.getDBConnection();
			String[] circleIds = circleListStr.split(",");
			for (int i = 0; i < circleIds.length; i++)
			{
				ps = con.prepareStatement("SELECT * FROM USER_MAPPING WHERE USER_LOGIN_ID=? and LOB_ID=? and CIRCLE_ID=?");
				ps.setString(1,userLoginId);
				ps.setInt(2, productLobId);
				ps.setInt(3, Integer.parseInt(circleIds[i]));
				rs = ps.executeQuery();
				if (rs.next())
				{
					isValid = true;
				}
				else
				{
					isValid = false;
					return isValid;
				}	
			}
		}
	catch (Exception e) {
		e.printStackTrace();
		throw new DAOException("Exception occured while isValidBulkUserCircle_LOB :  "+ e.getMessage(),e);
	} finally {
		try {
			DBConnection.releaseResources(con,ps,rs);
		} catch (Exception e) {				
			throw new DAOException(e.getMessage(), e);
		}
	}
	logger.info("isValidBulkUserCircle_LOB:::::" + isValid);
	return isValid;
	}
	//changes done by sugandha for LMS PHASE-2 to check duplicate entries for multi-circle users(Circle coordinator)	ends here


	//Added by Bhaskar for LMS-PHASE-2 BULK-USER_CREATION 
	public ArrayList insertBulkUserData(ArrayList<UserMstr> userList) throws DAOException 
	{
		Connection con = null;
		PreparedStatement ps = null; PreparedStatement ps1 = null;PreparedStatement ps2 = null;PreparedStatement ps3 = null;PreparedStatement ps4 = null;
		StringBuffer query = null;
		StringBuffer query1 = null;
		StringBuffer query2 = null;
		String eMail = "";
		ArrayList validUserEmailSend = new ArrayList();
		ResultSet rs = null;ResultSet rs1 = null;
		//con = getConnection();
		ResourceBundle rb=ResourceBundle.getBundle("ApplicationResources");
		String zone=rb.getString("user.CircleZoneFlag");
		logger.info("Zone flag"+zone);
		String cityZone=rb.getString("user.CityZoneFlag");
		logger.info("city Zone flag"+cityZone);
		String cityCode=null;
		String zoneCode=null;

		
		logger.info("inside insertBulkUserData daoimpl:::::::::::::::::::::::");
		try
		{	
		
			query = new StringBuffer("INSERT INTO NAS_USER_MSTR(USER_ID, USER_LOGIN_ID, USER_FNAME, USER_MNAME, USER_LNAME, USER_MOBILE_NUMBER, USER_EMAILID, USER_PASSWORD, USER_PSSWRD_EXPRY_DT, CREATED_DT, CREATED_BY, UPDATED_DT, UPDATED_BY, STATUS, KM_ACTOR_ID, LOGIN_ATTEMPTED, LAST_LOGIN_TIME, USER_LOGIN_STATUS,  PARTNER_NAME, PASSWORD_RESET_TIME,OLM_ID) VALUES  (NEXTVAL FOR KM_USER_ID_SEQ,?,?,?,?,?,?,?,current timestamp + 45 days, current timestamp,?,null,null,'A',?,0,null,'N',?,current timestamp,?)");
			
			query1 = new StringBuffer("INSERT INTO NAS_USER_MAPPING(USER_LOGIN_ID, LOB_ID, CIRCLE_ID, CITY_ZONE_CODE, CITY_CODE, ZONE_CODE, TRANSACTION_TIME, UPDATED_BY,ZONE_FLAG) VALUES(?, ?, ?, ?, ?, ?, current timestamp, ?,?)");
			
			query2 = new StringBuffer("INSERT INTO NAS_USER_MAPPING(USER_LOGIN_ID, LOB_ID, CIRCLE_ID, CITY_ZONE_CODE, CITY_CODE, ZONE_CODE, TRANSACTION_TIME, UPDATED_BY,ZONE_FLAG) VALUES(?, ?, ?, ?, ?, ?, current timestamp, ?,?)");
			
			String sql ="SELECT USER_LOGIN_ID FROM NAS_USER_MSTR WHERE USER_LOGIN_ID=?  ";
			String sql1="SELECT CZM.CITY_ZONE_CODE, CZM.CITY_CODE, CM.ZONE_CODE FROM CITY_ZONE_MSTR CZM, CITY_MSTR CM, ZONE_MSTR ZM , CIRCLE_MSTR CIM WHERE ucase(CZM.CITY_ZONE_CODE)=? AND CZM.CITY_CODE = CM.CITY_CODE AND CM.ZONE_CODE = ZM.ZONE_CODE AND ZM.CIRCLE_MSTR_ID = CIM.CIRCLE_MSTR_ID AND CIM.LOB_ID =? AND CIM.CIRCLE_ID = ?   ";
			con = DBConnection.getDBConnection();
			con.setAutoCommit(false);
			ps = con.prepareStatement(query.append("  ").toString());
			ps1 = con.prepareStatement(query1.append("  ").toString());
			ps2 = con.prepareStatement(sql);
			ps4= con.prepareStatement(query2.append("  ").toString());
			
			for (Iterator itr = userList.iterator(); itr.hasNext(); )
			{
					logger.info("inside insertBulkUserData:::: ");
					UserMstr user = (UserMstr)itr.next();
					int actorId = Integer.parseInt(user.getKmActorId());
					logger.info("Actor Id ........."+actorId);
					ps2.setString(1, user.getUserLoginId());
					rs=ps2.executeQuery();
					
					ps3=con.prepareStatement(sql1);
					ps2.clearParameters();
					if(!rs.next())
					{
						try
							{
							logger.info("first time user creation ::"+user.getUserLoginId());
							ps.setString(1, user.getUserLoginId());
							ps.setString(2, user.getUserFname());
							ps.setString(3, user.getUserMname());
							ps.setString(4, user.getUserLname());
							ps.setString(5, user.getUserMobileNumber()); 
							ps.setString(6, user.getUserEmailid().trim());
							ps.setString(7, user.getUserPasswordEncrypted().trim());
							ps.setInt(8, Integer.parseInt(user.getCreatedBy())); 
							ps.setInt(9, Integer.parseInt(user.getKmActorId()));
							ps.setString(10, user.getPartner());
							ps.setString(11, user.getUserLoginId());
							validUserEmailSend.add(user);
							ps.executeUpdate();
							ps.clearParameters();

					
			if(actorId==Constants.Circle_Coordinator_ID)
				{
				
					String[] circleIds = user.getCircleId().split(",");
					
					for (int i = 0; i < circleIds.length; i++)
					{	
						ps4.setString(1, user.getUserLoginId());
						ps4.setInt(2, Integer.parseInt(user.getLob()));
						ps4.setInt(3, Integer.parseInt(circleIds[i].trim()));
						ps4.setString(4, "");
						ps4.setString(5,"");
						ps4.setString(6,"");
						ps4.setString(7, user.getCreatedBy());
						ps4.setString(8,"");
						ps4.executeUpdate();
					}//for ends
					logger.info("=============== For Circle_Coordinator_ID USER_MAPPING_TABLE =======");
					
				}//if ends here
			else	
		if(actorId==Constants.ZBM_ID|| actorId==Constants.ZSM_ID ||actorId==Constants.Zone_Coordinator_ID)
   	 					
				{						
						if(user.getCityZoneCode()!= "" && user.getZoneCode() != "" ){
							
							
							ps3.setString(1,user.getCityZoneCode());
							ps3.setInt(2, Integer.parseInt(user.getLob()));
							ps3.setInt(3, Integer.parseInt(user.getCircleId()));
							rs1=ps3.executeQuery();
							if(rs1.next())
							{
								cityCode=rs1.getString("CITY_CODE");
								zoneCode=rs1.getString("zone_CODE");
																
							}
							
							ps1.setString(1, user.getUserLoginId());
	   	 					ps1.setInt(2, Integer.parseInt(user.getLob()));
	   	 					ps1.setInt(3, Integer.parseInt(user.getCircleId()));
	   	 					ps1.setString(4, user.getCityZoneCode());
	   	 					ps1.setString(5, cityCode);
	   	 					ps1.setString(6, user.getZoneCode());
	   	 					ps1.setString(7, user.getCreatedBy());
	   	 					if(user.getZoneFlag().equalsIgnoreCase(zone))
	   	 					{
	   	 					ps1.setString(8, zone);
	   	 					}
	   	 					else if(user.getZoneFlag().equalsIgnoreCase(cityZone))
	   	 					{
	   	 					ps1.setString(8, cityZone);
	   	 					}
	   	 					//ps1.setString(8, zone);
	   	 					ps1.executeUpdate();
						}
						
						else if(user.getCityZoneCode()!= "" && user.getZoneCode().equals("") && user.getZoneFlag().equalsIgnoreCase(cityZone))
						{
							
							ps3.setString(1,user.getCityZoneCode());
							ps3.setInt(2, Integer.parseInt(user.getLob()));
							ps3.setInt(3, Integer.parseInt(user.getCircleId()));
							rs1=ps3.executeQuery();
							if(rs1.next())
							{
								cityCode=rs1.getString("CITY_CODE");
								
								zoneCode=rs1.getString("zone_CODE");
							
								
							}
							ps1.setString(1, user.getUserLoginId());
   	 						ps1.setInt(2, Integer.parseInt(user.getLob()));
   	 						ps1.setInt(3, Integer.parseInt(user.getCircleId()));
   	 						ps1.setString(4, user.getCityZoneCode());
   	 						ps1.setString(5, cityCode);
   	 						ps1.setString(6, zoneCode);
	   	 					ps1.setString(7, user.getCreatedBy());
	   	 					ps1.setString(8, cityZone);
	     	 				ps1.executeUpdate();
							
							
						}
						else if(user.getZoneCode() != ""  && user.getCityZoneCode().equals("") &&  user.getZoneFlag().equalsIgnoreCase(zone) )
						{
							
							ps1.setString(1, user.getUserLoginId());
	   	 					ps1.setInt(2, Integer.parseInt(user.getLob()));
	   	 					ps1.setInt(3, Integer.parseInt(user.getCircleId()));
	   	 					ps1.setString(4, user.getCityZoneCode());
	   	 					ps1.setString(5, "");
	   	 					ps1.setString(6, user.getZoneCode());
	   	 					ps1.setString(7, user.getCreatedBy());
	   	 					ps1.setString(8,zone);
	   	 					ps1.executeUpdate();
	   	 					
						}
						}
				
   	 		else
   	 			{
   	 					
   	 					ps4.setString(1, user.getUserLoginId());
   	 					ps4.setInt(2, Integer.parseInt(user.getLob()));
   	 					ps4.setInt(3, Integer.parseInt(user.getCircleId()));
   	 					ps4.setString(4, "");
   	 					ps4.setString(5, "");
   	 					ps4.setString(6, "");
   	 					ps4.setString(7, user.getCreatedBy());
   	 					ps4.setString(8, "");
   	 					ps4.executeUpdate();
   	 					ps4.clearParameters();
   	 			}
				con.commit();
				}
				catch (Exception e)
				{
					con.rollback();
					e.printStackTrace();
					throw new DAOException("Exception occured while inserting data in user mstr :  "+ e.getMessage(),e);
				}
				finally{
					con.setAutoCommit(true);
				}
				}
					else{
						// Calling Update user
						updateBulkUserCreation(user);
					}
		}//for ends here
			
		/* Commented by Parnika for sending mail after generation of error logs 
		try
	      {
				for (Iterator<UserMstr> itr = validUserEmailSend.iterator();itr.hasNext();) 
				{	
					UserMstr user = (UserMstr) itr.next();	
					eMail = user.getUserEmailid();
					sendMail(eMail, user,"Sending Mail", "User Creation");
				}
			}
	     catch (Exception e) {
	    	  e.printStackTrace();
	    	  throw new DAOException("Exception occured while sending mail :  "+ e.getMessage(),e);
	      }
	      
	       End of comments by Parnika */
			
	}//try ends here
	
	catch (SQLException e) 
			{
	    	e.printStackTrace();
	    	logger.error(" SQL Exception occured during bulk user creation Exception Message: " + e.getMessage());
	    	//throw new DAOException("Exception: " + e.getMessage(), e);
			}   
	finally 
			{
		
		  	////////////DBConnection.releaseResources(con, ps, null);
		 	DBConnection.releaseResources(con, ps1, null);
		 	DBConnection.releaseResources(con, ps2, rs);
			}
			return validUserEmailSend;
		}//changes by bhaskar for LMS PHASE-2 bulk-user-creation to create user ends here.
		

//Added by amarjeet 
	public UserDto getUserDetailsForEdit(String userId ,String loginActorId ,int lobId) throws LMSException, DAOException {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		UserDto dto = null;
		UserDto actordto = null;
		StringBuffer sql = null;
		MasterService mstrService = new MasterServiceImpl();
		try {

			logger.info("Inside DAO");
			logger.info("User Id :" + userId);
			StringBuffer query=new StringBuffer(SQL_SELECT_FOR_EDIT);
			ArrayList circleList = new ArrayList();
			ArrayList<CircleDTO> circleForUserList = new ArrayList<CircleDTO>();
			//ArrayList circleList = new ArrayList();
			
			if(Integer.parseInt(loginActorId) == 1){
				sql =new StringBuffer("select KM_ACTOR_ID, KM_ACTOR_NAME from KM_ACTORS where KM_ACTOR_ID <> 1");
			}
			else if(Integer.parseInt(loginActorId) == 2)
			{
				sql =new StringBuffer("select KM_ACTOR_ID, KM_ACTOR_NAME from KM_ACTORS where KM_ACTOR_ID not in (1)");
			}
			else if(Integer.parseInt(loginActorId) == 3){
				sql =new StringBuffer("select KM_ACTOR_ID, KM_ACTOR_NAME from KM_ACTORS where KM_ACTOR_ID not in (1,2)");
			}
			else{
				sql =new StringBuffer("select KM_ACTOR_ID, KM_ACTOR_NAME from KM_ACTORS where KM_ACTOR_ID not in (1,2,3)");
			}
				
			//String sql = 
		
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement(query.toString());
			ps.setInt(1, Integer.parseInt(userId));
			ps.setInt(2, lobId);
			rs = ps.executeQuery();
			logger.info("Query to search user :: "+query);
			
			dto = new UserDto();
			
			while (rs.next()) 
			{
				dto.setUserId(rs.getString("USER_ID"));
				dto.setUserLoginId(rs.getString("USER_LOGIN_ID"));
				dto.setUserFname(rs.getString("USER_FNAME"));
				dto.setUserMname(rs.getString("USER_MNAME"));
				dto.setUserLname(rs.getString("USER_LNAME"));
				dto.setUserMobileNumber(rs.getString("USER_MOBILE_NUMBER"));
				dto.setUserEmailid(rs.getString("USER_EMAILID"));
				dto.setStatus(rs.getString("STATUS"));
				dto.setCircleId(rs.getString("CIRCLE_MSTR_ID"));
				dto.setKmActorId(rs.getString("KM_ACTOR_ID"));
				dto.setElementId(rs.getString("ELEMENT_ID"));
				dto.setCategoryId(rs.getString("FAV_CATEGORY_ID"));
				dto.setPartnerName(rs.getString("PARTNER_NAME"));
				dto.setKmActorName(rs.getString("KM_ACTOR_NAME"));
				dto.setCircleName(rs.getString("CIRCLE_NAME"));
				dto.setZoneName(rs.getString("ZONE_NAME"));
				dto.setZoneCode(rs.getString("ZONE_CODE"));
				dto.setCityZoneCode(rs.getString("CITY_ZONE_CODE"));
				dto.setCityZoneName(rs.getString("CITY_ZONE_NAME"));
				dto.setZoneFlag(rs.getString("ZONE_FLAG"));				
				dto.setLobName(rs.getString("PRODUCT_LOB"));
				dto.setLobId(rs.getInt("LOB_ID"));
				dto.setCreatedBy(rs.getString("CREATED_BY"));
				dto.setUpdatedBy(rs.getString("UPDATED_BY"));
				dto.setCreatedDt(rs.getTimestamp("CREATED_DT"));
				dto.setUpdatedDt(rs.getTimestamp("UPDATED_DT"));
			}
						
				// Added by Parnika for getting Role List and Circle List
				ps1 = con.prepareStatement(sql.append("    ").toString());
				rs1 = ps1.executeQuery();
				ArrayList list= new ArrayList();
				
				while(rs1.next())
				{
					actordto = new UserDto();
					actordto.setActorId(rs1.getInt("KM_ACTOR_ID"));
					actordto.setActorName(rs1.getString("KM_ACTOR_NAME"));
					list.add(actordto);
					
				}	
				
				circleList = mstrService.getCircleUserList();
				circleForUserList = mstrService.getCircleForUserLob(dto.getUserLoginId(), dto.getLobId());
				//End of changes by Parnika
				dto.setCircleForUserList(circleForUserList);
				dto.setLobForUserList(mstrService.getLobForUser(dto.getUserLoginId()));
				dto.setActorList(list);
				dto.setCircleList(circleList);
				

			
			logger.info("user data retrieved successfully");
			return dto;
		} catch (SQLException e) 
		{
			e.printStackTrace();
			logger.error("SQL Exception occured while selecting Users."+ "SQL Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			logger.error(" Exception occured while selecting Users."+ " Exception Message: "+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);

		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {
				logger.error(
						
						"DAO Exception occured while selecting Users."
						+ "DAO Exception Message: "
						+ e.getMessage());
				e.printStackTrace();
				throw new LMSException("Exception: " + e.getMessage(), e);
			}
		}
	}

public void updateBulkUserCreation(UserMstr bulkDto) throws DAOException {
		
		//System.out.println("userLoginId"+userLoginId);

		Connection con = null;
		PreparedStatement ps = null; PreparedStatement ps1 = null;PreparedStatement ps2 = null;PreparedStatement ps3 = null;PreparedStatement ps4 = null;
		
		String eMail = "";
		ArrayList validUserEmailSend = new ArrayList();
		ResultSet rs = null;ResultSet rs1 = null;
		ResourceBundle rb=ResourceBundle.getBundle("ApplicationResources");
		String zone=rb.getString("user.CircleZoneFlag");
		logger.info("Zone flag"+zone);
		String cityZone=rb.getString("user.CityZoneFlag");
		logger.info("city Zone flag"+cityZone);
		String cityCode=null;
		String zoneCode=null;
		StringBuffer query1 = null;
		StringBuffer query2 = null;
		int i=0;
		
		int paramIndex=1;
		
		String sql1="SELECT CZM.CITY_ZONE_CODE, CZM.CITY_CODE, CM.ZONE_CODE FROM CITY_ZONE_MSTR CZM, CITY_MSTR CM, ZONE_MSTR ZM , CIRCLE_MSTR CIM WHERE ucase(CZM.CITY_ZONE_CODE)=? AND CZM.CITY_CODE = CM.CITY_CODE AND CM.ZONE_CODE = ZM.ZONE_CODE AND ZM.CIRCLE_MSTR_ID = CIM.CIRCLE_MSTR_ID AND CIM.LOB_ID =? AND CIM.CIRCLE_ID = ?   ";
		logger.info("inside updateBulkUserData daoimpl:::::::::::::::::::::::");
		try
		{	
			query1 = new StringBuffer("INSERT INTO USER_MAPPING(USER_LOGIN_ID, LOB_ID, CIRCLE_ID, CITY_ZONE_CODE, CITY_CODE, ZONE_CODE, TRANSACTION_TIME, UPDATED_BY,ZONE_FLAG) VALUES(?, ?, ?, ?, ?, ?, current timestamp, ?,?)");
			
			query2 = new StringBuffer("INSERT INTO USER_MAPPING(USER_LOGIN_ID, LOB_ID, CIRCLE_ID, CITY_ZONE_CODE, CITY_CODE, ZONE_CODE, TRANSACTION_TIME, UPDATED_BY,ZONE_FLAG) VALUES(?, ?, ?, ?, ?, ?, current timestamp, ?,?)");
			
			con = DBConnection.getDBConnection();
			con.setAutoCommit(false);
			
			//updated query
			StringBuffer UPDATE_USER_MASTER = new StringBuffer("UPDATE NAS_USER_MSTR SET ");
			
//			Iterator<UserMstr> itr=listBulkDto.iterator();
//			while(itr.hasNext())
//			{
//				UserMstr dt=itr.next();
//				//System.out.println("update query"+UPDATE_USER_MASTER);
//				System.out.println("1st field"+dt.getUserFname());
//				System.out.println("2nd field"+dt.getUserMname());
//				System.out.println("3rd field"+dt.getUserEmailid());
//				
//				
//			}
			
			
			
			if (bulkDto.getUserFname() != null && !(bulkDto.getUserFname().trim().equals("")) ){
				UPDATE_USER_MASTER.append(" USER_FNAME= ? ");
			}
			if (bulkDto.getUserMname() != null && !(bulkDto.getUserMname().trim().equals("")) ){
				UPDATE_USER_MASTER.append(", USER_MNAME= ? ");
			}
			if (bulkDto.getUserLname() != null && !(bulkDto.getUserLname().trim().equals("")) ){
				UPDATE_USER_MASTER.append(", USER_LNAME= ? ");
			}
			if (bulkDto.getUserMobileNumber() != null && !(bulkDto.getUserMobileNumber().trim().equals("")) ){
				UPDATE_USER_MASTER.append(", USER_MOBILE_NUMBER= ? ");
			}
			if (bulkDto.getUserEmailid() != null && !(bulkDto.getUserEmailid().trim().equals("")) ){
				UPDATE_USER_MASTER.append(", USER_EMAILID= ? ");
			}
			if (bulkDto.getUpdatedBy()!= null && !(bulkDto.getUpdatedBy().trim().equals("")) ){
				UPDATE_USER_MASTER.append(", UPDATED_BY= ? ");
			}
			
			if (bulkDto.getPartnerName()!= null && !(bulkDto.getPartnerName().trim().equals("")) ){
				UPDATE_USER_MASTER.append(", PARTNER_NAME= ? ");
			}
			
			UPDATE_USER_MASTER.append("WHERE USER_LOGIN_ID = ? AND STATUS='A'");
			
			ps = con.prepareStatement(UPDATE_USER_MASTER.toString());		
			
			if (bulkDto.getUserFname() != null && !(bulkDto.getUserFname().trim().equals("")) ){
				ps.setString(paramIndex++, bulkDto.getUserFname());
			}
			if (bulkDto.getUserMname() != null && !(bulkDto.getUserMname().trim().equals("")) ){
				ps.setString(paramIndex++, bulkDto.getUserMname());
			}
			if (bulkDto.getUserLname() != null && !(bulkDto.getUserLname().trim().equals("")) ){
				ps.setString(paramIndex++, bulkDto.getUserLname());
			}
			if (bulkDto.getUserMobileNumber() != null && !(bulkDto.getUserMobileNumber().trim().equals("")) ){
				ps.setString(paramIndex++, bulkDto.getUserMobileNumber());
			}
			if (bulkDto.getUserEmailid() != null && !(bulkDto.getUserEmailid().trim().equals("")) ){
				ps.setString(paramIndex++, bulkDto.getUserEmailid());
			}
			if (bulkDto.getUpdatedBy()!= null && !(bulkDto.getUpdatedBy().trim().equals("")) ){
				ps.setString(paramIndex++, bulkDto.getUpdatedBy());
			}
			
			if (bulkDto.getPartnerName()!= null && !(bulkDto.getPartnerName().trim().equals("")) ){
				ps.setString(paramIndex++, bulkDto.getPartnerName());
			}
			//System.out.println("userLoginId"+userLoginId);
			ps.setString(paramIndex++, bulkDto.getUserLoginId());
			i=ps.executeUpdate();
						
			//User mapping inserted query
			
			ps1 = con.prepareStatement(query1.append("  ").toString());
			
			ps4 = con.prepareStatement(query2.append("  ").toString());
			ps2=con.prepareStatement(sql1);
			ps2.setString(1,bulkDto.getCityZoneCode());
			ps2.setInt(2, Integer.parseInt(bulkDto.getLob()));
			ps2.setInt(3, Integer.parseInt(bulkDto.getCircleId()));
			rs1=ps2.executeQuery();
			
			int actorId = Integer.parseInt(bulkDto.getKmActorId());
			logger.info("Actor Id ........."+actorId);
			if(actorId==Constants.Circle_Coordinator_ID)
			{
			
				String[] circleIds = bulkDto.getCircleId().split(",");
				
				for (int j = 0; j < circleIds.length; j++)
				{	
					ps4.setString(1, bulkDto.getUserLoginId());
					ps4.setInt(2, Integer.parseInt(bulkDto.getLob()));
					ps4.setInt(3, Integer.parseInt(circleIds[i]));
					ps4.setString(4, "");
					ps4.setString(5,"");
					ps4.setString(6,"");
					ps4.setString(7, bulkDto.getCreatedBy());
					ps4.setString(8,"");
					ps4.executeUpdate();
				}//for ends
				logger.info("=============== For Circle_Coordinator_ID USER_MAPPING_TABLE =======");
				
			}//if ends here
		else	
			if(actorId==Constants.ZBM_ID|| actorId==Constants.ZSM_ID ||actorId==Constants.Zone_Coordinator_ID)
	 					
			{				
					if(bulkDto.getCityZoneCode()!= "" && bulkDto.getZoneCode() != "" ){
						
						if(rs1.next())
						{
							cityCode=rs1.getString("CITY_CODE");
							zoneCode=rs1.getString("zone_CODE");						
							
						}
						
						ps1.setString(1, bulkDto.getUserLoginId());
   	 					ps1.setInt(2, Integer.parseInt(bulkDto.getLob()));
   	 					ps1.setInt(3, Integer.parseInt(bulkDto.getCircleId()));
   	 					ps1.setString(4, bulkDto.getCityZoneCode());
   	 					ps1.setString(5, cityCode);
   	 					ps1.setString(6, bulkDto.getZoneCode());
   	 					ps1.setString(7, bulkDto.getCreatedBy());
   	 					if(bulkDto.getZoneFlag().equalsIgnoreCase(zone))
   	 					{
   	 					ps1.setString(8, zone);
   	 					}
   	 					else if(bulkDto.getZoneFlag().equalsIgnoreCase(cityZone))
   	 					{
   	 					ps1.setString(8, cityZone);
   	 					}
   	 					//ps1.setString(8, zone);
   	 					ps1.executeUpdate();
					}
					
					else if(bulkDto.getCityZoneCode()!= "" && bulkDto.getZoneCode().equals("") && bulkDto.getZoneFlag().equalsIgnoreCase(cityZone))
					{
						
						if(rs1.next())
						{
							cityCode=rs1.getString("CITY_CODE");
							zoneCode=rs1.getString("zone_CODE");							
						}
							ps1.setString(1, bulkDto.getUserLoginId());
	 						ps1.setInt(2, Integer.parseInt(bulkDto.getLob()));
	 						ps1.setInt(3, Integer.parseInt(bulkDto.getCircleId()));
	 						ps1.setString(4, bulkDto.getCityZoneCode());
	 						ps1.setString(5, cityCode);
	 						ps1.setString(6, zoneCode);
	   	 					ps1.setString(7, bulkDto.getCreatedBy());
	   	 					ps1.setString(8, cityZone);
	     	 				ps1.executeUpdate();
						
						
					}
					else if(bulkDto.getZoneCode() != ""  && bulkDto.getCityZoneCode().equals("") &&  bulkDto.getZoneFlag().equalsIgnoreCase(zone) )
					{
						
						ps1.setString(1, bulkDto.getUserLoginId());
   	 					ps1.setInt(2, Integer.parseInt(bulkDto.getLob()));
   	 					ps1.setInt(3, Integer.parseInt(bulkDto.getCircleId()));
   	 					ps1.setString(4, bulkDto.getCityZoneCode());
   	 					ps1.setString(5, "");
   	 					ps1.setString(6, bulkDto.getZoneCode());
   	 					ps1.setString(7, bulkDto.getCreatedBy());
   	 					ps1.setString(8,zone);
   	 					ps1.executeUpdate();
   	 					
					}
					}
			
	 		else
	 			{
	 					
	 					ps4.setString(1, bulkDto.getUserLoginId());
	 					ps4.setInt(2, Integer.parseInt(bulkDto.getLob()));
	 					ps4.setInt(3, Integer.parseInt(bulkDto.getCircleId()));
	 					ps4.setString(4, "");
	 					ps4.setString(5, "");
	 					ps4.setString(6, "");
	 					ps4.setString(7, bulkDto.getCreatedBy());
	 					ps4.setString(8, "");
	 					ps4.executeUpdate();
	 					ps4.clearParameters();
	 			}
			con.commit();
			
		}
		catch(Exception e){
			e.printStackTrace();
			try
			{
			con.rollback();
			}
			catch(Exception exx)
			{
				exx.printStackTrace();
			}
			
			
		}
		finally
		{
			System.out.println("connection closed ::::");
			//////////////DBConnection.releaseResources(con, ps, null);
		//	DBConnection.releaseResources(con, ps1, null);
		//	DBConnection.releaseResources(con, ps2, null);
		//	DBConnection.releaseResources(con, null, rs1);
		 	}
	}

public String getUserLoginId(String userId) throws LMSException {
	Connection con = null;
	ResultSet rs = null;
	PreparedStatement ps = null;
	
	try {
		//String sql = SQL_GET_APPROVER;
		StringBuffer query=new StringBuffer(SQL_GET_USERLOGIN_ID);
		con = DBConnection.getDBConnection();
		ps = con.prepareStatement(query.toString());
		ps.setInt(1, Integer.parseInt(userId));
		rs = ps.executeQuery();
		String loginId = "";
		if (rs.next()) {
			loginId = rs.getString("USER_LOGIN_ID");
				
		}
		return loginId;
	} catch (SQLException e) {
		logger.error(
				"SQL Exception occured while getting login id."
				+ "SQL Exception Message: "
				+ e.getMessage());
		throw new LMSException("Exception: " + e.getMessage(), e);
	} catch (Exception e) {
		logger.error(
				" Exception occured while getting login id"
				+ " Exception Message: "
				+ e.getMessage());
		throw new LMSException("Exception: " + e.getMessage(), e);
	} finally {
		try {
		//	DBConnection.releaseResources(con, ps, rs);
		} catch (Exception e) {
			logger.error(
					"SQL Exception occured while getting login id."
					+ "SQL Exception Message: "
					+ e.getMessage());
			throw new LMSException("Exception: " + e.getMessage(), e);
		}
	}
}


public UserMstr getUserFnameDetails(String userFname) throws DAOException {
	logger.info("Entered CheckUserId for table:USER_MSTR");

	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	UserMstr user = null;
	
	try {
		StringBuffer query= new StringBuffer(SQL_SELECT_USERFNAME);
		con = DBConnection.getDBConnection();
		pst = con.prepareStatement(query.append("   ").toString());
		
		pst.setString(1, userFname.toUpperCase());
		rs = pst.executeQuery();
		if (rs.next()) {
			user= new UserMstr();
			user.setUserLoginId(rs.getString("USER_LOGIN_ID"));
			user.setElementId(rs.getString("ELEMENT_ID"));
			user.setUserId(rs.getString("USER_ID"));
		}
	} catch (SQLException e) {
		logger.error(e);
		logger.error(
				"SQL Exception occured while CheckUserId."
				+ "Exception Message: "
				+ e.getMessage());
		throw new LMSException("SQLException: " + e.getMessage(), e);
	} catch (DAOException e) {

		logger.error(
				"SQL Exception occured while CheckUserId."
				+ "Exception Message: "
				+ e.getMessage());
		throw new DAOException("SQLException: " + e.getMessage(), e);
	}
	catch (Exception e) {

		logger.error(
				"Exception occured while CheckUserId."
				+ "Exception Message: "
				+ e.getMessage());
		throw new LMSException("Exception: " + e.getMessage(), e);
	} finally {
		try {
		//	DBConnection.releaseResources(con, pst, rs);
		} catch (Exception e) {
			logger.error(
					"DAO Exception occured while CheckUserId."
					+ "Exception Message: "
					+ e.getMessage());
			throw new LMSException("DAO Exception: " + e.getMessage(), e);
		}
	}
	return user;
}
}
