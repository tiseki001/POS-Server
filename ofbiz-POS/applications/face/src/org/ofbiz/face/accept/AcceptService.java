package org.ofbiz.face.accept;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.util.JSONUtil;

public class AcceptService {
	
	/**
	 * create 售后受理单
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> createAccept(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
		String jsonStr = (String) context.get("jsonStr");
		Map<String,Object> jsonObj = JSONUtil.json2Map(jsonStr);
		
    	try {
    		AcceptServiceWorker.createAccept(delegator, context, jsonObj);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return ServiceUtil.returnSuccess();
	}
	/**
	 * update 售后受理单
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> updateAccept(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
    	String docId = (String) context.get("docId");
    	String jsonStr = (String) context.get("jsonStr");
    	Map<String,Object> jsonObj = JSONUtil.json2Map(jsonStr);
    	
    	try {
			AcceptServiceWorker.updateAccept(delegator, context, jsonObj, docId);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> getSaleOrderAndPersonView(
			DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		
		Map<String, Object> result = FastMap.newInstance();
		List<String> lists = FastList.newInstance();
		String sequenceId = (String) context.get("sequenceId");
		
		lists = AcceptServiceWorker.getSaleOrderAndPersonView(delegator, sequenceId);
		
		result.put("valList", lists);
		return result;
	}
	
	public static Map<String, Object> getPartyByStoreId(
			DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		
		Map<String, Object> result = FastMap.newInstance();
		List<String> lists = FastList.newInstance();
		String storeId = (String) context.get("storeId");
		
		lists = AcceptServiceWorker.getPartyByStoreId(delegator, storeId);
		
		result.put("partyList", lists);
		return result;
	}

}
