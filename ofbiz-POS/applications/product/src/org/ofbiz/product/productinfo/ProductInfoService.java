package org.ofbiz.product.productinfo;

import java.util.Map;

import javolution.util.FastMap;

import net.sf.json.JSONObject;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.util.JSONUtil;

public class ProductInfoService {
	  /*
     * Analytical parameters
     * 模糊查询
     */
	public static Map<String,Object> productInfo(DispatchContext dctx, Map<String, ? extends Object> context){
    	Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(jsonStr)) {
			JSONObject jsonObj = JSONObject.fromObject(jsonStr);	
			pObject = ProductInfoWork.productInfo(dctx, jsonObj);
			if(pObject.getFlag().equals(Constant.NG)){
				resultJson.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			}
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("result", strResult);
		}else{
			resultJson.put("result", "no param");
		}
		return resultJson;
    }

	public static Map<String,Object> productSequenceInfo(DispatchContext dctx, Map<String, ? extends Object> context){
    	Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		if (UtilValidate.isNotEmpty(jsonStr)) {
			JSONObject jsonObj = JSONObject.fromObject(jsonStr);	
			pObject = ProductInfoWork.productSequenceInfo(dctx, jsonObj);
			if(pObject.getFlag().equals(Constant.NG)){
				resultJson.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			}
			strResult = JSONUtil.bean2Json(pObject).toString();
			resultJson.put("result", strResult);
		}else{
			resultJson.put("result", "no param");
		}
		return resultJson;
    }
}
