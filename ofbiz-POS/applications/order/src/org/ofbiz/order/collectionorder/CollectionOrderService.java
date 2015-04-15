package org.ofbiz.order.collectionorder;

import java.util.Map;

import javolution.util.FastMap;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.util.JSONUtil;

public class CollectionOrderService {
	public static final String module = CollectionOrderService.class.getName();
	public static final String resource = "CollectionOrderUiLabels";
    public static final String resourceError = "CollectionOrderErrorUiLabels";

	 /**
     * Creates a CollectionOrder.
     * @param dctx The DispatchContext that this service is operating in.//操作service
     * @param context Map containing the input parameters.//Map 参数集合
     * @return Map with the result of the service, the output parameters.// 返回service 结果
     */
	public static Map<String, Object> createCollectionOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String,Object> results = FastMap.newInstance();
		BasePosObject pObject = null;	
		String strResult = "";
	      try{  //获取参数jsonStr串
			String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
			CollectionOrder collectionorder= null;
			if(UtilValidate.isNotEmpty("jsonStr")){
		collectionorder = (CollectionOrder)JSONUtil.json2Bean(jsonStr,CollectionOrder.class );
		pObject = CollectionOrderWork.createCollectionOrder(dctx,collectionorder);
		strResult = JSONSerializer.toJSON(pObject).toString();
		results.put("result",strResult);
		if(!pObject.getFlag().equals(Constant.OK)){
			  results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
	      }else{
	    	  results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
	      }
		}else{ 
			results.put("result", "no param");
		}
}catch (Exception e) {
}
  	return results;
}
    /**
     * updateCollectionOrderStatus
     * @param ctx
     * @param context
     * @return
     */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateCollectionOrderStatus(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String,Object> results = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
    	try{
    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
    		if (UtilValidate.isNotEmpty(jsonStr)) {
    		JSONObject jsonObj = JSONObject.fromObject(jsonStr);	
    		pObject= CollectionOrderWork.updateCollectionOrderStatus(dctx,jsonObj);
    		  strResult = JSONSerializer.toJSON(pObject).toString();
  	        results.put("result",strResult);
  		 //根据返回值输出message success or fail
  	        if(!pObject.getFlag().equals(Constant.OK)){
  	        	results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
  	        	}else{
  	        	results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
  	        	}
  	        }else{
  	        results.put("result", "no param");	
  	        }
  	        }catch (Exception e) {
  			e.printStackTrace();
  			}
      
         return results;
      }
	
	  /**
     * updateCollectionOrder
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> updateCollectionOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> results = FastMap.newInstance();
		BasePosObject pObject = null;	
		String strResult = "";
	      try{  //获取参数jsonStr串
			String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
			CollectionOrder collectionorder= null;
			if(UtilValidate.isNotEmpty("jsonStr")){
		collectionorder = (CollectionOrder)JSONUtil.json2Bean(jsonStr,CollectionOrder.class );
        		pObject = CollectionOrderWork.updateCollectionOrder(dctx,collectionorder);
            	strResult = JSONSerializer.toJSON(pObject).toString();
    			results.put("result", strResult);
    		}else{
    			results.put("result", "no param");
    		}
        }catch(Exception e){
        	e.printStackTrace();
        }
        return results;
    }   
    
    /**
     * getCollecOrderById
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getCollectionOrderById(DispatchContext dctx, Map<String,? extends Object> context) {
    	Map<String,Object> results = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
    	try{
    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject =  CollectionOrderWork.getCollectionOrderById(dctx,jsonObj);
				JSONObject jsonObjResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(jsonObjResult).toString();
				results.put("result", strResult);
				if(pObject.getFlag().equals(Constant.OK)){
					results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				}else{
					results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				}
			}else{
				results.put("result", "no param");
			}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		return results;
    }
	
    /**
     * getBackOrderByCondition
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String,Object> getCollectionOrderByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> results = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
        try {
        	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = CollectionOrderWork.getCollectionOrderByCondition(dctx,jsonObj);
				JSONObject jsonObjResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(jsonObjResult).toString();
				results.put("result", strResult);
				if(pObject.getFlag().equals(Constant.OK)){
					results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				}else{
					results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				}
			}else{
				results.put("result", "no param");
			}
 	      
         }catch(Exception e){
            e.printStackTrace(); 
         }
        return results;
    
    }   
/*
 * 通过条件查询收款头数据
 */
    @SuppressWarnings("unchecked")
	public static Map<String,Object> getCollectionOrderHeaderByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> results = FastMap.newInstance();
    BasePosObject pObject = null;
	String strResult = "";
    try {
    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
    	if (UtilValidate.isNotEmpty(jsonStr)) {
			JSONObject jsonObj = JSONObject.fromObject(jsonStr);
			pObject = CollectionOrderWork.getCollectionOrderHeaderByCondition(dctx, jsonObj);
			JSONObject jsonObjResult = JSONUtil.bean2Json(pObject);
			strResult = JSONSerializer.toJSON(jsonObjResult).toString();
			results.put("result", strResult);
			if(pObject.getFlag().equals(Constant.OK)){
				results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			}else{
				results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			}
		}else{
			results.put("result", "no param");
		}
    }catch(Exception e){
       e.printStackTrace(); 
    }
    return results;
    }
    /**
     * getPreOrderDtlByCondition
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String,Object> getCollectionOrderDtlByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
    	 Map<String,Object> results = FastMap.newInstance();
    	 BasePosObject pObject = null;
     	 String strResult = "";
         try {
         	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
 	    	if (UtilValidate.isNotEmpty(jsonStr)) {
 				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
 				pObject = CollectionOrderWork.getCollectionOrderDtlByCondition(dctx, jsonObj);
 				JSONObject jsonObjResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(jsonObjResult).toString();
 				results.put("result", strResult);
 				if(pObject.getFlag().equals(Constant.OK)){
					results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				}else{
					results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				}
 			}else{
 				results.put("result", "no param");
 			}
         }catch(Exception e){
            e.printStackTrace(); 
         }
        return results;
}
}
