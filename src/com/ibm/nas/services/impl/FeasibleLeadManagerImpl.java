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
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
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
import com.ibm.nas.dto.BulkAssignmentErrorLogDto;
import com.ibm.nas.dto.BulkUploadErrorLogDto;
import com.ibm.nas.dto.BulkUploadMsgDto;
import com.ibm.nas.dto.LeadStatusDTO;
import com.ibm.nas.dto.UserMstr;
import com.ibm.nas.exception.LMSException;
import com.ibm.nas.services.MasterService;
import com.ibm.nas.services.impl.MasterServiceImpl;
import com.ibm.nas.dao.FeasibilityLeadDAO;
import com.ibm.nas.dao.impl.FeasibilityLeadDAOImpl;
import com.ibm.nas.wf.dto.BulkFeasibilityDTO;
import com.ibm.nas.wf.dto.Constant;
import com.ibm.nas.wf.dto.Leads;
import com.ibm.nas.forms.LeadForm;
import com.ibm.nas.services.AssignedLeadsManager;
import com.ibm.nas.services.FeasibleLeadManager;

public class FeasibleLeadManagerImpl implements FeasibleLeadManager {

	private static final Logger logger;
	private List<BulkAssignmentErrorLogDto> errorList = new ArrayList<BulkAssignmentErrorLogDto>();
	  ArrayList<Leads> masterList = new ArrayList<Leads>();
	  private ArrayList<Integer> validInsertRowNosList = new ArrayList<Integer>();
	
	static {
		logger = Logger.getLogger(FeasibleLeadManagerImpl.class);
	}
	
	public ArrayList listFeasibilityLeads(String loginID, String startDate,String endDate) throws LMSException {
		FeasibilityLeadDAO feasibilityLeadDAO = new FeasibilityLeadDAOImpl();
		return feasibilityLeadDAO.listFeasibilityLeads(loginID,startDate,endDate);
	}
	
	public boolean isValidRSU(String rsuCode) throws LMSException {
		
		FeasibilityLeadDAO feasibilityLeadDAO = new FeasibilityLeadDAOImpl();
		Boolean flag = false;		
		flag = feasibilityLeadDAO.isValidRSU(rsuCode);
		return flag;
	}
	
	public Boolean qualifyTheFeasibleLead(LeadForm commonForm)	throws LMSException {
		FeasibilityLeadDAO feasibilityLeadDAO = new FeasibilityLeadDAOImpl();
		Boolean flag = false;
		ArrayList<Leads> masterList = new ArrayList<Leads>();
		Leads lead = new Leads();
		lead.setLeadID(commonForm.getLeadID().toString());
		lead.setStatus(commonForm.getActionType());
		lead.setRemarks(commonForm.getClosureComments());
		lead.setSubStatus(commonForm.getSubStatusID());
		lead.setRsuCode(commonForm.getRsuCode());
		lead.setUpdatedBy(commonForm.getUpdatedBy());
		lead.setUdId(commonForm.getUdId());
		lead.setProspect_mobile_number(commonForm.getProspect_mobile_number());
		lead.setCircleId(commonForm.getCircleId());
		
		masterList.add(lead);
		flag = feasibilityLeadDAO.qualifyTheFeasibleLead(masterList);
		return flag;
	}

	 private Set<Integer> getAllowedSubStatusCodes(List subStatusList)
	 {
		 Set <Integer>allowedSubStatusSet = new HashSet<Integer>();
		 for(int i = 0;i<subStatusList.size();i++)
			{
			 Constant cons = (Constant)subStatusList.get(i);
			// System.out.println("---------"+cons.getID());
			 allowedSubStatusSet.add(cons.getID().intValue());
			}
		 return allowedSubStatusSet;
	 }

	 
	public BulkUploadMsgDto uploadFeasibilityMatrix(FormFile file,HttpServletRequest request) throws LMSException {

		boolean isError = false;
		FeasibilityLeadDAO feasibilityLeadDAO = new FeasibilityLeadDAOImpl();
		MasterService masterservice= new MasterServiceImpl();
		Boolean flag = false;
		HttpSession session = request.getSession();
		UserMstr sessionUserBean = (UserMstr) session.getAttribute("USER_INFO");
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
	         
	          while (rows.hasNext()) 
	          {
//	        	  System.out.println("in while-----");
	        	  Row row = (Row) rows.next();
	        	  
	        	//  logger.info(" row.getLastCellNum() "+row.getLastCellNum());
//	        	  logger.info(" row.getPhysicalNumberOfCells() "+ row.getPhysicalNumberOfCells() );
//	        	System.out.println(" row.getPhysicalNumberOfCells() "+ row.getPhysicalNumberOfCells() );
	        	  rowNumber = row.getRowNum();
	        //	  logger.info("rowNumber-------"+rowNumber);
	        	  if(rowNumber == 0)
					{
						if((row.getPhysicalNumberOfCells() != Constants.EXCEL_HEADER_SIZE && "N".equalsIgnoreCase(masterservice.getParameterName("MOBILE_FLAG")))||(row.getPhysicalNumberOfCells() != Constants.EXCEL_HEADER_SIZE-3 && "Y".equalsIgnoreCase(masterservice.getParameterName("MOBILE_FLAG"))))
						{
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

					}	
//	        	 logger.info(" Row Number:" + row.getRowNum());
	        	//  System.out.println("row number-------------"+rowNumber);
	        	 if(row.getRowNum() > 0)    //Starting parsing excel after 1st row
	   	         {
	        		 
	        		 Iterator cells = row.cellIterator();
//	        	  	  System.out.println("enter if");
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
	     		            		if("N".equalsIgnoreCase(masterservice.getParameterName("MOBILE_FLAG"))
	     		            				&& columnIndex == Constants.EXCEL_INDEX_A || columnIndex == Constants.EXCEL_INDEX_B || 
	     		            				columnIndex == Constants.EXCEL_INDEX_C || columnIndex == Constants.EXCEL_INDEX_D ||
	     		            				columnIndex == Constants.EXCEL_INDEX_E){
//	     		            			System.out.println("column number------------------");
//	     		            			System.out.println("cell value----------"+cellValue);
	     		            		switch(columnIndex) {
	     		            		case Constants.EXCEL_INDEX_A:
     			            			bulkDto.setLeadID(cellValue);
     			            		break;
	     		            		case Constants.EXCEL_INDEX_B:
     			            			bulkDto.setRsuCode(cellValue);
     			            		break;
	     		            		case Constants.EXCEL_INDEX_C:
     			            			bulkDto.setStatus(cellValue);
     			            		break;
     			            		case Constants.EXCEL_INDEX_D:
     			            			bulkDto.setSubStatus(cellValue);
     			            		break;
     			            		case Constants.EXCEL_INDEX_E:
     			            			bulkDto.setRemarks(cellValue);
     			            		break;
	     		            		}
     		            		}	
	     		            		if("Y".equalsIgnoreCase(masterservice.getParameterName("MOBILE_FLAG"))
	     		            				&& columnIndex == 6 || columnIndex == 7 || 
	     		            				columnIndex == 8 || columnIndex == 9 ||
	     		            				columnIndex == 10){
//	     		            			System.out.println("column number------------------");
//	     		            			System.out.println("cell value----------"+cellValue);
	     		            		switch(columnIndex) {
	     		            		case 6:
     			            			bulkDto.setLeadID(cellValue);
     			            		break;
	     		            		case 7:
     			            			bulkDto.setRsuCode(cellValue);
     			            		break;
	     		            		case 8:
     			            			bulkDto.setStatus(cellValue);
     			            		break;
     			            		case 9:
     			            			bulkDto.setSubStatus(cellValue);
     			            		break;
     			            		case 10:
     			            			bulkDto.setRemarks(cellValue);
     			            		break;
	     		            		}
     		            		}
	     	            	}//Adding single row to a dto
	     	        	  bulkDto.setUdId(sessionUserBean.getUdId());
	     	        	 bulkDto.setUpdatedBy(sessionUserBean.getUserLoginId());
	     	        	 validateDto(bulkDto,rowNumber); 
	     	        	bulkDto= null;
	     	        	
	     	            }
	     	      
	        	 }//Parsing single row
	        	  
	            //rowNumber++;
	            
	        }//Parsing all rows
	          String logFilePath = PropertyReader.getAppValue("lms.bulk.upload.error.log"); 
			  SimpleDateFormat otdf = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
				logFilePath = logFilePath+ "bulkFeasibilityErrorLog" + otdf.format(new java.util.Date()) + ".csv" ;
				session.setAttribute("bulkLeadFeasibilityErrLogFilePath", logFilePath);
			
				if(errorList.size() ==0 )
					isError = false;
				else
					isError = true;


	          if( masterList.size() > 0)
	          {
	        	//  System.out.println("no error---------------------------------------------");
	        	  flag = feasibilityLeadDAO.qualifyTheFeasibleLead(masterList);
	        	  if(!flag)
				    {
				    	msgDto.setMsgId(Constants.SERVICE_MSG_ERROR);
						msgDto.setMessage(PropertyReader.getAppValue("lms.bulk.upload.error"));
						newFile.delete();
						return msgDto;
				    }
				    else
				    {
				    	for(int i=0;i<validInsertRowNosList.size();i++)
						{
				    		BulkAssignmentErrorLogDto bulkErr = new BulkAssignmentErrorLogDto();
							bulkErr.setRowNum(validInsertRowNosList.get(i));
							bulkErr.setErrMsg("Feasibility done successfully");
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
			logger.info("Error while reading the file");
			throw new LMSException("Exception Occurred in uploadFeasibilityMatrix");
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

	public void validateDto(Leads bulkDto,int rowNumber) throws LMSException 
	{
	 StringBuffer errMsg= new StringBuffer("");
	 boolean errFlag = false;
	  MasterService service = new MasterServiceImpl();
	  int status = 0;
	  int lobId=0;
	  Long leadId = 0L;
	  int prodLobId = 0;
	  FeasibleLeadManager feasibleLeadManager = new FeasibleLeadManagerImpl();

	  try
	  {
		  BulkAssignmentErrorLogDto bulkErr = new BulkAssignmentErrorLogDto();
		 List rsuList = service.getRsuList();
		if(bulkDto.getLeadID() !=null && !bulkDto.getLeadID().equalsIgnoreCase(""))
		{
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
		 if(bulkDto.getStatus()!=null && !bulkDto.getStatus().equalsIgnoreCase("")){
			 if(bulkDto.getStatus().length() <10){
			 try{
				 
				 //added by Nancy Agrawal
				  MasterService masterService=new MasterServiceImpl();
				  leadId= Long.parseLong(bulkDto.getLeadID());
				  prodLobId=masterService.getProductId(leadId);
				//  lobId=masterService.getLobId(bulkDto.getProduct_name());
				  
				  
					 AssignedLeadsManager assignedLeadsManager = new AssignedLeadsManagerImpl();
					 status = Integer.parseInt(bulkDto.getStatus().toString());
				
					 List<Constant> actionList = assignedLeadsManager.getActionList("FEASIBILITY_STATUS");
					 if((getAllowedSubStatusCodes(actionList).contains(status))== false)
				 {
					 errMsg.append("Enter valid STATUS CODE | ");
					 errFlag = true;  
				 }
				 else if(!service.isValidLeadforFeasibility(leadId,LMSStatusCodes.FEASIBILITY))
				 {
					 errMsg.append("Lead is not in Feasibility Status | ");
					 errFlag = true; 
				 }
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
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
			 errMsg.append("STATUS are mandatory | ");
			  errFlag = true;
		 }
		 if(bulkDto.getRsuCode() !=null && !"".equalsIgnoreCase(bulkDto.getRsuCode())){
			 if(bulkDto.getRsuCode().length() <= 25){
			 if(service.isValidRsuInCircle(bulkDto.getRsuCode(), bulkDto.getLeadID() )== false)
			 {
				 errMsg.append("Enter valid RSU CODE | ");
				  errFlag = true;  
			 }
			 }
			 else
			 {
				 errMsg.append("Enter valid RSU CODE | ");
				  errFlag = true;  
			 }
		}
			 else
			 {
				 if(LMSStatusCodes.WIRED == status)
					 /* Added by Parnika for unwired and info-inadequate cases */
				 {
					 errMsg.append("RSU is mandatory | ");
					  errFlag = true; 
				 }
				  /* End of changes by Parnika for unwired and info-inadequate cases */
 
			 }
		 if(bulkDto.getSubStatus()!=null && !bulkDto.getSubStatus().equalsIgnoreCase("")){
			 if(bulkDto.getSubStatus().length() <10){
			 try{
				 int substatus = Integer.parseInt(bulkDto.getSubStatus().toString());
				 List subStatusList = feasibleLeadManager.getSubStatusList(status,prodLobId);
				 if(subStatusList !=null && subStatusList.size() > 0){
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
			 else
			 {
				 errMsg.append("Enter valid SUB STATUS CODE | ");
				  errFlag = true;  
			 }
		 } 
		 else
		 {
			 if(status != LMSStatusCodes.INFO_INADEQUATE){
			 errMsg.append("SUB STATUS are mandatory | ");
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
		// System.out.println("errFlag-------------"+errFlag);
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
  
	
	
	public boolean writeErrorlogs(String logFilePath) throws LMSException
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
		  
	   }//writeErrorlogs

	public ArrayList<BulkFeasibilityDTO> listFeasibilityLeadsExcel(	String loginID,String startDate,String endDate) throws LMSException {
		FeasibilityLeadDAO feasibilityLeadDAO = new FeasibilityLeadDAOImpl();
		return feasibilityLeadDAO.listFeasibilityLeadsExcel(loginID,startDate,endDate);
	}


	public ArrayList<Constant>getSubStatusList(int statusID,int lobId)throws LMSException {
		FeasibilityLeadDAO feasibilityLeadDAO = new FeasibilityLeadDAOImpl();
		return feasibilityLeadDAO.getSubStatusList(statusID,lobId);
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
	 
	 public ArrayList<LeadStatusDTO> getStatusList()throws LMSException {
			FeasibilityLeadDAO feasibilityLeadDAO = new FeasibilityLeadDAOImpl();
			return feasibilityLeadDAO.getStatusList();
		}
	 
	 public ArrayList<Leads>  getNewFeasibilityLeads(int circleId,int statusId , String startDate,String endDate) throws LMSException {
			FeasibilityLeadDAO feasibilityLeadDAO = new FeasibilityLeadDAOImpl();
			return feasibilityLeadDAO.getNewFeasibilityLeads(circleId ,statusId ,startDate,endDate);
		}
}
