package com.ibm.nas.services.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.ibm.appsecure.util.ReadResource;
import com.ibm.nas.dao.LoginDao;
import com.ibm.nas.dao.PopulateUserDao;
import com.ibm.nas.dao.impl.LoginDaoImpl;
import com.ibm.nas.dao.impl.PopulateUserDaoImpl;
import com.ibm.nas.dto.CircleMstr;
import com.ibm.nas.dto.Login;
import com.ibm.nas.dto.UserMstr;
import com.ibm.nas.exception.DAOException;
import com.ibm.nas.exception.LMSException;
import com.ibm.nas.services.LoginService;

public class LoginServiceImpl implements LoginService{
	private static final Logger LOGGER;

	static {

		LOGGER = Logger.getLogger(LoginServiceImpl.class);
	}
	
	/**
	 * @param loginId
	 */
	public boolean isValidUser(String loginId) throws LMSException {
	
		LoginDao kmLoginDaoImpl = LoginDaoImpl.loginDaoInstance();//chnaged by srikant new LoginDaoImpl();
		
		try
		{
			return kmLoginDaoImpl.isValidUser(loginId);
		}catch(LMSException ee)
		{
			throw ee;
		}
	}
	
	/**
	 * @param OlmId
	 */
	public boolean isValidOlmId(String OlmId) throws LMSException {
	
		LoginDao kmLoginDaoImpl = LoginDaoImpl.loginDaoInstance();//chnaged by srikant new LoginDaoImpl();
		
		try
		{
			return kmLoginDaoImpl.isValidOlmId(OlmId);
		}catch(LMSException ee)
		{
			throw ee;
		}
	}
	

	/**
	 * 
	 * @param dto
	 */

	public UserMstr populateUserDetails(Login login) throws LMSException, DAOException {
		LOGGER.info("Populating user credentials" + login);
		PopulateUserDao populateDao = PopulateUserDaoImpl.populateUserDaoInstance();//changed by srikant new PopulateUserDaoImpl();
		UserMstr userBean = new UserMstr();
		CircleMstr circleBean = new CircleMstr();
		List dataList = null;
		try {
			dataList = new ArrayList();
			dataList = populateDao.populateValues(login);
			
			System.out.println(dataList+"===dataList");
			userBean = setUserBeanData(dataList, userBean);
			//circleBean=setCircleBeanData(dataList, circleBean);
			////System.out.println("Circle Flag: "+circleBean.getSingleSignInFlag()+" User Login Status: "+userBean.getUserLoginStatus());
		/*	if(circleBean.getSingleSignInFlag().equals("Y")&&userBean.getUserLoginStatus().equals("Y")){
				throw new KmException("User already logged in.");
			}else{
				userBean.setUserLoginStatus("Y");
			}*/
			
		} catch (Exception ex) {
			ex.printStackTrace();
			if (ex instanceof DAOException) {
				LOGGER.error(" DAOException occured in populateUserDetails():" + ex.getMessage());
				throw new DAOException(ex.getMessage());
			} else {
				LOGGER.error(" Exception occured in populateUserDetails():" + ex.getMessage());
				throw new LMSException(ex.getMessage());
			}

		}
		LOGGER.info("Populating user credentials successfully");
		return userBean;
	}

	private UserMstr setUserBeanData(List dataList, UserMstr userBean) throws LMSException {
		LOGGER.debug("Setting user bean");
		HashMap categoryMap = null;
		ArrayList categoryDesc = null;
		LoginDao dao = LoginDaoImpl.loginDaoInstance();//chnaged by srikant new LoginDaoImpl();
		try {
			int count = 0;
			ArrayList moduleList = (ArrayList) dataList.get(count);
			userBean.setModuleList(moduleList);
			System.out.println("MODULEEE DTATTTT"+moduleList);
			moduleList = null;
			 System.out.println(dataList+"****");
			List userDataList = (ArrayList) dataList.get(++count);
			HashMap userData = (HashMap) userDataList.get(0);
			System.out.println(userData+"===userData"+userDataList);
			userBean.setUserId(userData.get("USER_ID").toString());
			//userBean.setCircleId(userData.get("CIRCLE_ID").toString());
			userBean.setKmActorId(userData.get("KM_ACTOR_ID").toString());
			userBean.setCreatedBy(userData.get("CREATED_BY").toString());
			userBean.setCreatedDt((Timestamp)userData.get("CREATED_DT"));
			userBean.setUserLoginId(userData.get("USER_LOGIN_ID").toString());
		
			//Commented By Karan to get the correct Last Login Time on 29 Jan 2013
			
		/*	if (null == userData.get("LAST_LOGIN_TIME")) {
				userBean.setLastLoginTime((Timestamp)null);
			} else {
				userBean.setLastLoginTime((Timestamp)userData.get("LAST_LOGIN_TIME"));
			}*/
			
			Timestamp lastLoginTime = dao.getLastLogin(userBean.getUserLoginId());
			
			SimpleDateFormat sdf;
			sdf = new SimpleDateFormat("EEE, d MMM yyyy hh:mm aaa");
			String lastLogTime;
			    
			if (null == lastLoginTime) {
				userBean.setLastLoginTime("");
			} else {
				lastLogTime = sdf.format(lastLoginTime); 
				userBean.setLastLoginTime(lastLogTime);
			}

			userBean.setLoginAttempted(userData.get("LOGIN_ATTEMPTED").toString());
			userBean.setStatus(userData.get("STATUS").toString());
			if (null == userData.get("UPDATED_DT")) {
				userBean.setUpdatedDt((Timestamp)null);
			} else {
				userBean.setUpdatedDt((Timestamp)userData.get("UPDATED_DT"));
			}
			if (null == userData.get("UPDATED_BY")) {
				userBean.setUpdatedBy("");
			} else {
				userBean.setUpdatedBy(userData.get("UPDATED_BY").toString());
			}
			if (null == userData.get("USER_EMAILID")) {
				userBean.setUserEmailid("");
			} else {
				userBean.setUserEmailid(userData.get("USER_EMAILID").toString());
			}			
			userBean.setUserFname(userData.get("USER_FNAME").toString());
			if (null == userData.get("USER_MNAME")) {
				userBean.setUserMname("");
			} else {
				userBean.setUserMname(userData.get("USER_MNAME").toString());
			}
			if (null == userData.get("USER_LOGIN_STATUS")) {
				userBean.setUserLoginStatus("");
			} else {
				userBean.setUserLoginStatus(userData.get("USER_LOGIN_STATUS").toString());
			}
			userBean.setUserLname(userData.get("USER_LNAME").toString());
			
			if (null == userData.get("USER_MOBILE_NUMBER")) {
				userBean.setUserMobileNumber("");
			} else {
				userBean.setUserMobileNumber(userData.get("USER_MOBILE_NUMBER").toString());
			}				
			userBean.setUserPassword(userData.get("USER_PASSWORD").toString());
			userBean.setUserPsswrdExpryDt((Timestamp)userData.get("USER_PSSWRD_EXPRY_DT"));
			if (null == userData.get("FAV_CATEGORY_ID")) {
				userBean.setFavCategoryId("");
			} else {
				userBean.setFavCategoryId(userData.get("FAV_CATEGORY_ID").toString());
			}			
			if (null == userData.get("ELEMENT_ID")) {
				userBean.setElementId("");
			} else {
				userBean.setElementId(userData.get("ELEMENT_ID").toString());
			}	

			List actorList = (ArrayList) dataList.get(++count);
			HashMap actorData = (HashMap) actorList.get(0);
			
			
			userBean.setKmActorName(actorData.get("KM_ACTOR_NAME").toString());
			userBean.setKmActorId(actorData.get("KM_ACTOR_ID").toString());

			List circleList = (ArrayList) dataList.get(++count);
			HashMap circleData = (HashMap) circleList.get(0);
			
			
			
			userBean.setElementName("");
			
			/* Added by Parnika for LMS Phase 2 */						
			//userBean.setLobList(dao.getUserLobList(userData.get("USER_LOGIN_ID").toString()));			
			//userBean.setCircleList(dao.getUserCircleList(userData.get("USER_LOGIN_ID").toString()));						
			/* End of changes by Parnika */
		
			


		} catch (Exception ex) {
			ex.printStackTrace();
			if (ex instanceof DAOException) {
				throw new LMSException(ex.getMessage());
			} else {
				LOGGER.error(" Exception occured in setUserBeanData():" + ex.getMessage());
			}

		}
		LOGGER.info(" setUserBeanData:user information successfully set  ");
		return userBean;
	}
	
	private CircleMstr setCircleBeanData(List dataList, CircleMstr circleBean) throws LMSException {
		LOGGER.debug("Setting circle bean");
		HashMap categoryMap = null;
		ArrayList categoryDesc = null;
		try {
			int count = 0;
			ArrayList moduleList = (ArrayList) dataList.get(count);
			List userDataList = (ArrayList) dataList.get(++count);
			List actorList = (ArrayList) dataList.get(++count);
			List circleList = (ArrayList) dataList.get(++count);
			HashMap circleData = (HashMap) circleList.get(0);
			circleBean.setCircleName(circleData.get("CIRCLE_NAME").toString());
			circleBean.setCircleId(circleData.get("CIRCLE_ID").toString());
			circleBean.setSingleSignInFlag(circleData.get("SINGLE_SIGN_IN").toString());

		} catch (Exception ex) {
			ex.printStackTrace();
			if (ex instanceof DAOException) {
				throw new LMSException(ex.getMessage());
			} else {
				LOGGER.error(" Exception occured in setCircleBeanData():" + ex.getMessage());
			}

		}
		LOGGER.info(" setUserBeanData:user information successfully set  ");
		return circleBean;
	}	
	public String getWarning(Object ob) {
				int expiredDays = Integer.parseInt(ReadResource.getValue("GSD",
						"PwdExpiryLimit"));
				String msg = "";
				Timestamp timestamp = null;
				if (ob instanceof Timestamp) {
					timestamp = (Timestamp) ob;
				} else {
					timestamp = new Timestamp(((Date) ob).getTime());
				}

				Calendar c = GregorianCalendar.getInstance();
				c.set(c.get(GregorianCalendar.YEAR), c.get(GregorianCalendar.MONTH), c
						.get(GregorianCalendar.DATE)
						- expiredDays + 5); // +5 = It will start warning 5 days before expiration
				Date d = c.getTime();
				Timestamp timestamp2 = new Timestamp(d.getTime());
				if (timestamp2.getTime() > timestamp.getTime()) {
					c.setTime(new Date(timestamp.getTime()));
					c.set(c.get(GregorianCalendar.YEAR),
							c.get(GregorianCalendar.MONTH), c
									.get(GregorianCalendar.DATE)
									+ expiredDays);
					msg = "Change your password.Your iLMS password is valid till :"
							+ getStringFromDate(c.getTime());
				}
				return msg;
			}
	
	public String getStringFromDate(java.util.Date d) {
			String dateTime = null;
			try {

				if (d == null) {
					return "";
				}
				Calendar cal = Calendar.getInstance();
				cal.setTime(d);
				SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(
						"dd MMM yyyy");
				dateTime = dateTimeFormatter.format(cal.getTime());
			} catch (Exception e) {
				e.printStackTrace();
			}

			return dateTime;
		}		
	
		public String getConfigValue(String string) throws LMSException {
			LoginDao dao = LoginDaoImpl.loginDaoInstance();//chnaged by srikant new LoginDaoImpl();
			return 	dao.getConfigValue(string);
		
		}

		public ArrayList getEmailId(String userName) throws LMSException {
			LoginDao dao = LoginDaoImpl.loginDaoInstance();//chnaged by srikant new LoginDaoImpl();
			return 	dao.getEmailId(userName);
		}

		public void updatePassword(String userName, String encPassword) throws LMSException {
			LoginDao kmLoginDaoImpl = LoginDaoImpl.loginDaoInstance();//chnaged by srikant new LoginDaoImpl();
			kmLoginDaoImpl.updatePassword(userName,encPassword);
			
		}

		public int getExpiredDocumentCount(UserMstr userBean) throws LMSException {
			LoginDao kmLoginDaoImpl = LoginDaoImpl.loginDaoInstance();//chnaged by srikant new LoginDaoImpl();
			
			return kmLoginDaoImpl.getExpiredDocumentCount(userBean);
		}	
		
		 public List getFavorites(int userId) throws LMSException
		 {
			 LOGGER.info(" Inside getFavorites to get favorites of a user  ");
				LoginDao kmLoginDaoImpl = LoginDaoImpl.loginDaoInstance();//chnaged by srikant new LoginDaoImpl();
				
				return kmLoginDaoImpl.getFavorites(userId);
				 
		 }

}
