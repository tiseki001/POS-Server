package org.ofbiz.face.commission;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.util.ConvertUtil;

public class CommissionWorker {
	
	public static List<String> getStoreByStoreId(Delegator delegator, String storeId) {
		List<String> lists = FastList.newInstance();
		EntityFindOptions efo = new EntityFindOptions();
        efo.setDistinct(true);
		EntityCondition condition = EntityCondition.makeCondition(EntityCondition.makeCondition("productStoreId", storeId));
		List<GenericValue> gvs = null;
		try {
	        gvs = delegator.findList("ProductStore", condition, null,null, efo, true);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		for (GenericValue gv : gvs) {
			lists.add(gv.get("storeName").toString() 
					+ gv.get("productStoreId"));
		}
		return lists;
	}
	
	public static List<String> getProductByProductId(Delegator delegator, String productId) {
		List<String> lists = FastList.newInstance();
		EntityFindOptions efo = new EntityFindOptions();
        efo.setDistinct(true);
		EntityCondition condition = EntityCondition.makeCondition(EntityCondition.makeCondition("productId", productId));
		List<GenericValue> gvs = null;
		try {
	        gvs = delegator.findList("Product", condition, null,null, efo, true);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		for (GenericValue gv : gvs) {
			lists.add(gv.get("productName").toString() 
					+ gv.get("productId"));
		}
		return lists;
	}
	/**
	 * 创建现金提成
	 * @param delegator
	 * @param context
	 * @param jsonObj
	 */
	public static void createCommission(Delegator delegator,
			Map<String, ? extends Object> context, Map<String, Object> jsonObj)throws GenericEntityException {
		String startDate = (String) jsonObj.get("startDate");
		jsonObj.remove("startDate");
		String endDate = (String) jsonObj.get("endDate");
		jsonObj.remove("endDate");
		String awardString = (String) jsonObj.get("award");
		Long award = Long.parseLong(awardString);
		String storeId = (String) jsonObj.get("storeId");
		String id = storeId+delegator.getNextSeqId("Commission");
		String storeName = CommissionWorker.getStoreNameByStoreId(delegator, storeId);
    	GenericValue entity = delegator.makeValidValue("Commission", UtilMisc.toMap("id", id));
    	entity.setNonPKFields(jsonObj);
    	entity.set("award", 
    			award == null ? null : award);
    	entity.set("storeName", 
    			storeName == null ? null : storeName);
    	entity.set("startDate", 
    			startDate == null ? null : ConvertUtil.convertStringToTimeStamp(startDate.replaceAll("/", "-")));
    	entity.set("endDate", 
    			endDate == null ? null : ConvertUtil.convertStringToTimeStamp(endDate.replaceAll("/", "-")));
    	entity.create();
	}
	/**
	 * 修改现金提成
	 * @param delegator
	 * @param context
	 * @param jsonObj
	 * @param id
	 */
	public static void updateCommission(Delegator delegator,
			Map<String, ? extends Object> context, Map<String, Object> jsonObj,
			String id)throws GenericEntityException {
		GenericValue entity = delegator.findByPrimaryKey("Commission", UtilMisc.toMap("id", id));
		if (UtilValidate.isNotEmpty(entity)) {
			String startDate = (String) jsonObj.get("startDate");
			jsonObj.remove("startDate");
			String endDate = (String) jsonObj.get("endDate");
			jsonObj.remove("endDate");
			String awardString = (String) jsonObj.get("award");
			Long award = Long.parseLong(awardString);
			String storeId = (String) jsonObj.get("storeId");
			String storeName = CommissionWorker.getStoreNameByStoreId(delegator, storeId);
	    	entity.setNonPKFields(jsonObj);
	    	entity.set("award", 
	    			award == null ? null : award);
	    	entity.set("storeName", 
	    			storeName == null ? null : storeName);
	    	entity.set("startDate", 
	    			startDate == null ? null : ConvertUtil.convertStringToTimeStamp(startDate.replaceAll("/", "-")));
	    	entity.set("endDate", 
	    			endDate == null ? null : ConvertUtil.convertStringToTimeStamp(endDate.replaceAll("/", "-")));
			entity.store();
		}
	}
	/**
	 * 根据门店获取门店名称
	 * @param delegator
	 * @param storeId
	 * @return
	 */
	public static String getStoreNameByStoreId(Delegator delegator, String storeId) {
		GenericValue productStore = null;
		try {
			productStore = delegator.findByPrimaryKey("ProductStore", UtilMisc.toMap("productStoreId", storeId));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return productStore.get("storeName").toString();
	}
	/**
	 * 创建现奖励
	 * @param delegator
	 * @param context
	 * @param jsonObj
	 */
	public static void createAward(Delegator delegator,
			Map<String, ? extends Object> context, Map<String, Object> jsonObj)throws GenericEntityException {
		String startDate = (String) jsonObj.get("startDate");
		jsonObj.remove("startDate");
		String endDate = (String) jsonObj.get("endDate");
		jsonObj.remove("endDate");
		String awardString = (String) jsonObj.get("award");
		Long award = Long.parseLong(awardString);
		String qualifyString = (String) jsonObj.get("qualify");
		Long qualify = Long.parseLong(qualifyString);
		String storeId = (String) jsonObj.get("storeId");
		String id = storeId+delegator.getNextSeqId("Award");
		String productId = (String) jsonObj.get("productId");
		String productName = CommissionWorker.getProductNameByProductId(delegator, productId);
    	GenericValue entity = delegator.makeValidValue("Award", UtilMisc.toMap("id", id));
    	entity.setNonPKFields(jsonObj);
    	entity.set("award", 
    			award == null ? null : award);
    	entity.set("qualify", 
    			award == null ? null : qualify);
    	entity.set("productName", 
    			productName == null ? null : productName);
    	entity.set("startDate", 
    			startDate == null ? null : ConvertUtil.convertStringToTimeStamp(startDate.replaceAll("/", "-")));
    	entity.set("endDate", 
    			endDate == null ? null : ConvertUtil.convertStringToTimeStamp(endDate.replaceAll("/", "-")));
    	entity.create();
	}
	
	/**
	 * 修改现金奖励
	 * @param delegator
	 * @param context
	 * @param jsonObj
	 * @param id
	 */
	public static void updateAward(Delegator delegator,
			Map<String, ? extends Object> context, Map<String, Object> jsonObj,
			String id)throws GenericEntityException {
		GenericValue entity = delegator.findByPrimaryKey("Award", UtilMisc.toMap("id", id));
		if (UtilValidate.isNotEmpty(entity)) {
			String startDate = (String) jsonObj.get("startDate");
			jsonObj.remove("startDate");
			String endDate = (String) jsonObj.get("endDate");
			jsonObj.remove("endDate");
			String awardString = (String) jsonObj.get("award");
			Long award = Long.parseLong(awardString);
			String qualifyString = (String) jsonObj.get("qualify");
			Long qualify = Long.parseLong(qualifyString);
			String productId = (String) jsonObj.get("productId");
			String productName = CommissionWorker.getProductNameByProductId(delegator, productId);
	    	entity.setNonPKFields(jsonObj);
	    	entity.set("award", 
	    			award == null ? null : award);
	    	entity.set("qualify", 
	    			award == null ? null : qualify);
	    	entity.set("productName", 
	    			productName == null ? null : productName);
	    	entity.set("startDate", 
	    			startDate == null ? null : ConvertUtil.convertStringToTimeStamp(startDate.replaceAll("/", "-")));
	    	entity.set("endDate", 
	    			endDate == null ? null : ConvertUtil.convertStringToTimeStamp(endDate.replaceAll("/", "-")));
			entity.store();
		}
	}
	
	/**
	 * 根据商品id获取商品名称
	 * @param delegator
	 * @param productId
	 * @return
	 */
	public static String getProductNameByProductId(Delegator delegator, String productId) {
		GenericValue productStore = null;
		try {
			productStore = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return productStore.get("productName").toString();
	}
	
	/**
	 * 创建积分基数
	 * @param delegator
	 * @param context
	 * @param jsonObj
	 */
	public static void createBase(Delegator delegator,
			Map<String, ? extends Object> context, Map<String, Object> jsonObj)throws GenericEntityException {
		String startDate = (String) jsonObj.get("startDate");
		jsonObj.remove("startDate");
		String endDate = (String) jsonObj.get("endDate");
		jsonObj.remove("endDate");
		String baseString = (String) jsonObj.get("base");
		Long base = Long.parseLong(baseString);
		String storeId = (String) jsonObj.get("storeId");
		String storeName = CommissionWorker.getStoreNameByStoreId(delegator, storeId);
		String id = storeId+delegator.getNextSeqId("Base");
    	GenericValue entity = delegator.makeValidValue("Base", UtilMisc.toMap("id", id));
    	entity.setNonPKFields(jsonObj);
    	entity.set("base", 
    			base == null ? null : base);
    	entity.set("storeName", 
    			storeName == null ? null : storeName);
    	entity.set("startDate", 
    			startDate == null ? null : ConvertUtil.convertStringToTimeStamp(startDate.replaceAll("/", "-")));
    	entity.set("endDate", 
    			endDate == null ? null : ConvertUtil.convertStringToTimeStamp(endDate.replaceAll("/", "-")));
    	entity.create();
	}
	
	/**
	 * 修改积分基数
	 * @param delegator
	 * @param context
	 * @param jsonObj
	 * @param id
	 */
	public static void updateBase(Delegator delegator,
			Map<String, ? extends Object> context, Map<String, Object> jsonObj,
			String id)throws GenericEntityException {
		GenericValue entity = delegator.findByPrimaryKey("Base", UtilMisc.toMap("id", id));
		if (UtilValidate.isNotEmpty(entity)) {
			String startDate = (String) jsonObj.get("startDate");
			jsonObj.remove("startDate");
			String endDate = (String) jsonObj.get("endDate");
			jsonObj.remove("endDate");
			String baseString = (String) jsonObj.get("base");
			Long base = Long.parseLong(baseString);
			String storeId = (String) jsonObj.get("storeId");
			String storeName = CommissionWorker.getStoreNameByStoreId(delegator, storeId);
	    	entity.setNonPKFields(jsonObj);
	    	entity.set("base", 
	    			base == null ? null : base);
	    	entity.set("storeName", 
	    			storeName == null ? null : storeName);
	    	entity.set("startDate", 
	    			startDate == null ? null : ConvertUtil.convertStringToTimeStamp(startDate.replaceAll("/", "-")));
	    	entity.set("endDate", 
	    			endDate == null ? null : ConvertUtil.convertStringToTimeStamp(endDate.replaceAll("/", "-")));
			entity.store();
		}
	}
	
	/**
	 * 创建积分倍率
	 * @param delegator
	 * @param context
	 * @param jsonObj
	 */
	public static void createRate(Delegator delegator,
		Map<String, ? extends Object> context, Map<String, Object> jsonObj)throws GenericEntityException {
			String startDate = (String) jsonObj.get("startDate");
			jsonObj.remove("startDate");
			String endDate = (String) jsonObj.get("endDate");
			jsonObj.remove("endDate");
			String rateString = (String) jsonObj.get("rate");
			Long rate = Long.parseLong(rateString);
			String storeId = (String) jsonObj.get("storeId");
			String storeName = CommissionWorker.getStoreNameByStoreId(delegator, storeId);
			String productId = (String) jsonObj.get("productId");
			String productName = CommissionWorker.getProductNameByProductId(delegator, productId);
			String id = storeId+delegator.getNextSeqId("Rate");
	    	GenericValue entity = delegator.makeValidValue("Rate", UtilMisc.toMap("id", id));
	    	entity.setNonPKFields(jsonObj);
	    	entity.set("rate", 
	    			rate == null ? null : rate);
	    	entity.set("storeName", 
	    			storeName == null ? null : storeName);
	    	entity.set("productName", 
	    			productName == null ? null : productName);
	    	entity.set("startDate", 
	    			startDate == null ? null : ConvertUtil.convertStringToTimeStamp(startDate.replaceAll("/", "-")));
	    	entity.set("endDate", 
	    			endDate == null ? null : ConvertUtil.convertStringToTimeStamp(endDate.replaceAll("/", "-")));
	    	entity.create();
	}
	
	/**
	 * 修改积分倍率
	 * @param delegator
	 * @param context
	 * @param jsonObj
	 * @param id
	 */
	public static void updateRate(Delegator delegator,
			Map<String, ? extends Object> context, Map<String, Object> jsonObj,
			String id)throws GenericEntityException {
		GenericValue entity = delegator.findByPrimaryKey("Rate", UtilMisc.toMap("id", id));
		if (UtilValidate.isNotEmpty(entity)) {
			String startDate = (String) jsonObj.get("startDate");
			jsonObj.remove("startDate");
			String endDate = (String) jsonObj.get("endDate");
			jsonObj.remove("endDate");
			String rateString = (String) jsonObj.get("rate");
			Long rate = Long.parseLong(rateString);
			String storeId = (String) jsonObj.get("storeId");
			String storeName = CommissionWorker.getStoreNameByStoreId(delegator, storeId);
			String productId = (String) jsonObj.get("productId");
			String productName = CommissionWorker.getProductNameByProductId(delegator, productId);
	    	entity.setNonPKFields(jsonObj);
	    	entity.set("rate", 
	    			rate == null ? null : rate);
	    	entity.set("storeName", 
	    			storeName == null ? null : storeName);
	    	entity.set("productName", 
	    			productName == null ? null : productName);
	    	entity.set("startDate", 
	    			startDate == null ? null : ConvertUtil.convertStringToTimeStamp(startDate.replaceAll("/", "-")));
	    	entity.set("endDate", 
	    			endDate == null ? null : ConvertUtil.convertStringToTimeStamp(endDate.replaceAll("/", "-")));
			entity.store();
		}
	}
}
