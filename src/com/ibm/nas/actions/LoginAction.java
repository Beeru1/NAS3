package com.ibm.nas.actions;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.ibm.appsecure.exception.EncryptionException;
import com.ibm.appsecure.exception.ValidationException;
import com.ibm.appsecure.service.GSDService;
import com.ibm.appsecure.util.Encryption;
import com.ibm.appsecure.util.IEncryption;
import com.ibm.nas.common.Constants;
import com.ibm.nas.common.PropertyReader;
import com.ibm.nas.dto.LinkMstrDto;
import com.ibm.nas.dto.Login;
import com.ibm.nas.dto.UserMstr;
import com.ibm.nas.exception.DAOException;
import com.ibm.nas.exception.LMSException;
import com.ibm.nas.forms.LoginFormBean;
import com.ibm.nas.services.LoginService;
import com.ibm.nas.services.UserMstrService;
import com.ibm.nas.services.impl.LoginServiceImpl;
import com.ibm.nas.services.impl.UserMstrServiceImpl;

public class LoginAction extends Action{
	
	private static Logger logger = Logger.getLogger(LoginAction.class.getName());
	private static String AUTHENTICATION_SUCCESS = "loginSuccess";

	private static String FORGOTPASSWORD = "forgotPassword";

	private static String CSRLOGIN_SUCCESS = "csrLoginSuccess";

	private static String CSR_AUTHENTICATION_FAILURE = "csrLoginFailure";

	private static String FORGOTPASSWORD_SUCCESS = "forgotPwdSuccess";

	private static String HOME_SUCCESS = "home";
	private static String HELP = "help";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response)
	{
		java.util.Date dt1 = new java.util.Date();
		ActionErrors errors = new ActionErrors();
		ActionMessages messages=new ActionMessages();
		ActionForward forward = new ActionForward();
		HttpSession session = request.getSession();
		LoginFormBean loginformBean=null;
		loginformBean = (LoginFormBean) form;
		Login login = new Login();
		ArrayList userRoleList = new ArrayList();
		//KmLinkMstrService linkMstrService = new LinkMstrServiceImpl();
		UserMstrService userService = null;
		System.out.println("VCAP Host:" + System.getenv("VCAP_APP_HOST") + ":"
				+ System.getenv("VCAP_APP_PORT"));
		//System.out.println("Host IP:" + InetAddress.getLocalHost().getHostAddress());
		try{
		if ("/NASLogin".equalsIgnoreCase(mapping.getPath()))
		{
			String userId=(String)request.getAttribute("userid");
			String password=(String)request.getAttribute("password");
			
			/*if(userId ==null || userId.length() <=0  || ( password == null || password.length() <= 0) )
			 {
				return mapping.findForward("loginFailed");
			 }
			
			*/
			System.out.println("hiiiiiiiiiiiii");
			if(userId!=null){
				loginformBean.setUserId(userId);
				loginformBean.setPassword(password);
			}
			IEncryption encrypt = new Encryption();
			login.setUserId(loginformBean.getUserId());
			
			if(password!=null && password.trim().length()>0){

			login.setPassword(encrypt.generateDigest(loginformBean.getPassword()));

			}
			
			if(loginformBean.getUserId() ==null || loginformBean.getUserId().length() <=0  || ( loginformBean.getPassword() == null || loginformBean.getPassword().length() <= 0) )
			 {
				return mapping.findForward("loginFailed");
			 }
			////System.out.println(login.getPassword());
			 userService = new UserMstrServiceImpl();
			 
			if(userId !=null && !userService.checkDuplicateUserLogin(loginformBean.getUserId().toUpperCase())){
				errors.add("errors.login.invalid_id", new ActionError("errors.login.invalid_id"));
				saveErrors(request, errors);
				logger.error("Invalid Login Id");
			return mapping.findForward("loginFail");
							}
				           
			
			GSDService gSDService = new GSDService();
			UserMstr userInfo =
				(UserMstr) gSDService.validateCredentials(
					login.getUserId(),
						loginformBean.getPassword(),
					"com.ibm.nas.dto.UserMstr");

			

			LoginService loginService = new LoginServiceImpl();

          // LDAP user validation 
			if("Y".equalsIgnoreCase(PropertyReader.getAppValue("doLdapValidation")))
			{
				//logger.info("Checking LDAP as ValidateLDAP is "+PropertyReader.getAppValue("doLdapValidation"));
				try
				{
					if(!loginService.isValidUser(loginformBean.getUserId()))
					{
		                errors.add("errors.login.user_invalid", new ActionError("login.ldapValidationError"));
		 				saveErrors(request, errors);
		 				//logger.info("User LDAP validation failed for user : "+loginformBean.getUserId());
		 				forward=mapping.findForward(CSR_AUTHENTICATION_FAILURE);
		 				return (forward);
					}
				}catch(Exception ee)
				{
					 errors.add("errors.login.user_invalid", new ActionError("login.ldapConnectionFail"));
		 				saveErrors(request, errors);
		 				//logger.info("Connection couldn't established for the user : "+loginformBean.getUserId());
		 				forward=mapping.findForward(CSR_AUTHENTICATION_FAILURE);
		 				return (forward);
				}
			}
			//---------------------------- LDAP validation finish

			UserMstr userBean = loginService.populateUserDetails(login);
			ArrayList linksList = new ArrayList();
		//	ArrayList<LinkMstrDto> toplinksList = new ArrayList<LinkMstrDto>();

		//	toplinksList = linkMstrService.viewLinks(Constants.TOP_LINKS);

			//userRoleList = linkMstrService.getUserRoleList(userBean.getKmActorId());

			ArrayList topBarLinks = new ArrayList();
			ArrayList bottomBarLinks = new ArrayList();



			// Multiple login disable; inmplemented by Kundan Kumar for security finding closer

			logger.info("Checking Multiple login... : "+userBean.getUserLoginStatus());
/*
			if("Y".equals(userBean.getUserLoginStatus()))
			{
                errors.add("errors.login.user_invalid", new ActionError("login.singleSignError"));
 				saveErrors(request, errors);
 				logger.info("Multiple login found for user : "+loginformBean.getUserId());
 			 // userBean.setUserPassword("");
 				forward=mapping.findForward(CSR_AUTHENTICATION_FAILURE);
 				return (forward);
			}
			*/
			
			
			//---------------------------- Multiple login disable finish

			// Refreshing Cache for all the Master Table data List

		/*	if(userBean.getKmActorId().equals(Constants.SUPER_ADMIN))
			{
				new MasterServiceImpl().refreshCache();
			}
	 	*/
	/*	
			String sessionid = request.getSession().getId();
			// be careful overwriting: JSESSIONID may have been set with other flags
			response.setHeader("SET-COOKIE", "JSESSIONID=" + sessionid + "; HttpOnly");
	*/		

				//Setting user information in session
			    session.invalidate();
				session = request.getSession(true);
				//userBean = populatePathInfo(userBean, session);
				//session.setAttribute("appL1", appL1);
				//session.setAttribute("appL2", appL2);
				session.setAttribute("USER_INFO", userBean);
				session.setAttribute("LINKS_LIST",linksList);
				//session.setAttribute("TOP_LINKS_LIST",toplinksList);
				session.setAttribute("USER_ROLE_LIST",userRoleList);
				session.setAttribute("TOPBAR_LINKS",topBarLinks);
				session.setAttribute("BOTTOMBAR_LINKS",bottomBarLinks);
				//Warning the User for pasword expiry
				//request.setAttribute("warn", loginService.getWarning(userBean.getUserPsswrdExpryDt()));
				if(!Constants.SUPER_ADMIN.equalsIgnoreCase(userBean.getKmActorId()) && !Constants.CIRCLE_ADMIN.equalsIgnoreCase(userBean.getKmActorId())) {
					List<String> grouplinksList = new ArrayList<String>();
					/*for(Object dto : userRoleList) {
						LinkMstrDto dto2 =(LinkMstrDto)dto;
						grouplinksList.add(dto2.getLinkPath());
					}
					session.setAttribute("GROUP_LINKS_LIST",grouplinksList);*/
					}

				//logger.info("Actor ID: "+userBean.getKmActorId());

				if(userBean.getLastLoginTime()==null || userBean.getLastLoginTime().equals("")) {
						  request.setAttribute("FirstLogin", "true");

						  forward = mapping.findForward("firstLoginChangePassword");
				   }

				   else {
				   forward = mapping.findForward(AUTHENTICATION_SUCCESS);
				  //Added by Anil as part of UD integration
				   userBean.setUserLoginStatus("Y");
				   userBean.setSessionID(session.getId());
				   String ipXForwarded  = request.getHeader("X-Forwarded-For");
				   if(ipXForwarded != null && ipXForwarded.length() >7) {
					   userBean.setIpaddress(ipXForwarded);
					   //logger.info(loginformBean.getUserName() + "ipaddress: setting ip address X-Forwarded-For : "+ipXForwarded);
				   }
				   else if(request.getParameter("ipaddress") != null && !request.getParameter("ipaddress").equals("") ) {
					   userBean.setIpaddress(request.getParameter("ipaddress"));
					  // logger.info(loginformBean.getUserName() + "ipaddress: setting ip address : "+request.getParameter("ipaddress"));
				   }
				   else {
					   userBean.setIpaddress(request.getRemoteAddr() );
					   //logger.info(" setting getRemoteAddr : "+ request.getRemoteAddr());
				   }
				  // userService.updateUserStatus(userBean);
				   //logger.info(loginformBean.getUserId() + " Logged in to the LMS From the Machine with IP : "+userBean.getIpaddress());
				   }
		
		}
		if("/help".equalsIgnoreCase(mapping.getPath()))
		{
			forward = mapping.findForward(HELP);

		}
		if ("/home".equalsIgnoreCase(mapping.getPath()))
		{
			

			forward = mapping.findForward(HOME_SUCCESS);

		}if ("/initForgotPassword".equalsIgnoreCase(mapping.getPath())) {

			String kmForgot=request.getParameter("KmForgot");

			if(kmForgot!=null &&  kmForgot.equals("true")) {
				forward = mapping.findForward("kmForgotPassword");
			}
			else {
				forward = mapping.findForward(FORGOTPASSWORD);
			}

		}
		if ("/forgotPassword".equalsIgnoreCase(mapping.getPath()))
		{

			String userId=loginformBean.getUserName();
			String  ipaddress=null;
			//System.out.println("USER ID"+userId);
			
			   if(request.getParameter("ipaddress") != null && !request.getParameter("ipaddress").equals("") ) {
			   ipaddress = request.getParameter("ipaddress");
			  // System.out.println("IPADDRESSsssssssssssss"+ipaddress);
				logger.info(request.getHeader("X-Forwarded-For") + "X-Forwarded-For=== ipaddress in forgot password From the Machine with IP : "+ipaddress);
			   }

					//System.out.println("IPADDRESS"+ipaddress);
			   			logger.info(loginformBean.getUserName() + " is using forgot password ====X-Forwarded-For==="+request.getHeader("X-Forwarded-For")+"===========module From the Machine with IP : "+request.getRemoteAddr());

						String csrStatus="";
						csrStatus=request.getParameter("CSR");
						//String kmPass="";
						String kmPass =loginformBean.getKmforgot();
						//kmPass=(String)request.getAttribute("PASSWORDKM");
						//logger.info("kmpass"+kmPass);
						LoginService loginService= new LoginServiceImpl();
						
						//UserMstr userBean = loginService.populateUserDetails(login);
						//Added to capture Loginid and ip details
						UserMstr userBean=new UserMstr();
						userBean.setUserLoginId(userId);
						
						
						String ipXForwarded  = request.getHeader("X-Forwarded-For");
						   if(ipXForwarded != null && ipXForwarded.length() >7) {
							   userBean.setIpaddress(ipXForwarded);
							   //System.out.println("IP address 1"+userBean.getIpaddress());
							   logger.info(loginformBean.getUserName() + "ipaddress: setting ip address X-Forwarded-For : "+ipXForwarded);
						   }
						   else if(request.getParameter("ipaddress") != null && !request.getParameter("ipaddress").equals("") ) {
							   userBean.setIpaddress(request.getParameter("ipaddress"));
							   logger.info(loginformBean.getUserName() + "ipaddress: setting ip address : "+request.getParameter("ipaddress"));
							   
						   }
						   else {
							   userBean.setIpaddress(request.getRemoteAddr() );
							   logger.info(" setting getRemoteAddr : "+ request.getRemoteAddr());
						   }
						
						userService=new UserMstrServiceImpl();
						//Getting the user details and email id .
						ArrayList alist=loginService.getEmailId(loginformBean.getUserName());
						//	ArrayList alist=kmLoginDaoImpl.getEmailId(loginformBean.getUserName());
						String actorId="";
						String emailId="";
						String userLoginId="";
						String status="";

						if(alist!=null) {
							userLoginId=(String)alist.get(0);
							emailId=(String)alist.get(1);
							actorId=(String)alist.get(2);
							status=(String) alist.get(3);
							if(alist.get(4)!=null && Integer.parseInt((String) alist.get(4))<6){
								errors.add("loginId", new ActionError("password.reset.retry"));
								saveErrors(request, errors);
								if(csrStatus.equals("TRUE")) {
									forward = mapping.findForward("forgotPassword");
									return (forward);
								}
								else{
									forward = mapping.findForward("kmForgotPassword");
									return (forward);
								}
							}
							if(!status.equalsIgnoreCase("A")){
								errors.add("loginId", new ActionError("login.user.deactivated"));
								saveErrors(request, errors);
								if(csrStatus.equals("TRUE")) {
									forward = mapping.findForward("forgotPassword");
									return (forward);
								}
								else{
									forward = mapping.findForward("kmForgotPassword");
									return (forward);
								}
							}

						}
						else {
							errors.add("loginId", new ActionError("login.invalid.login"));
							saveErrors(request, errors);
							if(csrStatus.equals("TRUE")) {
								forward = mapping.findForward("forgotPassword");
								return (forward);
							}
							else{
								forward = mapping.findForward("kmForgotPassword");
								return (forward);
							}
						}


						if(userLoginId.equals("")) {
							if(csrStatus.equals("TRUE")) {
								forward = mapping.findForward("forgotPassword");
								return (forward);
							}
							else {
								if(userLoginId.equals("")) {
									errors.add("loginId", new ActionError("errors.login.invalid_id"));
									saveErrors(request, errors);
								}
								forward = mapping.findForward("kmForgotPassword");
								return (forward);
							}

						}
						String strFromEmail = null;

					//	String UDAdmin=getResources(request).getMessage("UD.Admin")
					//	.toString();
						strFromEmail = getResources(request).getMessage("login.email")
										.toString();
								String strSubject = "Your LMS Password";
								String strMessage = null;
								String txtMessage = null;
								StringBuffer sbMessage = new StringBuffer();
								logger.info("strFromEmail : "+strFromEmail);
								//String strHost = getResources(request).getMessage("login.smtp").toString();
								String strHost = PropertyReader.getAppValue("LOGIN.SMTP");
								logger.info("strHost : "+strHost);
						IEncryption t = new Encryption();

						if(!userLoginId.equalsIgnoreCase("")) {
							if(!emailId.equalsIgnoreCase("")) {
								sbMessage.append("Dear " + loginformBean.getUserName()
															+ ", \n\n");
													sbMessage.append("Your LMS password is : ");
					//strMessage = loginformBean.getUserName().substring(0, 1) Math.abs(new Random().nextInt()) loginformBean.getUserName().substring(2, 3);

					// Security closer for new generated password as GSD complaint ...
				    strMessage = generatePassword(loginformBean.getUserName());
				    // ---------------------------------------------------------------

								String encPassword = t.generateDigest(strMessage);
								sbMessage.append(strMessage +"\n");
													sbMessage.append("\nRegards ");
													sbMessage.append("\nLMS Administrator ");
													sbMessage.append("\n\n/** This is an Auto generated email.Please do not reply to this.**/");
													txtMessage = sbMessage.toString();
								try {
										Properties prop = System.getProperties();
										prop.put("mail.smtp.host", strHost);
										Session ses = Session.getDefaultInstance(prop, null);
										MimeMessage msg = new MimeMessage(ses);
										msg.setFrom(new InternetAddress(strFromEmail));
										msg.addRecipient(Message.RecipientType.TO,
												new InternetAddress(emailId));
									//	msg.addRecipient(Message.RecipientType.CC,new InternetAddress(UDAdmin));
										msg.setSubject(strSubject);
										msg.setText(txtMessage);
										msg.setSentDate(new Date());
										Transport.send(msg);
										// Changes are included in login.jsp and csrLogin.jsp
										messages.add("msg1",new ActionMessage("password.sent",emailId));
										saveMessages(request,messages);
									//	kmLoginDaoImpl.updatePasswordExpiryDate(userLoginId);
										//Changed by Anil against code review defect : MASDB00098318
										loginService.updatePassword(loginformBean.getUserName(),encPassword);
										//Login id and ip address and element id entry go to KM_LOGIN_DATA table
										userService.updateForgotPasswordUser(userBean);
									//	kmLoginDaoImpl.updatePassword(loginformBean.getUserName(),encPassword);
										loginformBean.setMessage("Password is sent to your mailid : "+emailId);

											return  mapping.findForward(FORGOTPASSWORD_SUCCESS);

									} catch (javax.mail.internet.AddressException ae) {
										errors.add("errors.forgotPassword", new ActionError("error.forgotPassword"));
										saveErrors(request, errors);
										forward = mapping.findForward(FORGOTPASSWORD_SUCCESS);
										logger.error("AddressException occurs in execute of Login Action: "+ ae.getMessage());
									} catch (javax.mail.MessagingException me) {
										errors.add("errors.forgotPassword", new ActionError("error.server.forgotPassword"));
										forward = mapping.findForward(FORGOTPASSWORD_SUCCESS);
										logger.error("MessagingException occurs in execute of Login Action: "+ me.getMessage());
										saveErrors(request, errors);
									}
									catch(Exception e){
										forward = mapping.findForward(FORGOTPASSWORD_SUCCESS);

									}
													//logger.info("strMessage"+strMessage);




							}
							else {
								errors.add("loginId", new ActionError("login.noEmailId"));
								saveErrors(request, errors);
								if(csrStatus!=null && csrStatus.equals("TRUE")){
									forward=mapping.findForward("forgotPassword");
								}else{
									forward = mapping.findForward("kmForgotPassword");
								}
							}
						}
						else {
							errors.add("loginId", new ActionError("login.invalid.login"));
							saveErrors(request, errors);
							if(csrStatus!=null && csrStatus.equals("TRUE")){
									forward=mapping.findForward("csrLoginFailure");
							}else{
									forward = mapping.findForward("loginFailure");
							}
						}

					
		}

		
	}
	catch (EncryptionException e) {
		errors.add("errors.login.user_invalid", new ActionError(e.getMessage()));
		e.printStackTrace();
		//logger.error("EncryptionException in Login by User ID: " + loginformBean.getUserId());
		saveErrors(request, errors);
		forward=mapping.findForward(CSR_AUTHENTICATION_FAILURE);
 }
catch (ValidationException ve) {
	 int wrongPwdCount = 0;
	 try{
        int PasswordDisableLimit =Integer.parseInt(PropertyReader.getGsdValue("PasswordDisableLimit"));
        wrongPwdCount = userService.getWrongPwdCount(loginformBean.getUserId());
   //System.out.println(loginformBean.getUserId()+" "+PasswordDisableLimit+" "+wrongPwdCount);
        if (PasswordDisableLimit <= wrongPwdCount )
        {
        	errors.add("errors",new ActionError("msg.security.id014"));
            saveErrors(request, errors);
            forward=mapping.findForward(CSR_AUTHENTICATION_FAILURE);
            return forward;
        }
        
		errors.add("errors.login.user_invalid", new ActionError(ve.getMessageId()));
		//ve.printStackTrace();
		//logger.error("ValidationException in Login by User ID: " + loginformBean.getUserId());
		saveErrors(request, errors);
		forward=mapping.findForward(CSR_AUTHENTICATION_FAILURE);
	 }
	 catch(Exception e)
	 {
		 //logger.error("ValidationException in Login by User ID: " + e.getMessage());
	 }
	}
       catch (LMSException ex) {
	
			errors.add("errors.login.user_invalid", new ActionError("errors.login.invalid_id"));
			saveErrors(request, errors);
		//	ex.printStackTrace();
			//logger.error("Single Sign-In Exception in Login by User ID: " + loginformBean.getUserId() );
			forward=mapping.findForward(CSR_AUTHENTICATION_FAILURE);
		}
     catch (DAOException ex) {
	
			errors.add("errors.login.user_invalid", new ActionError("login.connectionError"));
			saveErrors(request, errors);
			//logger.error("Network Exception in Login by User ID: " + ex.getMessage());
			forward=mapping.findForward(CSR_AUTHENTICATION_FAILURE);
}
	catch (Exception ex) {
		ex.printStackTrace();
		//logger.error("Exception in Login by User ID: " + ex.getMessage());
		forward=mapping.findForward(CSR_AUTHENTICATION_FAILURE);
	}

	java.util.Date dt2 = new java.util.Date();
//logger.info("Login processing Time:" + (dt2.getTime() - dt1.getTime()) );

	return (forward);

   }
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
}
