package org.ofbiz.order.backorder;
import java.util.HashMap;
import java.util.Map;

import javolution.util.FastMap;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.order.backorder.BackOrder;
import org.ofbiz.order.backorder.BackOrderWorker;
import org.ofbiz.order.parameter.Parameter;
import org.ofbiz.order.prerefundorder.PreRefundOrder;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.util.JSONUtil;

public class BackOrderServices {
	/**
	 * Services for BackOrder maintenance
	 */
	public static final String module = BackOrderServices.class.getName();
	public static final String resource = "BackOrderUiLabels";
    public static final String resourceError = "BackOrderErrorUiLabels";
    /**
     * Creates a BackOrder.
     * @param dctx The DispatchContext that this service is operating in.//操作service
     * @param context Map containing the input parameters.//Map 参数集合
     * @return Map with the result of the service, the output parameters.// 返回service 结果
     */
    public static Map<String, Object> createBackOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> resultStr = FastMap.newInstance();
    	BackOrder backOrder=null;
    	BasePosObject pObject = null;
    	String strResult = "";
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
	    		backOrder=(BackOrder)JSONUtil.json2Bean(jsonStr, BackOrder.class);
				pObject = BackOrderWorker.createBackOrder(dctx, backOrder);
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
     * createBackOrderAll
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> createBackOrderAll(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String,Object> results = FastMap.newInstance();
		BasePosObject pObject = null;	
		BackOrder backOrder = null;
		PreRefundOrder preRefundOrder = null;
		String strResult = "";
	      try{  //获取参数jsonStr串
	    	  String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	  if(UtilValidate.isNotEmpty(jsonStr)){
	    		  JSONObject jsonObj = JSONObject.fromObject(jsonStr);
	    		  
	    		  if(UtilValidate.isNotEmpty(jsonObj.get("backOrder"))){
	    			  backOrder = (BackOrder)JSONUtil.json2Bean(jsonObj.get("backOrder").toString(),BackOrder.class );
	    		  }
	    		  if(UtilValidate.isNotEmpty(jsonObj.get("preRefundOrder"))){
	    			  preRefundOrder = (PreRefundOrder)JSONUtil.json2Bean(jsonObj.get("preRefundOrder").toString(),PreRefundOrder.class );
	    		  }
	    		  pObject = BackOrderWorker.createBackOrderAll(dctx,backOrder,preRefundOrder);
				  strResult = JSONSerializer.toJSON(pObject).toString();
				  results.put("jsonOut",strResult);
				  if(!pObject.getFlag().equals(Constant.OK)){
					  results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			      }else{
			    	  results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
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
     * updateBackOrderAll
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> updateBackOrderAll(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String,Object> results = FastMap.newInstance();
		BasePosObject pObject = null;	
		BackOrder backOrder = null;
		PreRefundOrder preRefundOrder = null;
		String strResult = "";
	      try{  //获取参数jsonStr串
	    	  String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	  if(UtilValidate.isNotEmpty(jsonStr)){
	    		  JSONObject jsonObj = JSONObject.fromObject(jsonStr);
	    		  
	    		  if(UtilValidate.isNotEmpty(jsonObj.get("backOrder"))){
	    			  backOrder = (BackOrder)JSONUtil.json2Bean(jsonObj.get("backOrder").toString(),BackOrder.class );
	    		  }
	    		  if(UtilValidate.isNotEmpty(jsonObj.get("preRefundOrder"))){
	    			  preRefundOrder = (PreRefundOrder)JSONUtil.json2Bean(jsonObj.get("preRefundOrder").toString(),PreRefundOrder.class );
	    		  }
	    		  pObject = BackOrderWorker.updateBackOrderAll(dctx,backOrder,preRefundOrder);
				  strResult = JSONSerializer.toJSON(pObject).toString();
				  results.put("jsonOut",strResult);
				  if(!pObject.getFlag().equals(Constant.OK)){
					  results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			      }else{
			    	  results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
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
     * updateBackOrderStatus
     * @param ctx
     * @param context
     * @return
     */
    public static Map<String, Object> updateBackOrderStatus(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String, Object> resultStr = FastMap.newInstance();//结果集合
    	BasePosObject pObject = null;
    	String strResult = "";
    	Parameter parameter = null;
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				parameter=(Parameter)JSONUtil.json2Bean(jsonStr, Parameter.class);
				pObject = BackOrderWorker.updateBackOrderStatus(dctx, parameter);
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
     * updateBackOrder
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> updateBackOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String, Object> resultStr = FastMap.newInstance();
    	BackOrder backOrder=null;
    	BasePosObject pObject = null;
    	String strResult = "";
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
	    		backOrder=(BackOrder)JSONUtil.json2Bean(jsonStr, BackOrder.class);
				pObject = BackOrderWorker.updateBackOrder(dctx, backOrder);
				JSONObject pObjectResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(pObjectResult).toString();
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
     * getBackOrderById
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getBackOrderById(DispatchContext dctx, Map<String,? extends Object> context) {
    	Map<String,Object> resultStr = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = BackOrderWorker.getBackOrderById(dctx, jsonObj);
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
     * getBackOrderHeaderById
     * @param dctx
     * @param context
     * @return
     */
	public static Map<String,Object> getBackOrderHeaderById(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String,Object> resultStr = FastMap.newInstance();
        BasePosObject pObject = null;
    	String strResult = "";
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = BackOrderWorker.getBackOrderHeaderById(dctx, jsonObj);
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
     * getBackOrderHeaderByCondition
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String,Object> getBackOrderHeaderByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String,Object> resultStr = FastMap.newInstance();
        BasePosObject pObject = null;
    	String strResult = "";
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = BackOrderWorker.getBackOrderHeaderByCondition(dctx, jsonObj);
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
     * getBackOrderDtlById
     * @param dctx
     * @param context
     * @return
     */
	public static Map<String,Object> getBackOrderDtlById(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String,Object> resultStr = FastMap.newInstance();
        BasePosObject pObject = null;
    	String strResult = "";
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = BackOrderWorker.getBackOrderDtlById(dctx, jsonObj);
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
     * getBackOrderDtlByCondition
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String,Object> getBackOrderDtlByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> resultStr = FastMap.newInstance();
    	BasePosObject pObject = null;
     	String strResult = "";
     	try{
 	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
 	    	if (UtilValidate.isNotEmpty(jsonStr)) {
 				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
 				pObject = BackOrderWorker.getBackOrderDtlByCondition(dctx, jsonObj);
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
     * AddBackOrderPreRefundAmount.
     * @param dctx The DispatchContext that this service is operating in.//操作service
     * @param context Map containing the input parameters.//Map 参数集合
     * @return Map with the result of the service, the output parameters.// 返回service 结果
     */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> addBackOrderPreRefundAmount(DispatchContext dctx, Map<String, ? extends Object> context) {
    	//结果结合
    	Map<String,Object> resultStr = new HashMap<String,Object>();
    	BasePosObject pObject = null;
     	String strResult = "";
     	try{
 	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
 	    	if (UtilValidate.isNotEmpty(jsonStr)) {
 				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
 				pObject = BackOrderWorker.addBackOrderPreRefundAmount(dctx, jsonObj);
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
