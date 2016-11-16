package com.ibm.nas.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONObject;

import com.ibm.nas.dto.UserMstr;
import com.ibm.nas.services.UserMstrService;
import com.ibm.nas.services.impl.UserMstrServiceImpl;


public class AjaxSupportAction extends DispatchAction {

	private static final Logger logger = Logger.getLogger(AjaxSupportAction.class);
		

	
	public ActionForward getCircleBasedOnLobForCo(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {

		System.out.println("ajax calling method");
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("options");
			Element optionElement;
			HttpSession session = request.getSession();
			UserMstrService UserMstrService = new UserMstrServiceImpl();
			String productLobId = request.getParameter("selectedLobId");
			System.out.println("product Lob Id;;;;;;;;;;;;;"+productLobId);
			String userLoginId=request.getParameter("loginId");
			System.out.println("Login Id.................."+userLoginId);
	        
			try {
				
				JSONObject json = UserMstrService.getElementsAsJsonNew(productLobId,userLoginId);	
				response.setHeader("Cache-Control", "no-cache");
				response.setHeader("Content-Type", "application/x-json");
				response.getWriter().print(json);		
			
			} catch (Exception e) {
				logger.error("Exception in Get Circle List Based on Product LOB in getCircleBasedOnLobForCo() " + e.getMessage());
			}
			return null;
		}
	
	
	
	public ActionForward getCircleBasedOnLob(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("options");
		Element optionElement;
		HttpSession session = request.getSession();
		UserMstrService UserMstrService = new UserMstrServiceImpl();
		String productLobId = request.getParameter("selectedLobId");
        
		try {
			
			JSONObject json = UserMstrService.getElementsAsJson(productLobId);
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Content-Type", "application/x-json");
			response.getWriter().print(json);		
		
		} catch (Exception e) {
			logger.error("Exception in Get Circle List Based on Product LOB in getCircleBasedOnLob() " + e.getMessage());
		}
		return null;
	}
	//New One
	
	
	
	//Added by satish
	public ActionForward getAgencyUserIdAsParentId(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {

			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("options");
			Element optionElement;
		//	HttpSession session = request.getSession();
			HttpSession session = request.getSession();
			UserMstr sessionUserBean =
				(UserMstr) session.getAttribute("USER_INFO");
			UserMstrService UserMstrService = new UserMstrServiceImpl();
			String productLobId = request.getParameter("selectedLobId");
			String selectedActorId = request.getParameter("selectedActorId");
			String circleMstrId = request.getParameter("circleMstrId");
	        
			try {
				
			//	JSONObject json = UserMstrService.getElementsAsJson(productLobId);  getElementsAsJsonAgencyUser
				JSONObject json = UserMstrService.getElementsAsJsonAgencyUser(selectedActorId,productLobId,circleMstrId,sessionUserBean);
				response.setHeader("Cache-Control", "no-cache");
				response.setHeader("Content-Type", "application/x-json");
				response.getWriter().print(json);		
			
			} catch (Exception e) {
				logger.error("Exception in Get Agency user List  in getAgencyUserIdAsParentId() " + e.getMessage());
			}
			return null;
		}
	
	//end 
	
	
	}
