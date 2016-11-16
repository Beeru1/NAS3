package com.ibm.nas.engine.dao;


import com.ibm.nas.dto.AgencyResponseMessage;
import com.ibm.nas.dto.webservice.LeadCaptureServiceFirstVersDO;
import com.ibm.nas.engine.dataobjects.AgencyCaptureLeadDO;
import com.ibm.nas.engine.dataobjects.CaptureLeadDO;
import com.ibm.nas.engine.exception.DAOException;

public interface CaptureLeadDataDAO {
	
	public boolean captureLeadData(CaptureLeadDO[] leadData) throws DAOException;
	public AgencyResponseMessage captureLeadData(AgencyCaptureLeadDO[] leadData) throws DAOException;

	public String getLeadData(LeadCaptureServiceFirstVersDO leadData) throws DAOException;

}