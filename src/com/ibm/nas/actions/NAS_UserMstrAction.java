package com.ibm.nas.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

import com.ibm.appsecure.util.Encryption;
import com.ibm.appsecure.util.IEncryption;
import com.ibm.nas.common.Constants;
import com.ibm.nas.common.PropertyReader;
import com.ibm.nas.common.Utility;
import com.ibm.nas.dto.LOBDTO;
import com.ibm.nas.dto.UserDto;
import com.ibm.nas.dto.UserMstr;
import com.ibm.nas.exception.LMSException;
import com.ibm.nas.forms.UserMstrFormBean;
import com.ibm.nas.services.LoginService;
import com.ibm.nas.services.MasterService;
import com.ibm.nas.services.UserMstrService;
import com.ibm.nas.services.impl.LoginServiceImpl;
import com.ibm.nas.services.impl.MasterServiceImpl;
import com.ibm.nas.services.impl.UserMstrServiceImpl;

public class NAS_UserMstrAction extends DispatchAction {
	
	
	private static final Logger logger;

	static {

		logger = Logger.getLogger(NAS_UserMstrAction.class);
	}
	
	private static String CREATEUSER_FAILURE = "CreateUserFailure";
	private static String USERCREATED_SUCCESS = "UserCreatedSuccess";
	private static String VIEW_USER = "viewUser";
	public ActionForward init(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {

			ActionForward forward = new ActionForward(); // return value
			HttpSession session = request.getSession();
			UserMstrFormBean kmUserMstrFormBean = (UserMstrFormBean) form;
			UserMstr sessionUserBean =
				(UserMstr) session.getAttribute("USER_INFO");
			
			saveToken(request);

			try {
				
				initializeParameter(request, sessionUserBean, kmUserMstrFormBean);
			
				logger.info(
					sessionUserBean.getUserLoginId()
						+ " Entered into the init method of KmUserMstrAction");
				String loginUserActorId = sessionUserBean.getKmActorId();
				kmUserMstrFormBean.setKmLoginActorId(loginUserActorId);
				session.setAttribute("LOGIN_USER_ACTOR_ID", loginUserActorId);
				
				forward = mapping.findForward("initCreateUser");
				return forward;

			} catch (Exception e) {

				logger.error(
					"Exception occured while initializing the create user page");
				forward = mapping.findForward("error");
				return forward;
			}

		}

		private void initializeParameter(HttpServletRequest request,
				UserMstr userBean,
				UserMstrFormBean formBean) throws LMSException {
			ResourceBundle bundle = ResourceBundle.getBundle("ApplicationResources");
			MasterService mstrService = new MasterServiceImpl();
			ArrayList lobList = new ArrayList();
			ArrayList actorList = new ArrayList();
			ArrayList circleList = new ArrayList();
			
			
			HttpSession session = request.getSession();
			UserMstr sessionUserBean =
				(UserMstr) session.getAttribute("USER_INFO");
			saveToken(request);
			try{
			System.out.println("inside user action--------------");
			
			/* Added by Parnika for LMS Phase 2 , Introducing LOB list based on actor id*/
			
			if(sessionUserBean.getKmActorId().equalsIgnoreCase(Constants.CIRCLE_COORDINATOR_ACTOR)){
				lobList = mstrService.getLobListBasedOnUser(sessionUserBean.getLobList());
			}
			else {
				lobList = mstrService.getLobList();
			}		
			formBean.setLobList(lobList);
			circleList = mstrService.getCircleUserList();
			formBean.setCircleList(circleList);
			sessionUserBean.getUserLoginId();
		
			
			/* End of changes by Parnika */
			
			actorList = mstrService.getActorList(sessionUserBean.getKmActorId());
			if(actorList !=null && actorList.size() > 0)
				formBean.setActorList(actorList);
			request.setAttribute("circleList",circleList);
			}catch(Exception e){
				logger.info("Exception occured while initializing Parameter ");
				
			}
		}

		/**	
		*Creates new users. 
		**/
		public ActionForward insert(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {

			ActionForward forward = new ActionForward(); // return value
			HttpSession session = request.getSession();
			ActionErrors errors = new ActionErrors();
			ActionMessages messages = new ActionMessages();
			UserMstrFormBean kmUserMstrFormBean = (UserMstrFormBean) form;
			UserMstr sessionUserBean =
				(UserMstr) session.getAttribute("USER_INFO");
			UserMstrService createUserService = new UserMstrServiceImpl();
			MasterService mstrService = new MasterServiceImpl();
			
			String loginUserActorId = sessionUserBean.getKmActorId();
			session.setAttribute("LOGIN_USER_ACTOR_ID", loginUserActorId);
			
			
			System.out.println("===========================usermstraction======================================================="+loginUserActorId);
			int countId = 0;
			List<LOBDTO> lobIdsOfUsr=null;
			
			/*if ( !isTokenValid(request) ) {
				  return mapping.findForward("error");
				}*/
			try {

				logger.info(
					sessionUserBean.getUserLoginId()
						+ " Entered into the insert method of KmUserMstrAction");

				
				/*Modified by Karan 3 Jan 2013*/
				
				String pwd = generatePassword(kmUserMstrFormBean.getUserLoginId());
				
				IEncryption encpwd = new Encryption();
				String encpass = encpwd.generateDigest(pwd);
				kmUserMstrFormBean.setUserPassword(pwd);
				
				kmUserMstrFormBean.setCreatedBy(sessionUserBean.getUserId());

				
				/* Added by Parnika for LMS Phase 2 */
				
				kmUserMstrFormBean.setUpdatedBy(sessionUserBean.getUserLoginId());
				kmUserMstrFormBean.setLobId(kmUserMstrFormBean.getSelectedLobId());
				kmUserMstrFormBean.setCircleId(kmUserMstrFormBean.getCircleMstrId());
				kmUserMstrFormBean.setZoneId(kmUserMstrFormBean.getSelectedZoneId());
				
				/* End of changes by Parnika */
				
				kmUserMstrFormBean.setKmActorId(kmUserMstrFormBean.getKmActorId());			

				logger.info("loginactorId = " + kmUserMstrFormBean.getKmActorId());
				
				/*GSDService gSDService = new GSDService();
				IEncryption encrypt = new Encryption();

				logger.debug("Getting digest for user password");
				gSDService.validateCredentials(
					kmUserMstrFormBean.getUserLoginId(),
					kmUserMstrFormBean.getUserPassword());
				logger.info(
					"Start:" + kmUserMstrFormBean.getUserPassword() + ":End");
				//				Encrypting user password
				
				logger.info("\n\n\n\nValidating Password.....\n\n\n\n");*/
				

				// Security finding trans token impl...		
					 	if(!isTokenValid(request))
					     {
							kmUserMstrFormBean.setCircleList(mstrService.getCircleUserList());
							kmUserMstrFormBean.setActorList(mstrService.getActorList(loginUserActorId));
							forward = mapping.findForward(CREATEUSER_FAILURE);				
					    	errors.add("errors.incorrectPassword",new ActionError("msg.security.id021"));
							saveErrors(request, errors);
							return forward;
					     }
				
				UserMstr userMstrDto = new UserMstr();

				userMstrDto.setUserLoginId(kmUserMstrFormBean.getUserLoginId().toUpperCase());
				userMstrDto.setUserFname(kmUserMstrFormBean.getUserFname());
				userMstrDto.setUserMname(kmUserMstrFormBean.getUserMname());
				userMstrDto.setUserLname(kmUserMstrFormBean.getUserLname());
				userMstrDto.setUserMobileNumber(kmUserMstrFormBean.getUserMobileNumber());
				userMstrDto.setUserEmailid(kmUserMstrFormBean.getUserEmailid());
				
				userMstrDto.setUserPassword(encpass);
				userMstrDto.setCreatedBy(kmUserMstrFormBean.getCreatedBy());
				userMstrDto.setStatus("A");
				
				/* Added by Parnika for LMS phase 2 */
				
				userMstrDto.setLobId(kmUserMstrFormBean.getLobId());
				userMstrDto.setSelectedTypeId(kmUserMstrFormBean.getSelectedTypeId());
				userMstrDto.setZoneId(kmUserMstrFormBean.getZoneId());
				userMstrDto.setUpdatedBy(kmUserMstrFormBean.getUpdatedBy());
				userMstrDto.setLoginUserActorId(loginUserActorId);
				
				/* If Circle is not selected , then circle will be Pan India */
				
				userMstrDto.setCircleId(kmUserMstrFormBean.getCircleId());
			
				/* End of changes by Parnika for LMS Phase 2*/
				
				userMstrDto.setKmActorId(kmUserMstrFormBean.getKmActorId());
				LoginService loginService = new LoginServiceImpl();
				userMstrDto.setPartner(kmUserMstrFormBean.getPartner());
				
				//Calling the service that check for duplicate user login id
				/*if (createUserService
					.checkDuplicateUserLogin(userMstrDto.getUserLoginId())) {

					messages.add("msg1", new ActionMessage("createUser.duplicate"));
					kmUserMstrFormBean.setUserStatus(
						"User Login Id Already exists");
					kmUserMstrFormBean.setUserLoginId("");
					kmUserMstrFormBean.setUserPassword("");
					kmUserMstrFormBean.setUserConfirmPassword("");
					kmUserMstrFormBean.setKmActorId(userMstrDto.getKmActorId());
					kmUserMstrFormBean.setCircleList(mstrService.getCircleUserList());
					kmUserMstrFormBean.setActorList(mstrService.getActorList(loginUserActorId));
					forward = mapping.findForward(CREATEUSER_FAILURE);
				} */
				
				//Calling the service that check for duplicate user login id for LMS Phase 2 based on LOB and Circle
				if (createUserService.checkDuplicateLOBCircleUserLogin(userMstrDto.getUserLoginId(),userMstrDto.getLobId(),userMstrDto.getKmActorId()))				
				{
					messages.add("msg1", new ActionMessage("createUser.duplicateLob"));
					kmUserMstrFormBean.setUserStatus(
						"User Login Id Already exists for selected LOB.");
					kmUserMstrFormBean.setUserLoginId("");
					kmUserMstrFormBean.setUserPassword("");
					kmUserMstrFormBean.setUserConfirmPassword("");
					kmUserMstrFormBean.setKmActorId(userMstrDto.getKmActorId());
					kmUserMstrFormBean.setCircleList(mstrService.getCircleUserList());
					kmUserMstrFormBean.setActorList(mstrService.getActorList(loginUserActorId));
					forward = mapping.findForward(CREATEUSER_FAILURE);
				}
				
				/* Added by Parnika for LMS Phase 2 */
				else if (createUserService.checkActorForUserLogin(userMstrDto.getUserLoginId(), userMstrDto.getKmActorId()))				
				{
					messages.add("msg1", new ActionMessage("createUser.actor"));
					kmUserMstrFormBean.setUserStatus(
						"User Login Id exists for a different User Role.");
					kmUserMstrFormBean.setUserLoginId("");
					kmUserMstrFormBean.setUserPassword("");
					kmUserMstrFormBean.setUserConfirmPassword("");
					kmUserMstrFormBean.setKmActorId(userMstrDto.getKmActorId());
					kmUserMstrFormBean.setCircleList(mstrService.getCircleUserList());
					kmUserMstrFormBean.setActorList(mstrService.getActorList(loginUserActorId));
					forward = mapping.findForward(CREATEUSER_FAILURE);
				}
				/* End of changes by Parnika */
				
				// Calling the service that check for Login id as valid OLM ID
				else if("Y".equalsIgnoreCase(PropertyReader.getAppValue("doLdapValidation")) && !loginService.isValidOlmId(kmUserMstrFormBean.getUserLoginId()))
				 {
						messages.add("msg1", new ActionMessage("createUser.invalidUserId"));
						kmUserMstrFormBean.setUserStatus("User Login Id should have active OLM Id");
						
						kmUserMstrFormBean.setCircleList(mstrService.getCircleUserList());
						kmUserMstrFormBean.setActorList(mstrService.getActorList(loginUserActorId));
						
						forward = mapping.findForward(CREATEUSER_FAILURE);
				}    // LDAP validation finish
				
				   else
				   {
					/*Calling create user service  */
					int userCount =createUserService.insertUserData(userMstrDto);
					if(userCount==-1){
						forward = mapping.findForward(CREATEUSER_FAILURE);
						errors.add(
								"errors.max_user_limit",
								new ActionError("max.report.admin.message",PropertyReader.getAppValue("max.report.admin.limit")));
							saveErrors(request, errors);
							kmUserMstrFormBean.setUserLoginId("");
							kmUserMstrFormBean.setUserFname("");
							kmUserMstrFormBean.setUserMname("");
							kmUserMstrFormBean.setUserLname("");
							kmUserMstrFormBean.setUserEmailid("");
							kmUserMstrFormBean.setUserMobileNumber("");
							kmUserMstrFormBean.setUserPassword("");
							kmUserMstrFormBean.setUserConfirmPassword("");
							kmUserMstrFormBean.setCircleId("-1");
							kmUserMstrFormBean.setPartner("");
							kmUserMstrFormBean.setKmActorId("");
							kmUserMstrFormBean.setActorList(mstrService.getActorList(loginUserActorId));
							kmUserMstrFormBean.setCircleList(mstrService.getCircleUserList());
							resetToken(request);
							return (forward);
					}

					//countId = createUserService.countUserLoginId(userMstrDto.getUserLoginId());
					/*	lobIdsOfUsr = createUserService.countUserLoginId(userMstrDto.getUserLoginId());
						Iterator<LOBDTO> itr=lobIdsOfUsr.iterator();
						StringBuffer sb=new StringBuffer("");
						while(itr.hasNext())
						{
							LOBDTO lobDto=itr.next();
							sb.append(lobDto.getLobName()+",");
							countId++;
						}
						System.out.println("==========================================LOB is================================"+sb.toString());
						//builder.substring(0, builder.length()-1)
						kmUserMstrFormBean.setLobIdMsg(" for LOB "+sb.substring(0, sb.length()-1));*/
						messages.add("msg1",new ActionMessage("user.created"));
						kmUserMstrFormBean.setUserStatus("New User Created");
						resetToken(request);
						
						
						if(countId == 1){
							// Sending mail to user for password
							try{
								
							String eMail = kmUserMstrFormBean.getUserEmailid();
							String mailFlag = mstrService.getParameterName(PropertyReader.getAppValue("leadRegisteration.smsFlag"));
							if(mailFlag.equalsIgnoreCase("Y")){
							 sendMail(eMail, kmUserMstrFormBean, "Sending Mail", "User Creation");
							}
							}
							catch(Exception e){
								e.printStackTrace();
								messages.add("msg10",new ActionMessage("createUser.mailError"));
								
							}
						}
					saveMessages(request, messages);
					kmUserMstrFormBean.setUserLoginId("");
					kmUserMstrFormBean.setUserFname("");
					kmUserMstrFormBean.setUserMname("");
					kmUserMstrFormBean.setUserLname("");
					kmUserMstrFormBean.setUserEmailid("");
					kmUserMstrFormBean.setUserMobileNumber("");
					kmUserMstrFormBean.setUserPassword("");
					kmUserMstrFormBean.setUserConfirmPassword("");
					kmUserMstrFormBean.setCircleId("-1");
					kmUserMstrFormBean.setPartner("");
					kmUserMstrFormBean.setKmActorId("");
					
					/* Added by Parnika for LMS Phase 2 */
					// kmUserMstrFormBean.setCircleList(mstrService.getCircleUserList());
					kmUserMstrFormBean.setSelectedLobId("");
					kmUserMstrFormBean.setLobList(mstrService.getLobList());
					/* End of changes By parnika */
									
					kmUserMstrFormBean.setActorList(mstrService.getActorList(loginUserActorId));
					logger.info(
						"getUser() method : successful : redirected to target : UserEditJsp ");
					forward = mapping.findForward(USERCREATED_SUCCESS);
					
				}

				saveMessages(request, messages);
			} 
			
			catch (Exception e) {
				e.printStackTrace();

				kmUserMstrFormBean.setCircleList(mstrService.getCircleUserList());
				kmUserMstrFormBean.setUserLoginId("");
				kmUserMstrFormBean.setUserFname("");
				kmUserMstrFormBean.setUserMname("");
				kmUserMstrFormBean.setUserLname("");
				kmUserMstrFormBean.setUserEmailid("");
				kmUserMstrFormBean.setUserMobileNumber("");
				kmUserMstrFormBean.setUserPassword("");
				kmUserMstrFormBean.setUserConfirmPassword("");
				kmUserMstrFormBean.setCircleId("-1");
				kmUserMstrFormBean.setPartner("");
				kmUserMstrFormBean.setKmActorId("");
				kmUserMstrFormBean.setActorList(mstrService.getActorList(loginUserActorId));
				if (e instanceof IllegalAccessException) {
					logger.error(
						"createUser() method : caught IllegalAccessException Exception while using BeanUtils.copyProperties()");
				}
				if (e instanceof InvocationTargetException) {
					logger.error(
						"createUser() method : caught InvocationTargetException Exception while using BeanUtils.copyProperties()");
				}
				if (e instanceof LMSException) {

					logger.error(
						"createUser() method : caught KmException Exception while using user creation");
				}
				logger.error("Exception From LoginAction" + e);

				forward = mapping.findForward(CREATEUSER_FAILURE);
			}
			return (forward);

		}
		/**
		*Lists the users depending upon the type of the logged in user
		**/
		private static String generatePassword(String userLoginId)
		 {		 
			String specialChars="@#!";
			String lowerChars = "qwertyuipasdfghjklzxcvbnm";
			int alphabetIndex = (int)(Math.random()*23);			
			int specialCharsIndex = (int)(Math.random()*2);		
			StringBuilder strPassword = new StringBuilder();
			strPassword.append(lowerChars.toUpperCase().charAt(alphabetIndex)+""+ (11+(int)(Math.random()*80))+""+lowerChars.toUpperCase().charAt((int)(Math.random()*25))+""+((11+(int)(Math.random()*80))+7) +""+ specialChars.charAt(specialCharsIndex)+""+ lowerChars.charAt(alphabetIndex+1)); 
			return strPassword.toString().replace("0", "5");
		 }	
		
		public String sendMail(String userEmail,
				UserMstrFormBean kmUserMstrFormBean, String master,
				String activity) throws Exception {

			String message = null;
			StringBuffer sbMessage = new StringBuffer();

			String txtMessage = null;

			String strSubject = PropertyReader.getAppValue("lms.user.create.mail.subject");
			sbMessage.append("Hi \n\n");
			
			sbMessage.append(PropertyReader.getAppValue("lms.user.create.mail.body")+"\n\n");

			sbMessage.append("Login ID : "
					+ kmUserMstrFormBean.getUserLoginId()
					+ "\n");

			sbMessage.append("Password : "	
					+ kmUserMstrFormBean.getUserPassword()
					+ "\n");

			sbMessage.append("\n\nRegards ");
			sbMessage.append("\nLMS Administrator ");
			sbMessage
					.append("\n\n/** This is an Auto generated email.Please do not reply to this.**/");
			txtMessage = sbMessage.toString();
			String strHost = PropertyReader.getAppValue("LOGIN.SMTP");
			String strFromEmail = PropertyReader.getAppValue("SENDER.EMAIL");
			//
			logger.info("Login SMTP:" + strHost + " Mail Id:" + strFromEmail);
			try {

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
			return message;
		}
		
		 

		public ActionForward initSearchUser(
				ActionMapping mapping,
				ActionForm form,
				HttpServletRequest request,
				HttpServletResponse response)
				throws Exception {
			logger.info("Inside initSearchUser of User Master Action");
				UserMstrFormBean kmUserMstrFormBean = (UserMstrFormBean) form;
				UserMstr userDto = null;
				ActionForward forward = new ActionForward(); 
				forward =  mapping.findForward("searchUser");
				saveToken(request);
				if(null == kmUserMstrFormBean.getUserLoginId())
				{
					kmUserMstrFormBean.reset(mapping, request);
					kmUserMstrFormBean.setInitStatus("true");	
					if(Utility.isValidRequest(request)) {
			        	return mapping.findForward("error");
			        }
				}else
				{				
					kmUserMstrFormBean.setInitStatus("false");
					System.out.println("searching details for user :: "+kmUserMstrFormBean.getUserLoginId());
					System.out.println("searching details for user :: "+kmUserMstrFormBean.getUserFname());
					// check if user exist in the system				
					UserMstrService userService = new UserMstrServiceImpl();
					if(!"".equals(kmUserMstrFormBean.getUserLoginId()))
					{
						
					userDto=userService.getUserDetails(kmUserMstrFormBean.getUserLoginId());
					}
					if(!"".equals(kmUserMstrFormBean.getUserFname()))
					{
						userDto=userService.getUserFnameDetails(kmUserMstrFormBean.getUserFname());
					}
					 if(userDto==null)
					{
						System.out.println("User not found.");
						// User not found
						ActionErrors errors = new ActionErrors();
						errors.add("msg10",new ActionError("user.not.found"));
						saveErrors(request, errors);					
					}
					else
					{	
						System.out.println("User id found :"+userDto.getUserId());
						// forward to view/edit Users	
						kmUserMstrFormBean.setSelectedUserId(userDto.getUserId());
						forward = viewUser(mapping, kmUserMstrFormBean, request, response);
					}
				}
				return forward;
			}
		public ActionForward viewUser(
				ActionMapping mapping,
				ActionForm form,
				HttpServletRequest request,
				HttpServletResponse response)
				throws Exception {

				ActionForward forward = new ActionForward();
				UserMstrFormBean kmUserMstrFormBean = (UserMstrFormBean) form;
				UserMstr userMstrDto = new UserMstr();
				UserMstrService editUserService = new UserMstrServiceImpl();
				HttpSession session = request.getSession();
				UserMstr sessionUserBean =
					(UserMstr) session.getAttribute("USER_INFO");
				//KmElementMstrService elementService = new KmElementMstrServiceImpl();
				saveToken(request);
				System.out.println("----------------------view------------------------------");
				/*if ( !isTokenValid(request) ) {
					  return mapping.findForward("error");
					}*/
				try {
					logger.info(
						sessionUserBean.getUserLoginId()
							+ " Entered view User method.");
					String userId = kmUserMstrFormBean.getSelectedUserId();
					logger.info("Selected user Id= " + userId);
					//	String userLoginId=request.getParameter("USER_LOGIN_ID");

					kmUserMstrFormBean.setUserId(userId);
					//Calling service to get the details of user by passing userId
					kmUserMstrFormBean.setUserDetails(
						editUserService.getUserService(userId, sessionUserBean.getKmActorId()));
					UserDto userDto = new UserDto();
					userDto = kmUserMstrFormBean.getUserDetails();
					System.out.println("sessionUserBean.getKmActorId()"+sessionUserBean.getKmActorId());
					int loginUserActorId = Integer.parseInt(sessionUserBean.getKmActorId());
					System.out.println("sessionUserBean.getKmActorId()"+userDto.getKmActorId());
					if(userDto.getKmActorId()!=null){
						int searchedUserActorId = Integer.parseInt(userDto.getKmActorId());
						if(loginUserActorId >= searchedUserActorId)
							kmUserMstrFormBean.setLevelCheck("N");
						else
							kmUserMstrFormBean.setLevelCheck("Y");
					}
					
					
					
					kmUserMstrFormBean.setUserLoginId(userDto.getUserLoginId());
					kmUserMstrFormBean.setUserFname(userDto.getUserFname());
					kmUserMstrFormBean.setUserMname(userDto.getUserMname());
					kmUserMstrFormBean.setUserLname(userDto.getUserLname());
					kmUserMstrFormBean.setUserMobileNumber(userDto.getUserMobileNumber());
					kmUserMstrFormBean.setUserEmailid(userDto.getUserEmailid());			
					kmUserMstrFormBean.setKmActorId(userDto.getKmActorId());
					kmUserMstrFormBean.setZoneName(userDto.getZoneName());
					kmUserMstrFormBean.setCityZoneName(userDto.getCityZoneName());
					String lobs="";
					String lobId="";
					//String lobCircle ="";
					for(int i=0 ; i < userDto.getLobForUserList().size(); i++){
						lobs = userDto.getLobForUserList().get(i).getLobName();	
						
						lobId = lobId+", "+lobs;
						}			
					kmUserMstrFormBean.setLobId(lobId.replaceFirst(",", ""));
					kmUserMstrFormBean.setElementId(userDto.getElementId());
					kmUserMstrFormBean.setCircleId(userDto.getCircleId());
					kmUserMstrFormBean.setCategoryId(userDto.getCategoryId());
					kmUserMstrFormBean.setPartnerName(userDto.getPartnerName());
					kmUserMstrFormBean.setRole(userDto.getKmActorName());
					System.out.println("userDto.getKmActorName()"+userDto.getKmActorName());
					String circles=null;
					String circlesName="";
					for(int i=0 ; i < userDto.getCircleForUserList().size(); i++){
						circles = userDto.getCircleForUserList().get(i).getCircleName()+" ( "+ userDto.getCircleForUserList().get(i).getLobName()+" ) " ;	
						//circlesName = circles;
						circlesName = circlesName+", "+circles;
						
					}	
					//circlesName = circlesName+" ]";
					kmUserMstrFormBean.setCircleName(circlesName.replaceFirst(",", ""));
					System.out.println("userDto.getCircleName()"+circlesName);
					String status = userDto.getStatus();
					
					if("A".equals(status))
					{
						kmUserMstrFormBean.setStatus("Active");
					}
					else
					 if("D".equals(status))
					 {
						 kmUserMstrFormBean.setStatus("Deactive");
					 }
					 else
						 kmUserMstrFormBean.setStatus(status);			
					if(userDto.getCreatedBy()!=null)
					kmUserMstrFormBean.setCreatedBy(editUserService.getUserLoginId(userDto.getCreatedBy()));			
					kmUserMstrFormBean.setCreatedDt(userDto.getCreatedDt());
					if(userDto.getUpdatedBy()!=null)
					kmUserMstrFormBean.setUpdatedBy(editUserService.getUserLoginId(userDto.getUpdatedBy()));
					kmUserMstrFormBean.setUpdatedDt(userDto.getUpdatedDt());
					
					return mapping.findForward(VIEW_USER);
				} 
				catch (Exception e) {
					e.printStackTrace();
					logger.error("Exception occured while initializing editUser page ");
				}
				return null;
			}
		 

		
		
		

}
