package org.ofbiz.delivery.webService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.util.JSONUtil;

public class DeliveryWebService {
	public static final String module = DeliveryWebService.class.getName();
	public static final String resource = "DeliveryUiLabels";
	public static final String resourceError = "DeliveryErrorUiLabels";
	/*
	 * 查询固件类型
	 */
	public static Map<String, Object> findFaultType(DispatchContext dctx,Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = DeliveryWebWork.findFaultType(dctx);
		String strResult = JSONUtil.bean2Json(pObject).toString();
		resultJson.put("jsonOut", strResult);
		return resultJson;
	}
	/*
	 * 获取头命令
	 */
	public static Map<String, Object> findDeliveryCommand(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			pObject = DeliveryWebWork.findDeliveryCommand(jsonObj, dctx);
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		}else{
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}
	/*
	 * 发货单查询
	 */
	public static Map<String, Object> findDeliveryDoc(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			pObject = DeliveryWebWork.findDeliveryDoc(jsonObj, dctx);
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		}else{
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}
	/*
	 * 获取明细
	 */
	public static Map<String, Object> findDeliveryItemcommand(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			pObject = DeliveryWebWork.findDeliveryItemcommand(jsonObj, dctx);
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		}else{
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}
	/*
	 * 获取状态
	 */
	public static Map<String, Object> findDllConstant(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			pObject = DeliveryWebWork.findDllConstant(jsonObj, dctx);
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		}else{
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}
	/*
	 * 发货单明细
	 */
	public static Map<String, Object> findDeliveryItem(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			pObject = DeliveryWebWork.findDeliveryItem(jsonObj, dctx);
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		}else{
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}
	/*
	 * 获取移动数据
	 */
	public static Map<String,Object> findMovementWeb(DispatchContext dctx,Map<String, ? extends Object> context){
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			pObject = DeliveryWebWork.findMovementType(dctx,jsonObj);
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		}else{
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}
	/*
	 * 根据移动数据和店面查询仓库
	 */
	public static Map<String,Object> findFacility(DispatchContext dctx,Map<String, ? extends Object> context){
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			pObject = DeliveryWebWork.findFacility(dctx,jsonObj);
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		}else{
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}
	/*
	 * 删除指令头
	 */
	public static Map<String, Object> deleteDeliveryCommand(DispatchContext dctx,Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			
			pObject = DeliveryWebWork.deleteDeliveryCommand(jsonObj, dctx);
			if(pObject.getFlag().equals(Constant.NG)){
				resultJson.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			}
			strResult = JSONSerializer.toJSON(pObject).toString();
			resultJson.put("jsonOut", strResult);
		}else{
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}
	
	/*
	 * 创建发货单
	 */
	public static Map<String,Object> allocateDelivery(DispatchContext dctx,Map<String, ? extends Object> context){
    	
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			JSONObject jsonObj = JSONUtil.bean2Json(view);
			Map<String,Object> jsonHeader = JSONUtil.json2Map(jsonObj.get("header").toString());
			
			JSONArray listItem= JSONArray.fromObject(jsonObj.get("item"));
			List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
			for (int i=0;i<listItem.size();i++) {
				Map<String,Object> map = JSONUtil.json2Map(listItem.get(i).toString());
				itemList.add(map);
			}
			
			pObject = DeliveryWebWork.allocateDelivery(jsonHeader,itemList, dctx);
			if(pObject.getFlag().equals(Constant.NG)){
				resultJson.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			}
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		}else{
			
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}
	/*
	 * 修改发货单
	 */
	public static Map<String,Object> updateDelivery(DispatchContext dctx,Map<String, ? extends Object> context){
    	
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			JSONObject jsonObj = JSONUtil.bean2Json(view);
			Map<String,Object> jsonHeader = JSONUtil.json2Map(jsonObj.get("header").toString());
			
			JSONArray listItem= JSONArray.fromObject(jsonObj.get("item"));
			List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
			for (int i=0;i<listItem.size();i++) {
				Map<String,Object> map = JSONUtil.json2Map(listItem.get(i).toString());
				itemList.add(map);
			}
			
			pObject = DeliveryWebWork.updateDelivery(jsonHeader,itemList, dctx);
			if(pObject.getFlag().equals(Constant.NG)){
				resultJson.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			}
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		}else{
			
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}
	
	/*
	 * 修改打印次数
	 */
	public static Map<String, Object> updateDeliveryPrint(DispatchContext dctx,Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			pObject = DeliveryWebWork.updateDeliveryPrint(jsonObj, dctx);
			if(pObject.getFlag().equals(Constant.NG)){
				resultJson.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			}
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		}else{
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}
	/*
	 * 强制修改指令状态
	 */
	public static Map<String, Object> updateDeliveryCommandStatus(DispatchContext dctx,Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			pObject = DeliveryWebWork.updateDeliverycommandStatus(jsonObj, dctx);
			if(pObject.getFlag().equals(Constant.NG)){
				resultJson.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			}
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		}else{
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}
	
}
