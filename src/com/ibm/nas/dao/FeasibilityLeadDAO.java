package com.ibm.nas.dao;

import java.util.ArrayList;
import java.util.List;

import com.ibm.nas.dto.LeadStatusDTO;
import com.ibm.nas.exception.LMSException;
import com.ibm.nas.wf.dto.BulkFeasibilityDTO;
import com.ibm.nas.wf.dto.Constant;
import com.ibm.nas.wf.dto.Leads;
import com.ibm.nas.forms.LeadForm;

public interface FeasibilityLeadDAO {
	public ArrayList listFeasibilityLeads(String loginID,String startDate,String endDate) throws LMSException;
	public Boolean qualifyTheFeasibleLead(ArrayList masterList) throws LMSException;
	public ArrayList<BulkFeasibilityDTO> listFeasibilityLeadsExcel(String loginID,String startDate,String endDate) throws LMSException;
	public ArrayList<Constant> getSubStatusList(int statusID,int lobId) throws LMSException;
	public boolean isValidRSU(String rsuCode) throws LMSException ;
	public ArrayList<LeadStatusDTO> getStatusList()	throws LMSException ;
	 public ArrayList<Leads> getNewFeasibilityLeads(int circleId,int statusId , String startDate,String endDate) throws LMSException ;
}
