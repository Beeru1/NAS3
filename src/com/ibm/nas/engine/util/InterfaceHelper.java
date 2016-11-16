package com.ibm.nas.engine.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

public class InterfaceHelper {
	//private static ArrayList<String> data=null;
	
	/*public static void main(String[] args) throws SQLException {
		try {
			System.out.println("start process");
			InterfaceHelper.startProcess(data);
			System.out.println("end process");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
}*/
	/*private String pinCode=null;
	private String appointmentStart=null;
	private String appointmentEnd=null;*/
	public static JSONObject startProcess(String pinCode,String appointmentStart,String appointmentEnd) {
		HttpURLConnection urlConnection =null;
		String jsonData = "";
		try {
				//Added by srikant
			String RESTURL = ServerPropertyReader.getString("RESTFULLURL.APPONMENT");
				String appointmentst[]=appointmentStart.split(" ");
				String appointmentst1=appointmentst[0];
				String appointmentst2=appointmentst[1];
				String appointmented[]=appointmentEnd.split(" ");
				String appointmented1=appointmented[0];
				String appointmented2=appointmented[1];
				String pin=pinCode;
				String appointmentEnds =null;
				String appointmentstart = null;
				
				try{
					
				   	Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(appointmentst1);
					appointmentstart = new SimpleDateFormat("yyyy-MM-dd").format(date1);
					
					Date dateend = new SimpleDateFormat("dd-MM-yyyy").parse(appointmented1);
					appointmentEnds = new SimpleDateFormat("yyyy-MM-dd").format(dateend);
					
					}
					catch(Exception e)
					{
						
					}
		       
					URL url = new URL(RESTURL+"?lob=4G&tasktype=4G ODC Installation&"+"pin="+pin+"&startTime="+appointmentstart+"T"+appointmentst2+":00&endTime="+appointmentEnds+"T"+appointmented2+":05");
					System.out.println(RESTURL+"?lob=4G&tasktype=4G ODC Installation&"+"pin="+pin+"&startTime="+appointmentstart+"T"+appointmentst2+":00&endTime="+appointmentEnds+"T"+appointmented2+":05");
			  
			 	urlConnection = (HttpURLConnection) url.openConnection();
			 	urlConnection.setRequestMethod("GET");
			 	urlConnection.setDoOutput(true);
			 	urlConnection.setUseCaches(false);
			 	urlConnection.setConnectTimeout(10000);
			 	//urlConnection.setReadTimeout(10000);
			 	urlConnection.setRequestProperty("channel-id", "APP");
			 	urlConnection.setRequestProperty("Content-Type", "application/json");
			 	urlConnection.setRequestProperty("Accept", "application/json");
			 	urlConnection.connect();
			 	
			 	
			 	int HttpResult = urlConnection.getResponseCode();
			 	
			 	if (HttpResult == HttpURLConnection.HTTP_OK) {
			 		BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
			 		String line = null;
			 		while ((line = br.readLine()) != null) {
			 			jsonData += line + "\n";
			 		}
			 		br.close();
			 		urlConnection.disconnect();
			 		if (jsonData == null || jsonData.trim().length() == 0) {
			 			return null;
			 		}

			 		JSONObject jsonObject = new JSONObject(jsonData);
			 		System.out.println(jsonObject+"*****************");
			 		
			 		JSONObject GetAppointmentsResponse  = jsonObject.getJSONObject("GetAppointmentsResponse");
			 		
			 		System.out.println(GetAppointmentsResponse.getString("Message")+"**********mess*******");
			 		JSONArray arr = GetAppointmentsResponse.getJSONArray("AppointmentSlots");
			 		
			 		for(int i=0; i<arr.length(); i++){   
			 			  JSONObject o = arr.getJSONObject(i).getJSONObject("TimePeriod");  
			 			
			 			  System.out.println("TimePeriod=============startTime=="+o.getString("startDateTime")+"*************end Time***********"+o.getString("startDateTime"));  
			 			}
			 		return GetAppointmentsResponse;
			 	}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

