package org.ofbiz.order.prerefundorder;

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

public class PreRefundOrderServices {
	public static final String module = PreRefundOrderServices.class.getName();
	public static final String resource = "PreCollectionOrderUiLabels";
    public static final String resourceError = "PreCollectionOrderErrorUiLabels";
	 /**
     * Creates a PreRefundOrder.
     * @param dctx The DispatchContext that this service is operating in.//操作service
     * @param context Map containing the input parameters.//Map 参数集合
     * @return Map with the result of the service, the output parameters.// 返回service 结果
     */
	public static Map<String, Object> createPreRefundOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String,Object> results = FastMap.newInstance();
		BasePosObject pObject = null;	
		PreRefundOrder preRefundOrder= null;
		String strResult = "";
	      try{  //获取参数jsonStr串
			String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
			if(UtilValidate.isNotEmpty("jsonStr")){
			preRefundOrder = (PreRefundOrder)JSONUtil.json2Bean(jsonStr,PreRefundOrder.class );
		    pObject = PreRefundOrderWorker.createPreRefundOrder(dctx,preRefundOrder);
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
     * updatePreRefundOrderStatus
     * @param ctx
     * @param context
     * @return
     */
	public static Map<String, Object> updatePreRefundOrderStatus(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String,Object> results = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		Parameter parameter = null;
    	try{
    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
    		if (UtilValidate.isNotEmpty(jsonStr)) {
	    		//JSONObject jsonObj = JSONObject.fromObject(jsonStr);	
    			parameter = (Parameter)JSONUtil.json2Bean(jsonStr,Parameter.class );
	    		pObject= PreRefundOrderWorker.updatePreRefundOrderStatus(dctx,parameter);
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
     * updatePreRefundOrder
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> updatePreRefundOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
    	PreRefundOrder preRefundOrder=null;
    	Map<String,Object> results = FastMap.newInstance();
     	BasePosObject pObject = null;
     	String strResult = "";
         try{
            //获取参数jsonStr串
     		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
 	    	if (UtilValidate.isNotEmpty(jsonStr)) {
 	    		preRefundOrder = (PreRefundOrder)JSONUtil.json2Bean(jsonStr,PreRefundOrder.class );
        		pObject = PreRefundOrderWorker.updatePreRefundOrder(dctx,preRefundOrder);
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
     * getPreRefundOrderById
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getPreRefundOrderById(DispatchContext dctx, Map<String,? extends Object> context) {
    	Map<String,Object> results = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
    	try{
    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject =  PreRefundOrderWorker.getPreRefundOrderById(dctx,jsonObj);
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
     * getPreRefundOrderHeaderByCondition
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String,Object> getPreRefundOrderHeaderByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> results = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
        try {
        	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = PreRefundOrderWorker.getPreRefundOrderHeaderByCondition(dctx,jsonObj);
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
     * getPreRefundOrderDtlByCondition
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String,Object> getPreRefundOrderDtlByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> results = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
        try {
        	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = PreRefundOrderWorker.getPreRefundOrderDtlByCondition(dctx,jsonObj);
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
	
}
