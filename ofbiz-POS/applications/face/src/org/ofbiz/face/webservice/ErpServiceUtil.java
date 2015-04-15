package org.ofbiz.face.webservice;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.util.JSONUtil;

public class ErpServiceUtil {
	
	/**
	 * convertStringToTimeStamp
	 * "2014-11-03T19:14:19.200+08:00" to "2014-11-03 19:14:19.0"
	 * @param dateStr
	 * @return
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
        	date = new Date(format.parse(dateStr.replace("T", " ").replace("+08:00", "")).getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return new Timestamp(date.getTime());
	}
	
	/**
	 * 根据erp仓库id获取对应pos仓库id
	 * @param delegator
	 * @param erpFacilityId
	 * @return
	 */
	public static String getFacilityId(Delegator delegator, String erpFacilityId) {
		List<GenericValue> gv = null;
		try {
			gv = delegator.findByAnd("ErpFacilityMap", UtilMisc.toMap("erpFacilityId", erpFacilityId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (UtilValidate.isNotEmpty(gv)) {
			return gv.get(0).get("facilityId").toString();
		}
		return "";
	}
	/**
	 * 获取orderId
	 * @param dispatcher
	 * @param type
	 * @param storeId
	 * @return
	 */
	public static String getOrderId(LocalDispatcher dispatcher, String type, String storeId) {
		Map<String, String> orderId = FastMap.newInstance();
		orderId.put("jsonStr", "{\"type\":\"" + type + "\",\"storeId\":\"" + storeId.trim() + "\"}");
		
		Map<String, Object> resultId = FastMap.newInstance();
		try {
			resultId = dispatcher.runSync("getOrderId", orderId);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String resultStr = resultId.get("jsonOut").toString();
		
		BasePosObject obj =(BasePosObject)JSONUtil.json2Bean(resultStr, BasePosObject.class);
		
		String data = obj.getData().toString();
		return data;
	}
	
	public static Map<String, Object> getPropertiesMap() {
		 String Responsibility = UtilProperties.getPropertyValue("organization", "Responsibility", "localhost");
		 String RespApplication = UtilProperties.getPropertyValue("organization", "RespApplication", "localhost");
		 String SecurityGroup = UtilProperties.getPropertyValue("organization", "SecurityGroup", "localhost");
		 String NLSLanguage = UtilProperties.getPropertyValue("organization", "NLSLanguage", "localhost");
		 String Org_Id = UtilProperties.getPropertyValue("organization", "Org_Id", "localhost");
		 
		 Map<String,String> RESTHeader = FastMap.newInstance();
		 RESTHeader.put("Responsibility", Responsibility);
		 RESTHeader.put("RespApplication", RespApplication);
		 RESTHeader.put("SecurityGroup", SecurityGroup);
		 RESTHeader.put("NLSLanguage", NLSLanguage);
		 RESTHeader.put("Org_Id", Org_Id);
		 
		 String accessToken = UtilProperties.getPropertyValue("organization", "accessToken", "localhost");
		 String accessTokenName = UtilProperties.getPropertyValue("organization", "accessTokenName", "localhost");
		 String ebsVersion = UtilProperties.getPropertyValue("organization", "ebsVersion", "localhost");
		 String userName = UtilProperties.getPropertyValue("organization", "userName", "localhost");
		 
		 Map<String,String> data = FastMap.newInstance();
		 data.put("accessToken", accessToken);
		 data.put("accessTokenName", accessTokenName);
		 data.put("ebsVersion", ebsVersion);
		 data.put("userName", userName);
		 
		 Map<String,String> InputParameters = FastMap.newInstance();
		 InputParameters.put("P_STORE_CODE", "");
		 
		 Map<String, Object> inMap = FastMap.newInstance();
		 inMap.put("RESTHeader", RESTHeader);
		 inMap.put("data", data);
		 inMap.put("InputParameters", InputParameters);
		 
		 return inMap;
	}

}
