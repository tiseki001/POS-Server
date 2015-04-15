package org.ofbiz.receive.webService;

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

public class ReceiveWebService {
	public static final String module = ReceiveWebService.class.getName();
	public static final String resource = "ReceiveUiLabels";
	public static final String resourceError = "ReceiveErrorUiLabels";

	/*
	 * 获取头命令
	 */
	public static Map<String, Object> findReceiveCommand(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String, Object> jsonObj = JSONUtil.json2Map(view);
			pObject = ReceiveWebWork.findReceiveCommand(jsonObj, dctx);
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		}else {
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}
	/*
	 * 收货单查询
	 */
	public static Map<String, Object> findReceiveDoc(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			pObject = ReceiveWebWork.findReceiveDoc(jsonObj, dctx);
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		}else{
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}
	/*
	 * 获取明细指令
	 */
	public static Map<String, Object> findReceiveItemcommand(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			pObject = ReceiveWebWork.findReceiveItemcommand(jsonObj, dctx);
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		}else{
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}
	/*
	 * 收货单明细
	 */
	public static Map<String, Object> findReceiveItem(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			pObject = ReceiveWebWork.findReceiveItem(jsonObj, dctx);
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
	public static Map<String, Object> deleteReceiveCommand(DispatchContext dctx,Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			
			pObject = ReceiveWebWork.deleteReceiveCommand(jsonObj, dctx);
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
	 * 创建收货单
	 */
	public static Map<String,Object> allocateReceive(DispatchContext dctx,Map<String, ? extends Object> context){
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
			
			pObject = ReceiveWebWork.allocateReceive(jsonHeader,itemList, dctx);
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
	 * 修改收货单
	 */
	public static Map<String,Object> updateReceive(DispatchContext dctx,Map<String, ? extends Object> context){
    	
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
			
			pObject = ReceiveWebWork.updateReceive(jsonHeader,itemList, dctx);
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
	public static Map<String, Object> updateReceivePrint(DispatchContext dctx,Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String,Object> jsonObj = JSONUtil.json2Map(view);
			pObject = ReceiveWebWork.updateReceivePrint(jsonObj, dctx);
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
