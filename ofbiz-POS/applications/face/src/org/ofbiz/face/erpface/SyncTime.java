package org.ofbiz.face.erpface;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.face.log.ErpFaceLogModel;

public class SyncTime {
	public static String getLastTime(ErpFaceLogModel model, Delegator delegator) {
		
		Map<String, Object> pkMap = FastMap.newInstance();
		pkMap.put("model", model.getModel());
		pkMap.put("event", model.getEvent());
		Timestamp  lastTime;
		String timestr="";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
        format.setLenient(false);  
        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");  
        format1.setLenient(false);  
		try {
			GenericValue entity;

			entity = delegator.findByPrimaryKey("FaceSyncTime", pkMap);			//if()
			if (UtilValidate.isNotEmpty(entity)) {
				lastTime = (Timestamp)entity.get("lastTime");
				
				timestr = format.format(lastTime)+"T"+format1.format(lastTime);
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return timestr;
		
	}
	
	public static Timestamp getLastTimestamp(ErpFaceLogModel model, Delegator delegator) {
		
		Map<String, Object> pkMap = FastMap.newInstance();
		pkMap.put("model", model.getModel());
		pkMap.put("event", model.getEvent());
		Timestamp  lastTime = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
        format.setLenient(false);  
        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");  
        format1.setLenient(false);  
		try {
			GenericValue entity;

			entity = delegator.findByPrimaryKey("FaceSyncTime", pkMap);			//if()
			if (UtilValidate.isNotEmpty(entity)) {
				lastTime = (Timestamp)entity.get("lastTime");
			}else {
				
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return lastTime;
		
	}
	
	public static void saveLastTime(ErpFaceLogModel model, Timestamp timestamp, Delegator delegator) {
		
		Map<String, Object> pkMap = FastMap.newInstance();
		pkMap.put("model", model.getModel());
		pkMap.put("event", model.getEvent());
		String lastTime="";
		// Timestamp timestamp = new Timestamp(new Date().getTime());
		try {
			GenericValue entity ;

			entity = delegator.findByPrimaryKey("FaceSyncTime", pkMap);			//if()
			if (UtilValidate.isNotEmpty(entity)) {
				lastTime = entity.get("lastTime").toString();
				entity.set("lastTime", timestamp);
				entity = delegator.makeValue("FaceSyncTime", entity);
				entity.store();
			}else{
				entity = delegator.makeValue("FaceSyncTime", pkMap);
				entity.set("model", model.getModel());
				entity.set("event", (Object)model.getEvent());
				entity.set("lastTime", (Object)timestamp);
				entity = delegator.makeValue("FaceSyncTime", entity);
				entity.create();
				
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
