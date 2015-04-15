package org.ofbiz.querydataforsales;
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

public class QueryDataForSalesServices {
	//根据partyId,storeId查询List<productPriceTypeId>
	 public static Map<String, Object> getProductPriceTypeList(DispatchContext dctx, Map<String,? extends Object> context) {
	    	Map<String,Object> resultStr = FastMap.newInstance();
	    	BasePosObject pObject = null;
	    	Parameter parameter = null;
	    	String strResult = "";
	    	try{
		    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		    	if (UtilValidate.isNotEmpty(jsonStr)) {
		    		parameter = (Parameter)JSONUtil.json2Bean(jsonStr, Parameter.class);
					pObject = QueryDataForSalesWorker.getProductPriceTypeList(dctx,parameter);
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
	 //根据门店ID和移动类型获取仓库ID
	public static Map<String, Object> getFacilityByCond(DispatchContext dctx, Map<String,? extends Object> context){
		Map<String,Object> resultStr = FastMap.newInstance();
    	BasePosObject pObject = null;
    	Product product=null;
    	String strResult = "";
    	try{
	    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    	if (UtilValidate.isNotEmpty(jsonStr)) {
	    		product=(Product)JSONUtil.json2Bean(jsonStr, Product.class);
				pObject = QueryDataForSalesWorker.getFacilityByCond(dctx, product);
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
	 //根据条件查商品
	 public static Map<String, Object> getProductByCond(DispatchContext dctx, Map<String,? extends Object> context) {
	    	Map<String,Object> resultStr = FastMap.newInstance();
	    	BasePosObject pObject = null;
	    	Product product=null;
	    	String strResult = "";
	    	try{
		    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		    	if (UtilValidate.isNotEmpty(jsonStr)) {
		    		product=(Product)JSONUtil.json2Bean(jsonStr, Product.class);
					pObject = QueryDataForSalesWorker.getProductByCond(dctx, product);
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
	 //根据条件查预订商品
	 public static Map<String, Object> getPreOrderProductByCond(DispatchContext dctx, Map<String,? extends Object> context) {
	    	Map<String,Object> resultStr = FastMap.newInstance();
	    	BasePosObject pObject = null;
	    	Product product=null;
	    	String strResult = "";
	    	try{
		    	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		    	if (UtilValidate.isNotEmpty(jsonStr)) {
		    		product=(Product)JSONUtil.json2Bean(jsonStr, Product.class);
					pObject = QueryDataForSalesWorker.getPreOrderProductByCond(dctx, product);
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
	 
	 // 根据销售政策id,门店id获得套包list
	 public static Map<String, Object> getBomByCond(DispatchContext dctx, Map<String,? extends Object> context) {
	    	Map<String,Object> resultStr = FastMap.newInstance();
	    	BasePosObject pObject = null;
	    	SubProduct subProduct = null;
	    	String strResult = "";
	    	try{
	    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    		if (UtilValidate.isNotEmpty(jsonStr)) {
	    			subProduct=(SubProduct)JSONUtil.json2Bean(jsonStr, SubProduct.class);
					pObject = QueryDataForSalesWorker.getBomByCond(dctx, subProduct);
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
	// 套包Id,门店Id获得商品list
	 public static Map<String, Object> getProductByBomCond(DispatchContext dctx, Map<String,? extends Object> context) {
	    	Map<String,Object> resultStr = FastMap.newInstance();
	    	BasePosObject pObject = null;
	    	SubProduct subProduct = null;
	    	String strResult = "";
	    	try{
	    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    		if (UtilValidate.isNotEmpty(jsonStr)) {
	    			subProduct=(SubProduct)JSONUtil.json2Bean(jsonStr, SubProduct.class);
					pObject = QueryDataForSalesWorker.getProductByBomCond(dctx, subProduct);
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
	 //根据门店Id获取支付方式
	 @SuppressWarnings("unchecked")
	public static Map<String, Object> getPaymentMethod(DispatchContext dctx, Map<String,? extends Object> context) {
	    	Map<String,Object> resultStr = FastMap.newInstance();
	    	BasePosObject pObject = null;
	    	String strResult = "";
	    	try{
	    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    		if (UtilValidate.isNotEmpty(jsonStr)) {
					JSONObject jsonObj = JSONObject.fromObject(jsonStr);
					pObject = QueryDataForSalesWorker.getPaymentMethod(dctx, jsonObj);
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
	 //根据门店id，缴款日期获取缴款额
	public static Map<String, Object> getPaymentAmount(DispatchContext dctx, Map<String,? extends Object> context) {
	    	Map<String,Object> resultStr = FastMap.newInstance();
	    	BasePosObject pObject = null;
	    	Parameter parameter = null;
	    	String strResult = "";
	    	try{
	    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    		if (UtilValidate.isNotEmpty(jsonStr)) {
	    			parameter = (Parameter)JSONUtil.json2Bean(jsonStr, Parameter.class);
					pObject = QueryDataForSalesWorker.getPaymentAmount(dctx, parameter);
					JSONObject jsonObjResult = JSONUtil.bean2Json(pObject);
					strResult = JSONSerializer.toJSON(jsonObjResult).toString();
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
	 //根据会员手机号码 查询会员ID 和姓名
	public static Map<String, Object> getMemberByPhoneNo(DispatchContext dctx, Map<String,? extends Object> context) {
	    	Map<String,Object> results = FastMap.newInstance();
	    	BasePosObject pObject = null;
	    	String strResult = "";
	    	try{
		       String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		    	if (UtilValidate.isNotEmpty(jsonStr)) {
					JSONObject jsonObj = JSONObject.fromObject(jsonStr);
				pObject = QueryDataForSalesWorker.getMemberByPhoneNo(dctx, jsonObj);
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
	 	      
	 //　根据销售人员ID 查询销售人员姓名
	 public static Map<String, Object> getSalesPersonBySalesPersonId(DispatchContext dctx, Map<String,? extends Object> context) {
	    	Map<String,Object> results = FastMap.newInstance();
	    	BasePosObject pObject = null;
	    	String strResult = "";
	    	try{
	    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		    	if (UtilValidate.isNotEmpty(jsonStr)) {
					JSONObject jsonObj = JSONObject.fromObject(jsonStr);
		        //把对象传给worker 
				pObject = QueryDataForSalesWorker.getSalesPersonBySalesPersonId(dctx, jsonObj);
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
	 //修改表头备注信息
	public static Map<String, Object> updateHeaderMemo(DispatchContext dctx, Map<String,? extends Object> context) {
	    	Map<String,Object> resultStr = FastMap.newInstance();
	    	BasePosObject pObject = null;
	    	Parameter parameter = null;
	    	String strResult = "";
	    	try{
	    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	    		if (UtilValidate.isNotEmpty(jsonStr)) {
					parameter = (Parameter)JSONUtil.json2Bean(jsonStr, Parameter.class);
					pObject = QueryDataForSalesWorker.updateHeaderMemo(dctx, parameter);
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
	 
	 //修改表明细备注信息
	public static Map<String, Object> updateDetailMemo(DispatchContext dctx, Map<String,? extends Object> context) {
    	Map<String,Object> resultStr = FastMap.newInstance();
    	BasePosObject pObject = null;
    	Parameter parameter = null;
    	String strResult = "";
    	try{
    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
    		if (UtilValidate.isNotEmpty(jsonStr)) {
    			parameter = (Parameter)JSONUtil.json2Bean(jsonStr, Parameter.class);
				pObject = QueryDataForSalesWorker.updateDetailMemo(dctx, parameter);
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
	   
	 //　根据ProductId 查询销售人员姓名
	 public static Map<String, Object> getPersonByProductStoreId(DispatchContext dctx, Map<String,? extends Object> context) {
	    	Map<String,Object> results = FastMap.newInstance();
	    	BasePosObject pObject = null;
	    	String strResult = "";
	    	try{
	    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		    	if (UtilValidate.isNotEmpty(jsonStr)) {
					JSONObject jsonObj = JSONObject.fromObject(jsonStr);
		        //把对象传给worker 
				pObject = QueryDataForSalesWorker.getPersonByProductStoreId(dctx, jsonObj);
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

	// 　根据PriceType 查询销售人员姓名
		public static Map<String, Object> getPersonByPriceType(
				DispatchContext dctx, Map<String, ? extends Object> context) {
			Map<String, Object> results = FastMap.newInstance();
			BasePosObject pObject = null;
			String strResult = "";
			try {
				String jsonStr = context.get("jsonStr") == null ? ""
						: (String) context.get("jsonStr");
				if (UtilValidate.isNotEmpty(jsonStr)) {
					JSONObject jsonObj = JSONObject.fromObject(jsonStr);
					// 把对象传给worker
					pObject = QueryDataForSalesWorker.getPersonByPriceType(dctx,
							jsonObj);
					strResult = JSONSerializer.toJSON(pObject).toString();
					results.put("result", strResult);
					if (pObject.getFlag().equals(Constant.OK)) {
						results.put(ModelService.RESPONSE_MESSAGE,
								ModelService.RESPOND_SUCCESS);
					} else {
						results.put(ModelService.RESPONSE_MESSAGE,
								ModelService.RESPOND_ERROR);
					}
				} else {
					results.put("result", "no param");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return results;
		}
	
}
