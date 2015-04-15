package org.ofbiz.product.store;

import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.util.JSONUtil;

public class ProductStoreService {

	public static Map<String,Object> getProductStores(DispatchContext dctx, Map<String, ? extends Object> context){
    	Map<String, Object> resultJson = FastMap.newInstance();
		BasePosObject pObject = null;
		String strResult = "";
		pObject = ProductStoreWorker.getProductStores(dctx);
		if(pObject.getFlag().equals(Constant.NG)){
			resultJson.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
		}
		strResult = JSONUtil.bean2Json(pObject).toString();
		resultJson.put("jsonOut", strResult);
		return resultJson;
    }
}
