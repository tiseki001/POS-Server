package org.ofbiz.inventory.webService;

import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.util.JSONUtil;


/**
 * @author TotyuNote005
 *
 */
public class InventoryItemWebService {
	public static final String module = InventoryItemWebService.class.getName();
    public static final String resource = "InventoryItemUiLabels";
    public static final String resourceError = "InventoryItemErrorUiLabels";
    
    /*
     * Analytical parameters
     * 模糊查询
     */
	public static Map<String,Object> analyticalParameters(DispatchContext dctx, Map<String, ? extends Object> context){
    	Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			pObject = InventoryWebWork.operatingparameters(dctx, jsonObj);
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		}else{
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
    }
    /*
     * Analytical parameters
     * 精确查询
     */
	public static Map<String,Object> findInventoryItem(DispatchContext dctx, Map<String, ? extends Object> context){
    	Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			pObject = InventoryWebWork.findInventoreItem(dctx, jsonObj);
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		}else{
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
    }
    /*
	 * 修改库存数
	 */
	public static Map<String,Object> updateInventorynItemToOnhead(DispatchContext dctx,Map<String, ? extends Object> context){
    	
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		pObject = InventoryWebWork.updateInventorynItemToOnhead((GenericValue) context.get("inventoryn"),dctx);
		if(pObject.getFlag().equals(Constant.NG)){
			resultJson.put(ModelService.RESPONSE_MESSAGE, pObject.getMsg());
			return ServiceUtil.returnError(pObject.getMsg());
		}
		return resultJson;
	}
	/*
	 * 修改库承诺数
	 */
	public static Map<String,Object> updateInventorynItemToPromise(DispatchContext dctx,Map<String, ? extends Object> context){
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		pObject = InventoryWebWork.updateInventorynItemToPromise((GenericValue) context.get("inventoryn"),dctx);
		if(pObject.getFlag().equals(Constant.NG)){
			resultJson.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			
		}
		return resultJson;
	}
	/*
     * Analytical parameters
     * 搜索帮助，模糊查询
     */
	public static Map<String,Object> findProductInventory(DispatchContext dctx, Map<String, ? extends Object> context){
    	Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			pObject = InventoryWebWork.findProductSeq(dctx, jsonObj);
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		}else{
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
    }
	
	/*
     * 
     * 搜索帮助，模糊查询2 不分仓库且不包括在途仓
     */
	public static Map<String,Object> findProductInventoryGroup(DispatchContext dctx, Map<String, ? extends Object> context){
    	Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			pObject = InventoryWebWork.findProductSeqGroup(dctx, jsonObj);
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		}else{
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
    }
	/**
	 * create DeliveryItemcommand 触发此方法 
	 * 门店串号锁定 isOccupied = "Y"
	 * eecas lockProductSequence 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> lockProductSequence(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		GenericValue gv = (GenericValue) context.get("deliveryItemcommand");
		// 门店
		String strServerType = System.getProperty("posstore.type");
    	if (strServerType.equals("1")){
    		InventoryWebWork.lockProductSeq(delegator, gv.get("productId").toString(), 
    				(String) gv.get("sequenceId"));
    	}
		return ServiceUtil.returnSuccess();
	}
	
	/**
	 * create ReceiveItem 触发此方法 
	 * 门店串号仓库修改 facility_id = receiveItem.facilityId
	 * eecas updateProductSequence 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> updateProductSequence(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		GenericValue gv = (GenericValue) context.get("receiveItem");
		// 门店
		String strServerType = System.getProperty("posstore.type");
    	if (strServerType.equals("1")){
    		InventoryWebWork.updateProductSeq(delegator, gv.get("productId").toString(), 
    				gv.get("sequenceId").toString(), gv.get("facilityId").toString());
    	}
		return ServiceUtil.returnSuccess();
	}
	
	 /**
	  * 查询销售区域门店信息
	  * @param dctx
	  * @param context
	  * @return
	  */
     
	public static Map<String,Object> findAreaStore(DispatchContext dctx, Map<String, ? extends Object> context){
    	Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			pObject = InventoryWebWork.findAreaStore(dctx, jsonObj);
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		}else{
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
    }
}