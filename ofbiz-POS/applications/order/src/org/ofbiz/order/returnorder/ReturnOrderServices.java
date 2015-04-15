package org.ofbiz.order.returnorder;

import java.util.HashMap;
import java.util.Map;

import javolution.util.FastMap;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.order.parameter.Parameter;
import org.ofbiz.order.refundorder.RefundOrder;
import org.ofbiz.order.returninwhsorder.ReturnInWhsOrder;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.util.JSONUtil;

public class ReturnOrderServices {
	/**
	 * Services for ReturnOrder maintenance
	 */
	public static final String module = ReturnOrderServices.class.getName();
	public static final String resource = "ReturnOrderUiLabels";
    public static final String resourceError = "ReturnOrderErrorUiLabels";
    /**
     * Creates a ReturnOrder.
     * @param dctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> createReturnOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
    	ReturnOrder returnOrder=null;
    	Map<String,Object> resultStr = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				//JSONObject jsonObj = JSONObject.fromObject(jsonStr);
	    		returnOrder=(ReturnOrder)JSONUtil.json2Bean(jsonStr, ReturnOrder.class);
				pObject = ReturnOrderWorker.createReturnOrder(dctx, returnOrder);
				strResult = JSONSerializer.toJSON(pObject).toString();
				resultStr.put("jsonOut", strResult);
				if(pObject.getFlag().equals(Constant.OK)){
					resultStr.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				}else{
					resultStr.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				}
			}else{
				resultStr.put("jsonOut", "no param");
			}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
        return resultStr;
    }
    /**
     * updateReturnOrderStatus
     * @param ctx
     * @param context
     * @return
     */
    public static Map<String, Object> updateReturnOrderStatus(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String, Object> resultStr = FastMap.newInstance();//结果集合
    	BasePosObject pObject = null;
    	Parameter parameter = null;
    	String strResult = "";
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				parameter=(Parameter)JSONUtil.json2Bean(jsonStr, Parameter.class);
				pObject = ReturnOrderWorker.updateReturnOrderStatus(dctx, parameter);
				strResult = JSONSerializer.toJSON(pObject).toString();
				resultStr.put("jsonOut", strResult);
				if(pObject.getFlag().equals(Constant.OK)){
					resultStr.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				}else{
					resultStr.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				}
			}else{
				resultStr.put("jsonOut", "no param");
			}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
       return resultStr;
    }
    /**
     * updateReturnOrder
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> updateReturnOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
    	ReturnOrder returnOrder=null;
    	Map<String, Object> resultStr = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
	    		returnOrder=(ReturnOrder)JSONUtil.json2Bean(jsonStr, ReturnOrder.class);
				pObject = ReturnOrderWorker.updateReturnOrder(dctx, returnOrder);
				strResult = JSONSerializer.toJSON(pObject).toString();
				resultStr.put("jsonOut", strResult);
				if(pObject.getFlag().equals(Constant.OK)){
					resultStr.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				}else{
					resultStr.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				}
			}else{
				resultStr.put("jsonOut", "no param");
			}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
        return resultStr;
    }
    
    /**
     * getReturnOrderById
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getReturnOrderById(DispatchContext dctx, Map<String,? extends Object> context) {
    	Map<String,Object> resultStr = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = ReturnOrderWorker.getReturnOrderById(dctx, jsonObj);
				JSONObject pObjectResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(pObjectResult).toString();
				resultStr.put("jsonOut", strResult);
			}else{
				resultStr.put("jsonOut", "no param");
			}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		return resultStr;
    }
    
    /**
     * getReturnOrderHeaderById
     * @param dctx
     * @param context
     * @return
     */
	public static Map<String,Object> getReturnOrderHeaderById(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String,Object> resultStr = FastMap.newInstance();
        BasePosObject pObject = null;
    	String strResult = "";
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = ReturnOrderWorker.getReturnOrderHeaderById(dctx, jsonObj);
				JSONObject pObjectResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(pObjectResult).toString();
				resultStr.put("jsonOut", strResult);
			}else{
				resultStr.put("jsonOut", "no param");
			}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
        return resultStr;
    }    
    /**
     * getReturnOrderHeaderByCondition
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String,Object> getReturnOrderHeaderByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String,Object> resultStr = FastMap.newInstance();
        BasePosObject pObject = null;
    	String strResult = "";
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = ReturnOrderWorker.getReturnOrderHeaderByCondition(dctx, jsonObj);
				JSONObject pObjectResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(pObjectResult).toString();
				resultStr.put("jsonOut", strResult);
			}else{
				resultStr.put("jsonOut", "no param");
			}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
        return resultStr;
    }    
    /**
     * getReturnOrderDtlById
     * @param dctx
     * @param context
     * @return
     */
	public static Map<String,Object> getReturnOrderDtlById(DispatchContext dctx, Map<String, ? extends Object> context) {
    	 Map<String,Object> resultStr = FastMap.newInstance();
    	 BasePosObject pObject = null;
     	String strResult = "";
     	try{
 	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
 	    	if (UtilValidate.isNotEmpty(jsonStr)) {
 				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
 				pObject = ReturnOrderWorker.getReturnOrderDtlById(dctx, jsonObj);
 				JSONObject pObjectResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(pObjectResult).toString();
 				resultStr.put("jsonOut", strResult);
 			}else{
 				resultStr.put("jsonOut", "no param");
 			}
     	}catch(Exception e){
     		e.printStackTrace();
     	}
        return resultStr;
    }    
    /**
     * getReturnOrderDtlByCondition
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String,Object> getReturnOrderDtlByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
    	 Map<String,Object> resultStr = FastMap.newInstance();
    	 BasePosObject pObject = null;
     	String strResult = "";
     	try{
 	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
 	    	if (UtilValidate.isNotEmpty(jsonStr)) {
 				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
 				pObject = ReturnOrderWorker.getReturnOrderDtlByCondition(dctx, jsonObj);
 				JSONObject pObjectResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(pObjectResult).toString();
 				resultStr.put("jsonOut", strResult);
 			}else{
 				resultStr.put("jsonOut", "no param");
 			}
     	}catch(Exception e){
     		e.printStackTrace();
     	}
        return resultStr;
    }    
    
    /**
     * AddReturnOrderRefundAmount.
     * @param dctx The DispatchContext that this service is operating in.//操作service
     * @param context Map containing the input parameters.//Map 参数集合
     * @return Map with the result of the service, the output parameters.// 返回service 结果
     */
    @SuppressWarnings("unchecked")
	public static Map<String, Object> addReturnOrderRefundAmount(DispatchContext dctx, Map<String, ? extends Object> context) {
    	//结果结合
    	Map<String,Object> resultStr = new HashMap<String,Object>();
    	BasePosObject pObject = null;
      	String strResult = "";
      	try{
  	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
  	    	if (UtilValidate.isNotEmpty(jsonStr)) {
  				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
  				pObject = ReturnOrderWorker.addReturnOrderRefundAmount(dctx, jsonObj);
  				strResult = JSONSerializer.toJSON(pObject).toString();
  				resultStr.put("jsonOut", strResult);
  				if(pObject.getFlag().equals(Constant.OK)){
  					resultStr.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
  				}else{
  					resultStr.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
  				}
  			}else{
  				resultStr.put("jsonOut", "no param");
  			}
      	}catch(Exception e){
      		e.printStackTrace();
      	}
        return resultStr;
    }
    
    /**
     * AddReturnOrderWhsQty.
     * @param dctx The DispatchContext that this service is operating in.//操作service
     * @param context Map containing the input parameters.//Map 参数集合
     * @return Map with the result of the service, the output parameters.// 返回service 结果
     */
	public static Map<String, Object> addReturnOrderWhsQty(DispatchContext dctx, Map<String, ? extends Object> context) {
    	ReturnInWhsOrder returnInWhsOrder = null;
    	Map<String,Object> resultStr = new HashMap<String,Object>();
    	BasePosObject pObject = null;
      	String strResult = "";
      	try{
  	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
  	    	if (UtilValidate.isNotEmpty(jsonStr)) {
  	    		returnInWhsOrder=(ReturnInWhsOrder)JSONUtil.json2Bean(jsonStr, ReturnInWhsOrder.class);
  				pObject = ReturnOrderWorker.addReturnOrderWhsQty(dctx, returnInWhsOrder);
  				strResult = JSONSerializer.toJSON(pObject).toString();
  				resultStr.put("jsonOut", strResult);
  				if(pObject.getFlag().equals(Constant.OK)){
  					resultStr.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
  				}else{
  					resultStr.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
  				}
  			}else{
  				resultStr.put("jsonOut", "no param");
  			}
      	}catch(Exception e){
      		e.printStackTrace();
      	}
        return resultStr;
    }
	 /**
     * createReturnOrderAll.
     * @param dctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> createReturnOrderAll(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> resultStr = FastMap.newInstance();
    	BasePosObject pObject = null;
    	ReturnOrder returnOrder=null;
    	RefundOrder refundOrder=null;
    	ReturnInWhsOrder returnInWhsOrder = null;
    	String strResult = "";
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
	    		 JSONObject jsonObj = JSONObject.fromObject(jsonStr);
	    		 if(UtilValidate.isNotEmpty(jsonObj.get("returnOrder"))){
	    			 returnOrder = (ReturnOrder)JSONUtil.json2Bean(jsonObj.get("returnOrder").toString(),ReturnOrder.class );
	    		  }
	    		  if(UtilValidate.isNotEmpty(jsonObj.get("refundOrder"))){
	    			  refundOrder = (RefundOrder)JSONUtil.json2Bean(jsonObj.get("refundOrder").toString(),RefundOrder.class );
	    		  }
	    		  if(UtilValidate.isNotEmpty(jsonObj.get("returnInWhsOrder"))){
	    			  returnInWhsOrder = (ReturnInWhsOrder)JSONUtil.json2Bean(jsonObj.get("returnInWhsOrder").toString(),ReturnInWhsOrder.class );
	    		  }
	    		  pObject = ReturnOrderWorker.createReturnOrderAll(dctx,returnOrder,refundOrder,returnInWhsOrder);
				  strResult = JSONSerializer.toJSON(pObject).toString();
				  resultStr.put("jsonOut", strResult);
				  if(pObject.getFlag().equals(Constant.OK)){
					resultStr.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				 }else{
					resultStr.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				 }
			}else{
				resultStr.put("jsonOut", "no param");
			}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
        return resultStr;
    }
    
	 /**
     * updateReturnOrderAll.
     * @param dctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> updateReturnOrderAll(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> resultStr = FastMap.newInstance();
    	BasePosObject pObject = null;
    	ReturnOrder returnOrder=null;
    	RefundOrder refundOrder=null;
    	ReturnInWhsOrder returnInWhsOrder = null;
    	String strResult = "";
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
	    		 JSONObject jsonObj = JSONObject.fromObject(jsonStr);
	    		 if(UtilValidate.isNotEmpty(jsonObj.get("returnOrder"))){
	    			 returnOrder = (ReturnOrder)JSONUtil.json2Bean(jsonObj.get("returnOrder").toString(),ReturnOrder.class );
	    		  }
	    		  if(UtilValidate.isNotEmpty(jsonObj.get("refundOrder"))){
	    			  refundOrder = (RefundOrder)JSONUtil.json2Bean(jsonObj.get("refundOrder").toString(),RefundOrder.class );
	    		  }
	    		  if(UtilValidate.isNotEmpty(jsonObj.get("returnInWhsOrder"))){
	    			  returnInWhsOrder = (ReturnInWhsOrder)JSONUtil.json2Bean(jsonObj.get("returnInWhsOrder").toString(),ReturnInWhsOrder.class );
	    		  }
	    		  pObject = ReturnOrderWorker.updateReturnOrderAll(dctx,returnOrder,refundOrder,returnInWhsOrder);
				  strResult = JSONSerializer.toJSON(pObject).toString();
				  resultStr.put("jsonOut", strResult);
				  if(pObject.getFlag().equals(Constant.OK)){
					resultStr.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				  }else{
					 resultStr.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				  }
			}else{
				resultStr.put("jsonOut", "no param");
			}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
        return resultStr;
    }
}
