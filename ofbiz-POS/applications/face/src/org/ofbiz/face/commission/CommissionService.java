package org.ofbiz.face.commission;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.entity.Delegator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.util.JSONUtil;

public class CommissionService {
	/**
	 * create现金提成
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> createCommission(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
		String jsonStr = (String) context.get("jsonStr");
		Map<String,Object> jsonObj = JSONUtil.json2Map(jsonStr);
		
    	try {
    		CommissionWorker.createCommission(delegator, context, jsonObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return ServiceUtil.returnSuccess();
	}
	/**
	 * update 现金提成
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> updateCommission(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
    	String id = (String) context.get("id");
    	String jsonStr = (String) context.get("jsonStr");
    	Map<String,Object> jsonObj = JSONUtil.json2Map(jsonStr);
    	
    	try {
    		CommissionWorker.updateCommission(delegator, context, jsonObj, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return ServiceUtil.returnSuccess();
	}
	
	
	/**
	 * create现金奖励
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> createAward(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
		String jsonStr = (String) context.get("jsonStr");
		Map<String,Object> jsonObj = JSONUtil.json2Map(jsonStr);
		
    	try {
    		CommissionWorker.createAward(delegator, context, jsonObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return ServiceUtil.returnSuccess();
	}
	/**
	 * update 现金奖励
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> updateAward(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
    	String id = (String) context.get("id");
    	String jsonStr = (String) context.get("jsonStr");
    	Map<String,Object> jsonObj = JSONUtil.json2Map(jsonStr);
    	
    	try {
    		CommissionWorker.updateAward(delegator, context, jsonObj, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return ServiceUtil.returnSuccess();
	}
	
	/**
	 * create积分基数
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> createBase(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
		String jsonStr = (String) context.get("jsonStr");
		Map<String,Object> jsonObj = JSONUtil.json2Map(jsonStr);
		
    	try {
    		CommissionWorker.createBase(delegator, context, jsonObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return ServiceUtil.returnSuccess();
	}
	/**
	 * update积分基数
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> updateBase(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
    	String id = (String) context.get("id");
    	String jsonStr = (String) context.get("jsonStr");
    	Map<String,Object> jsonObj = JSONUtil.json2Map(jsonStr);
    	
    	try {
    		CommissionWorker.updateBase(delegator, context, jsonObj, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return ServiceUtil.returnSuccess();
	}
	
	/**
	 * create积分倍率
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> createRate(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
		String jsonStr = (String) context.get("jsonStr");
		Map<String,Object> jsonObj = JSONUtil.json2Map(jsonStr);
		
    	try {
    		CommissionWorker.createRate(delegator, context, jsonObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return ServiceUtil.returnSuccess();
	}
	/**
	 * update积分倍率
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> updateRate(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
    	String id = (String) context.get("id");
    	String jsonStr = (String) context.get("jsonStr");
    	Map<String,Object> jsonObj = JSONUtil.json2Map(jsonStr);
    	
    	try {
    		CommissionWorker.updateRate(delegator, context, jsonObj, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> getStoreByStoreId(
			DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		
		Map<String, Object> result = FastMap.newInstance();
		List<String> lists = FastList.newInstance();
		String storeId = (String) context.get("storeId");
		
		lists = CommissionWorker.getStoreByStoreId(delegator, storeId);
		
		result.put("storeList", lists);
		return result;
	}
	public static Map<String, Object> getProductByProductId(
			DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		
		Map<String, Object> result = FastMap.newInstance();
		List<String> lists = FastList.newInstance();
		String storeId = (String) context.get("productId");
		
		lists = CommissionWorker.getProductByProductId(delegator, storeId);
		
		result.put("productList", lists);
		return result;
	}
}
