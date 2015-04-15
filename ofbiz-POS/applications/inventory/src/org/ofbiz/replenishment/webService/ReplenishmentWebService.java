package org.ofbiz.replenishment.webService;

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

public class ReplenishmentWebService {
	public static final String module = ReplenishmentWebService.class.getName();
	public static final String resource = "ReplenishmentUiLabels";
	public static final String resourceError = "ReplenishmentErrorUiLabels";

	/*
	 * 获取头命令
	 */
	public static Map<String, Object> findReplenishment(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context
				.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String, Object> jsonObj = JSONUtil.json2Map(view);
			pObject = ReplenishmentWebWork.findReplenishment(jsonObj, dctx);
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		} else {
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}

	/*
	 * 获取明细
	 */
	public static Map<String, Object> findReplenishmentItem(
			DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context
				.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String, Object> jsonObj = JSONUtil.json2Map(view);
			pObject = ReplenishmentWebWork.findReplenishmentItem(jsonObj, dctx);
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		} else {
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}

	/*
	 * 添加补货单
	 */
	public static Map<String, Object> saveReplenishment(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context
				.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			JSONObject jsonObj = JSONUtil.bean2Json(view);
			Map<String, Object> jsonHeader = JSONUtil.json2Map(jsonObj.get(
					"header").toString());

			JSONArray listItem = JSONArray.fromObject(jsonObj.get("item"));
			List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < listItem.size(); i++) {
				Map<String, Object> map = JSONUtil.json2Map(listItem.get(i)
						.toString());
				itemList.add(map);
			}

			pObject = ReplenishmentWebWork.saveReplenishment(jsonHeader,
					itemList, dctx);
			if (pObject.getFlag().equals(Constant.NG)) {
				resultJson.put(ModelService.RESPONSE_MESSAGE,
						ModelService.RESPOND_ERROR);
			}
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		} else {
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}

	/*
	 * 补货单申请
	 */
	public static Map<String, Object> saveReplenishmentBefor(
			DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" : (String) context
				.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String, Object> jsonObj = JSONUtil.json2Map(view);
			pObject = ReplenishmentWebWork.saveReplenishmentBefor1(jsonObj,
					dctx);
			if (pObject.getFlag().equals(Constant.NG)) {
				resultJson.put(ModelService.RESPONSE_MESSAGE,
						ModelService.RESPOND_ERROR);
			}
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		} else {
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}
}
