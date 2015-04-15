package org.ofbiz.order.salespromotion;

import java.util.Map;

import javolution.util.FastMap;
import net.sf.json.JSONSerializer;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.order.parameter.Parameter;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.util.JSONUtil;


public class SalesPromotionServices {
	 /**
     * getProductByPromoCond.
     */
	 public static Map<String, Object> getProductPriceByPromoCond(DispatchContext dctx, Map<String, ? extends Object> context) {
	    	Map<String, Object> resultStr = FastMap.newInstance();
	    	Map<String, Object> pObject = null;
	    	Parameter parameter = null;
	    	String strResult = "";
	    	try{
	    		String jsonStr = context.get("jsonStr") == null ? "" : (String) context.get("jsonStr");
		    	if (UtilValidate.isNotEmpty(jsonStr)) {
					parameter = (Parameter)JSONUtil.json2Bean(jsonStr, Parameter.class);
					pObject = SalesPromotionWorker.getProductPriceByPromoCond(dctx, parameter);
					strResult = JSONSerializer.toJSON(pObject).toString();
					resultStr.put("jsonOut", strResult);
						resultStr.put("jsonOut", strResult);
					}else{
						resultStr.put("jsonOut", "no param");
					}
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	       return resultStr;
	    }
}
