package org.ofbiz.order.payment;

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
public class PaymentInOrderService {

    /**
     * findPostingDate
     * @param ctx
     * @param context
     * @return
     */

    public static Map<String,Object> findPostingDate(DispatchContext dctx, Map<String, ? extends Object> context){
    	Map<String,Object> results = FastMap.newInstance();
        BasePosObject pObject = null;
    	String strResult = "";
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				Map<String,Object> jsonObj = JSONUtil.json2Map(jsonStr);
				pObject = PaymentInOrderWorker.findPostingDate(dctx, jsonObj);
				JSONObject pObjectResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(pObjectResult).toString();
				results.put("jsonOut", strResult);
			}else{
				results.put("jsonOut", "no param");
			}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
        return results;
    }

    public static Map<String, Object> createPaymentInOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String,Object> results = FastMap.newInstance();
		BasePosObject pObject = null;	
		String strResult = "";
	      try{  //获取参数jsonStr串
			String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
			PaymentInOrder paymentorder= null;
			if(UtilValidate.isNotEmpty(jsonStr)){
				paymentorder = (PaymentInOrder)JSONUtil.json2Bean(jsonStr,PaymentInOrder.class );
				pObject = PaymentInOrderWorker.createPaymentInOrder(dctx,paymentorder);
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
      e.printStackTrace();
	}
	  	return results;
	}
    
  
    
    /**
     * getPaymentOrderHeaderByCondition
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String,Object> getPaymentInOrderHeaderById(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String,Object> results = FastMap.newInstance();
    	BasePosObject pObject= null;
    	String strResult = "";
        try {
        	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = PaymentInOrderWorker.getPaymentInOrderHeaderById(dctx,jsonObj);
				JSONObject jsonObjResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(jsonObjResult).toString();
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
     * getPaymentOrderHeaderByCondition
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String,Object> getPaymentInOrderHeaderByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String,Object> results = FastMap.newInstance();
    	BasePosObject pObject= null;
    	String strResult = "";
        try {
        	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = PaymentInOrderWorker.getPaymentInOrderHeaderByCondition(dctx,jsonObj);
				JSONObject jsonObjResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(jsonObjResult).toString();
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
     * getPaymentOrderHeaderByCondition
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String,Object> getPaymentInOrderDtlById(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String,Object> results = FastMap.newInstance();
    	BasePosObject pObject= null;
    	String strResult = "";
        try {
        	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = PaymentInOrderWorker.getPaymentInOrderDtlById(dctx,jsonObj);
				JSONObject jsonObjResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(jsonObjResult).toString();
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
>>>>>>> .r2724
     * getPaymentOrderDtlByCondition
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String,Object> getPaymentInOrderDtlByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
 Map<String,Object> results = FastMap.newInstance();
 BasePosObject pObject = null;
 String strResult = "";
 try {
 	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
  	if (UtilValidate.isNotEmpty(jsonStr)) {
			JSONObject jsonObj = JSONObject.fromObject(jsonStr);
 	        pObject = PaymentInOrderWorker.getPaymentInOrderDtlByCondition(dctx, jsonObj);
 	       JSONObject jsonObjResult = JSONUtil.bean2Json(pObject);
			strResult = JSONSerializer.toJSON(jsonObjResult).toString();
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
     * getPaymentOrderById
     * @param dctx
     * @param context
     * @return
     */
 
	public static Map<String, Object> getPaymentInOrderById(DispatchContext dctx, Map<String,? extends Object> context) {
    	Map<String,Object> resultStr = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = PaymentInOrderWorker.getPaymentInOrderById(dctx,jsonObj);
				JSONObject pObjectResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(pObjectResult).toString();
				resultStr.put("result", strResult);
			}else{
				resultStr.put("result", "no param");
			}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		return resultStr;
    }

	   /**
     * getPaymentInOrderByCondition
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String,Object> getPaymentInOrderByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> resultStr = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = PaymentInOrderWorker.getPaymentInOrderByCondition(dctx, jsonObj);
				JSONObject pObjectResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(pObjectResult).toString();
				resultStr.put("result", strResult);
			}else{
				resultStr.put("result", "no param");
			}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
        return resultStr;
    }    
    
    public static Map<String,Object> addPostingDateOneDay(DispatchContext dctx, Map<String, ? extends Object> context){
    	Map<String,Object> results = FastMap.newInstance();
        BasePosObject pObject = null;
        Parameter parameter = null;
    	String strResult = "";
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
	    		parameter = (Parameter)JSONUtil.json2Bean(jsonStr, Parameter.class);
				pObject = PaymentInOrderWorker.addPostingDateOneDay(dctx, parameter);
				strResult = JSONSerializer.toJSON(pObject).toString();
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
