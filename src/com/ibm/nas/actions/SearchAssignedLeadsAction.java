package com.ibm.nas.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.ibm.nas.common.Constants;
import com.ibm.nas.common.PropertyReader;
import com.ibm.nas.common.Utility;
import com.ibm.nas.dto.Leads;
import com.ibm.nas.dto.UserMstr;
import com.ibm.nas.forms.LeadForm;
import com.ibm.nas.services.AssignedLeadsManager;
import com.ibm.nas.services.FeasibleLeadManager;
import com.ibm.nas.services.MasterService;
import com.ibm.nas.services.impl.AssignedLeadsManagerImpl;
import com.ibm.nas.services.impl.FeasibleLeadManagerImpl;
import com.ibm.nas.services.impl.MasterServiceImpl;
import com.ibm.nas.wf.dto.Constant;
import com.ibm.nas.wf.dto.LeadDetailDTO;

public class SearchAssignedLeadsAction extends DispatchAction {

	private static Logger logger = Logger
			.getLogger(SearchAssignedLeadsAction.class.getName());

	public ActionForward init(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,

			HttpServletResponse response) throws Exception {

		ActionForward forward = new ActionForward(); // return value
		HttpSession session = request.getSession();
		/*logger.info(UserDetails.getUserLoginId(request)
				+ " : Entered init method");*/
		AssignedLeadsManager assignedLeadsManager = new AssignedLeadsManagerImpl();
		UserMstr sessionUserBean = (UserMstr) session.getAttribute("USER_INFO");
		/*logger.info(UserDetails.getUserLoginId(request)
				+ " : Exiting init method");*/
		MasterService masterService = new MasterServiceImpl();
		String view = "";
		LeadForm commonForm = (LeadForm) form;
		commonForm.reset();
		commonForm.setMsg("");
		commonForm.setMessage("");
		int recordslimitdays = Integer.parseInt(PropertyReader
				.getAppValue("records.limit.days"));
		session.setAttribute("recordslimitdays", recordslimitdays);
		if(Utility.isValidRequest(request) && (session.getAttribute("URLFLAG")==null || !"N".equalsIgnoreCase((String)session.getAttribute("URLFLAG")))) {
        	return mapping.findForward("error");
        }
		List userList = null;
		if (request.getParameter("view") != null) {
			view = request.getParameter("view").toString();
			commonForm.setView(view);
		}
		try {
			saveToken(request);
			List assignedLeads = new ArrayList();
			assignedLeads = assignedLeadsManager.listAssignedLeads(
					sessionUserBean.getUserLoginId(), null, null, commonForm
							.getView());
			if (assignedLeads != null && assignedLeads.size() > 0)
				commonForm.setAssignedLeads(assignedLeads);
			commonForm.setParam(masterService.getParameterName(PropertyReader
					.getAppValue("assigned.leads.no.of.days")));
			// changed by sudhanshu
			/*
			 * if((sessionUserBean.getKmActorId().equalsIgnoreCase(Constants.CIRCLE_USER
			 * ))||(sessionUserBean.getKmActorId().equalsIgnoreCase(Constants.
			 * ZBM_ACTOR
			 * ))||(sessionUserBean.getKmActorId().equalsIgnoreCase(Constants
			 * .ZSM_ACTOR
			 * ))||(sessionUserBean.getKmActorId().equalsIgnoreCase(Constants
			 * .ZONAL_COORDINATOR_ACTOR))){ userList =
			 * assignedLeadsManager.getUsersList
			 * (sessionUserBean.getCircleList(),sessionUserBean.getLobList()
			 * );//changed by sudhanshu } else userList =
			 * assignedLeadsManager.getChannelPartnerList
			 * (sessionUserBean.getCircleList(),sessionUserBean.getLobList()
			 * );//changed by sudhanshu
			 */// System.out.println("get circle co-ordi");
			

			//request.setAttribute("Mobile_FLAG", masterService.getParameterName("Mobile_FLAG"));
			request.setAttribute("userList", userList);

		} catch (Exception e) {
			logger.error("Exception occurs in viewUserList: " + e.getMessage());
		}
		forward = mapping.findForward("search");
		return (forward);

	}


	public ActionForward searchLead(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		logger
				.info("asaasa:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
		ActionForward forward = new ActionForward();
		HttpSession session = request.getSession();
		AssignedLeadsManager assignedLeadsManager = new AssignedLeadsManagerImpl();
	//	MasterService masterService = new MasterServiceImpl();
		saveToken(request);
		UserMstr sessionUserBean = (UserMstr) session.getAttribute("USER_INFO");
		/*logger.info(UserDetails.getUserLoginId(request)
				+ " : Exiting init method");*/
		LeadForm commonForm = (LeadForm) form;
		List userList = null;
		try {
			String startDate = (String) request.getParameter("startDate");
			String endDate = (String) request.getParameter("endDate");
			List assignedLeads = new ArrayList();
			System.out.println("usererrrr"+sessionUserBean.getUserLoginId());
			assignedLeads = assignedLeadsManager.listAssignedLeads(
					sessionUserBean.getUserLoginId(), startDate, endDate,
					commonForm.getView());
			// assignedLeads = assignedLeadsManager.listAssignedLeadsEscalation(
			// sessionUserBean.getUserLoginId(), startDate,
			// endDate,commonForm.getView());
			commonForm.setAssignedLeads(assignedLeads);
			System.out.println("hiiiiiiiiiiii"+assignedLeads.size());
			/*
			 * if(sessionUserBean.getKmActorId().equalsIgnoreCase(Constants.CIRCLE_USER
			 * )){ userList =
			 * assignedLeadsManager.getUsersList(sessionUserBean.getCircleList
			 * (),sessionUserBean.getLobList() );//changed by sudhanshu } else
			 * userList =
			 * assignedLeadsManager.getChannelPartnerList(sessionUserBean
			 * .getCircleList(),sessionUserBean.getLobList() );//changed by
			 * sudhanshu
			 */

			//request.setAttribute("Mobile_FLAG", masterService.getParameterName("Mobile_FLAG"));

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception occurs in search Lead: " + e.getMessage());
			logger
					.error("Exception occured while initializing lead Search page ");

		}
		forward = mapping.findForward("search");

		return forward;
	}

/*
	public ActionForward leadDetails(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response)throws Exception
	{
		HttpSession session = request.getSession();
		UserMstr sessionUserBean = (UserMstr) session.getAttribute("USER_INFO");
		ActionForward forward = new ActionForward();
		ActionErrors errors = new ActionErrors();
		LeadForm commonForm = (LeadForm) form;
		String AssignedOlmid;
		Long leadId;
		//System.out.println("rrrrrrrrrrrrrrrrrrrrrrrrrrr");
		AssignedLeadsManager assignedLeadsManager = new AssignedLeadsManagerImpl();
		Leads leads  =  assignedLeadsManager.getFSCDetails(request.getParameter("pinCode"), sessionUserBean.getUserLoginId());
		forward = mapping.findForward("ShowPage");
		request.setAttribute("LeadId",request.getParameter("leadID") );
		request.setAttribute("Pincode",request.getParameter("pinCode"));
		if(leads !=null) {
		request.setAttribute("OlmId", leads.getFwdOlmId());
		request.setAttribute("UserName", leads.getPrimaryUser());
		}else {
			request.setAttribute("OlmId", "");
			request.setAttribute("UserName", "");
		}
		
		
		return forward;
	}

*/
	

	public ActionForward leadDetails(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response)throws Exception
	{
		/*HttpSession session = request.getSession();
		UserMstr sessionUserBean = (UserMstr) session.getAttribute("USER_INFO");
		ActionForward forward = new ActionForward();
		ActionErrors errors = new ActionErrors();
		LeadForm commonForm = (LeadForm) form;*/
		

		

		HttpSession session = request.getSession();
		ActionForward forward = new ActionForward();
		ActionErrors errors = new ActionErrors();
		LeadForm commonForm = (LeadForm) form;
		String product_name;
		int lobId;
		Long leadId;
		int productId;
		long productIdForCheck;
		AssignedLeadsManager assignedLeadsManager = new AssignedLeadsManagerImpl();
		FeasibleLeadManager feasibleLeadManager = new FeasibleLeadManagerImpl();
		MasterService masterService = new MasterServiceImpl();
		UserMstr sessionUserBean = (UserMstr) session.getAttribute("USER_INFO");
		saveToken(request);
		Constant cons = new Constant();
		List userList = null;
		List productConfigureList=null;
		String loggedinUser =sessionUserBean.getUserLoginId();
		boolean flag=false;
		try {
			List<Constant> actionList = assignedLeadsManager
					.getActionList("LEAD_CLOSURE");
			int size = actionList.size();
			// sessionUserBean.setKmActorId("15");
			// added by Nancy
			
			
			//
			leadId = Long.parseLong(request.getParameter("leadID"));
			String assignedUser =assignedLeadsManager.checkAsssignedPrimaryUser(leadId);
			
			if(loggedinUser.equalsIgnoreCase(assignedUser)){
				flag=true;
			}
			if(!flag){
				errors.add("record.not.found", new ActionError("record.not.found"));
				saveErrors(request, errors);
				return mapping.findForward("search");
				
			}
			productId = masterService.getProductId(leadId);
			
			
			Long leadID = Long.parseLong(commonForm.getLeadID().toString());
			productIdForCheck = masterService.getProduct(leadId);
			LeadDetailDTO detailDTO = assignedLeadsManager.viewLeadDetail(leadID);
			
			//New code added here
			List<String> products=Arrays.asList(masterService.getParameterName("NEW_PRODUCT").trim().split(","));
			//System.out.println("FFFFFFFFFFFFFF"+masterService.getParameterName("NEW_PRODUCT"));
			
			boolean productCheck = products.contains(String.valueOf(productIdForCheck));
			
				userList = assignedLeadsManager.getChannelPartnerListForLead(
						sessionUserBean.getCircleList(), detailDTO,
						sessionUserBean.getLobList());
			
			String dropDownFlag=masterService.getParameterName("DROPDOWN_FLAG");
			if (productCheck && dropDownFlag.equalsIgnoreCase("Y")){
				userList=assignedLeadsManager.getUserListForNewProduct(detailDTO,sessionUserBean.getUserLoginId());
				//userList.addAll(productConfigureList);
			}
			
			List subStatusList = feasibleLeadManager.getSubStatusList(400,productId);
			logger.info("Actor id"+sessionUserBean.getKmActorId());
			//System.out.println("oooooooo"+(productCheck && Integer.parseInt(sessionUserBean.getKmActorId())==10));
			if(productCheck && Integer.parseInt(sessionUserBean.getKmActorId())== Constants.CHANNEL_PARTNER_ID )
			{
			
			for(Constant conste:actionList)
			{
				
				if(conste.getID()==Integer.parseInt(Constants.LEAD_STATUS_WON))
					actionList.remove(conste);
				}
			}
			request.setAttribute("cafStatus", false);
			if(productCheck){
				
				Constant constant=new Constant();
				int leadStatus=assignedLeadsManager.getLeadStatus(leadId);
				constant.setID((long)leadStatus);
				constant.setKeyValue("ON HOLD");
				actionList.add(constant);
				
				Constant constant1=new Constant();
				constant1.setID((long)leadStatus);
				constant1.setKeyValue("WIP");
				actionList.add(constant1);
				request.setAttribute("cafStatus", true);
				ArrayList<String> cafList=assignedLeadsManager.getSiteClosureList(detailDTO.getLobId());
				request.setAttribute("cafList", cafList);
				
			}
				
			else
				actionList.addAll(subStatusList);
			
			request.setAttribute("userList", userList);
			request.setAttribute("actionList", actionList);
			request.setAttribute("detail", detailDTO);
			request.setAttribute("Mobile_FLAG", masterService.getParameterName("Mobile_FLAG"));
		
		
		
		String AssignedOlmid;
		
		Leads leads  =  assignedLeadsManager.getFSCDetails(request.getParameter("pinCode"), sessionUserBean.getUserLoginId());
		forward = mapping.findForward("ShowPage");
		request.setAttribute("LeadId",request.getParameter("leadID") );
		request.setAttribute("Pincode",request.getParameter("pinCode"));
		if(leads !=null) {
		request.setAttribute("OlmId", leads.getFwdOlmId());
		request.setAttribute("UserName", leads.getPrimaryUser());
		}else {
			request.setAttribute("OlmId", "");
			request.setAttribute("UserName", "");
		}
		
		
		return forward;
		
	}catch (Exception e) {
		e.printStackTrace();
		logger.error("Exception occured while initializing editUser page ");
	}
	return null;
	
	
	}


	
	
	
	public ActionForward LeadForwardToMap(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response)throws Exception
	{
		
		ActionForward forward = new ActionForward();
		HttpSession session = request.getSession();
		UserMstr sessionUserBean = (UserMstr) session.getAttribute("USER_INFO");
		Long leadId;
		String pincode;
		System.out.println("Detials"+request.getParameter("leadID")+" "+request.getParameter("OlmId"));
		AssignedLeadsManager assignedLeadsManager = new AssignedLeadsManagerImpl();
		assignedLeadsManager.leadForwordTOFSCs(request.getParameter("leadID"), request.getParameter("OlmId"), sessionUserBean.getUserLoginId(), null);
		forward = mapping.findForward("search");
		return forward;
		
	}
	
	
}
