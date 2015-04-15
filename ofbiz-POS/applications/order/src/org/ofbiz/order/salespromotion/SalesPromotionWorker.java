package org.ofbiz.order.salespromotion;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.order.parameter.Parameter;
import org.ofbiz.product.condUtil.condSqlString;
import org.ofbiz.service.DispatchContext;

import com.ibm.icu.util.Calendar;

public class SalesPromotionWorker {
	// 根据促销条件查询商品价格
	public static Map<String, Object> getProductPriceByPromoCond(
			DispatchContext dctx, Parameter parameter) throws Exception {
		Map<String, Object> PromoCond = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		List<GenericValue> list = null;
		boolean isProductPromo = true;
		String type = null;
		try {
			String productId = parameter.getProductId();
			Timestamp docDate = new Timestamp(new Date().getTime());
			String storeId = parameter.getStoreId();
			String partyId = parameter.getPartyId();
			String sequenceId = parameter.getSequenceId();
			Long quantity = parameter.getQuantity();
			// 根据商品id，串号id选出适合的促销活动
			if (UtilValidate.isNotEmpty(productId)) {
				list = delegator.findByAnd("SpecialProductView",
						UtilMisc.toMap("productId", productId));
				type = "T";
			}
			if (UtilValidate.isNotEmpty(sequenceId)) {
				list = delegator.findByAnd("SpecialserialView",
						UtilMisc.toMap("sequenceId", sequenceId));
				type = "C";
			}
			if (UtilValidate.isNotEmpty(list) && list.size() == 1) {
				String productPromoId = (String) list.get(0).get(
						"productPromoId");
				Timestamp startDate = (Timestamp) list.get(0).get("fromDate");
				Timestamp endDate = (Timestamp) list.get(0).get("thruDate");
				// 根据促销ID查询时间是否符合促销活动
				isProductPromo = judgeProductPromoTime(delegator,
						productPromoId, startDate, endDate, docDate);
				// 根据促销ID查询促销门店是否符合促销活动
				isProductPromo = getProductPromoStores(delegator,
						productPromoId, storeId);
				// 根据促销ID查询会员是否符合促销活动
				isProductPromo = getProductPromoMembers(delegator,
						productPromoId, partyId);
				if (isProductPromo) {
					// 根据促销ID,类型查询促销价格
					List<GenericValue> values = delegator.findByAnd(
							"ProductPromoConditions", UtilMisc.toMap(
									"productPromoId", productPromoId,
									"productPromoType", type));
					if (UtilValidate.isNotEmpty(values)) {
						// 根据商品数量查询商品是否参加促销活动
						if ("S".equals((String) values.get(0).get(
								"ConditionsType"))
								&& !"C".equals(type)) {
							if (quantity < Long.parseLong((String) values
									.get(0).get("startValue"))
									|| quantity > Long
											.parseLong((String) values.get(0)
													.get("endValue"))) {
								isProductPromo = false;
							}
						}
						if (isProductPromo) {
							GenericValue value = delegator.findByPrimaryKey(
									"ProductPromo", UtilMisc.toMap(
											"productPromoId", productPromoId));
							PromoCond.put("guidePrice",
									list.get(0).get("guidePrice"));
							PromoCond.put("salesPrice",
									list.get(0).get("salesPrice"));
							PromoCond.put("checkPrice",
									list.get(0).get("checkPrice"));
							PromoCond.put("Name", value.get("promoName"));
							PromoCond
									.put("Description", value.get("promoText"));

						}
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return PromoCond;
	}

	// 根据促销ID查询促销时间
	public static boolean judgeProductPromoTime(Delegator delegator,
			String productPromoId, Timestamp startDate, Timestamp endDate,
			Timestamp createDocDate) {
		List<GenericValue> list = null;
		boolean isPromoTime = false;
		int day = 0;
		int dayWeek = 0;
		String Type = "";
		String startValue = "";
		String endValue = "";
		try {
			Calendar start = Calendar.getInstance();
			start.setTime(startDate);
			Calendar end = Calendar.getInstance();
			end.setTime(endDate);
			Calendar date = Calendar.getInstance();
			date.setTime(createDocDate);
			Date dates = new Date();
			dates = createDocDate;
			// 截取productPromoTime时间中每月，每周，每天中的时间为具体时间
			list = delegator.findByAnd("ProductPromoTime",
					UtilMisc.toMap("productPromoId", productPromoId));
			if (UtilValidate.isNotEmpty(list)) {
				for (int i = 0; i < list.size(); i++) {
					Type = (String) list.get(i).get("productPromoTimeType");
					startValue = (String) list.get(i).get("startValue");
					endValue = (String) list.get(i).get("endValue");
					DateFormat sdf = new SimpleDateFormat("HH");
					Date startdate = new Date();
					Date enddate = new Date();
					startdate = sdf.parse(startValue);
					enddate = sdf.parse(endValue);
					if ("T".equals(Type)) {
						if (dates.getHours() - startdate.getHours() >= 0
								&& dates.getHours() - enddate.getHours() <= 0) {
							isPromoTime = true;
							continue;
						}
					} else if ("Z".equals(Type)) {
						dayWeek = date.get(Calendar.DAY_OF_WEEK);
						switch (dayWeek) {
						case 1:
							day = 7;
							break;
						case 2:
							day = 1;
							break;
						case 3:
							day = 2;
							break;
						case 4:
							day = 3;
							break;
						case 5:
							day = 4;
							break;
						case 6:
							day = 5;
							break;
						case 7:
							day = 6;
						}
						if (day >= Integer.parseInt(startValue)
								&& day <= Integer.parseInt(endValue)) {
							isPromoTime = true;
							break;
						}
					} else if ("Y".equals(Type)) {
						start.set(Calendar.DAY_OF_MONTH,
								Integer.parseInt(startValue));
						end.set(Calendar.DAY_OF_MONTH,
								Integer.parseInt(endValue));
						if (date.compareTo(start) >= 0
								&& date.compareTo(end) <= 0) {
							isPromoTime = true;
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isPromoTime;
	}

	// 根据促销ID查询会员ID范围
	public static boolean getProductPromoMembers(Delegator delegator,
			String productPromoId, String partyId)
			throws GenericEntityException {
		EntityCondition mainCond = null;
		List<GenericValue> list = null;
		boolean isPromoTime = false;
		List<EntityCondition> andExprs = FastList.newInstance();
		//String Condition= "AND "+"PE.PARTY_ID"+"="+"'"+partyId+"'";
		try {
			String sqlString = condSqlString.getSqlString(delegator,
					"ProductPromoParty", "productPromoId", productPromoId);
			if (UtilValidate.isNotEmpty(sqlString)) {
				mainCond = EntityCondition.makeConditionWhere(sqlString);
				list = delegator.findList("ProductPromoPartyView", mainCond,
						null, null, null, false);
				if (list.size() == 0) {
					isPromoTime = true;
				} else {
					String sqlStr = condSqlString.getSqlmember(delegator,
							"ProductPromoParty", "productPromoId",
							productPromoId, partyId);
				
				//andExprs.add(EntityCondition.makeConditionWhere(Condition));
				//andExprs.add(EntityCondition.makeConditionWhere(sqlString));	
				//mainCond = EntityCondition.makeCondition(andExprs,EntityOperator.AND);	
					if (UtilValidate.isNotEmpty(sqlStr)) {
						mainCond = EntityCondition.makeConditionWhere(sqlStr);
						list = delegator.findList("ProductPromoPartyView",
								mainCond, null, null, null, false);
						if (list.size() > 0) {
							isPromoTime = true;
						}
					}
				}
			}else{isPromoTime = true;}
		} catch (GenericEntityException e) {
			throw e;
		}
		return isPromoTime;
	}

	// 根据促销ID查询促销门店范围
	public static boolean getProductPromoStores(Delegator delegator,
			String productPromoId, String storeId)
			throws GenericEntityException {
		EntityCondition mainCond = null;
		List<GenericValue> list = null;
		boolean isPromoTime = false;
		try {
			String sqlString = condSqlString.getSqlString(delegator,
					"ProductPromoStore", "productPromoId", productPromoId);
			if (UtilValidate.isNotEmpty(sqlString)) {
			mainCond = EntityCondition.makeConditionWhere(sqlString);
			list = delegator.findList("ProductPromoStoreView", mainCond, null,
					null, null, false);
			if (list.size() == 0) { 
				isPromoTime = true;
			} else {
				String sql = condSqlString.getSql(delegator,
						"ProductPromoStore", "productPromoId", productPromoId,
						storeId);
				mainCond = EntityCondition.makeConditionWhere(sql);// 查询次门店是否包含，
				list = delegator.findList("ProductPromoStoreView", mainCond,
						null, null, null, false);
				if (list.size() >0) {
					isPromoTime = true;
				}
			}
			}else{
				isPromoTime = true;
			}
		} catch (GenericEntityException e) {
			throw e;
		}
		return isPromoTime;
	}
}
