package com.ibm.nas.dto;

import org.json.JSONException;
import org.json.JSONObject;

public class AgencyUserDto {

	
	// num.USER_ID,num.KM_ACTOR_ID,num.USER_LOGIN_ID,nump.LOB_ID,nump.CIRCLE_ID
	
	 private String userId;
	 private String kmActorId;
	 private String userLoginId;
	 private String lobId;
	 private String circleId;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getKmActorId() {
		return kmActorId;
	}
	public void setKmActorId(String kmActorId) {
		this.kmActorId = kmActorId;
	}
	public String getUserLoginId() {
		return userLoginId;
	}
	public void setUserLoginId(String userLoginId) {
		this.userLoginId = userLoginId;
	}
	public String getLobId() {
		return lobId;
	}
	public void setLobId(String lobId) {
		this.lobId = lobId;
	}
	public String getCircleId() {
		return circleId;
	}
	public void setCircleId(String circleId) {
		this.circleId = circleId;
	}
	public JSONObject toJSONObject() {
		JSONObject json=new JSONObject();
		try {
			//json.put("circleId", circleId);
		//	json.put("kmActorId", kmActorId);
			json.put("userLoginId", userLoginId);
			json.put("userId", userId);
			//json.put("lobId", lobId);		

		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		return json; 
	}
	
}
