package com.ibm.nas.dto;

public class BulkAssignmentErrorLogDto {
	
	private int rowNum = 0;
	private String errMsg="";
	
	public int getRowNum() {
		return rowNum;
	}
	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	
 }
