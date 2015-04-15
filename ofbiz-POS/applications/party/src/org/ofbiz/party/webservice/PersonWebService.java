package org.ofbiz.party.webservice;

import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.util.JSONUtil;

public class PersonWebService {
	public static final String module = PartyWebServices.class.getName();
	/*
	 * 查询店面人员
	 */
	public static Map<String, Object> findPersonByProductStoreId(DispatchContext dctx,Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" :(String)context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String, Object> mapIn = JSONUtil.json2Map(view);
			pObject = PersonWebWork.findPersonByProductStoreId(mapIn, dctx);
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		}else {
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}
}
