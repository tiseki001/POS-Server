package org.ofbiz.inventoryDetail.webService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.util.JSONUtil;

public class InventoryDetailWebService {
	public static final String module = InventoryDetailWebService.class.getName();
	/*
	 * 查询盘点头信息
	 */
	public static Map<String, Object> findInventory(DispatchContext dctx,Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" :(String)context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String, Object> mapIn = JSONUtil.json2Map(view);
			pObject = InventoryDetailWebWork.findInventory(dctx, mapIn);
			strResult = JSONUtil.bean2Json(pObject).toString();
		}else{
			resultJson.put("jsonOut", "no param");
		}
		resultJson.put("jsonOut", strResult);
		return resultJson;
	}
	/*
	 * 查询盘明细信息
	 */
	public static Map<String, Object> findInventoryDetail(DispatchContext dctx,Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" :(String)context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String, Object> mapIn = JSONUtil.json2Map(view);
			pObject = InventoryDetailWebWork.findInventoryDetail(dctx, mapIn);
			strResult = JSONUtil.bean2Json(pObject).toString();
		}else{
			resultJson.put("jsonOut", "no param");
		}
		resultJson.put("jsonOut", strResult);
		return resultJson;
	}
	/*
	 * 创建盘点信息
	 */
	public static Map<String, Object> createInventory(DispatchContext dctx,Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			JSONObject jsonObj = JSONUtil.bean2Json(view);
			Map<String, Object> mapHeader = JSONUtil.json2Map(jsonObj.get("header").toString());

			JSONArray listItem = JSONArray.fromObject(jsonObj.get("item"));
			List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < listItem.size(); i++) {
				Map<String, Object> map = JSONUtil.json2Map(listItem.get(i).toString());
				itemList.add(map);
			}

			pObject = InventoryDetailWebWork.createInventory(mapHeader,itemList,dctx);
			if (pObject.getFlag().equals(Constant.NG)) {
				resultJson.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_ERROR);
			}
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		} else {
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}
	/*
	 * 修改盘点信息
	 */
	public static Map<String, Object> updateInventory(DispatchContext dctx,Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			JSONObject jsonObj = JSONUtil.bean2Json(view);
			Map<String, Object> mapHeader = JSONUtil.json2Map(jsonObj.get("header").toString());

			JSONArray listItem = JSONArray.fromObject(jsonObj.get("item"));
			List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < listItem.size(); i++) {
				Map<String, Object> map = JSONUtil.json2Map(listItem.get(i).toString());
				itemList.add(map);
			}

			pObject = InventoryDetailWebWork.updateInventory(mapHeader,itemList,dctx);
			if (pObject.getFlag().equals(Constant.NG)) {
				resultJson.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_ERROR);
			}
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		} else {
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}
	/*
	 * 删除盘点
	 */
	public static Map<String, Object> deleteInventory(DispatchContext dctx,Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			
			pObject = InventoryDetailWebWork.deleteInventory(jsonObj, dctx);
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
	 * 查询盘点指令
	 */
	public static Map<String, Object> findInventoryCommand(DispatchContext dctx,Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			
			pObject = InventoryDetailWebWork.findInventoryCommand(jsonObj, dctx);
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
	 * 查询商店盘点指令
	 */
	public static Map<String, Object> findInventoryCommandStore(DispatchContext dctx,Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			
			pObject = InventoryDetailWebWork.findInventoryCommandStore(jsonObj, dctx);
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
	 * 查询仓库盘点指令
	 */
	public static Map<String, Object> findInventoryCommandFacility(DispatchContext dctx,Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			
			pObject = InventoryDetailWebWork.findInventoryCommandFacility(jsonObj, dctx);
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
	 * 查询仓库盘点指令
	 */
	public static Map<String, Object> createInventoryDetailOther(DispatchContext dctx,Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			
			pObject = InventoryDetailWebWork.createInventoryDetailOther(jsonObj, dctx);
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
