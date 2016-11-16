package com.ibm.nas.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.upload.FormFile;

import com.ibm.nas.common.Constants;
import com.ibm.nas.common.LMSStatusCodes;
import com.ibm.nas.common.PropertyReader;
import com.ibm.nas.dao.AssignedLeadsDAO;
import com.ibm.nas.dao.impl.AssignedLeadsDAOImpl;
import com.ibm.nas.dto.BulkAssignmentErrorLogDto;
import com.ibm.nas.dto.BulkUploadMsgDto;
import com.ibm.nas.dto.Leads;
import com.ibm.nas.dto.UserDto;
import com.ibm.nas.dto.UserMstr;
import com.ibm.nas.exception.LMSException;
import com.ibm.nas.forms.LeadForm;
import com.ibm.nas.services.AssignedLeadsManager;
import com.ibm.nas.services.FeasibleLeadManager;
import com.ibm.nas.services.MasterService;
import com.ibm.nas.wf.dto.BulkFeasibilityDTO;
import com.ibm.nas.wf.dto.Constant;
import com.ibm.nas.wf.dto.LeadDetailDTO;

public class AssignedLeadsManagerImpl implements AssignedLeadsManager {
	
	private static final Logger logger;
	private List<BulkAssignmentErrorLogDto> errorList = new ArrayList<BulkAssignmentErrorLogDto>();
	  ArrayList<Leads> masterList = new ArrayList<Leads>();
	  private ArrayList<Integer> validInsertRowNosList = new ArrayList<Integer>();
		
	  
	static {
		logger = Logger.getLogger(AssignedLeadsManagerImpl.class);
	}
	
	public ArrayList<Leads> listAssignedLeads(String loginID,String startDate,String endDate,String view) throws LMSException {
		AssignedLeadsDAO assignedLeadsDAO = new AssignedLeadsDAOImpl();
		return assignedLeadsDAO.listAssignedLeads(loginID,startDate,endDate,view);
	}
	//aman
	public ArrayList<Leads> listAssignedLeadsEscalation(String loginID,String startDate,String endDate,String view) throws LMSException {
		AssignedLeadsDAO assignedLeadsDAO = new AssignedLeadsDAOImpl();
		return assignedLeadsDAO.listAssignedLeadsEscalation(loginID,startDate,endDate,view);
	}
	
	public ArrayList<Leads> listAssignedLeadsFeasibility(String loginID,String startDate,String endDate,String view) throws LMSException {
		AssignedLeadsDAO assignedLeadsDAO = new AssignedLeadsDAOImpl();
		return assignedLeadsDAO.listAssignedLeadsFeasibility(loginID,startDate,endDate,view);
	}
	
	public ArrayList<Constant> getActionList(String keyName) throws LMSException {
		AssignedLeadsDAO assignedLeadsDAO = new AssignedLeadsDAOImpl();
		return assignedLeadsDAO.getActionList(keyName);
	}

	public ArrayList<UserDto> getUsersList(ArrayList circleList, ArrayList lobList) throws LMSException {
		AssignedLeadsDAO assignedLeadsDAO = new AssignedLeadsDAOImpl();
		return assignedLeadsDAO.getUsersList(circleList,  lobList);
	}

	public Boolean closeTheLead(LeadForm commonForm) throws LMSException {
		System.out.println("*******inside closeTheLead*******");
		AssignedLeadsDAO assignedLeadsDAO = new AssignedLeadsDAOImpl();
		Boolean flag = false;
		ArrayList<Leads> masterList = new ArrayList<Leads>();
		Leads lead = new Leads();
		lead.setLeadID(commonForm.getLeadID().toString());
		lead.setStatus(commonForm.getActionType());
		lead.setRemarks(commonForm.getClosureComments());
		lead.setSubStatus(commonForm.getSubStatusID());
		lead.setCafNumber(commonForm.getCafNumber());
		System.out.println("*****in commonForm**"+commonForm.getCafNumber() );
		System.out.println("****in Leads***"+ lead.getCafNumber());
		lead.setUpdatedBy(commonForm.getUpdatedBy());
		lead.setUdId(commonForm.getUdId());
		masterList.add(lead);
		flag = assignedLeadsDAO.closeTheLead(masterList);
		return flag;
		//return assignedLeadsDAO.closeTheLead(commonForm);
	}
	
	public Boolean closeTheLeadCloseLoop(LeadForm commonForm) throws LMSException {
		AssignedLeadsDAO assignedLeadsDAO = new AssignedLeadsDAOImpl();
		Boolean flag = false;
		ArrayList<Leads> masterList = new ArrayList<Leads>();
		Leads lead = new Leads();
		lead.setLeadID(commonForm.getLeadID().toString());
		lead.setStatus(commonForm.getActionType());
		lead.setRemarks(commonForm.getClosureComments());
		lead.setSubStatus(commonForm.getSubStatusID());
		lead.setCafNumber(commonForm.getCafNumber());
		lead.setUpdatedBy(commonForm.getUpdatedBy());
		lead.setUdId(commonForm.getUdId());
		masterList.add(lead);
		flag = assignedLeadsDAO.closeTheLeadCloseLoop(masterList);
		return flag;
		//return assignedLeadsDAO.closeTheLead(commonForm);
	}
	
	public Boolean closeTheLeadSms(LeadForm commonForm) throws LMSException {
		AssignedLeadsDAO assignedLeadsDAO = new AssignedLeadsDAOImpl();
		Boolean flag = false;
		ArrayList<Leads> masterList = new ArrayList<Leads>();
		Leads lead = new Leads();
		lead.setLeadID(commonForm.getLeadID().toString());
		lead.setStatus(commonForm.getActionType());
		lead.setRemarks(commonForm.getClosureComments());
		lead.setSubStatus(commonForm.getSubStatusID());
		lead.setCafNumber(commonForm.getCafNumber());
		lead.setUpdatedBy(commonForm.getUpdatedBy());
		lead.setUdId(commonForm.getUdId());
		masterList.add(lead);
		flag = assignedLeadsDAO.closeTheLeadSms(masterList);
		return flag;
		//return assignedLeadsDAO.closeTheLead(commonForm);
	}
	
	public Boolean reAssignTheLead(LeadForm commonForm) throws LMSException {
		AssignedLeadsDAO assignedLeadsDAO = new AssignedLeadsDAOImpl();
		return assignedLeadsDAO.reAssignTheLead(commonForm);
	}


	public LeadDetailDTO viewLeadDetail(Long LeadID) throws LMSException {
		AssignedLeadsDAO assignedLeadsDAO = new AssignedLeadsDAOImpl();
		return assignedLeadsDAO.viewLeadDetail(LeadID);
	}

	
	public ArrayList<BulkFeasibilityDTO> listAssignedLeadsExcel(String loginID,String startDate,String endDate)throws LMSException {
		AssignedLeadsDAO assignedLeadsDAO = new AssignedLeadsDAOImpl();
		return assignedLeadsDAO.listAssignedLeadsExcel(loginID,startDate,endDate);
	}
	public BulkUploadMsgDto uploadAssignedMatrix(FormFile file,HttpServletRequest request) throws LMSException {

		boolean isError = false;
		AssignedLeadsDAO assignedLeadsDAO = new AssignedLeadsDAOImpl();
		HttpSession session = request.getSession();
		UserMstr sessionUserBean = (UserMstr) session.getAttribute("USER_INFO");
		MasterService masterservice= new MasterServiceImpl();
		Boolean flag = false;
		Boolean bulkFlagFwd=false;
		BulkUploadMsgDto msgDto = new BulkUploadMsgDto();
		File newFile = null;
		try
		{
			byte[] fileData =file.getFileData();
			 String path= PropertyReader.getAppValue("path.uploadedTempFile")+ new java.util.Date().getTime()+"_"+file.getFileName() ;
			 newFile  = new File(path);
			 
				RandomAccessFile raf = new RandomAccessFile(newFile, "rw");
				raf.write(fileData);
				raf.close();
				 InputStream inp = new FileInputStream(path);
			      String fileExtn = GetFileExtension(path);
			      Workbook wb_xssf; //Declare XSSF WorkBook
			      Workbook wb_hssf; //Declare HSSF WorkBook
			      Sheet sheet = null; // sheet can be used as common for XSSF and HSSF
		//	WorkBook
			      if (fileExtn.equalsIgnoreCase("xlsx"))
			      {
				       wb_xssf = new XSSFWorkbook(path);
				      System.out.println("xlsx="+wb_xssf.getSheetName(0));
				      sheet = wb_xssf.getSheetAt(0);
			      }
			      if (fileExtn.equalsIgnoreCase("xls"))
			      {
				     POIFSFileSystem fs = new POIFSFileSystem(inp);
			    	  wb_hssf = new HSSFWorkbook(fs);
			    	  System.out.println("xls="+wb_hssf.getSheetName(0));
			    	  sheet = wb_hssf.getSheetAt(0);
			      }
			  Iterator rows = sheet.rowIterator();
			  int totalrows = sheet.getLastRowNum()+1;
			  System.out.println("totalrows--------"+totalrows);
			  
	          int rowNumber = 1;
	        
	          while (rows.hasNext()) {
	        	  
	        	  Row row = (Row) rows.next();
	        	  
	        	//System.out.println(" row.getLastCellNum() "+row.getLastCellNum());
	        	//System.out.println(" row.getPhysicalNumberOfCells() "+ row.getPhysicalNumberOfCells() );
	        	//System.out.println("row.getPhysicalNumberOfCells() "+ row.getPhysicalNumberOfCells());
	        	
	        	
	        	rowNumber = row.getRowNum();
	        //	System.out.println("rowNumber-------"+rowNumber);
	        	
	        	  if(rowNumber == 0)
					{
						if(((row.getPhysicalNumberOfCells() != Constants.EXCEL_HEADER2_SIZE && row.getPhysicalNumberOfCells()!= Constants.EXCEL_HEADER_BULK_FORWARD && "N".equalsIgnoreCase(masterservice.getParameterName("MOBILE_FLAG")))||((row.getPhysicalNumberOfCells() != Constants.EXCEL_HEADER2_SIZE-1 && row.getPhysicalNumberOfCells()!= Constants.EXCEL_HEADER_BULK_FORWARD)&& "Y".equalsIgnoreCase(masterservice.getParameterName("MOBILE_FLAG")) ))  ) 
						{
							System.out.println("getPhysicalNumberOfCells-------"+row.getPhysicalNumberOfCells());
							msgDto.setMsgId(Constants.INVALID_EXCEL);
							msgDto.setMessage(PropertyReader.getAppValue("lms.bulk.upload.invalid.excel"));
							newFile.delete();
							return msgDto;
						}
						
						else
							if(totalrows == 1)
				        	  {
									msgDto.setMsgId(Constants.BLANK_EXCEL);
									msgDto.setMessage(PropertyReader.getAppValue("lms.bulk.upload.blank.excel"));
									newFile.delete();	
									return msgDto;
				        	  }	

					//	else
							//continue;
					}
	        	// logger.info(" Row Number:" + row.getRowNum());
	        	   bulkFlagFwd=row.getPhysicalNumberOfCells()==Constants.EXCEL_HEADER_BULK_FORWARD;
	        	 if(row.getRowNum() > 0)    //Starting parsing excel after 1st row
	   	         {
	        		 
	        		 Iterator cells = row.cellIterator();
	        	  	  
	     	        int columnIndex = 0;
	     	        int cellNo = 0;
	     	        
	     	            if(cells != null)
	     	            {
	     	            	Leads bulkDto = new Leads();
	     	                
	     	        	  while (cells.hasNext()) {
	     	        			  cellNo++;
	     	        			  Cell cell = (Cell) cells.next();
	     	        			  	columnIndex = cell.getColumnIndex();
	     		              		
	     		              		String cellValue = null;
	     		            		switch(cell.getCellType()) {
	     			            		case Cell.CELL_TYPE_NUMERIC:
	     			            			cellValue = String.valueOf((long)cell.getNumericCellValue());
	     			            		break;
	     			            		case Cell.CELL_TYPE_STRING:
	     			            			cellValue = cell.getStringCellValue();
	     				            	break;
	     		            		}
	     		            		if(cellValue != null)
	     		            		{
	     		            			cellValue = cellValue.trim();
	     		            		}
	     		            		System.out.println("cellValue-->"+cellValue+"columnIndex-->"+columnIndex);
	     		            		if("N".equalsIgnoreCase(masterservice.getParameterName("MOBILE_FLAG")) && (columnIndex ==4 || columnIndex ==5 || columnIndex ==8 || columnIndex == 9 || columnIndex==10 ||columnIndex == 11 || columnIndex==57 || columnIndex==58) 
	     		            			&& !bulkFlagFwd)
	     		            		{
	     		            			switch(columnIndex) {
		     		            		case 4:
	     			            			bulkDto.setLeadID(cellValue);
	     			            		break;
		     		            		
		     		            		case 5:
	     			            			bulkDto.setProduct_name(cellValue);
	     			            		break;
		     		            		case  8:
		     		            			bulkDto.setStatus(cellValue);
	     			            		break;
		     		            		case 9://added by Nancy Agrawal
		     		            			bulkDto.setSubStatus(cellValue);
		     		            			break;
		     		            		case 10:
		     		            			bulkDto.setCafNumber(cellValue);
		     		            			break;
	     			            		case 11:
	     			            			bulkDto.setRemarks(cellValue);
	     			            		case 57:
	     			            			bulkDto.setLattitude(cellValue);
	     			            		case 58: 
	     			            			bulkDto.setLongitude(cellValue);
	     			            		break;
	     		            		}
	     		            		}
	     		            		if("Y".equalsIgnoreCase(masterservice.getParameterName("MOBILE_FLAG")) && (columnIndex ==3 || columnIndex ==4 || columnIndex ==7 || columnIndex == 8 || columnIndex==9 ||columnIndex == 10 || columnIndex ==58 || columnIndex==59)
	     		            			 && !bulkFlagFwd )
		     		            		{
	     		            		switch(columnIndex) {
	     		            		case 3:
     			            			bulkDto.setLeadID(cellValue);
     			            		break;
	     		            		
	     		            		case 4:
     			            			bulkDto.setProduct_name(cellValue);
     			            		break;
	     		            		case  7:
	     		            			bulkDto.setStatus(cellValue);
     			            		break;
	     		            		case 8://added by Nancy Agrawal
	     		            			bulkDto.setSubStatus(cellValue);
	     		            			break;
	     		            		case 9:
	     		            			bulkDto.setCafNumber(cellValue);
	     		            			break;
     			            		case 10:
     			            			bulkDto.setRemarks(cellValue);
     			            		case 58:
     			            			bulkDto.setLattitude(cellValue);
     			            		case 59: 
     			            			bulkDto.setLongitude(cellValue);
     			            		break;
     			            			
	     		            		}
	     		            		
     		            		}
	     		            		else if(bulkFlagFwd && (columnIndex==3 || columnIndex ==4 || columnIndex ==5 || columnIndex ==6 || columnIndex ==7 || columnIndex==8)){
	     		            			switch(columnIndex){
	     		            			case 3:
	     		            			       bulkDto.setLeadID(cellValue);
	     		            			       break;
	     		            			case 4:
	     		            				   bulkDto.setProduct(cellValue);
	     		            				   break;
	     		            				   
	     		            			case 5:bulkDto.setStatus(cellValue);
	     		            					break;
	     		            			case 6: bulkDto.setSubStatus(cellValue);
	     		            					break;
	     		            					
	     		            			case 7:bulkDto.setRemarks(cellValue);
	     		            					break;
	     		            					
	     		            			case 8:bulkDto.setFwdOlmId(cellValue);
	     		            			       break;
	     		            			       
	     		            			default: break;
	     		            			       
	     		            			}
	     		            		}
	     	            	}//Adding single row to a dto
	     	        	 bulkDto.setUpdatedBy(sessionUserBean.getUserLoginId());
	     	        	 bulkDto.setUdId(sessionUserBean.getUdId());
	     	        	 bulkDto.setActorId(sessionUserBean.getKmActorId());
	     	        	 
	     	        	 validateDto(bulkDto,rowNumber,bulkFlagFwd); 
	     	        	  }
	     	      
	        	 }//Parsing single row
	        	  
	            //rowNumber++;
	            
	        }//Parsing all rows
	          
	          String logFilePath = PropertyReader.getAppValue("lms.bulk.upload.error.log"); 
			  SimpleDateFormat otdf = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
				logFilePath = logFilePath+ "bulkAssignedErrorLog" + otdf.format(new java.util.Date()) + ".csv" ;
				session.setAttribute("bulkLeadAssignedErrLogFilePath", logFilePath);
				
				if(errorList.size() ==0 )
					isError = false;
				else
					isError = true;


	         //writing error logs to the csv file
	        	  if(masterList.size() > 0 && !bulkFlagFwd){
	        	  flag = assignedLeadsDAO.closeTheLead(masterList);//feasibilityLeadDAO.qualifyTheFeasibleLead(masterList);
	        	  if(!flag)
				    {
				    	msgDto.setMsgId(Constants.SERVICE_MSG_ERROR);
						msgDto.setMessage(PropertyReader.getAppValue("lms.bulk.upload.error"));
						return msgDto;
				    }
				    else
				    {
				    	for(int i=0;i<validInsertRowNosList.size();i++)
						{
				    		BulkAssignmentErrorLogDto bulkErr = new BulkAssignmentErrorLogDto();
							bulkErr.setRowNum(validInsertRowNosList.get(i));
							bulkErr.setErrMsg("Action has been taken succesfully");
							errorList.add(bulkErr);
						}
				    	
	          }
	        	  }
	        	  else if(masterList.size()>0 && bulkFlagFwd){
	        		  boolean fwdflag=false;
	        		  for( int i=0;i<masterList.size();i++){
	        			  Leads dto=masterList.get(i);
	        			  LeadForm leadform=new LeadForm();
	        			  leadform.setLeadId(dto.getLeadID());
	        			  leadform.setUdId(dto.getUdId());
	        			  leadform.setUpdatedBy(dto.getUpdatedBy());
	        			  leadform.setForwardTo(dto.getFwdOlmId());
	        			  leadform.setRemarks(dto.getRemarks());
	        			  leadform.setLeadIds(new String []{dto.getLeadID()});
	        			  fwdflag=reAssignTheLead(leadform);
	        			  
	        		  }
	        		  if(!fwdflag)
					    {
					    	msgDto.setMsgId(Constants.SERVICE_MSG_ERROR);
							msgDto.setMessage(PropertyReader.getAppValue("lms.bulk.upload.error"));
							return msgDto;
					    }
					    else
					    {
					    	for(int i=0;i<validInsertRowNosList.size();i++)
							{
					    		BulkAssignmentErrorLogDto bulkErr = new BulkAssignmentErrorLogDto();
								bulkErr.setRowNum(validInsertRowNosList.get(i));
								bulkErr.setErrMsg("Action has been taken succesfully");
								errorList.add(bulkErr);
							}
					    	
		          }
	        	  }
	        	  if(errorList.size() > 0)
					{
						msgDto.setMsgId(Constants.SERVICE_MSG_FAIL);
						msgDto.setMessage(logFilePath);
						writeErrorlogs(logFilePath);  //writing logs to the csv file
					}


					if(!isError)
					{
						msgDto.setMsgId(Constants.SERVICE_MSG_SUCCESS);
						msgDto.setMessage(PropertyReader.getAppValue("lms.bulk.assignment.success"));
					}
	        	 
        }
		catch (Exception e) {
		
			logger.info(">>>>>>>>>>>>>>  Error while reading the file : "+e.getMessage());
			
			e.printStackTrace();
			throw new LMSException("Exception Occurred in uploadAssignmentMatrix");
        }
		finally{
			try{
			newFile.delete();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				logger.info("Error while deleting the file");
			}
		}
		return msgDto;
		
	}

	public void validateDto(Leads bulkDto,int rowNumber,Boolean bulkFwdFlag) throws LMSException 
	{
		
	 StringBuffer errMsg= new StringBuffer("");
	 boolean errFlag = false;
	  MasterService service = new MasterServiceImpl();
	  int status = 0;
	  int substatus = 0;
	  Long leadId = 0L;
	  int prodLobId=0;
	  int lobId=0;
	  try
	  {
		  FeasibleLeadManager feasibleLeadManager = new FeasibleLeadManagerImpl();
		  
		  //added by Nancy Agrawal
		  MasterService masterService=new MasterServiceImpl();
		  leadId=Long.parseLong(bulkDto.getLeadID());		  
		  prodLobId=masterService.getProductId(leadId);
		
		  BulkAssignmentErrorLogDto bulkErr = new BulkAssignmentErrorLogDto();
		  if(bulkDto.getLeadID() !=null && !bulkDto.getLeadID().equalsIgnoreCase(""))
			{
			  if(bulkDto.getLeadID().length() < 20){
				try
				{
					leadId = Long.parseLong(bulkDto.getLeadID());
				}
				catch(Exception e)
				{
					e.printStackTrace();
					 errMsg.append("Lead ID is not valid | ");
					  errFlag = true; 
				}
			  }
			  else
			  {
				  errMsg.append("Lead ID is not valid | ");
				  errFlag = true; 
			  }
			}
			else
			{
				errMsg.append("Lead ID is not valid | ");
				  errFlag = true;  
			}
		  if(bulkDto.getStatus()!=null && !"".equals(bulkDto.getStatus()) && !bulkFwdFlag){
			  
			  if(bulkDto.getStatus().length() <10)
			  {
			  try{
				  	status = Integer.parseInt(bulkDto.getStatus().toString());
				  	
				  	if(bulkDto.getSubStatus() != null)
				  	{
				  		substatus = Integer.parseInt(bulkDto.getSubStatus().toString());
				  		
				  	}				  	
				  	AssignedLeadsManager assignedLeadsManager = new AssignedLeadsManagerImpl();
			
			 List<Constant> actionList = assignedLeadsManager.getActionList("LEAD_CLOSURE");// for lost/won
			 
			 /* Added by Parnika for updating sub-status from bulk upload */
			 List<Constant> actionList1 = assignedLeadsManager.getActionList("ASSIGNED");// for status 400
			 actionList.addAll(actionList1);
			 /* End of changes by Parnika */
			 
			 List aList = feasibleLeadManager.getSubStatusList(status,prodLobId);//  changed by Nancy agrawal.
				actionList.addAll(aList);
			 if((getAllowedSubStatusCodes(actionList).contains(status))== false)
			 {
				 errMsg.append("Enter valid STATUS CODE | ");
				 errFlag = true;  
			 }

			 
		  	else if((!service.isValidLeadforFeasibility(leadId,LMSStatusCodes.ASSIGNED)) && (!service.isValidLeadforFeasibility(leadId,LMSStatusCodes.REASSIGNED)))
			 {
				 errMsg.append("Lead is not in Assigned Status | ");
				 errFlag = true; 
			 }
			  
			  
			  }
			  catch(Exception e)
			  {
				  errMsg.append("Enter valid STATUS CODE | ");
				  errFlag = true; 
			  }
			  }
			  else
			  {
				  errMsg.append("Enter valid STATUS CODE | ");
				  errFlag = true; 
			  }
		  }
		  else if(!bulkFwdFlag)
		  {
			  errMsg.append("STATUS is mandatory | ");
			  errFlag = true;
		  }
		  
		  if((status == LMSStatusCodes.WON || status == LMSStatusCodes.LOST) && !bulkFwdFlag)
		  {
			  String product=bulkDto.getProduct_name();
			  System.out.println("****Please find Product----+"+ product);
			  if(masterService.validateProduct(product) && status == LMSStatusCodes.WON){
				  if(bulkDto.getActorId().equalsIgnoreCase("10")){
					  errMsg.append("Channel Partner Cannot Mark status as WON |");
					  errFlag=true;
					 
				  }
			  }
			  else if((bulkDto.getSubStatus() == null || "".equalsIgnoreCase(bulkDto.getSubStatus())) && !bulkFwdFlag)
				 {
					 errMsg.append("Sub Status is mandatory for WON/LOST | ");
					  errFlag = true;
				 }
			  else if(bulkDto.getSubStatus().length() > 8)
			  {
					 errMsg.append("Sub Status is invalid | ");
					  errFlag = true;
				 }
			  
		///  }
		  else if(bulkDto.getSubStatus() !=null && !"".equals(bulkDto.getSubStatus()) && !bulkFwdFlag)
		  {
			  System.out.println("else block sub status wrong "+bulkDto.getSubStatus());
			  if(bulkDto.getSubStatus().length() <10)
			  {
			  try{
				          
						 List subStatusList = feasibleLeadManager.getSubStatusList(status,prodLobId);
						 if(subStatusList !=null && subStatusList.size() > 0)
						 {
						 if((getAllowedSubStatusCodes(subStatusList).contains(substatus))== false)
						  {
								  errMsg.append("Enter valid SUB STATUS CODE | ");
								  errFlag = true;  
						  }
						 }

			  }
			  catch(Exception e)
			  {
				  errMsg.append("Enter valid SUB STATUS CODE | ");
				  errFlag = true; 
			  }
			  }
		  }
			  else
			  {
				  errMsg.append("Enter valid SUB STATUS CODE | ");
				  errFlag = true; 
			  }
		  }
		  if(!errFlag)
		  {
			  if(status == LMSStatusCodes.WON && !bulkFwdFlag)
				  if(bulkDto.getCafNumber()== null || "".equalsIgnoreCase(bulkDto.getCafNumber()))
					 {
					
						 errMsg.append("Caf Number is mandatory | ");
						  errFlag = true;
					 }
				  else if(bulkDto.getCafNumber().length() > 25)
				  {
						 errMsg.append("Caf Number can not be greater than 45 Characters | ");
						  errFlag = true;
					 }
		  }
		 if(bulkDto.getRemarks()== null || "".equalsIgnoreCase(bulkDto.getRemarks()))
		 {
			 errMsg.append("Remarks are mandatory | ");
			  errFlag = true;
		 }
		 else
		 if(bulkDto.getRemarks().length() > 1000)
		 {
			 errMsg.append("Remarks can not be greater than 1000 characters. |");
			  errFlag = true;
		 }
		 else if(bulkFwdFlag && (bulkDto.getFwdOlmId()==null || bulkDto.getFwdOlmId().length()<=0 || bulkDto.getFwdOlmId().trim().equalsIgnoreCase("")
				 )){
			 errMsg.append("OLM ID Cannot be left blank |");
			 errFlag=true;
		 }
		 else if(bulkFwdFlag &&  !service.validateOlm(bulkDto.getFwdOlmId())){
			 errMsg.append("Not a Valid OLM ID |");
			 errFlag=true;
		 }
		 else if (bulkFwdFlag && !service.validateProduct(bulkDto.getProduct().trim())){
			 errMsg.append("Not configured Product");
			 errFlag=true;
		 }
		  if(errFlag)
		  {
		    bulkErr.setRowNum(rowNumber);
		    bulkErr.setErrMsg(errMsg.toString());
		    errorList.add(bulkErr);
		  }
		  else{
				masterList.add(bulkDto); 
				 validInsertRowNosList.add(rowNumber);
		  }
	  }
	  
	  catch (Exception e) {
			e.printStackTrace();
			logger.info("Error occured while validating bulkDto");
			throw new LMSException("Exception occurred in validateDto");
      }
	  
	  
	}//validateDto
  
	private Set<Integer> getAllowedSubStatusCodes(List subStatusList)
	 {
		 Set <Integer>allowedSubStatusSet = new HashSet<Integer>();
		 for(int i = 0;i<subStatusList.size();i++)
			{
			 Constant cons = (Constant)subStatusList.get(i);
			 allowedSubStatusSet.add(cons.getID().intValue());
			}
		 return allowedSubStatusSet;
	 }

	public boolean writeErrorlogs(String logFilePath) throws LMSException
	   {
		  boolean isError = false;
		  
		  try
		  {
			  if(errorList.size() > 0)
				{
				
		  FileWriter fw = new FileWriter(logFilePath);
	      PrintWriter pw = new PrintWriter(fw);
	      BulkAssignmentErrorLogDto bulkErrDto;
	      
	      pw.print("Row No");
	      pw.print(",");
	      pw.println("Error Description");
	      
	      Iterator iter = errorList.iterator();
	      
	      while(iter.hasNext())
	      {
	    	  bulkErrDto = (BulkAssignmentErrorLogDto) iter.next();
	    	  pw.print(bulkErrDto.getRowNum());
	    	 
	          pw.print(",");
	          pw.println(bulkErrDto.getErrMsg());
	      
	          isError = true;
	      }
	    
	      pw.flush(); //Flush the output to the file 
	      pw.close(); 
	      fw.close();
		  }
		  }
		  catch(IOException io)
		  {
			io.printStackTrace();
			logger.info("IO Exception occurred while writting error logs to csv");
			throw new LMSException("IO Exception occurred while writting error logs to csv");
		  }
		  catch(Exception e)
		  {
			e.printStackTrace();
			logger.info("Exception occurred while writting error logs to csv");
			throw new LMSException("Exception occurred while writting error logs to csv");
		  }
		  
		  return isError;
		  
	   }//writeErrorlogs
/*	public static Set<Integer> getAllowedAssignedStatusCodes()
	{
		Set <Integer>allowedAssignedStatusSet = new HashSet<Integer>();
		allowedAssignedStatusSet.add(LMSStatusCodes.WON);
		allowedAssignedStatusSet.add(LMSStatusCodes.LOST); 
		allowedAssignedStatusSet.add(LMSStatusCodes.MET);
		allowedAssignedStatusSet.add(LMSStatusCodes.NOTMET); 
		allowedAssignedStatusSet.add(LMSStatusCodes.YET_TO_GET_FEEDBACK); 
		return allowedAssignedStatusSet;
	}
	public static Set<Integer> getAllowedAssignedSubStatusCodes()
	{
		Set <Integer>allowedAssignedSubStatusSet = new HashSet<Integer>();
		allowedAssignedSubStatusSet.add(LMSStatusCodes.MET);
		allowedAssignedSubStatusSet.add(LMSStatusCodes.NOTMET); 
		allowedAssignedSubStatusSet.add(LMSStatusCodes.YET_TO_GET_FEEDBACK); 
		allowedAssignedSubStatusSet.add(LMSStatusCodes.WON);
		allowedAssignedSubStatusSet.add(LMSStatusCodes.LOST); 
		return allowedAssignedSubStatusSet;
	}*/

	public ArrayList<UserDto> getChannelPartnerList(ArrayList circleList, ArrayList lobList)throws LMSException {
		AssignedLeadsDAO assignedLeadsDAO = new AssignedLeadsDAOImpl();
		return assignedLeadsDAO.getChannelPartnerList(circleList, lobList); //changed by sudhanshu
	}
	
	public ArrayList<UserDto> getZonalCoordinatorList(ArrayList circleList, ArrayList lobList)throws LMSException {
		AssignedLeadsDAO assignedLeadsDAO = new AssignedLeadsDAOImpl();
		return assignedLeadsDAO.getZonalCoordinatorList(circleList, lobList); //changed by sudhanshu
	}
	
	
	private static String GetFileExtension(String fname2)
	  {
	      String fileName = fname2;
	      String fname="";
	      String ext="";
	      int mid= fileName.lastIndexOf(".");
	      fname=fileName.substring(0,mid);
	      ext=fileName.substring(mid+1,fileName.length());
	      return ext;
	  }
	
	/* Added By Parnika for LMS Phase 2 */	
	
	public ArrayList<UserDto> getChannelPartnerListForLead(ArrayList circleList, LeadDetailDTO detailDTO,ArrayList lobList)throws LMSException {
		AssignedLeadsDAO assignedLeadsDAO = new AssignedLeadsDAOImpl();
		return assignedLeadsDAO.getChannelPartnerListForLeadImproved(circleList, detailDTO,lobList); 
	}	
	/* End of changes By Parnika */
	
	//changes done by Sudhanshu
	public String getUserID(String mobileNo) throws LMSException{
		AssignedLeadsDAO assignedLeadsDAO = new AssignedLeadsDAOImpl();
		return assignedLeadsDAO.getUserID(mobileNo); 
	}
	//end of changes by Sudhanshu
	
	//Added By Bhaskar
	
	public BulkUploadMsgDto bulkUploadLeadFinalStatus(FormFile file, HttpServletRequest request) throws LMSException {


	
		boolean isError = false;
		AssignedLeadsDAO assignedLeadsDAO = new AssignedLeadsDAOImpl();
		HttpSession session = request.getSession();
		UserMstr sessionUserBean = (UserMstr) session.getAttribute("USER_INFO");
		Boolean flag = false;
		BulkUploadMsgDto msgDto = new BulkUploadMsgDto();
		File newFile = null;
		try
		{
			byte[] fileData =file.getFileData();
			 String path= PropertyReader.getAppValue("path.uploadedTempFile")+ new java.util.Date().getTime()+"_"+file.getFileName() ;
			 newFile  = new File(path);
				RandomAccessFile raf = new RandomAccessFile(newFile, "rw");
				raf.write(fileData);
				raf.close();
				 InputStream inp = new FileInputStream(path);
			      String fileExtn = GetFileExtension(path);
			      Workbook wb_xssf; //Declare XSSF WorkBook
			      Workbook wb_hssf; //Declare HSSF WorkBook
			      Sheet sheet = null; // sheet can be used as common for XSSF and HSSF
			      //	WorkBook
			      if (fileExtn.equalsIgnoreCase("xlsx"))
			      {
				       wb_xssf = new XSSFWorkbook(path);
				      System.out.println("xlsx="+wb_xssf.getSheetName(0));
				      sheet = wb_xssf.getSheetAt(0);
			      }
			      if (fileExtn.equalsIgnoreCase("xls"))
			      {
			    	//  System.out.println("xls file value");
				     POIFSFileSystem fs = new POIFSFileSystem(inp);
			    	  wb_hssf = new HSSFWorkbook(fs);
			    	  System.out.println("xls="+wb_hssf.getSheetName(0));
			    	  sheet = wb_hssf.getSheetAt(0);
			    	 // System.out.println("sheet"+sheet);
			      }
			  Iterator rows = sheet.rowIterator();
			  //System.out.println("rows"+rows);
			  int totalrows = sheet.getLastRowNum()+1;
			  //System.out.println("totalrows"+totalrows);
			
			  
	          int rowNumber = 0;
	        
	          while (rows.hasNext()) {
	        	  
	        	  Row row = (Row) rows.next();
	        	  
	        	//System.out.println(" row.getLastCellNum() "+row.getLastCellNum());
	        	//System.out.println(" row.getPhysicalNumberOfCells() "+ row.getPhysicalNumberOfCells() );
	        	//System.out.println("row.getPhysicalNumberOfCells() "+ row.getPhysicalNumberOfCells());
	        	
	        	  rowNumber=row.getRowNum();
	        	 if(rowNumber == 0)
					{
						if(row.getPhysicalNumberOfCells() != 10)
						{
							msgDto.setMsgId(Constants.INVALID_EXCEL);
							msgDto.setMessage(PropertyReader.getAppValue("lms.bulk.upload.invalid.excel"));
							return msgDto;
						}
						else
							if(totalrows == 1)
				        	  {
									msgDto.setMsgId(Constants.BLANK_EXCEL);
									msgDto.setMessage(PropertyReader.getAppValue("lms.bulk.upload.blank.excel"));
									return msgDto;
				        	  }	

						else
							continue;
					}
	        	  
	        	 
	        	   if(rowNumber > 0)    //Starting parsing excel after 1st row
	        	   {
	        		 Iterator cells = row.cellIterator();
	        	  	  
	     	        int columnIndex = 0;
	     	        int cellNo = 0;
	     	        
	     	            if(cells != null)
	     	            {
	     	            	Leads bulkDto = new Leads();
	     	             
	     	        	  while (cells.hasNext()) {
	     	        		
	     	        			  cellNo++;
	     	        			  Cell cell = (Cell) cells.next();
	     	        			  	columnIndex = cell.getColumnIndex();
	     		              		
	     		              		String cellValue = null;
	     		            		switch(cell.getCellType()) {
	     			            		case Cell.CELL_TYPE_NUMERIC:
	     			            			cellValue = String.valueOf((long)cell.getNumericCellValue());
	     			            		break;
	     			            		case Cell.CELL_TYPE_STRING:
	     			            			cellValue = cell.getStringCellValue();
	     				            	break;
	     		            		}
	     		            		if(cellValue != null)
	     		            		{
	     		            			cellValue = cellValue.trim();
	     		            		}
	     		            		if(columnIndex == 1 || columnIndex == 3 || columnIndex == 6 || columnIndex == 7 || columnIndex == 8 || columnIndex == 9){
	     		            		switch(columnIndex) {
	     		            		case 1:
     			            			bulkDto.setLeadID(cellValue);
     			            		break;
	     		            		case 3:
     			            			bulkDto.setProduct_name(cellValue);
     			            		break;
	     		            		case 6:
     			            			bulkDto.setStatus(cellValue);
     			            		break;
     			            		case 7://added by Nancy Agrawal
     			            			bulkDto.setSubStatus(cellValue);
     			            		break;
     			            		case 8:
     			            			bulkDto.setCafNumber(cellValue);
     			            		break;
     			            		case 9:
     			            			bulkDto.setRemarks(cellValue);
     			            		break;
	     		            		}
     		            		}		
	     	            	}//Adding single row to a dto
	     	        	  bulkDto.setUpdatedBy(sessionUserBean.getUserLoginId());
	     	        	  bulkDto.setUdId(sessionUserBean.getUdId());
	     	        	 errorList = validateLeadFinalDto(bulkDto,rowNumber,errorList); 
	     	        	 }
	     	      
	        	 }//Parsing single row
	        	  
	           //rowNumber++;
	            
	        }//Parsing all rows
	          
	       String logFilePath = PropertyReader.getAppValue("lms.bulk.upload.error.log");
			  SimpleDateFormat otdf = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
			  logFilePath = logFilePath+ "bulkLeadFinalErrorLog" + otdf.format(new java.util.Date()) + ".csv" ;
				session.setAttribute("bulkLeadFinalErrorLog", logFilePath);
				if(errorList.size() ==0 )
					isError = false;
				else
					isError = true;

				
	         //writing error logs to the csv file
	        	  if(masterList.size() > 0){
	        		 
	        		  String msg = assignedLeadsDAO.closeTheFinalLead(masterList); 
	        		
	        	  if(msg.equalsIgnoreCase("error"))
				    {
				    	msgDto.setMsgId(Constants.SERVICE_MSG_ERROR);
						msgDto.setMessage(PropertyReader.getAppValue("lms.bulk.upload.error"));
						return msgDto;
				    }
				    else
				    {
				    	for(int i=0;i<validInsertRowNosList.size();i++)
						{
				    		BulkAssignmentErrorLogDto bulkErr = new BulkAssignmentErrorLogDto();
							bulkErr.setRowNum(validInsertRowNosList.get(i));
							bulkErr.setErrMsg("Action has been taken succesfully");
							errorList.add(bulkErr);
						}
				    	}
	        	  }
	        	  else if(errorList.size() > 0)
					{
	        		 
						msgDto.setMsgId(Constants.SERVICE_MSG_FAIL);
						msgDto.setMessage(logFilePath);
						writeErrorlogs(logFilePath);  //writing logs to the csv file
					}


					if(!isError)
					{
						msgDto.setMsgId(Constants.SERVICE_MSG_SUCCESS);
						msgDto.setMessage(PropertyReader.getAppValue("lms.bulk.assignment.success"));
					}
	        	 
        }
		catch (Exception e) {
			e.printStackTrace();
			logger.info("Error while reading the file");
			throw new LMSException("Exception Occurred in uploadAssignmentMatrix");
        }
		finally{
			try{
			//file.delete();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				logger.info("Error while deleting the file");
			}
		}
		return msgDto;
		
	
	}
	private List<BulkAssignmentErrorLogDto> validateLeadFinalDto(Leads bulkDto, int rowNumber, List<BulkAssignmentErrorLogDto> errorList) {
		//System.out.println("****inside validateLeadFinalDto******* ");
		 StringBuffer errMsg= new StringBuffer("");
		 boolean errFlag = false;
		  MasterService service = new MasterServiceImpl();
		  int status = 0;
		  int substatus = 0;
		  Long leadId = 0L;
		  int lobId=0;
		 
		  try
		  {
			
			  FeasibleLeadManager feasibleLeadManager = new FeasibleLeadManagerImpl();
			  BulkAssignmentErrorLogDto bulkErr = new BulkAssignmentErrorLogDto();
			//added by Nancy Agrawal.
			  MasterService masterService=new MasterServiceImpl();
			  lobId=masterService.getLobId(bulkDto.getProduct_name());
			  System.out.println("*******validateDto product lob is********"+lobId);
			
			  if(bulkDto.getLeadID() !=null && !bulkDto.getLeadID().equalsIgnoreCase(""))
				{
				  if(bulkDto.getLeadID().length() < 20){
					try
					{
						leadId = Long.parseLong(bulkDto.getLeadID());
					}
					catch(Exception e)
					{
						e.printStackTrace();
						 errMsg.append("Lead ID is not valid | ");
						  errFlag = true; 
					}
				  }
				  else
				  {
					  errMsg.append("Lead ID is not valid | ");
					  errFlag = true; 
				  }
				}
				else
				{
					errMsg.append("Lead ID is not valid | ");
					  errFlag = true;  
				}
			  System.out.println("bulkDto.getStatus()"+bulkDto.getStatus());
			  if(bulkDto.getStatus()!=null && !"".equals(bulkDto.getStatus())){
				  if(bulkDto.getStatus().length() <10)
				  {
				  try{
					  	status = Integer.parseInt(bulkDto.getStatus().toString());
					  	if(bulkDto.getSubStatus() != null){
					  		substatus = Integer.parseInt(bulkDto.getSubStatus().toString());
					  	}				  	
					  	AssignedLeadsManager assignedLeadsManager = new AssignedLeadsManagerImpl();
				
				 List<Constant> actionList = assignedLeadsManager.getActionList("LEAD_CLOSURE");
				 
				 // Added by Parnika for updating sub-status from bulk upload 
				 List<Constant> actionList1 = assignedLeadsManager.getActionList("ASSIGNED");
				 actionList.addAll(actionList1);
				 // End of changes by Parnika 
				 
				 List aList = feasibleLeadManager.getSubStatusList(status,lobId);
					actionList.addAll(aList);
				 if((getAllowedSubStatusCodes(actionList).contains(status))== false)
				 {
					 errMsg.append("Enter valid STATUS CODE | ");
					 errFlag = true;  
				 }

				 
			  	else if((!service.isValidLeadforFeasibility(leadId,LMSStatusCodes.ASSIGNED)) && (!service.isValidLeadforFeasibility(leadId,LMSStatusCodes.REASSIGNED)))
				 {
					 errMsg.append("Lead is not in Assigned Status | ");
					 errFlag = true; 
				 }
				  
				  
				  }
				  catch(Exception e)
				  {
					  errMsg.append("Enter valid STATUS CODE | ");
					  errFlag = true; 
				  }
				  }
				  else
				  {
					  errMsg.append("Enter valid STATUS CODE | ");
					  errFlag = true; 
				  }
			  }
			  else
			  {
				  errMsg.append("STATUS is mandatory | ");
				  errFlag = true;
			  }
			  if(status == LMSStatusCodes.WON || status == LMSStatusCodes.LOST)
			  {
				  if(bulkDto.getSubStatus() == null || "".equalsIgnoreCase(bulkDto.getSubStatus()))
					 {
						 errMsg.append("Sub Status is mandatory for WON/LOST | ");
						  errFlag = true;
					 }
				  else if(bulkDto.getSubStatus().length() > 8)
				  {
						 errMsg.append("Sub Status is invalid | ");
						  errFlag = true;
					 }
				  
			 /* else {
				  System.out.println("sub Stsuyd valid block");
				  if(bulkDto.getSubStatus().length() <10){
				  try{
					 //method changed for fetching sub status list
							// List subStatusList = feasibleLeadManager.getSubStatusList(status,lobId);
							 System.out.println("subStatusList"+subStatusList.size());
							 if(subStatusList !=null && subStatusList.size() > 0){
								 System.out.println("subStatusList"+subStatusList.size());
							 if((getAllowedSubStatusCodes(subStatusList).contains(substatus))== false)
							  {System.out.println("valid block");
									  errMsg.append("Enter valid SUB STATUS CODE | ");
									  errFlag = true;  
							  }
							 }

				  }
				
				  
				  catch(Exception e)
				  {
					  errMsg.append("Enter valid SUB STATUS CODE | ");
					  errFlag = true; 
				  }
				  }
				  }*/
				 else
				  {
					  errMsg.append("Enter valid SUB STATUS CODE | ");
					  errFlag = true; 
				  }
			  }
			  if(!errFlag)
			  {
				  if(status == LMSStatusCodes.WON)
					  if(bulkDto.getCafNumber()== null || "".equalsIgnoreCase(bulkDto.getCafNumber()))
						 {
						 // System.out.println("caf number is**********"+ bulkDto.getCafNumber());
							 errMsg.append("Caf Number is mandatory | ");
							  errFlag = true;
						 }
					  else if(bulkDto.getCafNumber().length() > 25)
					  {
							 errMsg.append("Caf Number can not be greater than 45 Characters | ");
							  errFlag = true;
						 }
			  }
		
			  if(bulkDto.getRemarks() == null || "".equalsIgnoreCase(bulkDto.getRemarks()))
			 {
				 errMsg.append("Remarks are mandatory | ");
				  errFlag = true;
			 }
			 else
			 if(bulkDto.getRemarks().length() > 1000)
			 {
				 errMsg.append("Remarks can not be greater than 1000 characters.");
				  errFlag = true;
			 }
			  
			  if(errFlag)
			  {
				
			    bulkErr.setRowNum(rowNumber);
			    bulkErr.setErrMsg(errMsg.toString());
			    errorList.add(bulkErr);
			  }
			  else{
				
						masterList.add(bulkDto); 
						validInsertRowNosList.add(rowNumber);
			  }
		  }
		  catch (Exception e) {
				e.printStackTrace();
				logger.info("Error occured while validating bulkDto");
			
	      }
		return errorList;
		  }
	
	//write error logs 
	/*public boolean writeFinalErrorlogs(String logFilePath) throws LMSException
	   {
		  boolean isError = false;
		  
		  try
		  {
		  FileWriter fw = new FileWriter(logFilePath);
	      PrintWriter pw = new PrintWriter(fw);
	      BulkAssignmentErrorLogDto bulkErrDto;
	      
	      pw.print("Row No");
	      pw.print(",");
	      pw.println("Error Description");
	      
	      Iterator iter = errorList.iterator();
	      
	      while(iter.hasNext())
	      {
	    	  bulkErrDto = (BulkAssignmentErrorLogDto) iter.next();
	    	  pw.print(bulkErrDto.getRowNum());
	          pw.print(",");
	          pw.println(bulkErrDto.getErrMsg());
	          isError = true;
	      }
	    
	      pw.flush(); //Flush the output to the file 
	      pw.close(); 
	      fw.close();
		  }
		  catch(IOException io)
		  {
			io.printStackTrace();
			logger.info("IO Exception occurred while writting error logs to csv");
			throw new LMSException("IO Exception occurred while writting error logs to csv");
		  }
		  catch(Exception e)
		  {
			e.printStackTrace();
			logger.info("Exception occurred while writting error logs to csv");
			throw new LMSException("Exception occurred while writting error logs to csv");
		  }
		  
		  return isError;
		  
	   }*/


public ArrayList<UserDto> getUserListForNewProduct(LeadDetailDTO leadDetails,String userLoginId) throws LMSException
	{
		AssignedLeadsDAO assignedLeadsDAO = new AssignedLeadsDAOImpl();
		return assignedLeadsDAO.getUserListForNewProduct(leadDetails,userLoginId);
	}

public int getLeadStatus(long leadId) throws Exception {
	// TODO Auto-generated method stub
	
	AssignedLeadsDAO assignedLeadsDAO = new AssignedLeadsDAOImpl();
	return assignedLeadsDAO.getLeadStatus(leadId);
}

public ArrayList<String> getSiteClosureList(String lob) throws Exception {
	// TODO Auto-generated method stub
	AssignedLeadsDAO assignedLeadsDAO = new AssignedLeadsDAOImpl();
	return assignedLeadsDAO.getSiteClosureList(lob);
}

public String checkAsssignedPrimaryUser(Long leadId) throws Exception {
	// TODO Auto-generated method stub
	AssignedLeadsDAO assignedLeadsDAO = new AssignedLeadsDAOImpl();
	return assignedLeadsDAO.checkAsssignedPrimaryUser(leadId);
	
}


public Leads getFSCDetails(String pincode, String channelId)
		throws LMSException {
	AssignedLeadsDAO assignedLeadsDAO = new AssignedLeadsDAOImpl();
	return assignedLeadsDAO.getFSCDetails(pincode, channelId);
}


public void leadForwordTOFSCs(String loginID, String actorId,String updateBy,String productId)
		throws LMSException {
	AssignedLeadsDAO assignedLeadsDAO = new AssignedLeadsDAOImpl();
	assignedLeadsDAO.leadForwordTOFSCs(loginID, actorId , updateBy, productId);
	
}
}
