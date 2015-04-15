package org.ofbiz.face.accept;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.util.ConvertUtil;

public class AcceptServiceWorker {

	public static String getDocNameByProductId(Delegator delegator, String productId) {
		GenericValue product = null;
		try {
			product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return product.get("productName").toString() + productId;
	}
	
	public static List<String> getSaleOrderAndPersonView(Delegator delegator, String sequenceId) {
		List<String> lists = FastList.newInstance();
		List<GenericValue> gvs = null;
		try {
			gvs = delegator.findByAnd("SaleOrderAndPersonView", UtilMisc
					.toMap("serialNo", sequenceId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (UtilValidate.isNotEmpty(gvs)) {
			lists.add("storeId: " + gvs.get(0).get("storeId"));
			lists.add("postingDate: " + gvs.get(0).get("postingDate"));
			lists.add("productId: " + gvs.get(0).get("productId"));
			lists.add("rebatePrice: " + gvs.get(0).get("rebatePrice"));
			lists.add("name: " + gvs.get(0).get("firstName")
					+ gvs.get(0).get("lastName"));
			lists.add("phoneMobile: " + gvs.get(0).get("phoneMobile"));
			lists.add("address1: " + gvs.get(0).get("address1"));
		}else {
			lists.add("storeId: " + "");
			lists.add("postingDate: " + "");
			lists.add("productId: " + "");
			lists.add("rebatePrice: " + "");
			lists.add("name: " + "");
			lists.add("phoneMobile: " + "");
			lists.add("address1: " + "");
		}
		return lists;
	}
	
	public static List<String> getPartyByStoreId(Delegator delegator, String storeId) {
		List<String> lists = FastList.newInstance();
		
		EntityFindOptions efo = new EntityFindOptions();
        efo.setDistinct(true);
        
		EntityConditionList<EntityExpr> ecl = EntityCondition.makeCondition(EntityOperator.OR,
                EntityCondition.makeCondition("roleTypeId", "EMPLOYEE"),
                EntityCondition.makeCondition("parentTypeId", "EMPLOYEE"));
		EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
				ecl,EntityCondition.makeCondition("productStoreId", storeId));
		List<GenericValue> gvs = null;
		try {
	        gvs = delegator.findList("CustomerAndpersonView", condition, null,null, efo, true);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (GenericValue gv : gvs) {
			lists.add(gv.get("lastName").toString() 
					+ gv.get("firstName").toString() 
					+ ": "
					+ gv.get("partyId"));
		}
		return lists;
	}
	
	public static void createAccept(Delegator delegator, 
			Map<String, ? extends Object> context, 
				Map<String, Object> jsonObj) throws GenericEntityException {
		
		String accepteStoreId = (String) jsonObj.get("accepteStoreId");
    	String productId = (String) jsonObj.get("productId");
    	String price = (String) jsonObj.get("price");
		jsonObj.remove("price");
		String accepteDate = (String) jsonObj.get("accepteDate");
		jsonObj.remove("accepteDate");
		String auditorDate = (String) jsonObj.get("auditorDate");
		jsonObj.remove("auditorDate");
		String productDate = (String) jsonObj.get("productDate");
		jsonObj.remove("productDate");
    	
    	String docId = accepteStoreId + delegator.getNextSeqId("AfterSaleAccepted");
    	String docName = AcceptServiceWorker.getDocNameByProductId(delegator, productId);
    	
    	GenericValue entity = delegator.makeValidValue("AfterSaleAccepted", UtilMisc.toMap("docId", docId,
    			"docName", docName));
    	entity.setNonPKFields(jsonObj);
    	entity.set("price", 
    			price == null ? null : new BigDecimal(price));
    	entity.set("accepteDate", 
    			accepteDate == null ? null : ConvertUtil.convertStringToTimeStamp(accepteDate.replaceAll("/", "-")));
    	entity.set("auditorDate", 
    			auditorDate == null ? null : ConvertUtil.convertStringToTimeStamp(auditorDate.replaceAll("/", "-")));
    	entity.set("productDate", 
    			productDate == null ? null : ConvertUtil.convertStringToTimeStamp(productDate.replaceAll("/", "-")));
    	entity.create();
		
	}
	
	public static void updateAccept(Delegator delegator, 
			Map<String, ? extends Object> context, 
				Map<String, Object> jsonObj, String docId) throws GenericEntityException {
		GenericValue entity = delegator.findByPrimaryKey("AfterSaleAccepted", UtilMisc.toMap("docId", docId));
		
		if (jsonObj.get("flag").equals("accept")) {
			String price = (String) jsonObj.get("price");
			jsonObj.remove("price");
			String accepteDate = (String) jsonObj.get("accepteDate");
			jsonObj.remove("accepteDate");
			String auditorDate = (String) jsonObj.get("auditorDate");
			jsonObj.remove("auditorDate");
			String productDate = (String) jsonObj.get("productDate");
			jsonObj.remove("productDate");
			entity.setNonPKFields(jsonObj);
			entity.set("accepteDate", 
					accepteDate == null ? null : ConvertUtil.convertStringToTimeStamp(accepteDate.replaceAll("/", "-")));
	    	entity.set("auditorDate", 
	    			auditorDate == null ? null : ConvertUtil.convertStringToTimeStamp(auditorDate.replaceAll("/", "-")));
	    	entity.set("productDate", 
	    			productDate == null ? null : ConvertUtil.convertStringToTimeStamp(productDate.replaceAll("/", "-")));
			entity.set("price", 
					price == null ? null : new BigDecimal(price.replaceAll(",", "")));
			entity.store();
		}else {
			String cost1 = (String) jsonObj.get("cost1");
			jsonObj.remove("cost1");
			String cost2 = (String) jsonObj.get("cost2");
			jsonObj.remove("cost2");
			String costTotal = (String) jsonObj.get("costTotal");
			jsonObj.remove("costTotal");
			String quoteDate = (String) jsonObj.get("quoteDate");
			jsonObj.remove("quoteDate");
			String confirmDate = (String) jsonObj.get("confirmDate");
			jsonObj.remove("confirmDate");
			entity.setNonPKFields(jsonObj);
			entity.set("cost1", 
					cost1 == null ? null : new BigDecimal(cost1.replaceAll(",", "")));
			entity.set("cost2", 
					cost2 == null ? null : new BigDecimal(cost2.replaceAll(",", "")));
			entity.set("costTotal", 
					costTotal == null ? null : new BigDecimal(costTotal.replaceAll(",", "")));
			entity.set("quoteDate", 
					quoteDate == null ? null : ConvertUtil.convertStringToTimeStamp(quoteDate.replaceAll("/", "-")));
	    	entity.set("confirmDate", 
	    			confirmDate == null ? null : ConvertUtil.convertStringToTimeStamp(confirmDate.replaceAll("/", "-")));
			entity.store();
		}
		
	}
	
}
