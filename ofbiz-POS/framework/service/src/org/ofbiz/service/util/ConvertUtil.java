package org.ofbiz.service.util;


import java.util.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ConvertUtil {

	/**
	 * 转换字符串->timestamp
	 * @param dateStr
	 * @return Timestamp
	 */
	
	public static Timestamp convertStringToTimeStamp(String dateStr) {
        Date  date = null;
        if(dateStr.equals("")){
        	dateStr = "1975-1-1 00:00:00";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        format.setLenient(false);  
        //要转换字符串 
        try {
        	date = new Date(format.parse(dateStr).getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		return new Timestamp(date.getTime());
	}
	
	public static String convertDateToString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        format.setLenient(false);  
        //转换字符串 
		return format.format(date);
	}
	public static String convertDateToStringM(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM");  
        format.setLenient(false);  
        //转换字符串 
		return format.format(date);
	}
	public static String convertDateToStringD(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        format.setLenient(false);  
        //转换字符串 
		return format.format(date);
	}

}
