package org.ofbiz.order.salesoutwhsorder;

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

public class SalesOutWhsOrderService {
	public static final String module = SalesOutWhsOrderService.class.getName();
	public static final String resource = "SaleOutWhsOrderUiLabels";
    public static final String resourceError = "SaleOutWhsOrderErrorUiLabels";

	 /**
     * Creates a SaleOutWhsOrder.
     * @param dctx The DispatchContext that this service is operating in.//操作service
     * @param context Map containing the input parameters.//Map 参数集合
     * @return Map with the result of the service, the output parameters.// 返回service 结果
     */
	public static Map<String, Object> createSalesOutWhsOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String,Object> results = FastMap.newInstance();
		BasePosObject pObject = null;	
		String strResult = "";
	    try{
	        		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	        		if (UtilValidate.isNotEmpty(jsonStr)) {   			
	    			SalesOutWhsOrder salesoutwhsorder = (SalesOutWhsOrder)JSONUtil.json2Bean(jsonStr,SalesOutWhsOrder.class );
	        		pObject = SalesOutWhsOrderWork.createSalesOutWhsOrder(dctx, salesoutwhsorder);
	        		  strResult = JSONSerializer.toJSON(pObject).toString();
	        	        results.put("result",strResult);
	        		 //根据返回值输出message success or fail
	        	        if(pObject.getFlag().equals(Constant.NG)){
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
     * updateCollectionOrderStatus
     * @param ctx
     * @param context
     * @return
     */
	public static Map<String, Object> updateSalesOutWhsOrderStatus(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String,Object> results = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
    	Parameter parameter = null;
		try{
    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
    		if (UtilValidate.isNotEmpty(jsonStr)) {
				parameter = (Parameter)JSONUtil.json2Bean(jsonStr, Parameter.class);
    		pObject= SalesOutWhsOrderWork.updateSalesOutWhsOrderStatus(dctx, parameter);
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
     * upSalesOutWhsOrder
     * @param ctx
     * @param context
     * @return
     */
    public static Map<String, Object> updateSalesOutWhsOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String,Object> results = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
        try{
           //获取参数jsonStr串
    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
       		SalesOutWhsOrder salesoutwhsorder= null;
       		//json2Bean （定义时间类型）
       		salesoutwhsorder = (SalesOutWhsOrder)JSONUtil.json2Bean(jsonStr,SalesOutWhsOrder.class );
       		pObject =SalesOutWhsOrderWork.updateSalesOutWhsOrder(dctx, salesoutwhsorder);
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
     * getSalesOutWhsOrderHeaderByCondition
     * @param ctx
     * @param context
     * @return
     */
	public static Map<String, Object> getSalesOutWhsOrderHeaderByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String,Object> results = FastMap.newInstance();
		BasePosObject pObject = null;
		Parameter parameter = null;
		String strResult = "";
    	try{
    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
    		if (UtilValidate.isNotEmpty(jsonStr)) {
    		parameter = (Parameter)JSONUtil.json2Bean(jsonStr, Parameter.class);
    		pObject= SalesOutWhsOrderWork.getSalesOutWhsOrderHeaderByCondition(dctx, parameter);
    		JSONObject jsonObjResult = JSONUtil.bean2Json(pObject);
    		strResult = JSONSerializer.toJSON(jsonObjResult).toString();
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
    * getSalesOutWhsOrderDtlByCondition
    * @param ctx
    * @param context
    * @return
    */
	public static Map<String, Object> getSalesOutWhsOrderDtlByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String,Object> results = FastMap.newInstance();
		BasePosObject pObject = null;
		Parameter parameter = null;
		String strResult = "";
   	try{
   		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
   		if (UtilValidate.isNotEmpty(jsonStr)) {
   		parameter = (Parameter)JSONUtil.json2Bean(jsonStr, Parameter.class);
   		pObject= SalesOutWhsOrderWork.getSalesOutWhsOrderDtlByCondition(dctx, parameter);
   		JSONObject jsonObjResult = JSONUtil.bean2Json(pObject);
   		strResult = JSONSerializer.toJSON(jsonObjResult).toString();
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
     * getSaleOrderById
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> getSalesOutWhsOrderById(DispatchContext dctx, Map<String,? extends Object> context) {
    	Map<String,Object> results = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
    	Parameter parameter = null;
    	try{
    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
	    		parameter = (Parameter)JSONUtil.json2Bean(jsonStr, Parameter.class);
	    		pObject= SalesOutWhsOrderWork.getSalesOutWhsOrderById(dctx, parameter);
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
     * getSaleOrderByCondition
     * @param dctx
     * @param context
     * @return
     */
	public static Map<String,Object> getSalesOutWhsOrderByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> results = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
        try {
        	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
	    		//JSONObject jsonObj = JSONObject.fromObject(jsonStr);	    		
	    	Parameter	parameter = (Parameter)JSONUtil.json2Bean(jsonStr, Parameter.class);
	    		pObject= SalesOutWhsOrderWork.getSalesOutWhsOrderByCondition(dctx,parameter);
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
     * getSalesOutWhsOrderHeaderByID
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String,Object> getSalesOutWhsOrderHeaderById(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String,Object> results = FastMap.newInstance();
       BasePosObject pObject = null;
       String strResult = "";
       try {
       	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
	    		Parameter parameter = (Parameter)JSONUtil.json2Bean(jsonStr, Parameter.class);
				pObject = SalesOutWhsOrderWork.getSalesOutWhsOrderHeaderByCondition(dctx, parameter);
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
     * getSalesOutWhsOrderHeaderByID
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String,Object> getSalesOutWhsOrderDtlById(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String,Object> results = FastMap.newInstance();
       BasePosObject pObject = null;
       String strResult = "";
       try {
       	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
	    		Parameter parameter = (Parameter)JSONUtil.json2Bean(jsonStr, Parameter.class);
	       		pObject= SalesOutWhsOrderWork.getSalesOutWhsOrderDtlByCondition(dctx, parameter);
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

