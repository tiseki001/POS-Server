package org.ofbiz.syncbatch.util;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.service.util.ConvertUtil;
import org.ofbiz.syncbatch.scheduledjob.SyncJob;


public class DefaultUtil {

	public static final String initSyncTime = "2000-01-01 17:03:22";
	public static final String defaultUnUploadedF = "1";
	public static final String defaultUploadedF = "0";
	public static final String uploadFieldName = "isSync";
	
	public static final String inJsonParam = "inJsonParam";
	public static final String outJsonParam = "outJsonParam";
	public static final String resultJsonM = "result";
	public static final String dataJsonM = "data";
	public static final String tableJsonM = "tableName";
	public static final String syncTimeJsonM = "syncTime";
	public static final String storeId = "storeId";
	public static final String successJsonM = "success";
	public static final String errJsonM = "error";
	
	public static final String errMsg = "errMsg";
	
	public static final int nTimeout = 6000;
	
	public static String getPkFieldValue(List<String> keys,Map<String,String> obj){
	    StringBuffer primaryKeyVal = new StringBuffer();
	    primaryKeyVal.append(obj.get(keys.get(0)));
	    for (int i = 1; i < keys.size(); i++) {
	    	primaryKeyVal.append("|");
	    	primaryKeyVal.append(obj.get(keys.get(i)));
	    }
	    return primaryKeyVal.toString();
	}
    public static void convertStr2EntityO(ModelEntity modelEntity,String key,
    		Map<String,String> obj, Map<String, Object> conditionMap, Delegator delegator){
    	ModelField modelField= modelEntity.getField(key);
    	Object objResult = obj.get(key);
    	if (UtilValidate.isNotEmpty(modelField) && UtilValidate.isNotEmpty(objResult)) {
    		String objType = modelField.getType();
    		
    		if (objType.startsWith("date-time")) {
    			java.sql.Timestamp tmpTime = java.sql.Timestamp.valueOf(obj.get(key));
    			tmpTime.setNanos(0);
    			objResult = tmpTime;
			} else if(obj.get(key)!=null&&obj.get(key).toString().equals(SyncJob.posSyncKey)){
				objResult = null;
			}
    		else {
				objResult = modelEntity.convertFieldValue(key,obj.get(key), delegator);
			}
	    	conditionMap.put(key, objResult);
    	}
    }

    public static void convertStr2EntityO(ModelEntity modelEntity,String key,
    		Map<String,String> obj, GenericValue genericValue, Delegator delegator){
    	ModelField modelField= modelEntity.getField(key);
    	Object objResult = obj.get(key);
    	if (UtilValidate.isNotEmpty(modelField) && UtilValidate.isNotEmpty(objResult)) {
    		String objType = modelField.getType();
    		if (objType.startsWith("date-time")) {
    			java.sql.Timestamp tmpTime = java.sql.Timestamp.valueOf(obj.get(key));
    			tmpTime.setNanos(0);
    			objResult = tmpTime;
			} else if(obj.get(key)!=null&&obj.get(key).toString().equals(SyncJob.posSyncKey)){
				objResult = null;
			}
			else {
				objResult = modelEntity.convertFieldValue(key,obj.get(key), delegator);
			}
	    	genericValue.set(key, objResult);
    	}
    }
    
    public static boolean isEntityField(String keyName){
    	if (
    			keyName.equals("lastUpdatedTxStamp")||
    			keyName.equals("createdStamp")||
    			keyName.equals("createdTxStamp")){
    		return false;
    	}
    	return true;
    }
    
    public static Timestamp getStartTimewhere(Delegator delegator){
    	Timestamp time = getPostingDate(delegator);
		Calendar  startDate = Calendar.getInstance();
		time.setHours(0);
		time.setMinutes(0);
		time.setSeconds(0);
		startDate.setTime(time);
		startDate.add(Calendar.DAY_OF_YEAR, -Integer.parseInt("1"));
		String startStr=ConvertUtil.convertDateToString(startDate.getTime());
		Timestamp startTime =ConvertUtil.convertStringToTimeStamp(startStr);
		return startTime;
    }
    public static Timestamp getEndTimewhere(Delegator delegator){
    	Timestamp time = getPostingDate(delegator);
		Calendar  endDate = Calendar.getInstance();
		time.setHours(23);
		time.setMinutes(59);
		time.setSeconds(59);
		endDate.setTime(time);
		endDate.add(Calendar.DAY_OF_YEAR, -Integer.parseInt("1"));
		String endStr=ConvertUtil.convertDateToString(endDate.getTime());
		Timestamp endTime =ConvertUtil.convertStringToTimeStamp(endStr);
		return endTime;
		
    }
    
    public static Timestamp getPostingDate(Delegator delegator) {
    	GenericValue postingDate = null;
    	String storeId = getPosStoreId(delegator);
    	try {
    		postingDate = delegator.findByPrimaryKey("PostingDate", UtilMisc.toMap("productStoreId", storeId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (UtilValidate.isNotEmpty(postingDate)) {
			return (Timestamp) postingDate.get("postingDate");
		}else {
			return UtilDateTime.nowTimestamp();
		}
    }
    
    public static String getPosStoreId(Delegator delegator) {
    	List<GenericValue> posStoreInfos = null;
    	try {
    		posStoreInfos = delegator.findList("PosStoreInfo", null, null, null, null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (UtilValidate.isNotEmpty(posStoreInfos)) {
			return (String) posStoreInfos.get(0).get("storeId");
		}else {
			return "";
		}
    }
    
}
