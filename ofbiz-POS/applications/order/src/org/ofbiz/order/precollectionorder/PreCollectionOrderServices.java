package org.ofbiz.order.precollectionorder;
import java.util.Map;
import javolution.util.FastMap;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.order.parameter.Parameter;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.util.JSONUtil;
/**
 * Services for PreCollectionOrderServices maintenance
 */
public class PreCollectionOrderServices {
	public static final String module = PreCollectionOrderServices.class.getName();
	public static final String resource = "PreCollectionOrderUiLabels";
    public static final String resourceError = "PreCollectionOrderErrorUiLabels";
	 /**
     * Creates a PreCollectionOrder.
     * @param dctx The DispatchContext that this service is operating in.//操作service
     * @param context Map containing the input parameters.//Map 参数集合
     * @return Map with the result of the service, the output parameters.// 返回service 结果
     */
	public static Map<String, Object> createPreCollectionOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String,Object> results = FastMap.newInstance();
		BasePosObject pObject = null;	
		PreCollectionOrder preCollectionorder= null;
		String strResult = "";
	      try{  //获取参数jsonStr串
			String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
			if(UtilValidate.isNotEmpty("jsonStr")){
			preCollectionorder = (PreCollectionOrder)JSONUtil.json2Bean(jsonStr,PreCollectionOrder.class );
		    pObject = PreCollectionOrderWorker.createPreCollectionOrder(dctx,preCollectionorder);
		    strResult = JSONSerializer.toJSON(pObject).toString();
		    results.put("jsonOut",strResult);
			if(pObject.getFlag().equals(Constant.OK)){
				  results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		      }else{
		    	  results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
		      }
		}else{ 
			results.put("jsonOut", "no param");
		}
	}catch (Exception e) {
		e.printStackTrace();
	}
  	return results;
}
    /**
     * updatePreCollectionOrderStatus
     * @param ctx
     * @param context
     * @return
     */
	public static Map<String, Object> updatePreCollectionOrderStatus(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String,Object> results = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		Parameter parameter = null;
    	try{
    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
    		if (UtilValidate.isNotEmpty(jsonStr)) {
	    		parameter = (Parameter)JSONUtil.json2Bean(jsonStr,Parameter.class );
	    		pObject= PreCollectionOrderWorker.updatePreCollectionOrderStatus(dctx,parameter);
	    		strResult = JSONSerializer.toJSON(pObject).toString();
	  	        results.put("jsonOut",strResult);
	  		 //根据返回值输出message success or fail
	  	        if(pObject.getFlag().equals(Constant.OK)){
	  	        	results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
	  	        }else{
	  	        	results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
	  	        }
  	        }else{
  	        	results.put("jsonOut", "no param");	
            }
        }catch (Exception e) {
		e.printStackTrace();
		}
         return results;
      }
	
	  /**
     * updatePreCollectionOrder
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> updatePreCollectionOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
    	PreCollectionOrder preCollectionOrder=null;
    	Map<String,Object> results = FastMap.newInstance();
     	BasePosObject pObject = null;
     	String strResult = "";
         try{
            //获取参数jsonStr串
     		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
 	    	if (UtilValidate.isNotEmpty(jsonStr)) {
 	    		preCollectionOrder = (PreCollectionOrder)JSONUtil.json2Bean(jsonStr,PreCollectionOrder.class );
        		pObject = PreCollectionOrderWorker.updatePreCollectionOrder(dctx,preCollectionOrder);
            	strResult = JSONSerializer.toJSON(pObject).toString();
    			results.put("jsonOut", strResult);
    			 if(pObject.getFlag().equals(Constant.OK)){
 	  	        	results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
 	  	        }else{
 	  	        	results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
 	  	        }
    		}else{
    			results.put("jsonOut", "no param");
    		}
        }catch(Exception e){
        	e.printStackTrace();
        }
        return results;
    }   
    
    /**
     * getPreCollecOrderById
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getPreCollectionOrderById(DispatchContext dctx, Map<String,? extends Object> context) {
    	Map<String,Object> results = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
    	try{
    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject =  PreCollectionOrderWorker.getPreCollectionOrderById(dctx,jsonObj);
				JSONObject jsonObjResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(jsonObjResult).toString();
				results.put("jsonOut", strResult);
			}else{
				results.put("jsonOut", "no param");
			}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		return results;
    }
	
    /**
     * getPreCollectionHeaderByCondition
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String,Object> getPreCollectionOrderHeaderByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> results = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
        try {
        	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = PreCollectionOrderWorker.getPreCollectionOrderHeaderByCondition(dctx,jsonObj);
				JSONObject jsonObjResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(jsonObjResult).toString();
				results.put("jsonOut", strResult);
			}else{
				results.put("jsonOut", "no param");
			}
 	      
         }catch(Exception e){
            e.printStackTrace(); 
         }
        return results;
    
    }   
    
    /**
     * getPreCollectionDtlByCondition
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String,Object> getPreCollectionOrderDtlByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> results = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
        try {
        	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = PreCollectionOrderWorker.getPreCollectionOrderDtlByCondition(dctx,jsonObj);
				JSONObject jsonObjResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(jsonObjResult).toString();
				results.put("jsonOut", strResult);
				if(pObject.getFlag().equals(Constant.OK)){
					results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				}else{
					results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				}
			}else{
				results.put("jsonOut", "no param");
			}
 	      
         }catch(Exception e){
            e.printStackTrace(); 
         }
        return results;
    
    }   
	
    /**
     * addPreCollectionAmount
     * @param dctx
     * @param context
     * @return
     */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> addPreCollectionAmount(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> results = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
        try {
        	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
	    		JSONObject jsonObj = JSONObject.fromObject(jsonStr);
        		pObject = PreCollectionOrderWorker.addPreCollectionAmount(dctx,jsonObj);
				JSONObject jsonObjResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(jsonObjResult).toString();
				results.put("jsonOut", strResult);
				if(pObject.getFlag().equals(Constant.OK)){
					results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				}else{
					results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				}
			}else{
				results.put("jsonOut", "no param");
			}
 	      
         }catch(Exception e){
            e.printStackTrace(); 
         }
        return results;
    }  
}
