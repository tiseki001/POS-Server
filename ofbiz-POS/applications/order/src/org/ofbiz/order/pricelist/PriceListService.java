package org.ofbiz.order.pricelist;

import java.util.Map;
import javolution.util.FastMap;
import org.ofbiz.service.DispatchContext;

public class PriceListService {
	public static Map<String, Object> updateChangedPriceList(DispatchContext dctx,Map<String, ? extends Object> context) {
		Map<String, Object> resultJson = FastMap.newInstance();
		try {
			String whereCondition = "";
			if(context.get("whereCondition") == null){
				whereCondition = "";
			}else{
				whereCondition = (String) context.get("whereCondition");
				whereCondition = whereCondition.substring(1, whereCondition.length()-1);
			}
			ChangedPriceList.UpdateIsSee(whereCondition, dctx.getDelegator());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			resultJson.put("result", e.getMessage());
			e.printStackTrace();
			return resultJson;
		}
		resultJson.put("result", "S");
		return resultJson;
	}
}
