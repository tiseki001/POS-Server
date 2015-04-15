package org.ofbiz.order.pricelist;

import java.sql.Timestamp;
import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;

public class ChangedPriceList {
	public static void UpdateIsSee(String whereCondition, Delegator delegator){
		try {
			if(whereCondition.length() < 1) return;
			String storeId = whereCondition.split(",")[0];
			Timestamp lastUpdatedStamp = new Timestamp(System.currentTimeMillis()); 
			lastUpdatedStamp = Timestamp.valueOf(whereCondition.split(",")[1]); 
			//List<EntityCondition> andExprs = FastList.newInstance();	
			//andExprs.add(EntityCondition.makeCondition("storeId",EntityOperator.EQUALS,storeId));
			//andExprs.add(EntityCondition.makeCondition("lastUpdatedStamp",EntityOperator.LESS_THAN,lastUpdatedStamp));
			//EntityCondition mainCond = EntityCondition.makeCondition(andExprs,EntityOperator.AND);
			EntityCondition mainCond = EntityCondition.makeCondition("lastUpdatedStamp",EntityOperator.LESS_THAN,lastUpdatedStamp);
			TransactionUtil.begin();
			delegator.storeByCondition("PriceListChange",
					UtilMisc.toMap("isSee", "Y"), mainCond);
			TransactionUtil.commit();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
