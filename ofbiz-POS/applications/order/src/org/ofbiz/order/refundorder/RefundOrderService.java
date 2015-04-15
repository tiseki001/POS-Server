package org.ofbiz.order.refundorder;

import java.util.Map;

import javolution.util.FastMap;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.order.parameter.Parameter;
import org.ofbiz.order.returnorder.ReturnOrderWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.util.JSONUtil;

public class RefundOrderService {

	public static final String module = RefundOrderService.class.getName();
	public static final String resource = "RefundOrderUiLabels";
    public static final String resourceError = "RefundOrderErrorUiLabels";
    /**
     * createRefundOrder
     * @param ctx
     * @param context
     * @return
     */

  
    public static Map<String, Object> createRefundOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String,Object> results = FastMap.newInstance();
		BasePosObject pObject = null;	
		String strResult = "";
	      try{  //获取参数jsonStr串
			String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
			RefundOrder refundorder= null;
			if(UtilValidate.isNotEmpty(jsonStr)){
				refundorder = (RefundOrder)JSONUtil.json2Bean(jsonStr,RefundOrder.class );
				pObject = RefundOrderWork.createRefundOrder(dctx,refundorder);
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
     * updateRefundOrderStatus
     * @param ctx
     * @param context
     * @return
     */
    public static Map<String, Object> updateRefundOrderStatus(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String, Object> results = FastMap.newInstance();//结果集合
    	BasePosObject pObject = null;
    	Parameter parameter = null;
    	String strResult = "";
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				parameter=(Parameter)JSONUtil.json2Bean(jsonStr, Parameter.class);
				pObject = RefundOrderWork.updateRefundOrderStatus(dctx, parameter);
				strResult = JSONSerializer.toJSON(pObject).toString();
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
     * updateRefundOrder
     */
    public static Map<String, Object> updateRefundOrder(DispatchContext dctx, Map<String, ? extends Object> context) {

    	Map<String, Object> results = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				//JSONObject jsonObj = JSONObject.fromObject(jsonStr);
	    		RefundOrder refundOrder=(RefundOrder)JSONUtil.json2Bean(jsonStr, RefundOrder.class);
				pObject = RefundOrderWork.updateRefundOrder(dctx, refundOrder);
				strResult = JSONSerializer.toJSON(pObject).toString();
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
     * getReturnOrderHeaderByCondition
     * @param dctx
     * @param context
     * @return
     */
	public static Map<String,Object> getRefundOrderHeaderByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String,Object> results = FastMap.newInstance();
        BasePosObject pObject = null;
    	String strResult = "";
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = RefundOrderWork.getRefundOrderHeaderByCondition(dctx, jsonObj);
				JSONObject pObjectResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(pObjectResult).toString();
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
     * getReturnOrderDtlByCondition
     * @param dctx
     * @param context
     * @return
     */

	public static Map<String,Object> getRefundOrderDtlByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
    	 Map<String,Object> results = FastMap.newInstance();
    	 BasePosObject pObject = null;
     	String strResult = "";
     	try{
 	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
 	    	if (UtilValidate.isNotEmpty(jsonStr)) {
 				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
 				pObject = RefundOrderWork.getRefundOrderDtlByCondition(dctx, jsonObj);
 				JSONObject pObjectResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(pObjectResult).toString();
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
     * getRefundOrderById
     * @param dctx
     * @param context
     * @return
     */
 
	public static Map<String, Object> getRefundOrderById(DispatchContext dctx, Map<String,? extends Object> context) {
    	Map<String,Object> resultStr = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = RefundOrderWork.getRefundOrderById(dctx, jsonObj);
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
     * getReturnOrderByCondition
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String,Object> getRefundOrderByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> resultStr = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = RefundOrderWork.getRefundOrderByCondition(dctx, jsonObj);
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
}
