package com.ibm.nas.actions;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ibm.nas.common.UserDetails;
import com.ibm.nas.dto.UserMstr;
import com.ibm.nas.services.UserMstrService;
import com.ibm.nas.services.impl.UserMstrServiceImpl;


public class LogoutAction extends Action{

	/*
			 * Logger for the class.
			 */
	private static final Logger logger;
	static {
		logger = Logger.getLogger(LogoutAction.class);
	}
	/* Local Variables */
	private static int logoutCounter = 0;
	private static String LOGOUT_SUCCESS = "logoutSuccess";
	private static String CSR_LOGOUT_SUCCESS = "csrLogoutSuccess";
	private static String LOGOUT_FAILURE = "logoutFailure";

	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); 
		HttpSession session = request.getSession();
		saveToken(request);
		try {
			
			if("/timeout".equalsIgnoreCase(mapping.getPath()))
			{
				logger.info("mapping.getPath()-----"+mapping.getPath());
				forward = mapping.findForward("timeout");
			}
			
			if("/Logout".equalsIgnoreCase(mapping.getPath())) {				
				UserMstr userBean= UserDetails.getUserDetails(request);
//				if(userBean.getKmActorId().equals(Constants.CIRCLE_CSR)||userBean.getKmActorId().equals(Constants.CATEGORY_CSR)){
//					forward = mapping.findForward(CSR_LOGOUT_SUCCESS);	
//				}else{
//					forward = mapping.findForward(LOGOUT_SUCCESS);
//				}
				userBean.setUserLoginStatus("N");
				UserMstrService userService= new UserMstrServiceImpl();
				//userService.updateUserStatus(userBean);
				//System.out.println("LOGOUTTTTTTTTTTTTT");
				forward = mapping.findForward(LOGOUT_SUCCESS);
			}	
			
									
			logger.info("Attempt to Logout");
			session.invalidate();
			logger.info("User Logged out No. >>" + (++logoutCounter) + " at " + new Date());
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception From KmLogoutAction" + e);
		}
		return (forward);
	}

}
