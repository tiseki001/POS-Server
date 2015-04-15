package org.ofbiz.order.saleorder;


import java.util.HashMap;
import java.util.Map;

import javolution.util.FastMap;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.order.collectionorder.CollectionOrder;
import org.ofbiz.order.returnorder.ReturnOrder;
import org.ofbiz.order.salesoutwhsorder.SalesOutWhsOrder;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.util.JSONUtil;

public class SaleOrderService{
	/**
	 * Services for SaleOrder maintenance
	 * service 负责把json串转化成 对象
	 * service 调用 work 方法
	 * work 负责业务逻辑
	 */
	public static final String module = SaleOrderService.class.getName();
	public static final String resource = "SaleOrderUiLabels";
    public static final String resourceError = "SaleOrderErrorUiLabels";

    

    /**
     * CreateSOAll.
     * @param dctx The DispatchContext that this service is operating in.//操作service
     * @param context Map containing the input parameters.//Map 参数集合
     * @return Map with the result of the service, the output parameters.// 返回service 结果
     */
	public static Map<String, Object> createSalesOrderAll(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String,Object> results = FastMap.newInstance();
		BasePosObject pObject = null;	
		SaleOrder saleOrder = null;
		CollectionOrder collectionOrder = null;
		SalesOutWhsOrder salesOutWhsOrder = null;
		String strResult = "";
	      try{  //获取参数jsonStr串
	    	  String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	  if(UtilValidate.isNotEmpty(jsonStr)){
	    		  JSONObject jsonObj = JSONObject.fromObject(jsonStr);
	    		  
	    		  if(UtilValidate.isNotEmpty(jsonObj.get("salesOrder"))){
	    			  saleOrder = (SaleOrder)JSONUtil.json2Bean(jsonObj.get("salesOrder").toString(),SaleOrder.class );
	    		  }
	    		  if(UtilValidate.isNotEmpty(jsonObj.get("collectionOrder"))){
	    			  collectionOrder = (CollectionOrder)JSONUtil.json2Bean(jsonObj.get("collectionOrder").toString(),CollectionOrder.class );
	    		  }
	    		  if(UtilValidate.isNotEmpty(jsonObj.get("salesOutWhsOrder"))){
	    			  salesOutWhsOrder = (SalesOutWhsOrder)JSONUtil.json2Bean(jsonObj.get("salesOutWhsOrder").toString(),SalesOutWhsOrder.class );
	    		  }
	    		  pObject = SaleOrderWork.createSalesOrderAll(dctx,saleOrder,collectionOrder,salesOutWhsOrder);
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
     * updateSalesAll.
     * @param dctx The DispatchContext that this service is operating in.//操作service
     * @param context Map containing the input parameters.//Map 参数集合
     * @return Map with the result of the service, the output parameters.// 返回service 结果
     */
	public static Map<String, Object> updateSalesOrderAll(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String,Object> results = FastMap.newInstance();
		BasePosObject pObject = null;	
		SaleOrder saleOrder = null;
		CollectionOrder collectionOrder = null;
		SalesOutWhsOrder salesOutWhsOrder = null;
		String strResult = "";
	      try{  //获取参数jsonStr串
	    	  String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	  if(UtilValidate.isNotEmpty(jsonStr)){
	    		  JSONObject jsonObj = JSONObject.fromObject(jsonStr);
	    		  if(UtilValidate.isNotEmpty(jsonObj.get("salesOrder"))){
	    			  saleOrder = (SaleOrder)JSONUtil.json2Bean(jsonObj.get("salesOrder").toString(),SaleOrder.class );
	    		  }
	    		  if(UtilValidate.isNotEmpty(jsonObj.get("collectionOrder"))){
	    			  collectionOrder = (CollectionOrder)JSONUtil.json2Bean(jsonObj.get("collectionOrder").toString(),CollectionOrder.class );
	    		  }
	    		  if(UtilValidate.isNotEmpty(jsonObj.get("salesOutWhsOrder"))){
	    			  salesOutWhsOrder = (SalesOutWhsOrder)JSONUtil.json2Bean(jsonObj.get("salesOutWhsOrder").toString(),SalesOutWhsOrder.class );
	    		  }
	    		  pObject = SaleOrderWork.updateSalesOrderAll(dctx,saleOrder,collectionOrder,salesOutWhsOrder);
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
     * Creates a SaleOrder.
     * @param dctx The DispatchContext that this service is operating in.//操作service
     * @param context Map containing the input parameters.//Map 参数集合
     * @return Map with the result of the service, the output parameters.// 返回service 结果
     */
	public static Map<String, Object> createSalesOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String,Object> results = FastMap.newInstance();
		BasePosObject pObject = null;	
		String strResult = "";
	      try{  //获取参数jsonStr串
			String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
			SaleOrder saleorder= null;
			if(UtilValidate.isNotEmpty(jsonStr)){
				saleorder = (SaleOrder)JSONUtil.json2Bean(jsonStr,SaleOrder.class );
				pObject = SaleOrderWork.createSalesOrder(dctx,saleorder);
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
     * updateSaleOrderStatus
     * @param ctx
     * @param context
     * @return
     */
	@SuppressWarnings("unchecked")
    public static Map<String, Object> updateSalesOrderStatus(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> results = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
    	try{
    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
			if(UtilValidate.isNotEmpty(jsonStr)){
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = SaleOrderWork.updateSalesOrderStatus(dctx,jsonObj);
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
     * updateSaleOrder
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> updateSalesOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String,Object> results = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
        try{
           //获取参数jsonStr串
    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
       		SaleOrder saleorder= null;
       		//json2Bean （定义时间类型）
       		saleorder = (SaleOrder)JSONUtil.json2Bean(jsonStr,SaleOrder.class );
       		pObject =SaleOrderWork.updateSalesOrder(dctx, saleorder);
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
     * getSaleOrderById
     * @param dctx
     * @param context
     * @return
     */

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getSalesOrderById(DispatchContext dctx, Map<String,? extends Object> context) {
    	Map<String,Object> results = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
    	try{
    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject=  SaleOrderWork.getSalesOrderById(dctx,jsonObj);
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
     * deleteSaleOrderById
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> deleteSalesOrderById(DispatchContext dctx, Map<String,? extends Object> context) {
    	Map<String,Object> results = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
    	try{
    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = SaleOrderWork.deleteSalesOrderById(dctx, jsonObj);
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
     * getSaleOrderByCondition
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String,Object> getSalesOrderByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> results = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
        try {
        	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = SaleOrderWork.getSalesOrderByCondition(dctx,jsonObj );
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
     * getSalesOrderHeaderByCondition
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String,Object> getSalesOrderHeaderByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String,Object> results = FastMap.newInstance();
    	BasePosObject pObject= null;
    	String strResult = "";
        try {
        	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = SaleOrderWork.getSalesOrderHeaderByCondition(dctx,jsonObj);
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
     * getPreOrderHeaderByID
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
   	public static Map<String,Object> getSalesOrderHeaderById(DispatchContext dctx, Map<String, ? extends Object> context) {
           Map<String,Object> resultStr = FastMap.newInstance();
           BasePosObject pObject = null;
       	String strResult = "";
           try {
           	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
   	    	if (UtilValidate.isNotEmpty(jsonStr)) {
   				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
   				pObject = SaleOrderWork.getSalesOrderHeaderById(dctx, jsonObj);
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
     * getSaleOrderDtlByCondition
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String,Object> getSalesOrderDtlByCondition(DispatchContext dctx, Map<String, ? extends Object> context) {
 Map<String,Object> results = FastMap.newInstance();
 BasePosObject pObject = null;
 String strResult = "";
 try {
 	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
  	if (UtilValidate.isNotEmpty(jsonStr)) {
			JSONObject jsonObj = JSONObject.fromObject(jsonStr);
 	        pObject = SaleOrderWork.getSalesOrderDtlByCondition(dctx,jsonObj);
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
     * getSaleOrderDtlById
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String,Object> getSalesOrderDtlById(DispatchContext dctx, Map<String, ? extends Object> context) {
 	Map<String,Object> results = FastMap.newInstance();
 	BasePosObject pObject = null;
 	 String strResult = "";
 	 try {
 	 	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
 	  	if (UtilValidate.isNotEmpty(jsonStr)) {
 				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
 				  String docId = (String)jsonObj.get("docId");
 				  String where = "DOC_ID='"+docId+"'";
 				  Map<String,Object> view =FastMap.newInstance();
 				  view.put("where",where); 
 				pObject = SaleOrderWork.getSalesOrderDtlByCondition(dctx,view);
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
     * AddSaleOrderWhsQty. SalesOutWhsOrder 调用
     * @param dctx The DispatchContext that this service is operating in.//操作service
     * @param context Map containing the input parameters.//Map 参数集合
     * @return Map with the result of the service, the output parameters.// 返回service 结果
     */
    public static Map<String, Object> addSalesOrderWhsQty(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> results = FastMap.newInstance();
    	BasePosObject pObject = null;
    	 String strResult = "";
    	 try {
    	 	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
    		if(UtilValidate.isNotEmpty(jsonStr)){
			SalesOutWhsOrder salesoutwhsorder = (SalesOutWhsOrder)JSONUtil.json2Bean(jsonStr,SalesOutWhsOrder.class );
				pObject = SaleOrderWork.addSalesOrderWhsQty(dctx, salesoutwhsorder);
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
     * AddSaleOrderRtnQty.ReturnOrder 调用
     * @param dctx The DispatchContext that this service is operating in.//操作service
     * @param context Map containing the input parameters.//Map 参数集合
     * @return Map with the result of the service, the output parameters.// 返回service 结果
     */
    public static Map<String, Object> addSalesOrderRtnQty(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> results = FastMap.newInstance();
    	BasePosObject pObject = null;
    	String strResult = "";
   	 	try {
   	 	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
   	  	if (UtilValidate.isNotEmpty(jsonStr)) {
   				//JSONObject jsonObj = JSONObject.fromObject(jsonStr);
   	  		ReturnOrder returnOrder = (ReturnOrder)JSONUtil.json2Bean(jsonStr,ReturnOrder.class );
   	  		pObject = SaleOrderWork.addSalesOrderRtnQty(dctx,returnOrder);
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
     * AddSaleOrderCollectionAmount.
     * @param dctx The DispatchContext that this service is operating in.//操作service
     * @param context Map containing the input parameters.//Map 参数集合
     * @return Map with the result of the service, the output parameters.// 返回service 结果
     */
    @SuppressWarnings("unchecked")
	public static Map<String, Object> addSalesOrderCollectionAmount(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> results = new HashMap<String,Object>();
    	BasePosObject pObject = null;
    	String strResult = "";
   	 	try {
   	 	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
   	  	if (UtilValidate.isNotEmpty(jsonStr)) {
   				JSONObject jsonObj = JSONObject.fromObject(jsonStr);
	       pObject = SaleOrderWork.addSalesOrderCollectionAmount(dctx,jsonObj);
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
	   

