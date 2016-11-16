package com.ibm.nas.dao;

import java.util.ArrayList;

import com.ibm.nas.dto.Leads;
import com.ibm.nas.dto.UserDto;
import com.ibm.nas.exception.LMSException;
import com.ibm.nas.forms.LeadForm;
import com.ibm.nas.wf.dto.BulkFeasibilityDTO;
import com.ibm.nas.wf.dto.Constant;
import com.ibm.nas.wf.dto.LeadDetailDTO;

public interface AssignedLeadsDAO {
	public ArrayList<Leads> listAssignedLeads(String loginID,String startDate,String endDate,String view) throws LMSException;
	public ArrayList<Leads> listAssignedLeadsEscalation(String loginID,String startDate,String endDate,String view) throws LMSException;
	public ArrayList<Leads> listAssignedLeadsFeasibility(String loginID,String startDate,String endDate,String view) throws LMSException;
	public ArrayList<Constant> getActionList(String keyName) throws LMSException;
	public ArrayList<UserDto> getUsersList(ArrayList circleList,  ArrayList lobList) throws LMSException;
	public Boolean closeTheLead(ArrayList<Leads> masterList) throws LMSException;
	public Boolean closeTheLeadCloseLoop(ArrayList<Leads> masterList) throws LMSException;
	public Boolean closeTheLeadSms(ArrayList<Leads> masterList) throws LMSException;
	public Boolean reAssignTheLead(LeadForm commonForm) throws LMSException;
	public LeadDetailDTO viewLeadDetail(Long leadID) throws LMSException;
	public ArrayList<BulkFeasibilityDTO> listAssignedLeadsExcel(String loginID,String startDate,String endDate) throws LMSException;
	public ArrayList<UserDto> getChannelPartnerList(ArrayList circleList,  ArrayList lobList) throws LMSException;
	public ArrayList<UserDto> getZonalCoordinatorList(ArrayList circleList,  ArrayList lobList) throws LMSException;
//	public ArrayList<UserDto> getLeadDetail(Long lobId) throws LMSException;
	public ArrayList<UserDto> getChannelPartnerListForLead(ArrayList circleList, LeadDetailDTO detailDTO) throws LMSException;
	public ArrayList<UserDto> getChannelPartnerListForLeadImproved(ArrayList circleList, LeadDetailDTO detailDTO,ArrayList lobList) throws LMSException;
	public String getUserID(String mobileNo)throws LMSException;
	public String closeTheFinalLead(ArrayList<Leads> masterList)throws LMSException;
	public ArrayList<UserDto> getUserListForNewProduct(LeadDetailDTO leadDetails, String userLoginId) throws LMSException;
	public int getLeadStatus(long leadId) throws Exception;

	public String checkAsssignedPrimaryUser(Long leadId) throws Exception;
	
	public ArrayList<String> getSiteClosureList(String lob) throws Exception;
	
	public Leads getFSCDetails(String pincode,String channelId) throws LMSException;
	public void leadForwordTOFSCs(String LeadId,String actorId,String updateBy,String productId) throws LMSException;
}
