package org.ofbiz.product.condUtil;

import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

public class condSqlString {
	
	public static Map<String, String> entityOperators;

    static {
        entityOperators = FastMap.newInstance();
        entityOperators.put("EQ", " == ");
        entityOperators.put("GT", " > ");
        entityOperators.put("GTE", " >= ");
        entityOperators.put("LT", " < ");
        entityOperators.put("LTE", " <= ");
        entityOperators.put("NEQ", " <> ");
        entityOperators.put("like", " LIKE ");
    }
	
    public static String getSqlString(Delegator delegator, String entityName, String entityId, String vlaueId) {
    	StringBuffer sb = null;
    	List<GenericValue> gvs = null;
		try {
			gvs = delegator.findByAnd(entityName, UtilMisc.toMap(entityId, vlaueId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	int index = 0;
    	if (UtilValidate.isNotEmpty(gvs)) {
    		sb = new StringBuffer("(");
    		for (GenericValue gv : gvs) {
        		index++;
    			if (UtilValidate.isNotEmpty(gv.get("left1"))) {
    				sb.append("(");
    			}
    			sb.append(gv.get("formula"));
    			
    			if (UtilValidate.isNotEmpty(gv.get("right1"))) {
    				sb.append(")");
    			}
    			
    			if (gvs.size() != index) {
    				sb.append(" " + gv.get("relation") + " ");
    			}
    		}
    		sb.append(")");
    		return sb.toString();
		}
    	
    	return null;
    }
    
    public static String getPriceListStr(List<GenericValue> gvs) {
    	StringBuffer sb = null;
    	int index = 0;
    	if (UtilValidate.isNotEmpty(gvs)) {
    		sb = new StringBuffer("(");
    		for (GenericValue gv : gvs) {
        		index++;
    			if (UtilValidate.isNotEmpty(gv.get("left1"))) {
    				sb.append("(");
    			}
    			sb.append(gv.get("formula"));
    			
    			if (UtilValidate.isNotEmpty(gv.get("right1"))) {
    				sb.append(")");
    			}
    			
    			if (gvs.size() != index) {
    				sb.append(" " + gv.get("relation") + " ");
    			}
    		}
    		sb.append(")");
    		return sb.toString();
		}
    	
    	return null;
    }
    
    
    public static String getSql(Delegator delegator, String entityName, String entityId, String vlaueId ,String storeId) {
    	StringBuffer sb = null;
    	List<GenericValue> gvs = null;
		try {
			gvs = delegator.findByAnd(entityName, UtilMisc.toMap(entityId, vlaueId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	int index = 0;
    	if (UtilValidate.isNotEmpty(gvs)) {
    		sb = new StringBuffer("(");
    		for (GenericValue gv : gvs) {
        		index++;
    			if (UtilValidate.isNotEmpty(gv.get("left1"))) {
    				sb.append("(");
    			}
    			sb.append(gv.get("formula"));
    			
    			if (UtilValidate.isNotEmpty(gv.get("right1"))) {
    				//sb.append(")");
    			}
    			
    			if (gvs.size() != index) {
    				sb.append(" " + gv.get("relation") + " ");
    			}
    		}
    		if(UtilValidate.isNotEmpty(gvs)){
				sb.append("AND "+"PS.PRODUCT_STORE_ID"+"="+"'"+storeId+"'"+")");
				
    		}
    		sb.append(")");
    		return sb.toString();
		}
    	
    	return null;
    }
    public static String getSqlmember(Delegator delegator, String entityName, String entityId, String vlaueId ,String partyId) {
    	StringBuffer sb = null;
    	List<GenericValue> gvs = null;
		try {
			gvs = delegator.findByAnd(entityName, UtilMisc.toMap(entityId, vlaueId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	int index = 0;
    	if (UtilValidate.isNotEmpty(gvs)) {
    		sb = new StringBuffer("(");
    		for (GenericValue gv : gvs) {
        		index++;
    			if (UtilValidate.isNotEmpty(gv.get("left1"))) {
    				sb.append("(");
    			}
    			sb.append(gv.get("formula"));
    			
    			if (UtilValidate.isNotEmpty(gv.get("right1"))) {
    				//sb.append(")");
    			}
    			
    			if (gvs.size() != index) {
    				sb.append(" " + gv.get("relation") + " ");
    			}
    		}
    		if(UtilValidate.isNotEmpty(gvs)){
				sb.append("AND "+"PE.PARTY_ID"+"="+"'"+partyId+"'");
				
    		}
    		sb.append(")");
    		return sb.toString();
		}
    	
    	return null;
    }
}
