package org.ofbiz.syncbatch.webservice;


import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;





import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.util.ConvertUtil;
import org.ofbiz.syncbatch.scheduledjob.SyncJob;
import org.ofbiz.syncbatch.util.DefaultUtil;

/**
 * 同步数据库服务
 */
public class SyncService {
	
	public static final String module = SyncService.class.getName();

    /** 接受门店请求，取得实体数据（按时间导入）返回
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters（keyTable实体名 startTime实体记录的更新时间）
     *@return Map with the result of the service, the output parameters
     */
    public static Map<String,Object> exportDBSync(DispatchContext dctx, Map<String, ? extends Object> context) {
    	
    	
        Delegator delegator = dctx.getDelegator();
        String inParamJson = (String)context.get(DefaultUtil.inJsonParam);
        Map<String,Object> result = new HashMap<String,Object>();
        
        try {
        	
        	DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        	String syncTime1 = sdf.format(UtilDateTime.nowTimestamp());
	        JSONObject jsonObj = new JSONObject();
	        jsonObj = JSONObject.fromObject(inParamJson);
	        String keyTable = jsonObj.get(DefaultUtil.tableJsonM).toString();
	        String startTime = jsonObj.get(DefaultUtil.syncTimeJsonM).toString();
	        Map<String, List<Object>> allTableData = exportDBSyncMethod(delegator,keyTable,startTime);
	        JSONObject retJsonObj = new JSONObject();
	        //返回实体取得时间
	        
	        retJsonObj.put(DefaultUtil.syncTimeJsonM, syncTime1);
	        retJsonObj.put(DefaultUtil.dataJsonM,JSONSerializer.toJSON(allTableData));
	        String strResult = retJsonObj.toString();

            result.put(DefaultUtil.outJsonParam, strResult);
        }catch(Exception e){
            return ServiceUtil.returnError(DefaultUtil.errJsonM+e.getMessage());
        }   
        return result;
    }    

    public static Map<String, List<Object>> exportDBSyncMethod(Delegator delegator, String keyTable, String startTime) throws Exception{
//       String[] tables1 = keyTable.split(";");
        //List<GenericValue> toBeSend = null;
        Map<String, List<Object>> allTableData = FastMap.newInstance();
//        for (int k = 0 ; k <tables1.length ; k++ ) {
//        	String[] tableAndWhere = tables1[k].split(":");
//        	if(tableAndWhere[0].equals("ReceiveItemcommandS")){
//        		keyTable+="ProductSequenceC:ProductSequence:"+tableAndWhere[2]+":true;";
//        	}
//        	
//        }
        String[] tables = keyTable.split(";");
        for (int i = 0 ; i <tables.length ; i++ ) {
        	if (UtilValidate.isNotEmpty(tables[i])) {
        		List<EntityCondition> andExprs = FastList.newInstance();
        		List<EntityCondition> andExprsAll = FastList.newInstance();
        		String[] tableAndWhere = tables[i].split(":");
        		if (UtilValidate.isNotEmpty(tableAndWhere[0])) {

			        List<Object> oneTableData = FastList.newInstance();
			        if (tableAndWhere.length>2 && UtilValidate.isNotEmpty(tableAndWhere[2]))
			        	andExprs.add(EntityCondition.makeConditionWhere(tableAndWhere[2]));

		            andExprsAll.add(EntityCondition.makeCondition(andExprs, EntityOperator.AND));
			        if (tableAndWhere.length>3 && UtilValidate.isNotEmpty(tableAndWhere[3]))
			        	andExprsAll.add(EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.GREATER_THAN_EQUAL_TO, ConvertUtil.convertStringToTimeStamp(startTime)));
			        EntityCondition ecl = EntityCondition.makeCondition(andExprsAll, EntityOperator.AND);
			        List<String> orderby = null;
			        if(tableAndWhere[0].equals("InventorynTransfer")){
			        	orderby =  FastList.newInstance();
			        	orderby.add("onhand DESC");
			        	orderby.add("promise DESC");
			        }
		            EntityListIterator entityListIterator = delegator.find(tableAndWhere[0], ecl, null, null, orderby,null);
		            try {
			            GenericValue content;
			            int index = 0;
			            ModelField modelField=null;
			            ModelEntity modelEntity = delegator.getModelEntity(tableAndWhere[0]);
			            while((content = entityListIterator.next()) != null){
			                //GenericValue p = (GenericValue)entityUsageIt.next();
			            	index++;
			            	if (index > 500000) {
								Exception ex = new Exception("数据量太大");
								throw ex;
							} 
			                Map<String,String> itemMap = FastMap.newInstance();
			                for(Map.Entry<String,Object> entryItem : content.getAllFields().entrySet()){
			                	Object itemValue = entryItem.getValue();
			                	//如果实体记录该字段为null，则返回"null"字符串
			                	modelField= modelEntity.getField(entryItem.getKey());
			                	String objType = modelField.getType();
			                	if (null != itemValue && DefaultUtil.isEntityField(entryItem.getKey())){
			                		itemMap.put(entryItem.getKey(), itemValue.toString());
			                	}else if(!objType.startsWith("date-time")){
			                		itemMap.put(entryItem.getKey(), SyncJob.posSyncKey);
			                	}
			                	
			                }
			                
			                oneTableData.add(itemMap);
			            }
		            }catch (Exception e){
		            	throw e;
		            }finally{
		            	entityListIterator.close();
		            }

			        if (tableAndWhere.length>1 && UtilValidate.isNotEmpty(tableAndWhere[1]))
			        	allTableData.put(tableAndWhere[1], oneTableData);
			        else
			        	allTableData.put(tableAndWhere[0], oneTableData);
        		}
        	}
    	}
        return allTableData;
    }

    /** 从总服务器下载/导入数据（按时间导入）
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters（entityKey 导入库表的定义）
     *@return Map with the result of the service, the output parameters
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Map<String,Object> importDBSync(DispatchContext dctx, Map<String, ? extends Object> context) {
        boolean beganTransaction = true;
        JSONObject retJsonObj = new JSONObject();
        String inParamJson = (String)context.get(DefaultUtil.inJsonParam);
        Map<String,Object> result = new HashMap<String,Object>();
    	Map<String, String> errMap = FastMap.newInstance();

        try {
        	//事务开始
        	TransactionUtil.setTransactionTimeout(DefaultUtil.nTimeout);
        	beganTransaction = TransactionUtil.begin();
        	
	        JSONObject jsonObj = JSONObject.fromObject(inParamJson);
	        JSONObject jsonMap = jsonObj.getJSONObject(DefaultUtil.dataJsonM);
	        
	        if (null != jsonMap) {
	        
	        	Map<String, Object> mapJson = JSONObject.fromObject(jsonMap); 
		
		        //库表的循环导入
		        for(Map.Entry<String,Object> entry : mapJson.entrySet()){  
		        	Object strList = entry.getValue();
	                
		        	JSONArray jsonArray = JSONArray.fromObject(strList);
		        	
		        	List<Map<String,String>> importTableItems = (List)jsonArray;
		            //当前库表的记录循环
		        	for (int i = 0; i < importTableItems.size(); i++) {
		        		
		        		Map<String,String> obj=importTableItems.get(i);
		        		
		        		if(!importOneItem(dctx,entry.getKey(),obj,errMap)){
		        			throw new Exception(errMap.get(DefaultUtil.errMsg));
		        		}
		        	}
		        }
		        retJsonObj.put(DefaultUtil.resultJsonM,DefaultUtil.successJsonM);
		        result.put(DefaultUtil.outJsonParam, retJsonObj.toString());
	        }else{
	        	retJsonObj.put(DefaultUtil.resultJsonM,DefaultUtil.errJsonM);
	 	        retJsonObj.put(DefaultUtil.tableJsonM, errMap.get(DefaultUtil.tableJsonM));
	 	        retJsonObj.put(DefaultUtil.errMsg, errMap.get(DefaultUtil.errMsg)+"无数据");
	 	        retJsonObj.put("pKeyVal", errMap.get("pKeyVal"));
	 	        String strResult = retJsonObj.toString();
	 	        result.put(DefaultUtil.outJsonParam, strResult);
	        }

	        
    		//事务提交
    		TransactionUtil.commit(beganTransaction);
        }catch(Exception e){
        	//该记录导入失败，事务回滚；
            try {
            	TransactionUtil.rollback(beganTransaction, e.getMessage(), e);
            } catch (Exception e1){
            	Debug.logError(e1, module);
            }

	        retJsonObj.put(DefaultUtil.resultJsonM,DefaultUtil.errJsonM);
	        retJsonObj.put(DefaultUtil.tableJsonM, errMap.get(DefaultUtil.tableJsonM));
	        retJsonObj.put(DefaultUtil.errMsg, errMap.get(DefaultUtil.errMsg)+e.getMessage());
	        retJsonObj.put("pKeyVal", errMap.get("pKeyVal"));
	        String strResult = retJsonObj.toString();
	        result.put(DefaultUtil.outJsonParam, strResult);

    	}

        return result;
    }
    
    /*public static void importDBSyncMethod(DispatchContext dctx, 
    								Map<String,List<Map<String,String>>> importData,
    								Map<String, String> errMap) throws Exception{
    	boolean beganTransaction = true;
        try {
        	//事务开始
        	TransactionUtil.setTransactionTimeout(600);
        	beganTransaction = TransactionUtil.begin();
	        //库表的循环导入
	        for(Map.Entry<String,List<Map<String,String>>> entry : importData.entrySet()){  
	        	List<Map<String,String>> importTableItems = entry.getValue();
	            
	            //当前库表的记录循环
	        	for (int i = 0; i < importTableItems.size(); i++) {
	        		
	        		Map<String,String> obj=importTableItems.get(i);
	        		
	        		if(!importOneItem(dctx,entry.getKey(),obj,errMap)){
	        			throw new Exception(errMap.get(DefaultUtil.errMsg));
	        		}
	        	}
	        }
        }catch(Exception e){
        	//该记录导入失败，事务回滚；
            try {
            	TransactionUtil.rollback(beganTransaction, e.getMessage(), e);
            } catch (Exception e1){
            	Debug.logError(e1, module);
            }

	        retJsonObj.put(DefaultUtil.resultJsonM,DefaultUtil.errJsonM);
	        retJsonObj.put(DefaultUtil.tableJsonM, errMap.get(DefaultUtil.tableJsonM));
	        retJsonObj.put(DefaultUtil.errMsg, errMap.get(DefaultUtil.errMsg)+e.getMessage());
	        retJsonObj.put("pKeyVal", errMap.get("pKeyVal"));
	        String strResult = retJsonObj.toString();
	        result.put(DefaultUtil.outJsonParam, strResult);

    	}
    }*/
    
    /** 导入一条记录
     *@param ctx The DispatchContext that this service is operating in
     *@param tableName实体名
     *@param obj 记录内容
     *@param bDealErr 是否将导入失败记录保存到数据库表中
     *@return boolean 导入成功标志
     */
     private static boolean importOneItem(DispatchContext ctx,String tableName,Map<String,String> obj,Map<String, String> errMap){
         Delegator delegator = ctx.getDelegator();

         ModelEntity modelEntity = delegator.getModelEntity(tableName);
         
         //取得导入表主键
         List<String> keys = modelEntity.getPkFieldNames();
         Map<String, Object> conditionMap = FastMap.newInstance();

         for (int i = 0; i < keys.size(); i++) {
        	 DefaultUtil.convertStr2EntityO(modelEntity, keys.get(i), obj, conditionMap, delegator);
        	 
         }
         String pKeyVal = DefaultUtil.getPkFieldValue(keys,obj);
         try {
         	
         	List<GenericValue> localItemList = delegator.findByAnd(tableName, conditionMap);
         	//查找门店对应记录，存在进行更新，不存在插入
         	if (localItemList.size() != 0){
         		GenericValue localItem = localItemList.get(0);
         		//Map<String,Object> oriItemMap = localItem.getAllFields();
         		for(Map.Entry<String,String> entry : obj.entrySet()){
         			String key = entry.getKey();
       				DefaultUtil.convertStr2EntityO(modelEntity, key, obj,localItem, delegator);
         		}
         		//localItem.set(DefaultUtil.uploadFieldName, DefaultUtil.defaultUploadedF);
         		localItem.remove("lastUpdatedStamp");
         		localItem.store();

         	} else {
         		
         		Map<String,Object> objImportMap = FastMap.newInstance();
         		for(Map.Entry<String,String> entry : obj.entrySet()){
         			String key = entry.getKey();
         			Object objImport = obj.get(key);
         			if (null != objImport && !objImport.toString().equals("")) {
 	        			DefaultUtil.convertStr2EntityO(modelEntity, key, obj, objImportMap, delegator);
         			}
         		}
         		GenericValue localItem = delegator.makeValue(tableName, objImportMap);
         		//localItem.set(DefaultUtil.uploadFieldName, DefaultUtil.defaultUploadedF);
         		localItem.remove("lastUpdatedStamp");
         		localItem.create();
         	}

         } catch (Exception e) {
             Debug.logWarning(e.getMessage(), module);
             
        	 errMap.put(DefaultUtil.tableJsonM, tableName);
        	 errMap.put(DefaultUtil.errMsg, e.getMessage());
        	 errMap.put("pKeyVal", pKeyVal);
             
             return false;
 	    }

     	return true;
     }
     
     /** 从总服务器下载/导入数据（按时间导入）
      *@param ctx The DispatchContext that this service is operating in
      *@param context Map containing the input parameters（entityKey 导入库表的定义）
      *@return Map with the result of the service, the output parameters
      */
     @SuppressWarnings({ "rawtypes", "unchecked" })
     public static Map<String,Object> importSyncStoreInfo(DispatchContext dctx, Map<String, ? extends Object> context) {
         boolean beganTransaction = true;
         JSONObject retJsonObj = new JSONObject();
         String inParamJson = (String)context.get(DefaultUtil.inJsonParam);
         Map<String,Object> result = new HashMap<String,Object>();
     	Map<String, String> errMap = FastMap.newInstance();

         try {
         	//事务开始
         	TransactionUtil.setTransactionTimeout(DefaultUtil.nTimeout);
         	beganTransaction = TransactionUtil.begin();
         	
 	        JSONObject jsonObj = JSONObject.fromObject(inParamJson);
 	        JSONObject jsonMap = jsonObj.getJSONObject(DefaultUtil.dataJsonM);
 	        String storeId = jsonObj.getString(DefaultUtil.storeId);
 	        
 	        if (null != jsonMap) {
 	        
 	        	Map<String, Object> mapJson = JSONObject.fromObject(jsonMap); 
 		
 		        //库表的循环导入
 		        for(Map.Entry<String,Object> entry : mapJson.entrySet()){  
 		        	Object strList = entry.getValue();
 	                
 		        	JSONArray jsonArray = JSONArray.fromObject(strList);
 		        	
 		        	List<Map<String,String>> importTableItems = (List)jsonArray;
 		            //当前库表的记录循环
 		        	for (int i = 0; i < importTableItems.size(); i++) {
 		        		
 		        		Map<String,String> obj=importTableItems.get(i);
 		        		
 		        		if(!importOneSyncStoreInfo(dctx,entry.getKey(),obj,errMap,storeId)){
 		        			throw new Exception(errMap.get(DefaultUtil.errMsg));
 		        		}
 		        	}
 		        }
 	        }

 	        retJsonObj.put(DefaultUtil.resultJsonM,DefaultUtil.successJsonM);
 	        result.put(DefaultUtil.outJsonParam, retJsonObj.toString());
     		//事务提交
     		TransactionUtil.commit(beganTransaction);
         }catch(Exception e){
         	//该记录导入失败，事务回滚；
             try {
             	TransactionUtil.rollback(beganTransaction, e.getMessage(), e);
             } catch (Exception e1){
             	Debug.logError(e1, module);
             }

 	        retJsonObj.put(DefaultUtil.resultJsonM,DefaultUtil.errJsonM);
 	        retJsonObj.put(DefaultUtil.tableJsonM, errMap.get(DefaultUtil.tableJsonM));
 	        retJsonObj.put(DefaultUtil.errMsg, errMap.get(DefaultUtil.errMsg)+e.getMessage());
 	        retJsonObj.put("pKeyVal", errMap.get("pKeyVal"));
 	        String strResult = retJsonObj.toString();
 	        result.put(DefaultUtil.outJsonParam, strResult);
     	}

         return result;
     }
     /** 导入一条记录
      *@param ctx The DispatchContext that this service is operating in
      *@param tableName实体名
      *@param obj 记录内容
      *@param bDealErr 是否将导入失败记录保存到数据库表中
      *@return boolean 导入成功标志
      */
      private static boolean importOneSyncStoreInfo(DispatchContext ctx,String tableName,Map<String,String> obj,Map<String, String> errMap, String storeId){
    	  Delegator delegator = ctx.getDelegator();

          ModelEntity modelEntity = delegator.getModelEntity(tableName);
          
          //取得导入表主键
          List<String> keys = modelEntity.getPkFieldNames();
          Map<String, Object> conditionMap = FastMap.newInstance();

          for (int i = 0; i < keys.size(); i++) {
         	 DefaultUtil.convertStr2EntityO(modelEntity, keys.get(i), obj, conditionMap, delegator);
          }
          String pKeyVal = DefaultUtil.getPkFieldValue(keys,obj);
          saveSyncStoreInfo(delegator, tableName, pKeyVal, storeId);
          return true;
      }
     /**
      * 存放导入记录到总部
      * @param delegator
      * @param tableName
      * @param pKeyVal
      */
     public static void saveSyncStoreInfo(Delegator delegator, String tableName, String pKeyVal, String storeId) {
    	 String[] pk = {pKeyVal};
    	 if (pKeyVal.indexOf("|") != -1) {
    		 pk = pKeyVal.replace("|", "-").split("-");
    	 }
    	 try {
    		 GenericValue entity = delegator.makeValue("SyncStoreInfo", UtilMisc.toMap("storeId", storeId,
    				 "syncTime", UtilDateTime.nowTimestamp(),
    				 "tableName", tableName,
    				 "sequence", delegator.getNextSeqId("SyncStoreInfo")));
    		 for (int i = 0; i < pk.length; i++) {
    			 entity.set("KeyVal" + (i+1), pk[i]);
    		 }
			entity.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     }
     
     /**
      * 总部服务器更新门店下载数据时间
      * @param dctx
      * @param context
      * @author fangtao
      * @return
      */
     public static Map<String,Object> updateStoreSyncTime(DispatchContext dctx, Map<String, ? extends Object> context) {
    	 Delegator delegator = dctx.getDelegator();
    	 Map<String, Object> result = new HashMap<String, Object>();
    	 String inParamJson = (String)context.get(DefaultUtil.inJsonParam);
    	 
    	 try{
    		 JSONObject jsonObj = new JSONObject();
    		 jsonObj = JSONObject.fromObject(inParamJson);
    		 String productStoreId = (String)jsonObj.opt("productStoreId");
    		 String syncEntityName = (String)jsonObj.opt("syncEntityName");
    		 Timestamp startTime = Timestamp.valueOf(jsonObj.optString("startTime"));
    		 
    		 List<GenericValue> syncList = delegator.findByAnd("SyncScheduleHome", UtilMisc.toMap("productStoreId", productStoreId, "syncEntityName", syncEntityName));
    		//如果有该实体定义的记录，则更新，否则添加
         	if (syncList.size() != 0){
         		GenericValue syncItem = syncList.get(0);
         		syncItem.set("startTime", startTime);
         		syncItem.store();

         	} else {
         		GenericValue syncItem = delegator.makeValue("SyncScheduleHome", 
         				UtilMisc.toMap("productStoreId", productStoreId, "syncEntityName", syncEntityName, "startTime", startTime));
         		syncItem.create();
         	}
    		 
         	result.put(DefaultUtil.outJsonParam, DefaultUtil.successJsonM);
    	 }catch(Exception e){
    		 e.printStackTrace();
    		 return ServiceUtil.returnError(DefaultUtil.errJsonM + e.getMessage());
    	 }
    	 return result;
     }
}