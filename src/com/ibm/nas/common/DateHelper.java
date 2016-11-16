package com.ibm.nas.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateHelper {

	public static String APPOINMENT_DATE_FORMAT ="dd-MM-yyyy";
	public static String getDateOnly(String frmt ,int day) {
		String date = null;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, +day);
		SimpleDateFormat formatter = new SimpleDateFormat(frmt);
		try {
			date = formatter.format(cal.getTime());
			//System.out.println("*************************"+date);
			return date;
		} catch (Exception e) {
			return null;
		}
	}
}
