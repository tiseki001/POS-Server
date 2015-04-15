package org.ofbiz.query;

import java.util.Map;

import javolution.util.FastMap;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.util.JSONUtil;

public class QueryService {
	/*
	 * public static Map<String, Object> select(DispatchContext dctx,
	 * Map<String, ? extends Object> context) { Delegator delegator =
	 * dctx.getDelegator(); LocalDispatcher dispatcher = dctx.getDispatcher();
	 * Map<String,Object> resultJson = FastMap.newInstance(); BasePosObject
	 * pObject = new BasePosObject(); EntityCondition mainCond = null;
	 * List<GenericValue> resultList=null; String strResult="";
	 * 
	 * String view = context.get("view")==null?"":(String)context.get("view");
	 * String viewName=""; String where=""; JSONArray orderbyList = null;
	 * JSONArray fieldsList = null; List<String> fieldsToSelect = null;
	 * List<String> orderBy = null; //orderBy.getClass() try { Set<String>
	 * fieldsToSelectSet = null; if (UtilValidate.isNotEmpty(view)) { JSONObject
	 * jsonObj = JSONObject.fromObject(view); viewName =
	 * jsonObj.get("ViewName").toString(); where =
	 * jsonObj.get("Where")==null?"":jsonObj.get("Where").toString();
	 * orderbyList =
	 * jsonObj.get("OrderBy")==null?null:JSONArray.fromObject(jsonObj
	 * .get("OrderBy")); fieldsList =
	 * jsonObj.get("Fields")==null?null:JSONArray.
	 * fromObject(jsonObj.get("Fields")); int a = fieldsList.size(); }
	 * fieldsToSelect = JSONArray.toList(fieldsList); orderBy =
	 * JSONArray.toList(orderbyList); EntityFindOptions findOpts = new
	 * EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE,
	 * EntityFindOptions.CONCUR_READ_ONLY, -1, -1, true);
	 * 
	 * if (UtilValidate.isNotEmpty(where)) mainCond =
	 * EntityCondition.makeConditionWhere(where);
	 * if(fieldsToSelect!=null&&fieldsToSelect.size()>0) fieldsToSelectSet = new
	 * HashSet(fieldsToSelect);
	 * 
	 * TransactionUtil.begin(); resultList = delegator.findList(viewName,
	 * mainCond, fieldsToSelectSet, orderBy, findOpts, true);
	 * if(resultList!=null&&resultList.size()>0){ pObject.setFlag("S");
	 * pObject.setList(resultList); //pObject.setFlag("F");
	 * //pObject.setMsg("eeeeeee");
	 * 
	 * }
	 * 
	 * TransactionUtil.commit(); } catch (Exception e) { // TODO Auto-generated
	 * catch block pObject.setFlag("F"); pObject.setMsg(e.getMessage());
	 * e.printStackTrace(); } strResult =
	 * JSONSerializer.toJSON(pObject).toString(); resultJson.put("outJsonParam",
	 * strResult); return resultJson; }
	 */
	public static Map<String, Object> select(DispatchContext dctx,Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("view") == null ? "" : (String) context
				.get("view");
		if (UtilValidate.isNotEmpty(view)) {
			JSONObject jsonObj = JSONObject.fromObject(view);
			pObject = QueryWorker.select(dctx, jsonObj);
//			strResult = JSONSerializer.toJSON(pObject).toString();
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("outJsonParam", strResult);
		}else{
			resultJson.put("outJsonParam", "no param");
		}
		return resultJson;
	}
}
