package org.ofbiz.order.preorder;
import java.util.HashMap;
import java.util.Map;
import javolution.util.FastMap;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.order.backorder.BackOrder;
import org.ofbiz.order.parameter.Parameter;
import org.ofbiz.order.precollectionorder.PreCollectionOrder;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.util.JSONUtil;

public class PreOrderServices {
	/**
	 * Services for PreOrder maintenance
	 */
	public static final String module = PreOrderServices.class.getName();
	public static final String resource = "PreOrderUiLabels";
    public static final String resourceError = "PreOrderErrorUiLabels";
    /**
     * Create a PreOrder.
     * @param dctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> createPreOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> resultStr = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
    	PreOrder preOrder = null;
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
	    		preOrder=(PreOrder)JSONUtil.json2Bean(jsonStr, PreOrder.class);
				pObject = PreOrderWorker.createPreOrder(dctx, preOrder);
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
     * createPreOrderAll
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> createPreOrderAll(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String,Object> results = FastMap.newInstance();
		BasePosObject pObject = null;	
		PreOrder preOrder = null;
		PreCollectionOrder preCollectionOrder = null;
		String strResult = "";
	      try{  //获取参数jsonStr串
	    	  String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	  if(UtilValidate.isNotEmpty(jsonStr)){
	    		  JSONObject jsonObj = JSONObject.fromObject(jsonStr);
	    		  if(UtilValidate.isNotEmpty(jsonObj.get("preOrder"))){
	    			  preOrder = (PreOrder)JSONUtil.json2Bean(jsonObj.get("preOrder").toString(),PreOrder.class );
	    		  }
	    		  if(UtilValidate.isNotEmpty(jsonObj.get("preCollectionOrder"))){
	    			  preCollectionOrder = (PreCollectionOrder)JSONUtil.json2Bean(jsonObj.get("preCollectionOrder").toString(),PreCollectionOrder.class );
	    		  }
	    		  pObject = PreOrderWorker.createSalesOrderAll(dctx,preOrder,preCollectionOrder);
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
     * updatePreOrderAll
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> updatePreOrderAll(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String,Object> results = FastMap.newInstance();
		BasePosObject pObject = null;	
		PreOrder preOrder = null;
		PreCollectionOrder preCollectionOrder = null;
		String strResult = "";
	      try{  //获取参数jsonStr串
	    	  String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	  if(UtilValidate.isNotEmpty(jsonStr)){
	    		  JSONObject jsonObj = JSONObject.fromObject(jsonStr);
	    		  if(UtilValidate.isNotEmpty(jsonObj.get("preOrder"))){
	    			  preOrder = (PreOrder)JSONUtil.json2Bean(jsonObj.get("preOrder").toString(),PreOrder.class );
	    		  }
	    		  if(UtilValidate.isNotEmpty(jsonObj.get("preCollectionOrder"))){
	    			  preCollectionOrder = (PreCollectionOrder)JSONUtil.json2Bean(jsonObj.get("collectionOrder").toString(),PreCollectionOrder.class );
	    		  }
	    		  pObject = PreOrderWorker.updatePreOrderAll(dctx,preOrder,preCollectionOrder);
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
     * updatePreOrderStatus
     * @param ctx
     * @param context
     * @return
     */
    public static Map<String, Object> updatePreOrderStatus(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String, Object> resultStr = FastMap.newInstance();
    	BasePosObject pObject = null;
    	Parameter parameter = null;
    	String strResult = "";
    	try{
    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				parameter = (Parameter)JSONUtil.json2Bean(jsonStr, Parameter.class);
				pObject = PreOrderWorker.updatePreOrderStatus(dctx, parameter);
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
     * updatePreOrder
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> updatePreOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
    	PreOrder preOrder=null;
    	Map<String, Object> resultStr = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
    	try{
    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
	    		preOrder=(PreOrder)JSONUtil.json2Bean(jsonStr, PreOrder.class);
				pObject = PreOrderWorker.updatePreOrder(dctx, preOrder);
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
     * getPreOrderById
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getPreOrderById(DispatchContext dctx, Map<String,? extends Object> context) {
    	Map<String,Object> resultStr = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
    	try{
    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = PreOrderWorker.getPreOrderById(dctx, jsonObj);
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
     * deletePreOrderById
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String, Object> deletePreOrderById(DispatchContext dctx, Map<String,? extends Object> context) {
    	Map<String,Object> resultStr = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
    	try{
    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = PreOrderWorker.deletePreOrderById(dctx, jsonObj);
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
     * getPreOrderHeaderByCondition
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String,Object> getPreOrderHeaderByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String,Object> resultStr = FastMap.newInstance();
        BasePosObject pObject = null;
    	String strResult = "";
        try {
        	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = PreOrderWorker.getPreOrderHeaderByCondition(dctx, jsonObj);
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
     * getPreOrderHeaderByID
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
   	public static Map<String,Object> getPreOrderHeaderById(DispatchContext dctx, Map<String, ? extends Object> context) {
           Map<String,Object> resultStr = FastMap.newInstance();
           BasePosObject pObject = null;
       	String strResult = "";
           try {
           	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
   	    	if (UtilValidate.isNotEmpty(jsonStr)) {
   				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
   				pObject = PreOrderWorker.getPreOrderHeaderById(dctx, jsonObj);
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
     * getPreOrderDtlByCondition
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String,Object> getPreOrderDtlByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
    	 Map<String,Object> resultStr = FastMap.newInstance();
    	 BasePosObject pObject = null;
     	 String strResult = "";
         try {
         	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
 	    	if (UtilValidate.isNotEmpty(jsonStr)) {
 				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
 				pObject = PreOrderWorker.getPreOrderDtlByCondition(dctx, jsonObj);
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
     * getPreOrderDtlById
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String,Object> getPreOrderDtlById(DispatchContext dctx, Map<String, ? extends Object> context) {
   	 Map<String,Object> resultStr = FastMap.newInstance();
   	 BasePosObject pObject = null;
 	 String strResult = "";
     try { 
    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
    	if (UtilValidate.isNotEmpty(jsonStr)) {
			JSONObject jsonObj = JSONObject.fromObject(jsonStr);
			pObject = PreOrderWorker.getPreOrderDtlById(dctx, jsonObj);
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
     * AddPreOrderRtnQt.
     * @param dctx The DispatchContext that this service is operating in.//操作service
     * @param context Map containing the input parameters.//Map 参数集合
     * @return Map with the result of the service, the output parameters.// 返回service 结果
     */
	public static Map<String, Object> addPreOrderRtnQt(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> resultStr = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
        try{ 
        	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
        	if (UtilValidate.isNotEmpty(jsonStr)){
        		BackOrder backOrder = (BackOrder)JSONUtil.json2Bean(jsonStr,BackOrder.class );
    			pObject = PreOrderWorker.addPreOrderRtnQt(dctx, backOrder);
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
     * AddPreOrderCollectionAmount.
     * @param dctx The DispatchContext that this service is operating in.//操作service
     * @param context Map containing the input parameters.//Map 参数集合
     * @return Map with the result of the service, the output parameters.// 返回service 结果
     */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> addPreOrderCollectionAmount(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> resultStr = new HashMap<String,Object>();
    	BasePosObject pObject = null;
    	String strResult = "";
        try{ 
        	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
        	if (UtilValidate.isNotEmpty(jsonStr)){
        		JSONObject jsonObj = JSONObject.fromObject(jsonStr);
        		pObject = PreOrderWorker.addPreOrderCollectionAmount(dctx,jsonObj);
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

