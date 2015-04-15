package org.ofbiz.competing;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class CompetingServices {
	/**
	 * createCompeting
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> createCompeting(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		String productStoreId = (String) context.get("productStoreId");

		String docIds = productStoreId + delegator.getNextSeqId("Competing");
		GenericValue competing;

		try {
			competing = delegator.findByPrimaryKey("Competing",
					UtilMisc.toMap("docId", docIds));
			if (UtilValidate.isNotEmpty(competing)) {
				competing.setNonPKFields(context);
				competing.store();
			} else {
				competing = delegator.makeValue("Competing",
						UtilMisc.toMap("docId", docIds));
				competing.setNonPKFields(context);
				competing.set("docId", docIds);
				competing.create();
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<String, Object> results = ServiceUtil.returnSuccess();
		results.put("docId", docIds);
		return results;
	}

	/**
	 * findNoPrameList condition from table productPromoStore
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> findCompetingList(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		String docId = (String) context.get("docId");
		String lineNo = (String) context.get("lineNo");
		String entityName = (String) context.get("entityName");
		String orderBy = (String) context.get("orderBy");
		Map<String, Object> result = FastMap.newInstance();
		List<String> orderByList = FastList.newInstance();
		orderByList.add(orderBy);
		List<EntityCondition> andExprs = FastList.newInstance();
		// using list iterator
		EntityListIterator listIt = null;
		int listSize = 0;

		Integer viewSize = (Integer) context.get("viewSize");
		Integer viewIndex = (Integer) context.get("viewIndex");
		Integer maxRows = null;
		if (viewSize != null && viewIndex != null) {
			maxRows = viewSize * (viewIndex + 1);
		}
		maxRows = maxRows != null ? maxRows : -1;
		try {

			EntityCondition cond = EntityCondition.makeCondition("docId",
					EntityOperator.EQUALS, docId);

			listIt = delegator.find(entityName, cond, null, null, null,
					new EntityFindOptions(true,
							EntityFindOptions.TYPE_SCROLL_INSENSITIVE,
							EntityFindOptions.CONCUR_READ_ONLY, -1, maxRows,
							false));
			listSize = listIt.getResultsSizeAfterPartialList();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result.put("listIt", listIt);
		result.put("listSize", listSize);
		return result;
	}

	/**
	 * 更mingxi
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 * @throws GenericEntityException
	 */
	public static Map<String, Object> updateCompetingsinfo(
			DispatchContext dctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		String docId = (String) context.get("docId");
		String lineNo = (String) context.get("lineNo");
		String monitoringObject = (String) context.get("monitoringObject");
		String brand = (String) context.get("brand");
		String model = (String) context.get("model");
		BigDecimal price = (BigDecimal) context.get("price");
		BigDecimal suggestedPrice = (BigDecimal) context.get("suggestedPrice");
		BigDecimal publicityPrice = (BigDecimal) context.get("publicityPrice");
		BigDecimal transactionPrice = (BigDecimal) context
				.get("transactionPrice");
		BigDecimal salesPolicy = (BigDecimal) context.get("salesPolicy");
		String sourcesInfo = (String) context.get("sourcesInfo");
		String memo = (String) context.get("memo");
		String reason = (String) context.get("reason");
		String opinion = (String) context.get("opinion");
		/*
		 * Map<String, String> productCondMap = UtilMisc.toMap("docId", docId,
		 * "lineNo", lineNo);
		 */try {
			GenericValue compet = delegator.findByPrimaryKey("CompetingDtl",
					UtilMisc.toMap("docId", docId, "lineNo", lineNo));
			if (UtilValidate.isNotEmpty(compet)) {
				if (UtilValidate.isNotEmpty(monitoringObject)) {
					compet.set("monitoringObject", monitoringObject);
				}
				if (UtilValidate.isNotEmpty(brand)) {
					compet.set("brand", brand);
				}
				if (UtilValidate.isNotEmpty(model)) {
					compet.set("model", model);
				}
				if (UtilValidate.isNotEmpty(price)) {
					compet.set("price", price);
				}
				if (UtilValidate.isNotEmpty(suggestedPrice)) {
					compet.set("suggestedPrice", suggestedPrice);
				}
				if (UtilValidate.isNotEmpty(publicityPrice)) {
					compet.set("publicityPrice", publicityPrice);
				}
				if (UtilValidate.isNotEmpty(transactionPrice)) {
					compet.set("transactionPrice", transactionPrice);
				}
				if (UtilValidate.isNotEmpty(salesPolicy)) {
					compet.set("salesPolicy", salesPolicy);
				}
				if (UtilValidate.isNotEmpty(sourcesInfo)) {
					compet.set("sourcesInfo", sourcesInfo);
				}
				if (UtilValidate.isNotEmpty(memo)) {
					compet.set("memo", memo);
				}
				if (UtilValidate.isNotEmpty(reason)) {
					compet.set("reason", reason);
				}
				if (UtilValidate.isNotEmpty(opinion)) {
					compet.set("opinion", opinion);
				}
				compet.store();
			}

		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			throw e;
		}

		return ServiceUtil.returnSuccess();
	}

	public static Map<String, Object> deleteCompeting(DispatchContext dctx,
			Map<String, ? extends Object> context)
			throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		String docId = (String) context.get("docId");
		String lineNo = (String) context.get("lineNo");
		try {

			if (UtilValidate.isNotEmpty(docId)
					&& UtilValidate.isNotEmpty(lineNo)) {

				GenericValue demo = delegator.findByPrimaryKey("CompetingDtl",
						UtilMisc.toMap("docId", docId, "lineNo", lineNo));

				if (UtilValidate.isNotEmpty(demo)) {

					demo.remove();
				}
			}

		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			throw e;
		}

		return ServiceUtil.returnSuccess();
	}

	public static Map<String, Object> deleteCompetingheader(
			DispatchContext dctx, Map<String, ? extends Object> context)
			throws GenericEntityException {

		Delegator delegator = dctx.getDelegator();
		String docId = (String) context.get("docId");

		try {

			if (UtilValidate.isNotEmpty(docId)) {
				GenericValue demo = delegator.findByPrimaryKey("Competing",
						UtilMisc.toMap("docId", docId));
				if (UtilValidate.isNotEmpty(demo)) {
					demo.remove();
				}
				EntityCondition cond = EntityCondition.makeCondition("docId",
						EntityOperator.EQUALS, docId);
				List<GenericValue> list = delegator.findList("CompetingDtl",
						cond, null, null, null, false);

				delegator.removeAll(list);
				/*
				 * for(int i=0;i<list.size();i++){ list.remove(i); }
				 */
			}

		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			throw e;
		}

		return ServiceUtil.returnSuccess();
	}

	/**
	 * findNoPrameList condition from table productPromoStore
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> competFind(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		String productStoreId = (String) context.get("productStoreId");
		Date docDate = (Date) context.get("docDate");
		String entityName = (String) context.get("entityName");
		String orderBy = (String) context.get("orderBy");
		Map<String, Object> result = FastMap.newInstance();
		List<String> orderByList = FastList.newInstance();
		orderByList.add(orderBy);
		List<EntityCondition> andExprs = FastList.newInstance();
		// using list iterator
		EntityListIterator listIt = null;
		int listSize = 0;
		Integer viewSize = (Integer) context.get("viewSize");
		Integer viewIndex = (Integer) context.get("viewIndex");
		Integer maxRows = null;
		if (viewSize != null && viewIndex != null) {
			maxRows = viewSize * (viewIndex + 1);
		}
		maxRows = maxRows != null ? maxRows : -1;
		try {

			EntityCondition cond = EntityCondition.makeCondition(
					"productStoreId", EntityOperator.EQUALS, productStoreId);

			listIt = delegator.find(entityName, cond, null, null, null,
					new EntityFindOptions(true,
							EntityFindOptions.TYPE_SCROLL_INSENSITIVE,
							EntityFindOptions.CONCUR_READ_ONLY, -1, maxRows,
							false));
			listSize = listIt.getResultsSizeAfterPartialList();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result.put("listIt", listIt);
		result.put("listSize", listSize);
		return result;
	}

}
