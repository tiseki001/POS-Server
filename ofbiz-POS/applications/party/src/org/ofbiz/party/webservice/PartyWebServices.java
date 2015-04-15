package org.ofbiz.party.webservice;

import java.util.Map;

import javolution.util.FastMap;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.util.JSONUtil;

public class PartyWebServices {
	public static final String module = PartyWebServices.class.getName();
	
	public static final String username = "create_party";
	public static final String password = "partyadmin";//System.getProperty("admin.pwd");

	/*
	 * 查询会员信息
	 */
	public static Map<String, Object> findPartyInfo(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" :(String)context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			Map<String, Object> jsonObj = JSONUtil.json2Map(view);
			pObject = PartyWebWork.findPartyInfo(jsonObj, dctx);
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		}else {
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}
	
	/*
	 * 添加会员信息
	 */
	public static Map<String, Object> createParty(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		/*String username = (String) context.get("login.username");
        if (username == null) username = (String) context.get("username");
        String password = (String) context.get("login.password");
        if (password == null) password = (String) context.get("password");*/
        
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" :(String)context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			JSONObject jsonObj = JSONUtil.bean2Json(view);
			Map<String,Object> member = JSONUtil.json2Map(jsonObj.get("member").toString());
			pObject = PartyWebWork.createParty(dctx, member,
					username, 
					password);
			if(pObject.getFlag().equals(Constant.NG)){
				resultJson.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			}
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		}else {
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}
	
	/*
	 * 修改会员信息
	 */
	public static Map<String, Object> updateParty(DispatchContext dctx,
			Map<String, ? extends Object> context) {
        
		Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String view = context.get("jsonStr") == null ? "" :(String)context.get("jsonStr");
		if (UtilValidate.isNotEmpty(view)) {
			JSONObject jsonObj = JSONUtil.bean2Json(view);
			Map<String,Object> member = JSONUtil.json2Map(jsonObj.get("member").toString());
			pObject = PartyWebWork.updateParty(dctx, member,
					username, 
					password);
			if(pObject.getFlag().equals(Constant.NG)){
				resultJson.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			}
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		}else {
			resultJson.put("jsonOut", "no param");
		}
		return resultJson;
	}
	/*
	 * 获取ID
	 */
	public static Map<String, Object> getId(DispatchContext dctx,Map<String, ? extends Object> context){
		Map<String, Object> resultJson = FastMap.newInstance();
		String productStoreId=(String) context.get("productStoreId");
		resultJson.put("contactMechId", PartyWebWork.getId(dctx, productStoreId));
		return resultJson;
	}
	public static Map<String,Object> findPartyPc(DispatchContext dctx,Map<String, ? extends Object> context){
		Map<String, Object> resultJson = FastMap.newInstance();
		String strResult = "";
		BasePosObject pObject = PartyWebWork.findPartyPc(dctx);
		resultJson.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
		strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("jsonOut", strResult);
		return resultJson;
	}
	
}
