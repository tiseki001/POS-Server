package org.ofbiz.order.returninwhsorder;
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

public class ReturnInWhsOrderServices {
	public static final String module = ReturnInWhsOrderServices.class.getName();
	public static final String resource = "PreOrderUiLabels";
    public static final String resourceError = "PreOrderErrorUiLabels";
    /**
     * Creates a ReturnInWhsOrder.
     * @param dctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> createReturnInWhsOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> resultStr = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
    	ReturnInWhsOrder returnInWhsOrder = null;
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
	    		returnInWhsOrder=(ReturnInWhsOrder)JSONUtil.json2Bean(jsonStr, ReturnInWhsOrder.class);
				pObject = ReturnInWhsOrderWorker.createReturnInWhsOrder(dctx, returnInWhsOrder);
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
     * updateReturnInWhsOrderStatus
     * @param ctx
     * @param context
     * @return
     */
    public static Map<String, Object> updateReturnInWhsOrderStatus(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String, Object> resultStr = FastMap.newInstance();//结果集合
    	BasePosObject pObject = null;
    	Parameter parameter = null;
    	String strResult = "";
    	try{
    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				parameter = (Parameter)JSONUtil.json2Bean(jsonStr, Parameter.class);
				pObject = ReturnInWhsOrderWorker.updateReturnInWhsOrderStatus(dctx, parameter);
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
     * updateReturnInWhsOrder
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> updateReturnInWhsOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
    	ReturnInWhsOrder returnInWhsOrder = null;
    	Map<String, Object> resultStr = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
    	try{
    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
	    		returnInWhsOrder=(ReturnInWhsOrder)JSONUtil.json2Bean(jsonStr, ReturnInWhsOrder.class);
				pObject = ReturnInWhsOrderWorker.updateReturnInWhsOrder(dctx, returnInWhsOrder);
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
     * getReturnInWhsOrderById
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> getReturnInWhsOrderById(DispatchContext dctx, Map<String,? extends Object> context) {
    	Map<String,Object> resultStr = FastMap.newInstance();
    	BasePosObject pObject = null;
    	Parameter parameter = null;
    	String strResult = "";
    	try{
    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				parameter = (Parameter)JSONUtil.json2Bean(jsonStr, Parameter.class);
	    		pObject= ReturnInWhsOrderWorker.getReturnInWhsOrderById(dctx, parameter);
				JSONObject jsonObjResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(jsonObjResult).toString();
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
     * getReturnInWhsOrderByCondition
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String,Object> getReturnInWhsOrderByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> resultStr = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
        try {
        	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = ReturnInWhsOrderWorker.getReturnInWhsOrderByCondition(dctx, jsonObj);
				JSONObject jsonObjResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(jsonObjResult).toString();
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
     * getReturnInWhsOrderHeaderByCondition
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String,Object> getReturnInWhsOrderHeaderByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String,Object> resultStr = FastMap.newInstance();
        BasePosObject pObject = null;
        Parameter parameter = null;
    	String strResult = "";
        try {
        	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
	    		parameter = (Parameter)JSONUtil.json2Bean(jsonStr, Parameter.class);
				pObject = ReturnInWhsOrderWorker.getReturnInWhsOrderHeaderByCondition(dctx, parameter);
				JSONObject jsonObjResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(jsonObjResult).toString();
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
     * getReturnInWhsOrderDtlByCondition
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String,Object> getReturnInWhsOrderDtlByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
    	 Map<String,Object> resultStr = FastMap.newInstance();
    	 BasePosObject pObject = null;
    	 Parameter parameter = null;
     	 String strResult = "";
         try {
         	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
 	    	if (UtilValidate.isNotEmpty(jsonStr)) {
 				parameter = (Parameter)JSONUtil.json2Bean(jsonStr, Parameter.class);
 				pObject = ReturnInWhsOrderWorker.getReturnInWhsOrderDtlByCondition(dctx, parameter);
 				JSONObject jsonObjResult = JSONUtil.bean2Json(pObject);
				strResult = JSONSerializer.toJSON(jsonObjResult).toString();
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
