package org.ofbiz.syncbatch.scheduledjob;



import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.content.search.SearchWorker;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.util.CommServiceUtil;
import org.ofbiz.service.util.ConvertUtil;
import org.ofbiz.syncbatch.util.DefaultUtil;

/**
 * 同步数据库任务
 */
public class SyncJob {
	
	public static final String module = SyncJob.class.getName();
	public static final String posSyncKey = "imgsyncposnull";
	public static void importInitData2Store(DispatchContext dctx) {
		
        Map<String, Object> contextInit = FastMap.newInstance();
        Enumeration<Object> jobNames = UtilProperties.getOrderProperties("syncrelation").keys();

        while (jobNames.hasMoreElements()) {
        	
            String jobName = (String) jobNames.nextElement();
            if (!jobName.equals("sync.execF")) {
	            contextInit.put("entityKey", jobName);
	            importData2Store(dctx, contextInit);
            }
        }
	}

	public static Map<String,Object> importInitDataStore(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		importInitData2Store(dctx);
        return ServiceUtil.returnSuccess();
	}

    /** 从总服务器下载/导入数据（按时间导入）
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters（entityKey 导入库表的定义）
     *@return Map with the result of the service, the output parameters
     */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String,Object> importData2Store(DispatchContext dctx, Map<String, ? extends Object> context) {

    	Delegator delegator = dctx.getDelegator();

    	if (!UtilProperties.getPropertyAsBoolean("syncrelation", "sync.execF", true))
	        return ServiceUtil.returnSuccess();
    	//操作实体定义的key值，在syncrelation.properties中定义
    	String strKey = context.get("entityKey").toString();
    	String strEntityTime = getEntityTime(dctx,strKey);
    	
    	String storeId = "";
    	
    	boolean beganTransaction = true;
        JSONObject jsonObj = new JSONObject();
        //根据配置文件取得实体名
        String strTableName = UtilProperties.getPropertyValue("syncrelation", strKey, "");
		String strTableNameUp = UtilProperties.getPropertyValue("syncrelationupload", strKey, "");
        if(!strTableNameUp.equals("")){
        	uploadData2Store(dctx,context);
        }
    	BasePosObject posStoreObj = SearchWorker.getStoreInfo(delegator);
    	if (UtilValidate.isNotEmpty(posStoreObj) && UtilValidate.isNotEmpty(posStoreObj.getData())) {
    		GenericValue storeItem = (GenericValue)posStoreObj.getData();
    		storeId = storeItem.getString("storeId");
    		strTableName = strTableName.replaceAll("\\?", storeItem.getString("storeId"));
    	}
    	
        jsonObj.put(DefaultUtil.tableJsonM, strTableName);
        jsonObj.put(DefaultUtil.syncTimeJsonM, strEntityTime);
        Map<String,String> mInParam = FastMap.newInstance();

        //生成调用web服务的参数
        mInParam.put(DefaultUtil.inJsonParam,jsonObj.toString());
        Map<String, String> errMap = FastMap.newInstance();
        
        //String strServerUrl = UtilProperties.getPropertyValue("syncrelation", "serverUrl", "localhost");
        String strServerUrl = System.getProperty("posstore.server.url");
        //调用总店web服务
        String strJson = CommServiceUtil.sendRectWebService(strServerUrl+"/webtools/control/SOAPService/","exportDBSync", mInParam);
        
        JSONObject jsonRoot = null;
        JSONObject jsonMap = null;

    	try {
	        jsonRoot = JSONObject.fromObject(strJson);
	        jsonMap = jsonRoot.getJSONObject(DefaultUtil.dataJsonM);
    	}catch(Exception e){
    		
    	}

        if (null != jsonMap) {
            try {
            	//事务开始
            	TransactionUtil.setTransactionTimeout(DefaultUtil.nTimeout);
            	beganTransaction = TransactionUtil.begin();

	        	//取得总店数据及导入时间
             	Timestamp syncTime = Timestamp.valueOf(jsonRoot.getString(DefaultUtil.syncTimeJsonM));
		        
		        
		        Map<String, Object> mapJson = JSONObject.fromObject(jsonMap); 
		
		        //是否有记录被导入
		        boolean bModified = false;
		        //库表的循环导入
		        for(Map.Entry<String,Object> entry : mapJson.entrySet()){

		        	Object strList = entry.getValue();
		                
		        	JSONArray jsonArray = JSONArray.fromObject(strList);
		        	List<Map<String,String>> importTableItems = (List)jsonArray;
		            //当前库表的记录循环
		        	for (int i = 0; i < importTableItems.size(); i++) {  
		        		Map<String,String> obj=importTableItems.get(i);
		        		//for(Map.Entry<String,String> entryItem : obj.entrySet()){
		        		//导入一条记录
		        		if(importOneItem(dctx,entry.getKey(),obj,errMap)){
		        			bModified = true;
		        		} else {
		                	//该记录导入失败，事务回滚
		        			String strErrMsg = errMap.get(DefaultUtil.errMsg);
		                	ModelEntity modelEntity = delegator.getModelEntity(entry.getKey());
		                    List<String> keys = modelEntity.getPkFieldNames();

		               	 	saveOneErrItem(delegator,entry.getKey(),DefaultUtil.getPkFieldValue(keys,obj),strErrMsg);
		        			throw new Exception(strErrMsg);
		        		}
		        	}
		        }

		        //原方法：在门店服务器更新纪录
		        updateSyncTime(dctx,strKey,syncTime);
		        
		        //新方法：有记录被导入，则调用总部服务更新导入时间 add by ft
//		        if (bModified) {
//		        	JSONObject syncJsonObj = new JSONObject();
//		        	syncJsonObj.put("productStoreId", storeId);
//		        	syncJsonObj.put("syncEntityName", strKey);
//		        	syncJsonObj.put("startTime", syncTime);
//		        	
//		        	//生成调用web服务的参数
//		        	Map<String,String> syncParam = FastMap.newInstance();
//		        	syncParam.put(DefaultUtil.inJsonParam, syncJsonObj.toString());
//		        	
//		        	//调用总店更新下载数据时间服务
//		        	CommServiceUtil.sendRectWebService(strServerUrl + "/webtools/control/SOAPService/", "updateStoreSyncTime", syncParam);
//		        	
//		        }
	    		//事务提交
	    		TransactionUtil.commit(beganTransaction);
		        return ServiceUtil.returnSuccess();
            } catch (Exception e){
            	try {
            		TransactionUtil.rollback(beganTransaction, e.getMessage(), new Exception(e.getMessage()));
            	} catch (GenericTransactionException eG){
            		Debug.logWarning(eG.getMessage(), module);
            	}
            	return ServiceUtil.returnError(e.getMessage());
            }
        } else {
        	return ServiceUtil.returnError(DefaultUtil.errJsonM);
        }

    }

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
   			DefaultUtil.convertStr2EntityO(modelEntity, keys.get(i), obj,conditionMap,delegator);
        }
        String pKeyVal = DefaultUtil.getPkFieldValue(keys,obj);
        try {
        	
        	GenericValue localItem = delegator.findOne(tableName, conditionMap,false);
			if (null != obj.get(DefaultUtil.uploadFieldName)) {
				obj.put(DefaultUtil.uploadFieldName,DefaultUtil.defaultUploadedF);
			}
        	//查找门店对应记录，存在进行更新，不存在插入
        	if (null != localItem){

        		for(Map.Entry<String,String> entry : obj.entrySet()){
        			String key = entry.getKey();
        			DefaultUtil.convertStr2EntityO(modelEntity, key, obj, localItem,delegator);
        		}
        		localItem.store();

        	} else {
         		Map<String,Object> objImportMap = FastMap.newInstance();
         		for(Map.Entry<String,String> entry : obj.entrySet()){
         			String key = entry.getKey();
 	        		DefaultUtil.convertStr2EntityO(modelEntity, key, obj,objImportMap,delegator);
         		}
         		localItem = delegator.makeValue(tableName, objImportMap);
        		localItem.create();
        	}
        	Thread.sleep(20);
        } catch (Exception e) {
        	e.printStackTrace();
        	Debug.logWarning(e.getMessage(), module);
        	errMap.put(DefaultUtil.tableJsonM, tableName);
        	errMap.put(DefaultUtil.errMsg, e.getMessage());
        	errMap.put("pKeyVal", pKeyVal);
            
            return false;
	    }
        
    	return true;
    }
    
    /** 取得门店库表的更新时间
     *@param ctx The DispatchContext that this service is operating in
     *@param strKey 实体定义
     *@return String 更新时间
     */
    private static String getEntityTime(DispatchContext dctx,String strKey){
    	String strResult="";
    	Delegator delegator = dctx.getDelegator();
    	try {
        	List<GenericValue> timeList = delegator.findByAnd("SyncSchedule", UtilMisc.toMap("entityName", strKey));
        	if (timeList.size() != 0){
        		GenericValue timeItem = timeList.get(0);
        		strResult = timeItem.get("startTime").toString();
        	} else {
        		strResult = DefaultUtil.initSyncTime;
        	}
    	} catch(Exception e){
    		Debug.logError(e, module);
    	}
    	return strResult;
    }
    
    /** 更新门店库表的更新时间
     *@param ctx The DispatchContext that this service is operating in
     *@param strKey 实体定义
     *@param syncTime 总店取得数据的时间
     *@return boolean 执行成功标志
     */
   private static boolean updateSyncTime(DispatchContext dctx,String strKey,Timestamp syncTime){
        Delegator delegator = dctx.getDelegator();
        boolean bResult = false;
        try {

        	List<GenericValue> timeList = delegator.findByAnd("SyncSchedule", UtilMisc.toMap("entityName", strKey));
        	//如果有该实体定义的记录，则更新，否则添加
        	if (timeList.size() != 0){
        		GenericValue timeItem = timeList.get(0);
        		//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        		timeItem.set("startTime",syncTime);
        		timeItem.store();

        	} else {
        		GenericValue timeItem = delegator.makeValue("SyncSchedule", 
        				UtilMisc.toMap("entityName", strKey, "startTime",syncTime));
        		timeItem.create();
        	}

        } catch (Exception e) {
            Debug.logWarning(e.getMessage(), module);
            return bResult;
	    }

    	return bResult;
    }
    
   /** 保存导入失败的记录
    *@param ctx The DispatchContext that this service is operating in
    *@param tableName 实体名
    *@param obj 导入失败记录
    *@param keys 实体主键
    *@return boolean 执行成功标志
    */
    private static void saveOneErrItem(Delegator delegator,String tableName,String primaryKeyVal,String errMsg){

       boolean beganTransaction = true;
       try {
    	   beganTransaction = TransactionUtil.begin();
    	   List<GenericValue> errList = delegator.findByAnd("SyncError", UtilMisc.toMap("tableName", tableName,"pKeyVal",primaryKeyVal));
    	   //如果有该实体定义的记录，则更新，否则添加
    	   if (errList.size() != 0){
				GenericValue errItem = errList.get(0);
				errItem.set(DefaultUtil.errMsg,errMsg);
				errItem.store();
    	   } else {
				GenericValue errItem = delegator.makeValue("SyncError", 
						UtilMisc.toMap("tableName", tableName,"pKeyVal",primaryKeyVal.toString(),DefaultUtil.errMsg,errMsg));
				errItem.create();
    	   }
    	   TransactionUtil.commit(beganTransaction);
       } catch (Exception e) {
    	   Debug.logWarning(e.getMessage(), module);
    	   try {
        	   TransactionUtil.rollback(beganTransaction, e.getMessage(), e);
           } catch (Exception e1){
        	   Debug.logError(e1, module);
           }
       }

    }
    
    public static Map<String,Object> importData2StoreTest(DispatchContext dctx, Map<String, ? extends Object> context) {

    	Map<String, Object> result = FastMap.newInstance();
        //Map<String, Object> result = importData2Store(dctx,UtilMisc.toMap("entityKey", (Object)"sync.server.key.1"));

    	result = uploadData2Store(dctx,UtilMisc.toMap("entityKey", (Object)"sync.server.key.1"));
        LocalDispatcher dispatcher = dctx.getDispatcher();
        
        /*try {
        	//result = dispatcher.runSync("uploadData2Store", UtilMisc.toMap("entityKey", (Object)"sync.server.key.1")); // Create new one
        	result = dispatcher.runSync("import2StoreJob", UtilMisc.toMap("entityKey", (Object)"sync.server.key.1")); // Create new one
            
        } catch (GenericServiceException e) {
            Debug.logWarning(e.getMessage(), module);

            return ServiceUtil.returnError(e.getMessage());
        }*/
        //result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;

    }
    
    
    /**
     * 门店日结上传相关单据到总部
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String,Object> uploadDataToSyncStoreInfo(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        
        Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = new BasePosObject();
		pObject.setFlag("S");
		pObject.setMsg("success");
		String strResult = JSONSerializer.toJSON(pObject).toString();
		resultJson.put("jsonOut", strResult);
        List<EntityCondition> andExprs = FastList.newInstance();
        
        andExprs.add(EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.GREATER_THAN, DefaultUtil.getStartTimewhere(delegator)));
        andExprs.add(EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.LESS_THAN, DefaultUtil.getEndTimewhere(delegator)));
        	
        EntityCondition ecl = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
        
    	if (!UtilProperties.getPropertyAsBoolean("syncrelationupload", "sync.execF", true)) {
    		return resultJson;
    	}

        String strTableName = UtilProperties.getPropertyValue("syncrelationupload", "sync.server.entity", "");
        String[] tables = strTableName.split(";");

        int nCurEntity = 0; 

        try {
	        Map<String, List<Object>> allTableData = FastMap.newInstance();

	        for (int i = 0 ; i <tables.length ; i++ ) {
	        	nCurEntity = i;
	        	List<GenericValue> toBeSend = null;
        		if (UtilValidate.isNotEmpty(tables[i])) {
        			List<Object> oneTableData = FastList.newInstance();
		            toBeSend = delegator.findList(tables[i], ecl, null, null, null,false);
		            ModelEntity modelEntity = delegator.getModelEntity(tables[i]);
		            ModelField modelField=null;
		            Iterator<GenericValue> it = toBeSend.iterator();
		            while(it.hasNext()){
		            	GenericValue p = (GenericValue)it.next();
		                Map<String,String> itemMap = FastMap.newInstance();
		                for(Map.Entry<String,Object> entryItem : p.getPrimaryKey().entrySet()){
		                	Object itemValue = entryItem.getValue();
		                	modelField= modelEntity.getField(entryItem.getKey());
		                	String objType = modelField.getType();
		            		
		                	//如果实体记录该字段为null，则返回"null"字符串
		                	if (null != itemValue && DefaultUtil.isEntityField(entryItem.getKey())){
		                		itemMap.put(entryItem.getKey(), itemValue.toString());
		                	}else if(!objType.startsWith("date-time")){
		                		itemMap.put(entryItem.getKey(), "2000-11-11");
		                	}
		                }
		                
		                oneTableData.add(itemMap);
		            }

		            if (oneTableData.size()>0) {
		            	allTableData.put(tables[i], oneTableData);
		            }
	        	}
        	}
	        if (allTableData.size()>0) {
		        JSONObject jsonObj = new JSONObject();
		        //实体取得时间
		        jsonObj.put(DefaultUtil.dataJsonM,JSONSerializer.toJSON(allTableData));
		        jsonObj.put(DefaultUtil.storeId, DefaultUtil.getPosStoreId(delegator));
		        Map<String,String> mInParam = FastMap.newInstance();
	
		        //生成调用web服务的参数
		        mInParam.put(DefaultUtil.inJsonParam,jsonObj.toString());
		        
		        String serverUrl = System.getProperty("posstore.server.url");
		        //调用总店web服务
		        CommServiceUtil.sendRectWebService(serverUrl+"/webtools/control/SOAPService/","importSyncStoreInfo", mInParam);
	        }
	        return resultJson;
        }catch(Exception e){
        	saveOneErrItem(delegator,tables[nCurEntity],delegator.getNextSeqId(tables[nCurEntity]),e.getMessage());
        	pObject.setFlag("F");
    		pObject.setMsg("error");
    		strResult = JSONSerializer.toJSON(pObject).toString();
    		resultJson.put("jsonOut", strResult);
        	return resultJson;
        }
    }

    
    /** 取得实体数据（按时间导入）上传到总店服务器
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters（entityKey实体key定义）
     *@return Map with the result of the service, the output parameters
     */
    @SuppressWarnings({ "unchecked" })
    public static Map<String,Object> uploadData2Store(DispatchContext dctx, Map<String, ? extends Object> context) {

        Delegator delegator = dctx.getDelegator();
        boolean beganTransaction = true;

    	if (!UtilProperties.getPropertyAsBoolean("syncrelationupload", "sync.execF", true))
	        return ServiceUtil.returnSuccess();

    	String keyTable = context.get("entityKey").toString();
        String strTableName = UtilProperties.getPropertyValue("syncrelationupload", keyTable, "");
        String[] tables = strTableName.split(";");

        int nCurEntity = 0; 

        try {
	        Map<String, List<Object>> allTableData = FastMap.newInstance();
	        Map<String, Boolean> allTableDataUpdateF = FastMap.newInstance();

	        for (int i = 0 ; i <tables.length ; i++ ) {
	        	nCurEntity = i;
	        	List<GenericValue> toBeSend = null;
        		String[] tableAndWhere = tables[i].split(":");
        		if (UtilValidate.isNotEmpty(tableAndWhere[0])) {
        			List<EntityCondition> andExprs = FastList.newInstance();
			        List<Object> oneTableData = FastList.newInstance();
			        if (tableAndWhere.length>2 && UtilValidate.isNotEmpty(tableAndWhere[2]))
			        	andExprs.add(EntityCondition.makeConditionWhere(tableAndWhere[2]));
			        if (tableAndWhere.length>3 && UtilValidate.isNotEmpty(tableAndWhere[3])
			        		&& tableAndWhere[3].equals("T")) {
			        	allTableDataUpdateF.put(tableAndWhere[1], true);
			        }
		            EntityCondition ecl = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
	        	//if (!tables[i].isEmpty()) {
			    //    List<Object> oneTableData = FastList.newInstance();
		            toBeSend = delegator.findList(tableAndWhere[0], ecl, null, null, null,false);
		            ModelEntity modelEntity = delegator.getModelEntity(tableAndWhere[0]);
		            ModelField modelField=null;
		            Iterator<GenericValue> it = toBeSend.iterator();
		            while(it.hasNext()){
		            	GenericValue p = (GenericValue)it.next();
		                Map<String,String> itemMap = FastMap.newInstance();
		                for(Map.Entry<String,Object> entryItem : p.getAllFields().entrySet()){
		                	Object itemValue = entryItem.getValue();
		                	modelField= modelEntity.getField(entryItem.getKey());
		                	String objType = modelField.getType();
		            		
		            		
		                	//如果实体记录该字段为null，则返回"null"字符串
		                	if (null != itemValue && DefaultUtil.isEntityField(entryItem.getKey())){
		                		itemMap.put(entryItem.getKey(), itemValue.toString());
		                	}else if(!objType.startsWith("date-time")){
		                		itemMap.put(entryItem.getKey(), SyncJob.posSyncKey);
		                	}
		                }
		                
		                //Map<String,String> itemMap = UtilGenerics.(p.getAllFields());
		                oneTableData.add(itemMap);
		            }

		            if (oneTableData.size()>0) {
		            	allTableData.put(tableAndWhere[1], oneTableData);
		            }
	        	}
        	}
	       /* if (allTableData.size()>0) {
		        JSONObject jsonObj = new JSONObject();
		        //实体取得时间
	
		        jsonObj.put(DefaultUtil.dataJsonM,JSONSerializer.toJSON(allTableData));
		        Map<String,String> mInParam = FastMap.newInstance();
	
		        //生成调用web服务的参数
		        mInParam.put(DefaultUtil.inJsonParam,jsonObj.toString());
		        
		        //String serverUrl = UtilProperties.getPropertyValue("syncrelation", "serverUrl", "localhost");
		        String serverUrl = System.getProperty("posstore.server.url");
		        //调用总店web服务
		        String strJson = CommServiceUtil.sendRectWebService(serverUrl+"/webtools/control/SOAPService/","importDBSync", mInParam);
		        
	        }*/
	       if (allTableData.size()>0) {
		        JSONObject jsonObj = new JSONObject();
		        //实体取得时间
	
		        jsonObj.put(DefaultUtil.dataJsonM,JSONSerializer.toJSON(allTableData));
		        Map<String,String> mInParam = FastMap.newInstance();
	
		        //生成调用web服务的参数
		        mInParam.put(DefaultUtil.inJsonParam,jsonObj.toString());
		        
		        //String serverUrl = UtilProperties.getPropertyValue("syncrelation", "serverUrl", "localhost");
		        String serverUrl = System.getProperty("posstore.server.url");
		        //调用总店web服务
		        String strJson = CommServiceUtil.sendRectWebService(serverUrl+"/webtools/control/SOAPService/","importDBSync", mInParam);
		        
		        JSONObject jsonRoot = null;
		        Object strResult = null;
		        try {
		        	jsonRoot = JSONObject.fromObject(strJson);
		        	strResult = jsonRoot.get(DefaultUtil.resultJsonM);
		        } catch (Exception e) {
		        	
		        }
		        
		        if (null != strResult && strResult.toString().equals(DefaultUtil.successJsonM)) {
		        	beganTransaction = TransactionUtil.begin();
	        		String errMsg = "";
			        //库表的循环导入
			        for(Map.Entry<String,List<Object>> entry : allTableData.entrySet()){  
	
			        	List<Object> importTableItems = entry.getValue();
			            //当前库表的记录循环
			        	for (int i = 0; i < importTableItems.size(); i++) {  
			        		Map<String,String> obj=(Map<String,String>)importTableItems.get(i);
	
			        		//更新一条记录状态
			        		Boolean bUpdateF = allTableDataUpdateF.get(entry.getKey());
			        		if (null != bUpdateF && bUpdateF.booleanValue() && 
			        				!updateOneItemStatus(delegator,entry.getKey(),obj,errMsg)) {
				            	TransactionUtil.rollback(beganTransaction, errMsg, new Exception(""));
	
				            	ModelEntity modelEntity = delegator.getModelEntity(entry.getKey());
				                List<String> keys = modelEntity.getPkFieldNames();
	
			               	 	saveOneErrItem(delegator,entry.getKey(),DefaultUtil.getPkFieldValue(keys,obj),errMsg);
			               	 	return ServiceUtil.returnError(errMsg);
			        		}
			        	}
			        }
			        TransactionUtil.commit(beganTransaction);
	        		return ServiceUtil.returnSuccess();
	
		        } else {
	
		        	String tableName = jsonRoot.get(DefaultUtil.tableJsonM).toString();
		        	String pKeyVal = jsonRoot.get("pKeyVal").toString();
		        	String errMsg = jsonRoot.get(DefaultUtil.errMsg).toString();
		        	saveOneErrItem(delegator,tableName,pKeyVal,errMsg);
		        	return ServiceUtil.returnError(errMsg.toString());
		        }
	        }
	        return ServiceUtil.returnSuccess();
        }catch(Exception e){
        	String[] tableAndWhere = tables[nCurEntity].split(":");
        	saveOneErrItem(delegator,tableAndWhere[0],delegator.getNextSeqId(tableAndWhere[0]),e.getMessage());
        	return ServiceUtil.returnError(e.getMessage());
        }
    }
    
    /** 更新一条记录的同步状态
     *@param delegator Delegator instance 用于访问数据库
     *@param tableName实体名
     *@param obj 记录内容
     *@param bDealErr 是否将导入失败记录保存到数据库表中
     *@return boolean 导入成功标志
     */
     private static boolean updateOneItemStatus(Delegator delegator,String tableName,Map<String,String> obj,String errMsg){

         ModelEntity modelEntity = delegator.getModelEntity(tableName);
         
         //取得导入表主键
         List<String> keys = modelEntity.getPkFieldNames();
         Map<String, Object> conditionMap = FastMap.newInstance();
         for (int i = 0; i < keys.size(); i++) {
        	 DefaultUtil.convertStr2EntityO(modelEntity, keys.get(i), obj,conditionMap,delegator);
         }
         try {
         	
         	List<GenericValue> localItemList = delegator.findByAnd(tableName, conditionMap);
         	//查找门店对应记录，存在则更新状态
         	if (localItemList.size() != 0){
         		GenericValue localItem = localItemList.get(0);
         		if(localItem.getString("lastUpdatedStamp").equals(obj.get("lastUpdatedStamp"))){
	         		localItem.set(DefaultUtil.uploadFieldName, DefaultUtil.defaultUploadedF);
	
	         		localItem.store();
         		}
         	}

         } catch (Exception e) {

             Debug.logWarning(e.getMessage(), module);
             errMsg = e.getMessage();
             return false;
 	    }
     	return true;
     }
}