package org.ofbiz.querydatacommon;

import java.util.Map;
import javolution.util.FastMap;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

public class QueryDataCommonServices {
	/**
     * getOrderId
     * @param dctx
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getOrderId(DispatchContext dctx, Map<String,? extends Object> context) {
	    	Map<String,Object> resultStr = FastMap.newInstance();
	    	BasePosObject pObject = null;
	    	String strResult = "";
	    	try{ 
	        	String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
	        	if (UtilValidate.isNotEmpty(jsonStr)){
	    			JSONObject jsonObj = JSONObject.fromObject(jsonStr);
	    			pObject = QueryDataCommonWorker.getOrderId(dctx, jsonObj);
	    			strResult = JSONSerializer.toJSON(pObject).toString();
	    			resultStr.put("jsonOut", strResult);
	    			if(pObject.getFlag().equals(Constant.OK)){
						resultStr.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
					}else{
						resultStr.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					}
	    		}else{
	    			resultStr.put("jsonOut", "no param");
	    		}
	        }catch(Exception e){
	        	e.printStackTrace();
	        }
	        return resultStr;
	    }
}
